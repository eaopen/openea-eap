package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 *
 */
@Data
public class RuleListModel {
    /**
     * 父字段
     **/
    @Schema(description = "父字段")
    private String parentField;
    /**
     * 子字段
     **/
    @Schema(description = "子字段")
    private String childField;
}
