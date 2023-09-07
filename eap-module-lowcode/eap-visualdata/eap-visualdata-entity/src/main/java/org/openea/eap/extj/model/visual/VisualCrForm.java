package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.model.visualconfig.VisualConfigCrForm;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualCrForm {
    @Schema(description = "大屏基本信息")
    private VisualCrModel visual;
    @Schema(description = "大屏配置")
    private VisualConfigCrForm config;
}
