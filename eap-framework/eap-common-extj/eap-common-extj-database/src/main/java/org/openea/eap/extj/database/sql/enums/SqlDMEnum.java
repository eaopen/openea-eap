package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 达梦 SQL语句模板
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum SqlDMEnum implements SqlFrameBase {

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
                "\tB.COMMENTS AS " + DbAliasEnum.FIELD_COMMENT.getAlias() + ",\n" +
                "\tH.IS_AUTO AS " + DbAliasEnum.AUTO_INCREMENT.getAlias() + "\n" +
                "FROM \n" +
                "\tALL_TAB_COLUMNS A -- 表&字段 OWNER、TABLE_NAME、COLUMN_NAME_\t\n" +
                "LEFT JOIN \n" +
                "\tALL_COL_COMMENTS B -- 字段注释 TABLE_NAME、COLUMN_NAME\n" +
                "ON \n" +
                "\tA.OWNER = B.OWNER AND A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME \n" +
                "LEFT JOIN \n" +
                "\t(\n" +
                "\t\tSELECT \n" +
                "\t\t\tC.OWNER, C.TABLE_NAME, C.COLUMN_NAME, D.CONSTRAINT_TYPE  \n" +
                "\t\tFROM \n" +
                "\t\t\tALL_CONS_COLUMNS C\n" +
                "\t\tLEFT JOIN \n" +
                "\t\t\tALL_CONSTRAINTS D \n" +
                "\t\tON \n" +
                "\t\t\tC.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND D.CONSTRAINT_TYPE = 'P'\n" +
                "\t\tWHERE\n" +
                "\t\t\tC.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
                "\t\t\tAND C.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + "\n" + // 添加模式与表，增加查询效率
                "\t) E\n" +
                "ON\n" +
                "\tA.OWNER = E.OWNER AND A.TABLE_NAME = E.TABLE_NAME AND A.COLUMN_NAME = E.COLUMN_NAME \n" +
                "LEFT JOIN \n" +
                "\t(\n" +
                "\t\tSELECT \n"+
                "\t\t\tF.NAME, DECODE(F.INFO2,1,'1','0') AS IS_AUTO \n" +
                "\t\tFROM \n"+
                "\t\t\tSYS.SYSCOLUMNS F\n" +
                "\t\tWHERE\n" +
                "\t\t\tID ="+
                "\t(\n"+
                "\t\tSELECT \n"+
                "\t\t\tOBJECT_ID  \n" +
                "\t\tFROM \n" +
                "\t\t\tDBA_OBJECTS G \n"+
                "\t\tWHERE \n" +
                "\t\t\tG.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
                "\t\t\tAND G.OBJECT_NAME = " + ParamEnum.TABLE.getParamSign() + "\n" +
                "\t\t\tAND G.OBJECT_TYPE = 'TABLE') \n" +
                "\t) H\n" +
                "ON \n" +
                "\tA.COLUMN_NAME = H.NAME \n" +
                "WHERE\n" +
                "  A.OWNER = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
                "\tAND A.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + "\n" +
                "\tORDER BY A.COLUMN_ID"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },
    TABLES(
            // 作用:DBMS_STATS.GATHER_TABLE_STATS统计表,列,索引的统计信息（默认参数下是对表进行直方图信息收集，
            // 包含该表的自身-表的行数、数据块数、行长等信息；列的分析--列值的重复数、列上的空值、数据在列上的分布情况；
            // 索引的分析-索引页块的数量、索引的深度、索引聚合因子）.
//            "dbms_stats.GATHER_SCHEMA_stats (" + ParamEnum.DB_SCHEMA.getParamSign() +");\n" +
            "SELECT\n" +
            "ut.TABLE_NAME " + DbAliasEnum.TABLE_NAME.getAlias() + ",utc.COMMENTS " + DbAliasEnum.TABLE_COMMENT.getAlias() + "\n" +
            ",ut.num_rows " + DbAliasEnum.TABLE_SUM.getAlias() + "\n" +
            "FROM ALL_TABLES AS ut\n" +
            "LEFT JOIN\n" +
            "all_tab_comments AS utc\n" +
            "ON\n" +
            "ut.TABLE_NAME = utc.TABLE_NAME AND ut.OWNER = utc.OWNER\n" +
            "WHERE ut.OWNER = " + ParamEnum.DB_SCHEMA.getParamSign() + "\n" +
            "ORDER BY F_TABLE_NAME;"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
        }
    },
    TABLESANDVIEW(
            // 作用:DBMS_STATS.GATHER_TABLE_STATS统计表,列,索引的统计信息（默认参数下是对表进行直方图信息收集，
            // 包含该表的自身-表的行数、数据块数、行长等信息；列的分析--列值的重复数、列上的空值、数据在列上的分布情况；
            // 索引的分析-索引页块的数量、索引的深度、索引聚合因子）.
//            "dbms_stats.GATHER_SCHEMA_stats (" + ParamEnum.DB_SCHEMA.getParamSign() +");\n" +
            " select\tTABLE_NAME as " + DbAliasEnum.TABLE_NAME.getAlias() +
                    ",'TABLE' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " from all_tab_comments WHERE owner = " + ParamEnum.DB_NAME.getParamSign() + "\n" +
                    " UNION\n" +
                    " select\t view_name as " + DbAliasEnum.TABLE_NAME.getAlias() +
                    ",'VIEW' as " + DbAliasEnum.TABLE_TYPE.getAlias() + " from all_views WHERE owner = " + ParamEnum.DB_NAME.getParamSign()
//            "select atcs.table_name " + DbAliasEnum.TABLE_NAME.getAlias()
//                    + ",atcs.comments "  + DbAliasEnum.TABLE_COMMENT.getAlias()
//                    + ",atcs.table_type  " + DbAliasEnum.TABLE_TYPE.getAlias()
//                    + "\nfrom all_views alv left join all_tab_comments atcs on alv.owner = atcs.owner where atcs.owner = " + ParamEnum.DB_NAME.getParamSign() + ""
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(dbStruct.getPostGreDbSchema());
        }
    },
    TABLE(
            TABLES.sqlFrame.replace("ORDER BY", "AND ut.TABLE_NAME = " + ParamEnum.TABLE.getParamSign() + " ORDER BY")
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT (*) AS TOTAL FROM (" +
            "SELECT\n" +
            "ut.TABLE_NAME " + DbAliasEnum.TABLE_NAME.getAlias() + " FROM ALL_TABLES AS ut\n" +
            "WHERE ut.OWNER = " + ParamEnum.DB_SCHEMA.getParamSign() +
            " and ut.TABLE_NAME = "+ParamEnum.TABLE.getParamSign()
            + ") AS COUNT_TAB"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getDmDbSchema());
            list.add(table);
        }
    },


    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    /*=============================== ALTER ====================================*/
    CREATE_TABLE    (
            "<CREATE> <TABLE> {table}<(>\n" +
                    "【{column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}] [<PRIMARY KEY>]】" +
                    "\n<)>"
    ){
        @Override
        public String createIncrement(String sqlFrame,  Map<String, String> paramsMap) {
            // 自增标识：IDENTITY(1, 1)
            if (StringUtil.isNotEmpty(paramsMap.get("[AUTO_INCREMENT]"))){
                sqlFrame = super.createIncrement(sqlFrame, paramsMap)
                        .replace("{dataType}", "{dataType} IDENTITY(1, 1)");
                String openIdentity = OPEN_IDENTITY.sqlFrame
                        .replace("{table}", paramsMap.get("{table}"))
                        .replace("<ON|OFF>", "ON");
                return sqlFrame + ";\n" + openIdentity;
            }
            return sqlFrame;
        }
    },
    OPEN_IDENTITY("SET IDENTITY_INSERT {table} <ON|OFF>"),
    ALTER_DROP("ALTER TABLE 《schema》.{table} DROP COLUMN {column}"),
    /**
     * 添加字段
     */
    ALTER_ADD("ALTER TABLE 《schema》.{table} ADD {column} {dataType}"),
    /**
     * 修改字段
     */
    ALTER_TYPE("ALTER TABLE 《schema》.{table} <MODIFY> {column} {dataType}"),
    /**
     * 修改表名
     */
    ALTER_TABLE_NAME("ALTER TABLE 《schema》.{table} ALTER column {oldColumn} RENAME TO {newColumn};"),


    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。

    DB_TIME_SQL     ("select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') as TIME "),
    INSERT(
            "INSERT INTO 《schema》.{table} (【{column},】) VALUES (【{value},】)"
    ),
    /**
     *
     */
    CREATE_SCHEMA(
            "CREATE SCHEMA \"{schema}\";"
    ),
    DROP_SCHEMA(
            "DROP SCHEMA \"{schema}\" RESTRICT"
    ),
    DROP_TABLE(
            SqlPostgreSQLEnum.DROP_TABLE
            ),
    COMMENT_COLUMN(
            SqlPostgreSQLEnum.COMMENT_COLUMN
            ),
    COMMENT_TABLE(
            SqlPostgreSQLEnum.COMMENT_TABLE
            ),
    CREATE(
            SqlPostgreSQLEnum.CREATE
            ),



    /*=============================== 其他 ====================================*/

    ;

    private final String dbEncode = DbBase.DM;
    private final String sqlFrame;

    SqlDMEnum(SqlFrameBase sqlEnum){
        this.sqlFrame = sqlEnum.getSqlFrame();
    }

}
