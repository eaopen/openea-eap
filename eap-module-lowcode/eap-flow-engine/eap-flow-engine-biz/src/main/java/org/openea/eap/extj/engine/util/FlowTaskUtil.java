package org.openea.eap.extj.engine.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.*;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import org.openea.eap.extj.engine.model.flowengine.*;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.FlowAssignModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.RuleListModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.Custom;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import org.openea.eap.extj.engine.model.flowmessage.FlowMsgModel;
import org.openea.eap.extj.engine.model.flowtask.*;
import org.openea.eap.extj.engine.model.flowtask.method.TaskChild;
import org.openea.eap.extj.engine.model.flowtask.method.TaskHandleIdStatus;
import org.openea.eap.extj.engine.model.flowtask.method.TaskOperatoUser;
import org.openea.eap.extj.engine.model.flowtask.method.TaskOperator;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.wxutil.HttpUtil;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.*;
import org.openea.eap.extj.engine.model.flowengine.*;
import org.openea.eap.extj.engine.model.flowtask.*;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.job.WorkTimeoutJobUtil;
import org.openea.eap.extj.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.*;
import org.openea.eap.extj.engine.model.flowengine.*;
import org.openea.eap.extj.engine.model.flowtask.*;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 */
@Component
@Slf4j
public class FlowTaskUtil {

    /**
     * 节点id
     **/
    public String taskNodeId = "taskNodeId";
    /**
     * 任务id
     **/
    public String taskId = "taskId";

    @Autowired
    public FlowUserService flowUserService;
    @Autowired
    public FlowCandidatesService flowCandidatesService;
    @Autowired
    public FlowTaskNodeService flowTaskNodeService;
    @Autowired
    public FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    public FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    public FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    public FlowTemplateService flowTemplateService;
    @Autowired
    public FlowMsgUtil flowMsgUtil;
    @Autowired
    public FlowTaskService flowTaskService;
    @Autowired
    public FlowTaskNewService flowTaskNewService;
    @Autowired
    public ServiceAllUtil serviceUtil;
    @Autowired
    private FlowOperatorUserService flowOperatorUserService;
    @Autowired
    private WorkTimeoutJobUtil workTimeoutJobUtil;


    //--------------------------------------候选人------------------------------------------------------------------

    /**
     * 查询候选人
     *
     * @param taskNodeList     所有节点
     * @param childNodeListAll 节点数据
     * @param nodeCode         当前节点
     */
    public void candidate(List<FlowTaskNodeEntity> taskNodeList, List<ChildNodeList> childNodeListAll, String nodeCode, boolean isNext) {
        List<FlowTaskNodeEntity> nodeList = taskNodeList.stream().filter(t -> t.getNodeCode().equals(nodeCode)).collect(Collectors.toList());
        for (FlowTaskNodeEntity taskNodeEntity : nodeList) {
            if (isNext) {
                List<String> nextNodeList = Arrays.asList(taskNodeEntity.getNodeNext().split(","));
                List<FlowTaskNodeEntity> nextTaskNodeList = taskNodeList.stream().filter(t -> nextNodeList.contains(t.getNodeCode())).collect(Collectors.toList());
                for (FlowTaskNodeEntity nodeEntity : nextTaskNodeList) {
                    String nodeType = nodeEntity.getNodeType();
                    String code = nodeEntity.getNodeCode();
                    ChildNodeList taskNode = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
                    ChildNodeList childNodeList = JsonUtil.getJsonToBean(nodeEntity.getNodePropertyJson(), ChildNodeList.class);
                    childNodeList.getCustom().setBranchFlow(taskNode.getCustom().getBranchFlow());
                    childNodeList.getCustom().setFlow(taskNode.getCustom().getFlow());
                    childNodeListAll.add(childNodeList);
                    if (FlowNature.NodeSubFlow.equals(nodeType)) {
                        this.candidate(taskNodeList, childNodeListAll, code, isNext);
                    }
                }
            } else {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
                childNodeListAll.add(childNodeList);
            }
        }
    }

    //-----------------------------------提交保存--------------------------------------------

