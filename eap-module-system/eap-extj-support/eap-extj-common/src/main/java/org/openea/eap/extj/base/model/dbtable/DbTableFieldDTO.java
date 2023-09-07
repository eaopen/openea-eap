package org.openea.eap.extj.base.model.dbtable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;

import java.util.ArrayList;
import java.util.List;

@Data
public class DbTableFieldDTO {

    @Schema(description = "表基本信息")
    private DbTableForm tableInfo;

    @Schema(description = "字段信息")
    private List<DbFieldForm> tableFieldList;

    /**
     * 获取表模型
     *
     * @param dbLinkId 数据库连接Id
     * @return 表模型
     */
    public DbTableFieldModel getCreDbTableModel(String dbLinkId){
        String table = this.getTableInfo().getNewTable();
        return toCommon(table, null, null, dbLinkId);
    }

    public DbTableFieldModel getUpDbTableModel(String dbLinkId){
        // 更新时用表
        String oldTable = this.getTableInfo().getTable();
        String newTable = this.getTableInfo().getNewTable();
        return toCommon(null, oldTable, newTable, dbLinkId);
    }

    private DbTableFieldModel toCommon(String table, String oldTable, String newTable, String dbLinkId) {
        DbTableFieldModel dbTableModel = new DbTableFieldModel();
        DbTableForm dbTableForm = this.getTableInfo();
        // 数据连接Id
        dbTableModel.setDbLinkId(dbLinkId);
        // 创建表名
        dbTableModel.setTable(table);
        // 更新时表名
        dbTableModel.setUpdateOldTable(oldTable);
        dbTableModel.setUpdateNewTable(newTable);
        // 表注释
        dbTableModel.setComment(dbTableForm.getTableName());
        // 表字段集合
        List<DbFieldModel> list = new ArrayList<>();
        for (DbFieldForm dbFieldForm : this.getTableFieldList()) {
            // 字段
            DbFieldModel dbFieldModel = new DbFieldModel();
            // 字段名
            dbFieldModel.setField(dbFieldForm.getField());
            // 字段注释
            dbFieldModel.setComment(dbFieldForm.getFieldName());
            // 主键
            dbFieldModel.setIsPrimaryKey(dbFieldForm.getPrimaryKey() == 1);
            // 非空
            dbFieldModel.setNullSign(DbAliasConst.ALLOW_NULL.getSign(dbFieldForm.getAllowNull()));
            // 数据类型
            dbFieldModel.setLength(dbFieldForm.getDataLength());
            dbFieldModel.setDataType(dbFieldForm.getDataType());
            list.add(dbFieldModel);
        }
        dbTableModel.setDbFieldModelList(list);
        return dbTableModel;
    }

}
