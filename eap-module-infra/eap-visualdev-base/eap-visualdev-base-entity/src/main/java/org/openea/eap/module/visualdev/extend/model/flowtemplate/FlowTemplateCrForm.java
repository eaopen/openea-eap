package org.openea.eap.module.visualdev.extend.model.flowtemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowTemplateCrForm {
    @Schema(description = "流程引擎id")
    private String id;
    @Schema(description = "表单id")
    private String formId;
    @Schema(description = "排序码")
    private Long sortCode;
    @Schema(description = "流程编码")
    private String enCode;
    @Schema(description = "流程名称")
    private String fullName;
    @Schema(description = "流程类型")
    private Integer type;
    @Schema(description = "流程分类")
    private String category;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "图标背景色")
    private String iconBackground;
    @Schema(description = "流程模板")
    private String flowTemplateJson = "[]";
    @Schema(description = "描述")
    private String description;
    @Schema(description = "有效标志")
    private Integer enabledMark;
}
