package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.model.visualconfig.VisualConfigInfoModel;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualInfoVO {
    @Schema(description = "大屏基本信息")
    private VisualInfoModel visual;
    @Schema(description = "大屏配置")
    private VisualConfigInfoModel config;
}
