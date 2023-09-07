package org.openea.eap.extj.portal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PortalDataForm {

    @Schema(description = "门户id")
    private String portalId;

    @Schema(description = "PC:网页端 APP:手机端 ")
    private String platform;

    @Schema(description = "PC:网页端 APP:手机端 ")
    private String formData;

}
