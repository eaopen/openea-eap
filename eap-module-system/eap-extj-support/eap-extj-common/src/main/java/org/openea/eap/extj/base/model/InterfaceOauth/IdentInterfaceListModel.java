package org.openea.eap.extj.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IdentInterfaceListModel {

    @Schema(description = "接口认证id")
    private String interfaceIdentId;

    @Schema(description = "接口id")
    private String dataInterfaceIds;
}
