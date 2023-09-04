package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.util.StringUtil;

import java.util.List;
import java.util.Map;

public enum SqlDMEnum implements SqlFrameBase {
    FIELDS("SELECT \n\tA.COLUMN_NAME AS " + DbAliasEnum.FIELD.getAlias() + ",\n\tA.DATA_TYPE AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n\tA.DATA_LENGTH AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n\tA.DATA_PRECISION AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n\tA.DATA_SCALE AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n\tCASE WHEN E.CONSTRAINT_TYPE IS NOT NULL THEN '1' ELSE '0' END AS " + DbAliasEnum.PRIMARY_KEY.getAlias() + ",\n\tCASE A.NULLABLE WHEN 'N' THEN '0' ELSE '1' END AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n\tB.COMMENTS AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n\tH.IS_AUTO AS " + DbAliasEnum.AUTO_INCREMENT.getAlias() + "\nFROM \n\tALL_TAB_COLUMNS A -- 表&字段 OWNER、TABLE_NAME、COLUMN_NAME_\t\nLEFT JOIN \n\tALL_COL_COMMENTS B -- 字段注释 TABLE_NAME、COLUMN_NAME\nON \n\tA.OWNER = B.OWNER AND A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME \nLEFT JOIN \n\t(\n\t\tSELECT \n\t\t\tC.OWNER, C.TABLE_NAME, C.COLUMN_NAME, D.CONSTRAINT_TYPE  \n\t\tFROM \n\t\t\tALL_CONS_COLUMNS C\n\t\tLEFT JOIN \n\t\t\tALL_CONSTRAINTS D \n\t\tON \n\t\t\tC.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND D.CONSTRAINT_TYPE = 'P'\n\t\tWHERE\n\t\t\tC.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n\t\t\tAND C.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + "\n\t) E\nON\n\tA.OWNER = E.OWNER AND A.TABLE_NAME = E.TABLE_NAME AND A.COLUMN_NAME = E.COLUMN_NAME \nLEFT JOIN \n\t(\n\t\tSELECT \n\t\t\tF.NAME, DECODE(F.INFO2,1,'1','0') AS IS_AUTO \n\t\tFROM \n\t\t\tSYS.SYSCOLUMNS F\n\t\tWHERE\n\t\t\tID =\t(\n\t\tSELECT \n\t\t\tOBJECT_ID  \n\t\tFROM \n\t\t\tDBA_OBJECTS G \n\t\tWHERE \n\t\t\tG.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n\t\t\tAND G.OBJECT_NAME = " + ParamEnum.TABLE.getParamSign() + "\n\t\t\tAND G.OBJECT_TYPE = 'TABLE') \n\t) H\nON \n\tA.COLUMN_NAME = H.NAME \nWHERE\n  A.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n\tAND A.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + "\n\tORDER BY A.COLUMN_ID") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },
    TABLES("SELECT\nut.TABLE_NAME " + DbAliasEnum.TABLE_NAME.getAlias() + ",utc.COMMENTS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\n,ut.num_rows " + DbAliasEnum.TABLE_SUM.getAlias() + "\nFROM ALL_TABLES AS ut\nLEFT JOIN\nall_tab_comments AS utc\nON\nut.TABLE_NAME = utc.TABLE_NAME AND ut.OWNER = utc.OWNER\nWHERE ut.OWNER = " + ParamEnum.DB_SCHEMA.getParamSign() + "\nORDER BY F_TABLE_NAME;") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
        }
    },
    TABLESANDVIEW(" select\tTABLE_NAME as " + DbAliasEnum.TABLE_NAME.getAlias() + ",'TABLE' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " from all_tab_comments WHERE owner = " + ParamEnum.DB_NAME.getParamSign() + "\n UNION\n select\t view_name as " + DbAliasEnum.TABLE_NAME.getAlias() + ",'VIEW' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " from all_views WHERE owner = " + ParamEnum.DB_NAME.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLE(TABLES.sqlFrame.replace("ORDER BY", "AND ut.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + " ORDER BY")) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE("SELECT COUNT (*) AS TOTAL FROM (SELECT\nut.TABLE_NAME " + DbAliasEnum.TABLE_NAME.getAlias() + " FROM ALL_TABLES AS ut\nWHERE ut.OWNER = " + ParamEnum.DB_SCHEMA.getParamSign() + " and ut.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + ") AS TOTAL") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },
    CREATE_TABLE("<CREATE> <TABLE> {table}<(>\n【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】\n<)>") {
        public String createIncrement(String sqlFrame, Map<String, String> paramsMap) {
            if (StringUtil.isNotEmpty((String)paramsMap.get("[AUTO_INCREMENT]"))) {
                sqlFrame = super.createIncrement(sqlFrame, paramsMap).replace("{dataType}", "{dataType} IDENTITY(1, 1)");
                String openIdentity = OPEN_IDENTITY.sqlFrame.replace("{table}", (CharSequence)paramsMap.get("{table}")).replace("<ON|OFF>", "ON");
                return sqlFrame + ";\n" + openIdentity;
            } else {
                return sqlFrame;
            }
        }

        public String createIndex() {
            String createIndex = "CREATE UNIQUE {uniqueName} TO {table}【{column}】";
            return null;
        }
    },
    OPEN_IDENTITY("SET IDENTITY_INSERT {table} <ON|OFF>"),
    ALTER_DROP("ALTER TABLE 《schema》.{table} DROP COLUMN {column}"),
    ALTER_ADD("ALTER TABLE 《schema》.{table} ADD {column} {dataType}"),
    ALTER_TYPE("ALTER TABLE 《schema》.{table} <MODIFY> {column} {dataType}"),
    ALTER_TABLE_NAME("ALTER TABLE 《schema》.{table} ALTER column {oldColumn} RENAME TO {newColumn};"),
    DB_TIME_SQL("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as TIME "),
    INSERT("INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)"),
    CREATE_SCHEMA("CREATE SCHEMA \"{schema}\";"),
    DROP_SCHEMA("DROP SCHEMA \"{schema}\" RESTRICT"),
    DROP_TABLE(SqlPostgreSQLEnum.DROP_TABLE),
    COMMENT_COLUMN(SqlPostgreSQLEnum.COMMENT_COLUMN),
    COMMENT_TABLE(SqlPostgreSQLEnum.COMMENT_TABLE),
    CREATE(SqlPostgreSQLEnum.CREATE);

    private final String dbEncode;
    private final String sqlFrame;

    private SqlDMEnum(SqlFrameBase sqlEnum) {
        this.dbEncode = "DM";
        this.sqlFrame = sqlEnum.getSqlFrame();
    }

    public String getDbEncode() {
        this.getClass();
        return "DM";
    }

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    private SqlDMEnum(String sqlFrame) {
        this.dbEncode = "DM";
        this.sqlFrame = sqlFrame;
    }
}
