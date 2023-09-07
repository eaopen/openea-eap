package org.openea.eap.extj.job;

import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorRecordEntity;
import org.openea.eap.extj.engine.enums.FlowTaskStatusEnum;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.LimitModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.TimeModel;
import org.openea.eap.extj.engine.model.flowmessage.FlowMsgModel;
import org.openea.eap.extj.engine.model.flowtask.FlowApproveModel;
import org.openea.eap.extj.engine.model.flowtask.WorkTimeoutJobModel;
import org.openea.eap.extj.engine.model.flowtime.FlowTimeModel;
import org.openea.eap.extj.engine.service.FlowTaskNewService;
import org.openea.eap.extj.engine.service.FlowTaskNodeService;
import org.openea.eap.extj.engine.service.FlowTaskOperatorService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.engine.util.FlowContextHolder;
import org.openea.eap.extj.engine.util.FlowMsgUtil;
import org.openea.eap.extj.engine.util.FlowTaskUtil;
import org.openea.eap.extj.engine.util.FlowTimerUtil;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RedisUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 流程设计
 *
 *
 */
@Component
@Slf4j
@DependsOn("threadPoolTaskExecutor")
public class WorkTimeoutJobUtil {
    /**
     * 缓存key
     */
    public static final String WORKTIMEOUT_REDIS_KEY = "idgenerator_WorkTimeout";
    public static Map<String, List<ScheduledFuture>> futureList = new HashMap<>();

