package org.openea.eap.module.visualdev.extend.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "表单草稿存储对象模型")
public class FormDraftJsonModel {
    @Schema(description = "草稿json")
    private String draftJson;
    @Schema(description = "表json")
    private String tableJson;
}

