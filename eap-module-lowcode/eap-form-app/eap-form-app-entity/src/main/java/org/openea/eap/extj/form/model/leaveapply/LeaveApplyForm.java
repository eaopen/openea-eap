package org.openea.eap.extj.form.model.leaveapply;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 请假申请
 *
 *
 */
@Data
public class LeaveApplyForm {
    @Schema(description = "相关附件")
    private String fileJson;
    @NotNull(message = "紧急程度不能为空")
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @Schema(description = "请假天数")
    private String leaveDayCount;
    @Schema(description = "请假小时")
    private String leaveHour;
    @NotNull(message = "请假时间不能为空")
    @Schema(description = "请假时间")
    private Long leaveStartTime;
    @Schema(description = "申请职位")
    private String applyPost;
    @Schema(description = "申请人员")
    private String applyUser;
    @Schema(description = "流程标题")
    private String flowTitle;
    @Schema(description = "申请部门")
    private String applyDept;
    @Schema(description = "请假类别")
    private String leaveType;
    @Schema(description = "请假原因")
    private String leaveReason;
    @NotNull(message = "申请日期不能为空")
    @Schema(description = "申请日期")
    private Long applyDate;
    @Schema(description = "流程主键")
    private String flowId;
    @Schema(description = "流程单据")
    private String billNo;
    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private Long leaveEndTime;
    @Schema(description = "提交/保存 0-1")
    private String status;

}
