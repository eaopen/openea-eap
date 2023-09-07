package org.openea.eap.extj.engine.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.model.flowbefore.FlowBatchModel;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.openea.eap.extj.engine.model.flowtask.PaginationFlowTask;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowTemplateJsonPage;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.FlowTaskStatusEnum;
import org.openea.eap.extj.engine.mapper.FlowTaskMapper;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.engine.util.FlowNature;
import org.openea.eap.extj.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程任务
 *
 * 
 */
@Slf4j
@Service
public class FlowTaskServiceImpl extends SuperServiceImpl<FlowTaskMapper, FlowTaskEntity> implements FlowTaskService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FlowDelegateService flowDelegateService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowEngineVisibleService flowEngineVisibleService;
    @Autowired
    private FlowOperatorUserService flowOperatorUserService;
    @Autowired
    private FlowRejectDataService flowRejectDataService;
    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    private FlowTaskCirculateService flowTaskCirculateService;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Override
    public List<FlowTaskEntity> getMonitorList(PaginationFlowTask paginationFlowTask) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        UserInfo userInfo = userProvider.get();
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(FlowTaskEntity::getId);
        queryWrapper.lambda().gt(FlowTaskEntity::getStatus, FlowTaskStatusEnum.Draft.getCode());
        queryWrapper.lambda().and(
                t -> t.isNull(FlowTaskEntity::getSuspend)
                        .or().ne(FlowTaskEntity::getSuspend, FlowTaskStatusEnum.Draft.getCode())
        );
        if (!userInfo.getIsAdministrator()) {
            List<FlowEngineVisibleEntity> visibleFlowList = flowEngineVisibleService.getVisibleFlowList(userInfo.getUserId(), "2");
            List<String> tempId = visibleFlowList.stream().map(FlowEngineVisibleEntity::getFlowId).collect(Collectors.toList());
            tempId.addAll(flowTemplateJsonService.getMonitorList().stream().map(FlowTemplateJsonEntity::getTemplateId).collect(Collectors.toList()));
            if (tempId.size() == 0) {
                return new ArrayList<>();
            }
            queryWrapper.lambda().in(FlowTaskEntity::getTemplateId, tempId);
        }
        //关键字（流程名称、流程编码）
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(FlowTaskEntity::getEnCode, keyWord)
                            .or().like(FlowTaskEntity::getFullName, keyWord)
            );
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            flag = true;
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().between(FlowTaskEntity::getCreatorTime, startTimes, endTimes);
        }
        //流程状态
        Integer status = paginationFlowTask.getStatus() != null ? paginationFlowTask.getStatus() : null;
        if (ObjectUtil.isNotEmpty(status)) {
            flag = true;
            queryWrapper.lambda().eq(FlowTaskEntity::getStatus, status);
        }
        //流程模板
        String templateId = paginationFlowTask.getTemplateId() != null ? paginationFlowTask.getTemplateId() : null;
        if (ObjectUtil.isNotEmpty(templateId)) {
            flag = true;
            queryWrapper.lambda().eq(FlowTaskEntity::getTemplateId, templateId);
        }
        //所属流程
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (ObjectUtil.isNotEmpty(flowId)) {
            flag = true;
            FlowTemplateJsonPage page = new FlowTemplateJsonPage();
            page.setTemplateId(templateId);
            page.setFlowId(flowId);
            List<String> flowList = flowTemplateJsonService.getListPage(page, false).stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
            if (flowList.size() == 0) {
                return new ArrayList<>();
            }
            queryWrapper.lambda().in(FlowTaskEntity::getFlowId, flowList);
        }
        //所属分类
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (ObjectUtil.isNotEmpty(flowCategory)) {
            flag = true;
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowCategory, flowCategory);
        }
        //发起人员
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        if (ObjectUtil.isNotEmpty(creatorUserId)) {
            flag = true;
            queryWrapper.lambda().eq(FlowTaskEntity::getCreatorUserId, creatorUserId);
        }
        //紧急程度
        Integer flowUrgent = paginationFlowTask.getFlowUrgent() != null ? paginationFlowTask.getFlowUrgent() : null;
        if (ObjectUtil.isNotEmpty(flowUrgent)) {
            flag = true;
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowUrgent, flowUrgent);
        }
        //排序
