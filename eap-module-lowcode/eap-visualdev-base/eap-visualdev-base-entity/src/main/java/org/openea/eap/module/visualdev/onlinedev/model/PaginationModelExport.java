package org.openea.eap.module.visualdev.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.module.visualdev.base.model.PaginationModel;

/**
 *
 */
@Data
@Schema(description = "导出参数")
public class PaginationModelExport extends PaginationModel {
    @Schema(description = "导出selectKey")
    private String[] selectKey;
    @Schema(description = "导出json")
    private String json;
}
