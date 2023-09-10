package org.openea.eap.extj.form.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程表单对象
 *
 *
 */
@Data
@Schema(description = "流程下拉对象")
public class FlowSelectVo {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "表单编码")
    private String enCode;

    @Schema(description = "表单名称")
    private String fullName;

    @Schema(description = "流程类型")
    private Integer flowType;

    @Schema(description = "表单类型")
    private Integer formType;

    @Schema(description = "表单分类")
    private String category;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序码")
    private Long sortCode;

}
