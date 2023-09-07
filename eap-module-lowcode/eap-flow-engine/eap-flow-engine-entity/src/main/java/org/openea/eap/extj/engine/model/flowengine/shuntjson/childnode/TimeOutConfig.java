package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class TimeOutConfig {
    /**开关**/
    @Schema(description = "开关")
    private Boolean on = false;
    /**数量**/
    @Schema(description = "数量")
    private Integer quantity;
    /**类型 day、 hour、 minute**/
    @Schema(description = "类型")
    private String type;
    /**同意1 拒绝2**/
    @Schema(description = "类型")
    private Integer handler;
}
