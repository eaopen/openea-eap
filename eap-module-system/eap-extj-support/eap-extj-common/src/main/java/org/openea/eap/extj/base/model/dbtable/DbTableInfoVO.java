package org.openea.eap.extj.base.model.dbtable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DbTableInfoVO {

    @Schema(description = "表信息")
    private DbTableVO tableInfo;
    @Schema(description = "字段信息集合")
    private List<DbFieldVO> tableFieldList;
    @Schema(description = "表是否存在信息")
    private Boolean hasTableData;

    public DbTableInfoVO(DbTableFieldModel dbTableModel, List<DbFieldModel> dbFieldModelList){
        if(dbTableModel != null){
            List<DbFieldVO> list = new ArrayList<>();
            for (DbFieldModel dbFieldModel : dbFieldModelList) {
                list.add(new DbFieldVO(dbFieldModel));
            }
            this.tableFieldList = list;
            this.tableInfo = new DbTableVO(dbTableModel);
            this.hasTableData = dbTableModel.getHasTableData();
        }
    }

}