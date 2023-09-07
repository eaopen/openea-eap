package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.model.visualconfig.VisualConfigUpForm;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualUpForm {
    @Schema(description = "大屏基本信息")
    private VisualUpModel visual;
    @Schema(description = "大屏配置")
    private VisualConfigUpForm config;
}
