package org.openea.eap.extj.model.visualmap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualMapListVO {
    @Schema(description = "地图名称")
    private String name;
    @Schema(description = "主键")
    private String id;
}
