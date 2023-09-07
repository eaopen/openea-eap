package org.openea.eap.extj.base.util;

import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.dbtable.JdbcTableModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;

import java.util.List;

/**
 * 打印模板-工具类
 *
 * 
 */
public class PrintDevUtil {

    /**
     * 获取字段注释
     * [1]:表注释 [2]:字段注释
     */
    public static String[] getTableColumnComment(DbLinkEntity dbLinkEntity, String table, String columnName){
        try {
            JdbcTableModel jdbcTableModel = new JdbcTableModel(dbLinkEntity, table);
            String tableComment = jdbcTableModel.getComment();
            String columnComment = "";
            List<JdbcColumnModel> columnList = jdbcTableModel.getJdbcColumnModelList();
            for(JdbcColumnModel column : columnList){
                if(column.getField().equalsIgnoreCase(columnName)){
                    columnComment =  column.getComment();
                }
            }
            return new String[] {tableComment, columnComment};
        } catch (Exception e) {
            throw new RuntimeException("表信息抽取异常！", e);
        }
    }

}
