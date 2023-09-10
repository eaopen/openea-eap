package org.openea.eap.extj.controller.admin.form;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.util.RegexUtils;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.form.entity.LeaveApplyEntity;
import org.openea.eap.extj.form.model.leaveapply.LeaveApplyForm;
import org.openea.eap.extj.form.model.leaveapply.LeaveApplyInfoVO;
import org.openea.eap.extj.form.service.LeaveApplyService;
import org.openea.eap.extj.util.JsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 请假申请
 *
 * 
 */
@Tag(name = "请假申请", description = "LeaveApply")
@RestController
@RequestMapping("/api/workflow/Form/LeaveApply")
public class LeaveApplyController extends SuperController<LeaveApplyService, LeaveApplyEntity> {

    @Autowired
    private LeaveApplyService leaveApplyService;

    /**
     * 获取请假申请信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取请假申请信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult<LeaveApplyInfoVO> info(@PathVariable("id") String id) {
        LeaveApplyEntity entity = leaveApplyService.getInfo(id);
        LeaveApplyInfoVO vo = JsonUtil.getJsonToBean(entity, LeaveApplyInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建请假申请
     *
     * @param leaveApplyForm 表单对象
     * @return
     */
    @Operation(summary = "新建请假申请")
    @PostMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "leaveApplyForm", description = "请假模型", required = true),
    })
    public ActionResult create(@RequestBody @Valid LeaveApplyForm leaveApplyForm, @PathVariable("id") String id) {
        if (leaveApplyForm.getLeaveStartTime() > leaveApplyForm.getLeaveEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveDayCount())) {
            return ActionResult.fail("请假天数只能是0.5的倍数");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveHour())) {
            return ActionResult.fail("请假小时只能是0.5的倍数");
        }
        LeaveApplyEntity entity = JsonUtil.getJsonToBean(leaveApplyForm, LeaveApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(id, entity, leaveApplyForm);
            return ActionResult.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(id, entity, leaveApplyForm);
        return ActionResult.success(MsgCode.SU006.get());
    }

    /**
     * 修改请假申请
     *
     * @param leaveApplyForm 表单对象
     * @param id             主键
     * @return
     */
    @Operation(summary = "修改请假申请")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "leaveApplyForm", description = "请假模型", required = true),
    })
    public ActionResult update(@RequestBody @Valid LeaveApplyForm leaveApplyForm, @PathVariable("id") String id) {
        if (leaveApplyForm.getLeaveStartTime() > leaveApplyForm.getLeaveEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveDayCount())) {
            return ActionResult.fail("请假天数只能是0.5的倍数");
        }
        if (!RegexUtils.checkLeave(leaveApplyForm.getLeaveHour())) {
            return ActionResult.fail("请假小时只能是0.5的倍数");
        }
        LeaveApplyEntity entity = JsonUtil.getJsonToBean(leaveApplyForm, LeaveApplyEntity.class);
        entity.setId(id);
        if (FlowStatusEnum.save.getMessage().equals(leaveApplyForm.getStatus())) {
            leaveApplyService.save(id, entity, leaveApplyForm);
            return ActionResult.success(MsgCode.SU002.get());
        }
        leaveApplyService.submit(id, entity, leaveApplyForm);
        return ActionResult.success(MsgCode.SU006.get());
    }
}
