package org.openea.eap.extj.engine.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.*;
import org.openea.eap.extj.engine.model.flowbefore.*;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateListModel;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateUserModel;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateVO;
import org.openea.eap.extj.engine.model.flowcandidate.FlowRejectVO;
import org.openea.eap.extj.engine.model.flowdelegate.FlowDelegateModel;
import org.openea.eap.extj.engine.model.flowengine.*;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.LimitModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.Custom;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import org.openea.eap.extj.engine.model.flowmessage.FlowMsgModel;
import org.openea.eap.extj.engine.model.flowmessage.FlowParameterModel;
import org.openea.eap.extj.engine.model.flowtask.*;
import org.openea.eap.extj.engine.model.flowtask.method.TaskChild;
import org.openea.eap.extj.engine.model.flowtask.method.TaskHandleIdStatus;
import org.openea.eap.extj.engine.model.flowtask.method.TaskOperatoUser;
import org.openea.eap.extj.engine.model.flowtask.method.TaskOperator;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.engine.util.*;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.form.FlowFormVo;
import org.openea.eap.extj.permission.constant.PermissionConst;
import org.openea.eap.extj.permission.entity.*;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.job.WorkJobUtil;
import org.openea.eap.extj.job.WorkTimeoutJobUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程引擎
 *
 *
 */
@Service
@Slf4j
public class FlowTaskNewServiceImpl implements FlowTaskNewService {

    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowUserService flowUserService;
    @Autowired
    private FlowCandidatesService flowCandidatesService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    private FlowTaskCirculateService flowTaskCirculateService;
    @Autowired
    private FlowRejectDataService flowRejectDataService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskUtil flowTaskUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FlowOperatorUserService flowOperatorUserService;
    @Autowired
    private FlowMsgUtil flowMsgUtil;
    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private WorkTimeoutJobUtil workTimeoutJobUtil;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FlowEngineVisibleService flowEngineVisibleService;
    @Autowired
    private UserProvider userProvider;


    @Override
    @DSTransactional
    public FlowTaskEntity saveIsAdmin(FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity entity = this.save(flowModel);
        return entity;
    }

    @Override
    @DSTransactional
    public FlowTaskEntity save(FlowModel flowModel) throws WorkFlowException {
        String flowId = flowModel.getFlowId();
        UserInfo userInfo = flowModel.getUserInfo();
        flowModel.setStatus(StringUtil.isNotEmpty(flowModel.getStatus()) ? flowModel.getStatus() : FlowStatusEnum.save.getMessage());
        flowModel.setUserId(StringUtil.isNotEmpty(flowModel.getUserId()) ? flowModel.getUserId() : userInfo.getUserId());
        //流程引擎
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowId);
        FlowTemplateJsonEntity templateJson = templateAllModel.getTemplateJson();
        FlowTemplateEntity template = templateAllModel.getTemplate();
        FlowTaskEntity infoSubmit = flowTaskService.getInfoSubmit(flowModel.getProcessId());
        String fullName = templateJson.getFullName();
        //流程实例
        String flowTitle = StringUtil.isNotEmpty(flowModel.getFlowTitle()) ? flowModel.getFlowTitle() : userInfo.getUserName() + "的" + fullName;
        FlowTaskEntity flowTaskEntity = new FlowTaskEntity();
        flowTaskEntity.setParentId(flowModel.getParentId());
        flowTaskEntity.setIsAsync(FlowNature.ChildSync);
        flowTaskEntity.setFullName(flowTitle);
        flowTaskEntity.setStatus(FlowTaskStatusEnum.Draft.getCode());
        FlowTaskEntity taskEntity = infoSubmit != null ? infoSubmit : flowTaskEntity;
        if (infoSubmit != null) {
            //判断流程是否处于挂起状态
            flowTaskUtil.isSuspend(taskEntity);
            flowModel.setStatus(FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? FlowStatusEnum.none.getMessage() : flowModel.getStatus());
            flowModel.setUserId(taskEntity.getCreatorUserId());
            flowModel.setIsAsync(FlowNature.ChildAsync.equals(taskEntity.getIsAsync()));
            flowTitle = taskEntity.getFullName();
        }
        if (!FlowNature.ParentId.equals(taskEntity.getParentId())) {
            flowModel.setParentId(taskEntity.getParentId());
            flowTitle = taskEntity.getFullName();
        }
        ChildNode childNodeAll = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeAll.getProperties();
        Map<String, Object> data = flowModel.getFormData() != null ? flowModel.getFormData() : new HashMap<>();
        data.put("@flowFullName", fullName);
        data.put("@flowFullCode", template.getEnCode());
        data.put("@launchUserName", userInfo.getUserName());
        data.put("@launchTime", DateUtil.daFormat(new Date()));
        if (!FlowNature.TitleType.equals(properties.getTitleType())) {
            flowTitle = FlowJsonUtil.field(properties.getTitleContent(), data, "1");
            flowTitle+= !FlowNature.ParentId.equals(flowModel.getParentId()) ? "(子流程)" : "";
        }
        flowModel.setFlowTitle(flowTitle);
        flowTaskUtil.task(taskEntity, templateAllModel, flowModel);
        //更新流程任务
        flowTaskService.createOrUpdate(taskEntity);
        return taskEntity;
    }

    @Override
    @DSTransactional
    public void submit(FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        flowModel.setStatus(FlowStatusEnum.submit.getMessage());
        //流程节点
        List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        //流程经办
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        FlowTaskEntity flowTask = save(flowModel);
        boolean isRejectId = StringUtil.isEmpty(flowTask.getRejectId());
        if (isRejectId) {
            //发起用户信息
            flowTaskUtil.flowUser(flowTask);
            flowTask.setStartTime(new Date());
            flowModel.setTaskOperatorId(FlowNature.ParentId);
            //流程表单Json
            String formDataJson = flowTask.getFlowTemplateJson();
            ChildNode childNodeAll = JsonUtil.getJsonToBean(formDataJson, ChildNode.class);
            //获取流程节点
            List<ConditionList> conditionListAll = new ArrayList<>();
            FlowUpdateNode updateNode = FlowUpdateNode.builder().childNodeAll(childNodeAll).nodeListAll(nodeListAll).taskNodeList(taskNodeList).conditionListAll(conditionListAll).flowTask(flowTask).userInfo(userInfo).isSubmit(true).build();
            flowTaskUtil.updateNodeList(updateNode);
            //保存节点数据
            FlowTaskNodeEntity startNode = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().get();
            FlowTaskOperatorEntity operatorEntity = new FlowTaskOperatorEntity();
            operatorEntity.setId(FlowNature.ParentId);
            operatorEntity.setNodeCode(startNode.getNodeCode());
            FlowNodeListModel nodeListModel = new FlowNodeListModel(taskNodeList, flowModel, true, startNode, 1L);
            flowTaskUtil.nodeListAll(nodeListModel);
        } else {
            FlowRejectDataEntity info = flowRejectDataService.getInfo(flowTask.getRejectId());
            List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(flowTask.getId());
            List<FlowTaskNodeEntity> rejectTaskNodeList = JsonUtil.getJsonToList(info.getTaskNodeJson(), FlowTaskNodeEntity.class);
            for (FlowTaskNodeEntity taskNodeEntity : rejectTaskNodeList) {
                flowTaskNodeService.update(taskNodeEntity);
                for (FlowTaskNodeEntity model : list) {
                    if (model.getId().equals(taskNodeEntity.getId())) {
                        taskNodeEntity.setNodePropertyJson(model.getNodePropertyJson());
                    }
                }
            }
            List<FlowTaskNodeEntity> rejectTaskNode = flowTaskNodeService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            for (FlowTaskNodeEntity taskNodeEntity : rejectTaskNode) {
                nodeListAll.add(JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class));
                taskNodeList.add(taskNodeEntity);
            }
            FlowTaskEntity jsonToBean = JsonUtil.getJsonToBean(info.getTaskJson(), FlowTaskEntity.class);
            flowTask.setStatus(jsonToBean.getStatus());
            flowTask.setThisStep(jsonToBean.getThisStep());
            flowTask.setThisStepId(jsonToBean.getThisStepId());
            flowTask.setCompletion(jsonToBean.getCompletion());
            //还原审批数据状态
            List<FlowTaskOperatorEntity> operatorEntityList = JsonUtil.getJsonToList(info.getTaskOperatorJson(), FlowTaskOperatorEntity.class);
            for (FlowTaskOperatorEntity flowTaskOperatorEntity : operatorEntityList) {
                flowTaskOperatorService.update(flowTaskOperatorEntity);
            }
            //冻结数据传递
            FlowTaskNodeEntity startNode = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().get();
            flowTaskUtil.dataAssignment(startNode.getId(), rejectTaskNodeList, flowModel);
        }
        FlowTaskNodeEntity startNode = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().get();
        ChildNodeList start = JsonUtil.getJsonToBean(startNode.getNodePropertyJson(), ChildNodeList.class);
        //审批记录
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setTaskId(flowTask.getId());
        operator.setNodeCode(startNode.getNodeCode());
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        //审批数据赋值
        if (isRejectId) {
            FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.submit.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).build();
            flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
            flowTaskOperatorRecordService.create(operatorRecord);
        }
        List<String> nodeList = new ArrayList<>();
        nodeList.addAll(isRejectId ? Arrays.asList(startNode.getNodeNext().split(",")) : Arrays.asList(flowTask.getThisStepId().split(",")));
        //获取下一审批人
        List<ChildNodeList> nextOperatorList = nodeListAll.stream().filter(t -> nodeList.contains(t.getCustom().getNodeId())).collect(Collectors.toList());
        Map<String, List<String>> asyncTaskList = new HashMap<>();
        Map<String, List<String>> nodeTaskIdList = new HashMap<>();
        FlowOperator flowOperator = FlowOperator.builder().operatorListAll(operatorList).flowModel(flowModel).flowTask(flowTask).nodeList(nextOperatorList).taskNodeListAll(taskNodeList).userInfo(userInfo).asyncTaskList(asyncTaskList).nodeTaskIdList(nodeTaskIdList).build();
        flowTaskUtil.nextOperator(flowOperator);
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        for (ChildNodeList childNodeList : nextOperatorList) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            boolean counterSign = FlowNature.ImproperApprover.equals(properties.getCounterSign());
            if (counterSign && StringUtil.isEmpty(flowModel.getFreeApproverUserId())) {
                List<FlowTaskOperatorEntity> listAll = operatorMap.get(childNodeList.getTaskNodeId()) != null ? operatorMap.get(childNodeList.getTaskNodeId()) : new ArrayList<>();
                flowOperatorUserService.updateReject(flowTask.getId(), new HashSet() {{
                    add(childNodeList.getTaskNodeId());
                }});
                flowOperatorUserService.create(JsonUtil.getJsonToList(listAll, FlowOperatorUserEntity.class));
            }
            //todo 插入表单数据
            FlowDataModel flowDataModel = new FlowDataModel(childNodeList, taskNodeList, flowModel, true, true);
            flowTaskUtil.createOrUpdate(flowDataModel);
        }
        //过滤依次审批人
        flowTaskUtil.improperApproverUser(operatorList, taskNodeList, start, null);
        flowTaskOperatorService.create(operatorList);
        //更新关联同步子流程id
        for (String nodeId : nodeTaskIdList.keySet()) {
            FlowTaskNodeEntity entity = flowTaskNodeService.getInfo(nodeId);
            if (entity != null) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                childNodeList.getCustom().setTaskId(nodeTaskIdList.get(nodeId));
                entity.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                flowTaskNodeService.update(entity);
            }
        }
        //更新关联异步子流程id
        for (String nodeId : asyncTaskList.keySet()) {
            FlowTaskNodeEntity entity = flowTaskNodeService.getInfo(nodeId);
            if (entity != null) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                childNodeList.getCustom().setAsyncTaskList(asyncTaskList.get(nodeId));
                entity.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                flowTaskNodeService.update(entity);
            }
        }
        //定时器
        FlowTaskOperatorEntity startOperator = new FlowTaskOperatorEntity();
        startOperator.setTaskId(start.getTaskId());
        startOperator.setTaskNodeId(start.getTaskNodeId());
        DateProperties timer = start.getTimer();
        List<Date> dateList = new ArrayList<>();
        if (timer.getTime() && isRejectId) {
            Date date = new Date();
            date = DateUtil.dateAddDays(date, timer.getDay());
            date = DateUtil.dateAddHours(date, timer.getHour());
            date = DateUtil.dateAddMinutes(date, timer.getMinute());
            date = DateUtil.dateAddSeconds(date, timer.getSecond());
            dateList.add(date);
        }
        startOperator.setDescription(JsonUtil.getObjectToString(dateList));
        List<FlowTaskOperatorEntity> operatorAll = flowTaskUtil.timer(startOperator, taskNodeList, operatorList);
        for (FlowTaskOperatorEntity operatorTime : operatorAll) {
            List<Date> dateAll = JsonUtil.getJsonToList(operatorTime.getDescription(), Date.class);
            if (dateAll.size() > 0) {
                Date max = Collections.max(dateAll);
                operatorTime.setCreatorTime(max);
            }
            flowTaskOperatorService.update(operatorTime);
        }
        //更新流程节点
        if (isRejectId) {
            //修改选择分支没有走过的节点
            List<String> nodeCodeList = nextOperatorList.stream().map(t -> t.getCustom().getNodeId()).collect(Collectors.toList());
            flowTaskUtil.branchTaskNode(nodeCodeList, taskNodeList, operatorList);
            if (StringUtil.isEmpty(flowTask.getThisStepId())) {
                flowTaskUtil.getNextStepId(nextOperatorList, flowTask, flowModel);
            }
            boolean isEnd = nodeList.contains(FlowNature.NodeEnd);
            if (isEnd) {
                flowTaskUtil.endround(flowTask, nodeListAll.get(0), flowModel);
            }
        }
        flowTask.setRejectId(null);
        flowTaskService.update(flowTask);
        if (isRejectId) {
            //获取抄送人
            List<FlowTaskCirculateEntity> circulateList = new ArrayList<>();
            flowTaskUtil.circulateList(start, circulateList, flowModel, flowTask);
            flowTaskCirculateService.create(circulateList);
            //开始事件
            flowMsgUtil.event(1, start, operatorRecord, flowModel);
            //保存节点数据
            startNode.setDraftData(flowTask.getFlowFormContentJson());
            flowTaskNodeService.update(startNode);
            //自动审批
            FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(operatorList).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).isSubmit(true).build();
            flowTaskUtil.approve(approveModel);
            //发送消息
            FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowModel.getFlowId());
            FlowMsgModel flowMsgModel = new FlowMsgModel();
            flowMsgModel.setCirculateList(circulateList);
            flowMsgModel.setNodeList(taskNodeList);
            flowMsgModel.setOperatorList(operatorList);
            flowMsgModel.setData(flowModel.getFormData());
            flowMsgModel.setFlowModel(flowModel);
            flowMsgModel.setTaskEntity(flowTask);
            flowMsgModel.setCopy(true);
            flowMsgModel.setFlowTemplateAllModel(templateAllModel);
            boolean isEnd = nodeList.contains(FlowNature.NodeEnd);
            if (isEnd && isRejectId) {
                flowMsgModel.setTaskNodeEntity(startNode);
            }
