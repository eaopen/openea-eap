package org.openea.eap.module.visualdev.extend.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

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