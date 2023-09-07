package org.openea.eap.extj.portal.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description="门户修改表单")
public class PortalUpForm extends PortalCrForm {

    @Schema(description = "门户id")
    private String id;

    @Schema(description = "PC:网页端 APP:手机端 ")
    String platform;



}
