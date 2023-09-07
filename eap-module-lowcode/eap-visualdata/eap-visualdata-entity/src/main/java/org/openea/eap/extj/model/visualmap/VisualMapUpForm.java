package org.openea.eap.extj.model.visualmap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualMapUpForm extends VisualMapCrForm {
    @Schema(description = "主键")
    private String id;
}
