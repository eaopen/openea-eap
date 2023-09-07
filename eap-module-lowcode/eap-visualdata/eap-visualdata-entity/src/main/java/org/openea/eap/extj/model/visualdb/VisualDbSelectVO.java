package org.openea.eap.extj.model.visualdb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualDbSelectVO {
    @Schema(description = "驱动")
    private String driverClass;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "主键")
    private String id;

}
