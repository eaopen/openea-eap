package org.openea.eap.extj.model.visualconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualConfigUpForm extends VisualConfigCrForm {
    @Schema(description = "大屏配置主键")
    private String id;
    @Schema(description = "大屏基本主键")
    private String visualId;
}
