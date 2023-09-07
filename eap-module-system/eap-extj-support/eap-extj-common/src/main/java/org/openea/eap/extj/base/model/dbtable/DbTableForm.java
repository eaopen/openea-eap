package org.openea.eap.extj.base.model.dbtable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DbTableForm  {

    @Schema(description = "表名")
    private String table;

    @NotBlank(message = "必填")
    @Schema(description = "表说明")
    private String tableName;

    @Schema(description = "新表名")
    private String newTable;

}