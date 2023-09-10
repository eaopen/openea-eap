package org.openea.eap.extj.form.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;

/**
 *
 * 表单验证
 *
 */
@Data
@Schema(description = "表单验证模型")
public class FormCheckModel {
	@Schema(description = "名称")
	private String label;
	@Schema(description = "选择值")
	private SelectStatementProvider statementProvider;
}
