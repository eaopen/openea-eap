package org.openea.eap.extj.base.model.dbtable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class DbTableVO {

    @NotBlank(message = "必填")
    @Schema(description = "表名")
    private String table;
    @NotBlank(message = "必填")
    @Schema(description = "表注释")
    private String tableName;

    public DbTableVO(DbTableFieldModel dbTableFieldModel){
        this.table = dbTableFieldModel.getTable();
        this.tableName = dbTableFieldModel.getComment();
    }

}