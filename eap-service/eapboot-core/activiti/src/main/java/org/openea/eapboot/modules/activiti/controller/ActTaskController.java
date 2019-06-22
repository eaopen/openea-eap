package org.openea.eapboot.modules.activiti.controller;

import org.openea.eapboot.common.constant.ActivitiConstant;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.common.utils.SnowFlakeUtil;
import org.openea.eapboot.common.vo.PageVo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;
import org.openea.eapboot.modules.activiti.entity.ActProcess;
import org.openea.eapboot.modules.activiti.service.ActBusinessService;
import org.openea.eapboot.modules.activiti.service.ActProcessService;
import org.openea.eapboot.modules.activiti.service.mybatis.IHistoryIdentityService;
import org.openea.eapboot.modules.activiti.utils.MessageUtil;
import org.openea.eapboot.modules.activiti.vo.ActPage;
import org.openea.eapboot.modules.activiti.vo.Assignee;
import org.openea.eapboot.modules.activiti.vo.HistoricTaskVo;
import org.openea.eapboot.modules.activiti.vo.TaskVo;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 */
@Slf4j
@RestController
@Api(description = "任务管理接口")
@RequestMapping("/eapboot/actTask")
@Transactional
public class ActTaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private UserService userService;

    @Autowired
    private IHistoryIdentityService iHistoryIdentityService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private MessageUtil messageUtil;

    @RequestMapping(value = "/todoList", method = RequestMethod.GET)
    @ApiOperation(value = "代办列表")
    public Result<Object> todoList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String categoryId,
                                   @RequestParam(required = false) Integer priority,
                                   @ModelAttribute SearchVo searchVo,
                                   @ModelAttribute PageVo pageVo){

        ActPage<TaskVo> page = new ActPage<TaskVo>();
        List<TaskVo> list = new ArrayList<>();

        String userId = securityUtil.getCurrUser().getId();
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId);

        // 多条件搜索
        if("createTime".equals(pageVo.getSort())&&"asc".equals(pageVo.getOrder())){
            query.orderByTaskCreateTime().asc();
        }else if("priority".equals(pageVo.getSort())&&"asc".equals(pageVo.getOrder())){
            query.orderByTaskPriority().asc();
        }else if("priority".equals(pageVo.getSort())&&"desc".equals(pageVo.getOrder())){
            query.orderByTaskPriority().desc();
        }else{
            query.orderByTaskCreateTime().desc();
        }
        if(StrUtil.isNotBlank(name)){
            query.taskNameLike("%"+name+"%");
        }
        if(StrUtil.isNotBlank(categoryId)){
            query.taskCategory(categoryId);
        }
        if(priority!=null){
            query.taskPriority(priority);
        }
        if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
            Date start = DateUtil.parse(searchVo.getStartDate());
            Date end = DateUtil.parse(searchVo.getEndDate());
            query.taskCreatedAfter(start);
            query.taskCreatedBefore(DateUtil.endOfDay(end));
        }

        page.setTotalElements(query.count());
        int first =  (pageVo.getPageNumber()-1) * pageVo.getPageSize();
        List<Task> taskList = query.listPage(first, pageVo.getPageSize());

        // 转换vo
        taskList.forEach(e -> {
            TaskVo tv = new TaskVo(e);

            // 关联委托人
            if(StrUtil.isNotBlank(tv.getOwner())){
                tv.setOwner(userService.get(tv.getOwner()).getUsername());
            }
            List<IdentityLink> identityLinks = runtimeService.getIdentityLinksForProcessInstance(tv.getProcInstId());
            for(IdentityLink ik : identityLinks){
                // 关联发起人
                if("starter".equals(ik.getType())&&StrUtil.isNotBlank(ik.getUserId())){
                    tv.setApplyer(userService.get(ik.getUserId()).getUsername());
                }
            }
            // 关联流程信息
            ActProcess actProcess = actProcessService.get(tv.getProcDefId());
            if(actProcess!=null){
                tv.setProcessName(actProcess.getName());
                tv.setRouteName(actProcess.getRouteName());
            }
            // 关联业务key
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(tv.getProcInstId()).singleResult();
            tv.setBusinessKey(pi.getBusinessKey());
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            if(actBusiness!=null){
                tv.setTableId(actBusiness.getTableId());
            }

            list.add(tv);
        });
        page.setContent(list);
        return new ResultUtil<Object>().setData(page);
    }

    @RequestMapping(value = "/doneList", method = RequestMethod.GET)
    @ApiOperation(value = "已办列表")
    public Result<Object> doneList(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String categoryId,
                                   @RequestParam(required = false) Integer priority,
                                   @ModelAttribute SearchVo searchVo,
                                   @ModelAttribute PageVo pageVo){

        ActPage<HistoricTaskVo> page = new ActPage<HistoricTaskVo>();
        List<HistoricTaskVo> list = new ArrayList<>();

        String userId = securityUtil.getCurrUser().getId();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().or().taskCandidateUser(userId).
                taskAssignee(userId).endOr().finished();

        // 多条件搜索
        if("createTime".equals(pageVo.getSort())&&"asc".equals(pageVo.getOrder())){
            query.orderByHistoricTaskInstanceEndTime().asc();
        }else if("priority".equals(pageVo.getSort())&&"asc".equals(pageVo.getOrder())){
            query.orderByTaskPriority().asc();
        }else if("priority".equals(pageVo.getSort())&&"desc".equals(pageVo.getOrder())){
            query.orderByTaskPriority().desc();
        }else if("duration".equals(pageVo.getSort())&&"asc".equals(pageVo.getOrder())){
            query.orderByHistoricTaskInstanceDuration().asc();
        }else if("duration".equals(pageVo.getSort())&&"desc".equals(pageVo.getOrder())){
            query.orderByHistoricTaskInstanceDuration().desc();
        }else{
            query.orderByHistoricTaskInstanceEndTime().desc();
        }
        if(StrUtil.isNotBlank(name)){
            query.taskNameLike("%"+name+"%");
        }
        if(StrUtil.isNotBlank(categoryId)){
            query.taskCategory(categoryId);
        }
        if(priority!=null){
            query.taskPriority(priority);
        }
        if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
            Date start = DateUtil.parse(searchVo.getStartDate());
            Date end = DateUtil.parse(searchVo.getEndDate());
            query.taskCompletedAfter(start);
            query.taskCompletedBefore(DateUtil.endOfDay(end));
        }

        page.setTotalElements((long) query.list().size());
        int first =  (pageVo.getPageNumber()-1) * pageVo.getPageSize();
        List<HistoricTaskInstance> taskList = query.listPage(first, pageVo.getPageSize());
        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            // 关联委托人
            if(StrUtil.isNotBlank(htv.getOwner())){
                htv.setOwner(userService.get(htv.getOwner()).getUsername());
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForProcessInstance(htv.getProcInstId());
            for(HistoricIdentityLink hik : identityLinks){
                // 关联发起人
                if("starter".equals(hik.getType())&&StrUtil.isNotBlank(hik.getUserId())){
                    htv.setApplyer(userService.get(hik.getUserId()).getUsername());
                }
            }
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if(comments!=null&&comments.size()>0){
                htv.setComment(comments.get(0).getFullMessage());
            }
            // 关联流程信息
            ActProcess actProcess = actProcessService.get(htv.getProcDefId());
            if(actProcess!=null){
                htv.setProcessName(actProcess.getName());
                htv.setRouteName(actProcess.getRouteName());
            }
            // 关联业务key
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().processInstanceId(htv.getProcInstId()).singleResult();
            htv.setBusinessKey(hpi.getBusinessKey());
            ActBusiness actBusiness = actBusinessService.get(hpi.getBusinessKey());
            if(actBusiness!=null){
                htv.setTableId(actBusiness.getTableId());
            }

            list.add(htv);
        });
        page.setContent(list);
        return new ResultUtil<Object>().setData(page);
    }

    @RequestMapping(value = "/historicFlow/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "流程流转历史")
    public Result<Object> historicFlow(@ApiParam("流程实例id") @PathVariable String id){

        List<HistoricTaskVo> list = new ArrayList<>();

        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(id).orderByHistoricTaskInstanceEndTime().asc().list();

        // 转换vo
        taskList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            List<Assignee> assignees = new ArrayList<>();
            // 关联分配人（委托用户时显示该人）
            if(StrUtil.isNotBlank(htv.getAssignee())){
                String username = userService.get(htv.getAssignee()).getUsername();
                htv.setAssignee(username);
                assignees.add(new Assignee(username, true));
            }
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(e.getId());
            StringBuilder candidateBuilder = new StringBuilder();
            // 获取实际审批用户id
            String userId = iHistoryIdentityService.findUserIdByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE, e.getId());
            for(HistoricIdentityLink hik : identityLinks){
                // 关联候选用户（分配的候选用户审批人）
                if("candidate".equals(hik.getType())&&StrUtil.isNotBlank(hik.getUserId())){
                    String username = userService.get(hik.getUserId()).getUsername();
                    Assignee assignee = new Assignee(username, false);
                    if(StrUtil.isNotBlank(userId)&&userId.equals(hik.getUserId())){
                        assignee.setIsExecutor(true);
                        username+="(实际审批)";
                    }
                    if(StrUtil.isBlank(candidateBuilder.toString())){
                        candidateBuilder.append(username);
                    }else{
                        candidateBuilder.append("、" + username);
                    }
                    assignees.add(assignee);
                }
            }
            if(StrUtil.isBlank(htv.getAssignee())&&StrUtil.isNotBlank(candidateBuilder.toString())){
                htv.setAssignee(candidateBuilder.toString());
                htv.setAssignees(assignees);
            }
            // 关联审批意见
            List<Comment> comments = taskService.getTaskComments(htv.getId(), "comment");
            if(comments!=null&&comments.size()>0){
                htv.setComment(comments.get(0).getFullMessage());
            }
            list.add(htv);
        });
        return new ResultUtil<Object>().setData(list);
    }

    @RequestMapping(value = "/pass", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批通过")
    public Result<Object> pass(@ApiParam("任务id") @RequestParam String id,
                               @ApiParam("流程实例id") @RequestParam String procInstId,
                               @ApiParam("下个节点审批人") @RequestParam(required = false) String[] assignees,
                               @ApiParam("优先级") @RequestParam(required = false) Integer priority,
                               @ApiParam("意见评论") @RequestParam(required = false) String comment,
                               @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                               @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                               @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        if(StrUtil.isNotBlank(task.getOwner())&&!("RESOLVED").equals(task.getDelegationState().toString())){
            // 未解决的委托任务 先resolve
            taskService.resolveTask(id);
        }
        taskService.complete(id);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        // 判断下一个节点
        if(tasks!=null&&tasks.size()>0){
            for(Task t : tasks){
                if(assignees==null||assignees.length<1){
                    // 如果分配审批人为空 设置默认审批用户
                    List<User> users = actProcessService.getNode(t.getTaskDefinitionKey()).getUsers();
                    for(User user : users){
                        taskService.addCandidateUser(t.getId(), user.getId());
                        // 异步发消息
                        messageUtil.sendActMessage(user.getId(), ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                    }
                }
                for(String assignee : assignees){
                    taskService.addCandidateUser(t.getId(), assignee);
                    // 异步发消息
                    messageUtil.sendActMessage(assignee, ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                }
                if(priority!=null){
                    taskService.setPriority(t.getId(), priority);
                }
            }
        } else {
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_PASS);
            actBusinessService.update(actBusiness);
            // 异步发消息
            messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_PASS_CONTENT, sendMessage, sendSms, sendEmail);
        }
        // 记录实际审批人员
        iHistoryIdentityService.insert(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/passAll/{ids}", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过")
    public Result<Object> passAll(@ApiParam("任务ids") @PathVariable String[] ids,
                                  @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                  @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                  @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        for(String id:ids){
            Task task = taskService.createTaskQuery().taskId(id).singleResult();
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            if(StrUtil.isNotBlank(task.getOwner())&&!("RESOLVED").equals(task.getDelegationState().toString())){
                // 未解决的委托任务 先resolve
                taskService.resolveTask(id);
            }
            taskService.complete(id);
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            // 判断下一个节点
            if(tasks!=null&&tasks.size()>0){
                for(Task t : tasks){
                    List<User> users = actProcessService.getNode(t.getTaskDefinitionKey()).getUsers();
                    for(User user : users){
                        taskService.addCandidateUser(t.getId(), user.getId());
                        // 异步发消息
                        messageUtil.sendActMessage(user.getId(), ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                    }
                    taskService.setPriority(t.getId(), task.getPriority());
                }
            } else {
                ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
                actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
                actBusiness.setResult(ActivitiConstant.RESULT_PASS);
                actBusinessService.update(actBusiness);
                // 异步发消息
                messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_PASS_CONTENT, sendMessage, sendSms, sendEmail);
            }
            // 记录实际审批人员
            iHistoryIdentityService.insert(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()),
                    ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, pi.getId());
        }
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/getBackList/{procInstId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取可返回的节点")
    public Result<Object> getBackList(@PathVariable String procInstId){

        List<HistoricTaskVo> list = new ArrayList<>();
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId)
                .finished().list();

        taskInstanceList.forEach(e -> {
            HistoricTaskVo htv = new HistoricTaskVo(e);
            list.add(htv);
        });

        // 去重
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        List<HistoricTaskVo> newList = new ArrayList<>();
        list.forEach(e->{
            if(set.add(e.getName())){
                newList.add(e);
            }
        });

        return new ResultUtil<Object>().setData(newList);
    }

    @RequestMapping(value = "/backToTask", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批驳回至指定历史节点")
    public Result<Object> backToTask(@ApiParam("任务id") @RequestParam String id,
                                     @ApiParam("驳回指定节点key") @RequestParam String backTaskKey,
                                     @ApiParam("流程实例id") @RequestParam String procInstId,
                                     @ApiParam("流程定义id") @RequestParam String procDefId,
                                     @ApiParam("原节点审批人") @RequestParam(required = false) String[] assignees,
                                     @ApiParam("优先级") @RequestParam(required = false) Integer priority,
                                     @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                     @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                     @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                     @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){


        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(procDefId);
        // 获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(backTaskKey);
        // 实现跳转
        managementService.executeCommand(new JumpTask(procInstId, hisActivity.getId()));
        // 重新分配原节点审批人
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(tasks!=null&&tasks.size()>0){
            tasks.forEach(e->{
                for(String assignee:assignees){
                    taskService.addCandidateUser(e.getId(), assignee);
                    // 异步发消息
                    messageUtil.sendActMessage(assignee, ActivitiConstant.MESSAGE_TODO_CONTENT, sendMessage, sendSms, sendEmail);
                }
                if(priority!=null){
                    taskService.setPriority(e.getId(), priority);
                }
            });
        }
        // 记录实际审批人员
        iHistoryIdentityService.insert(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    public class JumpTask implements Command<ExecutionEntity> {

        private String procInstId;
        private String activityId;

        public JumpTask(String procInstId, String activityId) {
            this.procInstId = procInstId;
            this.activityId = activityId;
        }

        @Override
        public ExecutionEntity execute(CommandContext commandContext) {

            ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(procInstId);
            executionEntity.destroyScope("backed");
            ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
            ActivityImpl activity = processDefinition.findActivity(activityId);
            executionEntity.executeActivity(activity);

            return executionEntity;
        }
    }

    @RequestMapping(value = "/back", method = RequestMethod.POST)
    @ApiOperation(value = "任务节点审批驳回至发起人")
    public Result<Object> back(@ApiParam("任务id") @RequestParam String id,
                               @ApiParam("流程实例id") @RequestParam String procInstId,
                               @ApiParam("意见评论") @RequestParam(required = false) String comment,
                               @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                               @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                               @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){


        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        // 删除流程实例
        runtimeService.deleteProcessInstance(procInstId, "backed");
        ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
        actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
        actBusiness.setResult(ActivitiConstant.RESULT_FAIL);
        actBusinessService.update(actBusiness);
        // 异步发消息
        messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_BACK_CONTENT, sendMessage, sendSms, sendEmail);
        // 记录实际审批人员
        iHistoryIdentityService.insert(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()),
                ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), id, procInstId);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/backAll/{procInstIds}", method = RequestMethod.POST)
    @ApiOperation(value = "批量驳回至发起人")
    public Result<Object> backAll(@ApiParam("流程实例ids") @PathVariable String[] procInstIds,
                                  @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                  @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                  @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        for(String procInstId:procInstIds){
            // 记录实际审批人员
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
            tasks.forEach(t->{
                iHistoryIdentityService.insert(String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId()),
                        ActivitiConstant.EXECUTOR_TYPE, securityUtil.getCurrUser().getId(), t.getId(), procInstId);
            });
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
            // 删除流程实例
            runtimeService.deleteProcessInstance(procInstId, ActivitiConstant.BACKED_FLAG);
            ActBusiness actBusiness = actBusinessService.get(pi.getBusinessKey());
            actBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            actBusiness.setResult(ActivitiConstant.RESULT_FAIL);
            actBusinessService.update(actBusiness);
            // 异步发消息
            messageUtil.sendActMessage(actBusiness.getUserId(), ActivitiConstant.MESSAGE_BACK_CONTENT, sendMessage, sendSms, sendEmail);
        }
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delegate", method = RequestMethod.POST)
    @ApiOperation(value = "委托他人代办")
    public Result<Object> delegate(@ApiParam("任务id") @RequestParam String id,
                                   @ApiParam("委托用户id") @RequestParam String userId,
                                   @ApiParam("流程实例id") @RequestParam String procInstId,
                                   @ApiParam("意见评论") @RequestParam(required = false) String comment,
                                   @ApiParam("是否发送站内消息") @RequestParam(defaultValue = "false") Boolean sendMessage,
                                   @ApiParam("是否发送短信通知") @RequestParam(defaultValue = "false") Boolean sendSms,
                                   @ApiParam("是否发送邮件通知") @RequestParam(defaultValue = "false") Boolean sendEmail){

        if(StrUtil.isBlank(comment)){
            comment = "";
        }
        taskService.addComment(id, procInstId, comment);
        taskService.delegateTask(id, userId);
        taskService.setOwner(id, securityUtil.getCurrUser().getId());
        // 异步发消息
        messageUtil.sendActMessage(userId, ActivitiConstant.MESSAGE_DELEGATE_CONTENT, sendMessage, sendSms, sendEmail);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务")
    public Result<Object> delete(@ApiParam("任务id") @PathVariable String[] ids,
                                 @ApiParam("原因") @RequestParam(required = false) String reason){

        if(StrUtil.isBlank(reason)){
            reason = "";
        }
        for(String id : ids){
            taskService.deleteTask(id, reason);
        }
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/deleteHistoric/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务历史")
    public Result<Object> deleteHistoric(@ApiParam("任务id") @PathVariable String[] ids){

        for(String id : ids){
            historyService.deleteHistoricTaskInstance(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }
}
