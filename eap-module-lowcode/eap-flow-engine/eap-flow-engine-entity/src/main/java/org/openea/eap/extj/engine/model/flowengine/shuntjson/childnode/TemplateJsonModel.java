package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class TemplateJsonModel {

    @Schema(description = "字段")
    public String field;
    @Schema(description = "名称")
    public String fieldName;
    @Schema(description = "字段")
    public String relationField;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "是否子流程")
    private Boolean isSubTable = false;
    @Schema(description = "消息主键")
    private String msgTemplateId;

}
