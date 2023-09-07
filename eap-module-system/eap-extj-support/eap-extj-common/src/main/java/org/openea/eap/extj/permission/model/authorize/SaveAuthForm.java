package org.openea.eap.extj.permission.model.authorize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class SaveAuthForm {
    @Schema(description = "权限类型")
    private String itemType;
    @Schema(description = "对象类型")
    private String objectType;
    @Schema(description = "对象主键")
    private String[] objectId;
}
