package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.sql.model.DbStruct;

import java.util.List;
import java.util.Map;

public enum SqlKingbaseESEnum implements SqlFrameBase {
    FIELDS(SqlPostgreSQLEnum.FIELDS.getSqlFrame()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            SqlPostgreSQLEnum.FIELDS.setStructParams(table, dbStruct, list);
        }
    },
    TABLES("SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", 0 AS " + DbAliasEnum.TABLE_SUM.getAlias() + " FROM\ninformation_schema.TABLES AS t\nLEFT JOIN\n(SELECT TABLE_NAME,COMMENTS FROM DBA_TAB_COMMENTS)AS c\nON\nupper(t.TABLE_NAME) = upper(c.TABLE_NAME)\nWHERE\n TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    TABLESANDVIEW("SELECT t.table_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",t.table_type AS " + DbAliasEnum.TABLE_TYPE.getAlias() + "\n FROM information_schema.TABLES\n\tAS T LEFT JOIN ( SELECT TABLE_NAME, COMMENTS FROM DBA_TAB_COMMENTS ) AS C ON UPPER ( T.TABLE_NAME ) = UPPER ( C.TABLE_NAME ) \nWHERE\n\tTABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    SqlKingbaseESEnum("SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",c.COMMENTS AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", 0 AS " + DbAliasEnum.TABLE_SUM.getAlias() + " FROM\ninformation_schema.TABLES AS t\nLEFT JOIN\n(SELECT TABLE_NAME,COMMENTS FROM DBA_TAB_COMMENTS)AS c\nON\nupper(t.TABLE_NAME) = upper(c.TABLE_NAME)\nWHERE\n TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
        }
    },
    TABLE(TABLES.sqlFrame + " AND t.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE("SELECT COUNT (*) AS TOTAL FROM (SELECT t.TABLE_NAME AS " + DbAliasEnum.TABLE_NAME.getAlias() + " FROM\ninformation_schema.TABLES AS t WHERE TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign() + " and t.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + ") AS COUNT_TAB") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getKingBaseDbSchema());
            list.add(table);
        }
    },
    CREATE_DATABASE("CREATE DATABASE WITH owner=\"{database}\" "),
    DROP_DATABASE("DROP DATABASE \"{database}\""),
    CREATE_SCHEMA("CREATE SCHEMA AUTHORIZATION \"{schema}\""),
    DROP_SCHEMA("DROP SCHEMA \"{schema}\" CASCADE;"),
    DROP("DROP TABLE {table}"),
    DROP_TABLE(SqlPostgreSQLEnum.DROP_TABLE),
    COMMENT_COLUMN(SqlPostgreSQLEnum.COMMENT_COLUMN),
    COMMENT_TABLE(SqlPostgreSQLEnum.COMMENT_TABLE),
    CREATE(SqlPostgreSQLEnum.CREATE),
    CREATE_TABLE(SqlPostgreSQLEnum.CREATE_TABLE.getSqlFrame()) {
        public String createIncrement(String sqlFrame, Map<String, String> paramsMap) {
            return SqlPostgreSQLEnum.CREATE_TABLE.createIncrement(sqlFrame, paramsMap);
        }
    },
    ALTER_DROP("ALTER TABLE 《schema》.{table} DROP COLUMN {column}"),
    ALTER_ADD("ALTER TABLE 《schema》.{table} ADD COLUMN {column} {dataType}"),
    ALTER_TYPE("ALTER TABLE 《schema》.{table} ALTER COLUMN {column} TYPE {dataType}"),
    RE_TABLE_NAME(SqlPostgreSQLEnum.RE_TABLE_NAME),
    INSERT("INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)");

    private final String dbEncode;
    private String sqlFrame;

    private SqlKingbaseESEnum(SqlFrameBase sqlEnum) {
        this.dbEncode = "KingbaseES";
        this.sqlFrame = sqlEnum.getSqlFrame();
    }

    public String getDbEncode() {
        this.getClass();
        return "KingbaseES";
    }

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    private SqlKingbaseESEnum(String sqlFrame) {
        this.dbEncode = "KingbaseES";
        this.sqlFrame = sqlFrame;
    }
}

