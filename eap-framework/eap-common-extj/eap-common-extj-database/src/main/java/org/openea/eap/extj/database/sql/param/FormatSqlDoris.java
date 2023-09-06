package org.openea.eap.extj.database.sql.param;

import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能
 *
 */
@Data
public class FormatSqlDoris {

    private String primaryField;

    /**
     * 添加主键相关内容
     * @param sql 建表SQL
     * @param dbTableFieldModel 表字段对象
     * @param dbType 数据类型
     * @return sql语句
     */
//    public static String getPrimaryFieldSql(String sql, DbTableFieldModel dbTableFieldModel, String dbType){
//        if(dbType.equals(DbBase.DORIS)) {
//            List<DbFieldModel> dbFieldModelList = dbTableFieldModel.getDbFieldModelList();
//            for (DbFieldModel dbFieldModel : dbFieldModelList) {
//                if (dbFieldModel.getIsPrimaryKey()) {
//                    sql = sql.replace("{primary_column}", dbFieldModel.getField());
//                }
//            }
//            sql = sql.replace("{tableComment}", dbTableFieldModel.getComment());
//        }
//        return sql;
//    }

    /**
     * 获取KEY设置SQL
     */
    public static String getPrimaryFieldSql(String sql, DbTableFieldModel dbTableFieldModel, String dbType){
        if(dbType.equals(DbBase.DORIS)) {
            List<DbFieldModel> dbFieldModelList = dbTableFieldModel.getDbFieldModelList();
            List<String> keyList = new ArrayList<>();
            for (DbFieldModel dbFieldModel : dbFieldModelList) {
                keyList.add(dbFieldModel.getField());
                if (dbFieldModel.getIsPrimaryKey()) {
                    sql = sql.replace("{tablet_column}", dbFieldModel.getField());
                }
            }
            sql = sql.replace("{primary_column}", StringUtil.join(keyList, ","));
            sql = sql.replace("{tableComment}", dbTableFieldModel.getComment());
        }
        return sql;
    }

    public static String getPrimaryFieldColumn(List<DbFieldModel> fieldModelList, String dbType){
        if(dbType.equals(DbBase.DORIS)) {
            for (DbFieldModel dbFieldModel : fieldModelList) {
                if (dbFieldModel.getIsPrimaryKey()) {
                    return dbFieldModel.getField();
                }
            }
        }
        return "f_id";
    }

    /**
     * Doris需要Key需要排序
     */
    public static void orderUniqueColumn(String dbType, List<String> columnSqlList, DbTableFieldModel dbTableFieldModel){
        if(dbType.equals(DbBase.DORIS)) {
            for (DbFieldModel dbFieldModel : dbTableFieldModel.getDbFieldModelList()) {
                if (dbFieldModel.getIsPrimaryKey()) {
                    for (int i = 0; i < columnSqlList.size(); i++) {
                        if(columnSqlList.get(i).contains("\t" + dbFieldModel.getField())){
                            columnSqlList.add(0, columnSqlList.remove(i));
                        }
                    }
                }
            }

        }

    }


}