//            flowMsgUtil.message(flowMsgModel);
            //定时器
            WorkJobModel workJobModel = new WorkJobModel(flowModel.getProcessId(), flowMsgModel, userInfo);
            WorkJobUtil.insertRedis(workJobModel, redisUtil);
            //超时
            insTimeOutRedis(flowModel, operatorList, userInfo, flowTask, taskNodeList);
        }
    }

    @Override
    @DSTransactional
    public void submitAll(FlowModel flowModel) throws WorkFlowException {
        try {
            this.submit(flowModel);
            FlowTaskEntity flowTask = flowTaskService.getInfo(flowModel.getProcessId());
            FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
            //委托消息
            UserInfo userInfo = flowModel.getUserInfo();
            boolean approve = StringUtil.isNotEmpty(flowTask.getDelegateUser());
            FlowDelegateModel delegate = new FlowDelegateModel();
            delegate.setToUserIds(new ArrayList() {{
                add(flowTask.getCreatorUserId());
            }});
            if (approve) {
                UserEntity userEntity = serviceUtil.getUserInfo(flowTask.getDelegateUser());
                userInfo.setUserName(userEntity != null ? userEntity.getRealName() : "");
            }
            delegate.setDelegate(false);
            delegate.setUserInfo(userInfo);
            delegate.setFlowTask(flowTask);
            delegate.setTemplateAllModel(templateAllModel);
            delegate.setApprove(approve);
            flowMsgUtil.delegateMsg(delegate);
            //表单数据
            Map<String, Map<String, Object>> childAllData = FlowContextHolder.getChildAllData();
            for (String idAll : childAllData.keySet()) {
                String[] idList = idAll.split("_jnpf_");
                Map<String, Object> formData = childAllData.get(idAll);
                serviceUtil.createOrUpdate(idList[1], idList[0], formData);
            }
            List<FlowParameterModel> parameterModels = FlowContextHolder.getAllEvent();
            for (FlowParameterModel model : parameterModels) {
                serviceUtil.infoToId(model.getInterId(), model.getParameterMap());
            }
        } finally {
            FlowContextHolder.clearAll();
        }
    }

    @Override
    @DSTransactional
    public void audit(FlowTaskEntity flowTask, FlowTaskOperatorEntity operator, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        //判断是否审批过
        if (!FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            throw new WorkFlowException(MsgCode.WF005.get());
        }
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        //主流程冻结,子流程结束报错
        if (StringUtil.isNotEmpty(flowTask.getRejectId()) && StringUtil.isEmpty(operator.getReject())) {
            throw new WorkFlowException(MsgCode.WF129.get());
        }
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        boolean before = FlowNature.Before.equals(flowModel.getFreeApproverType());
        //更新表单数据
        boolean isOperator = StringUtil.isNotEmpty(operator.getId());
        boolean isUser = StringUtil.isNotEmpty(flowModel.getFreeApproverUserId());
        boolean isReject = StringUtil.isEmpty(flowTask.getRejectId());
        flowModel.setTaskOperatorId(operator.getId());
        flowModel.setParentId(isUser ? operator.getId() : flowModel.getParentId());
        flowModel.setRollbackId(before ? operator.getId() : operator.getRollbackId());
        flowModel.setRejectUser(StringUtil.isNotEmpty(flowTask.getRejectId()) ? true : false);
        FlowTaskNodeEntity flowTaskNode = flowTaskNodeService.getInfo(operator.getTaskNodeId());
        //自动审批获取最新表单数据
        Map<String, Object> formData = flowModel.getVoluntarily() ? flowTaskUtil.infoData(flowTaskNode.getFormId(), flowTask.getId()) : flowModel.getFormData();
        if (!FlowNature.NodeSubFlow.equals(flowTaskNode.getNodeType())) {
            FlowContextHolder.addData(flowTaskNode.getFormId(), formData);
            FlowContextHolder.addChildData(flowTask.getId(), flowTaskNode.getFormId(), formData);
        }
        //todo
        if (!flowModel.getIsAsync() && isReject) {
            flowTaskUtil.auditTaskNode(templateAllModel, flowTask, flowTaskNode, flowModel);
        }
        //加签回流
        if (!isUser) {
            String rollbackId = StringUtil.isNotEmpty(operator.getRollbackId()) ? operator.getRollbackId() : RandomUtil.uuId();
            FlowTaskOperatorEntity operatorInfo = flowTaskOperatorService.getOperatorInfo(rollbackId);
            if (operatorInfo != null) {
                FlowModel rollbackModel = new FlowModel();
                rollbackModel.setUserInfo(flowModel.getUserInfo());
                rollbackModel.setTaskOperatorId(flowModel.getTaskOperatorId());
                rollbackModel.setParentId(flowModel.getTaskOperatorId());
                rollbackModel.setCopyIds(flowModel.getCopyIds());
                rollbackModel.setFileList(flowModel.getFileList());
                rollbackModel.setHandleOpinion(flowModel.getHandleOpinion());
                rollbackModel.setSignImg(flowModel.getSignImg());
                rollbackModel.setRejectUser(flowModel.getRejectUser());
                flowModel = JsonUtil.getJsonToBean(rollbackModel, FlowModel.class);
                flowModel.setFreeApproverUserId(operatorInfo.getHandleId());
                flowModel.setRollbackId(operatorInfo.getRollbackId());
                flowModel.setFreeApproverType(FlowNature.Reflux);
                flowModel.setTaskOperatorId(FlowNature.ParentId.equals(operatorInfo.getParentId()) ? FlowNature.ParentId : flowModel.getTaskOperatorId());
            }
        }
        String userId = StringUtil.isNotEmpty(flowModel.getUserId()) ? flowModel.getUserId() : userInfo.getUserId();
        //流程所有节点
        List<FlowTaskNodeEntity> flowTaskNodeAll = flowTaskNodeService.getList(flowTask.getId());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeAll.stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //当前节点
        Optional<FlowTaskNodeEntity> first = taskNodeList.stream().filter(m -> m.getId().equals(operator.getTaskNodeId())).findFirst();
        if (!first.isPresent()) {
            throw new WorkFlowException(MsgCode.COD001.get());
        }
        FlowTaskNodeEntity taskNode = first.get();
        //当前节点属性
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        //同意记录
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.audit.getCode()).flowModel(flowModel).userId(userId).operator(operator).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        //子流程不新增流转记录
        if (!flowModel.getIsAsync() && !before) {
            flowTaskOperatorRecordService.create(operatorRecord);
        }
        //修改或签、会签经办数据
        TaskHandleIdStatus handleIdStatus = new TaskHandleIdStatus();
        handleIdStatus.setStatus(1);
        handleIdStatus.setNodeModel(nodeModel);
        handleIdStatus.setUserInfo(userInfo);
        handleIdStatus.setTaskNodeList(taskNodeList);
        handleIdStatus.setFlowModel(flowModel);
        flowTaskUtil.handleIdStatus(operator, handleIdStatus);
        //更新流当前程经办状态
        if (isOperator) {
            operator.setState(StringUtil.isNotEmpty(flowModel.getFreeApproverUserId()) ? FlowNodeEnum.FreeApprover.getCode() : operator.getState());
            flowTaskOperatorService.update(operator);
        }
        //更新下一节点
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        //获取下一审批人
        List<FlowTaskNodeEntity> nextNode = taskNodeList.stream().filter(t -> taskNode.getNodeNext().contains(t.getNodeCode())).collect(Collectors.toList());
        List<ChildNodeList> nextOperatorList = new ArrayList<>();
        List<FlowTaskNodeEntity> result = flowTaskUtil.isNextAll(taskNodeList, nextNode, taskNode, flowModel);
        flowTaskUtil.candidateList(flowModel, taskNodeList, operator);
        if (result.size() > 0) {
            boolean freeApproverUserId = StringUtil.isEmpty(flowModel.getFreeApproverUserId());
            if (freeApproverUserId && !isReject) {
                FlowRejectDataEntity info = flowRejectDataService.getInfo(flowTask.getRejectId());
                FlowTaskEntity jsonToBean = JsonUtil.getJsonToBean(info.getTaskJson(), FlowTaskEntity.class);
                jsonToBean.setRejectId(null);
                flowTask.setRejectId(null);
                flowModel.setRejectUser(false);
                flowTaskService.update(jsonToBean);
                Set<String> rejectNodeList = new HashSet<>();
                List<String> rejectList = new ArrayList() {{
                    add(operator.getTaskNodeId());
                }};
                List<String> thisStepId = Arrays.asList(jsonToBean.getThisStepId().split(","));
                List<FlowTaskNodeEntity> rejectTaskNodeList = JsonUtil.getJsonToList(info.getTaskNodeJson(), FlowTaskNodeEntity.class);
                flowTaskUtil.upAll(rejectNodeList, rejectList, rejectTaskNodeList);
                for (FlowTaskNodeEntity taskNodeEntity : rejectTaskNodeList) {
                    FlowTaskNodeEntity node = flowTaskNodeAll.stream().filter(t -> t.getId().equals(taskNodeEntity.getId())).findFirst().orElse(null);
                    taskNodeEntity.setDraftData(node != null ? JsonUtil.getObjectToString(flowModel.getFormData()) : taskNodeEntity.getDraftData());
                    flowTaskNodeService.update(taskNodeEntity);
                    taskNodeEntity.setNodePropertyJson(node != null ? node.getNodePropertyJson() : "{}");
                }
                //获取
                List<FlowTaskNodeEntity> rejectNodeAll = rejectTaskNodeList.stream().filter(t -> rejectNodeList.contains(t.getId())).collect(Collectors.toList());
                for (FlowTaskNodeEntity taskNodeEntity : rejectNodeAll) {
                    if (thisStepId.contains(taskNodeEntity.getNodeCode())) {
                        nextOperatorList.add(JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class));
                    }
                }
                //还原审批数据状态
                List<FlowTaskOperatorEntity> operatorEntityList = JsonUtil.getJsonToList(info.getTaskOperatorJson(), FlowTaskOperatorEntity.class);
                for (FlowTaskOperatorEntity flowTaskOperatorEntity : operatorEntityList) {
                    flowTaskOperatorService.update(flowTaskOperatorEntity);
                }
                //冻结数据传递
                flowTaskUtil.dataAssignment(operator.getTaskNodeId(), rejectTaskNodeList, flowModel);
            } else {
                for (FlowTaskNodeEntity entity : result) {
                    ChildNodeList node = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                    nextOperatorList.add(node);
                }
            }
        }
        //下个节点
        Map<String, List<String>> asyncTaskList = new HashMap<>();
        Map<String, List<String>> nodeTaskIdList = new HashMap<>();
        FlowOperator flowOperator = FlowOperator.builder().operatorListAll(operatorList).flowModel(flowModel).flowTask(flowTask).nodeList(nextOperatorList).taskNodeListAll(taskNodeList).userInfo(userInfo).asyncTaskList(asyncTaskList).nodeTaskIdList(nodeTaskIdList).build();
        flowTaskUtil.nextOperator(flowOperator);
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        for (ChildNodeList childNodeList : nextOperatorList) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            boolean counterSign = FlowNature.ImproperApprover.equals(properties.getCounterSign());
            if (counterSign && StringUtil.isEmpty(flowModel.getFreeApproverUserId())) {
                List<FlowTaskOperatorEntity> listAll = operatorMap.get(childNodeList.getTaskNodeId()) != null ? operatorMap.get(childNodeList.getTaskNodeId()) : new ArrayList<>();
                flowOperatorUserService.create(JsonUtil.getJsonToList(listAll, FlowOperatorUserEntity.class));
            }
            FlowDataModel flowDataModel = new FlowDataModel(childNodeList, taskNodeList, flowModel, true, true);
            flowTaskUtil.createOrUpdate(flowDataModel);
        }
        //过滤依次审批人
        if (StringUtil.isEmpty(flowModel.getFreeApproverUserId())) {
            flowTaskUtil.improperApproverUser(operatorList, taskNodeList, nodeModel, operator);
        }
        flowTaskOperatorService.create(operatorList);
        //修改选择分支没有走过的节点
        if (isReject) {
            List<String> nodeCodeList = result.stream().map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList());
            flowTaskUtil.branchTaskNode(nodeCodeList, taskNodeList, operatorList);
            //更新流程节点
            flowTaskUtil.getNextStepId(nextOperatorList, flowTask, flowModel);
            flowTask.setTaskNodeId(null);
            flowTask.setCompletion(FlowNature.NodeEnd.equals(flowTask.getThisStepId()) ? FlowNature.Progress : flowTask.getCompletion());
            if (StringUtil.isNotEmpty(flowTask.getThisStepId())) {
                flowTaskService.update(flowTask);
            }
        }
        //更新关联同步子流程id
        for (String nodeId : nodeTaskIdList.keySet()) {
            FlowTaskNodeEntity entity = flowTaskNodeService.getInfo(nodeId);
            if (entity != null) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                childNodeList.getCustom().setTaskId(nodeTaskIdList.get(nodeId));
                entity.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                flowTaskNodeService.update(entity);
            }
        }
        //更新关联异步子流程id
        for (String nodeId : asyncTaskList.keySet()) {
            FlowTaskNodeEntity entity = flowTaskNodeService.getInfo(nodeId);
            if (entity != null) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                childNodeList.getCustom().setAsyncTaskList(asyncTaskList.get(nodeId));
                entity.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                flowTaskNodeService.update(entity);
            }
        }
        //定时器
        if(isReject) {
            List<FlowTaskOperatorEntity> operatorAll = flowTaskUtil.timer(operator, taskNodeList, operatorList);
            for (FlowTaskOperatorEntity operatorTime : operatorAll) {
                List<Date> dateAll = JsonUtil.getJsonToList(operatorTime.getDescription(), Date.class);
                if (dateAll.size() > 0) {
                    Date max = Collections.max(dateAll);
                    operatorTime.setCreatorTime(max);
                }
                flowTaskOperatorService.update(operatorTime);
            }
        }
        //更新节点接收时间
        flowTaskUtil.taskCreatTime(operatorList);
        if (isReject) {
            //节点事件
            flowMsgUtil.event(4, nodeModel, operatorRecord, flowModel);
            //获取抄送人
            List<FlowTaskCirculateEntity> circulateList = new ArrayList<>();
            flowTaskUtil.circulateList(nodeModel, circulateList, flowModel, flowTask);
            flowTaskCirculateService.create(circulateList);
            //发送消息
            FlowMsgModel flowMsgModel = new FlowMsgModel();
            flowMsgModel.setApprove(FlowNature.AuditCompletion.equals(taskNode.getCompletion()));
            flowMsgModel.setCopy(true);
            flowMsgModel.setNodeList(taskNodeList);
            List<FlowTaskOperatorEntity> operatorListAll = new ArrayList() {{
                addAll(operatorList);
            }};
            flowMsgModel.setOperatorList(operatorListAll);
            flowMsgModel.setCirculateList(circulateList);
            flowMsgModel.setData(flowModel.getFormData());
            flowMsgModel.setTaskNodeEntity(taskNode);
            flowMsgModel.setTaskEntity(flowTask);
            flowMsgModel.setFlowTemplateAllModel(templateAllModel);
            FlowTaskOperatorRecordEntity taskOperatorRecord = new FlowTaskOperatorRecordEntity();
            taskOperatorRecord.setHandleId(userInfo.getUserId());
            flowMsgModel.setFlowModel(flowModel);
//            flowMsgUtil.message(flowMsgModel);
            //定时器
            WorkJobModel workJobModel = new WorkJobModel(StringUtil.isNotEmpty(operator.getId()) ? operator.getId() : RandomUtil.uuId(), flowMsgModel, userInfo);
            WorkJobUtil.insertRedis(workJobModel, redisUtil);
            //超时
            insTimeOutRedis(flowModel, operatorListAll, userInfo, flowTask, taskNodeList);
            //自动审批
            FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(operatorListAll).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
            flowTaskUtil.approve(approveModel);
            //查询代办用户是否通过
            flowTaskUtil.approverPass(flowTask, taskNodeList, flowModel, operator);
        }
    }

    @Override
    @DSTransactional
    public void auditAll(FlowTaskEntity flowTask, FlowTaskOperatorEntity operator, FlowModel flowModel) throws WorkFlowException {
        try {
            UserInfo userInfo = flowModel.getUserInfo();
            boolean approve = !userInfo.getUserId().equals(operator.getHandleId());
            FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
            this.audit(flowTask, operator, flowModel);
            //委托消息
            FlowDelegateModel delegate = new FlowDelegateModel();
            delegate.setToUserIds(new ArrayList() {{
                add(operator.getHandleId());
            }});
            delegate.setDelegate(false);
            delegate.setType("1");
            delegate.setUserInfo(userInfo);
            delegate.setFlowTask(flowTask);
            delegate.setTemplateAllModel(templateAllModel);
            delegate.setApprove(approve);
            flowMsgUtil.delegateMsg(delegate);
            //表单数据
            Map<String, Map<String, Object>> childAllData = FlowContextHolder.getChildAllData();
            for (String idAll : childAllData.keySet()) {
                String[] idList = idAll.split("_jnpf_");
                Map<String, Object> formData = childAllData.get(idAll);
                serviceUtil.createOrUpdate(idList[1], idList[0], formData);
            }
            List<FlowParameterModel> parameterModels = FlowContextHolder.getAllEvent();
            for (FlowParameterModel model : parameterModels) {
                serviceUtil.infoToId(model.getInterId(), model.getParameterMap());
            }
        } finally {
            FlowContextHolder.clearAll();
        }
    }

    @Override
    @DSTransactional
    public void reject(FlowTaskEntity flowTask, FlowTaskOperatorEntity operator, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        String userId = StringUtil.isNotEmpty(flowModel.getUserId()) ? flowModel.getUserId() : userInfo.getUserId();
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        //流程所有节点
        List<FlowTaskNodeEntity> flowTaskNodeAll = flowTaskNodeService.getList(flowTask.getId());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeAll.stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        List<FlowTaskOperatorEntity> operatorEntityList = flowTaskOperatorService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //保存当前节点和任务
        String taskJson = JsonUtil.getObjectToString(JsonUtil.getJsonToBean(flowTask, FlowTaskRejectModel.class));
        String taskNodeJson = JsonUtil.getObjectToString(JsonUtil.getJsonToList(taskNodeList, FlowTaskNodeRejectModel.class));
        //当前节点
        Optional<FlowTaskNodeEntity> first = taskNodeList.stream().filter(m -> m.getId().equals(operator.getTaskNodeId())).findFirst();
        if (!first.isPresent()) {
            throw new WorkFlowException(MsgCode.COD001.get());
        }
        FlowTaskNodeEntity taskNode = first.get();
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        flowTaskUtil.candidateList(flowModel, taskNodeList, operator);
        //当前节点属性
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        boolean rejectType = FlowNature.PresentType.equals(flowModel.getRejectType());
        taskNode.setNodeUp(flowModel.getRejectStep());
        //驳回记录
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.reject.getCode()).flowModel(flowModel).userId(userId).operator(operator).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //修改或签、会签经办数据
        TaskHandleIdStatus handleIdStatus = new TaskHandleIdStatus();
        handleIdStatus.setStatus(0);
        handleIdStatus.setNodeModel(nodeModel);
        handleIdStatus.setUserInfo(userInfo);
        handleIdStatus.setTaskNodeList(taskNodeList);
        flowTaskUtil.handleIdStatus(operator, handleIdStatus);
        //更新流当前程经办状态
        flowTaskOperatorService.update(operator);
        //判断是否是会签
        List<FlowTaskOperatorEntity> rejectOperatorList = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(taskNode.getId()) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        FlowCountersignModel countersign = new FlowCountersignModel();
        countersign.setTaskNodeId(taskNode.getId());
        countersign.setOperatorList(rejectOperatorList);
        boolean isReject = flowTaskUtil.isRejectCountersign(countersign);
        //更新驳回节点
        List<ChildNodeList> nextOperatorList = new ArrayList<>();
        Set<FlowTaskNodeEntity> thisStepAll = new HashSet<>();
        String[] thisStepId = flowTask.getThisStepId().split(",");
        List<FlowTaskNodeEntity> upAll = flowTaskUtil.isUpAll(taskNodeList, taskNode, isReject, thisStepAll, thisStepId);
        List<String> rejectList = new ArrayList<>();
        for (FlowTaskNodeEntity entity : upAll) {
            ChildNodeList node = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
            nextOperatorList.add(node);
            rejectList.add(entity.getId());
        }
        //驳回节点
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        //如果开始节点就不需要找下一节点
        boolean isStart = nextOperatorList.stream().filter(t -> FlowNature.NodeStart.equals(t.getCustom().getType())).count() > 0;
        if (!isStart) {
            //赋值数据
            flowModel.setProcessId(flowTask.getId());
            flowModel.setId(flowTask.getId());
            Map<String, Object> data = JsonUtil.stringToMap(flowTask.getFlowFormContentJson());
            flowModel.setFormData(data);
            FlowOperator flowOperator = FlowOperator.builder().operatorListAll(operatorList).flowModel(flowModel).flowTask(flowTask).nodeList(nextOperatorList).taskNodeListAll(taskNodeList).reject(true).asyncTaskList(new HashMap<>()).build();
            flowTaskUtil.nextOperator(flowOperator);
        }
        //更新驳回当前节点
        List<String> stepIdList = new ArrayList<>();
        List<String> stepNameList = new ArrayList<>();
        List<String> progressList = new ArrayList<>();
        for (FlowTaskNodeEntity taskNodes : thisStepAll) {
            ChildNodeList childNode = JsonUtil.getJsonToBean(taskNodes.getNodePropertyJson(), ChildNodeList.class);
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            String progress = properties.getProgress();
            if (StringUtil.isNotEmpty(progress)) {
                progressList.add(progress);
            }
            stepIdList.add(taskNodes.getNodeCode());
            stepNameList.add(taskNodes.getNodeName());
        }
        List<FlowTaskNodeEntity> rejectNodeListAll = new ArrayList<>();
        //会签拒绝更新未审批用户
        Set<String> rejectNodeList = new HashSet<>();
        //驳回比例不够，不修改当前节点
        if (thisStepAll.size() > 0) {
            //驳回比例够又没有人,异常是默认通过,不修改当前节点
            if (operatorList.size() != 0) {
                Collections.sort(progressList);
                flowTask.setCompletion(progressList.size() > 0 ? Integer.parseInt(progressList.get(0)) : 0);
                flowTask.setThisStepId(String.join(",", stepIdList));
                flowTask.setThisStep(String.join(",", stepNameList));
                //判断驳回节点是否是开发节点
                flowTask.setStatus(isStart ? FlowTaskStatusEnum.Reject.getCode() : flowTask.getStatus());
                flowTaskUtil.upAll(rejectNodeList, rejectList, taskNodeList);
                flowTaskOperatorService.updateReject(flowTask.getId(), rejectNodeList);
                flowOperatorUserService.updateReject(flowTask.getId(), rejectNodeList);
                //驳回节点之后的状态修改
                flowTaskNodeService.updateCompletion(new ArrayList<>(rejectNodeList), FlowNature.ProcessCompletion);
                flowTaskOperatorRecordService.updateStatus(rejectNodeList, flowTask.getId());
                rejectNodeListAll.addAll(taskNodeList.stream().filter(t -> rejectNodeList.contains(t.getId())).collect(Collectors.toList()));
                //删除节点候选人
                List<String> candidates = new ArrayList<>();
                rejectNodeList.removeAll(rejectList);
                candidates.addAll(rejectNodeList);
                if (!rejectType) {
                    flowCandidatesService.deleteTaskNodeId(candidates);
                    flowTaskNodeService.updateTaskNodeCandidates(candidates);
                    //终止子流程
                    for (FlowTaskNodeEntity taskNodeEntity : rejectNodeListAll) {
                        ChildNodeList childNodeList = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
                        List<String> taskIdList = new ArrayList<>();
                        taskIdList.addAll(childNodeList.getCustom().getAsyncTaskList());
                        taskIdList.addAll(childNodeList.getCustom().getTaskId());
                        List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(taskIdList);
                        for (FlowTaskEntity flowTaskEntity : orderStaList) {
                            FlowModel cancelModel = new FlowModel();
                            cancelModel.setUserInfo(flowModel.getUserInfo());
                            this.cancel(flowTaskEntity, cancelModel);
                        }
                    }
                }
            }
        }
        //保存冻结的数据
        if (isStart || (thisStepAll.size() > 0 && operatorList.size() != 0)) {
            if (isStart) {
                rejectNodeList.addAll(taskNodeList.stream().map(FlowTaskNodeEntity::getId).collect(Collectors.toList()));
            }
            if (rejectType) {
                for (FlowTaskOperatorEntity operatorEntity : operatorEntityList) {
                    boolean count = rejectNodeList.contains(operatorEntity.getTaskNodeId());
                    operatorEntity.setState(count ? FlowNodeEnum.Futility.getCode() : operatorEntity.getState());
//                    operatorEntity.setState(count ? FlowNodeEnum.Process.getCode() : operatorEntity.getState());
//                    operatorEntity.setCompletion(count ? FlowNature.AuditCompletion : operatorEntity.getCompletion());
                }
                String operatorJson = JsonUtil.getObjectToString(JsonUtil.getJsonToList(operatorEntityList, FlowTaskOperatorRejectModel.class));
                FlowRejectDataEntity rejectEntity = new FlowRejectDataEntity();
                rejectEntity.setTaskNodeJson(taskNodeJson);
                rejectEntity.setTaskOperatorJson(operatorJson);
                rejectEntity.setTaskJson(taskJson);
                rejectEntity.setId(flowTask.getId());
                flowTask.setRejectId(flowTask.getId());
                flowRejectDataService.createOrUpdate(rejectEntity);
            }
        }
        //驳回开始节点
        if (isStart) {
            flowTask.setStatus(FlowTaskStatusEnum.Reject.getCode());
            flowTask.setCompletion(FlowNature.ProcessCompletion);
            flowTask.setThisStepId(String.join(",", new ArrayList<>()));
            flowTask.setThisStep(String.join(",", new ArrayList<>()));
            flowTaskNodeService.update(flowTask.getId());
            flowTaskOperatorService.update(flowTask.getId());
            flowTaskOperatorRecordService.update(flowTask.getId());
            if (!rejectType) {
                flowUserService.deleteByTaskId(flowTask.getId());
            }
            flowOperatorUserService.deleteByTaskId(flowTask.getId());
        }
        //更新流程节点
        flowTask.setTaskNodeId(null);
        flowTaskService.update(flowTask);
        //显示当前的驳回记录
        flowTaskOperatorRecordService.update(operatorRecord.getId(), operatorRecord);
        for (FlowTaskOperatorEntity operatorEntity : operatorList) {
            operatorEntity.setReject(rejectType ? "1" : "");
        }
        //创建审批人
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        for (ChildNodeList childNodeList : nextOperatorList) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            boolean counterSign = FlowNature.ImproperApprover.equals(properties.getCounterSign());
            if (counterSign && StringUtil.isEmpty(flowModel.getFreeApproverUserId())) {
                List<FlowTaskOperatorEntity> listAll = operatorMap.get(childNodeList.getTaskNodeId()) != null ? operatorMap.get(childNodeList.getTaskNodeId()) : new ArrayList<>();
                flowOperatorUserService.create(JsonUtil.getJsonToList(listAll, FlowOperatorUserEntity.class));
            }
        }
        //过滤依次审批人
        flowTaskUtil.improperApproverUser(operatorList, taskNodeList, nodeModel, operator);
        flowTaskOperatorService.create(operatorList);
        //更新节点接收时间
        flowTaskUtil.taskCreatTime(operatorList);
        //获取抄送人
        List<FlowTaskCirculateEntity> circulateList = new ArrayList<>();
        flowTaskUtil.circulateList(nodeModel, circulateList, flowModel, flowTask);
        flowTaskCirculateService.create(circulateList);
        //节点事件
        flowMsgUtil.event(5, nodeModel, operatorRecord, flowModel);
        //发送消息
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setCirculateList(circulateList);
        flowMsgModel.setNodeList(taskNodeList);
        List<FlowTaskOperatorEntity> operatorListAll = new ArrayList() {{
            addAll(operatorList);
        }};
        flowMsgModel.setOperatorList(operatorListAll);
        flowMsgModel.setReject(true);
        flowMsgModel.setCopy(true);
        flowMsgModel.setStart(isStart);
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setTaskNodeEntity(taskNode);
        flowMsgModel.setTaskEntity(flowTask);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        FlowTaskOperatorRecordEntity taskOperatorRecord = new FlowTaskOperatorRecordEntity();
        taskOperatorRecord.setHandleId(userInfo.getUserId());
        flowMsgModel.setFlowModel(flowModel);
        if (isStart) {
            flowMsgUtil.message(flowMsgModel);
        } else {
            //定时器
            WorkJobModel workJobModel = new WorkJobModel(operator.getId(), flowMsgModel, userInfo);
            WorkJobUtil.insertRedis(workJobModel, redisUtil);
        }
        //超时
        insTimeOutRedis(flowModel, operatorListAll, userInfo, flowTask, taskNodeList);
        //自动审批
        FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(operatorListAll).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
        flowTaskUtil.approve(approveModel);
    }

    @Override
    @DSTransactional
    public void rejectAll(FlowTaskEntity flowTask, FlowTaskOperatorEntity operator, FlowModel flowModel) throws WorkFlowException {
        try {
            if (StringUtil.isNotEmpty(flowTask.getRejectId())) {
                throw new WorkFlowException("退回至您的审批，不能再发起退回");
            }
            this.reject(flowTask, operator, flowModel);
            Map<String, Map<String, Object>> childAllData = FlowContextHolder.getChildAllData();
            for (String idAll : childAllData.keySet()) {
                String[] idList = idAll.split("_jnpf_");
                Map<String, Object> formData = childAllData.get(idAll);
                serviceUtil.createOrUpdate(idList[1], idList[0], formData);
            }
            List<FlowParameterModel> parameterModels = FlowContextHolder.getAllEvent();
            for (FlowParameterModel model : parameterModels) {
                serviceUtil.infoToId(model.getInterId(), model.getParameterMap());
            }
        } finally {
            FlowContextHolder.clearAll();
        }
    }

    @Override
    @DSTransactional
    public void recall(String id, FlowTaskOperatorRecordEntity operatorRecord, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        //撤回经办
        FlowTaskOperatorEntity operatorEntity = flowTaskOperatorService.getInfo(operatorRecord.getTaskOperatorId());
        if (FlowNodeEnum.Futility.getCode().equals(operatorEntity.getState())) {
            throw new WorkFlowException(MsgCode.WF104.get());
        }
        FlowTaskOperatorEntity rollbackOperator = flowTaskOperatorService.getOperatorInfo(StringUtil.isNotEmpty(operatorEntity.getRollbackId()) ? operatorEntity.getRollbackId() : RandomUtil.uuId());
        boolean isParentId = (FlowNature.ParentId.equals(operatorEntity.getParentId()) || (rollbackOperator != null && FlowNature.ParentId.equals(rollbackOperator.getParentId())));
        //撤回节点
        FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeService.getInfo(operatorRecord.getTaskNodeId());
        //撤回任务
        FlowTaskEntity flowTask = flowTaskService.getInfo(operatorRecord.getTaskId());
        if (StringUtil.isNotEmpty(flowTask.getRejectId())) {
            throw new WorkFlowException("退回至您的审批,不能再退回审批.");
        }
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        //所有节点
        List<FlowTaskNodeEntity> flowTaskNodeEntityList = flowTaskNodeService.getList(operatorRecord.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //所有经办
        List<FlowTaskOperatorEntity> flowTaskOperatorEntityList = flowTaskOperatorService.getList(operatorRecord.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //撤回节点属性
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(flowTaskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
        //拒绝不撤回
        if (FlowNature.ProcessCompletion.equals(operatorEntity.getHandleStatus())) {
            throw new WorkFlowException(MsgCode.WF104.get());
        }
        //任务待审状态才能撤回
        if (!(flowTask.getEnabledMark() == 1 && FlowTaskStatusEnum.Handle.getCode().equals(flowTask.getStatus()))) {
            throw new WorkFlowException(MsgCode.WF105.get());
        }
        //撤回节点下一节点已操作
        List<FlowTaskOperatorEntity> recallNextOperatorList = flowTaskOperatorEntityList.stream().filter(x -> flowTaskNodeEntity.getNodeNext().contains(x.getNodeCode())).collect(Collectors.toList());
        boolean isRecall = recallNextOperatorList.stream().filter(t -> FlowNature.AuditCompletion.equals(t.getCompletion()) && FlowNodeEnum.Process.getCode().equals(t.getState())).count() > 0;
        boolean isNode = FlowNature.AuditCompletion.equals(flowTaskNodeEntity.getCompletion()) && recallNextOperatorList.size() == 0;
        if (isRecall || isNode) {
            throw new WorkFlowException(MsgCode.WF106.get());
        }
        List<FlowTaskEntity> childList = flowTaskService.getChildList(operatorRecord.getTaskId(), FlowTaskEntity::getId, FlowTaskEntity::getStatus).stream().filter(t -> !FlowTaskStatusEnum.Cancel.getCode().equals(t.getStatus())).collect(Collectors.toList());
        boolean isNext = childList.size() > 0;
        if (isNext) {
            throw new WorkFlowException(MsgCode.WF107.get());
        }
        //加签人
        Set<FlowTaskOperatorEntity> operatorList = new HashSet<>();
        flowTaskUtil.getOperator(operatorEntity.getId(), operatorList);
        operatorEntity.setHandleStatus(null);
        operatorEntity.setHandleTime(null);
        operatorEntity.setCreatorTime(new Date());
        operatorEntity.setCompletion(FlowNature.ProcessCompletion);
        operatorEntity.setState(FlowNodeEnum.Process.getCode());
        operatorList.add(operatorEntity);
        List<String> delOperatorRecordIds = new ArrayList<>();
        for (FlowTaskOperatorEntity item : operatorList) {
            FlowTaskOperatorRecordEntity record = flowTaskOperatorRecordService.getInfo(item.getTaskId(), item.getTaskNodeId(), item.getId());
            if (record != null) {
                delOperatorRecordIds.add(record.getId());
            }
        }
        Set<String> rejectNodeList = new HashSet<>();
        List<String> rejectList = new ArrayList() {{
            add(flowTaskNodeEntity.getId());
        }};
        flowTaskUtil.upAll(rejectNodeList, rejectList, flowTaskNodeEntityList);
        //撤回节点是否完成
        if (FlowNature.AuditCompletion.equals(flowTaskNodeEntity.getCompletion())) {
            //撤回节点下一节点经办删除
            List<String> idAll = recallNextOperatorList.stream().map(FlowTaskOperatorEntity::getId).collect(Collectors.toList());
            flowTaskOperatorService.updateTaskOperatorState(idAll);
            List<FlowTaskOperatorEntity> hanleOperatorList = flowTaskOperatorEntityList.stream().filter(x -> x.getTaskNodeId().equals(operatorRecord.getTaskNodeId()) && Objects.isNull(x.getHandleStatus()) && Objects.isNull(x.getHandleTime()) && Objects.isNull(x.getParentId())).collect(Collectors.toList());
            for (FlowTaskOperatorEntity taskOperator : hanleOperatorList) {
                taskOperator.setCompletion(FlowNature.ProcessCompletion);
            }
            operatorList.addAll(hanleOperatorList);
            flowTaskNodeService.updateCompletion(new ArrayList<>(rejectNodeList), FlowNature.ProcessCompletion);
            //更新任务流程
            List<String> stepIdList = new ArrayList<>(Arrays.asList(flowTask.getThisStepId().split(",")));
            List<String> stepNameList = new ArrayList<>();
            List<String> progressList = new ArrayList<>();
            List<String> rejectNodeCode = flowTaskNodeEntityList.stream().filter(t -> rejectNodeList.contains(t.getId())).map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList());
            stepIdList.removeAll(rejectNodeCode);
            stepIdList.add(flowTaskNodeEntity.getNodeCode());
            List<FlowTaskNodeEntity> recallNodeList = flowTaskNodeEntityList.stream().filter(x -> stepIdList.contains(x.getNodeCode())).collect(Collectors.toList());
            for (FlowTaskNodeEntity taskNodeEntity : recallNodeList) {
                ChildNodeList childNode = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
                org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
                String progress = properties.getProgress();
                if (StringUtil.isNotEmpty(progress)) {
                    progressList.add(progress);
                }
                stepIdList.add(taskNodeEntity.getNodeCode());
                stepNameList.add(taskNodeEntity.getNodeName());
            }
            //更新当前节点
            flowTask.setCompletion(progressList.size() > 0 ? Integer.parseInt(progressList.get(0)) : 0);
            flowTask.setThisStepId(String.join(",", stepIdList));
            flowTask.setThisStep(String.join(",", stepNameList));
            flowTask.setStatus(FlowTaskStatusEnum.Handle.getCode());
            flowTaskService.update(flowTask);
        }
        for (FlowTaskOperatorEntity taskOperator : operatorList) {
            flowTaskOperatorService.update(taskOperator);
        }
        if (isParentId) {
            FlowTaskNodeEntity node = flowTaskNodeService.getInfo(operatorEntity.getTaskNodeId());
            List<String> nextNode = Arrays.asList(node.getNodeNext().split(","));
            List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(operatorEntity.getTaskId()).stream().filter(t -> nextNode.contains(t.getNodeCode()) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            int num = 0;
            for (FlowTaskNodeEntity taskNode : list) {
                List<String> candidateList = StringUtil.isNotEmpty(taskNode.getCandidates()) ? JsonUtil.getJsonToList(taskNode.getCandidates(), String.class) : new ArrayList<>();
                num += candidateList.size() > 0 ? 1 : 0;
            }
            if (num > 0) {
                //删除异常人
                List<String> candidateList = list.stream().map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
                flowCandidatesService.deleteTaskNodeId(candidateList, FlowNature.Candidates);
            }
            rejectNodeList.removeAll(rejectList);
            flowTaskNodeService.updateTaskNodeCandidates(new ArrayList<>(rejectNodeList));
            //删除异常人
            flowCandidatesService.deleteTaskNodeId(new ArrayList<>(rejectNodeList), FlowNature.CandidatesError);
            //删除依次审批人
            FlowOperatorUserEntity nextOperatorUser = flowOperatorUserService.getList(flowTask.getId()).stream().filter(t -> operatorEntity.getSortCode().equals(t.getSortCode() - 1)).findFirst().orElse(null);
            if (nextOperatorUser != null) {
                boolean count = flowTaskOperatorEntityList.stream().filter(t -> t.getId().equals(nextOperatorUser.getId()) && FlowNature.AuditCompletion.equals(t.getCompletion())).count() > 0;
                if (count) {
                    throw new WorkFlowException(MsgCode.WF104.get());
                } else {
                    flowTaskOperatorService.deleteList(new ArrayList() {{
                        add(nextOperatorUser.getId());
                    }});
                    flowOperatorUserService.updateReject(flowTask.getId(), rejectNodeList);
                }
            }
        }
        List<String> taskOperatorId = operatorList.stream().map(t -> t.getId()).collect(Collectors.toList());
        flowCandidatesService.delete(taskOperatorId);
        //删除经办记录
        delOperatorRecordIds.add(operatorRecord.getId());
        flowTaskOperatorRecordService.updateStatus(delOperatorRecordIds);
        //撤回记录
        FlowTaskOperatorEntity operator = JsonUtil.getJsonToBean(operatorRecord, FlowTaskOperatorEntity.class);
        operator.setId(operatorRecord.getTaskOperatorId());
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.revoke.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        flowModel.setFormData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        //节点事件
        flowMsgUtil.event(6, nodeModel, operatorRecord, flowModel);
        //超时
        insTimeOutRedis(flowModel, operatorList, userInfo, flowTask, flowTaskNodeEntityList);
    }

    @Override
    @DSTransactional
    public void revoke(FlowTaskEntity flowTask, FlowModel flowModel) throws WorkFlowException {
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        UserInfo userInfo = flowModel.getUserInfo();
        List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(flowTask.getId());
        FlowTaskNodeEntity start = list.stream().filter(t -> FlowNature.NodeStart.equals(String.valueOf(t.getNodeType()))).findFirst().orElse(null);
        //删除节点
        flowTaskNodeService.deleteByTaskId(flowTask.getId());
        //删除经办
        flowTaskOperatorService.deleteByTaskId(flowTask.getId());
        //删除候选人
        flowCandidatesService.deleteByTaskId(flowTask.getId());
        //删除发起用户信息
        flowUserService.deleteByTaskId(flowTask.getId());
        //更新当前节点
        flowTask.setThisStep("开始");
        flowTask.setCompletion(FlowNature.ProcessCompletion);
        flowTask.setStatus(FlowTaskStatusEnum.Revoke.getCode());
        flowTask.setStartTime(null);
        flowTask.setEndTime(null);
        flowTaskService.update(flowTask);
        //撤回记录
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        operatorRecord.setTaskId(flowTask.getId());
        operatorRecord.setHandleStatus(FlowRecordEnum.revoke.getCode());
        FlowTaskOperatorEntity operator = JsonUtil.getJsonToBean(operatorRecord, FlowTaskOperatorEntity.class);
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.revoke.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //撤回事件
        if (start != null) {
            ChildNodeList nodeModel = JsonUtil.getJsonToBean(start.getNodePropertyJson(), ChildNodeList.class);
            flowModel.setFormData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
            operatorRecord.setHandleStatus(FlowTaskStatusEnum.Revoke.getCode());
            flowMsgUtil.event(3, nodeModel, operatorRecord, flowModel);
        }
        List<String> childAllList = flowTaskService.getChildAllList(flowTask.getId());
        childAllList.remove(flowTask.getId());
        List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(childAllList);
        for (FlowTaskEntity flowTaskEntity : orderStaList) {
            FlowModel cancelModel = new FlowModel();
            cancelModel.setUserInfo(flowModel.getUserInfo());
            this.revoke(flowTaskEntity, flowModel);
        }
    }

    @Override
    @DSTransactional
    public void cancel(FlowTaskEntity flowTask, FlowModel flowModel) throws WorkFlowException {
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        UserInfo userInfo = flowModel.getUserInfo();
        //终止记录
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setTaskId(flowTask.getId());
        operator.setNodeCode(flowTask.getThisStepId());
        operator.setNodeName(flowTask.getThisStep());
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.cancel.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //更新实例
        flowTask.setStatus(FlowTaskStatusEnum.Cancel.getCode());
        flowTask.setEndTime(new Date());
        flowTaskService.update(flowTask);
        //发送消息
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        String nodeJson = "{}";
        FlowTaskNodeEntity start = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().orElse(null);
        if (start != null) {
            nodeJson = start.getNodePropertyJson();
        } //发送消息
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setEnd(true);
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setTaskEntity(flowTask);
        FlowTaskNodeEntity taskNodeEntity = new FlowTaskNodeEntity();
        taskNodeEntity.setNodePropertyJson(nodeJson);
        flowMsgModel.setTaskNodeEntity(taskNodeEntity);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setFlowModel(flowModel);
        flowMsgUtil.message(flowMsgModel);
        //递归查询子流程，并终止
        List<String> childAllList = flowTaskService.getChildAllList(flowTask.getId());
        childAllList.remove(flowTask.getId());
        List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(childAllList);
        for (FlowTaskEntity flowTaskEntity : orderStaList) {
            FlowModel cancelModel = new FlowModel();
            cancelModel.setUserInfo(flowModel.getUserInfo());
            this.cancel(flowTaskEntity, flowModel);
        }
    }

    @Override
    @DSTransactional
    public void assign(String id, FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity flowTask = flowTaskService.getInfo(id);
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        List<FlowTaskOperatorEntity> list = flowTaskOperatorService.getList(id).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState()) && flowModel.getNodeCode().equals(t.getNodeCode())).collect(Collectors.toList());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(id);
        FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> flowModel.getNodeCode().equals(t.getNodeCode())).findFirst().orElse(null);
        ChildNodeList childNode = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        boolean isRejct = list.stream().filter(t -> StringUtil.isNotEmpty(t.getReject())).count() > 0;
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        TaskOperatoUser taskOperatoUser = new TaskOperatoUser();
        taskOperatoUser.setDate(new Date());
        taskOperatoUser.setChildNode(childNode);
        taskOperatoUser.setAutomation("");
        taskOperatoUser.setId(FlowNature.ParentId);
        taskOperatoUser.setHandLeId(flowModel.getFreeApproverUserId());
        taskOperatoUser.setSortCode(1);
        taskOperatoUser.setRejectUser(isRejct);
        flowTaskUtil.operatorUser(operatorList, taskOperatoUser);
        List<String> idAll = list.stream().map(FlowTaskOperatorEntity::getId).collect(Collectors.toList());
        flowTaskOperatorService.deleteList(idAll);
        flowTaskOperatorService.create(operatorList);
        Set<String> taskNodeId = new HashSet() {{
            add(taskNode.getId());
        }};
        flowOperatorUserService.updateReject(id, taskNodeId);
        //指派记录
        UserInfo userInfo = flowModel.getUserInfo();
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setTaskId(taskNode.getTaskId());
        operator.setNodeCode(taskNode.getNodeCode());
        operator.setNodeName(taskNode.getNodeName());
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.assign.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).operatorId(flowModel.getFreeApproverUserId()).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //发送消息
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setOperatorList(operatorList);
        flowMsgModel.setTaskNodeEntity(taskNode);
        flowMsgModel.setTaskEntity(flowTask);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setFlowModel(flowModel);
        flowMsgUtil.message(flowMsgModel);
        //超时
        insTimeOutRedis(flowModel, operatorList, userInfo, flowTask, taskNodeList);
    }

    @Override
    @DSTransactional
    public void transfer(FlowTaskOperatorEntity taskOperator, FlowModel flowModel) throws WorkFlowException {
        flowTaskOperatorService.update(taskOperator);
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(taskOperator.getTaskId());
        FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(taskOperator.getTaskNodeId())).findFirst().orElse(null);
        ChildNodeList childNode = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        FlowTaskEntity flowTask = flowTaskService.getInfoSubmit(taskNode.getTaskId(), FlowTaskEntity::getId, FlowTaskEntity::getFlowId, FlowTaskEntity::getFullName, FlowTaskEntity::getCreatorUserId, FlowTaskEntity::getStatus, FlowTaskEntity::getFlowFormContentJson);
        //判断流程是否处于挂起状态
        flowTaskUtil.isSuspend(flowTask);
        //转办记录
        UserInfo userInfo = flowModel.getUserInfo();
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setTaskId(taskOperator.getTaskId());
        operator.setNodeCode(taskOperator.getNodeCode());
        operator.setTaskNodeId(taskOperator.getTaskNodeId());
        operator.setNodeName(taskOperator.getNodeName());
        //审批数据赋值
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(FlowRecordEnum.transfer.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).operatorId(taskOperator.getHandleId()).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //自动审批
        List<FlowTaskOperatorEntity> operatorListAll = new ArrayList<>();
        List<UserEntity> userName = new ArrayList() {{
            UserEntity user = new UserEntity();
            user.setId(taskOperator.getHandleId());
            add(user);
        }};
        TaskOperatoUser taskOperatoUser = new TaskOperatoUser();
        taskOperatoUser.setDate(new Date());
        taskOperatoUser.setId(FlowNature.ParentId);
        taskOperatoUser.setHandLeId(operator.getHandleId());
        taskOperatoUser.setChildNode(childNode);
        FlowAgreeRuleModel ruleModel = FlowAgreeRuleModel.builder().operatorListAll(operatorListAll).taskOperatoUser(taskOperatoUser).flowTask(flowTask).userName(userName).childNode(childNode).taskNodeList(taskNodeList).reject(false).build();
        flowTaskUtil.flowAgreeRule(ruleModel);
        operatorListAll.stream().forEach(t -> t.setId(taskOperator.getId()));
        //发送消息
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        List<FlowTaskOperatorEntity> operatorList = new ArrayList() {{
            FlowTaskOperatorEntity operatorEntity = new FlowTaskOperatorEntity();
            operatorEntity.setId(taskOperator.getId());
            operatorEntity.setTaskId(operatorRecord.getTaskId());
            operatorEntity.setHandleId(taskOperator.getHandleId());
            operatorEntity.setTaskNodeId(operatorRecord.getTaskNodeId());
            add(operatorEntity);
        }};
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setOperatorList(operatorList);
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setTaskNodeEntity(taskNode);
        flowMsgModel.setTaskEntity(flowTask);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setFlowModel(flowModel);
        flowMsgUtil.message(flowMsgModel);
        //自动审批
        FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(operatorListAll).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
        flowTaskUtil.approve(approveModel);
        //超时
        insTimeOutRedis(flowModel, operatorListAll, userInfo, flowTask, taskNodeList);
    }

    @Override
    public FlowBeforeInfoVO getBeforeInfo(FlowModel flowModel) throws WorkFlowException {
        FlowBeforeInfoVO vo = new FlowBeforeInfoVO();
        String taskNodeId = flowModel.getTaskNodeId();
        String operatorId = flowModel.getTaskOperatorId();
        String flowId = flowModel.getFlowId();
        FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(flowModel.getId());
        FlowTemplateAllModel templateAllModel = taskEntity != null ? flowTaskUtil.templateJson(taskEntity.getFlowId()) : flowTaskUtil.templateJson(flowId);
        FlowTemplateJsonEntity templateJson = templateAllModel.getTemplateJson();
        FlowTemplateEntity template = templateAllModel.getTemplate();
        ChildNodeList childNodeAll = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNodeList.class);
        String formId = childNodeAll.getProperties().getFormId();
        //判断是否有查看权限
        UserInfo userInfo = userProvider.get();
        if (taskEntity != null && taskEntity.getFlowType() == 0 && !userInfo.getIsAdministrator()) {
            String userId = userInfo.getUserId();
            //查询协管权限
            List<FlowEngineVisibleEntity> visibleFlowList = flowEngineVisibleService.getVisibleFlowList(userId, "2");
            List<String> tempId = visibleFlowList.stream().map(FlowEngineVisibleEntity::getFlowId).collect(Collectors.toList());
            tempId.addAll(flowTemplateJsonService.getMonitorList().stream().map(FlowTemplateJsonEntity::getTemplateId).collect(Collectors.toList()));
            //查询用户权限
            List<String> userIdList = new ArrayList<>();
            //审批
            List<String> operatorUserList = flowTaskOperatorService.getList(taskEntity.getId()).stream().map(FlowTaskOperatorEntity::getHandleId).collect(Collectors.toList());
            userIdList.addAll(operatorUserList);
            //抄送
            List<String> circulateUserList = flowTaskCirculateService.getList(taskEntity.getId()).stream().map(FlowTaskCirculateEntity::getObjectId).collect(Collectors.toList());
            userIdList.addAll(circulateUserList);
            //委托
            List<FlowDelegateEntity> flowDelegateList = flowDelegateService.getUser(userId);
            for (FlowDelegateEntity entity : flowDelegateList) {
                userIdList.add(entity.getUserId());
                userIdList.add(entity.getToUserId());
            }
            userIdList.add(taskEntity.getCreatorUserId());
            if (StringUtil.isNotEmpty(taskEntity.getDelegateUser())) {
                userIdList.add(taskEntity.getDelegateUser());
            }
            if (!tempId.contains(taskEntity.getTemplateId()) && !userIdList.contains(userId)) {
                throw new WorkFlowException("无权限查看");
            }
        }
        if (taskEntity != null) {
            boolean isRejectId = StringUtil.isEmpty(taskEntity.getRejectId());
            List<FlowTaskNodeEntity> taskNodeAllList = flowTaskNodeService.getList(taskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            //排除分支没有走过的节点
            flowTaskUtil.nodeList(taskNodeAllList);
            List<FlowTaskNodeEntity> taskNodeList = taskNodeAllList.stream().sorted(Comparator.comparing(FlowTaskNodeEntity::getSortCode)).collect(Collectors.toList());
            List<Integer> state = new ArrayList() {{
                add(FlowNodeEnum.Process.getCode());
                add(FlowNodeEnum.FreeApprover.getCode());
            }};
            List<FlowTaskOperatorEntity> taskOperatorList = flowTaskOperatorService.getList(taskEntity.getId()).stream().filter(t -> state.contains(t.getState()) && FlowNature.ParentId.equals(t.getParentId())).collect(Collectors.toList());
            Map<String, List<FlowTaskOperatorEntity>> nodeIdList = taskOperatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            List<FlowTaskOperatorRecordEntity> operatorRecordList = flowTaskOperatorRecordService.getList(taskEntity.getId());
            List<FlowOperatorUserEntity> operatorUserAll = flowOperatorUserService.getList(taskEntity.getId());
            for (int i = 0; i < operatorUserAll.size(); i++) {
                FlowOperatorUserEntity operatorUserEntity = operatorUserAll.get(i);
                boolean count = taskOperatorList.stream().filter(t -> t.getId().equals(operatorUserEntity.getId())).count() > 0;
                if (count) {
                    operatorUserAll.remove(i);
                }
            }
            Map<String, List<FlowOperatorUserEntity>> operatorNodeIdList = operatorUserAll.stream().collect(Collectors.groupingBy(FlowOperatorUserEntity::getTaskNodeId));
            boolean colorFlag = true;
            //流程任务
            FlowTaskModel flowTaskModel = JsonUtil.getJsonToBean(taskEntity, FlowTaskModel.class);
            flowTaskModel.setType(template.getType());
            flowTaskModel.setFlowId(template.getId());
            flowTaskModel.setRejectDataId(taskEntity.getRejectId());
            FlowTaskEntity parentTask = flowTaskService.getInfoSubmit(taskEntity.getParentId());
            flowTaskModel.setSuspend(parentTask != null ? FlowTaskStatusEnum.Suspend.getCode().equals(parentTask.getStatus()) : false);
            vo.setFlowTaskInfo(flowTaskModel);
            //已办人员
            List<FlowTaskOperatorRecordModel> recordList = new ArrayList<>();
            List<String> userIdAll = new ArrayList<>();
            operatorRecordList.stream().forEach(t -> {
                userIdAll.add(t.getHandleId());
                if (StringUtil.isNotEmpty(t.getOperatorId())) {
                    userIdAll.add(t.getOperatorId());
                }
            });
            userIdAll.addAll(taskOperatorList.stream().map(FlowTaskOperatorEntity::getHandleId).collect(Collectors.toList()));
            userIdAll.addAll(operatorUserAll.stream().map(FlowOperatorUserEntity::getHandleId).collect(Collectors.toList()));
            userIdAll.add(taskEntity.getCreatorUserId());
            //子流程人员
            List<FlowTaskEntity> childList = flowTaskService.getChildList(taskEntity.getId(), FlowTaskEntity::getId, FlowTaskEntity::getCreatorUserId);
            userIdAll.addAll(childList.stream().map(FlowTaskEntity::getCreatorUserId).collect(Collectors.toList()));
            List<UserEntity> userList = serviceUtil.getUserName(userIdAll);
            for (FlowTaskOperatorRecordEntity entity : operatorRecordList) {
                FlowTaskOperatorRecordModel infoModel = JsonUtil.getJsonToBean(entity, FlowTaskOperatorRecordModel.class);
                UserEntity operatorName = userList.stream().filter(t -> t.getId().equals(entity.getOperatorId())).findFirst().orElse(null);
                infoModel.setOperatorId(operatorName != null ? operatorName.getRealName() + "/" + operatorName.getAccount() : "");
                UserEntity userName = userList.stream().filter(t -> t.getId().equals(entity.getHandleId())).findFirst().orElse(null);
                infoModel.setUserName(userName != null ? userName.getRealName() + "/" + userName.getAccount() : "");
                FlowTaskOperatorEntity operatorEntity = taskOperatorList.stream().filter(t -> t.getId().equals(entity.getTaskOperatorId())).findFirst().orElse(null);
                int status = 0;
                if (operatorEntity != null) {
                    status += FlowNature.ParentId.equals(operatorEntity.getParentId()) ? 0 : 1;
                    infoModel.setCreatorTime(operatorEntity.getCreatorTime().getTime());
                } else {
                    infoModel.setCreatorTime(entity.getHandleTime().getTime());
                }
                infoModel.setStatus(status);
                recordList.add(infoModel);
            }
            vo.setFlowTaskOperatorRecordList(recordList);
            //流程节点
            String[] tepId = taskEntity.getThisStepId() != null ? taskEntity.getThisStepId().split(",") : new String[]{};
            List<String> tepIdAll = Arrays.asList(tepId);
            List<FlowTaskNodeModel> flowTaskNodeListAll = JsonUtil.getJsonToList(taskNodeList, FlowTaskNodeModel.class);
            for (FlowTaskNodeModel model : flowTaskNodeListAll) {
                List<String> nodeId = new ArrayList<>();
                if (tepIdAll.contains(model.getNodeCode())) {
                    model.setType("1");
                    colorFlag = false;
                    if (FlowNature.NodeEnd.equals(model.getNodeCode())) {
                        model.setType("0");
                    }
                }
                //流程图节点颜色
                if (colorFlag || model.getCompletion() == 1) {
                    if (model.getSortCode() != -2) {
                        model.setType("0");
                    }
                }
                if (StringUtil.isNotEmpty(model.getType())) {
                    nodeId.add(model.getType());
                }
                //查询审批人
                ChildNodeList childNode = JsonUtil.getJsonToBean(model.getNodePropertyJson(), ChildNodeList.class);
                Custom custom = childNode.getCustom();
                org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
                String type = properties.getAssigneeType();
                List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
                TaskOperator taskOperator = new TaskOperator();
                taskOperator.setChildNode(childNode);
                taskOperator.setTaskEntity(taskEntity);
                taskOperator.setFlowModel(flowModel);
                taskOperator.setDetails(false);
                taskOperator.setVerify(false);
                taskOperator.setTaskNodeList(taskNodeList);
                List<String> userName = new ArrayList<>();
                if (FlowNature.NodeStart.equals(custom.getType())) {
                    UserEntity startUser = userList.stream().filter(t -> t.getId().equals(taskEntity.getCreatorUserId())).findFirst().orElse(null);
                    userName.add(startUser != null ? startUser.getRealName() + "/" + startUser.getAccount() : "");
                } else if (FlowNature.NodeSubFlow.equals(custom.getType())) {
                    List<UserEntity> list = new ArrayList<>();
                    List<String> taskListAll = new ArrayList() {{
                        addAll(childNode.getCustom().getAsyncTaskList());
                        addAll(childNode.getCustom().getTaskId());
                    }};
                    List<String> taskUserList = childList.stream().filter(t -> taskListAll.contains(t.getId())).map(FlowTaskEntity::getCreatorUserId).collect(Collectors.toList());
                    List<UserEntity> subFlowUserList = userList.stream().filter(t -> taskUserList.contains(t.getId())).collect(Collectors.toList());
                    if (subFlowUserList.size() > 0) {
                        list.addAll(subFlowUserList);
                    } else {
                        TaskChild taskChild = new TaskChild();
                        taskChild.setChildNode(childNode);
                        taskChild.setFlowTask(taskEntity);
                        taskChild.setTemplateAllModel(templateAllModel);
                        taskChild.setFlowModel(flowModel);
                        taskChild.setFlowTaskNodeList(taskNodeAllList);
                        taskChild.setVerify(false);
                        list.addAll(flowTaskUtil.childSaveList(taskChild));
                    }
                    List<String> nameList = new ArrayList<>();
                    for (UserEntity entity : list) {
                        nameList.add(entity.getRealName() + "/" + entity.getAccount());
                    }
                    userName.addAll(nameList);
                } else if (!FlowNature.NodeEnd.equals(custom.getNodeId())) {
                    List<UserEntity> operatorUser = new ArrayList<>();
                    List<String> list = nodeIdList.get(model.getId()) != null ? nodeIdList.get(model.getId()).stream().map(FlowTaskOperatorEntity::getHandleId).collect(Collectors.toList()) : new ArrayList<>();
                    List<String> operUserAll = operatorNodeIdList.get(model.getId()) != null ? operatorNodeIdList.get(model.getId()).stream().map(FlowOperatorUserEntity::getHandleId).collect(Collectors.toList()) : new ArrayList<>();
                    Set<String> userAll = new HashSet() {{
                        addAll(list);
                        addAll(operUserAll);
                    }};
                    if (userAll.size() > 0) {
                        operatorUser.addAll(userList.stream().filter(t -> userAll.contains(t.getId())).collect(Collectors.toList()));
                    } else {
                        List<UserEntity> operator = flowTaskUtil.operator(operatorList, taskOperator);
                        operatorUser.addAll(operator);
                    }
                    boolean isShow = true;
                    //环节还没有经过和当前不显示审批人
                    if (isRejectId) {
                        if (FlowTaskOperatorEnum.Tache.getCode().equals(type) || FlowTaskOperatorEnum.Candidate.getCode().equals(type)) {
                            List<String> typeList = new ArrayList() {{
                                add("0");
                                add("1");
                            }};
                            boolean completion = typeList.contains(model.getType());
                            if (!completion) {
                                isShow = false;
                            }
                        }
                    }
                    if (isShow) {
                        List<String> nameList = new ArrayList<>();
                        for (UserEntity operator : operatorUser) {
                            nameList.add(operator.getRealName() + "/" + operator.getAccount());
                        }
                        userName.addAll(nameList);
                    }
                }
                model.setUserName(String.join(",", userName));
            }
            vo.setFlowTaskNodeList(flowTaskNodeListAll);
            //获取当前节点
            if (StringUtil.isNotEmpty(taskNodeId)) {
                FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(taskNodeId);
                if (taskNode != null) {
                    childNodeAll = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                    formId = taskNode.getFormId();
                }
            }
            //草稿数据、复活数据
            if (StringUtil.isNotEmpty(operatorId) || StringUtil.isNotEmpty(taskEntity.getTaskNodeId())) {
                FlowTaskOperatorEntity operator = taskOperatorList.stream().filter(t -> t.getId().equals(operatorId)).findFirst().orElse(null);
                boolean isOperator = operator != null;
                String draftTaskNodeId = StringUtil.isNotEmpty(taskEntity.getTaskNodeId()) ? taskEntity.getTaskNodeId() : isOperator ? operator.getTaskNodeId() : null;
                FlowTaskNodeEntity taskNode = StringUtil.isNotEmpty(draftTaskNodeId) ? flowTaskNodeService.getInfo(draftTaskNodeId) : null;
                List<String> versionList = new ArrayList() {{
                    add("version");
                    add("f_version");
                }};
                Map<String, Object> draftDataAll = new HashMap<>();
                if (taskNode != null) {
                    Map<String, Object> objectMap = flowTaskUtil.infoData(formId, taskEntity.getId());
                    //草稿数据
                    Map<String, Object> operatorData = isOperator && StringUtil.isNotEmpty(operator.getDraftData()) ? JsonUtil.stringToMap(operator.getDraftData()) : null;
                    //复活数据
                    Map<String, Object> taskNodeData = StringUtil.isNotEmpty(taskNode.getDraftData()) ? JsonUtil.stringToMap(taskNode.getDraftData()) : new HashMap<>();
                    Map<String, Object> draftData = StringUtil.isNotEmpty(taskEntity.getTaskNodeId()) ? taskNodeData : operatorData;
                    if (draftData != null) {
                        draftData.keySet().removeIf(key -> versionList.contains(key.toLowerCase()));
                        objectMap.putAll(draftData);
                        draftDataAll.putAll(objectMap);
                        vo.setDraftData(draftDataAll);
                    }
                }
            }
        }
        FlowFormEntity form = serviceUtil.getForm(formId);
        vo.setFlowFormInfo(JsonUtil.getJsonToBean(form, FlowFormVo.class));
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties approversProperties = childNodeAll.getProperties();
        vo.setApproversProperties(approversProperties);
        vo.setFormOperates(approversProperties.getFormOperates());
        FlowTemplateModel templateModel = JsonUtil.getJsonToBean(templateJson, FlowTemplateModel.class);
        templateModel.setType(template.getType());
        templateModel.setFullName(template.getFullName());
        vo.setFlowTemplateInfo(templateModel);
        vo.setFormData(serviceUtil.infoData(formId, flowModel.getId()));
        return vo;
    }

    @Override
    public List<FlowSummary> recordList(String id, String category, String type) {
        //审批汇总
        List<Integer> handleStatus = new ArrayList<>();
        if (!"0".equals(type)) {
            handleStatus.add(0);
            handleStatus.add(1);
        }
        List<FlowTaskOperatorRecordEntity> recordListAll = flowTaskOperatorRecordService.getRecordList(id, handleStatus);
        List<String> userIdAll = new ArrayList<>();
        List<String> userIdList = recordListAll.stream().map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
        List<String> operatorId = recordListAll.stream().filter(t -> StringUtil.isNotEmpty(t.getOperatorId())).map(FlowTaskOperatorRecordEntity::getOperatorId).collect(Collectors.toList());
        userIdAll.addAll(userIdList);
        userIdAll.addAll(operatorId);
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll);
        List<FlowSummary> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        Map<String, List<FlowTaskOperatorRecordEntity>> operatorAll = new HashMap<>();
        if (FlowRecordListEnum.position.getCode().equals(category)) {
            List<String> userId = userList.stream().map(UserEntity::getId).collect(Collectors.toList());
            List<UserRelationEntity> relationList = serviceUtil.getListByUserIdAll(userId);
            List<String> objectId = relationList.stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
            List<PositionEntity> positionListAll = serviceUtil.getPositionName(objectId);
            for (PositionEntity entity : positionListAll) {
                map.put(entity.getId(), entity.getFullName());
                List<String> userAll = relationList.stream().filter(t -> t.getObjectId().equals(entity.getId())).map(UserRelationEntity::getUserId).collect(Collectors.toList());
                List<FlowTaskOperatorRecordEntity> operator = new ArrayList<>();
                for (FlowTaskOperatorRecordEntity recordEntity : recordListAll) {
                    if (userAll.contains(recordEntity.getHandleId())) {
                        operator.add(recordEntity);
                    }
                }
                operatorAll.put(entity.getId(), operator);
            }
        } else if (FlowRecordListEnum.role.getCode().equals(category)) {
            List<String> userId = userList.stream().map(UserEntity::getId).collect(Collectors.toList());
            List<UserRelationEntity> relationList = serviceUtil.getListByUserIdAll(userId);
            List<String> objectId = relationList.stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList());
            List<RoleEntity> roleListAll = serviceUtil.getListByIds(objectId);
            for (RoleEntity entity : roleListAll) {
                map.put(entity.getId(), entity.getFullName());
                List<String> userAll = relationList.stream().filter(t -> t.getObjectId().equals(entity.getId())).map(UserRelationEntity::getUserId).collect(Collectors.toList());
                List<FlowTaskOperatorRecordEntity> operator = new ArrayList<>();
                for (FlowTaskOperatorRecordEntity recordEntity : recordListAll) {
                    if (userAll.contains(recordEntity.getHandleId())) {
                        operator.add(recordEntity);
                    }
                }
                operatorAll.put(entity.getId(), operator);
            }
        } else if (FlowRecordListEnum.department.getCode().equals(category)) {
            List<String> organizeList = userList.stream().map(UserEntity::getOrganizeId).collect(Collectors.toList());
            List<OrganizeEntity> organizeListAll = serviceUtil.getOrganizeName(organizeList);
            for (OrganizeEntity entity : organizeListAll) {
                map.put(entity.getId(), entity.getFullName());
                List<String> userAll = userList.stream().filter(t -> t.getOrganizeId().equals(entity.getId())).map(UserEntity::getId).collect(Collectors.toList());
                List<FlowTaskOperatorRecordEntity> operator = new ArrayList<>();
                for (FlowTaskOperatorRecordEntity recordEntity : recordListAll) {
                    if (userAll.contains(recordEntity.getHandleId())) {
                        operator.add(recordEntity);
                    }
                }
                operatorAll.put(entity.getId(), operator);
            }
        }
        for (String key : map.keySet()) {
            String fullName = map.get(key);
            FlowSummary summary = new FlowSummary();
            summary.setId(key);
            summary.setFullName(fullName);
            List<FlowTaskOperatorRecordEntity> recordList = operatorAll.get(key);
            List<FlowSummary> childList = new ArrayList<>();
            for (FlowTaskOperatorRecordEntity entity : recordList) {
                FlowSummary childSummary = JsonUtil.getJsonToBean(entity, FlowSummary.class);
                UserEntity user = userList.stream().filter(t -> t.getId().equals(entity.getHandleId())).findFirst().orElse(null);
                childSummary.setUserName(user != null ? user.getRealName() + "/" + user.getAccount() : "");
                UserEntity userEntity = userList.stream().filter(t -> t.getId().equals(entity.getOperatorId())).findFirst().orElse(null);
                childSummary.setOperatorId(userEntity != null ? userEntity.getRealName() + "/" + userEntity.getAccount() : "");
                childSummary.setHeadIcon(UploaderUtil.uploaderImg(user.getHeadIcon()));
                childList.add(childSummary);
            }
            summary.setList(childList);
            list.add(summary);
        }
        return list;
    }

    @Override
    public boolean press(String id, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTaskEntity.getFlowId());
        List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.press(id);
        boolean flag = operatorList.size() > 0;
        Map<String, Object> data = JsonUtil.stringToMap(flowTaskEntity.getFlowFormContentJson());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(id);
        //发送消息
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setOperatorList(operatorList);
        flowMsgModel.setTaskEntity(flowTaskEntity);
        flowMsgModel.setData(data);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setFlowModel(flowModel);
