package org.openea.eap.extj.database.sql.param;

import oracle.sql.TIMESTAMP;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.sql.enums.SqlOracleEnum;
import org.openea.eap.extj.database.sql.model.SqlPrintHandler;
import org.openea.eap.extj.database.sql.util.SqlFrameUtil;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.JdbcUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.util.context.SpringContext;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FormatSqlOracle {
    private static SqlPrintHandler sqlPrintHandler = (SqlPrintHandler) SpringContext.getBean(SqlPrintHandler.class);
    public static Function<Map<String, Object>, Map.Entry<String, Object>> getPrimaryVal = (fieldMap) -> {
        Iterator var1 = fieldMap.entrySet().iterator();

        Map.Entry field;
        String primaryKey;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            field = (Map.Entry)var1.next();
            primaryKey = (String)field.getKey();
        } while(!primaryKey.equalsIgnoreCase("F_Id") && !primaryKey.equalsIgnoreCase("ID"));

        return field;
    };

    public static String dateTime(String dbEncode, String dateTime) {
        return "Oracle".equals(dbEncode) ? "to_date(" + dateTime + ", 'yyyy-mm-dd hh24:mi:ss')" : dateTime;
    }

    public static Object timestamp(Object value) {
        if (value instanceof TIMESTAMP) {
            try {
                return ((TIMESTAMP)value).dateValue();
            } catch (SQLException var2) {
                var2.printStackTrace();
            }
        }

        return value;
    }

    public static void nullValue(String dbEncode, JdbcColumnModel model, Map<String, Object> map) {
        if (("Oracle".equals(dbEncode) || "DM".equals(dbEncode)) && model.getValue() instanceof String && model.getNullSign().equals("NOT NULL") && model.getValue().toString().equals("")) {
            map.put(model.getField(), " ");
        }

    }

    public static Object clobExecute(String dbEncode, Object context, String table, String column, Map<String, Object> dataMap, StringBuilder sqlBuilder) {
        if ("Oracle".equals(dbEncode) && context instanceof String) {
            Map.Entry<String, Object> field = (Map.Entry)getPrimaryVal.apply(dataMap);
            if (field != null) {
                String clobUpdateSql = clobUpdate(context.toString(), table, column, (String)field.getKey(), field.getValue().toString());
                if (clobUpdateSql != null) {
                    sqlPrintHandler.updateClob(clobUpdateSql);
                    sqlBuilder.append(clobUpdateSql).append(";\n/\n");
                    return "context";
                }
            }
        }

        return context;
    }

    public static String clobUpdate(String context, String table, String column, String primaryColumn, String primaryValue) {
        if (context.length() <= 1500) {
            return null;
        } else {
            context = context.replace("'", "''");
            List<String> splitStrList = SqlFrameUtil.splitStrRepeat(context, 1500);
            StringBuilder contextInfo = new StringBuilder();
            Iterator var7 = splitStrList.iterator();

            while(var7.hasNext()) {
                String contextFragment = (String)var7.next();
                contextInfo.append("\t").append(SqlOracleEnum.CLOB_APPEND.getSqlFrame().replace("{context}", contextFragment)).append(";\n");
            }

            return SqlOracleEnum.CLOB_UPDATE.getFastSql(Arrays.asList(table, column, contextInfo.toString(), primaryColumn, primaryValue));
        }
    }

    public static void autoIncrement(DbLinkEntity dbLinkEntity, DbTableFieldModel dbTableFieldModel) throws Exception {
        if (DbTypeUtil.checkOracle(dbLinkEntity)) {
            String table = dbTableFieldModel.getTable();
            boolean autoInc = false;
            String autoIncField = "";
            Iterator var5 = dbTableFieldModel.getDbFieldModelList().iterator();

            while(var5.hasNext()) {
                DbFieldModel dbFieldModel = (DbFieldModel)var5.next();
                if (dbFieldModel.getIsAutoIncrement() != null && dbFieldModel.getIsAutoIncrement()) {
                    autoInc = true;
                    autoIncField = dbFieldModel.getField();
                }
            }

            if (autoInc) {
                NotTenantPluginHolder.setNotSwitchFlag();
                String autoIncrement = SqlOracleEnum.CREATE_AUTO_INCREMENT.getSqlFrame().replace("{table}", table);
                String autoIncrementTrigger = SqlOracleEnum.CREATE_AUTO_INCREMENT_TRIGGER.getSqlFrame().replace("{table}", table).replace("{autoInc_field}", autoIncField);
                if (!sqlPrintHandler.oracleAutoIncrement(autoIncrement + ";\n" + autoIncrementTrigger)) {
                    JdbcUtil.creUpDe((new PrepSqlDTO(autoIncrement)).withConn(dbLinkEntity));
                    JdbcUtil.update((new PrepSqlDTO(autoIncrementTrigger)).withConn(dbLinkEntity));
                }
            }
        }

    }

    public FormatSqlOracle() {
    }

}

