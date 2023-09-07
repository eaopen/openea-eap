package org.openea.eap.extj.portal.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PortalInfoVO extends PortalCrForm {

    private String id;

    @Schema(description = "pc发布标识")
    Integer pcIsRelease;
    @Schema(description = "app发布标识")
    Integer appIsRelease;

}
