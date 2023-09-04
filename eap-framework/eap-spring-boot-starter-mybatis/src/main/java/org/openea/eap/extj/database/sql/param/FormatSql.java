package org.openea.eap.extj.database.sql.param;

import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.SqlPrintHandler;
import org.openea.eap.extj.util.context.SpringContext;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FormatSql {
    private static SqlPrintHandler sqlPrintHandler = (SqlPrintHandler) SpringContext.getBean(SqlPrintHandler.class);
    private String dbEncode;

    public static Object convertValue(JdbcColumnModel dbColumnModel, DbBase toDb) throws Exception {
        switch (toDb.getJnpfDbEncode()) {
            case "Oracle":
            case "PostgreSQL":
                return getPostgreValue(dbColumnModel);
            case "MySQL":
                return FormatSqlMySQL.getMysqlValue(dbColumnModel);
            default:
                return dbColumnModel.getValue();
        }
    }

    public static Object getPostgreValue(JdbcColumnModel dbColumnModel) {
        return dbColumnModel.getValue();
    }

    public static String getFieldName(String fieldName, String dbEncode) {
        switch (dbEncode) {
            case "MySQL":
                return "`" + fieldName + "`";
            case "SQLServer":
                return fieldName;
            case "Oracle":
            case "DM":
                return "\"" + fieldName.toUpperCase() + "\"";
            case "PostgreSQL":
                return "\"" + fieldName.toLowerCase() + "\"";
            default:
                return fieldName;
        }
    }

    public static String formatValue(Object value, String dbEncode) {
        if (value == null) {
            return "Null";
        } else if (!(value instanceof LocalDateTime) && !(value instanceof Date)) {
            if (value instanceof String) {
                String context = value.toString().replace("'", "''");
                return "'" + context + "'";
            } else {
                return value instanceof Integer ? value.toString() : value.toString();
            }
        } else {
            Date date;
            if (value instanceof LocalDateTime) {
                date = Date.from(((LocalDateTime)value).atZone(ZoneId.systemDefault()).toInstant());
            } else {
                date = (Date)value;
            }

            String dateInfo = "'" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date) + "'";
            dateInfo = FormatSqlOracle.dateTime(dbEncode, dateInfo);
            return dateInfo;
        }
    }

    public static String defaultCheck(DbFieldModel fieldModel, String dbEncode) {
        if (fieldModel.getDefaultValue() != null) {
            String defaultValue = "'" + fieldModel.getDefaultValue() + "'";
            if ("Oracle".equals(dbEncode)) {
                fieldModel.setNullSign("");
                if (fieldModel.getDataType().equalsIgnoreCase("DATETIME")) {
                    defaultValue = "to_date(" + defaultValue + ", 'yyyy-mm-dd hh24:mi:ss')";
                }
            }

            return "DEFAULT " + defaultValue;
        } else {
            return "";
        }
    }

    public String getDbEncode() {
        return this.dbEncode;
    }

    public void setDbEncode(String dbEncode) {
        this.dbEncode = dbEncode;
    }


    public FormatSql(String dbEncode) {
        this.dbEncode = dbEncode;
    }
}