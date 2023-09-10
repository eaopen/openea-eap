package org.openea.eap.extj.form.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流程设计
 *
 *
 */
@Data
@Accessors(chain = true)
@Schema(description = "表单草稿存储对象模型")
public class FormDraftJsonModel {
    @Schema(description = "草稿json")
    private String draftJson;
    @Schema(description = "表json")
    private String tableJson;
}
