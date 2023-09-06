package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Oracle SQL语句模板
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum SqlOracleEnum implements SqlFrameBase {

    /* =============================== 系统语句 ==================================== */
    FIELDS(
            "SELECT \n" +
                    "\tA.COLUMN_NAME AS " + DbAliasEnum.FIELD.getAlias() + ",\n" +
                    "\tA.DATA_TYPE AS " + DbAliasEnum.DATA_TYPE.getAlias() + ",\n" +
                    "\tA.DATA_LENGTH AS " + DbAliasEnum.CHAR_LENGTH.getAlias() + ",\n" +
                    "\tA.DATA_PRECISION AS " + DbAliasEnum.NUM_PRECISION.getAlias() + ",\n" +
                    "\tA.DATA_SCALE AS " + DbAliasEnum.NUM_SCALE.getAlias() + ",\n" +
                    "\tCASE WHEN E.CONSTRAINT_TYPE IS NOT NULL THEN '1' ELSE '0' END AS " + DbAliasEnum.PRIMARY_KEY.getAlias() + ",\n" +
                    "\tCASE A.NULLABLE WHEN 'N' THEN '0' ELSE '1' END AS " + DbAliasEnum.ALLOW_NULL.getAlias() + ",\n" +
                    "\t(SELECT COUNT(*) FROM ALL_TRIGGERS WHERE TABLE_NAME = A.TABLE_NAME AND TABLE_OWNER = A.OWNER AND INSTR(TRIGGER_NAME, 'AUTO_') >0) AS " + DbAliasEnum.AUTO_TRIGGER.getAlias() + ",\n" +
                    "\tB.COMMENTS AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + "\n" +
                    "FROM \n" +
                    "\tALL_TAB_COLUMNS A -- 表&字段 OWNER、TABLE_NAME、COLUMN_NAME_\t\n" +
                    "LEFT JOIN \n" +
                    "\tALL_COL_COMMENTS B -- 字段注释 TABLE_NAME、COLUMN_NAME\n" +
                    "ON \n" +
                    "\tA.OWNER = B.OWNER AND A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME \n" +
                    "LEFT JOIN \n" +
                    "\t(SELECT \n" +
                    "\t\t\tC.OWNER, C.TABLE_NAME, C.COLUMN_NAME, D.CONSTRAINT_TYPE  \n" +
                    "\t\tFROM \n" +
                    "\t\t\tALL_CONS_COLUMNS C, ALL_CONSTRAINTS D \n" +
                    "\t\tWHERE \n" +
                    "\t\t\tC.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND D.CONSTRAINT_TYPE = 'P'\n" +
                    "\t) E\n" +
                    "ON\n" +
                    "\tA.OWNER = E.OWNER AND A.TABLE_NAME = E.TABLE_NAME AND A.COLUMN_NAME = E.COLUMN_NAME \n" +
                    "WHERE\n" +
                    "  A.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
                    "\tAND A.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
            list.add(table);
        }
    },
