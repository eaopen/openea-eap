package org.openea.eapboot.modules.activiti.controller;

import org.openea.eapboot.common.constant.ActivitiConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.utils.PageUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.PageVo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;
import org.openea.eapboot.modules.activiti.entity.ActProcess;
import org.openea.eapboot.modules.activiti.entity.business.Leave;
import org.openea.eapboot.modules.activiti.service.ActBusinessService;
import org.openea.eapboot.modules.activiti.service.ActProcessService;
import org.openea.eapboot.modules.activiti.service.business.LeaveService;
import org.openea.eapboot.modules.activiti.service.mybatis.IActService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 */
@Slf4j
@RestController
@Api(description = "业务申请管理接口")
@RequestMapping("/eapboot/actBusiness")
@Transactional
public class ActBusinessController {

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private IActService iActService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private LeaveService leaveService;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取申请列表")
    public Result<Page<ActBusiness>> getByCondition(@ModelAttribute ActBusiness actBusiness,
                                                    @ModelAttribute SearchVo searchVo,
                                                    @ModelAttribute PageVo pageVo){

        Page<ActBusiness> page = actBusinessService.findByCondition(actBusiness, searchVo, PageUtil.initPage(pageVo));
        page.getContent().forEach(e -> {
            if(StrUtil.isNotBlank(e.getProcDefId())){
                ActProcess actProcess = actProcessService.get(e.getProcDefId());
                e.setRouteName(actProcess.getRouteName());
                e.setProcessName(actProcess.getName());
            }
            if(ActivitiConstant.STATUS_DEALING.equals(e.getStatus())){
                // 关联当前任务
                Task task = taskService.createTaskQuery().processInstanceId(e.getProcInstId()).list().get(0);
                if(task!=null){
                    e.setCurrTaskName(task.getName());
                }
            }
        });
        return new ResultUtil<Page<ActBusiness>>().setData(page);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ApiOperation(value = "提交申请 启动流程")
    public Result<Object> apply(@ModelAttribute ActBusiness act){

        ActBusiness actBusiness = actBusinessService.get(act.getId());
        act.setTableId(actBusiness.getTableId());
        // 根据你的业务需求放入相应流程所需变量
        act = putParams(act);
        String processInstanceId = actProcessService.startProcess(act);
        actBusiness.setProcInstId(processInstanceId);
        actBusiness.setStatus(ActivitiConstant.STATUS_DEALING);
        actBusiness.setResult(ActivitiConstant.RESULT_DEALING);
        actBusiness.setApplyTime(new Date());
        actBusinessService.update(actBusiness);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ApiOperation(value = "流程选择组件启动流程")
    public Result<Object> start(@ModelAttribute ActBusiness act){

        ActBusiness actBusiness = actBusinessService.get(act.getId());
        act.setTableId(actBusiness.getTableId());
        // 根据你的业务需求放入相应流程所需变量
        act = putParams(act);
        String processInstanceId = actProcessService.startProcess(act);
        actBusiness.setProcDefId(act.getProcDefId());
        actBusiness.setTitle(act.getTitle());
        actBusiness.setProcInstId(processInstanceId);
        actBusiness.setStatus(ActivitiConstant.STATUS_DEALING);
        actBusiness.setResult(ActivitiConstant.RESULT_DEALING);
        actBusiness.setApplyTime(new Date());
        actBusinessService.update(actBusiness);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    /**
     * 放入相应流程所需变量
     * @param act
     * @return
     */
    public ActBusiness putParams(ActBusiness act){

        if(StrUtil.isBlank(act.getTableId())){
            throw new EapbootException("关联业务表TableId不能为空");
        }
        // 如果属于请假流程
        Leave leave = leaveService.get(act.getTableId());
        if(leave!=null){
            // 放入变量
            act.getParams().put("duration", leave.getDuration());
        }
        return act;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ApiOperation(value = "撤回申请")
    public Result<Object> cancel(@RequestParam String id,
                                 @RequestParam String procInstId,
                                 @RequestParam(required = false) String reason){

        if(StrUtil.isBlank(reason)){
            reason = "";
        }
        runtimeService.deleteProcessInstance(procInstId, "canceled-"+reason);
        ActBusiness actBusiness = actBusinessService.get(id);
        actBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
        actBusiness.setResult(ActivitiConstant.RESULT_TO_SUBMIT);
        actBusinessService.update(actBusiness);
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除草稿状态申请")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id : ids){
            ActBusiness actBusiness = actBusinessService.get(id);
            if(!ActivitiConstant.STATUS_TO_APPLY.equals(actBusiness.getStatus())){
                return new ResultUtil<Object>().setErrorMsg("删除失败, 仅能删除草稿状态的申请");
            }
            // 删除关联业务表
            ActProcess actProcess = actProcessService.get(actBusiness.getProcDefId());
            iActService.deleteBusiness(actProcess.getBusinessTable(), actBusiness.getTableId());
            actBusinessService.delete(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("删除成功");
    }
}
