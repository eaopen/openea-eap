package org.openea.eap.extj.portal.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PortalDefaultDTO {

    @Schema(description = "默认门户ID")
    private String defaultPortalId;

    @Schema(description = "系统ID")
    private String systemId;

    public PortalDefaultDTO(){

    }

}
