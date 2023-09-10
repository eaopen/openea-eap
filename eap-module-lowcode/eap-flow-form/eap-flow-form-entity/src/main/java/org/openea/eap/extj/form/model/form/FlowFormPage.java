package org.openea.eap.extj.form.model.form;


import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程设计
 *
 *
 */
@Schema(description = "表单列表查询参数")
@Data
public class FlowFormPage  extends Pagination {
    private String keyword;
    @Schema(description = "流程类型:0-发起流程，1-功能流程")
    private Integer flowType;
    @Schema(description = "表单类型:1-系统表单，2-自定义表单")
    private Integer formType;
    @Schema(description = "该参数下拉列表无效")
    private Integer enabledMark;
}