    /**
     * 获取节点候选人
     *
     * @return
     * @throws WorkFlowException
     */
    public List<ChildNodeList> childNodeListAll(FlowTaskOperatorEntity operatorEntity, FlowModel flowModel) throws WorkFlowException {
        List<ChildNodeList> childNodeListAll = new ArrayList<>();
        List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        String startNode = "";
        UserInfo userInfo = flowModel.getUserInfo();
        boolean isNodeCode = StringUtil.isNotEmpty(flowModel.getNodeCode());
        boolean operator = operatorEntity != null;
        String flowId = flowModel.getFlowId();
        Map<String, Object> formData = flowModel.getFormData();
        if (ObjectUtil.isNotNull(flowId)) {
            FlowTemplateAllModel templateAllModel = templateJson(flowId);
            FlowTemplateJsonEntity templateJson = templateAllModel.getTemplateJson();
            ChildNode childNodeAll = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
            //获取流程节点
            List<ChildNodeList> nodeListAll = new ArrayList<>();
            //递归获取条件数据和节点数据
            FlowTaskEntity flowTask = operator ? flowTaskService.getInfo(operatorEntity.getTaskId()) : new FlowTaskEntity();
            flowTask.setId(operator ? flowTask.getId() : RandomUtil.uuId());
            flowTask.setCreatorUserId(operator ? flowTask.getCreatorUserId() : userInfo.getUserId());
            flowTask.setCreatorTime(operator ? flowTask.getCreatorTime() : new Date());
            flowTask.setFlowFormContentJson(JsonUtil.getObjectToString(formData));
            FlowUpdateNode updateNode = FlowUpdateNode.builder().childNodeAll(childNodeAll).nodeListAll(nodeListAll).taskNodeList(taskNodeList).conditionListAll(conditionListAll).flowTask(flowTask).userInfo(userInfo).isSubmit(!operator).build();
            this.updateNodeList(updateNode);
            Optional<FlowTaskNodeEntity> first = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst();
            if (!first.isPresent()) {
                throw new WorkFlowException(MsgCode.COD001.get());
            }
            FlowTaskNodeEntity startNodes = first.get();
            startNode = startNodes.getNodeCode();
            this.nodeList(taskNodeList, startNode, 1L);
        }
        String nodeCode = isNodeCode ? flowModel.getNodeCode() : operator ? operatorEntity.getNodeCode() : startNode;
        //判断条件下选择分支
        FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getNodeCode().equals(nodeCode)).findFirst().orElse(null);
        if (taskNode != null) {
            for (ConditionList condition : conditionListAll) {
                if (taskNode.getNodeNext().equals(condition.getFlowId())) {
                    ChildNodeList childNodeList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                    childNodeList.getCustom().setBranchFlow(condition.getBranchFlow());
                    taskNode.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                }
            }
        }
        this.candidate(taskNodeList, childNodeListAll, nodeCode, !isNodeCode);
        return childNodeListAll;
    }

    /**
     * 流程任务赋值
     *
     * @param taskEntity 流程任务实例
     * @param flowModel  提交数据
     * @throws WorkFlowException 异常
     */
    public void task(FlowTaskEntity taskEntity, FlowTemplateAllModel templateAllModel, FlowModel flowModel) throws WorkFlowException {
        if (StringUtil.isNotEmpty(taskEntity.getId()) && !checkStatus(taskEntity.getStatus())) {
            throw new WorkFlowException(MsgCode.WF108.get());
        }
        FlowTemplateJsonEntity templateJson = templateAllModel.getTemplateJson();
        FlowTemplateEntity template = templateAllModel.getTemplate();
        //创建实例
        taskEntity.setId(flowModel.getProcessId());
        taskEntity.setProcessId(flowModel.getProcessId());
        taskEntity.setEnCode(flowModel.getBillNo());
        taskEntity.setFullName(flowModel.getFlowTitle());
        taskEntity.setFlowUrgent(flowModel.getFlowUrgent() != null ? flowModel.getFlowUrgent() : 1);
        taskEntity.setFlowCode(template.getEnCode() != null ? template.getEnCode() : MsgCode.WF109.get());
        taskEntity.setFlowName(template.getFullName());
        taskEntity.setFlowType(template.getType());
        taskEntity.setFlowCategory(template.getCategory());
        taskEntity.setFlowTemplateJson(templateJson.getFlowTemplateJson());
        taskEntity.setFlowVersion(templateJson.getVersion());
        taskEntity.setFlowId(templateJson.getId());
        taskEntity.setTemplateId(template.getId());
        List<String> statusList = new ArrayList() {{
            add(FlowStatusEnum.save.getMessage());
            add(FlowStatusEnum.submit.getMessage());
        }};
        if (statusList.contains(flowModel.getStatus())) {
            taskEntity.setStatus(FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? FlowTaskStatusEnum.Draft.getCode() : FlowTaskStatusEnum.Handle.getCode());
        }
        taskEntity.setCompletion(FlowNature.ProcessCompletion);
        taskEntity.setCreatorTime(new Date());
        taskEntity.setEnabledMark(1);
        taskEntity.setCreatorUserId(flowModel.getUserId());
        taskEntity.setFlowFormContentJson(flowModel.getFormData() != null ? JsonUtil.getObjectToString(flowModel.getFormData()) : "{}");
        taskEntity.setParentId(flowModel.getParentId() != null ? flowModel.getParentId() : FlowNature.ParentId);
        taskEntity.setIsAsync(flowModel.getIsAsync() ? FlowNature.ChildAsync : FlowNature.ChildSync);
        taskEntity.setDelegateUser(flowModel.getDelegateUser());
        ChildNode childNode = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
        boolean isBatchApproval = childNode.getProperties().getIsBatchApproval();
        taskEntity.setIsBatch(isBatchApproval ? 1 : 0);
    }

    /**
     * 验证有效状态
     *
     * @param status 状态编码
     * @return
     */
    public boolean checkStatus(int status) {
        if (status == FlowTaskStatusEnum.Draft.getCode() || status == FlowTaskStatusEnum.Reject.getCode() || status == FlowTaskStatusEnum.Revoke.getCode()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提交节点
     *
     * @param nodeListModel 条件对象
     */
    public void nodeListAll(FlowNodeListModel nodeListModel) throws WorkFlowException {
        List<FlowTaskNodeEntity> dataAll = nodeListModel.getDataAll();
        FlowModel flowModel = nodeListModel.getFlowModel();
        boolean isAdd = nodeListModel.getIsAdd();
        FlowTaskNodeEntity taskNode = nodeListModel.getTaskNode();
        long num = nodeListModel.getNum();
        this.nodeList(dataAll, taskNode.getNodeCode(), num);
        String nodeNext = FlowNature.NodeEnd;
        String type = FlowNature.EndRound;
        long maxNum = 99L;
        for (int i = 0; i < dataAll.size(); i++) {
            FlowTaskNodeEntity entity = dataAll.get(i);
            if ("timer".equals(entity.getNodeType())) {
                dataAll.remove(i);
            }
        }
        for (int i = 0; i < dataAll.size(); i++) {
            FlowTaskNodeEntity entity = dataAll.get(i);
            if (StringUtil.isEmpty(entity.getNodeNext())) {
                entity.setNodeNext(nodeNext);
            }
            //选择分支
            List<String> branchList = flowModel.getBranchList() != null ? flowModel.getBranchList() : new ArrayList<>();
            List<String> flowIdList = new ArrayList() {{
                add(entity.getNodeCode());
            }};
            flowIdList.retainAll(branchList);
            entity.setCandidates(flowIdList.size() > 0 ? JsonUtil.getObjectToString(branchList) : JsonUtil.getObjectToString(new ArrayList<>()));
            //查询子流程发起的表单
            if (FlowNature.NodeSubFlow.equals(entity.getNodeType())) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                FlowTemplateAllModel templateAllModel = templateJson(childNodeList.getProperties().getFlowId());
                ChildNode subFlow = JsonUtil.getJsonToBean(templateAllModel.getTemplateJson().getFlowTemplateJson(), ChildNode.class);
                childNodeList.getProperties().setFormId(subFlow.getProperties().getFormId());
                entity.setNodePropertyJson(JsonUtil.getObjectToString(childNodeList));
                entity.setFormId(subFlow.getProperties().getFormId());
            }
        }
        if (isAdd) {
            FlowTaskNodeEntity endround = new FlowTaskNodeEntity();
            endround.setId(RandomUtil.uuId());
            endround.setNodeCode(nodeNext);
            endround.setNodeName(MsgCode.WF007.get());
            endround.setCompletion(FlowNature.ProcessCompletion);
            endround.setCreatorTime(new Date());
            endround.setSortCode(maxNum);
            endround.setTaskId(taskNode.getTaskId());
            ChildNodeList endNode = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            endNode.getCustom().setNodeId(nodeNext);
            endNode.setTaskNodeId(endround.getId());
            endNode.getCustom().setType(type);
            endround.setNodePropertyJson(JsonUtil.getObjectToString(endNode));
            endround.setNodeType(type);
            endround.setState(FlowNodeEnum.Process.getCode());
            endround.setNodeNext("");
            endround.setCandidates(JsonUtil.getObjectToString(new ArrayList<>()));
            dataAll.add(endround);
            for (FlowTaskNodeEntity entity : dataAll) {
                flowTaskNodeService.create(entity);
            }
            FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
            operator.setId(FlowNature.ParentId);
            this.candidateList(flowModel, dataAll, operator);
        }
    }

    /**
     * 递归遍历编码
     *
     * @param dataAll 所有节点
     * @param node    当前节点
     * @param num     排序
     */
    public void nodeList(List<FlowTaskNodeEntity> dataAll, String node, long num) {
        List<String> nodeAll = Arrays.asList(node.split(","));
        List<FlowTaskNodeEntity> nodeList = dataAll.stream().filter(t -> nodeAll.contains(t.getNodeCode())).collect(Collectors.toList());
        for (FlowTaskNodeEntity entity : nodeList) {
            entity.setSortCode(num);
            entity.setState(FlowNodeEnum.Process.getCode());
        }
        List<String> nextNode = nodeList.stream().filter(t -> StringUtil.isNotEmpty(t.getNodeNext())).map(FlowTaskNodeEntity::getNodeNext).collect(Collectors.toList());
        if (nextNode.size() > 0) {
            String nodes = String.join(",", nextNode);
            num++;
            nodeList(dataAll, nodes, num);
        }
    }

    /**
     * 流程用户信息
     *
     * @param flowTask
     */
    public void flowUser(FlowTaskEntity flowTask) {
        UserEntity user = serviceUtil.getUserInfo(flowTask.getCreatorUserId());
        FlowUserEntity flowUser = new FlowUserEntity();
        flowUser.setTaskId(flowTask.getId());
        if (user != null) {
            flowUser.setManagerId(user.getManagerId());
            flowUser.setOrganizeId(user.getOrganizeId());
            flowUser.setPositionId(user.getPositionId());
            String listByManagerId = serviceUtil.getListByManagerId(user.getId()).stream().map(UserEntity::getId).collect(Collectors.joining(","));
            flowUser.setSubordinate(listByManagerId);
            flowUser.setSuperior(user.getManagerId());
            List<OrganizeEntity> departmentAll = serviceUtil.getDepartmentAll(user.getOrganizeId());
            String department = departmentAll.stream().map(OrganizeEntity::getId).collect(Collectors.joining(","));
            flowUser.setDepartment(department);

        }
        flowUserService.create(flowUser);
    }

    //-------------------------审批--------------------------------
    //---------通过-------------

    /**
     * 修改选择分支的流程状态
     *
     * @param nodeCodeList
     * @param taskNodeList
     * @param operatorList
     */
    public void branchTaskNode(List<String> nodeCodeList, List<FlowTaskNodeEntity> taskNodeList, List<FlowTaskOperatorEntity> operatorList) {
        if (operatorList.size() > 0) {
            Set<String> operatorNode = operatorList.stream().map(FlowTaskOperatorEntity::getNodeCode).collect(Collectors.toSet());
            if (operatorNode.size() != nodeCodeList.size()) {
                //差集
                List<String> reduce = nodeCodeList.stream().filter(item -> !operatorNode.contains(item)).collect(Collectors.toList());
                List<String> reduceList = taskNodeList.stream().filter(t -> reduce.contains(t.getNodeCode())).map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
                Set<String> reduceListAll = new HashSet<>();
                this.upAll(reduceListAll, reduceList, taskNodeList);
                flowTaskNodeService.updateCompletion(new ArrayList<>(reduceListAll), FlowNature.AuditCompletion);
                //交集
                List<String> intersec = nodeCodeList.stream().filter(item -> operatorNode.contains(item)).collect(Collectors.toList());
                List<String> intersecList = taskNodeList.stream().filter(t -> intersec.contains(t.getNodeCode())).map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
                Set<String> intersectionList = new HashSet<>();
                this.upAll(intersectionList, intersecList, taskNodeList);
                flowTaskNodeService.updateCompletion(new ArrayList<>(intersectionList), FlowNature.ProcessCompletion);
            }
        }
    }

    /**
     * 创建节点
     *
     * @param flowTask
     * @param nodeListAll
     * @param conditionListAll
     * @param taskNodeList
     */
    public void createNodeList(FlowTaskEntity flowTask, List<ChildNodeList> nodeListAll, List<ConditionList> conditionListAll, List<FlowTaskNodeEntity> taskNodeList, UserInfo userInfo, boolean isSubmit) {
        List<FlowTaskNodeEntity> timerList = new ArrayList<>();
        List<FlowTaskNodeEntity> emptyList = new ArrayList<>();
        UserEntity userEntity = serviceUtil.getUserInfo(flowTask.getCreatorUserId());
        if (!isSubmit) {
            flowTask.setLastModifyUserId(userInfo.getUserId());
            flowTask.setLastModifyTime(new Date());
        }
        for (ChildNodeList childNode : nodeListAll) {
            FlowTaskNodeEntity taskNode = new FlowTaskNodeEntity();
            String nodeId = childNode.getCustom().getNodeId();
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            String dataJson = flowTask.getFlowFormContentJson();
            String type = childNode.getCustom().getType();
            taskNode.setId(RandomUtil.uuId());
            childNode.setTaskNodeId(taskNode.getId());
            childNode.setTaskId(flowTask.getId());
            taskNode.setCreatorTime(new Date());
            taskNode.setTaskId(flowTask.getId());
            taskNode.setNodeCode(nodeId);
            taskNode.setNodeType(type);
            taskNode.setState(FlowNodeEnum.Futility.getCode());
            taskNode.setSortCode(-2L);
            taskNode.setNodeUp(properties.getRejectStep());
            taskNode.setFormId(properties.getFormId());
            FlowConditionModel conditionModel = FlowConditionModel.builder().conditionListAll(conditionListAll).childNodeListAll(nodeListAll).flowTaskEntity(flowTask).data(dataJson).nodeId(nodeId).build();
            conditionModel.setUserEntity(userEntity);
            conditionModel.setFlowTaskEntity(flowTask);
            conditionModel.setUserInfo(userInfo);
            taskNode.setNodeNext(FlowJsonUtil.getNextNode(conditionModel));
            //赋值分支属性
            ConditionList conditionList = conditionListAll.stream().filter(t -> taskNode.getNodeNext().equals(t.getFlowId())).findFirst().orElse(null);
            if (conditionList != null) {
                childNode.getCustom().setFlow(conditionList.getFlow());
                childNode.getCustom().setBranchFlow(conditionList.getBranchFlow());
                childNode.getCustom().setFlowId(conditionList.getFlowId());
            }
            taskNode.setNodePropertyJson(JsonUtil.getObjectToString(childNode));
            boolean isStart = FlowNature.NodeStart.equals(childNode.getCustom().getType());
            taskNode.setCompletion(isStart ? FlowNature.AuditCompletion : FlowNature.ProcessCompletion);
            taskNode.setNodeName(isStart ? MsgCode.WF006.get() : properties.getTitle());
            taskNodeList.add(taskNode);
            if ("empty".equals(type)) {
                emptyList.add(taskNode);
            }
            if ("timer".equals(type)) {
                timerList.add(taskNode);
            }
        }
        //指向empty，继续指向下一个节点
        for (FlowTaskNodeEntity empty : emptyList) {
            List<FlowTaskNodeEntity> noxtEmptyList = taskNodeList.stream().filter(t -> t.getNodeNext().contains(empty.getNodeCode())).collect(Collectors.toList());
            for (FlowTaskNodeEntity entity : noxtEmptyList) {
                entity.setNodeNext(empty.getNodeNext());
            }
        }
        //指向timer，继续指向下一个节点
        for (FlowTaskNodeEntity timer : timerList) {
            //获取到timer的上一节点
            ChildNodeList timerlList = JsonUtil.getJsonToBean(timer.getNodePropertyJson(), ChildNodeList.class);
            DateProperties timers = timerlList.getTimer();
            timers.setNodeId(timer.getNodeCode());
            timers.setTime(true);
            List<FlowTaskNodeEntity> upEmptyList = taskNodeList.stream().filter(t -> t.getNodeNext().contains(timer.getNodeCode())).collect(Collectors.toList());
            for (FlowTaskNodeEntity entity : upEmptyList) {
                //上一节点赋值timer的属性
                ChildNodeList modelList = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                modelList.setTimer(timers);
                entity.setNodeNext(timer.getNodeNext());
                entity.setNodePropertyJson(JsonUtil.getObjectToString(modelList));
            }
        }
    }

    /**
     * 判断是否进行下一步
     *
     * @param nodeListAll    所有节点
     * @param nextNodeEntity 下一节点
     * @param taskNode       当前节点
     * @param flowModel      提交数据
     * @return
     */
    public List<FlowTaskNodeEntity> isNextAll(List<FlowTaskNodeEntity> nodeListAll, List<FlowTaskNodeEntity> nextNodeEntity, FlowTaskNodeEntity taskNode, FlowModel flowModel) {
        //1.先看是否加签人，有都不要进行，无进行下一步
        //2.判断会签是否比例通过
        //3.判断分流是否都结束
        //4.判断审批人是否都通过
        List<FlowTaskNodeEntity> result = new ArrayList<>();
        boolean hasFreeApprover = StringUtil.isNotEmpty(flowModel.getFreeApproverUserId());
        if (hasFreeApprover) {
            result.add(taskNode);
            //加签记录
        } else {
            //会签通过
            List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(taskNode.getId()) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            FlowCountersignModel countersign = new FlowCountersignModel();
            countersign.setTaskNodeId(taskNode.getId());
            countersign.setOperatorList(operatorList);
            //判断是否是会签
            boolean isCountersign = this.isCountersign(countersign);
            ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
            String type = properties.getAssigneeType();
            //流程通过
            if (isCountersign) {
                flowTaskOperatorService.update(nodeModel.getTaskNodeId(), type);
                taskNode.setCompletion(FlowNature.AuditCompletion);
                taskNode.setDraftData(JsonUtil.getObjectToString(flowModel.getFormData()));
                //跟新审批状态
                flowTaskNodeService.update(taskNode);
                //分流通过
                boolean isShunt = this.isShunt(nodeListAll, nextNodeEntity, taskNode);
                if (isShunt) {
                    result.addAll(nextNodeEntity);
                }
            }
        }
        return result;
    }

    /**
     * 判断会签比例
     *
     * @param operatorList
     * @return
     */
    public boolean fixedJointly(List<FlowTaskOperatorEntity> operatorList, ChildNodeList nodeModel, FlowCountersignModel countersign) {
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        boolean isCountersign = true;
        boolean fixed = !FlowNature.FixedApprover.equals(properties.getCounterSign());
        long pass = properties.getCountersignRatio();
        countersign.setFixed(fixed);
        countersign.setPass(pass);
        if (fixed) {
            double total = operatorList.size();
            List<FlowTaskOperatorEntity> passNumList = this.passNum(operatorList, FlowNature.AuditCompletion);
            countersign.setPassNumList(passNumList);
            double passNum = passNumList.size();
            isCountersign = this.isCountersign(pass, total, passNum);
        }
        return isCountersign;
    }

    /**
     * 判断分流是否结束
     *
     * @param nodeListAll    所有节点
     * @param nextNodeEntity 下一节点
     * @param taskNode       单前节点
     * @return
     */
    public boolean isShunt(List<FlowTaskNodeEntity> nodeListAll, List<FlowTaskNodeEntity> nextNodeEntity, FlowTaskNodeEntity taskNode) {
        boolean isNext = true;
        for (FlowTaskNodeEntity nodeEntity : nextNodeEntity) {
            String nextNode = nodeEntity.getNodeCode();
            List<FlowTaskNodeEntity> interflowAll = nodeListAll.stream().filter(t -> String.valueOf(t.getNodeNext()).contains(nextNode) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
            List<FlowTaskNodeEntity> flowAll = interflowAll.stream().filter(t -> !FlowNature.AuditCompletion.equals(t.getCompletion())).collect(Collectors.toList());
            if (flowAll.size() > 0) {
                isNext = false;
                break;
            }
        }
        return isNext;
    }

    /**
     * 修改节点走向
     */
    public void auditTaskNode(FlowTemplateAllModel templateAllModel, FlowTaskEntity flowTask, FlowTaskNodeEntity flowTaskNode, FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        List<Integer> completion = new ArrayList() {{
            add(FlowNodeEnum.Process.getCode());
        }};
        List<FlowTaskNodeEntity> flowTaskNodeListAll = flowTaskNodeService.getList(flowTask.getId()).stream().filter(t -> completion.contains(t.getCompletion()) && !FlowNature.NodeEnd.equals(t.getNodeCode())).collect(Collectors.toList());
        //更新数据
        flowModel.setProcessId(flowTask.getId());
        flowModel.setId(flowTask.getId());
        //更新新流程
        ChildNode childNodeAll = JsonUtil.getJsonToBean(flowTask.getFlowTemplateJson(), ChildNode.class);
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        List<FlowTaskNodeEntity> taskNodeLisAll = new ArrayList<>();
        //审批哪个节点获取哪个节点表单数据,当作当前节点的表单数据
        String formId = flowTaskNode.getFormId();
        String taskId = flowTaskNode.getTaskId();
        Map<String, Object> formData = this.infoData(formId, taskId);
        flowTask.setFlowFormContentJson(JsonUtil.getObjectToString(formData));
        FlowUpdateNode updateNode = FlowUpdateNode.builder().childNodeAll(childNodeAll).nodeListAll(nodeListAll).taskNodeList(taskNodeLisAll).conditionListAll(conditionListAll).flowTask(flowTask).userInfo(userInfo).build();
        this.updateNodeList(updateNode);
        if (flowTaskNode != null) {
            FlowNodeListModel nodeListModel = new FlowNodeListModel(taskNodeLisAll, flowModel, false, flowTaskNode, flowTaskNode.getSortCode());
            this.nodeListAll(nodeListModel);
        }
        //修改节点的id
        taskNodeLisAll.stream().forEach(k -> {
            FlowTaskNodeEntity taskNodeEntity = flowTaskNodeListAll.stream().filter(t -> t.getNodeCode().equals(k.getNodeCode())).findFirst().orElse(null);
            if (taskNodeEntity != null) {
                k.setId(taskNodeEntity.getId());
            }
        });
        //递归新节点
        Set<String> rejectNodeList = new HashSet<>();
        List<String> rejectList = new ArrayList() {{
            add(flowTaskNode.getId());
        }};
        this.upAll(rejectNodeList, rejectList, taskNodeLisAll);
        //递归旧节点
        this.upAll(rejectNodeList, rejectList, flowTaskNodeListAll);
        //更新没有走过的节点状态
        List<String> branchList = flowModel.getBranchList() != null ? flowModel.getBranchList() : new ArrayList<>();
        List<FlowTaskNodeEntity> flowTaskNodeList = flowTaskNodeListAll.stream().filter(t -> rejectNodeList.contains(t.getId())).collect(Collectors.toList());
        for (FlowTaskNodeEntity taskNode : flowTaskNodeList) {
            FlowTaskNodeEntity nodeEntity = taskNodeLisAll.stream().filter(t -> t.getNodeCode().equals(taskNode.getNodeCode())).findFirst().orElse(null);
            List<String> candidatesList = branchList.contains(taskNode.getNodeCode()) ? branchList : new ArrayList<>();
            if (nodeEntity != null) {
                taskNode.setNodeNext(nodeEntity.getNodeNext());
                taskNode.setState(nodeEntity.getState());
                taskNode.setSortCode(nodeEntity.getSortCode());
            }
            if (candidatesList.size() > 0) {
                taskNode.setCandidates(JsonUtil.getObjectToString(candidatesList));
            }
            //判断条件下是否选择分支
            ConditionList conditionList = conditionListAll.stream().filter(t -> taskNode.getNodeNext().equals(t.getFlowId())).findFirst().orElse(null);
            if (conditionList != null) {
                ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                nodeModel.getCustom().setFlow(conditionList.getFlow());
                nodeModel.getCustom().setBranchFlow(conditionList.getBranchFlow());
                nodeModel.getCustom().setFlowId(conditionList.getFlowId());
                taskNode.setNodePropertyJson(JsonUtil.getObjectToString(nodeModel));
            }
        }
        flowTaskNodeService.updateTaskNode(flowTaskNodeListAll);
    }

    //---------------拒绝-------------------

    /**
     * 驳回获取节点下所有节点
     *
     * @param rejectNodeList
     * @param rejectList
     * @param taskNodeList
     */
    public void upAll(Set<String> rejectNodeList, List<String> rejectList, List<FlowTaskNodeEntity> taskNodeList) {
        List<FlowTaskNodeEntity> nodeList = taskNodeList.stream().filter(t -> rejectList.contains(t.getId())).collect(Collectors.toList());
        for (FlowTaskNodeEntity taskNode : nodeList) {
            List<String> list = StringUtil.isNotEmpty(taskNode.getNodeNext()) ? Arrays.asList(taskNode.getNodeNext().split(",")) : new ArrayList<>();
            List<FlowTaskNodeEntity> taskList = taskNodeList.stream().filter(t -> list.contains(t.getNodeCode())).collect(Collectors.toList());
            List<String> rejectListAll = taskList.stream().map(t -> t.getId()).collect(Collectors.toList());
            rejectNodeList.add(taskNode.getId());
            upAll(rejectNodeList, rejectListAll, taskNodeList);
        }
    }

    /**
     * 驳回可以驳回的节点数据
     *
     * @param taskNodeList
     * @param nodeIdList
     * @param rejectNodeList
     */
    public void upNodeList(List<FlowTaskNodeEntity> taskNodeList, List<String> nodeIdList, Set<FlowTaskNodeEntity> rejectNodeList, String stopNodeCode) {
        List<FlowTaskNodeEntity> nodeList = taskNodeList.stream().filter(t -> nodeIdList.contains(t.getId())).collect(Collectors.toList());
        for (FlowTaskNodeEntity taskNode : nodeList) {
            List<FlowTaskNodeEntity> taskList = taskNodeList.stream().filter(t -> StringUtil.isNotEmpty(t.getNodeNext()) && t.getNodeNext().contains(taskNode.getNodeCode())).collect(Collectors.toList());
            List<String> rejectListAll = taskList.stream().map(t -> t.getId()).collect(Collectors.toList());
            boolean next = true;
            if (StringUtil.isNotEmpty(stopNodeCode)) {
                next = taskList.stream().filter(t -> t.getNodeCode().equals(stopNodeCode)).count() == 0;
            }
            if (next) {
                rejectNodeList.addAll(taskList);
                upNodeList(taskNodeList, rejectListAll, rejectNodeList, stopNodeCode);
            }
        }
    }

    /**
     * 审批驳回节点
     *
     * @param nodeListAll 所有节点
     * @param taskNode    审批节点
     * @param isReject    是否驳回
     * @param thisStepAll 当前节点
     * @param thisStepId  任务当前节点
     * @return
     */
    public List<FlowTaskNodeEntity> isUpAll(List<FlowTaskNodeEntity> nodeListAll, FlowTaskNodeEntity taskNode, boolean isReject, Set<FlowTaskNodeEntity> thisStepAll, String[] thisStepId) throws WorkFlowException {
        List<FlowTaskNodeEntity> result = new ArrayList<>();
        List<FlowTaskNodeEntity> resultList = new ArrayList<>();
        List<String> thisStepIdAll = new ArrayList<>(Arrays.asList(thisStepId));
        if (isReject) {
            List<String> nodeUpList = Arrays.asList(taskNode.getNodeUp().split(","));
            List<FlowTaskNodeEntity> nodeList = nodeListAll.stream().filter(t -> nodeUpList.contains(t.getNodeCode())).collect(Collectors.toList());
            result.addAll(nodeList);
            Set<String> rejectNodeList = new HashSet<>();
            List<String> reject = nodeList.stream().map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
            this.upAll(rejectNodeList, reject, nodeListAll);
            List<String> delNode = nodeListAll.stream().filter(t -> rejectNodeList.contains(t.getId())).map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList());
            thisStepIdAll.removeAll(delNode);
            thisStepIdAll.addAll(nodeUpList);
            boolean isChild = result.stream().anyMatch(t -> FlowNature.NodeSubFlow.equals(t.getNodeType()));
            if (isChild) {
                throw new WorkFlowException(MsgCode.WF114.get());
            }
            resultList.addAll(result);
            boolean start = result.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).count() > 0;
            if (!start) {
                List<FlowTaskOperatorEntity> taskOperatorList = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
                List<String> taskNodeList = taskOperatorList.stream().map(FlowTaskOperatorEntity::getTaskNodeId).collect(Collectors.toList());
                List<FlowTaskNodeEntity> delNodeList = nodeListAll.stream().filter(t -> !taskNodeList.contains(t.getId())).collect(Collectors.toList());
                result.removeAll(delNodeList);
            }
            if (result.size() == 0) {
                result.addAll(resultList);
            }
        }
        thisStepAll.addAll(nodeListAll.stream().filter(t -> thisStepIdAll.contains(t.getNodeCode())).collect(Collectors.toList()));
        return result;
    }

    //-----------------------子节点---------------------------------

    /**
     * 获取会签是否通过
     *
     * @param countersign
     * @return
     */
    public boolean isRejectCountersign(FlowCountersignModel countersign) {
        String taskNodeId = countersign.getTaskNodeId();
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(taskNodeId);
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        List<FlowTaskOperatorEntity> operatorList = countersign.getOperatorList();
        List<String> state = new ArrayList() {{
            add(FlowNodeEnum.Process.getCode());
            add(FlowNodeEnum.FreeApprover.getCode());
        }};
        List<FlowTaskOperatorEntity> operatorListAll = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(taskNode.getId()) && state.contains(t.getState())).collect(Collectors.toList());
        List<String> operatorIdList = new ArrayList<>();
        operatorListAll.stream().forEach(t -> {
            if (FlowNature.ParentId.equals(t.getParentId())) {
                operatorIdList.add(t.getId());
            }
        });
        this.improperApprover(nodeModel, operatorList, operatorIdList);
        boolean isCountersign = this.rejectFixedJointly(operatorList, nodeModel, countersign);
        return isCountersign;
    }

    /**
     * 判断会签比例
     *
     * @param operatorList
     * @return
     */
    public boolean rejectFixedJointly(List<FlowTaskOperatorEntity> operatorList, ChildNodeList nodeModel, FlowCountersignModel countersign) {
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        boolean isCountersign = true;
        boolean fixed = !FlowNature.FixedApprover.equals(properties.getCounterSign());
        long pass = 100 - properties.getCountersignRatio();
        countersign.setFixed(fixed);
        countersign.setPass(pass);
        if (fixed) {
            double total = operatorList.size();
            List<FlowTaskOperatorEntity> passNumList = this.passNum(operatorList, FlowNature.RejectCompletion);
            countersign.setPassNumList(passNumList);
            double passNum = passNumList.size();
            isCountersign = this.isCountersign(pass, total, passNum);
        }
        return isCountersign;
    }

    /**
     * 判断子流程是否全部走完，进行主流程任务
     *
     * @param flowTask 子流程任务
     * @throws WorkFlowException
     */
    public boolean isNext(FlowTaskEntity flowTask, FlowModel flowModel) throws WorkFlowException {
        //子流程结束，触发主流程
        if (!FlowNature.ParentId.equals(flowTask.getParentId())) {
            FlowTaskEntity parentFlowTask = flowTaskService.getInfo(flowTask.getParentId());
            FlowTaskOperatorEntity parentOperator = new FlowTaskOperatorEntity();
            boolean isAudit = this.updateTaskNode(parentFlowTask, flowTask.getId(), parentOperator, flowModel);
            FlowModel parentModel = new FlowModel();
            parentModel.setIsAsync(true);
            Map<String, Object> data = new HashMap<>(16);
            parentModel.setFormData(data);
            parentModel.setUserInfo(flowModel.getUserInfo());
            parentModel.setVoluntarily(true);
            if (isAudit) {
                if (StringUtil.isNotEmpty(parentFlowTask.getRejectId())) {
                    throw new WorkFlowException(MsgCode.WF129.get());
                }
                flowTaskNewService.audit(parentFlowTask, parentOperator, parentModel);
            }
        }
        return true;
    }

    /**
     * 子节点审批人
     *
     * @param taskChild
     * @return
     */
    public List<UserEntity> childSaveList(TaskChild taskChild) {
        ChildNodeList childNode = taskChild.getChildNode();
        List<FlowTaskNodeEntity> taskNodeListAll = taskChild.getFlowTaskNodeList();
        FlowModel flowModel = taskChild.getFlowModel();
        FlowTaskEntity taskEntity = taskChild.getFlowTask();
        boolean verify = taskChild.getVerify();
        //赋值子流程
        String createUserId = taskEntity.getCreatorUserId();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
        Integer errorRule = properties.getErrorRule();
        String type = properties.getInitiateType();
        List<String> userIdAll = new ArrayList<>();
        Date date = new Date();
        TaskOperatoUser taskOperatoUser = new TaskOperatoUser();
        taskOperatoUser.setDate(date);
        taskOperatoUser.setChildNode(childNode);
        //发起者【部门主管】
        if (FlowTaskOperatorEnum.DepartmentCharge.getCode().equals(type)) {
            UserEntity userEntity = serviceUtil.getUserInfo(createUserId);
            if (userEntity != null) {
                OrganizeEntity organizeEntity = getManageOrgByLevel(userEntity.getOrganizeId(), properties.getDepartmentLevel());
                if (organizeEntity != null) {
                    userIdAll.add(organizeEntity.getManager());
                }
            }
        }
        //发起者【发起者主管】
        if (FlowTaskOperatorEnum.LaunchCharge.getCode().equals(type)) {
            //时时查用户主管
            UserEntity info = serviceUtil.getUserInfo(createUserId);
            if (info != null) {
                userIdAll.add(getManagerByLevel(info.getManagerId(), properties.getManagerLevel()));
            }
        }
        //【发起本人】
        if (FlowTaskOperatorEnum.InitiatorMe.getCode().equals(type)) {
            userIdAll.add(createUserId);
        }
        //【变量】
        if (FlowTaskOperatorEnum.Variate.getCode().equals(type)) {
            FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, false, true);
            Map<String, Object> dataAll = this.createOrUpdate(flowDataModel);
            Object data = dataAll.get(properties.getFormField());
            if (data != null) {
                List<String> list = new ArrayList<>();
                try {
                    list.addAll(JsonUtil.getJsonToList(String.valueOf(data), String.class));
                } catch (Exception e) {

                }
                if (data instanceof List) {
                    list.addAll((List) data);
                } else {
                    list.addAll(Arrays.asList(String.valueOf(data).split(",")));
                }
                List<String> id = new ArrayList<>();
                for (String s : list) {
                    id.add(s.split("--")[0]);
                }
                List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(id);
                List<String> userPosition = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
                List<String> handleIdAll = new ArrayList<>();
                handleIdAll.addAll(userPosition);
                handleIdAll.addAll(id);
                userIdAll.addAll(handleIdAll);
            }
        }
        //【环节】
        if (FlowTaskOperatorEnum.Tache.getCode().equals(type)) {
            List<FlowTaskOperatorRecordEntity> operatorUserList = flowTaskOperatorRecordService.getList(taskEntity.getId()).stream().filter(t -> properties.getNodeId().equals(t.getNodeCode()) && FlowRecordEnum.audit.getCode().equals(t.getHandleStatus()) && FlowNodeEnum.Process.getCode().equals(t.getStatus())).collect(Collectors.toList());
            List<String> handleId = operatorUserList.stream().map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
            userIdAll.addAll(handleId);
        }
        //【服务】
        if (FlowTaskOperatorEnum.Serve.getCode().equals(type)) {
            FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, false, true);
            Map<String, Object> dataAll = this.createOrUpdate(flowDataModel);
            String url = properties.getGetUserUrl() + "?" + taskNodeId + "=" + childNode.getTaskNodeId() + "&" + taskId + "=" + childNode.getTaskId();
            String token = UserProvider.getToken();
            JSONObject object = HttpUtil.httpRequest(url, "POST", JsonUtil.getObjectToString(dataAll), token);
            if (object != null && object.get("data") != null) {
                JSONObject data = object.getJSONObject("data");
                List<String> handleId = StringUtil.isNotEmpty(data.getString("handleId")) ? Arrays.asList(data.getString("handleId").split(",")) : new ArrayList<>();
                userIdAll.addAll(handleId);
            }
        }
        //【候选人】
        if (FlowTaskOperatorEnum.Candidate.getCode().equals(type)) {
            String nodeId = childNode.getTaskNodeId();
            List<FlowCandidatesEntity> candidatesList = flowCandidatesService.getList(nodeId);
            candidatesList.stream().forEach(t -> {
                List<String> candidates = StringUtil.isNotEmpty(t.getCandidates()) ? JsonUtil.getJsonToList(t.getCandidates(), String.class) : new ArrayList<>();
                userIdAll.addAll(candidates);
            });
        }
        //【指定人】
        if (FlowTaskOperatorEnum.Nominator.getCode().equals(type)) {
            userIdAll.addAll(properties.getInitiator());
            List<String> list = new ArrayList<>();
            list.addAll(properties.getInitiatePos());
            list.addAll(properties.getInitiateRole());
            list.addAll(properties.getInitiateOrg());
            list.addAll(properties.getInitiateGroup());
            List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(list);
            List<String> handleId = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
            userIdAll.addAll(handleId);
        }
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
        //异常处理规则
        List<String> errList = new ArrayList<>();
        switch (FlowErrorRuleEnum.getByCode(errorRule)) {
            case administrator:
            case notSubmit:
            case pass:
                errList.add(serviceUtil.getAdmin());
                break;
            case initiator:
                List<UserEntity> errorRuleUser = serviceUtil.getUserName(properties.getErrorRuleUser(), true);
                if (errorRuleUser.size() > 0) {
                    errList.addAll(errorRuleUser.stream().map(UserEntity::getId).collect(Collectors.toList()));
                } else {
                    errList.add(serviceUtil.getAdmin());
                }
                break;
            case creatorUserId:
                errList.add(createUserId);
                break;
            default:
                break;
        }
        if (userList.size() == 0 && verify) {
            userList.addAll(serviceUtil.getUserName(errList, true));
        }
        if (userList.size() == 0 && !verify) {
            List<String> childUserIdAll = new ArrayList<>();
            childUserIdAll.addAll(childNode.getCustom().getTaskId());
            childUserIdAll.addAll(childNode.getCustom().getAsyncTaskList());
            List<String> childList = flowTaskService.getOrderStaList(childUserIdAll).stream().map(FlowTaskEntity::getCreatorUserId).collect(Collectors.toList());
            userList.addAll(serviceUtil.getUserName(childList));
        }
        return userList;
    }

    /**
     * 子流程完成了修改父节点的状态
     */
    public boolean updateTaskNode(FlowTaskEntity parentFlowTask, String taskId, FlowTaskOperatorEntity parentOperator, FlowModel flowModel) {
        List<FlowTaskNodeEntity> flowTaskNodeAll = flowTaskNodeService.getList(parentFlowTask.getId());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeAll.stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        List<FlowTaskNodeEntity> result = new ArrayList<>();
        flowModel.setFreeApproverUserId(null);
        for (FlowTaskNodeEntity nodeEntity : taskNodeList) {
            ChildNodeList parentNode = JsonUtil.getJsonToBean(nodeEntity.getNodePropertyJson(), ChildNodeList.class);
            List<String> taskIdAll = parentNode.getCustom().getTaskId();
            boolean isNum = taskIdAll.contains(taskId);
            boolean isEnd = flowTaskService.getOrderStaList(taskIdAll).stream().filter(t -> !FlowTaskStatusEnum.Adopt.getCode().equals(t.getStatus())).count() == 0;
            if (isNum && isEnd) {
                this.parentOperator(parentOperator, nodeEntity);
                List<FlowTaskNodeEntity> nextNode = new ArrayList<>();
                nextNode.addAll(taskNodeList.stream().filter(t -> nodeEntity.getNodeNext().contains(t.getNodeCode())).collect(Collectors.toList()));
                result.addAll(this.isNextAll(taskNodeList, nextNode, nodeEntity, flowModel));
            }
        }
        return result.size() > 0;
    }

    //----------------------撤回--------------------------

    /**
     * 赋值审批数据
     *
     * @param parentOperator
     * @param nodeEntity
     */
    public void parentOperator(FlowTaskOperatorEntity parentOperator, FlowTaskNodeEntity nodeEntity) {
        parentOperator.setTaskNodeId(nodeEntity.getId());
        parentOperator.setDescription(JsonUtil.getObjectToString(new ArrayList<>()));
        parentOperator.setNodeCode(nodeEntity.getNodeCode());
        parentOperator.setNodeName(nodeEntity.getNodeName());
        parentOperator.setTaskId(nodeEntity.getTaskId());
        parentOperator.setCompletion(FlowNature.ProcessCompletion);
        parentOperator.setSortCode(1L);
    }


    //----------------------变更、复活--------------------------

    /**
     * 修改下一节点
     *
     * @param nodeListAll
     * @param nodeEntity
     * @param revive
     */
    public void change(List<FlowTaskNodeEntity> nodeListAll, FlowTaskNodeEntity nodeEntity, boolean revive, int completion, FlowModel flowModel) {
        if (nodeEntity != null) {
            List<String> next = Arrays.asList(String.valueOf(nodeEntity.getNodeNext()).split(","));
            for (String id : next) {
                FlowTaskNodeEntity nextNode = nodeListAll.stream().filter(t -> t.getNodeCode().equals(id)).findFirst().orElse(null);
                if (nextNode != null) {
                    ChildNodeList nodeModel = JsonUtil.getJsonToBean(nextNode.getNodePropertyJson(), ChildNodeList.class);
                    nextNode.setCompletion(completion);
                    flowTaskNodeService.update(nextNode);
                    if (FlowNature.NodeSubFlow.equals(nextNode.getNodeType())) {
                        if (!revive) {
                            //同步
                            List<String> idAll = nodeModel.getCustom().getTaskId();
                            flowTaskService.deleteChildAll(idAll);
                        }
                    }
                    this.change(nodeListAll, nextNode, revive, completion, flowModel);
                }
            }
        }
    }

    //---------------------公共方法--------------------------

    /**
     * 递归获取加签人
     *
     * @param id
     * @param operatorList
     */
    public void getOperator(String id, Set<FlowTaskOperatorEntity> operatorList) {
        if (StringUtil.isNotEmpty(id)) {
            List<FlowTaskOperatorEntity> operatorListAll = flowTaskOperatorService.getParentId(id);
            for (FlowTaskOperatorEntity operatorEntity : operatorListAll) {
                operatorEntity.setState(FlowNodeEnum.Futility.getCode());
                operatorList.add(operatorEntity);
                this.getOperator(operatorEntity.getId(), operatorList);
            }
        }
    }

    /**
     * 更新当前节点
     *
     * @param nextOperatorList 下一审批节点
     * @param flowTask         流程任务
     */
    public boolean getNextStepId(List<ChildNodeList> nextOperatorList, FlowTaskEntity flowTask, FlowModel flowModel) throws WorkFlowException {
        boolean isEnd = false;
        Set<String> delNodeList = new HashSet<>();
        List<String> progressList = new ArrayList<>();
        List<String> nextOperator = new ArrayList<>();
        ChildNodeList end = nextOperatorList.stream().filter(t -> t.getCustom().getNodeId().contains(FlowNature.NodeEnd)).findFirst().orElse(null);
        List<FlowTaskNodeEntity> flowTaskNodeList = flowTaskNodeService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        for (ChildNodeList childNode : nextOperatorList) {
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            String id = childNode.getCustom().getNodeId();
            String progress = properties.getProgress();
            List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeList.stream().filter(t -> StringUtil.isNotEmpty(t.getNodeNext()) && t.getNodeNext().contains(id)).collect(Collectors.toList());
            List<String> nodeList = taskNodeList.stream().map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList());
            delNodeList.addAll(nodeList);
            boolean isAddNext = flowTaskNodeList.stream().filter(t -> t.getId().equals(childNode.getTaskNodeId()) && FlowNature.ProcessCompletion.equals(t.getCompletion())).count() > 0;
            if (isAddNext) {
                nextOperator.add(id);
            }
            if (StringUtil.isNotEmpty(progress)) {
                progressList.add(progress);
            }
        }
        String[] thisNode = flowTask.getThisStepId() != null ? flowTask.getThisStepId().split(",") : new String[]{};
        Set<String> thisStepId = new HashSet<>();
        for (String id : thisNode) {
            boolean isStepId = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(id) && FlowNature.ProcessCompletion.equals(t.getCompletion())).count() > 0;
            if (isStepId) {
                thisStepId.add(id);
            }
        }
        thisStepId.removeAll(delNodeList);
        thisStepId.addAll(nextOperator);
        List<String> thisNodeName = new ArrayList<>();
        for (String id : thisStepId) {
            List<String> nodeList = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(id)).map(FlowTaskNodeEntity::getNodeName).collect(Collectors.toList());
            thisNodeName.addAll(nodeList);
        }
        flowTask.setThisStepId(String.join(",", thisStepId));
        flowTask.setThisStep(String.join(",", thisNodeName));
        Collections.sort(progressList);
        flowTask.setCompletion(progressList.size() > 0 ? Integer.valueOf(progressList.get(0)) : null);
        if (end != null) {
            isEnd = this.endround(flowTask, end, flowModel);
        }
        return isEnd;
    }

    /**
     * 审核记录
     *
     * @param record         审批实例
     * @param operatordModel 对象数据
     */
    public void operatorRecord(FlowTaskOperatorRecordEntity record, FlowOperatordModel operatordModel) {
        int status = operatordModel.getStatus();
        FlowModel flowModel = operatordModel.getFlowModel();
        String userId = operatordModel.getUserId();
        FlowTaskOperatorEntity operator = operatordModel.getOperator();
        String operatorId = operatordModel.getOperatorId();
        record.setHandleOpinion(flowModel.getHandleOpinion());
        record.setHandleId(userId);
        record.setHandleTime(new Date());
        record.setHandleStatus(status);
        record.setOperatorId(operatorId);
        record.setNodeCode(operator.getNodeCode());
        record.setNodeName(operator.getNodeName() != null ? operator.getNodeName() : "开始");
        record.setTaskOperatorId(operator.getId());
        record.setTaskNodeId(operator.getTaskNodeId());
        record.setTaskId(operator.getTaskId());
        record.setSignImg(flowModel.getSignImg());
        record.setFileList(JsonUtil.getObjectToString(flowModel.getFileList()));
        record.setDraftData(flowModel.getVoluntarily() ? JsonUtil.getObjectToString(new HashMap<>()) : JsonUtil.getObjectToString(flowModel.getFormData()));
        record.setApproverType(flowModel.getFreeApproverType());
        boolean freeApprover = !FlowNature.ParentId.equals(operator.getParentId());
        record.setStatus(freeApprover ? FlowNodeEnum.FreeApprover.getCode() : FlowNodeEnum.Process.getCode());
    }

    /**
     * 定时器
     *
     * @param taskOperator 流程经办
     * @param taskNodeList 所有流程节点
     * @param operatorList 下一流程经办
     * @return
     */
    public List<FlowTaskOperatorEntity> timer(FlowTaskOperatorEntity taskOperator, List<FlowTaskNodeEntity> taskNodeList, List<FlowTaskOperatorEntity> operatorList) {
        List<FlowTaskOperatorEntity> operatorListAll = new ArrayList<>();
        FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(taskOperator.getTaskNodeId())).findFirst().orElse(null);
        if (taskNode != null) {
            //获取其他分流的定时器
            List<String> nodeList = taskNodeList.stream().filter(t -> t.getSortCode().equals(taskNode.getSortCode())).map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
            List<FlowTaskOperatorEntity> operatorAll = flowTaskOperatorService.getList(taskOperator.getTaskId());
            Set<Date> dateListAll = new HashSet<>();
            List<FlowTaskOperatorEntity> list = operatorAll.stream().filter(t -> nodeList.contains(t.getTaskNodeId())).collect(Collectors.toList());
            for (FlowTaskOperatorEntity operator : list) {
                if (StringUtil.isNotEmpty(operator.getDescription())) {
                    List<Date> dateList = JsonUtil.getJsonToList(operator.getDescription(), Date.class);
                    dateListAll.addAll(dateList);
                }
            }
            //获取单前审批定时器
            if (StringUtil.isNotEmpty(taskOperator.getDescription())) {
                List<Date> date = JsonUtil.getJsonToList(taskOperator.getDescription(), Date.class);
                dateListAll.addAll(date);
            }
            for (FlowTaskOperatorEntity operator : operatorList) {
                if (FlowNature.ParentId.equals(operator.getParentId())) {
                    operator.setDescription(JsonUtil.getObjectToString(dateListAll));
                }
                operatorListAll.add(operator);
            }
        }
        return operatorListAll;
    }

    /**
     * 更新节点数据
     */
    public void updateNodeList(FlowUpdateNode flowUpdateNode) {
        FlowTaskEntity flowTask = flowUpdateNode.getFlowTask();
        ChildNode childNodeAll = flowUpdateNode.getChildNodeAll();
        String formId = childNodeAll.getProperties().getFormId();
        List<ChildNodeList> nodeListAll = flowUpdateNode.getNodeListAll();
        List<ConditionList> conditionListAll = flowUpdateNode.getConditionListAll();
        List<FlowTaskNodeEntity> taskNodeList = flowUpdateNode.getTaskNodeList();
        UserInfo userInfo = flowUpdateNode.getUserInfo();
        FlowJsonUtil.getTemplateAll(childNodeAll, nodeListAll, conditionListAll);
        boolean submit = flowUpdateNode.isSubmit();
        for (ChildNodeList childNodeList : nodeListAll) {
            Custom custom = childNodeList.getCustom();
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNodeList.getProperties();
            String childFormId = properties.getFormId();
            if (!FlowNature.NodeSubFlow.equals(custom.getType())) {
                childNodeList.getProperties().setFormId(StringUtil.isNotEmpty(childFormId) ? childFormId : formId);
                childNodeList.getProperties().setErrorRule(childNodeAll.getProperties().getErrorRule());
                childNodeList.getProperties().setErrorRuleUser(childNodeAll.getProperties().getErrorRuleUser());
            }
        }
        this.createNodeList(flowTask, nodeListAll, conditionListAll, taskNodeList, userInfo, submit);
    }

    /**
     * 下一审批人
     *
     * @throws WorkFlowException 异常
     */
    public Map<String, List<String>> nextOperator(FlowOperator flowOperator) throws WorkFlowException {
        List<FlowTaskOperatorEntity> operatorListAll = flowOperator.getOperatorListAll();
        List<ChildNodeList> nodeList = flowOperator.getNodeList();
        FlowTaskEntity flowTask = flowOperator.getFlowTask();
        FlowModel flowModel = flowOperator.getFlowModel();
        Map<String, List<String>> asyncTaskList = flowOperator.getAsyncTaskList();
        Map<String, List<String>> nodeTaskIdList = flowOperator.getNodeTaskIdList();
        UserInfo userInfo = flowOperator.getUserInfo();
        List<FlowTaskNodeEntity> taskNodeListAll = flowOperator.getTaskNodeListAll();
        Map<String, List<String>> taskNode = new HashMap<>(16);
        List<FlowErrorModel> errorList = new ArrayList<>();
        //查询审批人
        for (ChildNodeList childNode : nodeList) {
            //封装查询对象
            TaskOperator taskOperator = new TaskOperator();
            taskOperator.setChildNode(childNode);
            taskOperator.setTaskNodeList(taskNodeListAll);
            taskOperator.setTaskEntity(flowTask);
            taskOperator.setFlowModel(flowModel);
            taskOperator.setExtraRule(true);
            taskOperator.setErrorRule(true);
            taskOperator.setReject(flowOperator.isReject());
            //判断节点属性
            List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
            Custom custom = childNode.getCustom();
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            String type = custom.getType();
            String flowId = properties.getFlowId();
            //判断子流程
            boolean isChild = FlowNature.NodeSubFlow.equals(type);
            if (isChild) {
                //组装子表数据
                FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, false, true);
                Map<String, Object> childDataMap = this.createOrUpdate(flowDataModel);
                childDataMap.put("flowId",flowId);
                //todo 获取子表表单id
                FlowTaskNodeEntity childTaskNode = taskNodeListAll.stream().filter(t -> t.getId().equals(childNode.getTaskNodeId())).findFirst().orElse(null);
                String formId = childTaskNode.getFormId();
                flowModel.setFormData(childDataMap);
                taskOperator.setFlowModel(flowModel);
                List<ChildNodeList> childNodeListAll = new ArrayList<>();
                this.candidate(taskNodeListAll, childNodeListAll, custom.getNodeId(), true);
                int num = 0;
                List<String> branchList = flowModel.getBranchList() != null ? flowModel.getBranchList() : new ArrayList<>();
                for (ChildNodeList childNodeList : childNodeListAll) {
                    String nodeId = childNodeList.getCustom().getNodeId();
                    boolean nextNode = branchList.size() > 0 ? branchList.contains(nodeId) : true;
                    if (!FlowNature.NodeEnd.equals(nodeId) && nextNode) {
                        taskOperator.setChildNode(childNodeList);
                        List<String> userIdAll = this.userListAll(taskOperator);
                        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
                        if (userList.size() == 0) {
                            num += taskOperator.getNotSubmit() > 0 ? 1 : 0;
                            if (taskOperator.getNode() > 0) {
                                FlowErrorModel errorModel = new FlowErrorModel();
                                errorModel.setNodeId(nodeId);
                                errorModel.setNodeName(childNodeList.getProperties().getTitle());
                                errorList.add(errorModel);
                            }
                        }
                    }
                }
                if (num > 0) {
                    throw new WorkFlowException("下一节点无审批人员请联系管理员！");
                }
                //判断当前流程引擎类型
                FlowTemplateAllModel parentTemplateAllModel = templateJson(flowTask.getFlowId());
                List<String> taskNodeList = new ArrayList<>();
                List<String> asyncTaskNodeList = new ArrayList<>();

                //获取子流程引擎
                FlowTemplateAllModel childTemplateAllModel = templateJson(flowId);
                String fullName = childTemplateAllModel.getTemplateJson().getFullName();

                //子节点审批人
                TaskChild taskChild = new TaskChild();
                taskChild.setChildNode(childNode);
                taskChild.setFlowTask(flowTask);
                taskChild.setTemplateAllModel(parentTemplateAllModel);
                taskChild.setFlowModel(flowModel);
                taskChild.setFlowTaskNodeList(taskNodeListAll);
                List<UserEntity> list = this.childSaveList(taskChild);
                errorList.addAll(taskChild.getErrorList());
                if (errorList.size() == 0) {
                    //子流程消息
                    List<FlowTaskNodeEntity> childTaskNodeAll = flowTaskNodeService.getList(flowTask.getId());
                    List<FlowTaskOperatorEntity> childOperatorList = new ArrayList<>();
                    FlowMsgModel flowMsgModel = new FlowMsgModel();
                    flowMsgModel.setNodeList(childTaskNodeAll);
                    flowMsgModel.setData(flowModel.getFormData());
                    flowMsgModel.setWait(false);
                    flowMsgModel.setLaunch(true);
                    boolean isAsync = properties.getIsAsync();
                    FlowTaskNodeEntity taskNodeEntity = flowTaskNodeService.getInfo(childNode.getTaskNodeId());
                    for (UserEntity entity : list) {
                        String title = entity.getRealName() + "的" + fullName + "(子流程)";
                        FlowModel nextFlowModel = new FlowModel();
                        nextFlowModel.setFlowTitle(title);
                        nextFlowModel.setParentId(flowTask.getId());
                        nextFlowModel.setUserId(entity.getId());
                        nextFlowModel.setIsAsync(properties.getIsAsync());
                        nextFlowModel.setFormData(flowModel.getFormData());
                        nextFlowModel.setFlowId(flowId);
                        userInfo.setUserName(entity.getRealName());
                        nextFlowModel.setUserInfo(userInfo);
                        nextFlowModel.setProcessId(RandomUtil.uuId());
                        FlowTaskEntity childTaskEntity = flowTaskNewService.save(nextFlowModel);
                        childNode.setTaskId(childTaskEntity.getId());
                        //新增子表表单数据
                        FlowContextHolder.addChildData(childTaskEntity.getId(), formId, childDataMap);
                        FlowTaskOperatorEntity parentOperator = new FlowTaskOperatorEntity();
                        this.parentOperator(parentOperator, taskNodeEntity);
                        if (!isAsync) {
                            //同步
                            taskNodeList.add(nextFlowModel.getProcessId());
                        } else {
                            asyncTaskNodeList.add(nextFlowModel.getProcessId());
                        }
                        parentOperator.setHandleId(entity.getId());
                        parentOperator.setTaskId(nextFlowModel.getProcessId());
                        childOperatorList.add(parentOperator);
                        //发送子流程消息
                        List<FlowTaskOperatorEntity> launchList = new ArrayList<>();
                        FlowTaskEntity taskEntity = new FlowTaskEntity();
                        taskEntity.setFullName(title);
                        taskEntity.setCreatorUserId(entity.getId());
                        launchList.add(parentOperator);
                        flowMsgModel.setOperatorList(launchList);
                        flowMsgModel.setFlowTemplateAllModel(parentTemplateAllModel);
                        flowMsgModel.setTaskEntity(taskEntity);
                        flowMsgModel.setFlowModel(flowModel);
                        flowMsgUtil.message(flowMsgModel);
                    }
                    //子流程数据整合
                    if (isAsync) {
                        FlowModel parentModel = new FlowModel();
                        parentModel.setUserId("");
                        parentModel.setFormData(flowModel.getFormData());
                        parentModel.setIsAsync(properties.getIsAsync());
                        parentModel.setUserInfo(userInfo);
                        parentModel.setVoluntarily(true);
                        FlowTaskOperatorEntity parentOperator = new FlowTaskOperatorEntity();
                        this.parentOperator(parentOperator, taskNodeEntity);
                        flowTaskNewService.audit(flowTask, parentOperator, parentModel);
                        taskNodeEntity.setCompletion(FlowNature.AuditCompletion);
                        flowTaskNodeService.update(taskNodeEntity);
                    }
                    taskNode.put(childNode.getTaskNodeId(), taskNodeList);
                    asyncTaskList.put(childNode.getTaskNodeId(), asyncTaskNodeList);
                    nodeTaskIdList.put(childNode.getTaskNodeId(), taskNodeList);
                }
            } else {
                if (!FlowNature.NodeEnd.equals(childNode.getCustom().getNodeId())) {
                    List<String> taskIdList = nodeList.stream().map(ChildNodeList::getTaskNodeId).collect(Collectors.toList());
                    List<FlowTaskNodeEntity> flowTaskNodeList = taskNodeListAll.stream().filter(t -> taskIdList.contains(t.getId())).collect(Collectors.toList());
                    List<String> brachListAll = new ArrayList<>();
                    List<String> taskBrachList = new ArrayList<>();
                    flowTaskNodeList.stream().forEach(t -> {
                        List<String> brachList = StringUtil.isNotEmpty(t.getCandidates()) ? JsonUtil.getJsonToList(t.getCandidates(), String.class) : new ArrayList<>();
                        brachListAll.addAll(brachList);
                        if (t.getId().equals(childNode.getTaskNodeId())) {
                            taskBrachList.addAll(brachList);
                        }
                    });
                    boolean isAdd = (brachListAll.size() > 0 && taskBrachList.size() == 0) ? false : true;
                    if (isAdd) {
                        this.operator(operatorList, taskOperator);
                        errorList.addAll(taskOperator.getErrorList());
                    }
                }
            }
            operatorListAll.addAll(operatorList);
        }
        if (errorList.size() > 0) {
            throw new WorkFlowException(200, JsonUtil.getObjectToString(errorList));
        }
        return taskNode;
    }

    /**
     * 审批人
     * taskOperator 对象
     *
     * @param operatorList
     * @param taskOperator
     */
    public List<UserEntity> operator(List<FlowTaskOperatorEntity> operatorList, TaskOperator taskOperator) throws WorkFlowException {
        ChildNodeList childNode = taskOperator.getChildNode();
        FlowTaskEntity flowTask = taskOperator.getTaskEntity();
        FlowModel flowModel = taskOperator.getFlowModel();
        UserInfo userInfo = flowModel.getUserInfo();
        List<FlowTaskNodeEntity> taskNodeList = taskOperator.getTaskNodeList();
        String operatorId = flowModel.getTaskOperatorId();
        String parentId = flowModel.getParentId();
        String rollbackId = flowModel.getRollbackId();
        Boolean rejectUser = flowModel.getRejectUser();
        boolean verify = taskOperator.getVerify();
        boolean reject = taskOperator.getReject();
        List<UserEntity> userAll = new ArrayList<>();
        Date date = new Date();
        List<FlowTaskOperatorEntity> nextList = new ArrayList<>();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
        String freeApproverUserId = flowModel.getFreeApproverUserId();
        TaskOperatoUser taskOperatoUser = new TaskOperatoUser();
        taskOperatoUser.setDate(date);
        taskOperatoUser.setChildNode(childNode);
        boolean isUser = StringUtil.isNotEmpty(freeApproverUserId);
        taskOperatoUser.setId(isUser ? operatorId : FlowNature.ParentId);
        taskOperatoUser.setParentId(parentId);
        taskOperatoUser.setRollbackId(rollbackId);
        taskOperatoUser.setRejectUser(rejectUser);
        FlowTaskNodeEntity taskNodeEntity = flowTaskNodeService.getInfo(childNode.getTaskNodeId());
        int pass = 0;
        int notSubmit = 0;
        int node = 0;
        List<String> userIdAll = new ArrayList<>();
        //【加签】
        if (isUser) {
            userIdAll.add(freeApproverUserId);
            boolean details = taskOperator.getDetails();
            //加签记录
            if (details) {
                Custom custom = childNode.getCustom();
                FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
                FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
                operator.setTaskNodeId(childNode.getTaskNodeId());
                operator.setTaskId(childNode.getTaskId());
                operator.setNodeCode(custom.getNodeId());
                operator.setNodeName(properties.getTitle());
                boolean before = FlowNature.Before.equals(flowModel.getFreeApproverType());
                operator.setId(operatorId);
                //审批数据赋值
                int status = before ? FlowRecordEnum.befoCopyId.getCode() : FlowRecordEnum.copyId.getCode();
                FlowOperatordModel flowOperatordModel = FlowOperatordModel.builder().status(status).flowModel(flowModel).userId(userInfo.getUserId()).operator(operator).operatorId(freeApproverUserId).build();
                this.operatorRecord(operatorRecord, flowOperatordModel);
                if (!FlowNature.Reflux.equals(flowModel.getFreeApproverType())) {
                    flowTaskOperatorRecordService.create(operatorRecord);
                }
            }
        } else {
            userIdAll.addAll(this.userListAll(taskOperator));
            pass = taskOperator.getPass();
            notSubmit = taskOperator.getNotSubmit();
            node = taskOperator.getNode();
        }
        List<UserEntity> userName = serviceUtil.getUserName(userIdAll, true);
        userAll.addAll(userName);
        if (verify) {
            FlowAgreeRuleModel ruleModel = FlowAgreeRuleModel.builder().operatorListAll(nextList).taskOperatoUser(taskOperatoUser).flowTask(flowTask).userName(userName).childNode(childNode).taskNodeList(taskNodeList).reject(reject).build();
            this.flowAgreeRule(ruleModel);
        }
        if (nextList.size() == 0 && verify) {
            boolean isPass = this.pass(taskNodeList, childNode.getCustom().getNodeId(), flowModel, flowTask);
            if (pass > 0) {
                //1.验证下一节点类型
                if (isPass) {
                    FlowModel parentModel = new FlowModel();
                    parentModel.setUserId("0");
                    parentModel.setFormData(flowModel.getFormData());
                    parentModel.setHandleOpinion("默认通过");
                    parentModel.setUserInfo(flowModel.getUserInfo());
                    parentModel.setVoluntarily(true);
                    FlowTaskOperatorEntity parentOperator = new FlowTaskOperatorEntity();
                    parentOperator.setParentId(FlowNature.ParentId);
                    this.parentOperator(parentOperator, taskNodeEntity);
                    flowTaskNewService.audit(flowTask, parentOperator, parentModel);
                } else {
                    taskOperatoUser.setHandLeId(serviceUtil.getAdmin());
                    this.operatorUser(nextList, taskOperatoUser);
                }
            }
            if (notSubmit > 0) {
                throw new WorkFlowException("下一节点无审批人员请联系管理员！");
            }
            if (node > 0) {
                List<FlowErrorModel> errorList = new ArrayList() {{
                    FlowErrorModel errorModel = new FlowErrorModel();
                    errorModel.setNodeId(childNode.getCustom().getNodeId());
                    errorModel.setNodeName(childNode.getProperties().getTitle());
                    add(errorModel);
                }};
                taskOperator.setErrorList(errorList);
            }
        }
        operatorList.addAll(nextList);
        if (userAll.size() == 0 && !verify) {
            List<String> operatorUserList = flowTaskOperatorService.getList(childNode.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState()) && t.getTaskNodeId().equals(childNode.getTaskNodeId())).map(FlowTaskOperatorEntity::getHandleId).collect(Collectors.toList());
            userAll.addAll(serviceUtil.getUserName(operatorUserList));
        }
        return userAll;
    }

    /**
     * 验证自动通过
     *
     * @param ruleModel
     */
    public void flowAgreeRule(FlowAgreeRuleModel ruleModel) {
        FlowTaskEntity flowTask = ruleModel.getFlowTask();
        List<UserEntity> userName = ruleModel.getUserName();
        TaskOperatoUser taskOperatoUser = ruleModel.getTaskOperatoUser();
        List<FlowTaskOperatorEntity> nextList = ruleModel.getOperatorListAll();
        ChildNodeList childNode = ruleModel.getChildNode();
        List<FlowTaskNodeEntity> taskNodeList = ruleModel.getTaskNodeList();
        boolean reject = ruleModel.getReject();
        int sort = 0;
        String creatorUserId = flowTask.getCreatorUserId();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
        List<Integer> statusList = new ArrayList() {{
            add(FlowNodeEnum.Process.getCode());
            add(FlowNodeEnum.FreeApprover.getCode());
        }};
        List<FlowTaskOperatorRecordEntity> recordList = flowTaskOperatorRecordService.getList(flowTask.getId()).stream().filter(t -> FlowRecordEnum.audit.getCode().equals(t.getHandleStatus()) && statusList.contains(t.getStatus())).collect(Collectors.toList());
        for (UserEntity entity : userName) {
            int num = 0;
            if (properties.getHasAgreeRule()) {
                for (Integer rule : properties.getAgreeRules()) {
                    switch (FlowAgreeRuleEnum.getByCode(rule)) {
                        case initiator:
                            num += entity.getId().equals(creatorUserId) ? 1 : 0;
                            break;
                        case node:
                            List<String> nodeList = taskNodeList.stream().filter(t -> StringUtil.isNotEmpty(t.getNodeNext()) && t.getNodeNext().contains(childNode.getCustom().getNodeId())).map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
                            List<String> list = recordList.stream().filter(t -> nodeList.contains(t.getTaskNodeId())).map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
                            num += list.contains(entity.getId()) ? 1 : 0;
                            break;
                        case pass:
                            num += recordList.stream().filter(t -> t.getHandleId().equals(entity.getId())).count() > 0 ? 1 : 0;
                            break;
                        default:
                            break;
                    }
                }
            }
            taskOperatoUser.setHandLeId(entity.getId());
            taskOperatoUser.setAutomation(num > 0 && !reject ? "1" : "");
            taskOperatoUser.setSortCode(++sort);
            this.operatorUser(nextList, taskOperatoUser);
        }
    }

    /**
     * 获取审批人
     *
     * @param taskOperator
     * @return
     */
    public List<String> userListAll(TaskOperator taskOperator) throws WorkFlowException {
        List<String> userIdAll = new ArrayList<>();
        ChildNodeList childNode = taskOperator.getChildNode();
        FlowModel flowModel = taskOperator.getFlowModel();
        List<FlowTaskNodeEntity> taskNodeListAll = taskOperator.getTaskNodeList();
        FlowTaskNodeEntity startNode = taskNodeListAll.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().get();
        ChildNodeList startNodeJson = JsonUtil.getJsonToBean(startNode.getNodePropertyJson(), ChildNodeList.class);
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties startProper = startNodeJson.getProperties();
        Boolean errorRule = taskOperator.getErrorRule();
        int pass = 0;
        int notSubmit = 0;
        int node = 0;
        Boolean extraRule = taskOperator.getExtraRule();
        FlowTaskEntity taskEntity = taskOperator.getTaskEntity();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
        String type = StringUtil.isNotEmpty(properties.getAssigneeType()) ? properties.getAssigneeType() : "";
        String createUserId = taskEntity.getCreatorUserId();
        //发起者【发起者主管】
        if (FlowTaskOperatorEnum.LaunchCharge.getCode().equals(type)) {
            UserEntity info = serviceUtil.getUserInfo(createUserId);
            if (info != null) {
                userIdAll.add(getManagerByLevel(info.getManagerId(), properties.getManagerLevel()));
            }
        }
        //发起者【部门主管】
        if (FlowTaskOperatorEnum.DepartmentCharge.getCode().equals(type)) {
            UserEntity userEntity = serviceUtil.getUserInfo(createUserId);
            if (userEntity != null) {
                OrganizeEntity organizeEntity = getManageOrgByLevel(userEntity.getOrganizeId(), properties.getDepartmentLevel());
                if (organizeEntity != null) {
                    userIdAll.add(organizeEntity.getManager());
                }
            }
        }
        //【发起本人】
        if (FlowTaskOperatorEnum.InitiatorMe.getCode().equals(type)) {
            userIdAll.add(createUserId);
        }
        //【环节】
        if (FlowTaskOperatorEnum.Tache.getCode().equals(type)) {
            List<FlowTaskOperatorRecordEntity> operatorUserList = flowTaskOperatorRecordService.getList(taskEntity.getId()).stream().filter(t -> properties.getNodeId().equals(t.getNodeCode()) && FlowRecordEnum.audit.getCode().equals(t.getHandleStatus()) && FlowNodeEnum.Process.getCode().equals(t.getStatus())).collect(Collectors.toList());
            List<String> handleId = operatorUserList.stream().map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
            userIdAll.addAll(handleId);
        }
        //【变量】
        if (FlowTaskOperatorEnum.Variate.getCode().equals(type)) {
            FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, false, true);
            Map<String, Object> dataAll = this.createOrUpdate(flowDataModel);
            Object data = dataAll.get(properties.getFormField());
            if (data != null) {
                List<String> list = new ArrayList<>();
                try {
                    list.addAll(JsonUtil.getJsonToList(String.valueOf(data), String.class));
                } catch (Exception e) {

                }
                if (data instanceof List) {
                    list.addAll((List) data);
                } else {
                    list.addAll(Arrays.asList(String.valueOf(data).split(",")));
                }
                List<String> id = new ArrayList<>();
                for (String s : list) {
                    id.add(s.split("--")[0]);
                }
                List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(id);
                List<String> userPosition = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
                List<String> handleIdAll = new ArrayList<>();
                handleIdAll.addAll(userPosition);
                handleIdAll.addAll(id);
                userIdAll.addAll(handleIdAll);
            }
        }
        //【服务】
        if (FlowTaskOperatorEnum.Serve.getCode().equals(type)) {
            FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, false, true);
            Map<String, Object> dataAll = this.createOrUpdate(flowDataModel);
            String url = properties.getGetUserUrl() + "?" + taskNodeId + "=" + childNode.getTaskNodeId() + "&" + taskId + "=" + childNode.getTaskId();
            String token = UserProvider.getToken();
            JSONObject object = HttpUtil.httpRequest(url, "POST", JsonUtil.getObjectToString(dataAll), token);
            if (object != null && object.get("data") != null) {
                JSONObject data = object.getJSONObject("data");
                List<String> handleId = StringUtil.isNotEmpty(data.getString("handleId")) ? Arrays.asList(data.getString("handleId").split(",")) : new ArrayList<>();
                userIdAll.addAll(handleId);
            }
        }
        //【候选人】
        if (FlowTaskOperatorEnum.Candidate.getCode().equals(type)) {
            String nodeId = childNode.getTaskNodeId();
            List<FlowCandidatesEntity> candidatesList = flowCandidatesService.getList(nodeId);
            candidatesList.stream().forEach(t -> {
                List<String> candidates = StringUtil.isNotEmpty(t.getCandidates()) ? JsonUtil.getJsonToList(t.getCandidates(), String.class) : new ArrayList<>();
                userIdAll.addAll(candidates);
            });
        }
        //【指定人】
        if (FlowTaskOperatorEnum.Nominator.getCode().equals(type)) {
            userIdAll.addAll(properties.getApprovers());
            List<String> list = new ArrayList<>();
            list.addAll(properties.getApproverPos());
            list.addAll(properties.getApproverRole());
            list.addAll(properties.getApproverOrg());
            list.addAll(properties.getApproverGroup());
            List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(list);
            List<String> userPosition = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
            userIdAll.addAll(userPosition);
        }
        //附加规则
        if (extraRule) {
            this.rule(userIdAll, taskEntity.getId(), properties.getExtraRule());
        }
        //获取最新用户
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
        //异常规则
        if (errorRule && userList.size() == 0) {
            //异常处理规则
            switch (FlowErrorRuleEnum.getByCode(startProper.getErrorRule())) {
                case administrator:
                    userIdAll.add(serviceUtil.getAdmin());
                    break;
                case initiator:
                    List<UserEntity> errorRuleUser = serviceUtil.getUserName(startProper.getErrorRuleUser(), true);
                    if (errorRuleUser.size() > 0) {
                        userIdAll.addAll(errorRuleUser.stream().map(UserEntity::getId).collect(Collectors.toList()));
                    } else {
                        userIdAll.add(serviceUtil.getAdmin());
                    }
                    break;
                case node:
                    String nodeId = childNode.getTaskNodeId();
                    List<FlowCandidatesEntity> candidatesList = flowCandidatesService.getList(nodeId);
                    candidatesList.stream().forEach(t -> {
                        List<String> candidates = StringUtil.isNotEmpty(t.getCandidates()) ? JsonUtil.getJsonToList(t.getCandidates(), String.class) : new ArrayList<>();
                        userIdAll.addAll(candidates);
                    });
                    node++;
                    break;
                case pass:
                    pass++;
                    break;
                case notSubmit:
                    notSubmit++;
                    break;
                default:
                    break;
            }
        }
        taskOperator.setPass(pass);
        taskOperator.setNotSubmit(notSubmit);
        taskOperator.setNode(node);
        return userIdAll;
    }

    /**
     * 递归主管
     *
     * @param managerId 主管id
     * @param level     第几级
     * @return
     */
    public String getManagerByLevel(String managerId, long level) {
        --level;
        if (level == 0) {
            return managerId;
        } else {
            UserEntity userEntity = serviceUtil.getUserInfo(managerId);
            return userEntity != null ? getManagerByLevel(userEntity.getManagerId(), level) : "";
        }
    }

    /**
     * 递归上级部门
     *
     * @param
     * @return
     */
    private OrganizeEntity getManageOrgByLevel(String organizeId, long level) {
        --level;
        if (level == 0) {
            return serviceUtil.getOrganizeInfo(organizeId);
        } else {
            OrganizeEntity organizeInfo = serviceUtil.getOrganizeInfo(organizeId);
            return organizeInfo != null ? getManageOrgByLevel(organizeInfo.getParentId(), level) : null;
        }
    }

    /**
     * 获取代办用户是否自动通过
     *
     * @param flowTask
     * @throws WorkFlowException
     */
    public void approverPass(FlowTaskEntity flowTask, List<FlowTaskNodeEntity> taskNodeList, FlowModel flowModel, FlowTaskOperatorEntity operator) throws WorkFlowException {
        //查询审批记录
        List<FlowTaskOperatorRecordEntity> recordList = flowTaskOperatorRecordService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getStatus()) && FlowRecordEnum.audit.getCode().equals(t.getHandleStatus())).collect(Collectors.toList());
        List<String> recordUserList = recordList.stream().map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
        List<String> taskNodeAll = new ArrayList() {{
            add(operator.getTaskNodeId());
        }};
        List<FlowTaskOperatorEntity> operatorList = flowTaskOperatorService.getList(flowTask.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState()) && FlowNature.ProcessCompletion.equals(t.getCompletion()) && recordUserList.contains(t.getHandleId()) && !taskNodeAll.contains(t.getTaskNodeId())).collect(Collectors.toList());
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        //获取代办节点
        for (String key : operatorMap.keySet()) {
            FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
            if (taskNode != null) {
                List<FlowTaskOperatorEntity> operatorListAll = operatorMap.get(key);
                ChildNodeList childNode = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
                Set<FlowTaskOperatorEntity> flowTaskOperatorList = new HashSet<>();
                if (properties.getHasAgreeRule()) {
                    for (Integer rule : properties.getAgreeRules()) {
                        //判断节点是否审批人审批过
                        switch (FlowAgreeRuleEnum.getByCode(rule)) {
                            case initiator:
                                operatorListAll.stream().forEach(t -> {
                                    t.setAutomation(t.getHandleId().equals(flowTask.getCreatorUserId()) ? "1" : "");
                                });
                                break;
                            case node:
                                List<String> nodeList = taskNodeList.stream().filter(t -> StringUtil.isNotEmpty(t.getNodeNext()) && t.getNodeNext().contains(childNode.getCustom().getNodeId())).map(FlowTaskNodeEntity::getId).collect(Collectors.toList());
                                List<String> list = recordList.stream().filter(t -> FlowRecordEnum.audit.getCode().equals(t.getHandleStatus()) && nodeList.contains(t.getTaskNodeId())).map(FlowTaskOperatorRecordEntity::getHandleId).collect(Collectors.toList());
                                operatorListAll.stream().forEach(t -> {
                                    t.setAutomation(list.contains(t.getHandleId()) ? "1" : "");
                                });
                                break;
                            case pass:
                                operatorListAll.stream().forEach(t -> t.setAutomation("1"));
                                break;
                            default:
                                break;
                        }
                        flowTaskOperatorList.addAll(operatorListAll.stream().filter(t -> StringUtil.isNotEmpty(t.getAutomation())).collect(Collectors.toList()));
                    }
                }
                boolean isApprove = flowTaskOperatorList.size() > 0;
                if (isApprove) {
                    FlowApproveModel approveModel = FlowApproveModel.builder().operatorList(new ArrayList<>(flowTaskOperatorList)).taskNodeList(taskNodeList).flowTask(flowTask).flowModel(flowModel).build();
                    this.approve(approveModel);
                }
            }
        }
    }

    /**
     * 封装审批人
     *
     * @param nextList        所有审批人数据
     * @param taskOperatoUser 对象
     */
    public void operatorUser(List<FlowTaskOperatorEntity> nextList, TaskOperatoUser taskOperatoUser) {
        String handLeId = taskOperatoUser.getHandLeId();
        Date date = taskOperatoUser.getDate();
        ChildNodeList childNode = taskOperatoUser.getChildNode();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
        Custom custom = childNode.getCustom();
        String type = properties.getAssigneeType();
        FlowTaskOperatorEntity operator = new FlowTaskOperatorEntity();
        operator.setId(RandomUtil.uuId());
        operator.setHandleType(taskOperatoUser.getParentId());
        operator.setHandleId(StringUtil.isEmpty(handLeId) ? serviceUtil.getAdmin() : handLeId);
        operator.setTaskNodeId(childNode.getTaskNodeId());
        operator.setTaskId(childNode.getTaskId());
        operator.setNodeCode(custom.getNodeId());
        operator.setNodeName(properties.getTitle());
        operator.setDescription(JsonUtil.getObjectToString(new ArrayList<>()));
        operator.setCreatorTime(date);
        operator.setCompletion(FlowNature.ProcessCompletion);
        operator.setType(type);
        operator.setState(FlowNodeEnum.Process.getCode());
        operator.setParentId(taskOperatoUser.getId());
        operator.setAutomation(taskOperatoUser.getAutomation());
        operator.setSortCode(taskOperatoUser.getSortCode());
        operator.setRollbackId(taskOperatoUser.getRollbackId());
        operator.setReject(taskOperatoUser.getRejectUser() ? "1" : "");
        nextList.add(operator);
    }

    /**
     * 更新经办数据
     *
     * @param operator   当前经办
     * @param handStatus 对象
     */
    public void handleIdStatus(FlowTaskOperatorEntity operator, TaskHandleIdStatus handStatus) {
        int status = handStatus.getStatus();
        ChildNodeList nodeModel = handStatus.getNodeModel();
        FlowModel flowModel = handStatus.getFlowModel();
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        Integer counterSign = properties.getCounterSign();
        operator.setHandleTime(new Date());
        operator.setHandleStatus(status);
        String type = properties.getAssigneeType();
        boolean isApprover = !FlowNature.FixedApprover.equals(counterSign);
        List<String> userIdListAll = new ArrayList<>();
        if (status == 1) {
            boolean hasFreeApprover = StringUtil.isEmpty(flowModel.getFreeApproverUserId());
            if (isApprover) {
                //更新会签都改成完成
                flowTaskOperatorService.update(operator.getTaskNodeId(), userIdListAll, "1");
            } else {
                if (hasFreeApprover) {
                    //更新或签都改成完成
                    flowTaskOperatorService.update(operator.getTaskNodeId(), type);
                }
            }
            operator.setCompletion(FlowNature.AuditCompletion);
            //修改当前审批的定时器
            List<Date> list = JsonUtil.getJsonToList(operator.getDescription(), Date.class);
            DateProperties timer = nodeModel.getTimer();
            if (timer.getTime()) {
                Date date = new Date();
                date = DateUtil.dateAddDays(date, timer.getDay());
                date = DateUtil.dateAddHours(date, timer.getHour());
                date = DateUtil.dateAddMinutes(date, timer.getMinute());
                date = DateUtil.dateAddSeconds(date, timer.getSecond());
                list.add(date);
                operator.setDescription(JsonUtil.getObjectToString(list));
            }
        } else {
            if (isApprover) {
                //更新会签都改成完成
                flowTaskOperatorService.update(operator.getTaskNodeId(), userIdListAll, "-1");
            } else {
                //更新或签都改成完成
                flowTaskOperatorService.update(operator.getTaskNodeId(), type);
            }
            operator.setCompletion(FlowNature.RejectCompletion);
        }
    }

    /**
     * 会签比例
     *
     * @param pass    比例
     * @param total   总数
     * @param passNum 数量
     * @return
     */
    public boolean isCountersign(long pass, double total, double passNum) {
        int scale = (int) (passNum / total * 100);
        return scale >= pass;
    }

    /**
     * 获取通过人数
     *
     * @param operatorList 流程经办数据
     * @return
     */
    public List<FlowTaskOperatorEntity> passNum(List<FlowTaskOperatorEntity> operatorList, Integer completion) {
        List<FlowTaskOperatorEntity> passListAll = operatorList.stream().filter(t -> completion.equals(t.getCompletion())).collect(Collectors.toList());
        return passListAll;
    }

    /**
     * 抄送人
     *
     * @param nodeModel     当前json对象
     * @param circulateList 抄送list
     * @param flowModel     提交数据
     */
    public void circulateList(ChildNodeList nodeModel, List<FlowTaskCirculateEntity> circulateList, FlowModel flowModel, FlowTaskEntity flowTask) {
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        List<String> userIdAll = new ArrayList<>();
        userIdAll.addAll(properties.getCirculateUser());
        List<String> userAll = new ArrayList<>();
        userAll.addAll(properties.getCirculateRole());
        userAll.addAll(properties.getCirculatePosition());
        userAll.addAll(properties.getCirculateGroup());
        userAll.addAll(properties.getCirculateOrg());
        List<UserRelationEntity> listByObjectIdAll = serviceUtil.getListByObjectIdAll(userAll);
        List<String> userPosition = listByObjectIdAll.stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
        userIdAll.addAll(userPosition);
        //附加规则
        this.rule(userIdAll, nodeModel.getTaskId(), properties.getExtraCopyRule());
        //指定传阅人
        String[] copyIds = StringUtil.isNotEmpty(flowModel.getCopyIds()) ? flowModel.getCopyIds().split(",") : new String[]{};
        List<String> id = Arrays.asList(copyIds);
        userIdAll.addAll(id);
        //抄送自己
        if (nodeModel.getProperties().getIsInitiatorCopy()) {
            userIdAll.add(flowTask.getCreatorUserId());
        }
        //获取最新用户
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
        for (UserEntity userEntity : userList) {
            FlowTaskCirculateEntity circulate = new FlowTaskCirculateEntity();
            circulate.setId(RandomUtil.uuId());
            circulate.setObjectId(userEntity.getId());
            circulate.setNodeCode(nodeModel.getCustom().getNodeId());
            circulate.setNodeName(nodeModel.getProperties().getTitle());
            circulate.setTaskNodeId(nodeModel.getTaskNodeId());
            circulate.setTaskId(nodeModel.getTaskId());
            circulate.setCreatorTime(new Date());
            circulateList.add(circulate);
        }
    }

    /**
     * 流程任务结束
     *
     * @param flowTask 流程任务
     */
    public boolean endround(FlowTaskEntity flowTask, ChildNodeList childNode, FlowModel flowModel) throws WorkFlowException {
        flowTask.setStatus(FlowTaskStatusEnum.Adopt.getCode());
        flowTask.setCompletion(100);
        flowTask.setEndTime(DateUtil.getNowDate());
        flowTask.setThisStepId(FlowNature.NodeEnd);
        flowTask.setThisStep("结束");
        //结束事件
        FlowTaskOperatorRecordEntity operatorRecord = new FlowTaskOperatorRecordEntity();
        operatorRecord.setTaskId(flowTask.getId());
        operatorRecord.setHandleStatus(flowTask.getStatus());
        UserInfo userInfo = flowModel.getUserInfo();
        operatorRecord.setHandleId(userInfo.getUserId());
        flowTaskService.update(flowTask);
        flowMsgUtil.event(2, childNode, operatorRecord, flowModel);
        FlowTemplateAllModel templateAllModel = templateJson(flowTask.getFlowId());
        List<FlowTaskNodeEntity> taskNodeList = flowTaskNodeService.getList(flowTask.getId());
        //发送消息
        FlowMsgModel flowMsgModel = new FlowMsgModel();
        flowMsgModel.setEnd(true);
        flowMsgModel.setNodeList(taskNodeList);
        flowMsgModel.setTaskEntity(flowTask);
        FlowTaskNodeEntity taskNodeEntity = new FlowTaskNodeEntity();
        taskNodeEntity.setNodePropertyJson(JsonUtil.getObjectToString(childNode));
        flowMsgModel.setTaskNodeEntity(taskNodeEntity);
        flowMsgModel.setFlowTemplateAllModel(templateAllModel);
        flowMsgModel.setData(JsonUtil.stringToMap(flowTask.getFlowFormContentJson()));
        flowMsgModel.setFlowModel(flowModel);
        flowMsgUtil.message(flowMsgModel);
        //子流程结束，触发主流程
        boolean isEnd = this.isNext(flowTask, flowModel);
        return isEnd;
    }

    /**
     * 更新节点接收时间
     *
     * @param operatorList
     */
    public void taskCreatTime(List<FlowTaskOperatorEntity> operatorList) {
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().filter(t -> FlowNature.ParentId.equals(t.getParentId())).collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        for (String key : operatorMap.keySet()) {
            List<FlowTaskOperatorEntity> list = operatorMap.get(key).stream().sorted(Comparator.comparing(FlowTaskOperatorEntity::getCreatorTime)).collect(Collectors.toList());
            Date date = list.size() > 0 ? list.get(0).getCreatorTime() : null;
            if (date != null) {
                FlowTaskNodeEntity taskNodeEntity = new FlowTaskNodeEntity();
                taskNodeEntity.setId(key);
                taskNodeEntity.setCreatorTime(date);
                flowTaskNodeService.update(taskNodeEntity);
            }
        }
    }

    /**
     * 自动审批
     */
    public void approve(FlowApproveModel approveModel) throws WorkFlowException {
        FlowTaskEntity flowTask = approveModel.getFlowTask();
        boolean rejectId = StringUtil.isEmpty(flowTask.getRejectId());
        if (rejectId) {
            List<FlowTaskOperatorEntity> operatorList = approveModel.getOperatorList();
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            List<FlowTaskNodeEntity> taskNodeList = approveModel.getTaskNodeList();
            FlowModel flowModel = approveModel.getFlowModel();
            List<FlowTaskOperatorEntity> result = new ArrayList<>();
            for (String key : operatorMap.keySet()) {
                FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
                if (taskNode != null) {
                    result.addAll(this.operator(taskNode, taskNodeList, operatorList, flowModel, flowTask));
                }
            }
            for (FlowTaskOperatorEntity entity : result) {
                boolean isFlowTask = StringUtil.isNotEmpty(flowTask.getRejectId()) || FlowTaskStatusEnum.Suspend.getCode().equals(flowTask.getStatus());
                FlowTaskNodeEntity flowTaskNode = flowTaskNodeService.getInfo(entity.getTaskNodeId());
                boolean isTaskNode = FlowNature.ProcessCompletion.equals(flowTaskNode.getCompletion());
                FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(entity.getId());
                boolean isOperator = FlowNature.ProcessCompletion.equals(operator.getCompletion());
                if (!isFlowTask && isTaskNode && isOperator) {
                    UserEntity userEntity = serviceUtil.getUserInfo(operator.getHandleId());
                    UserInfo userInfo = flowModel.getUserInfo();
                    userInfo.setUserId(userEntity.getId());
                    userInfo.setUserAccount(userEntity.getAccount());
                    FlowModel parentModel = JsonUtil.getJsonToBean(flowModel, FlowModel.class);
                    parentModel.setFreeApproverUserId(null);
                    parentModel.setUserId(operator.getHandleId());
                    parentModel.setHandleOpinion("自动审批通过");
                    parentModel.setUserInfo(userInfo);
                    parentModel.setCopyIds(null);
                    parentModel.setIsAsync(false);
                    parentModel.setVoluntarily(true);
                    flowTaskNewService.audit(flowTask, operator, parentModel);
                }
            }
        }
    }

    /**
     * 验证下一节点和会签比例
     *
     * @return
     */
    public List<FlowTaskOperatorEntity> operator(FlowTaskNodeEntity taskNode, List<FlowTaskNodeEntity> taskNodeList, List<FlowTaskOperatorEntity> operatorList, FlowModel flowModel, FlowTaskEntity flowTask) throws WorkFlowException {
        List<FlowTaskOperatorEntity> result = new ArrayList<>();
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        boolean fixed = !FlowNature.FixedApprover.equals(properties.getCounterSign());
        List<FlowTaskOperatorEntity> list = operatorList.stream().filter(t -> StringUtil.isNotEmpty(t.getAutomation())).collect(Collectors.toList());
        List<FlowTaskOperatorEntity> operatorListAll = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(taskNode.getId()) && FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        boolean isPass = this.pass(taskNodeList, taskNode.getNodeCode(), flowModel, flowTask);
        List<String> operatorId = new ArrayList<>();
        for (FlowTaskOperatorEntity operator : list) {
            operatorId.add(operator.getId());
            operatorListAll.stream().forEach(t -> {
                boolean isCompletion = operatorId.contains(t.getId());
                t.setCompletion(isCompletion ? FlowNature.AuditCompletion : t.getCompletion());
            });
            FlowCountersignModel countersign = new FlowCountersignModel();
            countersign.setTaskNodeId(taskNode.getId());
            countersign.setOperatorList(operatorListAll);
            boolean isCountersign = this.isCountersign(countersign);
            if (isPass || (fixed && !isCountersign)) {
                result.add(operator);
            }
        }
        return result;
    }

    /**
     * 判断是否自动通过
     *
     * @param taskNodeList
     * @param nodeCode
     * @return
     */
    public boolean pass(List<FlowTaskNodeEntity> taskNodeList, String nodeCode, FlowModel flowModel, FlowTaskEntity flowtask) throws WorkFlowException {
        boolean result = false;
        int num = 0;
        List<ChildNodeList> childNodeLists = new ArrayList<>();
        List<FlowTaskNodeEntity> thisList = taskNodeList.stream().filter(t -> t.getNodeCode().equals(nodeCode)).collect(Collectors.toList());
        //当前节点
        for (FlowTaskNodeEntity taskNode : thisList) {
            ChildNodeList taskNodechildNodeList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            List<ChildNodeList> childNodeListAll = new ArrayList<>();
            this.candidate(taskNodeList, childNodeListAll, taskNode.getNodeCode(), true);
            for (ChildNodeList childNodeList : childNodeListAll) {
                childNodeLists.add(childNodeList);
                String type = childNodeList.getProperties().getAssigneeType();
                if (taskNodechildNodeList.getCustom().getBranchFlow() || FlowTaskOperatorEnum.Candidate.getCode().equals(type)) {
                    num++;
                }
            }
        }
        if (num == 0) {
            result = this.nextUser(childNodeLists, taskNodeList, flowtask, flowModel);
        }
        return result;
    }

    /**
     * 查询下一节点是否有人
     *
     * @return
     */
    public boolean nextUser(List<ChildNodeList> childNodeLists, List<FlowTaskNodeEntity> taskNodeList, FlowTaskEntity flowTask, FlowModel flowModel) throws WorkFlowException {
        int num = 0;
        FlowTaskNodeEntity start = taskNodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().get();
        ChildNodeList startPrope = JsonUtil.getJsonToBean(start.getNodePropertyJson(), ChildNodeList.class);
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = startPrope.getProperties();
        List<FlowErrorRuleEnum> list = new ArrayList() {{
            add(FlowErrorRuleEnum.notSubmit);
            add(FlowErrorRuleEnum.node);
        }};
        boolean errorRule = list.contains(FlowErrorRuleEnum.getByCode(properties.getErrorRule()));
        for (ChildNodeList childNodeList : childNodeLists) {
            String nodeId = childNodeList.getCustom().getNodeId();
            TaskOperator taskOperator = new TaskOperator();
            taskOperator.setChildNode(childNodeList);
            taskOperator.setTaskEntity(flowTask);
            taskOperator.setFlowModel(flowModel);
            taskOperator.setTaskNodeList(taskNodeList);
            taskOperator.setExtraRule(true);
            List<String> userIdAll = this.userListAll(taskOperator);
            List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
            if (!FlowNature.NodeEnd.equals(nodeId)) {
                num += userList.size() > 0 ? 0 : 1;
            }
        }
        boolean result = (num > 0 && errorRule) ? false : true;
        return result;
    }

    /**
     * 获取会签是否通过
     *
     * @param countersign
     * @return
     */
    public boolean isCountersign(FlowCountersignModel countersign) {
        String taskNodeId = countersign.getTaskNodeId();
        List<FlowTaskOperatorEntity> operatorList = countersign.getOperatorList();
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(taskNodeId);
        ChildNodeList nodeModel = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
        List<String> state = new ArrayList() {{
            add(FlowNodeEnum.Process.getCode());
            add(FlowNodeEnum.FreeApprover.getCode());
        }};
        List<FlowTaskOperatorEntity> operatorListAll = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> t.getTaskNodeId().equals(taskNode.getId()) && state.contains(t.getState())).collect(Collectors.toList());
        List<String> operatorIdList = new ArrayList<>();
        operatorListAll.stream().forEach(t -> {
            if (FlowNature.ParentId.equals(t.getParentId())) {
                operatorIdList.add(t.getId());
            }
        });
        this.improperApprover(nodeModel, operatorList, operatorIdList);
        boolean isCountersign = this.fixedJointly(operatorList, nodeModel, countersign);
        return isCountersign;
    }

    /**
     * 查询依次审批比例
     */
    public void improperApprover(ChildNodeList nodeModel, List<FlowTaskOperatorEntity> operatorList, List<String> operatorIdList) {
        org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = nodeModel.getProperties();
        boolean fixed = FlowNature.ImproperApprover.equals(properties.getCounterSign());
        if (fixed) {
            List<FlowOperatorUserEntity> operatorAllIdList = flowOperatorUserService.getTaskList(null, nodeModel.getTaskNodeId());
            for (FlowOperatorUserEntity entity : operatorAllIdList) {
                if (!operatorIdList.contains(entity.getId())) {
                    FlowTaskOperatorEntity taskOperator = new FlowTaskOperatorEntity();
                    taskOperator.setParentId(FlowNature.ParentId);
                    taskOperator.setHandleId(serviceUtil.getAdmin());
                    taskOperator.setCompletion(FlowNature.ProcessCompletion);
                    operatorList.add(taskOperator);
                }
            }
        }
    }

    /**
     * 过滤依次审批人
     */
    public void improperApproverUser(List<FlowTaskOperatorEntity> operatorListAll, List<FlowTaskNodeEntity> taskNodeList, ChildNodeList nodeModel, FlowTaskOperatorEntity flowOperator) {
        FlowTaskOperatorEntity operatorEntity = new FlowTaskOperatorEntity();
        operatorEntity.setTaskNodeId(nodeModel.getTaskNodeId());
        operatorEntity.setTaskId(nodeModel.getTaskId());
        operatorEntity.setParentId(FlowNature.ParentId);
        Set<Long> operatorIdList = new HashSet<>();
        if (operatorListAll.size() == 0) {
            if (flowOperator != null) {
                List<FlowTaskOperatorEntity> taskOperatorList = flowTaskOperatorService.getList(nodeModel.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
                this.upOperator(flowOperator, taskOperatorList, operatorIdList);
                List<FlowOperatorUserEntity> taskList = flowOperatorUserService.getTaskList(null, nodeModel.getTaskNodeId());
                List<String> operatorUserId = taskList.stream().filter(t -> operatorIdList.contains(t.getSortCode())).map(FlowOperatorUserEntity::getId).collect(Collectors.toList());
                boolean count = taskOperatorList.stream().filter(t -> operatorUserId.contains(t.getId())).count() > 0;
                if (!count) {
                    operatorListAll.add(operatorEntity);
                }
            }
        }
        Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorListAll.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
        //下个节点
        for (String key : operatorMap.keySet()) {
            FlowTaskNodeEntity taskNode = taskNodeList.stream().filter(t -> t.getId().equals(key)).findFirst().orElse(null);
            List<FlowTaskOperatorEntity> list = operatorMap.get(key);
            if (taskNode != null) {
                ChildNodeList childNodeList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
                org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties thisProperties = childNodeList.getProperties();
                boolean thisCounterSign = FlowNature.ImproperApprover.equals(thisProperties.getCounterSign());
                boolean isParentId = list.stream().filter(t -> FlowNature.ParentId.equals(t.getParentId())).count() > 0;
                if (thisCounterSign && isParentId) {
                    List<String> operatorList = flowTaskOperatorService.getList(taskNode.getTaskId()).stream().filter(t -> FlowNature.ParentId.equals(t.getParentId()) && t.getTaskNodeId().equals(taskNode.getId())).map(FlowTaskOperatorEntity::getId).collect(Collectors.toList());
                    List<FlowOperatorUserEntity> taskList = flowOperatorUserService.getTaskList(null, taskNode.getId());
                    List<String> taskUserList = taskList.stream().map(FlowOperatorUserEntity::getId).collect(Collectors.toList());
                    taskUserList.removeAll(operatorList);
                    List<FlowOperatorUserEntity> userList = taskList.stream().filter(t -> taskUserList.contains(t.getId())).sorted(Comparator.comparing(FlowOperatorUserEntity::getSortCode)).collect(Collectors.toList());
                    if (userList.size() > 0) {
                        operatorListAll.removeAll(list);
                        FlowTaskOperatorEntity operator = JsonUtil.getJsonToBean(userList.get(0), FlowTaskOperatorEntity.class);
                        operator.setCreatorTime(new Date());
                        operatorListAll.add(operator);
                    }
                }
            }
        }
        operatorListAll.remove(operatorEntity);
    }

    /**
     * 插入候选人和异常人
     */
    public void candidateList(FlowModel flowModel, List<FlowTaskNodeEntity> taskNodeList, FlowTaskOperatorEntity operator) {
        UserInfo userInfo = flowModel.getUserInfo();
        //新增候选人
        Map<String, List<String>> candidateList = flowModel.getCandidateList() != null ? flowModel.getCandidateList() : new HashMap<>();
        for (String key : candidateList.keySet()) {
            FlowTaskNodeEntity taskNodeEntity = taskNodeList.stream().filter(t -> t.getNodeCode().equals(key)).findFirst().orElse(null);
            if (taskNodeEntity != null) {
                List<String> list = candidateList.get(key);
                FlowCandidatesEntity entity = new FlowCandidatesEntity();
                entity.setHandleId(userInfo.getUserId());
                entity.setTaskId(taskNodeEntity.getTaskId());
                entity.setTaskNodeId(taskNodeEntity.getId());
                entity.setAccount(userInfo.getUserAccount());
                entity.setCandidates(JsonUtil.getObjectToString(list));
                entity.setOperatorId(operator.getId());
                entity.setType(FlowNature.Candidates);
                flowCandidatesService.create(entity);
            }
        }
        //新增异常人
        Map<String, List<String>> candidateErrorList = flowModel.getErrorRuleUserList() != null ? flowModel.getErrorRuleUserList() : new HashMap<>();
        for (String key : candidateErrorList.keySet()) {
            FlowTaskNodeEntity taskNodeEntity = taskNodeList.stream().filter(t -> t.getNodeCode().equals(key)).findFirst().orElse(null);
            if (taskNodeEntity != null) {
                List<String> list = candidateErrorList.get(key);
                FlowCandidatesEntity entity = new FlowCandidatesEntity();
                entity.setHandleId(userInfo.getUserId());
                entity.setTaskId(taskNodeEntity.getTaskId());
                entity.setTaskNodeId(taskNodeEntity.getId());
                entity.setAccount(userInfo.getUserAccount());
                entity.setCandidates(JsonUtil.getObjectToString(list));
                entity.setOperatorId(operator.getId());
                entity.setType(FlowNature.CandidatesError);
                flowCandidatesService.create(entity);
            }
        }
    }

    /**
     * 获取流程节点
     */
    public void nodeList(List<FlowTaskNodeEntity> taskNodeAllList) {
        Set<FlowTaskNodeEntity> delList = new HashSet<>();
        for (FlowTaskNodeEntity taskNode : taskNodeAllList) {
            ChildNodeList childNode = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            if (childNode.getCustom().getFlow() && FlowNature.AuditCompletion.equals(taskNode.getCompletion()) && StringUtil.isNotEmpty(childNode.getCustom().getFlowId())) {
                List<String> flowId = Arrays.asList(childNode.getCustom().getFlowId().split(","));
                List<FlowTaskNodeEntity> nodeListAll = taskNodeAllList.stream().filter(t -> flowId.contains(t.getNodeCode())).collect(Collectors.toList());
                List<FlowTaskNodeEntity> add = new ArrayList<>();
                List<FlowTaskNodeEntity> del = new ArrayList<>();
                for (FlowTaskNodeEntity taskNodeEntity : nodeListAll) {
                    boolean branchFlow = JsonUtil.getJsonToList(taskNodeEntity.getCandidates(), String.class).size() > 0;
                    Set<String> rejectNodeList = new HashSet<>();
                    List<String> rejectList = new ArrayList() {{
                        add(taskNodeEntity.getId());
                    }};
                    this.upAll(rejectNodeList, rejectList, taskNodeAllList);
                    List<FlowTaskNodeEntity> rejectNodeListAll = taskNodeAllList.stream().filter(t -> rejectNodeList.contains(t.getId())).collect(Collectors.toList());
                    if (branchFlow) {
                        add.addAll(rejectNodeListAll);
                    } else {
                        del.addAll(rejectNodeListAll);
                    }
                }
                if (add.size() > 0) {
                    del.removeAll(add);
                    delList.addAll(del);
                }
            }
        }
        taskNodeAllList.removeAll(delList);
    }

    /**
     * 附加条件
     */
    public void rule(List<String> userIdAll, String taskId, int rule) {
        List<UserEntity> userList = serviceUtil.getUserName(userIdAll, true);
        List<String> userListAll = userList.stream().map(UserEntity::getId).collect(Collectors.toList());
        Map<String, List<UserRelationEntity>> relationUserList = serviceUtil.getListByUserIdAll(userListAll).stream().filter(t->StringUtil.isNotEmpty(t.getObjectId())).collect(Collectors.groupingBy(UserRelationEntity::getObjectId));
        FlowUserEntity flowUser = flowUserService.getTaskInfo(taskId);
        List<String> organize = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> manager = new ArrayList<>();
        List<String> subordinate = new ArrayList<>();
        List<String> department = new ArrayList<>();
        if (flowUser != null) {
            organize.addAll(relationUserList.get(flowUser.getOrganizeId()) != null ? relationUserList.get(flowUser.getOrganizeId()).stream().map(UserRelationEntity::getUserId).collect(Collectors.toList()) : new ArrayList<>());
            position.addAll(relationUserList.get(flowUser.getPositionId()) != null ? relationUserList.get(flowUser.getPositionId()).stream().map(UserRelationEntity::getUserId).collect(Collectors.toList()) : new ArrayList<>());
            manager.add(flowUser.getSuperior());
            subordinate.addAll(StringUtil.isNotEmpty(flowUser.getSubordinate()) ? new ArrayList<>(Arrays.asList(flowUser.getSubordinate().split(","))) : new ArrayList<>());
            List<String> departmentAll = StringUtil.isNotEmpty(flowUser.getDepartment()) ? new ArrayList<>(Arrays.asList(flowUser.getDepartment().split(","))) : new ArrayList<>();
            for (String id : departmentAll) {
                department.addAll(relationUserList.get(id) != null ? relationUserList.get(id).stream().map(UserRelationEntity::getUserId).collect(Collectors.toList()) : new ArrayList<>());
            }
        }
        //附加条件
        switch (FlowExtraRuleEnum.getByCode(rule)) {
            case organize:
                userIdAll.retainAll(organize);
                break;
            case position:
                userIdAll.retainAll(position);
                break;
            case manager:
                userIdAll.retainAll(manager);
                break;
            case subordinate:
                userIdAll.retainAll(subordinate);
                break;
            case department:
                userIdAll.retainAll(department);
                break;
            default:
                break;
        }
    }

    /**
     * 判断审批的初始人
     */
    public void upOperator(FlowTaskOperatorEntity operatorEntity, List<FlowTaskOperatorEntity> operatorList, Set<Long> operatorIdList) {
        List<FlowTaskOperatorEntity> collect = operatorList.stream().filter(t -> t.getId().equals(operatorEntity.getParentId())).collect(Collectors.toList());
        for (FlowTaskOperatorEntity entity : collect) {
            this.upOperator(entity, operatorList, operatorIdList);
        }
        if (FlowNature.ParentId.equals(operatorEntity.getParentId())) {
            operatorIdList.add(operatorEntity.getSortCode() + 1);
        }
    }

    /**
     * 获取流程版本
     *
     * @param id
     * @return
     * @throws WorkFlowException
     */
    public FlowTemplateAllModel templateJson(String id) throws WorkFlowException {
        FlowTemplateJsonEntity templateJson = flowTemplateJsonService.getInfo(id);
        FlowTemplateEntity template = flowTemplateService.getInfo(templateJson.getTemplateId());
        List<FlowTemplateJsonEntity> templateJsonList = new ArrayList() {{
            add(templateJson);
        }};
        FlowTemplateAllModel model = new FlowTemplateAllModel();
        model.setTemplate(template);
        model.setTemplateJson(templateJson);
        model.setTemplateJsonList(templateJsonList);
        return model;
    }

    /**
     * 保存、更新数据
     */
    public Map<String, Object> createOrUpdate(FlowDataModel flowDataModel) {
        ChildNodeList childNodeList = flowDataModel.getChildNodeList();
        List<FlowTaskNodeEntity> taskNodeList = flowDataModel.getTaskNodeList();
        FlowModel flowModel = flowDataModel.getFlowModel();
        boolean voluntarily = flowModel.getVoluntarily();
        boolean isAssign = flowDataModel.getIsAssig();
        boolean isData = flowDataModel.getIsData();
        Map<String, Object> formData = flowModel.getFormData();
        Map<String, Object> data = new HashMap<>();
        Map<String, Map<String, Object>> allData = FlowContextHolder.getAllData();
        //保存数据的表单
        Properties properties = childNodeList.getProperties();
        Custom custom = childNodeList.getCustom();
        String formId = properties.getFormId();
        String taskId = childNodeList.getTaskId();
        List<Integer> handleStatus = new ArrayList() {{
            add(FlowRecordEnum.audit.getCode());
            add(FlowRecordEnum.submit.getCode());
        }};
        //递归
        List<String> nodeIdList = new ArrayList<>();
        this.prevNodeList(taskNodeList, childNodeList, nodeIdList);
        List<FlowTaskOperatorRecordEntity> list = flowTaskOperatorRecordService.getRecordList(taskId, handleStatus).stream().filter(t -> nodeIdList.contains(t.getNodeCode())).sorted(Comparator.comparing(FlowTaskOperatorRecordEntity::getHandleTime).reversed()).collect(Collectors.toList());
        List<String> recordNodeIdList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                recordNodeIdList.add(list.get(i).getNodeCode());
            }
        }
        List<FlowTaskNodeEntity> taskNodeEntity = taskNodeList.stream().filter(t -> nodeIdList.contains(t.getNodeCode()) && FlowNodeEnum.Process.getCode().equals(t.getCompletion())).collect(Collectors.toList());
        if (taskNodeEntity.size() == 0) {
            taskNodeEntity.addAll(taskNodeList.stream().filter(t -> recordNodeIdList.contains(t.getNodeCode())).collect(Collectors.toList()));
        }
        String taskNodeFormId = "";
        String nodeCode = "";
        if (taskNodeEntity.size() > 0) {
            taskNodeFormId = taskNodeEntity.get(0).getFormId();
            nodeCode = taskNodeEntity.get(0).getNodeCode();
        }
        if (StringUtil.isNotEmpty(taskNodeFormId) && isData) {
            Map<String, Object> taskNodeData = this.infoData(taskNodeFormId, taskId);
            allData.put(taskNodeFormId, taskNodeData);
            if (FlowNature.NodeSubFlow.equals(childNodeList.getCustom().getType()) || voluntarily) {
                formData.putAll(taskNodeData);
            }
        }
        String resultNodeCode = nodeCode;
        List<FlowAssignModel> assignList = properties.getAssignList().stream().filter(t -> t.getNodeId().equals(resultNodeCode)).collect(Collectors.toList());
        //获取当前表单
        Map<String, Object> thisNodeData = this.infoData(formId, taskId);
        data.putAll(thisNodeData);
        data.putAll(this.formData(formData, assignList));
        List<String> typeList = new ArrayList() {{
            add(FlowNature.EndRound);
            add(FlowNature.NodeSubFlow);
        }};
        if (isAssign && !typeList.contains(custom.getType())) {
            FlowContextHolder.addData(formId, data);
            FlowContextHolder.addChildData(taskId, formId, data);
        }
        return data;
    }

    /**
     * 表单赋值
     */
    public Map<String, Object> formData(Map<String, Object> formData, List<FlowAssignModel> assignListAll) {
        Map<String, Object> result = new HashMap<>(formData);
        Map<String, Object> oldData = new HashMap<>(formData);
        for (FlowAssignModel flowAssignModel : assignListAll) {
            List<RuleListModel> ruleList = flowAssignModel.getRuleList();
            for (RuleListModel assignMode : ruleList) {
                //子表处理规则
                String parentField = assignMode.getParentField();
                String[] parentFieldList = parentField.split("-");
                String childField = assignMode.getChildField();
                String[] childFieldList = childField.split("-");
                Object childData = this.getSysParamData(oldData,parentField);
                if (childFieldList.length > 1) {
                    List<Map<String, Object>> childMapAll = new ArrayList<>();
                    if (result.get(childFieldList[0]) instanceof List) {
                        List<Map<String, Object>> childList = (List<Map<String, Object>>) result.get(childFieldList[0]);
                        for (Map<String, Object> objectMap : childList) {
                            Map<String, Object> childMap = new HashMap<>(objectMap);
                            childMapAll.add(childMap);
                        }
                    }
                    if (parentFieldList.length > 1) {
                        if (oldData.get(parentFieldList[0]) instanceof List) {
                            List<Map<String, Object>> parentList = (List<Map<String, Object>>) oldData.get(parentFieldList[0]);
                            int num = parentList.size() - childMapAll.size();
                            for (int i = 0; i < num; i++) {
                                childMapAll.add(new HashMap<>());
                            }
                            for (int i = 0; i < parentList.size(); i++) {
                                Map<String, Object> parentMap = parentList.get(i);
                                Map<String, Object> childMap = childMapAll.get(i);
                                childMap.put(childFieldList[1], parentMap.get(parentFieldList[1]));
                            }
                        }
                    } else {
                        if (1 > childMapAll.size()) {
                            childMapAll.add(new HashMap<>());
                        }
                        Map<String, Object> childMap = childMapAll.get(0);
                        childMap.put(childFieldList[1], childData);
                    }
                    result.put(childFieldList[0], childMapAll);
                } else {
                    if (parentFieldList.length > 1) {
                        if (oldData.get(parentFieldList[0]) instanceof List) {
                            List<Map<String, Object>> parentList = (List<Map<String, Object>>) oldData.get(parentFieldList[0]);
                            for (int i = 0; i < parentList.size(); i++) {
                                Map<String, Object> parentMap = parentList.get(i);
                                if (i == 0) {
                                    childData = parentMap.get(parentFieldList[1]);
                                }
                            }
                        }
                    }
                    result.put(childField, childData);
                }

                //副表
                String[] childFieldMastTableList = childField.split("_jnpf_");
                if (childFieldMastTableList.length > 1) {
                    String key = childFieldMastTableList[0].replaceAll("jnpf_", "");
                    if (result.get(key) != null && result.get(key) instanceof Map) {
                        Map<String, Object> objectMap = (Map<String, Object>) result.get(key);
                        objectMap.put(childFieldMastTableList[1], childData);
                        result.put(key, objectMap);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 处理表单值传递
     *
     * @param taskNodeId
     * @param taskNodeListAll
     */
    public void dataAssignment(String taskNodeId, List<FlowTaskNodeEntity> taskNodeListAll, FlowModel flowModel) {
        Set<String> rejectNodeList = new HashSet<>();
        List<String> rejectList = new ArrayList() {{
            add(taskNodeId);
        }};
        this.upAll(rejectNodeList, rejectList, taskNodeListAll);
        for (FlowTaskNodeEntity entity : taskNodeListAll) {
            if (rejectNodeList.contains(entity.getId()) && FlowNature.AuditCompletion.equals(entity.getCompletion())) {
                ChildNodeList childNode = JsonUtil.getJsonToBean(entity.getNodePropertyJson(), ChildNodeList.class);
                FlowDataModel flowDataModel = new FlowDataModel(childNode, taskNodeListAll, flowModel, true, true);
                this.createOrUpdate(flowDataModel);
            }
        }
    }

    /**
     * 查询表单值
     *
     * @return
     */
    public Map<String, Object> infoData(String formId, String taskId) {
        Map<String, Map<String, Object>> allData = FlowContextHolder.getAllData();
        Map<String, Object> dataAll = allData.get(formId) != null ? allData.get(formId) : serviceUtil.infoData(formId, taskId);
        return dataAll;
    }

    /**
     * 获取上级节点
     *
     * @param taskNodeList
     * @param childNodeList
     * @param nodeList
     * @return
     */
    public void prevNodeList(List<FlowTaskNodeEntity> taskNodeList, ChildNodeList childNodeList, List<String> nodeList) {
        List<String> nodeIdList = new ArrayList() {{
            add(childNodeList.getTaskNodeId());
        }};
        Set<FlowTaskNodeEntity> rejectNodeList = new HashSet<>();
        this.upNodeList(taskNodeList, nodeIdList, rejectNodeList, null);
        nodeList.addAll(rejectNodeList.stream().map(FlowTaskNodeEntity::getNodeCode).collect(Collectors.toList()));
    }

    /**
     * 流程挂起和恢复
     */
    /**
     * @param idList
     * @param isSuspend true 挂起  false 恢复
     */
    public List<FlowTaskOperatorEntity> suspend(List<String> idList, boolean isSuspend) {
        List<FlowTaskEntity> orderStaList = flowTaskService.getOrderStaList(idList);
        List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
        for (FlowTaskEntity flowTaskEntity : orderStaList) {
            if (isSuspend) {
                flowTaskEntity.setSuspend(!FlowTaskStatusEnum.Suspend.getCode().equals(flowTaskEntity.getStatus())?flowTaskEntity.getStatus():flowTaskEntity.getSuspend());
                flowTaskEntity.setStatus(FlowTaskStatusEnum.Suspend.getCode());
            } else {
                flowTaskEntity.setStatus(ObjectUtil.isNotEmpty(flowTaskEntity.getSuspend()) ? flowTaskEntity.getSuspend() : flowTaskEntity.getStatus());
                flowTaskEntity.setSuspend(null);
            }
            flowTaskService.update(flowTaskEntity);
            operatorList.addAll(flowTaskOperatorService.getList(flowTaskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList()));
        }
        return operatorList;
    }

    /**
     * 判断任务是否在挂起状态
     *
     * @param flowTask
     */
    public void isSuspend(FlowTaskEntity flowTask) throws WorkFlowException {
        Integer status = flowTask != null ? flowTask.getStatus() : null;
        if (FlowTaskStatusEnum.Suspend.getCode().equals(status)) {
            throw new WorkFlowException("流程处于挂起状态，不可操作");
        }
    }



    /**
     * 处理系统参数：
     *
     * @prevNodeFormId上节点表单id
     */
    private Object getSysParamData(Map<String, Object> formData, String parentField) {
        Object data;
        if ("@prevNodeFormId".equals(parentField)) {
            data = formData.get("id" );
        } else {
            data = formData.get(parentField);
        }
        return data;
    }
}
