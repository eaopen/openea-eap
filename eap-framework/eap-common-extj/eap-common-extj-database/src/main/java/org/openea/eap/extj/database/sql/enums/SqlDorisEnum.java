package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 类功能
 *
 */
@Getter
@AllArgsConstructor
public enum SqlDorisEnum implements SqlFrameBase {

    /* =============================== 系统语句 ==================================== */
    FIELDS(
            SqlMySQLEnum.FIELDS.getSqlFrame()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            SqlMySQLEnum.FIELDS.setStructParams(table, dbStruct, list);
        }
    },
    TABLES(
            SqlMySQLEnum.TABLES.getSqlFrame()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            SqlMySQLEnum.TABLES.setStructParams(table, dbStruct, list);
        }
    },
    TABLE(
            SqlMySQLEnum.TABLE.getSqlFrame()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            SqlMySQLEnum.TABLE.setStructParams(table, dbStruct, list);
        }
    },
    EXISTS_TABLE(
            SqlMySQLEnum.EXISTS_TABLE.getSqlFrame()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            SqlMySQLEnum.EXISTS_TABLE.setStructParams(table, dbStruct, list);
        }
    },
    /* =============================== DDL:定义语句 ==================================== */
    /**
     * UNIQUE KEY ({primary_column}) 当字段做唯一索引的时候，必须在字段排列第一排，不然
     * 报错：1105 - errCode = 2, detailMessage = Key columns should be a ordered prefix of the schema.
     */
    CREATE_TABLE    (
            "<CREATE> <TABLE> {table} <(>\n" +
                    "【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [AUTO_INCREMENT] [<COMMENT> {comment}]】\n" +
                    "<)>\n" +
                    "UNIQUE KEY ({primary_column})\n" +
                    "COMMENT '{tableComment}'\n" +
                    "DISTRIBUTED BY HASH({tablet_column}) BUCKETS 1\n" +
                    "PROPERTIES ('replication_num' = '1')"
            ),

    CREATE(
            "<CREATE> <TABLE> {table} <(>\n" +
                    "【\r" +
                    "1:(<PRIMARY> <KEY> <(>{primaryColumn}<)> [<USING> {useValue}])|" +
                    "3:(<INDEX> {indexValue} <(>{column}<)>)|" +
                    "2:({column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}])" +
                    ",\n】" +
                    "<)> [<COMMENT> <=> {comment}]"
    ),
    DROP_TABLE      ("<DROP> <TABLE> [<IF> <EXISTS>] {table}"),
    /**
     * 表重命名
     */
    RE_TABLE_NAME   ("ALTER TABLE {oldTable} RENAME {newTable}"),
    ALTER_ADD_MODIFY(
            "<ALTER> <TABLE> {table} <ADD|MODIFY> [COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"
    ),
    ALTER_ADD_MODIFY_MULTI(
            "<ALTER> <TABLE> {table} <ADD|MODIFY><(>\n" +
                    "【[COLUMN] {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}],\n】" +
                    "<)>"
    ),
    ALTER_CHANGE    (
            "<ALTER> <TABLE> {table} <CHANGE> {oldColumn} {newColumn} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<COMMENT> {comment}]"
    ),
    COMMENT_TABLE   ("<ALTER> <TABLE> {table} <COMMENT> = '{comment}'"),
    COMMENT_COLUMN  ("<ALTER> <TABLE> {table} <MODIFY> `{column}` {dataType} [DEFAULT {defaultValue}] <COMMENT> '{comment}'"),
    DELETE_ALL      ("DELETE FROM base_authorize WHERE {idColumn} != null"),


    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
    /*mysql可以用 SELECT SQL_CALC_FOUND_ROWS * FROM table
        LIMIT index;SELECT FOUND_ROWS();方法获得两个结果集*/
    SELECT_TABLE    ("SELECT * FROM {table}"),
    DB_TIME_SQL     ("SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') as TIME"),
    COUNT_SIZE      ("SELECT COUNT(*) AS {totalAlias} FROM ({selectSql}) JNPF_TABLE"),
    COUNT_TABLE_SIZE(COUNT_SIZE.sqlFrame.replace("({selectSql})", "{table}")),
    INSERT          ("<INSERT> <INTO> {table} ([【{column},】]) <VALUES> (【{value},】)"),

    /* =============================== 后缀 ==================================== */
    // LIMIT必须在ORDER之后，在前报错。顺序先排序再分页。
    ORDER_PAGE      ("{selectSql} ORDER BY {orderColumn} [DESC] LIMIT {beginIndex},{pageSize}"),
    PAGE            ("{selectSql} LIMIT {beginIndex},{pageSize}"),
    ORDER           ("{selectSql} ORDER BY {column} [DESC]"),
    LIKE            ("{selectSql} WHERE {column} like {condition}"),

    ;

    private final String sqlFrame;
    private final String dbEncode = DbBase.DORIS;

}
