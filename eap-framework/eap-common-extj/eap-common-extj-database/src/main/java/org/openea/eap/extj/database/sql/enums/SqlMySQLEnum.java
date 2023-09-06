package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * MySQL SQL语句模板
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum SqlMySQLEnum implements SqlFrameBase {


    /* =============================== 系统语句 ==================================== */
    FIELDS("SELECT\n" +
            "\tNUMERIC_SCALE AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n" +
            "\tNUMERIC_PRECISION AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n" +
            "\tCHARACTER_MAXIMUM_LENGTH AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n" +
            "\tCOLUMN_NAME AS " + DbAliasEnum.FIELD.getAlias() + ",\n" +
            "\tDATA_TYPE AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n" +
            "\tCOLUMN_COMMENT AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n" +
            "\tCOLUMN_DEFAULT AS " + DbAliasEnum.DEFAULT_VALUE.getAlias() + ",\n" +
            "  IF( EXTRA = 'auto_increment', '1', '0') AS " + DbAliasEnum.AUTO_INCREMENT.getAlias() + ",\n" +
            "  IF( IS_NULLABLE = 'YES', '1', '0' ) AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n" +
            "  IF( COLUMN_KEY = 'PRI', '1', '0' ) AS " + DbAliasEnum.PRIMARY_KEY.getAlias() + " \n" +
            "FROM\n" +
            "\tINFORMATION_SCHEMA.COLUMNS \n" +
            "WHERE\n" +
            "\tTABLE_NAME = " + ParamEnum.TABLE.getParamSign()+ "\n" +
            "\tAND TABLE_SCHEMA = " + ParamEnum.DB_SCHEMA.getParamSign() + " \n" +
            "ORDER BY\n" +
            "\tORDINAL_POSITION;"
    ) {
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
            list.add(dbStruct.getMysqlDbName());
        }
},
    TABLES(
            "SELECT\n" +
            "\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
            "\ttable_rows AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n" +
            "\tdata_length AS " + DbAliasEnum.TABLE_SIZE.getAlias() + ",\n" +
            "\ttable_comment AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\n" +
            "FROM\n" +
            "\tINFORMATION_SCHEMA.TABLES \n" +
            "WHERE\n" +
            "\tTABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
            "AND \n" +
            "\tTABLE_TYPE != 'VIEW'"
    ) {
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
        }
    },
    TABLESANDVIEW(
            "SELECT\n" +
                    "\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
                    "\ttable_rows AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n" +
                    "\tdata_length AS " + DbAliasEnum.TABLE_SIZE.getAlias() + ",\n" +
                    "\ttable_comment AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",\n" +
                    "\ttable_type AS " + DbAliasEnum.TABLE_TYPE.getAlias() + "\n" +
                    "FROM\n" +
                    "\tINFORMATION_SCHEMA.TABLES \n" +
                    "WHERE\n" +
                    "\tTABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + "\n"
    ) {
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
        }
    },
    TABLE(
            TABLES.sqlFrame + "AND table_name = " + ParamEnum.TABLE.getParamSign()
    ) {
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT(*) AS TOTAL FROM (" +
                    "SELECT "
                    + "table_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + " "
                    + "FROM "
                    + "information_schema.TABLES "
                    + "WHERE "
                    + "TABLE_SCHEMA = " + ParamEnum.DB_NAME.getParamSign() + " "
                    + "and "
                    + "table_name = " + ParamEnum.TABLE.getParamSign()
                    + ") AS COUNT_TAB"
    ) {
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getMysqlDbName());
            list.add(table);
        }
    },
    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    CREATE_TABLE("<CREATE> <TABLE> {table} <(>\n" +
            "【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>] [AUTO_INCREMENT] [<COMMENT> {comment}]】" +
            "<)> "),

    CREATE(
            "<CREATE> <TABLE> {table} <(>\n" +
                    "【\r" +
                    "1:(<PRIMARY> <KEY> <(>{primaryColumn}<)> [<USING> {useValue}])|" +
                    "3:(<INDEX> {indexValue} <(>{column}<)>)|" +
                    "2:({column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}])" +
                    ",\n】" +
                    "<)> [<COMMENT> <=> {comment}]"
    ),
    DROP_TABLE("<DROP> <TABLE> [<IF> <EXISTS>] {table}"),
    /**
     * 表重命名
     */
    RE_TABLE_NAME("ALTER TABLE {oldTable} RENAME [TO] {newTable}"),
    ALTER_ADD_MODIFY(
            "<ALTER> <TABLE> {table} <ADD|MODIFY> [COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"
    ),
    ALTER_ADD_MODIFY_MULTI(
            "<ALTER> <TABLE> {table} <ADD|MODIFY><(>\n" +
                    "【[COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}],\n】" +
                    "<)>"
    ),
    ALTER_CHANGE(
            "<ALTER> <TABLE> {table} <CHANGE> {oldColumn} {newColumn} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"
    ),
    COMMENT_TABLE("<ALTER> <TABLE> {table} <COMMENT> = '{comment}'"),
    COMMENT_COLUMN("<ALTER> <TABLE> {table} <MODIFY> `{column}` {dataType} [DEFAULT {defaultValue}] <COMMENT> '{comment}'"),

    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
    /*mysql可以用 SELECT SQL_CALC_FOUND_ROWS * FROM table
        LIMIT index;SELECT FOUND_ROWS();方法获得两个结果集*/
    SELECT_TABLE("SELECT * FROM {table}"),
    DB_TIME_SQL("SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') as TIME"),
    COUNT_SIZE("SELECT COUNT(*) AS {totalAlias} FROM ({selectSql}) JNPF_TABLE"),
    COUNT_TABLE_SIZE(COUNT_SIZE.sqlFrame.replace("({selectSql})", "{table}")),
    INSERT("<INSERT> <INTO> {table} ([【{column},】]) <VALUES> (【{value},】)"),
    DELETE_ALL("DELETE FROM {table}"),
    /* =============================== 后缀 ==================================== */
    // LIMIT必须在ORDER之后，在前报错。顺序先排序再分页。
    ORDER_PAGE("{selectSql} ORDER BY {orderColumn} [DESC] LIMIT {beginIndex},{pageSize}"),
    PAGE("{selectSql} LIMIT {beginIndex},{pageSize}"),
    ORDER("{selectSql} ORDER BY {column} [DESC]"),
    LIKE("{selectSql} WHERE {column} like {condition}"),


    ;

    /**
     *
     */
    private final String sqlFrame;
    private final String dbEncode = DbBase.MYSQL;

}
