package org.openea.eap.extj.base.model.dataInterface;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 访问接口参数
 *
 */
@Data
@Schema(description = "访问接口参数")
public class DataInterfaceActionModel {
    @Schema(description = "租户id")
    private String tenantId;
    @Schema(description = "认证字符串")
    private String authString;
    @Schema(description = "接口参数")
    private Map<String, String> map;
    @Schema(description = "接口类型")
    private String invokType;
}
