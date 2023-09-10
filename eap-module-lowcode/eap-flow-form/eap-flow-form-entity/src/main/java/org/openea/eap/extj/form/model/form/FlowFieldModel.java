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
@Schema(description = "字段模型")
public class FlowFieldModel {
    /**
     *__vModel__
     */
    @Schema(description = "字段id")
    String filedId;
    /**
     *__config__.label
     */
    @Schema(description = "字段名称")
    String filedName;
    /**
     *__config__.jnpfKey
     */
    @Schema(description = "字段jnpfkey")
    String jnpfKey;
    /**
     *__config__.required
     */
    @Schema(description = "字段是否必填")
    String required;
}
