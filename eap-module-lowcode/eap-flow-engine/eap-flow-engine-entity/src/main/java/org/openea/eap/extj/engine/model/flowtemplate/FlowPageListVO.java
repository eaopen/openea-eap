package org.openea.eap.extj.engine.model.flowtemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowPageListVO {
    @Schema(description = "流程编码")
    private String enCode;
    @Schema(description = "流程名称")
    private String fullName;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "流程分类")
    private String category;
    @Schema(description = "表单类型")
    private Integer formType;
    @Schema(description = "流程类型")
    private Integer type;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "图标背景色")
    private String iconBackground;
    @Schema(description = "创建人")
    private String creatorUser;
    @Schema(description = "创建时间")
    private Long creatorTime;
    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "是否审批按钮")
    private Integer hasAssistBtn = 0;
}
