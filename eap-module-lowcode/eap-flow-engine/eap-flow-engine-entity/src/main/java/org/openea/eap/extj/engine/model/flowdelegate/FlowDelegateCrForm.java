package org.openea.eap.extj.engine.model.flowdelegate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 *
 *
 */
@Data
public class FlowDelegateCrForm {
    @Schema(description = "委托人名称")
    @NotBlank(message = "必填")
    private String userName;
    @Schema(description = "委托人id")
    @NotBlank(message = "必填")
    private String userId;
    @Schema(description = "被委托人")
    @NotBlank(message = "必填")
    private String toUserName;
    @Schema(description = "被委托人id")
    @NotBlank(message = "必填")
    private String toUserId;
    @Schema(description = "委托类型（0-发起委托，1-审批委托）")
    @NotBlank(message = "必填")
    private String type;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "开始日期")
    @NotNull(message = "必填")
    private Long startTime;
    @Schema(description = "结束日期")
    @NotNull(message = "必填")
    private Long endTime;
    @Schema(description = "委托流程id")
    private String flowId;
    @Schema(description = "委托流程名称")
    @NotBlank(message = "必填")
    private String flowName;
}
