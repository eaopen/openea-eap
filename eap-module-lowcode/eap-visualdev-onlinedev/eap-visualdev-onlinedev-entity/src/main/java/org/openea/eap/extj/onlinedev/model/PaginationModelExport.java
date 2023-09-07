package org.openea.eap.extj.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.model.PaginationModel;


@Data
@Schema(description = "导出参数")
public class PaginationModelExport extends PaginationModel {
    @Schema(description = "导出selectKey")
    private String[] selectKey;
    @Schema(description = "导出json")
    private String json;
}
