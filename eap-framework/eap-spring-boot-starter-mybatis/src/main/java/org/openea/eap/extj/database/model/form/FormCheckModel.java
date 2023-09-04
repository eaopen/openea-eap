package org.openea.eap.extj.database.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
@Data
@ApiModel("表单验证模型")
public class FormCheckModel {
    @ApiModelProperty("名称")
    private String label;
    @ApiModelProperty("选择值")
    private SelectStatementProvider statementProvider;
}