//    "SELECT atcs.table_name " + DbAliasConst.TABLE_NAME + ", atcs.comments " + DbAliasConst.TABLE_COMMENT + ", ats.num_rows " + DbAliasConst.TABLE_SUM + "\n" +
//            "FROM user_tab_comments atcs,all_tables ats WHERE atcs.table_name = ats.table_name AND ats.owner = '" + ParamEnum.DB_NAME.getParamSign() + "'"
    TABLES("SELECT atcs.table_name " + DbAliasEnum.TABLE_NAME.getAlias() + ", atcs.comments " + DbAliasEnum.TABLE_COMMENT.getAlias() + ", ats.num_rows " + DbAliasEnum.TABLE_SUM.getAlias() + "\n" +
            "FROM all_tab_comments atcs,all_tables ats WHERE atcs.table_name = ats.table_name AND ats.owner = atcs.owner AND ats.owner = " + ParamEnum.DB_NAME.getParamSign() + ""
//            "SELECT " +
//            "a.TABLE_NAME " + DbAliasConst.TABLE_NAME + ", " +
//            "b.COMMENTS " + DbAliasConst.TABLE_COMMENT + ", " +
//            "a.num_rows " + DbAliasConst.TABLE_SUM +
//            "\nFROM user_tables a, user_tab_comments b "
//            + "WHERE a.TABLE_NAME = b.TABLE_NAME "
            /*+ "and a.TABLESPACE_NAME='"+ ParamEnum.TABLE_SPACE.getTarget()+"'"*/
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
        }
    },
    TABLESANDVIEW("SELECT\n" +
            "\tatcs.table_name " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
            "\tatcs.comments " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",\n" +
            "\tatcs.table_type " + DbAliasEnum.TABLE_TYPE.getAlias() + " \n" +
            "FROM\n" +
            "\tall_tab_comments atcs\n" +
            "\tLEFT JOIN all_views alv ON alv.owner = atcs.owner \n" +
            "WHERE\n" +
            "\tatcs.owner = " + ParamEnum.DB_NAME.getParamSign()

    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
        }
    },
    TABLE(
            TABLES.sqlFrame + " AND ats.TABLE_NAME = " + ParamEnum.TABLE.getParamSign()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getOracleDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT(*) AS TOTAL FROM USER_TABLES WHERE USER_TABLES.TABLE_NAME = UPPER(" + ParamEnum.TABLE.getParamSign() + ")"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
        }
    },


    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop

    CREATE_TABLE    ("<CREATE> <TABLE> {table} <(>\n" +
                    "【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】" +
                    "<)> "),
    // 添加自增
    CREATE_AUTO_INCREMENT(
            "create sequence {table}_seq\n" +
                    "increment by 1\n" +
                    "START WITH 1\n" +
                    "NOMAXVALUE\n" +
                    "NOCYCLE\n" +
                    "NOCACHE"
    ),
    // 添加自增触发器
    CREATE_AUTO_INCREMENT_TRIGGER(
        "create or replace trigger AUTO_{table}_seq " +
                "before insert on {table} " +
                "for each row " +
                "begin" +
                "  select {table}_seq.nextval into:new.{autoInc_field} from dual;" +
                "end;"
    ),
    CREATE          ("CREATE TABLE 《schema》.{table}(\n" +
                    "【  " +
                    "1:(PRIMARY KEY ({primaryColumn}))|" +
                    "2:({column} {dataType} [[NOT] [NULL]] [DEFAULT {defaultValue}])" +
                    ",\n】)"
    ),
    DROP_TABLE      ("DROP TABLE 《schema》.{table}"),
    /**
     * 注意：Oracle DDL:COMMENT 无法在Mybatis Mapper.xml里面使用?占位符
     * 会出现：ORA-01780: 要求文字字符串
     */
    COMMENT_TABLE   ("COMMENT ON TABLE 《schema》.{table} IS '{comment}'"),
    COMMENT_COLUMN  ("COMMENT ON COLUMN 《schema》.{table}.{column} IS '{comment}'"),
    DROP_COLUMN     ("ALTER TABLE 《schema》.{table} DROP COLUMN {column}"),
    ADD_COLUMN      ("ALTER TABLE 《schema》.{table} ADD {column} {dataType}"),
    MODIFY_TYPE     ("ALTER TABLE 《schema》.{table} ALTER COLUMN {column} TYPE {dataType}"),
    ALTER_TABLE     ("ALTER TABLE 《schema》.{oldTable} RENAME TO {newTable}"),

    /* =============================== DML操作语句 ==================================== */
    /* （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
        注意：有一些Oracle版本中的语法子查询不支持别名关键词 AS */
    /*=============================== ALTER ====================================*/
    ORDER_PAGE      ("SELECT * FROM " +
                    "(SELECT JNPF_TEMP.*, ROWNUM JNPF_RowNo " +
                        "FROM " +
                            "({selectSql}) JNPF_TEMP " +
                        "ORDER BY {orderColumn} [DESC]) JNPF_TAB " +
                    "WHERE JNPF_TAB.JNPF_RowNo BETWEEN {beginIndex} AND {endIndex}"),
    DB_TIME_SQL     ("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as TIME from dual"),
    INSERT          ("INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)"),
    /**
     * 【注意：Oracle所有插入字符，也就是''之间的内容都会隐式转成varchar2类型，都不得超过4000】
     * 尾部用;结尾，不然会报错
     */
    CLOB_INSERT     ("DECLARE\n" +
                               "\tclobVal {table}.{column}%TYPE;\n" +
                               "BEGIN\t\n" +
                               "\tclobVal := '{value}';\n" +
                               "INSERT INTO {table} (【{clobColumn},{column},】) VALUES (【{clobVal},{value},】);\n" +
                               "END"),
    CLOB_UPDATE     (
                "DECLARE \n" +
                        "-- 此表为：{table}\n" +
                        "\tcontext NCLOB;\n" +
                        "BEGIN\n" +
                        "\tDBMS_LOB.CREATETEMPORARY(context,TRUE);\n" +
//                        "\tcontext := '' ;\n" +
                        "\t【DBMS_LOB.APPEND(context, '{context}');】\n" +
                        "\tUPDATE {table} SET {column} = context WHERE {key} = '{value}';\n" +
                        "END"
    ){
        @Override
        public String getFastSql(List<String> values){
            String sql = this.getSqlFrame();
            List<String> keys = Arrays.asList(
                    "{table}",
                    "{column}",
                    "\t【DBMS_LOB.APPEND(context, '{context}');】\n",
                    "{key}",
                    "{value}"
            );
            for (int i = 0; i < values.size(); i++) {
                if(values.get(i) != null){
                    sql = sql.replace(keys.get(i), values.get(i));
                }
            }
            return sql;
        }

    },
    CLOB_APPEND("DBMS_LOB.APPEND(context, '{context}')"),
    /* =============================== 其他 ==================================== */
    /**
     * oracle 时间格式转换
     */
    TO_TIME         ("TO_DATE('{datetime}','YYYY-MM-DD HH24:MI:SS')"),
    /**
     * 查看现有表空间信息
     */
    SELECT_TABLESPACE("SELECT TABLESPACE_NAME,FILE_ID,FILE_NAME,round(bytes/(1024*1024),0) total_space FROM DBA_DATA_FILES ORDER BY TABLESPACE_NAME"),
    /**
     * 创建临时表空间
     */
    CREATE_TEMP_TABLESPACE("CREATE TEMPORARY TABLESPACE UQSM_TEMP TEMPFILE '/{path}/{tempTablespaceName}.dbf' " +
            "size 8000m autoextend on next 50m maxsize unlimited extent management local;"),
    /**
     * 创建表空间
     */
    CREATE_TABLESPACE("\n" + "CREATE TABLESPACE UQSM_DATA LOGGING DATAFILE '/{path}/{tempTablespaceName}.dbf' " +
            "SIZE 8000m autoextendon next 50M maxsize unlimited extent management local;\n"),
    /**
     * 创建用户
     */
    CREATE_USER     ("CREATE USER {user} IDENTIFIED BY {password} DEFAULT TABLESPACE {tablespace} TEMPORARY TABLESPACE {tempTablespace}"),
    /**
     * 删除用户
     */
    DROP_USER       ("DROP USER {user}"),
    /**
     * 给用户授予权限
     */
    GRANT_ROLE      ("GRANT connect,RESOURCE,dba to {user};"),
    /**
     * 修改用户的密码
     */
    ALTER_USER_PASSWORD("ALTER USER {user} IDENTIFIED BY {password}"),
    ;

    private final String sqlFrame;
    private final String dbEncode = DbBase.ORACLE;

    public String getFastSql(List<String> values){
        return this.getSqlFrame();
    }

}