//        if ("desc".equals(paginationFlowTask.getSort().toLowerCase())) {
//            queryWrapper.lambda().orderByDesc(FlowTaskEntity::getCreatorTime);
//        } else {
        queryWrapper.lambda().orderByAsc(FlowTaskEntity::getSortCode).orderByDesc(FlowTaskEntity::getCreatorTime);
//        }
        if (flag) {
            queryWrapper.lambda().orderByDesc(FlowTaskEntity::getLastModifyTime);
        }
        Page<FlowTaskEntity> page = new Page<>(paginationFlowTask.getCurrentPage(), paginationFlowTask.getPageSize());
        IPage<FlowTaskEntity> flowTaskEntityPage = this.page(page, queryWrapper);
        if (!flowTaskEntityPage.getRecords().isEmpty()) {
            List<String> ids = flowTaskEntityPage.getRecords().stream().map(FlowTaskEntity::getId).collect(Collectors.toList());
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTaskEntity::getId, ids);
            //排序
            queryWrapper.lambda().orderByAsc(FlowTaskEntity::getSortCode).orderByDesc(FlowTaskEntity::getCreatorTime);
            if (flag) {
                queryWrapper.lambda().orderByDesc(FlowTaskEntity::getLastModifyTime);
            }
            flowTaskEntityPage.setRecords(this.list(queryWrapper));
        }
        return paginationFlowTask.setData(flowTaskEntityPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTaskEntity> getLaunchList(PaginationFlowTask paginationFlowTask) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        String userId = userProvider.get().getUserId();
        if (paginationFlowTask.getDelegateType()) {
            queryWrapper.lambda().select(FlowTaskEntity::getId).eq(FlowTaskEntity::getDelegateUser, userId);
        } else {
            queryWrapper.lambda().select(FlowTaskEntity::getId).eq(FlowTaskEntity::getCreatorUserId, userId);
        }
        //关键字（流程名称、流程编码）
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            queryWrapper.lambda().and(
                    t -> t.like(FlowTaskEntity::getEnCode, keyWord)
                            .or().like(FlowTaskEntity::getFullName, keyWord)
            );
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().between(FlowTaskEntity::getCreatorTime, startTimes, endTimes);
        }
        //流程模板
        String templateId = paginationFlowTask.getTemplateId() != null ? paginationFlowTask.getTemplateId() : null;
        if (ObjectUtil.isNotEmpty(templateId)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getTemplateId, templateId);
        }
        //所属流程
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (ObjectUtil.isNotEmpty(flowId)) {
            FlowTemplateJsonPage page = new FlowTemplateJsonPage();
            page.setTemplateId(templateId);
            page.setFlowId(flowId);
            List<String> flowList = flowTemplateJsonService.getListPage(page, false).stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
            if (flowList.size() == 0) {
                return new ArrayList<>();
            }
            queryWrapper.lambda().in(FlowTaskEntity::getFlowId, flowList);
        }
        //流程状态
        Integer status = paginationFlowTask.getStatus() != null ? paginationFlowTask.getStatus() : null;
        if (ObjectUtil.isNotEmpty(status)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getStatus, status);
        }
        //紧急程度
        Integer flowUrgent = paginationFlowTask.getFlowUrgent() != null ? paginationFlowTask.getFlowUrgent() : null;
        if (ObjectUtil.isNotEmpty(flowUrgent)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowUrgent, flowUrgent);
        }
        //所属分类
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        if (ObjectUtil.isNotEmpty(flowCategory)) {
            queryWrapper.lambda().eq(FlowTaskEntity::getFlowCategory, flowCategory);
        }
        //排序
        if ("asc".equals(paginationFlowTask.getSort().toLowerCase())) {
            queryWrapper.lambda().orderByAsc(FlowTaskEntity::getStatus).orderByAsc(FlowTaskEntity::getStartTime);
        } else {
            queryWrapper.lambda().orderByAsc(FlowTaskEntity::getStatus).orderByDesc(FlowTaskEntity::getStartTime);
        }
        Page<FlowTaskEntity> page = new Page<>(paginationFlowTask.getCurrentPage(), paginationFlowTask.getPageSize());
        IPage<FlowTaskEntity> flowTaskEntityPage = this.page(page, queryWrapper);
        if (!flowTaskEntityPage.getRecords().isEmpty()) {
            List<String> ids = flowTaskEntityPage.getRecords().stream().map(FlowTaskEntity::getId).collect(Collectors.toList());
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTaskEntity::getId, ids);
            //排序
            if ("asc".equals(paginationFlowTask.getSort().toLowerCase())) {
                queryWrapper.lambda().orderByAsc(FlowTaskEntity::getStatus).orderByAsc(FlowTaskEntity::getStartTime);
            } else {
                queryWrapper.lambda().orderByAsc(FlowTaskEntity::getStatus).orderByDesc(FlowTaskEntity::getStartTime);
            }
            flowTaskEntityPage.setRecords(this.list(queryWrapper));
        }
        return paginationFlowTask.setData(flowTaskEntityPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTaskListModel> getWaitList(PaginationFlowTask paginationFlowTask) {
        List<FlowTaskListModel> result = getWaitListAll(paginationFlowTask);
        //返回数据
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), result), result.size());
    }

    @Override
    public List<FlowTaskListModel> getWaitListAll(PaginationFlowTask paginationFlowTask) {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>();
        List<String> handleId = new ArrayList<>();
        List<FlowDelegateEntity> flowDelegateList = flowDelegateService.getUser(userId);
        for (FlowDelegateEntity entity : flowDelegateList) {
            handleId.add(entity.getUserId());
        }
        handleId.add(userId);
        //代办人
        map.put("handleId", handleId);
        //关键字（流程名称、流程编码）
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            map.put("keyWord", "%" + keyWord + "%");
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            String startTimes = DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00";
            String endTimes = DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59";
            map.put("startTime", startTimes);
            map.put("endTime", endTimes);
            if (DbTypeUtil.checkOracle(dataSourceUtil)) {
                map.put("oracle", '1');
            }
        }
        //流程模板
        String templateId = paginationFlowTask.getTemplateId() != null ? paginationFlowTask.getTemplateId() : null;
        map.put("templateId", templateId);
        //是否批量
        Integer isBatch = paginationFlowTask.getIsBatch() != null ? paginationFlowTask.getIsBatch() : null;
        map.put("isBatch", isBatch);
        //所属流程
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (StringUtil.isNotEmpty(flowId)) {
            FlowTemplateJsonPage page = new FlowTemplateJsonPage();
            page.setTemplateId(templateId);
            page.setFlowId(flowId);
            List<String> flowList = new ArrayList<>();
            if (ObjectUtil.isEmpty(isBatch)) {
                flowList.addAll(flowTemplateJsonService.getListPage(page, false).stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList()));
            } else {
                flowList.add(flowId);
            }
            if (flowList.size() == 0) {
                return new ArrayList<>();
            }
            map.put("flowList", flowList);
        }
        //所属分类
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        map.put("flowCategory", flowCategory);
        //发起人员
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        map.put("creatorUserId", creatorUserId);
        //节点编码
        String nodeCode = paginationFlowTask.getNodeCode() != null ? paginationFlowTask.getNodeCode() : null;
        map.put("nodeCode", nodeCode);
        //紧急程度
        Integer flowUrgent = paginationFlowTask.getFlowUrgent() != null ? paginationFlowTask.getFlowUrgent() : null;
        map.put("flowUrgent", flowUrgent);
        List<FlowTaskListModel> data = this.baseMapper.getWaitList(map);
        List<FlowTaskListModel> result = new LinkedList<>();
        for (FlowTaskListModel entity : data) {
            List<Date> list = StringUtil.isNotEmpty(entity.getDescription()) ? JsonUtil.getJsonToList(entity.getDescription(), Date.class) : new ArrayList<>();
            boolean isuser = entity.getHandleId().equals(userId);
            entity.setDelegateUser(!isuser ? entity.getCreatorUserId() : null);
            List<FlowDelegateEntity> flowList = flowDelegateList.stream().filter(t -> (StringUtil.isNotEmpty(t.getFlowId()) && t.getFlowId().contains(entity.getTemplateId()) || StringUtil.isEmpty(t.getFlowId()))).collect(Collectors.toList());
            //判断是否有自己审核
            boolean delegate = !isuser ? flowList.stream().filter(t -> t.getUserId().equals(entity.getHandleId())).count() > 0 : true;
            if (delegate) {
                result.add(entity);
                Date date = new Date();
                boolean del = list.stream().filter(t -> t.getTime() > date.getTime()).count() > 0;
                if (del) {
                    result.remove(entity);
                }
            }
        }
        return result;
    }

    @Override
    public List<FlowTaskListModel> getTrialList(PaginationFlowTask paginationFlowTask) {
        Map<String, Object> map = new HashMap<>(16);
        String userId = userProvider.get().getUserId();
        map.put("handleId", userId);
        //关键字（流程名称、流程编码）
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            map.put("keyWord", "%" + keyWord + "%");
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            String startTimes = DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00";
            String endTimes = DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59";
            map.put("startTime", startTimes);
            map.put("endTime", endTimes);
            if (DbTypeUtil.checkOracle(dataSourceUtil)) {
                map.put("oracle", '1');
            }
        }
        //流程模板
        String templateId = paginationFlowTask.getTemplateId() != null ? paginationFlowTask.getTemplateId() : null;
        map.put("templateId", templateId);
        //所属流程
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (StringUtil.isNotEmpty(flowId)) {
            FlowTemplateJsonPage page = new FlowTemplateJsonPage();
            page.setTemplateId(templateId);
            page.setFlowId(flowId);
            List<String> flowList = flowTemplateJsonService.getListPage(page, false).stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
            if (flowList.size() == 0) {
                return new ArrayList<>();
            }
            map.put("flowList", flowList);
        }
        //所属分类
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        map.put("flowCategory", flowCategory);
        //发起人员
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        map.put("creatorUserId", creatorUserId);
        //紧急程度
        Integer flowUrgent = paginationFlowTask.getFlowUrgent() != null ? paginationFlowTask.getFlowUrgent() : null;
        map.put("flowUrgent", flowUrgent);
        List<FlowTaskListModel> data = this.baseMapper.getTrialList(map);
        Map<String, List<FlowTaskListModel>> dataAll = data.stream().collect(Collectors.groupingBy(FlowTaskListModel::getThisStepId));
        List<FlowTaskListModel> result = new ArrayList<>();
        for (String key : dataAll.keySet()) {
            List<FlowTaskListModel> flowTaskListModels = dataAll.get(key).stream().sorted(Comparator.comparing(FlowTaskListModel::getCreatorTime).reversed()).collect(Collectors.toList());
            if (flowTaskListModels.size() > 0) {
                FlowTaskListModel entity = flowTaskListModels.get(0);
                boolean isuser = entity.getHandleId().equals(entity.getDelegateUser());
                entity.setDelegateUser(!isuser ? entity.getCreatorUserId() : null);
                result.add(flowTaskListModels.get(0));
            }
        }
        result = result.stream().sorted(Comparator.comparing(FlowTaskListModel::getCreatorTime).reversed()).collect(Collectors.toList());
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), result), result.size());
    }

    @Override
    public List<FlowTaskListModel> getTrialList() {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("handleId", userId);
        List<FlowTaskListModel> data = this.baseMapper.getTrialList(map);
        return data;
    }

    @Override
    public List<FlowTaskEntity> getWaitList() {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>();
        List<String> handleId = new ArrayList<>();
        List<FlowDelegateEntity> flowDelegateList = flowDelegateService.getUser(userId);
        for (FlowDelegateEntity entity : flowDelegateList) {
            handleId.add(entity.getCreatorUserId());
        }
        handleId.add(userId);
        //代办人
        map.put("handleId", handleId);
        List<FlowTaskListModel> data = this.baseMapper.getWaitList(map);
        //返回数据
        List<FlowTaskEntity> result = JsonUtil.getJsonToList(data, FlowTaskEntity.class);
        return result;
    }

    @Override
    public List<FlowTaskListModel> getCirculateList() {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>();
        List<String> objectId = new ArrayList<>();
        objectId.add(userId);
        //传阅人员
        map.put("objectId", objectId);
        List<FlowTaskListModel> result = this.baseMapper.getCirculateList(map);
        return result;
    }

    @Override
    public List<FlowTaskListModel> getDashboardAllWaitList() {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>();
        List<String> handleId = new ArrayList<>();
        handleId.add(userId);
        //代办人
        map.put("handleId", handleId);
        List<FlowTaskListModel> data = this.baseMapper.getWaitList(map);
        if (data.size() > 20) {
            return data.subList(0, 20);
        }
        return data;
    }

    @Override
    public List<FlowTaskListModel> getCirculateList(PaginationFlowTask paginationFlowTask) {
        String userId = userProvider.get().getUserId();
        Map<String, Object> map = new HashMap<>();
        List<String> objectId = new ArrayList<>();
        objectId.add(userId);
        List<UserRelationEntity> list = serviceUtil.getListByUserIdAll(objectId);
        objectId.addAll(list.stream().map(UserRelationEntity::getObjectId).collect(Collectors.toList()));
        //传阅人员
        map.put("objectId", objectId);
        //关键字（流程名称、流程编码）
        String keyWord = paginationFlowTask.getKeyword() != null ? paginationFlowTask.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            map.put("keyWord", "%" + keyWord + "%");
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationFlowTask.getStartTime() != null ? paginationFlowTask.getStartTime() : null;
        String endTime = paginationFlowTask.getEndTime() != null ? paginationFlowTask.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            String startTimes = DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00";
            String endTimes = DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59";
            map.put("startTime", startTimes);
            map.put("endTime", endTimes);
            if (DbTypeUtil.checkOracle(dataSourceUtil)) {
                map.put("oracle", '1');
            }
        }
        //流程模板
        String templateId = paginationFlowTask.getTemplateId() != null ? paginationFlowTask.getTemplateId() : null;
        map.put("templateId", templateId);
        //所属流程
        String flowId = paginationFlowTask.getFlowId() != null ? paginationFlowTask.getFlowId() : null;
        if (StringUtil.isNotEmpty(flowId)) {
            FlowTemplateJsonPage page = new FlowTemplateJsonPage();
            page.setTemplateId(templateId);
            page.setFlowId(flowId);
            List<String> flowList = flowTemplateJsonService.getListPage(page, false).stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
            if (flowList.size() == 0) {
                return new ArrayList<>();
            }
            map.put("flowList", flowList);
        }
        //所属分类
        String flowCategory = paginationFlowTask.getFlowCategory() != null ? paginationFlowTask.getFlowCategory() : null;
        map.put("flowCategory", flowCategory);
        //发起人员
        String creatorUserId = paginationFlowTask.getCreatorUserId() != null ? paginationFlowTask.getCreatorUserId() : null;
        map.put("creatorUserId", creatorUserId);
        //紧急程度
        Integer flowUrgent = paginationFlowTask.getFlowUrgent() != null ? paginationFlowTask.getFlowUrgent() : null;
        map.put("flowUrgent", flowUrgent);
        List<FlowTaskListModel> data = this.baseMapper.getCirculateList(map);
        return paginationFlowTask.setData(PageUtil.getListPage((int) paginationFlowTask.getCurrentPage(), (int) paginationFlowTask.getPageSize(), data), data.size());
    }

    @Override
    public FlowTaskEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskEntity::getId, id);
        FlowTaskEntity entity = this.getOne(queryWrapper);
        if (entity == null) {
            throw new WorkFlowException(MsgCode.WF115.get());
        }
        return entity;
    }

    @Override
    public void update(FlowTaskEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void create(FlowTaskEntity entity) {
        this.save(entity);
    }

    @Override
    public void createOrUpdate(FlowTaskEntity entity) {
        this.saveOrUpdate(entity);
    }

    @Override
    public FlowTaskEntity getInfoSubmit(String id, SFunction<FlowTaskEntity, ?>... columns) {
        List<FlowTaskEntity> list = getInfosSubmit(new String[]{id}, columns);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<FlowTaskEntity> getInfosSubmit(String[] ids, SFunction<FlowTaskEntity, ?>... columns) {
        List<FlowTaskEntity> resultList = Collections.emptyList();
        if (ids == null || ids.length == 0) {
            return resultList;
        }
        LambdaQueryWrapper<FlowTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (ids.length == 1) {
            queryWrapper.select(columns).and(
                    t -> t.eq(FlowTaskEntity::getId, ids[0])
            );
            resultList = this.list(queryWrapper);
            if (resultList.isEmpty()) {
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(columns).and(
                        t -> t.eq(FlowTaskEntity::getProcessId, ids[0])
                );
                resultList = this.list(queryWrapper);
            }
        } else {
            queryWrapper.select(FlowTaskEntity::getId).and(t -> {
                t.in(FlowTaskEntity::getId, ids).or().in(FlowTaskEntity::getProcessId, ids);
            });
            List<String> resultIds = this.listObjs(queryWrapper, t -> t.toString());
            if (!resultIds.isEmpty()) {
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(columns).in(FlowTaskEntity::getId, resultIds);
                resultList = this.list(queryWrapper);
            }
        }
        return resultList;
    }

    @Override
    public void delete(FlowTaskEntity entity) throws WorkFlowException {
        if(FlowTaskStatusEnum.Suspend.getCode().equals(entity.getStatus())){
            throw new WorkFlowException("流程处于挂起状态，不可操作");
        }
        if (!checkStatus(entity.getStatus())) {
            throw new WorkFlowException(MsgCode.WF116.get());
        } else {
            List<String> idList = new ArrayList() {{
                add(entity.getId());
            }};
            this.deleteAll(idList, true, true);
        }
    }

    @Override
    public void deleteChildAll(List<String> idAll) {
        this.deleteAll(idAll, true, true);
    }

    @Override
    public void delete(String[] ids) throws WorkFlowException {
        if (ids.length > 0) {
            List<String> idList = Arrays.asList(ids);
            List<FlowTaskEntity> flowTaskList = getOrderStaList(idList);
            List<FlowTaskEntity> del = flowTaskList.stream().filter(t -> t.getFlowType() == 1).collect(Collectors.toList());
            if (del.size() > 0) {
                throw new WorkFlowException(del.get(0).getFullName() + MsgCode.WF117.get());
            }
            List<FlowTaskEntity> child = flowTaskList.stream().filter(t -> !FlowNature.ParentId.equals(t.getParentId()) && StringUtil.isNotEmpty(t.getParentId())).collect(Collectors.toList());
            if (child.size() > 0) {
                throw new WorkFlowException(child.get(0).getFullName() + MsgCode.WF118.get());
            }
            List<FlowTaskEntity> taskStatusList = new ArrayList<>();
            for(String id : ids){
                List<String> childAllList = getChildAllList(id);
                taskStatusList.addAll(getOrderStaList(childAllList));
            }
            List<FlowTaskEntity> taskStatus = taskStatusList.stream().filter(t -> FlowTaskStatusEnum.Suspend.getCode().equals(t.getStatus())).collect(Collectors.toList());
            if (taskStatus.size() > 0) {
                throw new WorkFlowException(taskStatus.get(0).getFullName() + "已被挂起不能删除");
            }
            this.deleteAll(idList, true, true);
        }
    }

    @Override
    public List<FlowTaskEntity> getOrderStaList(List<String> id) {
        List<FlowTaskEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTaskEntity::getId, id);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public List<FlowTaskEntity> getChildList(String id, SFunction<FlowTaskEntity, ?>... columns) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(columns).in(FlowTaskEntity::getParentId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskEntity> getTemplateIdList(String tempId) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskEntity::getTemplateId, tempId);
        queryWrapper.lambda().select(FlowTaskEntity::getId, FlowTaskEntity::getFlowId);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskEntity> getFlowList(String flowId) {
        QueryWrapper<FlowTaskEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskEntity::getFlowId, flowId);
        queryWrapper.lambda().select(FlowTaskEntity::getId);
        return list(queryWrapper);
    }

    @Override
    public List<FlowBatchModel> batchFlowSelector() {
        List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getBatchList();
        List<String> taskIdList = operatorList.stream().map(FlowTaskOperatorEntity::getTaskId).collect(Collectors.toList());
        List<FlowTaskEntity> taskList = getOrderStaList(taskIdList);
        Map<String, List<FlowTaskEntity>> flowList = taskList.stream().filter(t -> ObjectUtil.isNotEmpty(t.getIsBatch()) && t.getIsBatch() == 1).collect(Collectors.groupingBy(FlowTaskEntity::getTemplateId));
        List<FlowBatchModel> batchFlowList = new ArrayList<>();
        for (String key : flowList.keySet()) {
            List<FlowTaskEntity> flowTaskList = flowList.get(key);
            List<String> flowTask = flowTaskList.stream().map(FlowTaskEntity::getId).collect(Collectors.toList());
            List<FlowTemplateJsonEntity> templateJsonList = flowTemplateJsonService.getTemplateJsonList(flowTaskList.stream().map(FlowTaskEntity::getFlowId).collect(Collectors.toList()));
            if (flowTaskList.size() > 0) {
                String flowName = flowTaskList.stream().map(FlowTaskEntity::getFlowName).distinct().collect(Collectors.joining(","));
                String flowId = templateJsonList.stream().map(FlowTemplateJsonEntity::getTemplateId).distinct().collect(Collectors.joining(","));
                Long count = operatorList.stream().filter(t -> flowTask.contains(t.getTaskId())).count();
                FlowBatchModel batchModel = new FlowBatchModel();
                batchModel.setNum(count);
                batchModel.setId(flowId);
                batchModel.setFullName(flowName + "(" + count + ")");
                batchFlowList.add(batchModel);
            }
        }
        batchFlowList = batchFlowList.stream().sorted(Comparator.comparing(FlowBatchModel::getNum).reversed()).collect(Collectors.toList());
        return batchFlowList;
    }

    @Override
    public List<String> getChildAllList(String id) {
        List<String> idAll = new ArrayList<>();
        List<String> idList = new ArrayList() {{
            add(id);
        }};
        this.deleTaskAll(idList, idAll);
        return idAll;
    }

    @Override
    public void deleTaskAll(List<String> idList, List<String> idAll) {
        idAll.addAll(idList);
        for (String id : idList) {
            List<FlowTaskEntity> taskAll = this.getChildList(id, FlowTaskEntity::getId);
            List<String> list = taskAll.stream().map(FlowTaskEntity::getId).collect(Collectors.toList());
            this.deleTaskAll(list, idAll);
        }
    }

    @Override
    public String getVisualFormId(String id) {
        return this.baseMapper.getVisualFormId(id);
    }

    @Override
    public void getChildList(String id, boolean suspend, List<String> list) {
        List<FlowTaskEntity> taskAll = this.getChildList(id, FlowTaskEntity::getId, FlowTaskEntity::getIsAsync);
        if (suspend) {
            taskAll = taskAll.stream().filter(t -> FlowNature.ChildSync.equals(t.getIsAsync())).collect(Collectors.toList());
        }
        for (FlowTaskEntity entity : taskAll) {
            list.add(entity.getId());
            this.getChildList(entity.getId(), suspend, list);
        }
    }

    /**
     * 验证有效状态
     *
     * @param status 状态编码
     * @return
     */
    private boolean checkStatus(int status) {
        List<Integer> statusList = new ArrayList(){{
            add(FlowTaskStatusEnum.Draft.getCode());
            add(FlowTaskStatusEnum.Reject.getCode());
            add(FlowTaskStatusEnum.Revoke.getCode());
        }};
        if (statusList.contains(status)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 递归删除所有子节点
     *
     * @param idList
     * @param isRecord
     * @param isCirculate
     */
    private void deleteAll(List<String> idList, boolean isRecord, boolean isCirculate) {
        List<String> idAll = new ArrayList<>();
        this.deleTaskAll(idList, idAll);
        if (idAll.size() > 0) {
            QueryWrapper<FlowTaskEntity> task = new QueryWrapper<>();
            task.lambda().in(FlowTaskEntity::getId, idAll);
            this.remove(task);
            QueryWrapper<FlowTaskNodeEntity> node = new QueryWrapper<>();
            node.lambda().in(FlowTaskNodeEntity::getTaskId, idAll);
            flowTaskNodeService.remove(node);
            QueryWrapper<FlowTaskOperatorEntity> operator = new QueryWrapper<>();
            operator.lambda().in(FlowTaskOperatorEntity::getTaskId, idAll);
            flowTaskOperatorService.remove(operator);
            QueryWrapper<FlowOperatorUserEntity> operatoruser = new QueryWrapper<>();
            operatoruser.lambda().in(FlowOperatorUserEntity::getTaskId, idAll);
            flowOperatorUserService.remove(operatoruser);
            QueryWrapper<FlowRejectDataEntity> rejectData = new QueryWrapper<>();
            rejectData.lambda().in(FlowRejectDataEntity::getId, idAll);
            flowRejectDataService.remove(rejectData);
            if (isRecord) {
                QueryWrapper<FlowTaskOperatorRecordEntity> record = new QueryWrapper<>();
                record.lambda().in(FlowTaskOperatorRecordEntity::getTaskId, idAll);
                flowTaskOperatorRecordService.remove(record);
            }
            if (isCirculate) {
                QueryWrapper<FlowTaskCirculateEntity> circulate = new QueryWrapper<>();
                circulate.lambda().in(FlowTaskCirculateEntity::getTaskId, idAll);
                flowTaskCirculateService.remove(circulate);
            }
        }
    }


}