//        flowMsgUtil.message(flowMsgModel);
        //定时器
        WorkJobModel workJobModel = new WorkJobModel(id, flowMsgModel, userInfo);
        WorkJobUtil.insertRedis(workJobModel, redisUtil);
        return flag;
    }

    @Override
    public FlowCandidateVO candidates(String id, FlowModel flowModel, boolean batch) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getOperatorInfo(id);
        String flowTaskId = operator != null ? operator.getTaskId() : flowModel.getId();
        FlowTaskEntity flowTask = StringUtil.isNotEmpty(flowTaskId) ? flowTaskService.getInfoSubmit(flowTaskId,FlowTaskEntity::getRejectId) : null;
        List<ChildNodeList> childNodeListAll = flowTaskUtil.childNodeListAll(operator, flowModel);
        List<FlowCandidateListModel> listVO = new ArrayList<>();
        FlowCandidateVO vo = new FlowCandidateVO();
        boolean brachFlow = childNodeListAll.stream().filter(t -> t.getCustom().getBranchFlow()).count() > 0;
        int candidates = 0;
        for (ChildNodeList childNodeList : childNodeListAll) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            Custom custom = childNodeList.getCustom();
            String nodeId = custom.getNodeId();
            String nodeName = properties.getTitle();
            String assigneeType = properties.getAssigneeType();
            String initiateType = properties.getInitiateType();
            FlowCandidateListModel candidateVO = new FlowCandidateListModel();
            candidateVO.setNodeName(nodeName);
            candidateVO.setNodeId(nodeId);
            boolean isCandidates = (FlowTaskOperatorEnum.Candidate.getCode().equals(initiateType) || FlowTaskOperatorEnum.Candidate.getCode().equals(assigneeType));
            candidateVO.setIsCandidates(isCandidates);
            boolean isBranchFlow = custom.getBranchFlow();
            candidateVO.setIsBranchFlow(isBranchFlow);
            if (isCandidates) {
                List<String> list = new ArrayList<>();
                list.addAll(properties.getApproverPos());
                list.addAll(properties.getApproverRole());
                list.addAll(properties.getApproverOrg());
                list.addAll(properties.getApproverGroup());
                list.addAll(properties.getInitiatePos());
                list.addAll(properties.getInitiateRole());
                list.addAll(properties.getInitiateOrg());
                list.addAll(properties.getInitiateGroup());
                List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(list);
                List<String> userId = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
                userId.addAll(properties.getApprovers());
                userId.addAll(properties.getInitiator());
                Pagination pagination = JsonUtil.getJsonToBean(flowModel, Pagination.class);
                List<UserEntity> userName = serviceUtil.getUserName(userId, pagination);
                candidateVO.setHasCandidates(userName.size() > 0);
            }
            candidates += isCandidates ? 1 : 0;
            listVO.add(candidateVO);
        }
        if (batch && brachFlow) {
            throw new WorkFlowException(MsgCode.WF124.get());
        }
        int flowType = brachFlow ? 1 : candidates > 0 ? 2 : 3;
        if (operator != null) {
            if (brachFlow) {
                List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getList(operator.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(operator.getTaskNodeId()) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
                operatorList.stream().forEach(t -> {
                    if (t.getId().equals(id)) {
                        t.setCompletion(FlowNature.AuditCompletion);
                    }
                    if (StringUtil.isEmpty(t.getRollbackId()) && !FlowNature.ParentId.equals(t.getParentId())) {
                        t.setCompletion(FlowNature.AuditCompletion);
                    }
                });
                FlowCountersignModel countersign = new FlowCountersignModel();
                countersign.setTaskNodeId(operator.getTaskNodeId());
                countersign.setOperatorList(operatorList);
                boolean isCountersign = flowTaskUtil.isCountersign(countersign);
                flowType = isCountersign ? flowType : brachFlow ? 3 : candidates > 0 ? 2 : 3;
            }
            if (!FlowNature.ParentId.equals(operator.getParentId())) {
                flowType = 3;
            }
        }
        if (flowTask != null && StringUtil.isNotEmpty(flowTask.getRejectId())) {
            flowType = 3;
        }
        vo.setType(flowType);
        vo.setList(listVO);
        return vo;
    }

    @Override
    public List<FlowCandidateUserModel> candidateUser(String id, FlowModel flowModel) throws WorkFlowException {
        List<FlowCandidateUserModel> dataList = new ArrayList<>();
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getOperatorInfo(id);
        List<ChildNodeList> childNodeListAll = flowTaskUtil.childNodeListAll(operator, flowModel);
        for (ChildNodeList childNodeList : childNodeListAll) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            List<String> list = new ArrayList<>();
            list.addAll(properties.getApproverPos());
            list.addAll(properties.getApproverRole());
            list.addAll(properties.getApproverOrg());
            list.addAll(properties.getApproverGroup());
            list.addAll(properties.getInitiatePos());
            list.addAll(properties.getInitiateRole());
            list.addAll(properties.getInitiatePos());
            list.addAll(properties.getInitiateOrg());
            list.addAll(properties.getInitiateGroup());
            List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(list);
            List<String> userId = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
            userId.addAll(properties.getApprovers());
            userId.addAll(properties.getInitiator());
            Pagination pagination = JsonUtil.getJsonToBean(flowModel, Pagination.class);
            List<UserEntity> userName = serviceUtil.getUserName(userId, pagination);
            flowModel.setTotal(pagination.getTotal());
            List<String> userIdAll = userName.stream().map(UserEntity::getId).collect(Collectors.toList());
            Map<String, List<UserRelationEntity>> userMap = serviceUtil.getListByUserIdAll(userIdAll).stream().filter(t -> PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.groupingBy(UserRelationEntity::getUserId));
            for (UserEntity entity : userName) {
                List<UserRelationEntity> listByUserId = userMap.get(entity.getId()) != null ? userMap.get(entity.getId()) : new ArrayList<>();
                StringJoiner joiner = new StringJoiner(",");
                for (UserRelationEntity relation : listByUserId) {
                    List<OrganizeEntity> organizeId = serviceUtil.getOrganizeId(relation.getObjectId());
                    if (organizeId.size() > 0) {
                        String organizeName = organizeId.stream().map(OrganizeEntity::getFullName).collect(Collectors.joining("/"));
                        joiner.add(organizeName);
                    }
                }
                FlowCandidateUserModel userModel = JsonUtil.getJsonToBean(entity, FlowCandidateUserModel.class);
                userModel.setFullName(entity.getRealName() + "/" + entity.getAccount());
                userModel.setHeadIcon(UploaderUtil.uploaderImg(entity.getHeadIcon()));
                userModel.setOrganize(joiner.toString());
                dataList.add(userModel);
            }
        }
        return dataList;
    }

    @Override
    @DSTransactional
    public void batch(FlowModel flowModel) throws WorkFlowException {
        List<String> idList = flowModel.getIds() != null ? flowModel.getIds() : new ArrayList<>();
        Integer batchType = flowModel.getBatchType();
        UserInfo userInfo = flowModel.getUserInfo();
        for (String id : idList) {
            String rejecttKey = userInfo.getTenantId() + id;
            if (redisUtil.exists(rejecttKey)) {
                throw new WorkFlowException(MsgCode.WF005.get());
            }
            redisUtil.insert(rejecttKey, id, 10);
            FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
            FlowTaskEntity task = flowTaskService.getInfo(operator.getTaskId());
            switch (batchType) {
                case 0:
                    flowModel.setVoluntarily(true);
                    this.auditAll(task, operator, flowModel);
                    break;
                case 1:
                    this.rejectAll(task, operator, flowModel);
                    break;
                case 2:
                    operator.setHandleId(flowModel.getFreeApproverUserId());
                    operator.setCreatorTime(new Date());
                    this.transfer(operator, flowModel);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public FlowCandidateVO batchCandidates(String flowId, String taskOperatorId, FlowModel flowModel) throws WorkFlowException {
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowId);
        FlowTemplateJsonEntity templateJson = templateAllModel.getTemplateJson();
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(taskOperatorId);
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(operator.getTaskNodeId());
        ChildNode childNodeAll = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
        //获取流程节点
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        //递归获取条件数据和节点数据
        FlowJsonUtil.getTemplateAll(childNodeAll, nodeListAll, conditionListAll);
        //判断节点是否有在条件中
        boolean isCondition = conditionListAll.stream().filter(t -> operator.getNodeCode().equals(t.getPrevId())).count() > 0;
        List<ChildNodeList> freeApprover = new ArrayList<>();
        List<ChildNodeList> branchFlow = new ArrayList<>();
        if (isCondition) {
            List<String> nodeNext = StringUtil.isNotEmpty(taskNode.getNodeNext()) ? Arrays.asList(taskNode.getNodeNext().split(",")) : new ArrayList<>();
            List<ChildNodeList> nextList = nodeListAll.stream().filter(t -> nodeNext.contains(t.getCustom().getNodeId())).collect(Collectors.toList());
            nextList.stream().forEach(t -> {
                if (FlowTaskOperatorEnum.Candidate.getCode().equals(t.getProperties().getAssigneeType())) {
                    freeApprover.add(t);
                }
                if (t.getCustom().getBranchFlow()) {
                    branchFlow.add(t);
                }
            });
        }
        if (freeApprover.size() > 0) {
            throw new WorkFlowException(MsgCode.WF128.get());
        }
        if (branchFlow.size() > 0) {
            throw new WorkFlowException(MsgCode.WF124.get());
        }
        return candidates(taskOperatorId, flowModel, true);
    }

    @Override
    public void permissions(String userId, FlowTaskEntity flowTask, FlowTaskOperatorEntity operator, String msg, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        if (operator == null || FlowNodeEnum.Futility.getCode().equals(operator.getState())) {
            throw new WorkFlowException(StringUtil.isEmpty(msg) ? MsgCode.WF122.get() : msg);
        }
        List<String> flowDelegateList = flowDelegateService.getUser(userId, flowTask.getTemplateId(), userInfo.getUserId()).stream().map(FlowDelegateEntity::getToUserId).collect(Collectors.toList());
//        FlowTaskOperatorRecordEntity  recordEntity = flowTaskOperatorRecordService.getIsCheck(operator.getId(),0);
        flowDelegateList.add(userId);
        if (!flowDelegateList.contains(userInfo.getUserId())) {
            throw new WorkFlowException(MsgCode.WF122.get());
        }
//        if(recordEntity != null){
//            if (!flowDelegateList.contains(userInfo.getUserId())) {
//                throw new WorkFlowException(MsgCode.WF122.get());
//            }
//        }
        if (FlowTaskStatusEnum.Cancel.getCode().equals(flowTask.getStatus())) {
            throw new WorkFlowException(MsgCode.WF121.get());
        }
        if (FlowTaskStatusEnum.Revoke.getCode().equals(flowTask.getStatus())) {
            throw new WorkFlowException(MsgCode.WF120.get());
        }
    }

    @Override
    @DSTransactional
    public void change(FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        FlowTaskEntity flowTask = flowTaskService.getInfo(flowModel.getTaskId());
        List<String> thisStepId = Arrays.asList(flowTask.getThisStepId().split(","));
        FlowTaskNodeEntity taskNodeEntity = flowTaskNodeService.getInfo(flowModel.getTaskNodeId());
        FlowTemplateAllModel templateAllModel = flowTaskUtil.templateJson(flowTask.getFlowId());
        boolean resurgence = flowModel.getResurgence();
        if (resurgence) {
            flowTask.setTaskNodeId(flowModel.getTaskNodeId());
            flowTask.setParentId(FlowNature.ParentId);
            flowTask.setStatus(FlowNature.NodeStart.equals(taskNodeEntity.getNodeType()) ? FlowTaskStatusEnum.Draft.getCode() : FlowTaskStatusEnum.Handle.getCode());
            if (StringUtil.isEmpty(taskNodeEntity.getDraftData())) {
                throw new WorkFlowException("该节点没有数据,无法复活");
            }
        }
        List<Integer> handleStatus = new ArrayList() {{
            add(FlowRecordEnum.audit.getCode());
            add(FlowRecordEnum.submit.getCode());
        }};
        FlowTaskOperatorRecordEntity record = flowTaskOperatorRecordService.getList(flowTask.getId()).stream().filter(t -> handleStatus.contains(t.getHandleStatus())).sorted(Comparator.comparing(FlowTaskOperatorRecordEntity::getHandleTime).reversed()).findFirst().orElse(null);
        //获取节点
        List<FlowTaskNodeEntity> taskNodeListAll = flowTaskNodeService.getList(flowModel.getTaskId());
        List<FlowTaskNodeEntity> taskNodeList = taskNodeListAll.stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        List<FlowTaskNodeEntity> nodeListAll = taskNodeList.stream().filter(t -> t.getNodeCode().equals(record.getNodeCode())).collect(Collectors.toList());
        for (FlowTaskNodeEntity nodeEntity : nodeListAll) {
            String formId = nodeEntity.getFormId();
            //验证变更是否同表单
            if (!resurgence && !taskNodeEntity.getFormId().equals(formId)) {
                throw new WorkFlowException("此流程不支持变更");
            }
        }
        //当前节点是子流程不能变更
        List<FlowTaskNodeEntity> thisTaskNodeList = taskNodeList.stream().filter(t -> thisStepId.contains(t.getNodeCode())).collect(Collectors.toList());
        boolean isChild = thisTaskNodeList.stream().filter(t -> FlowNature.NodeSubFlow.equals(t.getNodeType())).count() > 0;
        if (isChild) {
            throw new WorkFlowException("当前节点有子流程无法变更");
        }
        ChildNodeList childNode = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
        flowTaskNodeService.update(taskNodeEntity);
        String nodeId = childNode.getCustom().getNodeId();
        String title = childNode.getProperties().getTitle();
        flowTask.setThisStepId(taskNodeEntity.getNodeCode());
        flowTask.setThisStep(taskNodeEntity.getNodeName());
        flowTask.setRejectId(null);
        Integer progress = childNode.getProperties().getProgress() != null ? Integer.valueOf(childNode.getProperties().getProgress()) : null;
        flowTask.setCompletion(progress);
        flowTaskService.update(flowTask);
        //获取节点
        List<String> id = taskNodeList.stream().map(t -> t.getId()).collect(Collectors.toList());
        flowCandidatesService.deleteTaskNodeId(id);
        flowTaskNodeService.updateCompletion(id, FlowNature.AuditCompletion);
        List<FlowTaskNodeEntity> nextTaskNodeList = taskNodeList.stream().filter(t -> thisStepId.contains(t.getNodeCode())).collect(Collectors.toList());
        for (FlowTaskNodeEntity nodeEntity : nextTaskNodeList) {
            flowTaskUtil.change(taskNodeList, nodeEntity, false, FlowNature.AuditCompletion, flowModel);
            if (FlowNature.NodeSubFlow.equals(nodeEntity.getNodeType())) {
                ChildNodeList nodeModel = JsonUtil.getJsonToBean(nodeEntity.getNodePropertyJson(), ChildNodeList.class);
                List<String> idAll = nodeModel.getCustom().getTaskId();
                flowTaskService.deleteChildAll(idAll);
            }
        }
        if (taskNodeEntity != null) {
            flowTaskUtil.change(taskNodeList, taskNodeEntity, resurgence, FlowNature.ProcessCompletion, flowModel);
            taskNodeEntity.setCompletion(FlowNature.ProcessCompletion);
            flowTaskNodeService.update(taskNodeEntity);
        }
        Set<String> rejectNodeList = taskNodeList.stream().map(t -> t.getId()).collect(Collectors.toSet());
        flowTaskOperatorService.updateReject(flowModel.getTaskId(), rejectNodeList);
        flowOperatorUserService.updateReject(flowModel.getTaskId(), rejectNodeList);
        Set<String> recordNodeList = new HashSet<>();
        List<String> delTaskNode = new ArrayList() {{
            add(flowModel.getTaskNodeId());
        }};
        flowTaskUtil.upAll(recordNodeList, delTaskNode, taskNodeList);
        flowTaskOperatorRecordService.updateStatus(recordNodeList, flowTask.getId());
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        List<ChildNodeList> nextOperatorList = new ArrayList<>();
        nextOperatorList.add(childNode);
        flowModel.setFormData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowModel.setHandleOpinion(flowModel.getHandleOpinion());
        //查询审批人
        TaskOperator taskOperator = new TaskOperator();
        taskOperator.setChildNode(childNode);
        taskOperator.setTaskNodeList(taskNodeList);
        taskOperator.setTaskEntity(flowTask);
        taskOperator.setFlowModel(flowModel);
        //插入新的候选人
        List<String> userIdAll = flowTaskUtil.userListAll(taskOperator);
        Map<String, List<String>> candidateErrorList = flowModel.getErrorRuleUserList() != null ? flowModel.getErrorRuleUserList() : new HashMap<>();
        for (String key : candidateErrorList.keySet()) {
            userIdAll.addAll(candidateErrorList.get(key));
        }
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
        if (userList.size() == 0) {
            List<FlowErrorModel> errorList = new ArrayList() {{
                FlowErrorModel errorModel = new FlowErrorModel();
                errorModel.setNodeId(nodeId);
                errorModel.setNodeName(title);
                add(errorModel);
            }};
            throw new WorkFlowException(200, JsonUtil.getObjectToString(errorList));
        }
        for (int i = 0; i < userList.size(); i++) {
            UserEntity userEntity = userList.get(i);
            TaskOperatoUser taskOperatoUser = new TaskOperatoUser();
            taskOperatoUser.setDate(new Date());
            taskOperatoUser.setChildNode(childNode);
            taskOperatoUser.setAutomation("");
            taskOperatoUser.setId(FlowNature.ParentId);
            taskOperatoUser.setHandLeId(userEntity.getId());
            taskOperatoUser.setSortCode(i + 1);
            flowTaskUtil.operatorUser(operatorList, taskOperatoUser);
        }
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        for (ChildNodeList childNodeList : nextOperatorList) {
            Properties properties = childNodeList.getProperties();
            boolean counterSign = FlowNature.ImproperApprover.equals(properties.getCounterSign());
            if (counterSign && StringUtil.isEmpty(flowModel.getFreeApproverUserId())) {
                List<FlowTaskOperatorEntity> listAll = operatorMap.get(childNodeList.getTaskNodeId()) != null ? operatorMap.get(childNodeList.getTaskNodeId()) : new ArrayList<>();
                flowOperatorUserService.create(JsonUtil.getJsonToList(listAll, FlowOperatorUserEntity.class));
            }
        }
        //过滤依次审批人
        flowTaskUtil.improperApproverUser(operatorList, taskNodeList, childNode, null);
        flowTaskOperatorService.create(operatorList);
        //修改节点的选择分支数据
        recordNodeList.removeAll(delTaskNode);
        flowTaskNodeService.updateTaskNodeCandidates(new ArrayList<>(recordNodeList));
        //自动审批
        FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(operatorList).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
        flowTaskUtil.approve(approveModel);
        //审批数据赋值
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setTaskId(flowTask.getId());
        operator.setHandleId(userInfo.getUserId());
        operator.setNodeCode(taskNodeEntity.getNodeCode());
        operator.setNodeName(taskNodeEntity.getNodeName());
        operator.setTaskNodeId(taskNodeEntity.getId());
        FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(resurgence ? FlowRecordEnum.resurrection.getCode() : FlowRecordEnum.change.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).operatorId(operator.getHandleId()).build();
        flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
        flowTaskOperatorRecordService.create(operatorRecord);
        //发送消息
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setOperatorList(operatorList);
        flowMsgModel.setData(flowModel.getFormData());
        flowMsgModel.setTaskNodeEntity(null);
        flowMsgModel.setTaskEntity(flowTask);
        flowMsgModel.setFlowModel(flowModel);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
//        flowMsgUtil.message(flowMsgModel);
        //定时器
        WorkJobModel workJobModel = new WorkJobModel(flowModel.getTaskNodeId(), flowMsgModel, userInfo);
        WorkJobUtil.insertRedis(workJobModel, redisUtil);
        //超时
        insTimeOutRedis(flowModel, operatorList, userInfo, flowTask, taskNodeList);
    }

    @Override
    public FlowRejectVO rejectList(String id, boolean batch) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getList(operator.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(operator.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        FlowTaskNodeEntity nodeEntity = flowTaskNodeService.getInfo(operator.getTaskNodeId());
        ChildNodeList childNodeList = JsonUtil.getJsonToBean(nodeEntity.getNodePropertyJson(), ChildNodeList.class);
        String rejectStep = childNodeList.getProperties().getRejectStep();
        List<FlowTaskNodeEntity> rejectNode = list.stream().filter(t -> t.getNodeCode().equals(rejectStep)).collect(Collectors.toList());
        boolean up = FlowNature.UP.equals(rejectStep);
        boolean reject = FlowNature.Reject.equals(rejectStep);
        if (rejectNode.size() == 0) {
            List<FlowTaskNodeEntity> taskNodeEntity = list.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).collect(Collectors.toList());
            if (up) {
                List<FlowTaskNodeEntity> collect = list.stream().filter(t -> t.getNodeNext().contains(nodeEntity.getNodeCode())).collect(Collectors.toList());
                rejectNode.addAll(collect);
            } else if (reject) {
                Set<FlowTaskNodeEntity> rejectNodeList = new HashSet<>();
                List<String> nodetype = new ArrayList() {{
                    add(FlowNature.NodeSubFlow);
                }};
                flowTaskUtil.nodeList(list);
                List<String> nodeIdList = new ArrayList() {{
                    add(operator.getTaskNodeId());
                }};
                flowTaskUtil.upNodeList(list, nodeIdList, rejectNodeList, null);
                rejectNode.addAll(rejectNodeList.stream().filter(t -> !nodetype.contains(t.getNodeType())).collect(Collectors.toSet()));
            } else {
                rejectNode.addAll(taskNodeEntity);
            }
        }
        Set<FlowTaskNodeEntity> rejectNodeAll = new HashSet<>();
        for (int i = 0; i < rejectNode.size(); i++) {
            FlowTaskNodeEntity taskNodeEntity = rejectNode.get(i);
            boolean add = operatorList.stream().filter(t -> t.getTaskNodeId().equals(taskNodeEntity.getId())).count() > 0;
            if (add || !reject) {
                rejectNodeAll.add(taskNodeEntity);
            }
        }
        if (reject) {
            List<FlowTaskNodeEntity> startNode = list.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).collect(Collectors.toList());
            rejectNodeAll.addAll(startNode);
        }
        Set<FlowTaskNodeEntity> rejectNodeList = new HashSet<>();
        if (up) {
            FlowTaskNodeEntity upNode = new FlowTaskNodeEntity();
            List<String> node = rejectNode.stream().map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList());
            upNode.setNodeCode(String.join(",", node));
            upNode.setNodeName("上一审批");
            rejectNodeList.add(upNode);
        } else {
            rejectNodeList.addAll(rejectNodeAll);
        }
        List<TaskNodeModel> nodeList = JsonUtil.getJsonToList(rejectNodeList, TaskNodeModel.class);
        List<FlowTaskOperatorEntity> rejectOperatorList = operatorList.stream().filter(t -> t.getTaskNodeId().equals(operator.getTaskNodeId())).collect(Collectors.toList());
        rejectOperatorList.stream().forEach(t -> {
            if (t.getId().equals(id)) {
                t.setCompletion(FlowNature.RejectCompletion);
            }
        });
        FlowCountersignModel countersign = new FlowCountersignModel();
        countersign.setTaskNodeId(operator.getTaskNodeId());
        countersign.setOperatorList(rejectOperatorList);
        boolean isCountersign = flowTaskUtil.isRejectCountersign(countersign);
        FlowRejectVO vo = new FlowRejectVO();
        vo.setList(nodeList);
        boolean appro = reject ? isCountersign ? true : false : true;
        vo.setIsLastAppro(appro);
        return vo;
    }

    @Override
    @DSTransactional
    public void suspend(String id, FlowModel flowModel, boolean isSuspend) {
        UserInfo userInfo = flowModel.getUserInfo();
        List<String> idList = new ArrayList() {{
            add(id);
        }};
        flowTaskService.getChildList(id, flowModel.getSuspend(), idList);
        List<FlowTaskOperatorEntity> operatorList = flowTaskUtil.suspend(idList, isSuspend);
        List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(idList);
        for (FlowTaskEntity flowTask : orderStaList) {
            //恢复、挂起记录
            FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
            FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
            operator.setTaskId(flowTask.getId());
            operator.setNodeCode(flowTask.getThisStepId());
            operator.setNodeName(flowTask.getThisStep());
            //审批数据赋值
            FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(isSuspend ? FlowRecordEnum.suspend.getCode() : FlowRecordEnum.restore.getCode()).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).build();
            flowTaskUtil.operatorRecord(operatorRecord, flowOperatordModel);
            flowTaskOperatorRecordService.create(operatorRecord);
        }
        //启动、停止定时器
        workTimeoutJobUtil.suspendFuture(operatorList, isSuspend);
    }

    private void insTimeOutRedis(FlowModel flowModel, Collection<FlowTaskOperatorEntity> operatorList, UserInfo userInfo, FlowTaskEntity flowTask, List<FlowTaskNodeEntity> nodeList) {
        for (FlowTaskOperatorEntity operatorOne : operatorList) {
            FlowTaskNodeEntity taskNodeEntity = nodeList.stream().filter(t -> t.getId().equals(operatorOne.getTaskNodeId())).findFirst().orElse(null);
            boolean flag = ifInsRedis(taskNodeEntity, nodeList);
            if (flag) {
                WorkTimeoutJobModel workTimeoutJobModel = WorkTimeoutJobModel.builder().flowModel(flowModel)
                        .taskId(flowTask.getId()).taskNodeId(operatorOne.getTaskNodeId()).taskNodeOperatorId(operatorOne.getId()).operatorEntity(operatorOne)
                        .tenantId(userInfo.getTenantId()).tenantDbConnectionString(userInfo.getTenantDbConnectionString()).isAssignDataSource(userInfo.isAssignDataSource()).build();
                workTimeoutJobUtil.insertRedis(workTimeoutJobModel, redisUtil);
            }
        }
    }

    /**
     * 封装超时消息
     *
     * @return
     */
    public boolean ifInsRedis(FlowTaskNodeEntity taskNodeEntity, List<FlowTaskNodeEntity> nodeList) {
        ChildNodeList childNode = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
        LimitModel timeLimitConfig = childNode.getProperties().getTimeLimitConfig();
        FlowTaskNodeEntity startNode = nodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().orElse(null);
        if (timeLimitConfig.getOn() == 0) {
            return false;
        } else if (timeLimitConfig.getOn() == 2) {
            ChildNodeList childNodeStart = JsonUtil.getJsonToBean(startNode.getNodePropertyJson(), ChildNodeList.class);
            if (childNodeStart.getProperties().getTimeLimitConfig().getOn() == 0) {
                return false;
            }
        }
        return true;
    }

}
