package org.openea.eap.extj.portal.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发布(同步)表单
 *
 */
@Data
@Schema(description="门户创建表单")
public class PortalReleaseForm {

    @Schema(description = "pc标识")
    private Integer pc;
//    @Schema(description = "pc应用集合")
//    private List<String> pcModuleParentId;
    @Schema(description = "pc应用集合")
    private String pcSystemId;
    @Schema(description = "app标识")
    private Integer app;
//    @Schema(description = "app应用集合")
//    private List<String> appModuleParentId;
    @Schema(description = "pc应用集合")
    private String appSystemId;

}
