package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.entity.*;
import org.openea.eap.extj.engine.enums.FlowNodeEnum;
import org.openea.eap.extj.engine.enums.FlowRecordEnum;
import org.openea.eap.extj.engine.enums.FlowTaskStatusEnum;
import org.openea.eap.extj.engine.model.flowbefore.*;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateUserModel;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateVO;
import org.openea.eap.extj.engine.model.flowcandidate.FlowRejectVO;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.openea.eap.extj.engine.model.flowtask.PaginationFlowTask;
import org.openea.eap.extj.engine.model.flowtask.TaskNodeModel;
import org.openea.eap.extj.engine.service.*;
import org.openea.eap.extj.engine.util.FlowJsonUtil;
import org.openea.eap.extj.engine.util.FlowNature;
import org.openea.eap.extj.engine.util.FlowTaskUtil;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.form.service.FormDataService;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待我审核
 *
 * 
 */
@Tag(name = "待我审核", description = "FlowBefore")
@RestController
@RequestMapping("/api/workflow/Engine/FlowBefore")
public class FlowBeforeController {


    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FlowTaskUtil flowTaskUtil;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskNewService flowTaskNewService;
    @Autowired
    private FormDataService formDataService;

    /**
     * 获取待我审核列表
     *
     * @param category           分类
     * @param paginationFlowTask 分页模型
     * @return
     */
    @Operation(summary = "获取待我审核列表(有带分页)，1-待办事宜，2-已办事宜，3-抄送事宜,4-批量审批")
    @GetMapping("/List/{category}")
    @Parameters({
            @Parameter(name = "category", description = "分类", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate"}, mode = SaMode.OR)
    public ActionResult<PageListVO<FlowBeforeListVO>> list(@PathVariable("category") String category, PaginationFlowTask paginationFlowTask) {
        List<FlowTaskListModel> data = new ArrayList<>();
        if (FlowNature.WAIT.equals(category)) {
            data.addAll(flowTaskService.getWaitList(paginationFlowTask));
        } else if (FlowNature.TRIAL.equals(category)) {
            data.addAll(flowTaskService.getTrialList(paginationFlowTask));
        } else if (FlowNature.CIRCULATE.equals(category)) {
            data.addAll(flowTaskService.getCirculateList(paginationFlowTask));
        } else if (FlowNature.BATCH.equals(category)) {
            paginationFlowTask.setIsBatch(1);
            data.addAll(flowTaskService.getWaitList(paginationFlowTask));
        }
        List<FlowBeforeListVO> listVO = new LinkedList<>();
        List<UserEntity> userList = serviceUtil.getUserName(data.stream().map(FlowTaskListModel::getCreatorUserId).collect(Collectors.toList()));
        boolean isBatch = FlowNature.BATCH.equals(category);
        for (FlowTaskListModel task : data) {
            FlowBeforeListVO vo = JsonUtil.getJsonToBean(task, FlowBeforeListVO.class);
            //用户名称赋值
            UserEntity user = userList.stream().filter(t -> t.getId().equals(vo.getCreatorUserId())).findFirst().orElse(null);
            vo.setUserName(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            if (isBatch) {
                ChildNodeList childNode = JsonUtil.getJsonToBean(vo.getApproversProperties(), ChildNodeList.class);
                vo.setApproversProperties(JsonUtil.getObjectToString(childNode.getProperties()));
            }
            vo.setFlowVersion(StringUtil.isEmpty(vo.getFlowVersion()) ? "" : vo.getFlowVersion());
            listVO.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationFlowTask, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 获取待我审批信息
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取待我审批信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult<FlowBeforeInfoVO> info(@PathVariable("id") String id, FlowModel flowModel) throws WorkFlowException {
        flowModel.setId(id);
        FlowBeforeInfoVO vo = flowTaskNewService.getBeforeInfo(flowModel);
        //处理当前默认值
        if (vo != null && vo.getFlowFormInfo() != null && StringUtil.isNotEmpty(vo.getFlowFormInfo().getPropertyJson()) && vo.getFlowFormInfo().getFormType() == 2) {
            UserInfo userInfo = userProvider.get();
            Map<String, Integer> havaDefaultCurrentValue = new HashMap<>();
            vo.getFlowFormInfo().setPropertyJson(formDataService.setDefaultCurrentValue(vo.getFlowFormInfo().getPropertyJson(), havaDefaultCurrentValue));
        }
        return ActionResult.success(vo);
    }

    /**
     * 待我审核审核
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核审核")
    @PostMapping("/Audit/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult audit(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        if (FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            String rejecttKey = userInfo.getTenantId() + id;
            if (redisUtil.exists(rejecttKey)) {
                throw new WorkFlowException(MsgCode.WF005.get());
            }
            redisUtil.insert(rejecttKey, id, 10);
            flowTaskNewService.auditAll(flowTask, operator, flowModel);
            return ActionResult.success("审核成功");
        } else {
            return ActionResult.fail(MsgCode.WF005.get());
        }
    }

    /**
     * 保存草稿
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "保存草稿")
    @PostMapping("/SaveAudit/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult saveAudit(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(id);
        if (flowTaskOperatorEntity != null) {
            Map<String, Object> formDataAll = flowModel.getFormData();
            flowTaskOperatorEntity.setDraftData(JsonUtil.getObjectToString(formDataAll));
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            return ActionResult.success(MsgCode.SU002.get());
        }
        return ActionResult.fail(MsgCode.FA001.get());
    }

    /**
     * 审批汇总
     *
     * @param id       主键
     * @param category 类型
     * @param type     类型
     * @return
     */
    @Operation(summary = "审批汇总")
    @GetMapping("/RecordList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<List<FlowSummary>> recordList(@PathVariable("id") String id, String category, String type) {
        List<FlowSummary> flowSummaries = flowTaskNewService.recordList(id, category, type);
        return ActionResult.success(flowSummaries);
    }

    /**
     * 待我审核驳回
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核驳回")
    @PostMapping("/Reject/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult reject(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        if (FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            String rejecttKey = userInfo.getTenantId() + id;
            if (redisUtil.exists(rejecttKey)) {
                throw new WorkFlowException(MsgCode.WF112.get());
            }
            redisUtil.insert(rejecttKey, id, 10);
            flowTaskNewService.rejectAll(flowTask, operator, flowModel);
            return ActionResult.success("退回成功");
        } else {
            return ActionResult.fail("已审核完成");
        }
    }

    /**
     * 待我审核转办
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核转办")
    @PostMapping("/Transfer/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult transfer(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        operator.setHandleId(flowModel.getFreeApproverUserId());
        operator.setCreatorTime(new Date());
        flowTaskNewService.transfer(operator, flowModel);
        return ActionResult.success("转办成功");
    }

    /**
     * 待我审核转办
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核加签")
    @PostMapping("/freeApprover/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult freeApprover(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        if (FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            String rejecttKey = userInfo.getTenantId() + id;
            if (redisUtil.exists(rejecttKey)) {
                throw new WorkFlowException(MsgCode.WF005.get());
            }
            redisUtil.insert(rejecttKey, id, 10);
            flowTaskNewService.auditAll(flowTask, operator, flowModel);
        }
        return ActionResult.success("加签成功");
    }

    /**
     * 待我审核撤回审核
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核撤回审核")
    @PostMapping("/Recall/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult recall(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskOperatorRecordEntity operatorRecord = flowTaskOperatorRecordService.getInfo(id);
        List<FlowTaskNodeEntity> nodeList = flowTaskNodeService.getList(operatorRecord.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        FlowTaskNodeEntity taskNode = nodeList.stream().filter(t -> t.getId().equals(operatorRecord.getTaskNodeId())).findFirst().orElse(null);
        if(taskNode == null || FlowRecordEnum.revoke.getCode().equals(operatorRecord.getStatus())){
            return ActionResult.fail("流程已撤回，不能重复操作！");
        }
        if (taskNode != null && !FlowRecordEnum.revoke.getCode().equals(operatorRecord.getStatus())) {
            flowModel.setUserInfo(userProvider.get());
            flowTaskNewService.recall(id, operatorRecord, flowModel);
        }
        return ActionResult.success("撤回成功");
    }

    /**
     * 待我审核终止审核
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "待我审核终止审核")
    @PostMapping("/Cancel/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult cancel(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        if (flowTaskEntity != null) {
            if (flowTaskEntity.getFlowType() == 1) {
                return ActionResult.fail("功能流程不能终止");
            }
            flowModel.setUserInfo(userProvider.get());
            flowTaskNewService.cancel(flowTaskEntity, flowModel);
            return ActionResult.success(MsgCode.SU009.get());
        }
        return ActionResult.fail(MsgCode.FA009.get());
    }

    /**
     * 指派人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "指派人")
    @PostMapping("/Assign/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult assign(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.assign(id, flowModel);
        return ActionResult.success("指派成功");
    }

    /**
     * 获取候选人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取候选人节点")
    @PostMapping("/Candidates/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<FlowCandidateVO> candidates(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        FlowCandidateVO candidate = flowTaskNewService.candidates(id, flowModel, false);
        return ActionResult.success(candidate);
    }

    /**
     * 获取候选人
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "获取候选人")
    @PostMapping("/CandidateUser/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<PageListVO<FlowCandidateUserModel>> candidateUser(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        List<FlowCandidateUserModel> candidate = flowTaskNewService.candidateUser(id, flowModel);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(flowModel, PaginationVO.class);
        return ActionResult.page(candidate, paginationVO);
    }

    /**
     * 批量审批引擎
     *
     * @return
     */
    @Operation(summary = "批量审批引擎")
    @GetMapping("/BatchFlowSelector")
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<List<FlowBatchModel>> batchFlowSelector() {
        List<FlowBatchModel> batchFlowList = flowTaskService.batchFlowSelector();
        return ActionResult.success(batchFlowList);
    }

    /**
     * 拒绝下拉框
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "拒绝下拉框")
    @GetMapping("/RejectList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<FlowRejectVO> rejectList(@PathVariable("id") String id) throws WorkFlowException {
        FlowRejectVO vo = flowTaskNewService.rejectList(id, false);
        return ActionResult.success(vo);
    }

    /**
     * 引擎节点
     *
     * @param id 主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "引擎节点")
    @GetMapping("/NodeSelector/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<List<FlowBatchModel>> nodeSelector(@PathVariable("id") String id) throws WorkFlowException {
        FlowTemplateAllModel template = flowTaskUtil.templateJson(id);
        String templateJson = template.getTemplateJson().getFlowTemplateJson();
        List<FlowBatchModel> batchList = new ArrayList<>();
        ChildNode childNodeAll = JsonUtil.getJsonToBean(templateJson, ChildNode.class);
        //获取流程节点
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        //递归获取条件数据和节点数据
        FlowJsonUtil.getTemplateAll(childNodeAll, nodeListAll, conditionListAll);
        List<String> type = new ArrayList() {{
            add(FlowNature.NodeSubFlow);
            add(FlowNature.NodeStart);
        }};
        for (ChildNodeList childNodeList : nodeListAll) {
            if (!type.contains(childNodeList.getCustom().getType())) {
                FlowBatchModel batchModel = new FlowBatchModel();
                batchModel.setFullName(childNodeList.getProperties().getTitle());
                batchModel.setId(childNodeList.getCustom().getNodeId());
                batchList.add(batchModel);
            }
        }
        return ActionResult.success(batchList);
    }

    /**
     * 流程批量类型下拉
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "流程批量类型下拉")
    @GetMapping("/BatchFlowJsonList/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<List<FlowBatchModel>> batchFlowJsonList(@PathVariable("id") String id) {
        List<String> taskIdList = flowTaskOperatorService.getBatchList().stream().map(FlowTaskOperatorEntity::getTaskId).collect(Collectors.toList());
        List<FlowTaskEntity> taskListAll = flowTaskService.getOrderStaList(taskIdList);
        List<String> flowIdList = taskListAll.stream().filter(t -> t.getTemplateId().equals(id)).map(FlowTaskEntity::getFlowId).collect(Collectors.toList());
        List<FlowTemplateJsonEntity> templateJsonList = flowTemplateJsonService.getTemplateJsonList(flowIdList);
        List<FlowBatchModel> listVO = new ArrayList<>();
        for (FlowTemplateJsonEntity entity : templateJsonList) {
            FlowBatchModel vo = JsonUtil.getJsonToBean(entity, FlowBatchModel.class);
            vo.setFullName(vo.getFullName() + "(v" + entity.getVersion() + ")");
            listVO.add(vo);
        }
        return ActionResult.success(listVO);
    }

    /**
     * 批量审批
     *
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "批量审批")
    @PostMapping("/BatchOperation")
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult batchOperation(@RequestBody FlowModel flowModel) throws WorkFlowException {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.batch(flowModel);
        return ActionResult.success("批量操作完成");
    }

    /**
     * 批量获取候选人
     *
     * @param flowId         流程主键
     * @param taskOperatorId 代办主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "批量获取候选人")
    @GetMapping("/BatchCandidate")
    @Parameters({
            @Parameter(name = "flowId", description = "流程主键", required = true),
            @Parameter(name = "taskOperatorId", description = "代办主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<FlowCandidateVO> batchCandidate(String flowId, String taskOperatorId) throws WorkFlowException {
        FlowModel flowModel = new FlowModel();
        flowModel.setUserInfo(userProvider.get());
        flowModel.setFlowId(flowId);
        FlowCandidateVO candidate = flowTaskNewService.batchCandidates(flowId, taskOperatorId, flowModel);
        return ActionResult.success(candidate);
    }

    /**
     * 消息跳转工作流
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "消息跳转工作流")
    @GetMapping("/{id}/Info")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult taskOperatorId(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskOperatorEntity operator = flowTaskOperatorService.getInfo(id);
        FlowTaskEntity flowTask = flowTaskService.getInfo(operator.getTaskId());
        FlowModel flowModel = new FlowModel();
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.permissions(operator.getHandleId(), flowTask, operator, "", flowModel);
        Map<String, Object> map = new HashMap<>();
        if (!FlowNature.ProcessCompletion.equals(operator.getCompletion())) {
            map.put("isCheck", true);
        } else {
            map.put("isCheck", false);
        }
        List<FlowTaskListModel> data = flowTaskService.getTrialList();
        if (data != null && data.size() > 0) {
            List<String> taskNodeId = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                FlowTaskListModel taskListModel = data.get(i);
                String nodeId = taskListModel.getThisStepId();
                if (taskNodeId.contains(nodeId)) {
                    data.remove(i);
                }
                taskNodeId.add(nodeId);
            }
            FlowTaskListModel taskListModel = data.stream().filter(t -> t.getThisStepId().equals(operator.getTaskNodeId())).findFirst().orElse(null);
            if (taskListModel != null) {
                map.put("flowTaskOperatorRecordId", taskListModel.getId());
            }
        }
        return ActionResult.success(map);
    }

    /**
     * 节点下拉框
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "节点下拉框")
    @GetMapping("/Selector/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<List<TaskNodeModel>> selector(@PathVariable("id") String id) {
        List<String> nodetype = new ArrayList() {{
            add(FlowNature.NodeStart);
            add(FlowNature.NodeSubFlow);
            add(FlowNature.EndRound);
        }};
        List<FlowTaskNodeEntity> list = flowTaskNodeService.getList(id).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        flowTaskUtil.nodeList(list);
        list = list.stream().filter(t -> !nodetype.contains(t.getNodeType())).collect(Collectors.toList());
        List<TaskNodeModel> nodeList = JsonUtil.getJsonToList(list, TaskNodeModel.class);
        return ActionResult.success(nodeList);
    }

    /**
     * 变更或者复活
     *
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "变更或者复活")
    @PostMapping("/Change")
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult change(@RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity info = flowTaskService.getInfo(flowModel.getTaskId());
        if (FlowTaskStatusEnum.Revoke.getCode().equals(info.getStatus()) || FlowTaskStatusEnum.Cancel.getCode().equals(info.getStatus()) || FlowTaskStatusEnum.Draft.getCode().equals(info.getStatus())) {
            throw new WorkFlowException("该流程不能操作");
        }
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.change(flowModel);
        String msg = flowModel.getResurgence() ? "复活成功" : "变更成功";
        return ActionResult.success(msg);
    }

    /**
     * 子流程数据
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "子流程数据")
    @GetMapping("/SubFlowInfo/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult<List<FlowBeforeInfoVO>> subFlowInfo(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskNodeEntity taskNode = flowTaskNodeService.getInfo(id);
        List<FlowBeforeInfoVO> listVO = new ArrayList<>();
        if (taskNode != null) {
            ChildNodeList childNodeList = JsonUtil.getJsonToBean(taskNode.getNodePropertyJson(), ChildNodeList.class);
            List<String> flowTaskIdList = new ArrayList<>();
            flowTaskIdList.addAll(childNodeList.getCustom().getAsyncTaskList());
            flowTaskIdList.addAll(childNodeList.getCustom().getTaskId());
            for (String taskId : flowTaskIdList) {
                FlowModel flowModel = new FlowModel();
                flowModel.setId(taskId);
                FlowBeforeInfoVO vo = flowTaskNewService.getBeforeInfo(flowModel);
                listVO.add(vo);
            }
        }
        return ActionResult.success(listVO);
    }

    /**
     * 流程类型下拉
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "流程类型下拉")
    @GetMapping("/Suspend/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult suspend(@PathVariable("id") String id) {
        List<FlowTaskEntity> childList = flowTaskService.getChildList(id, FlowTaskEntity::getId, FlowTaskEntity::getIsAsync);
        boolean isAsync = childList.stream().filter(t -> FlowNature.ChildAsync.equals(t.getIsAsync())).count() > 0;
        return ActionResult.success(isAsync);
    }

    /**
     * 流程挂起
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "流程挂起")
    @PostMapping("/Suspend/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult suspend(@PathVariable("id") String id, @RequestBody FlowModel flowModel) {
        flowModel.setUserInfo(userProvider.get());
        flowTaskNewService.suspend(id, flowModel, true);
        return ActionResult.success("挂起成功");
    }

    /**
     * 流程恢复
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "流程恢复")
    @PostMapping("/Restore/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult restore(@PathVariable("id") String id, @RequestBody FlowModel flowModel) {
        flowModel.setUserInfo(userProvider.get());
        flowModel.setSuspend(false);
        flowTaskNewService.suspend(id, flowModel, false);
        return ActionResult.success("恢复成功");
    }

}
