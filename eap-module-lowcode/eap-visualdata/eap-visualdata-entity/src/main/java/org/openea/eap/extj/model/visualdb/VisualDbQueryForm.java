package org.openea.eap.extj.model.visualdb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualDbQueryForm {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "sql语句")
    private String sql;
}
