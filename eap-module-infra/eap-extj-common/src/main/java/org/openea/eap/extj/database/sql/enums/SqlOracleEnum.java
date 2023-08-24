package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.sql.model.DbStruct;

import java.util.Arrays;
import java.util.List;

public enum SqlOracleEnum implements SqlFrameBase {
    FIELDS("SELECT \n\tA.COLUMN_NAME AS " + DbAliasEnum.FIELD.getAlias() + ",\n\tA.DATA_TYPE AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n\tA.DATA_LENGTH AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n\tA.DATA_PRECISION AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n\tA.DATA_SCALE AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n\tCASE WHEN E.CONSTRAINT_TYPE IS NOT NULL THEN '1' ELSE '0' END AS " + DbAliasEnum.PRIMARY_KEY.getAlias() + ",\n\tCASE A.NULLABLE WHEN 'N' THEN '0' ELSE '1' END AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n\t(SELECT COUNT(*) FROM ALL_TRIGGERS WHERE TABLE_NAME = A.TABLE_NAME AND TABLE_OWNER = A.OWNER AND INSTR(TRIGGER_NAME, 'AUTO_') >0) AS " + DbAliasEnum.AUTO_TRIGGER.getAlias() + ",\n\tB.COMMENTS AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + "\nFROM \n\tALL_TAB_COLUMNS A -- 表&字段 OWNER、TABLE_NAME、COLUMN_NAME_\t\nLEFT JOIN \n\tALL_COL_COMMENTS B -- 字段注释 TABLE_NAME、COLUMN_NAME\nON \n\tA.OWNER = B.OWNER AND A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME \nLEFT JOIN \n\t(SELECT \n\t\t\tC.OWNER, C.TABLE_NAME, C.COLUMN_NAME, D.CONSTRAINT_TYPE  \n\t\tFROM \n\t\t\tALL_CONS_COLUMNS C, ALL_CONSTRAINTS D \n\t\tWHERE \n\t\t\tC.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND D.CONSTRAINT_TYPE = 'P'\n\t) E\nON\n\tA.OWNER = E.OWNER AND A.TABLE_NAME = E.TABLE_NAME AND A.COLUMN_NAME = E.COLUMN_NAME \nWHERE\n  A.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n\tAND A.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
            list.add(table);
        }
    },
    TABLES("SELECT atcs.table_name " + DbAliasEnum.TABLE_NAME.getAlias() + ", atcs.comments " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", ats.num_rows " + DbAliasEnum.TABLE_SUM.getAlias() + "\nFROM all_tab_comments atcs,all_tables ats WHERE atcs.table_name = ats.table_name AND ats.owner = atcs.owner AND ats.owner = " + ParamEnum.DB_NAME.getParamSign() + "") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
        }
    },
    TABLESANDVIEW("SELECT\n\tatcs.table_name " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n\tatcs.comments " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",\n\tatcs.table_type " + DbAliasEnum.TABLE_TYPE.getAlias() + " \nFROM\n\tall_tab_comments atcs\n\tLEFT JOIN all_views alv ON alv.owner = atcs.owner \nWHERE\n\tatcs.owner = " + ParamEnum.DB_NAME.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
        }
    },
    TABLE(TABLES.sqlFrame + " AND ats.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE("SELECT COUNT(*) AS TOTAL FROM USER_TABLES WHERE USER_TABLES.TABLE_NAME = UPPER(" + ParamEnum.TABLE.getParamSign() + ")") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
        }
    },
    CREATE_TABLE("<CREATE> <TABLE> {table} <(>\n【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】\n<)>") {
        public String createIndex() {
            String model = "CREATE UNIQUE INDEX {indexName} ON {table}(【column】)";
            return null;
        }
    },
    CREATE_AUTO_INCREMENT("CREATE SEQUENCE {table}_sq\nINCREMENT by 1\nSTART WITH 1\nNOMAXVALUE\nNOCYCLE\nNOCACHE"),
    CREATE_AUTO_INCREMENT_TRIGGER("CREATE OR REPLACE TRIGGER AUTO_{table}_tg\nBEFORE INSERT ON {table}\nFOR EACH ROW\nBEGIN\n\tSELECT {table}_sq.NEXTVAL INTO :new.{autoInc_field} FROM dual;\nEND;"),
    DROP_SEQ("DROP sequence {seqName}"),
    DROP_TRIGGER("DROP trigger {triggerName}"),
    CREATE("CREATE TABLE 《schema》.{table}(\n【  1:(PRIMARY KEY ({primaryColumn}))|2:({column} {dataType} [[NOT] [NULL]] [DEFAULT {defaultValue}]),\n】)"),
    DROP_TABLE("DROP TABLE {table}"),
    COMMENT_TABLE("COMMENT ON TABLE 《schema》.{table} IS '{comment}'"),
    COMMENT_COLUMN("COMMENT ON COLUMN 《schema》.{table}.{column} IS '{comment}'"),
    DROP_COLUMN("ALTER TABLE 《schema》.{table} DROP COLUMN {column}"),
    ADD_COLUMN("ALTER TABLE 《schema》.{table} ADD {column} {dataType}"),
    MODIFY_TYPE("ALTER TABLE 《schema》.{table} ALTER COLUMN {column} TYPE {dataType}"),
    ALTER_TABLE("ALTER TABLE 《schema》.{oldTable} RENAME TO {newTable}"),
    ORDER_PAGE("SELECT * FROM (SELECT JNPF_TEMP.*, ROWNUM JNPF_RowNo FROM ({selectSql}) JNPF_TEMP ORDER BY {orderColumn} [DESC]) JNPF_TAB WHERE JNPF_TAB.JNPF_RowNo BETWEEN {beginIndex} AND {endIndex}"),
    DB_TIME_SQL("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as TIME from dual"),
    INSERT("INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)"),
    CLOB_INSERT("DECLARE\n\tclobVal {table}.{column}%TYPE;\nBEGIN\t\n\tclobVal := '{value}';\nINSERT INTO {table} (【{clobColumn},{column},】) VALUES (【{clobVal},{value},】);\nEND"),
    CLOB_UPDATE("DECLARE \n-- 此表为：{table}\n\tcontext NCLOB;\nBEGIN\n\tDBMS_LOB.CREATETEMPORARY(context,TRUE);\n\t【DBMS_LOB.APPEND(context, '{context}');】\n\tUPDATE {table} SET {column} = context WHERE {key} = '{value}';\nEND") {
        public String getFastSql(List<String> values) {
            String sql = this.getSqlFrame();
            List<String> keys = Arrays.asList("{table}", "{column}", "\t【DBMS_LOB.APPEND(context, '{context}');】\n", "{key}", "{value}");

            for(int i = 0; i < values.size(); ++i) {
                if (values.get(i) != null) {
                    sql = sql.replace((CharSequence)keys.get(i), (CharSequence)values.get(i));
                }
            }

            return sql;
        }
    },
    CLOB_APPEND("DBMS_LOB.APPEND(context, '{context}')"),
    TO_TIME("TO_DATE('{datetime}','YYYY-MM-DD HH24:MI:SS')"),
    SELECT_TABLESPACE("SELECT TABLESPACE_NAME,FILE_ID,FILE_NAME,round(bytes/(1024*1024),0) total_space FROM DBA_DATA_FILES ORDER BY TABLESPACE_NAME"),
    CREATE_TEMP_TABLESPACE("CREATE TEMPORARY TABLESPACE UQSM_TEMP TEMPFILE '/{path}/{tempTablespaceName}.dbf' size 8000m autoextend on next 50m maxsize unlimited extent management local;"),
    CREATE_TABLESPACE("\nCREATE TABLESPACE UQSM_DATA LOGGING DATAFILE '/{path}/{tempTablespaceName}.dbf' SIZE 8000m autoextendon next 50M maxsize unlimited extent management local;\n"),
    CREATE_USER("CREATE USER {user} IDENTIFIED BY {password} DEFAULT TABLESPACE {tablespace} TEMPORARY TABLESPACE {tempTablespace}"),
    DROP_USER("DROP USER {user}"),
    GRANT_ROLE("GRANT connect,RESOURCE,dba to {user};"),
    ALTER_USER_PASSWORD("ALTER USER {user} IDENTIFIED BY {password}");

    private final String sqlFrame;
    private final String dbEncode;

    public String getFastSql(List<String> values) {
        return this.getSqlFrame();
    }

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    public String getDbEncode() {
        this.getClass();
        return "Oracle";
    }

    private SqlOracleEnum(String sqlFrame) {
        this.dbEncode = "Oracle";
        this.sqlFrame = sqlFrame;
    }
}
