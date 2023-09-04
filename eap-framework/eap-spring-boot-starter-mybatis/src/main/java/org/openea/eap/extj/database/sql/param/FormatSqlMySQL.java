package org.openea.eap.extj.database.sql.param;


import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.exception.DataTypeException;

public class FormatSqlMySQL {
    public FormatSqlMySQL() {
    }

    public static String singleQuotes(String value) {
        return value.replace("'", "\\'");
    }

    public static Object getMysqlValue(JdbcColumnModel dbColumnModel) {
        return dbColumnModel.getValue();
    }

    public static void checkMysqlFieldPrimary(DbFieldModel field, String table) throws DataTypeException {
        if (field.getIsPrimaryKey()) {
            switch ((DtMySQLEnum)field.getDtModelDTO().getConvertTargetDtEnum()) {
                case BLOB:
                case TINY_TEXT:
                case MEDIUM_TEXT:
                case TEXT:
                case LONG_TEXT:
                    throw new DataTypeException("表 \"" + table + "\"中字段 \"" + field.getField() + "\" 为主键，不允许数据类型 \"" + field.getDtModelDTO().getDtEnum().getDataType() + "\" ");
                case VARCHAR:
                    if (field.getDtModelDTO().getCharLength() > 768L) {
                        field.getDtModelDTO().setCharLength(768L);
                    }
            }
        }

    }
}
