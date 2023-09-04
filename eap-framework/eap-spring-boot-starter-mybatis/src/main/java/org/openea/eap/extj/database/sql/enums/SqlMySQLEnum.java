package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.sql.model.DbStruct;

import java.util.List;

public enum SqlMySQLEnum implements SqlFrameBase {
    FIELDS("SELECT\n\tNUMERIC_SCALE AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n\tNUMERIC_PRECISION AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n\tCHARACTER_MAXIMUM_LENGTH AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n\tCOLUMN_NAME AS " + DbAliasEnum.FIELD.getAlias() + ",\n\tDATA_TYPE AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n\tCOLUMN_COMMENT AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n\tCOLUMN_DEFAULT AS " + DbAliasEnum.DEFAULT_VALUE.getAlias() + ",\n  IF( EXTRA = 'auto_increment', '1', '0') AS " + DbAliasEnum.AUTO_INCREMENT.getAlias() + ",\n  IF( IS_NULLABLE = 'YES', '1', '0' ) AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n  IF( COLUMN_KEY = 'PRI', '1', '0' ) AS " + DbAliasEnum.PRIMARY_KEY.getAlias() + " \nFROM\n\tINFORMATION_SCHEMA.COLUMNS \nWHERE\n\tTABLE_NAME = " + ParamEnum.TABLE.getParamSign() + "\n\tAND TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign() + " \nORDER BY\n\tORDINAL_POSITION;") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
            list.add(dbStruct.getMysqlDbName());
        }
    },
    TABLES("SELECT\n\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n\ttable_rows AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n\tdata_length AS " + DbAliasEnum.TABLE_SIZE.getAlias() + ",\n\ttable_comment AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\nFROM\n\tINFORMATION_SCHEMA.TABLES \nWHERE\n\tTABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + "\nAND \n\tTABLE_TYPE != 'VIEW'") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
        }
    },
    TABLESANDVIEW("SELECT\n\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n\ttable_rows AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n\tdata_length AS " + DbAliasEnum.TABLE_SIZE.getAlias() + ",\n\ttable_comment AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",\n\ttable_type AS " + DbAliasEnum.TABLE_TYPE.getAlias() + "\nFROM\n\tINFORMATION_SCHEMA.TABLES \nWHERE\n\tTABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + "\n") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
        }
    },
    TABLE(TABLES.sqlFrame + "AND table_name = " + ParamEnum.TABLE.getParamSign()) {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
            list.add(table);
        }
    },
    EXISTS_TABLE("SELECT COUNT(*) AS TOTAL FROM (SELECT table_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + " FROM information_schema.TABLES WHERE TABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + " and table_name = " + ParamEnum.TABLE.getParamSign() + ") AS COUNT_TAB") {
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
            list.add(table);
        }
    },
    CREATE_TABLE("<CREATE> <TABLE> {table} <(>\n【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>] [AUTO_INCREMENT] [<COMMENT> {comment}]】\n<)> "),
    CREATE("<CREATE> <TABLE> {table} <(>\n【\r1:(<PRIMARY> <KEY> <(>{primaryColumn}<)> [<USING> {useValue}])|3:(<INDEX> {indexValue} <(>{column}<)>)|2:({column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]),\n】<)> [<COMMENT> <=> {comment}]"),
    DROP_TABLE("<DROP> <TABLE> [<IF> <EXISTS>] {table}"),
    RE_TABLE_NAME("ALTER TABLE {oldTable} RENAME [TO] {newTable}"),
    ALTER_ADD_MODIFY("<ALTER> <TABLE> {table} <ADD|MODIFY> [COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"),
    ALTER_ADD_MODIFY_MULTI("<ALTER> <TABLE> {table} <ADD|MODIFY><(>\n【[COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}],\n】<)>"),
    ALTER_CHANGE("<ALTER> <TABLE> {table} <CHANGE> {oldColumn} {newColumn} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"),
    COMMENT_TABLE("<ALTER> <TABLE> {table} <COMMENT> = '{comment}'"),
    COMMENT_COLUMN("<ALTER> <TABLE> {table} <MODIFY> `{column}` {dataType} [DEFAULT {defaultValue}] <COMMENT> '{comment}'"),
    SELECT_TABLE("SELECT * FROM {table}"),
    DB_TIME_SQL("SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') as TIME"),
    COUNT_SIZE("SELECT COUNT(*) AS {totalAlias} FROM ({selectSql}) JNPF_TABLE"),
    COUNT_TABLE_SIZE(COUNT_SIZE.sqlFrame.replace("({selectSql})", "{table}")),
    INSERT("<INSERT> <INTO> {table} ([【{column},】]) <VALUES> (【{value},】)"),
    DELETE_ALL("DELETE FROM {table}"),
    ORDER_PAGE("{selectSql} ORDER BY {orderColumn} [DESC] LIMIT {beginIndex},{pageSize}"),
    PAGE("{selectSql} LIMIT {beginIndex},{pageSize}"),
    ORDER("{selectSql} ORDER BY {column} [DESC]"),
    LIKE("{selectSql} WHERE {column} like {condition}");

    private final String sqlFrame;
    private final String dbEncode;

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    public String getDbEncode() {
        this.getClass();
        return "MySQL";
    }

    private SqlMySQLEnum(String sqlFrame) {
        this.dbEncode = "MySQL";
        this.sqlFrame = sqlFrame;
    }
}

