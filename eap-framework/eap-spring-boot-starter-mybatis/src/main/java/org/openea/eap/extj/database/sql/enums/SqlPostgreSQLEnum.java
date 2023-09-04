package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.util.StringUtil;

import java.util.List;
import java.util.Map;

public enum SqlPostgreSQLEnum implements SqlFrameBase {
    FIELDS("SELECT\n\tcol.column_name AS " + DbAliasEnum.FIELD.getAlias() + ",\n\tcol.udt_name AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n\tis_nullable AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n\tcol_description (pa.pg_oid, attnum) " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n\tcharacter_maximum_length AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n\tnumeric_precision AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n\tcolumn_default AS " + DbAliasEnum.COLUMN_DEFAULT.getAlias() + ",\n\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n\tnumeric_scale AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n\t(CASE WHEN ( SELECT pa.attnum = ANY ( conkey ) FROM pg_constraint WHERE conrelid = pa.pg_oid AND contype = 'p' ) = 't' \n\tTHEN 1 ELSE 0 END ) " + DbAliasEnum.PRIMARY_KEY.getAlias() + "\t\nFROM\n\tinformation_schema.COLUMNS AS col\nLEFT JOIN (\n\tSELECT\n\t\t\t\t\t* \n\tFROM\n    (SELECT *,oid AS pg_oid FROM pg_class WHERE relnamespace IN ( SELECT oid FROM pg_namespace WHERE nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + " )) AS pc\n\tLEFT JOIN\n\t  pg_attribute AS pat \n\tON \n\t\tpat.attrelid = pc.pg_oid\n\tWHERE \n\t\t\tpc.relname = " + ParamEnum.TABLE.getParamSign() + "\n\t) AS pa \nON \n\tpa.attname = col.column_name \nWHERE\n\tcol.table_schema = " + ParamEnum.DB_SCHEMA.getParamSign() + " \n\tAND TABLE_NAME = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
        }
    },
    TABLES("SELECT\n        pt.*,\n        pg_tab.relname AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n        pg_tab.reltuples AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n        pg_tab.nspname,\n        CAST ( obj_description ( pg_tab.relfilenode, 'pg_class' ) AS VARCHAR ) AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\nFROM\n        pg_tables pt\nLEFT JOIN        \n(SELECT \n        * \nFROM\n        pg_class pc\nLEFT JOIN \n        pg_namespace pns\nON\n          pns.oid = pc.relnamespace\nWHERE\n         pns.nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + ") AS pg_tab\nON\n        pt.tablename = pg_tab.relname\nWHERE pt.schemaname = " + ParamEnum.DB_SCHEMA.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLESANDVIEW("SELECT viewname as " + DbAliasEnum.TABLE_NAME.getAlias() + ", 'VIEW' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " FROM pg_views WHERE schemaname = " + ParamEnum.DB_SCHEMA.getParamSign() + "\nUNION\nSELECT tablename as " + DbAliasEnum.TABLE_NAME.getAlias() + ",'TABLE' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " FROM pg_tables WHERE schemaname = " + ParamEnum.DB_SCHEMA.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLE(TABLES.sqlFrame + "AND pg_tab.relname = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE("SELECT COUNT (*) AS TOTAL \nFROM\n( \n        SELECT relname AS F_TABLE_NAME FROM pg_class C WHERE relname = lower(" + ParamEnum.TABLE.getParamSign() + ") AND relnamespace IN \n        ( \n                SELECT oid FROM pg_namespace WHERE nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + "\n        ) \n) AS COUNT_TAB") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    CREATE("CREATE TABLE 《schema》.{table}(\n【  1:(PRIMARY KEY ({primaryColumn}))|2:({column} {dataType} [[NOT] [NULL]] [DEFAULT {defaultValue}]),\n】)"),
    CREATE_TABLE("<CREATE> <TABLE> 《schema》.{table} <(>\n【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】\n<)>") {
        public String createIncrement(String sqlFrame, Map<String, String> paramsMap) {
            if (StringUtil.isNotEmpty((String)paramsMap.get("[AUTO_INCREMENT]"))) {
                sqlFrame = super.createIncrement(sqlFrame, paramsMap).replace("{dataType}", "[SERIAL]");
            }

            return sqlFrame;
        }

        public String createIndex() {
            String model = "CREATE UNIQUE INDEX {indexName} ON {table}(【column】)";
            return null;
        }
    },
    COMMENT_TABLE("COMMENT ON TABLE 《schema》.{table} IS {comment}"),
    COMMENT_COLUMN("COMMENT ON COLUMN 《schema》.{table}.{column} IS {comment}"),
    DROP_TABLE("DROP TABLE IF EXISTS 《schema》.{table}"),
    ADD_COLUMN("<ALTER> <TABLE> 《schema》.{table} ADD {column} {dataType}"),
    DROP_COLUMN("ALTER TABLE 《schema》.{table} DROP {column}"),
    MODIFY_TYPE("<ALTER> <TABLE> 《schema》.{table} <ALTER> <COLUMN> {column} <TYPE> {dataType}"),
    RE_COLUMN_NAME("<ALTER> <TABLE> 《schema》.{table} RENAME <COLUMN> {oldColumn} <TO> {newColumn}"),
    RE_TABLE_NAME("<ALTER> <TABLE> 《schema》.{oldTable} RENAME <TO> {newTable}"),
    ALTER_NOT_NULL("<ALTER> <TABLE> 《schema》.{table} <MODIFY> {column} {datatype} [[NOT] [NULL]]"),
    ALTER_DEFAULT("<ALTER> <TABLE> 《schema》.{table} <ALTER> {column} <SET> <DEFAULT> {defaultValue}"),
    ALTER_PRIMARY("<ALTER> <TABLE> 《schema》.{table} <ADD> CONSTRAINT {primaryKey} PRIMARY KEY (【{column},】)"),
    INSERT("INSERT INTO 《schema》.{table}(【{column},】) VALUES (【{value},】)"),
    DELETE_INFO("DELETE FROM 《schema》.{table} WHERE {column} = {value}"),
    ORDER_PAGE("{selectSql} ORDER BY {orderColumn} [DESC] LIMIT {pageSize} OFFSET {beginIndex}"),
    CREATE_DATABASE("CREATE DATABASE \"{database}\""),
    DROP_DATABASE("DROP DATABASE [IF EXISTS] {database}"),
    CREATE_SCHEMA("CREATE SCHEMA \"{schema}\";"),
    DROP_SCHEMA("DROP SCHEMA \"{schema}\" CASCADE");

    private String sqlFrame;
    private final String dbEncode;

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    public String getDbEncode() {
        this.getClass();
        return "PostgreSQL";
    }

    private SqlPostgreSQLEnum(String sqlFrame) {
        this.dbEncode = "PostgreSQL";
        this.sqlFrame = sqlFrame;
    }
}
