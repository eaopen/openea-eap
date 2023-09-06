package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Postgre SQL语句模板
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum SqlPostgreSQLEnum implements SqlFrameBase {

    /* =============================== 系统语句 ==================================== */
    FIELDS(
            "SELECT\n" +
                    "\tcol.column_name AS " + DbAliasEnum.FIELD.getAlias() + ",\n" +
                    "\tcol.udt_name AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n" +
                    "\tis_nullable AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n" +
                    "\tcol_description (pa.pg_oid, attnum) " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n" +
                    "\tcharacter_maximum_length AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n" +
                    "\tnumeric_precision AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n" +
                    "\tnumeric_scale AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n" +
                    "\tcolumn_default AS " + DbAliasEnum.COLUMN_DEFAULT.getAlias() + ",\n" +
                    "\ttable_name AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
                    "\t(CASE WHEN ( SELECT pa.attnum = ANY ( conkey ) FROM pg_constraint WHERE conrelid = pa.pg_oid AND contype = 'p' ) = 't' \n" +
                    "\tTHEN 1 ELSE 0 END ) " + DbAliasEnum.PRIMARY_KEY.getAlias() + "\t\n" +
                    "FROM\n" +
                    "\tinformation_schema.COLUMNS AS col\n" +
                    "LEFT JOIN (\n" +
                    "\tSELECT\n" +
                    "\t\t\t\t\t* \n" +
                    "\tFROM\n" +
                    "    (SELECT *,oid AS pg_oid FROM pg_class WHERE relnamespace IN ( SELECT oid FROM pg_namespace WHERE nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + " )) AS pc\n" +
                    "\tLEFT JOIN\n" +
                    "\t  pg_attribute AS pat \n" +
                    "\tON \n" +
                    "\t\tpat.attrelid = pc.pg_oid\n" +
                    "\tWHERE \n" +
                    "\t\t\tpc.relname = " + ParamEnum.TABLE.getParamSign() + "\n" +
                    "\t) AS pa \n" +
                    "ON \n" +
                    "\tpa.attname = col.column_name \n" +
                    "WHERE\n" +
                    "\tcol.table_schema = " + ParamEnum.DB_SCHEMA.getParamSign() + " \n" +
                    "\tAND TABLE_NAME = " + ParamEnum.TABLE.getParamSign()

    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
        }
    },
    /* POSITION()函数返回一个整数，该整数表示子字符串在字符串中的位置。如果在字符串中未找到子字符串，则POSITION()函数将返回零(0)。
       如果子字符串或字符串参数为null，则返回null */
    TABLES(
            "SELECT\n" +
                    "        pt.*,\n" +
                    "        pg_tab.relname AS " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
                    "        pg_tab.reltuples AS " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n" +
                    "        pg_tab.nspname,\n" +
                    "        CAST ( obj_description ( pg_tab.relfilenode, 'pg_class' ) AS VARCHAR ) AS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\n" +
                    "FROM\n" +
                    "        pg_tables pt\n" +
                    "LEFT JOIN        \n" +
                    "(SELECT \n" +
                    "        * \n" +
                    "FROM\n" +
                    "        pg_class pc\n" +
                    "LEFT JOIN \n" +
                    "        pg_namespace pns\n" +
                    "ON\n" +
                    "          pns.oid = pc.relnamespace\n" +
                    "WHERE\n" +
                    "         pns.nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + ") AS pg_tab\n" +
                    "ON\n" +
                    "        pt.tablename = pg_tab.relname\n" +
                    "WHERE pt.schemaname = " + ParamEnum.DB_SCHEMA.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLESANDVIEW(
            "SELECT viewname as " + DbAliasEnum.TABLE_NAME.getAlias() + ", 'VIEW' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " FROM pg_views WHERE schemaname = " + ParamEnum.DB_SCHEMA.getParamSign() + "\n" +
                    "UNION\n" +
                    "SELECT tablename as " + DbAliasEnum.TABLE_NAME.getAlias() + ",'TABLE' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " FROM pg_tables WHERE schemaname = " + ParamEnum.DB_SCHEMA.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLE(
        TABLES.sqlFrame + "AND pg_tab.relname = " + ParamEnum.TABLE.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getPostGreDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT (*) AS TOTAL \n" +
                    "FROM\n" +
                    "( \n" +
                    "        SELECT relname AS F_TABLE_NAME FROM pg_class C WHERE relname = lower(" + ParamEnum.TABLE.getParamSign() + ") AND relnamespace IN \n" +
                    "        ( \n" +
                    "                SELECT oid FROM pg_namespace WHERE nspname = " + ParamEnum.DB_SCHEMA.getParamSign() + "\n" +
                    "        ) \n" +
                    ") AS COUNT_TAB"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
            list.add(dbStruct.getPostGreDbSchema());
        }
    },

    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    /**
     * 建表语句
     */
    CREATE(
            "CREATE TABLE 《schema》.{table}(\n" +
                    "【  " +
                    "1:(PRIMARY KEY ({primaryColumn}))|" +
                    "2:({column} {dataType} [[NOT] [NULL]] [DEFAULT {defaultValue}])" +
                    ",\n】)"
    ),
    // SERIAL自增标识，自增时：1、不定义数据类型；2、不定义默认值；3、非空；
    CREATE_TABLE    ("<CREATE> <TABLE> 《schema》.{table} <(>\n" +
            "【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】" +
            "\n<)>"){
        @Override
        public String createIncrement(String sqlFrame,  Map<String, String> paramsMap) {
            // 自增标识：SERIAL
            if(StringUtil.isNotEmpty(paramsMap.get("[AUTO_INCREMENT]"))){
                sqlFrame = super.createIncrement(sqlFrame, paramsMap)
                        .replace("{dataType}", "[SERIAL]");
            }
            return sqlFrame;
        }
    },
    COMMENT_TABLE   ("COMMENT ON TABLE 《schema》.{table} IS {comment}"),
    COMMENT_COLUMN  ("COMMENT ON COLUMN 《schema》.{table}.{column} IS {comment}"),
    DROP_TABLE      ("DROP TABLE IF EXISTS 《schema》.{table}"),
    ADD_COLUMN      ("<ALTER> <TABLE> 《schema》.{table} ADD {column} {dataType}"),
    DROP_COLUMN     ("ALTER TABLE 《schema》.{table} DROP {column}"),
    MODIFY_TYPE     ("<ALTER> <TABLE> 《schema》.{table} <ALTER> <COLUMN> {column} <TYPE> {dataType}"),
    RE_COLUMN_NAME   ("<ALTER> <TABLE> 《schema》.{table} RENAME <COLUMN> {oldColumn} <TO> {newColumn}"),
    RE_TABLE_NAME   ("<ALTER> <TABLE> 《schema》.{oldTable} RENAME <TO> {newTable}"),
    /**
     * 修改: NOT NULL 约束
     */
    ALTER_NOT_NULL  ("<ALTER> <TABLE> 《schema》.{table} <MODIFY> {column} {datatype} [[NOT] [NULL]]"),
    /**
     * 修改: 默认值
     */
    ALTER_DEFAULT   ("<ALTER> <TABLE> 《schema》.{table} <ALTER> {column} <SET> <DEFAULT> {defaultValue}"),
    /**
     * 添加: 主键约束
     */
    ALTER_PRIMARY   ("<ALTER> <TABLE> 《schema》.{table} <ADD> CONSTRAINT {primaryKey} PRIMARY KEY (【{column},】)"),

    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
    INSERT          ("INSERT INTO 《schema》.{table}(【{column},】) VALUES (【{value},】)"),
    DELETE_INFO     ("DELETE FROM 《schema》.{table} WHERE {column} = {value}"),
    ORDER_PAGE     ("{selectSql} ORDER BY {orderColumn} [DESC] LIMIT {pageSize} OFFSET {beginIndex}"),

    /* =============================== 其他 ==================================== */


    CREATE_DATABASE("CREATE DATABASE \"{database}\""),
    DROP_DATABASE("DROP DATABASE [IF EXISTS] {database}"),
    CREATE_SCHEMA("CREATE SCHEMA \"{schema}\";"),
    DROP_SCHEMA("DROP SCHEMA \"{schema}\" CASCADE"),
    ;

    private String sqlFrame;
    private final String dbEncode = DbBase.POSTGRE_SQL;

}
