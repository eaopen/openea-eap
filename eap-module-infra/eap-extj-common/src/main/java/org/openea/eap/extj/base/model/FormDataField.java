package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "表单字段")
public class FormDataField {
    @Schema(description = "key")
    private String vModel;
    @Schema(description = "名称")
    private String label;
}
