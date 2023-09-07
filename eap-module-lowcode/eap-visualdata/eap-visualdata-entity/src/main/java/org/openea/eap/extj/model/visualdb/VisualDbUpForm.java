package org.openea.eap.extj.model.visualdb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualDbUpForm extends VisualDbCrForm{
    @Schema(description = "主键")
    private String id;


}
