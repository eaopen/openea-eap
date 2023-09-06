package org.openea.eap.extj.database.sql.param;

import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.exception.DataTypeException;

/**
 * 类功能
 *
 */
public class FormatSqlMySQL {

    /**
     * 处理单引号 ''
     */
    public static String singleQuotes(String value){
        return value.replace("'", "\\'");
    }

    /**
     * Mysql一些类型的特殊处理
     */
    public static Object getMysqlValue(JdbcColumnModel dbColumnModel) {
        return dbColumnModel.getValue();
    }

    public static void  checkMysqlFieldPrimary(DbFieldModel field, String table) throws DataTypeException {
        // Mysql对主键的一些限制
        if(field.getIsPrimaryKey()){
            // Mysql 字段为主键的时候，不能为BLOB/TEXT/tinytext的类型
            switch ((DtMySQLEnum)field.getDtModelDTO().getConvertTargetDtEnum()){
                case BLOB:
                case TINY_TEXT:
                case MEDIUM_TEXT:
                case TEXT:
                case LONG_TEXT:
                    throw new DataTypeException("表 \"" + table + "\"中字段 \"" + field.getField() + "\" 为主键，不允许数据类型 \""
                            + field.getDtModelDTO().getDtEnum().getDataType() + "\" ");
                case VARCHAR:
                    // varchar作为主键的时候，长度不允许超过768
                    if(field.getDtModelDTO().getCharLength() > 768L){
                        field.getDtModelDTO().setCharLength(768L);
                    }
                    break;
                default:
            }
        }
    }

}
