package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualUpModel extends VisualCrModel{
    @Schema(description = "背景url")
    private String backgroundUrl;
    @Schema(description = "发布状态")
    private String status;
    @Schema(description = "主键")
    private String id;
}