    //测试
    boolean testFlag = false;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    public FlowMsgUtil flowMsgUtil;
    @Autowired
    public FlowTaskNodeService flowTaskNodeService;
    @Autowired
    public FlowTaskNewService flowTaskNewService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowTimerUtil flowTimerUtil;
    @Autowired
    private FlowTaskUtil flowTaskUtil;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    WorkTimeoutJobUtil(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2, threadPoolTaskExecutor.getThreadPoolExecutor().getThreadFactory());
    }

    /**
     * 将数据放入缓存
     *
     * @param
     * @return
     */
    public void insertRedis(WorkTimeoutJobModel workTimeoutJobModel, RedisUtil redisUtil) {
        insertRedis(workTimeoutJobModel, redisUtil, true);
    }

    /**
     * 将数据放入缓存
     *
     * @param
     * @return
     */
    public void insertRedis(WorkTimeoutJobModel workTimeoutJobModel, RedisUtil redisUtil, boolean isRunTimeOut) {
        workTimeoutJobModel.setCounter(0);
        workTimeoutJobModel.setOvertimeNum(0);
        String objectToString = JsonUtil.getObjectToString(workTimeoutJobModel);
        redisUtil.insertHash(WORKTIMEOUT_REDIS_KEY, workTimeoutJobModel.getTaskNodeOperatorId(), objectToString);
        if (isRunTimeOut) {
            this.runTimeOutMethod(workTimeoutJobModel, false);
        }
    }

    /**
     * 定时器取用数据调用创建方法
     *
     * @param
     * @return
     */
    public List<WorkTimeoutJobModel> getListRedis(RedisUtil redisUtil) {
        List<WorkTimeoutJobModel> list = new ArrayList<>();
        if (redisUtil.exists(WORKTIMEOUT_REDIS_KEY)) {
            Map<String, Object> map = redisUtil.getMap(WORKTIMEOUT_REDIS_KEY);
            for (Object object : map.keySet()) {
                if (map.get(object) instanceof String) {
                    WorkTimeoutJobModel workTimeoutJobModel = JsonUtil.getJsonToBean(String.valueOf(map.get(object)), WorkTimeoutJobModel.class);
                    list.add(workTimeoutJobModel);
                }
            }
        }
        return list;
    }

    /**
     * 运行限时设置的内容。
     *
     * @param
     * @return
     */
    public void runTimeOutMethod(WorkTimeoutJobModel entity, boolean isTimeTask) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH");
        if (testFlag) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        //切换数据库
        if (configValueUtil.isMultiTenancy()) {
            if (StringUtil.isNotEmpty(entity.getTenantDbConnectionString())) {
                DataSourceContextHolder.setDatasource(entity.getTenantId(), entity.getTenantDbConnectionString(), entity.isAssignDataSource());
            }
        }
        try {
            FlowTaskOperatorEntity operatorInfo = flowTaskOperatorService.getOperatorInfo(entity.getTaskNodeOperatorId());
            if (operatorInfo == null || operatorInfo.getCompletion() != 0 || operatorInfo.getState() != 0) {
                throw new WorkFlowException("任务不存在,或者已处理");
            }
            Date realThisTime = DateUtil.stringToDate(DateUtil.getNow());
            Date thisTime = formatter.parse(DateUtil.getmmNow());
            FlowTaskEntity flowTask = flowTaskService.getInfo(entity.getTaskId());
            FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
            List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(flowTask.getId());
            FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeService.getInfo(entity.getTaskNodeId());

            //节点属性
            FlowTimeModel flowTimeModel = flowTimerUtil.time(flowTaskNodeEntity, taskNodeList, flowTask, operatorInfo);
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties approversThis = flowTimeModel.getChildNodeEvnet().getProperties();
            Properties approversStart = flowTimeModel.getChildNode().getProperties();
            LimitModel timeLimitConfig = approversThis.getTimeLimitConfig();
            if (timeLimitConfig.getOn() == 2) {
                timeLimitConfig = approversStart.getTimeLimitConfig();
            }
            TimeModel overTimeConfig = approversThis.getOverTimeConfig();
            if (overTimeConfig.getOn() == 2) {
                overTimeConfig = approversStart.getOverTimeConfig();
            }
            TimeModel noticeConfig = approversThis.getNoticeConfig();
            if (noticeConfig.getOn() == 2) {
                noticeConfig = approversStart.getNoticeConfig();
            }
            FlowModel flowModel = entity.getFlowModel();
            //限时设置
            if (timeLimitConfig.getOn() == 1) {
                Date limitStartTime = formatter.parse(formatter.format(flowTimeModel.getDate()));
                Date realStartTime = flowTimeModel.getDate();
                Long timeout = realStartTime.getTime() - limitStartTime.getTime();
                Integer limitedDuration = timeLimitConfig.getDuringDeal();
                Date limitEndTime = DateUtil.dateAddHours(limitStartTime, limitedDuration);//加限时时间
                if (testFlag) {
                    //以1分钟为准
                    limitEndTime = DateUtil.dateAddMinutes(limitStartTime, limitedDuration);//加限时时间
                }
                if (isTimeTask) {//砍掉尾数的定时器触发当前时间加上被砍掉的时间尾数，为本应该是触发定时器的时间
                    realThisTime = DateUtil.dateAddSeconds(thisTime, timeout.intValue() / 1000);
                }
                //1.提醒设置 在限定时间范围内有效
                if (noticeConfig.getOn() == 1 && realThisTime.getTime() >= limitStartTime.getTime() + timeout && realThisTime.getTime() <= limitEndTime.getTime() + timeout) {
                    noticeMethod(entity, testFlag, thisTime, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, flowTimeModel, noticeConfig, flowModel, limitStartTime, timeout, limitEndTime, isTimeTask, realThisTime);
                }
                //2.超时,在限时时长之后生效
                if (overTimeConfig.getOn() == 1 && realThisTime.getTime() >= limitEndTime.getTime() + timeout) {
                    timeoutMethod(entity, formatter, testFlag, thisTime, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, operatorInfo, flowTimeModel, overTimeConfig, flowModel, timeout, limitEndTime, isTimeTask, realThisTime);
                }
                //判断是提交过来的，是否在1个小时范围内
                if (!isTimeTask) {
                    if (overTimeConfig.getOn() == 1 && realThisTime.getTime() >= limitStartTime.getTime() + timeout && realThisTime.getTime() <= limitEndTime.getTime() + timeout) {
                        timeout = limitEndTime.getTime() + timeout - System.currentTimeMillis();
                        if (timeout < 3600000 && !testFlag) {
                            isTimeTask = true;
                            timeoutMethod(entity, formatter, testFlag, thisTime, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, operatorInfo, flowTimeModel, overTimeConfig, flowModel, timeout, limitEndTime, isTimeTask, realThisTime);
                        }
                    }
                }
            }
        } catch (WorkFlowException we) {
            we.printStackTrace();
            redisUtil.removeHash(this.WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeOperatorId());
            redisUtil.removeHash(this.WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 超时相关逻辑
     *
     * @param
     * @return
     */
    private void timeoutMethod(WorkTimeoutJobModel entity, SimpleDateFormat formatter, boolean testFlag, Date thisTime, FlowTaskEntity flowTask, FlowTemplateAllModel templateAllModel,
                               List<FlowTaskNodeEntity> taskNodeList, FlowTaskNodeEntity flowTaskNodeEntity, FlowTaskOperatorEntity operatorInfo,
                               FlowTimeModel flowTimeModel, TimeModel overTimeConfig, FlowModel flowModel, long timeout, Date limitEndTime, boolean isTimeTask, Date realThisTime) throws ParseException {
        Integer firstTimeout = overTimeConfig.getFirstOver();
        Integer timeoutInterval = overTimeConfig.getOverTimeDuring();
        Date timeoutStartTime = DateUtil.dateAddHours(limitEndTime, firstTimeout);
        Long delayTime = 500l;
        if (isTimeTask) {
            delayTime = timeout;
        }
        if (testFlag) {
            //以1分钟为单位进行测试超时测试，假设起始时间starTime=xxxx以startTimeTest替换给定
            timeoutStartTime = DateUtil.dateAddMinutes(limitEndTime, firstTimeout);
        }
        //当节点创建时间大于超时开始时间时，以节点创建时间为起始
//        if (operatorInfo.getCreatorTime().getTime() >= timeoutStartTime.getTime()) {
//            String format = formatter.format(operatorInfo.getCreatorTime());
//            timeoutStartTime = formatter.parse(format);
//        }
        if (overTimeConfig.getOverTimeDuring() == 0) {//超时间隔=0只提醒一次
            if (thisTime.compareTo(timeoutStartTime) == 0) {
                TimeModel finalOverTimeConfig = overTimeConfig;
                Runnable resetExpire = () -> {
                    FlowTaskOperatorEntity operatorNow = null;
                    if (isTimeTask) {
                        operatorNow = onTimeCheckOperatorHave(entity, 1);
                    } else {
                        operatorNow = entity.getOperatorEntity();
                    }
                    if (operatorNow == null) return;
                    //超时消息
                    if (finalOverTimeConfig.getOverNotice()) {
                        List<FlowTaskOperatorEntity> list = new ArrayList();
                        list.add(operatorNow);
                        sendMessge(false, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                    }
                    //超时事件
                    if (finalOverTimeConfig.getOverEvent() && finalOverTimeConfig.getOverEventTime() >= 1) {
                        sendEvent(entity, 7, flowTimeModel);
                    }
                    //超时自动审批
                    if (finalOverTimeConfig.getOverAutoApprove() && finalOverTimeConfig.getOverAutoApproveTime() >= 1) {
//                        _log("超时任务: {}-自动审批", entity.getTaskNodeOperatorId());
                        //自动审批
                        autoApprove(operatorNow, taskNodeList, flowTask, flowModel);
                    }
                    redisUtil.removeHash(this.WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeOperatorId());//超时提醒只有一次的时候完成就移除
                };
                this.runScheduleTask(resetExpire, delayTime, entity.getTaskNodeOperatorId());
            }
        } else if (overTimeConfig.getOverTimeDuring() > 0) {//超时间隔>0
            //创建即触发超时
            if (!isTimeTask && entity.getOvertimeNum() == 0 && realThisTime.getTime() > timeoutStartTime.getTime() + timeout) {
                Long bett = realThisTime.getTime() - timeoutStartTime.getTime() - timeout;
                int finaConut = 0;
                if (testFlag) {
                    finaConut = bett.intValue() / (60000 * timeoutInterval);
                } else {
                    finaConut = bett.intValue() / (3600000 * timeoutInterval);
                }
//                _log("创建即超时次数：{}", finaConut);
                if (overTimeConfig.getOverNotice()) {
//                    _log("超时任务: {}-创建即触发超时通知", entity.getTaskNodeOperatorId());
                    List<FlowTaskOperatorEntity> list = new ArrayList<>();
                    list.add(entity.getOperatorEntity());
                    sendMessge(false, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                }
                if (overTimeConfig.getOverEvent() && overTimeConfig.getOverEventTime() <= (finaConut + 1) && entity.getOvertimeNum() == 0) {
                    //接收到就已经超时多次，直接触发一次事件
//                    _log("超时任务: {}-创建即触发进入超时事件", entity.getTaskNodeOperatorId());
                    sendEvent(entity, 7, flowTimeModel);
                }
                if (overTimeConfig.getOverAutoApprove() && overTimeConfig.getOverAutoApproveTime() <= (finaConut + 1) && entity.getOvertimeNum() == 0) {
//                    _log("超时任务: {}-创建即触发超时自动审批", entity.getTaskNodeOperatorId());
                    //自动审批
                    autoApprove(entity.getOperatorEntity(), taskNodeList, flowTask, flowModel);
                }
                addNum(entity.getTaskNodeOperatorId(), 1);//有执行则次数加1
            }
            int n = 0;
            while (isTimeTask) {
                Date whileTime = DateUtil.dateAddHours(timeoutStartTime, timeoutInterval * n);
                if (testFlag) {
                    //以1分钟为单位进行测试超时测试，假设起始时间starTime=xxxx以startTimeTest替换给定
                    whileTime = DateUtil.dateAddMinutes(timeoutStartTime, timeoutInterval * n);
                }
//                _log("超时任务: {}-第:{}次超时时间循环:{}", entity.getTaskNodeOperatorId(),  (n + 1), DateUtil.dateFormat(whileTime));
                if (thisTime.compareTo(whileTime) == 0) {
                    int finalN = n;
                    TimeModel finalOverTimeConfig1 = overTimeConfig;
                    Runnable resetExpire = () -> {
                        FlowTaskOperatorEntity operatorNow = null;
                        if (isTimeTask) {
                            operatorNow = onTimeCheckOperatorHave(entity, 1);
                        } else {
                            operatorNow = entity.getOperatorEntity();
                        }
                        if (operatorNow == null) return;
                        //todo 超时通知
                        if (finalOverTimeConfig1.getOverNotice()) {
//                            _log("超时任务: {}-第{}次的超时通知", entity.getTaskNodeOperatorId(), (finalN + 1));
                            List<FlowTaskOperatorEntity> list = new ArrayList<>();
                            list.add(operatorNow);
                            sendMessge(false, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                        }
                        //todo 超时事件
                        if (finalOverTimeConfig1.getOverEvent() && finalOverTimeConfig1.getOverEventTime() == (finalN + 1)) {
//                            _log("超时任务: {}-第{}次进入超时事件", entity.getTaskNodeOperatorId(), (finalN + 1));
                            //节点事件
                            sendEvent(entity, 7, flowTimeModel);
                        }
                        //todo 超时自动审批
                        if (finalOverTimeConfig1.getOverAutoApprove() && finalOverTimeConfig1.getOverAutoApproveTime() == (finalN + 1)) {
//                            _log("超时任务: {}-第{}次进入超时自动审批", entity.getTaskNodeOperatorId(), (finalN + 1));
                            //自动审批
                            autoApprove(operatorNow, taskNodeList, flowTask, flowModel);
                        }
                        addNum(entity.getTaskNodeOperatorId(), 1);
                    };
                    this.runScheduleTask(resetExpire, delayTime, entity.getTaskNodeOperatorId());
                }
                if (whileTime.after(thisTime)) {
                    break;
                }
                n++;
            }
        }
    }

    /**
     * 提醒方法执行内容
     *
     * @param
     * @return
     */
    public void noticeMethod(WorkTimeoutJobModel entity, boolean testFlag, Date thisTime, FlowTaskEntity flowTask, FlowTemplateAllModel templateAllModel,
                             List<FlowTaskNodeEntity> taskNodeList, FlowTaskNodeEntity flowTaskNodeEntity, FlowTimeModel flowTimeModel, TimeModel noticeConfig,
                             FlowModel flowModel, Date limitStartTime, long timeout, Date limitEndTime, boolean isTimeTask, Date realThisTime) {
        Integer firstWarnTime = noticeConfig.getFirstOver();
        Integer warnInterval = noticeConfig.getOverTimeDuring();
        Date warnStartTime = DateUtil.dateAddHours(limitStartTime, firstWarnTime);
        Long delayTime = 0l;
        if (isTimeTask) {
            delayTime = timeout;
        }
        if (testFlag) {
            warnStartTime = DateUtil.dateAddMinutes(limitStartTime, firstWarnTime);
        }
        if (noticeConfig.getOverTimeDuring() == 0) {//提醒间隔为0-只提醒一次
            if (thisTime.compareTo(warnStartTime) == 0) {
                TimeModel finalNoticeConfig = noticeConfig;
                Runnable resetExpire = () -> {
                    //待处理任务列表
                    FlowTaskOperatorEntity operatorNow = null;
                    if (isTimeTask) {
                        operatorNow = onTimeCheckOperatorHave(entity, 0);
                    } else {
                        operatorNow = entity.getOperatorEntity();
                    }
                    if (operatorNow == null) return;
                    if (finalNoticeConfig.getOverNotice()) {
                        List<FlowTaskOperatorEntity> list = new ArrayList();
                        list.add(operatorNow);
                        sendMessge(true, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                    }
                    if (finalNoticeConfig.getOverEvent() && finalNoticeConfig.getOverEventTime() >= 1) {
                        sendEvent(entity, 8, flowTimeModel);
                    }
                    addNum(entity.getTaskNodeOperatorId(), 0);
                };
                this.runScheduleTask(resetExpire, delayTime, entity.getTaskNodeOperatorId());
            }
        } else if (noticeConfig.getOverTimeDuring() > 0) {//提醒间隔大于0
            long noticeTimes = (limitEndTime.getTime() - warnStartTime.getTime()) / (warnInterval * 60 * 60 * 1000);
            if (testFlag) {
                noticeTimes = (limitEndTime.getTime() - warnStartTime.getTime()) / (warnInterval * 60 * 1000);
            }
            //限定时间在创建时间之前，有过几次时的提醒的情况（前面不管多少次只提醒一次）
            if (!isTimeTask && entity.getCounter() == 0 && noticeConfig.getOverEventTime() > 0 && realThisTime.getTime() > warnStartTime.getTime() + timeout) {
                if (noticeConfig.getOverNotice()) {
//                    _log("创建即出发提醒信息");
                    List<FlowTaskOperatorEntity> list = new ArrayList();
                    list.add(entity.getOperatorEntity());
                    sendMessge(true, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                }
                if (noticeConfig.getOverEvent()) {
//                    _log("创建即出发提醒事件");
                    sendEvent(entity, 8, flowTimeModel);
                }
                addNum(entity.getTaskNodeOperatorId(), 0);//有执行则次数加1
            }
            for (int n = 0; n <= noticeTimes; n++) {
                Date whileTime = DateUtil.dateAddHours(warnStartTime, warnInterval * n);
                if (testFlag) {
                    //假设以1分钟代替一个小时，
                    whileTime = DateUtil.dateAddMinutes(warnStartTime, warnInterval * n);
                }
//                _log("提醒任务: {}-第{}次提醒时间循环: {}", entity.getTaskNodeOperatorId(), (n + 1), DateUtil.dateFormat(whileTime));
                if (isTimeTask && thisTime.compareTo(whileTime) == 0) {//定时器提醒时间
                    int finalN = n;
                    TimeModel finalNoticeConfig1 = noticeConfig;
                    Runnable resetExpire = () -> {
                        FlowTaskOperatorEntity operatorNow = null;
                        if (isTimeTask) {
                            operatorNow = onTimeCheckOperatorHave(entity, 0);
                        } else {
                            operatorNow = entity.getOperatorEntity();
                        }
                        if (operatorNow == null) return;
                        //todo 提醒事件
                        if (finalNoticeConfig1.getOverEvent() && finalNoticeConfig1.getOverEventTime() == (finalN + 1)) {
//                            _log("提醒任务: {}-第{}次进入提醒事件", entity.getTaskNodeOperatorId(), (finalN + 1));
                            sendEvent(entity, 8, flowTimeModel);
                        }
                        //todo 提醒通知
                        if (finalNoticeConfig1.getOverNotice()) {
//                            _log("提醒任务: {}-第{}次的提醒通知", entity.getTaskNodeOperatorId(), (finalN + 1));
                            List<FlowTaskOperatorEntity> list = new ArrayList();
                            list.add(operatorNow);
                            sendMessge(true, flowTask, templateAllModel, taskNodeList, flowTaskNodeEntity, list, flowModel);
                        }
                        addNum(entity.getTaskNodeOperatorId(), 0);
                    };
                    this.runScheduleTask(resetExpire, delayTime, entity.getTaskNodeOperatorId());
                }
            }
        }
    }

    /**
     * 自动审批
     *
     * @param
     * @return
     */
    private void autoApprove(FlowTaskOperatorEntity operatorNow, List<FlowTaskNodeEntity> taskNodeList, FlowTaskEntity flowTask, FlowModel flowModel) {
        operatorNow.setAutomation("1");
        List<FlowTaskOperatorEntity> list = new ArrayList();
        list.add(operatorNow);
        try {
            FlowTaskEntity infoSubmit = flowTaskService.getInfoSubmit(flowTask.getId(), FlowTaskEntity::getStatus);
            if (infoSubmit != null && !FlowTaskStatusEnum.Suspend.getCode().equals(infoSubmit.getStatus())) {
                FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(list).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
                flowTaskUtil.approve(approveModel);
            }
        } catch (WorkFlowException e) {
            e.printStackTrace();
        } finally {
            FlowContextHolder.clearAll();
        }
    }

    private FlowTaskOperatorEntity onTimeCheckOperatorHave(WorkTimeoutJobModel entity, Integer type) {
        FlowTaskOperatorEntity operatorNow = flowTaskOperatorService.getOperatorInfo(entity.getTaskNodeOperatorId());
        if (operatorNow == null || operatorNow.getCompletion() != 0 || operatorNow.getState() != 0) {
            removeTaskOperatorEntities(entity);
            return null;
        }
        String hashValues = redisUtil.getHashValues(this.WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeOperatorId());
        WorkTimeoutJobModel workTimeoutJobModel = JsonUtil.getJsonToBean(hashValues, WorkTimeoutJobModel.class);
        if (workTimeoutJobModel == null) {
            return null;
        }
        if (type == 1 && !entity.getOvertimeNum().equals(workTimeoutJobModel.getOvertimeNum())) {//判断redis被取用之后是否被改变,被改变这次不执行
            return null;
        }
        if (type == 0 && !entity.getCounter().equals(workTimeoutJobModel.getCounter())) {//判断redis被取用之后是否被改变,被改变这次不执行
            return null;
        }
        return operatorNow;
    }


    private void removeTaskOperatorEntities(WorkTimeoutJobModel entity) {
        //已处理任务移除缓存
        redisUtil.removeHash(this.WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeOperatorId());
    }

    /**
     * 执行缓存次数加一
     *
     * @param
     * @return
     */
    private void addNum(String operatorId, Integer type) {
        //执行没有问题，缓存替换次数加一 type:0-提醒，1-超时
        String hashValues = redisUtil.getHashValues(WORKTIMEOUT_REDIS_KEY, operatorId);
        WorkTimeoutJobModel entity = JsonUtil.getJsonToBean(hashValues, WorkTimeoutJobModel.class);
        if (type == 0) {
            Integer counter = entity.getCounter() == null ? 1 : entity.getCounter() + 1;
            entity.setCounter(counter);
        } else {
            Integer overtimenum = entity.getOvertimeNum() == null ? 1 : entity.getOvertimeNum() + 1;
            entity.setOvertimeNum(overtimenum);
        }
        String objectToString = JsonUtil.getObjectToString(entity);
        redisUtil.insertHash(WORKTIMEOUT_REDIS_KEY, entity.getTaskNodeOperatorId(), objectToString);
    }

    /**
     * 发送事件
     *
     * @param
     * @return
     */
    private void sendEvent(WorkTimeoutJobModel entity, int status, FlowTimeModel flowTimeModel) {
//        _log("进入事件调用");
        //节点事件
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        operatorRecord.setTaskNodeId(entity.getTaskNodeId());
        operatorRecord.setTaskId(entity.getTaskId());
        FlowTaskEntity infoSubmit = flowTaskService.getInfoSubmit(entity.getTaskId(), FlowTaskEntity::getStatus);
        if (infoSubmit != null && !FlowTaskStatusEnum.Suspend.getCode().equals(infoSubmit.getStatus())) {
            flowMsgUtil.event(status, flowTimeModel.getChildNodeEvnet(), operatorRecord, entity.getFlowModel());
        }
    }

    /**
     * 发送消息
     *
     * @param
     * @return
     */
    @Async
    public void sendMessge(boolean isNotice, FlowTaskEntity flowTask, FlowTemplateAllModel templateAllModel, List<FlowTaskNodeEntity> taskNodeList, FlowTaskNodeEntity flowTaskNodeEntity, List<FlowTaskOperatorEntity> list, FlowModel flowModel) {
        //发送流程消息
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setWait(false);
        if (isNotice) {
            flowMsgModel.setNotice(true);
        } else {
            flowMsgModel.setOvertime(true);
        }
        flowMsgModel.setCirculateList(new ArrayList<>());
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setOperatorList(list);
        flowMsgModel.setTaskEntity(flowTask);
        flowMsgModel.setTaskNodeEntity(flowTaskNodeEntity);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setFlowModel(flowModel);
        FlowTaskEntity infoSubmit = flowTaskService.getInfoSubmit(flowTask.getId(), FlowTaskEntity::getStatus);
        if (infoSubmit != null && !FlowTaskStatusEnum.Suspend.getCode().equals(infoSubmit.getStatus())) {
            flowMsgUtil.message(flowMsgModel);
        }
    }

    private void runScheduleTask(Runnable runnable, long delayTime, String operatorId) {
        ScheduledFuture schedule = scheduledThreadPoolExecutor.schedule(runnable, delayTime / 1000, TimeUnit.SECONDS);
        addFuture(operatorId, schedule);
    }

    private void _log(String msg, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(msg, args);
        }
    }

    private void addFuture(String operatorId, ScheduledFuture future) {
        List<ScheduledFuture> scheduledFutures = futureList.get(operatorId) != null ? futureList.get(operatorId) : new ArrayList<>();
        scheduledFutures.add(future);
        futureList.put(operatorId, scheduledFutures);
    }

    /**
     * @param operatorList
     * @param isSuspend    true 挂起  false 恢复
     */
    public void suspendFuture(List<FlowTaskOperatorEntity> operatorList, boolean isSuspend) {
        for (FlowTaskOperatorEntity entity : operatorList) {
//            List<ScheduledFuture> scheduledFutures = futureList.get(entity.getId()) != null ? futureList.get(entity.getId()) : new ArrayList<>();
//            for (ScheduledFuture scheduledFuture : scheduledFutures) {
//                scheduledFuture.cancel(true);
//            }
//            futureList.remove(entity.getId());
            String hashValues = redisUtil.getHashValues(WORKTIMEOUT_REDIS_KEY, entity.getId());
            if (StringUtil.isNotEmpty(hashValues)) {
                WorkTimeoutJobModel workTimeoutJobModel = JsonUtil.getJsonToBean(hashValues, WorkTimeoutJobModel.class);
                workTimeoutJobModel.setSuspend(isSuspend);
                String objectToString = JsonUtil.getObjectToString(workTimeoutJobModel);
                redisUtil.insertHash(WORKTIMEOUT_REDIS_KEY, workTimeoutJobModel.getTaskNodeOperatorId(), objectToString);
            }
        }
    }

}
