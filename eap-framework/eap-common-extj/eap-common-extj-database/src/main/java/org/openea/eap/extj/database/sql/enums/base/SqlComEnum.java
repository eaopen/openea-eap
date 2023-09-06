package org.openea.eap.extj.database.sql.enums.base;

import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.sql.enums.*;
import org.openea.eap.extj.database.sql.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.sql.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 通用 SQL语句模板
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum SqlComEnum implements SqlFrameBase {

    /*
     * []:可选、<>:必填 《》:配置开关填写、{}:参数、【】:循环 N:(选择框架   =========================================
     */
    /* =============================== 系统语句 ==================================== */
    TABLES(         SqlMySQLEnum.TABLES,
                    SqlOracleEnum.TABLES,
                    SqlSQLServerEnum.TABLES,
                    SqlDMEnum.TABLES,
                    SqlKingbaseESEnum.TABLES,
                    SqlPostgreSQLEnum.TABLES,
                    SqlDorisEnum.TABLES
    ),
    TABLESANDVIEW(          SqlMySQLEnum.TABLESANDVIEW,
            SqlOracleEnum.TABLESANDVIEW,
            SqlSQLServerEnum.TABLESANDVIEW,
            SqlDMEnum.TABLESANDVIEW,
            SqlKingbaseESEnum.TABLESANDVIEW,
            SqlPostgreSQLEnum.TABLESANDVIEW
    ),
    TABLE(          SqlMySQLEnum.TABLE,
                    SqlOracleEnum.TABLE,
                    SqlSQLServerEnum.TABLE,
                    SqlDMEnum.TABLE,
                    SqlKingbaseESEnum.TABLE,
                    SqlPostgreSQLEnum.TABLE,
                    SqlDorisEnum.TABLE
    ),
    FIELDS(         SqlMySQLEnum.FIELDS,
                    SqlOracleEnum.FIELDS,
                    SqlSQLServerEnum.FIELDS,
                    SqlDMEnum.FIELDS,
                    SqlKingbaseESEnum.FIELDS,
                    SqlPostgreSQLEnum.FIELDS,
                    SqlDorisEnum.FIELDS
    ),
    EXISTS_TABLE(   SqlMySQLEnum.EXISTS_TABLE,
                    SqlOracleEnum.EXISTS_TABLE,
                    SqlSQLServerEnum.EXISTS_TABLE,
                    SqlDMEnum.EXISTS_TABLE,
                    SqlKingbaseESEnum.EXISTS_TABLE,
                    SqlPostgreSQLEnum.EXISTS_TABLE,
                    SqlDorisEnum.EXISTS_TABLE
    ),
    /* =============================== 定义语句 ==================================== */
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    /**
     * 创表
     */
    CREATE_TABLE    (SqlOracleEnum.CREATE_TABLE,
                    Arrays.asList(
                         "{table}",
                         "{column}",
                         "{dataType}",
                         "[[NOT] [NULL]]",
                         "[<DEFAULT> {defaultValue}]",
                         "[<PRIMARY KEY>]",
                         "[AUTO_INCREMENT]",
                         "[<COMMENT> {comment}]"
                    ),
                    SqlMySQLEnum.CREATE_TABLE,
                    SqlSQLServerEnum.CREATE_TABLE,
                    SqlDMEnum.CREATE_TABLE,
                    SqlKingbaseESEnum.CREATE_TABLE,
                    SqlPostgreSQLEnum.CREATE_TABLE,
                    SqlDorisEnum.CREATE_TABLE
    ),
    ADD_COLUMN      (SqlMySQLEnum.ALTER_ADD_MODIFY,
                    Arrays.asList(
                            "<ADD|MODIFY>",
                            "{table}",
                            "{column}",
                            "{dataType}",
                            "[[NOT] [NULL]]",
                            "[<DEFAULT> {defaultValue}]",
                            "{comment}"
                    ),
                    SqlOracleEnum.ADD_COLUMN,
                    SqlPostgreSQLEnum.ADD_COLUMN,
                    SqlDMEnum.ALTER_ADD,
                    SqlKingbaseESEnum.ALTER_ADD,
                    SqlSQLServerEnum.ALTER_COLUMN
    ),
    /**
     * 删除表
     */
    DROP_TABLE      (SqlOracleEnum.DROP_TABLE,
                    Collections.singletonList(
                        "{table}"
                    ),
                    SqlMySQLEnum.DROP_TABLE
    ),
    /**
     * 表重命名
     */
    RE_TABLE_NAME   (SqlMySQLEnum.RE_TABLE_NAME,
                    Arrays.asList(
                        "{oldTable}",
                        "{newTable}"
                    ),
                    SqlKingbaseESEnum.RE_TABLE_NAME,
                    SqlPostgreSQLEnum.RE_TABLE_NAME,
                    SqlSQLServerEnum.RE_TABLE_NAME,
                    SqlDorisEnum.RE_TABLE_NAME
    ),
    /**
     * 表注释
     */
    COMMENT_TABLE   (SqlOracleEnum.COMMENT_TABLE,
                    Arrays.asList(
                         "{table}",
                         "'{comment}'"
                    ),
                    SqlMySQLEnum.COMMENT_TABLE,
                    SqlSQLServerEnum.COMMENT_TABLE
    ),
    /**
     * 字段注释
     */
    COMMENT_COLUMN  (SqlOracleEnum.COMMENT_COLUMN,
                    Arrays.asList(
                         "{table}",
                         "{column}",
                         "'{comment}'",
                         "{dataType}",
                         "[DEFAULT {defaultValue}]"
                    ),
                    SqlMySQLEnum.COMMENT_COLUMN,
                    SqlSQLServerEnum.COMMENT_COLUMN
    ),
    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。
    /**
     * 获取表数据SQL
     */
    SELECT_TABLE    (SqlMySQLEnum.SELECT_TABLE,
                    Collections.singletonList(
                        "{table}"
                    )
    ),
    /**
     * selectSql不能为纯表，会报语法异常
     */
    COUNT_SIZE      (SqlMySQLEnum.COUNT_SIZE,
                    Arrays.asList(
                        "{totalAlias}",
                        "{selectSql}"
                    )
    ),
    COUNT_SIZE_TABLE(SqlMySQLEnum.COUNT_TABLE_SIZE,
                    Arrays.asList(
                        "{totalAlias}",
                        "{table}"
                    )
    ),
    INSERT          (SqlMySQLEnum.INSERT,
                    Arrays.asList(
                        "{table}",
                        "[【{column},】]",
                        "【{value},】"
                    )
    ),
    DELETE_ALL      (SqlMySQLEnum.DELETE_ALL,
                    Arrays.asList(
                        "{table}",
                        "{idColumn}"
                    ),
                    SqlDorisEnum.DELETE_ALL

    ),
    /* =============================== 后缀 ==================================== */
    /**
     * beginIndex（起始下标）
     * = (currentPage - 1) * pageSize（当前页 * 页大小）
     * endIndex（结束下标）
     * = currentPage * pageSize
     * 先查询还是先排序
     */
    ORDER_PAGE     (SqlMySQLEnum.ORDER_PAGE,
                    Arrays.asList(
                        "{selectSql}",
                        "{orderColumn}",
                        "{beginIndex}",
                        "{endIndex}",
                        "{pageSize}",
                        "[DESC]"
                    ),
                    SqlOracleEnum.ORDER_PAGE,
                    SqlSQLServerEnum.ORDER_PAGE,
                    SqlPostgreSQLEnum.ORDER_PAGE
    ),
    /**
     * ASC（ascend）：升序 1234 放空默认
     * DESC（descend）：降序 4321
     */
    ORDER           (SqlMySQLEnum.ORDER,
                    Arrays.asList(
                        "{column}",
                        "[DESC]"
                    )
    ),
    /**
     * 模糊查询
     * * : 多字符, c*c代表cc,cBc,cbc,cabdfec等
     * % : 多个字符, %c%代表agdcagd等
     * ? : 单个字符, %c%代表agdcagd等
     * # : 单数字, k#k代表k1k,k8k,k0k
     * [*] : 特殊字符, a[*]a代表a*a
     * [a-z] : 字符范围,  [a-z]代表a到z的26个字母中任意一个 指定一个范围中任意一个
     */
    LIKE           (SqlMySQLEnum.LIKE,
                    Arrays.asList(
                        "{selectSql}",
                        "{column}",
                        "{condition}"
                    )
    ),


    ;

    private String sqlFrame;
    private SqlFrameBase baseSqlEnum;
    private List<SqlFrameBase> frameEnums;
    private List<String> frameParamList;
    private final String dbEncode = "common";

    /**
     * 构造
     * @param baseSqlEnum 基础枚举（其他数据库没有独特SQL，遵循这个枚举的SQL）
     */
    SqlComEnum(SqlFrameBase baseSqlEnum, List<String> frameParamList) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        this.frameEnums = new ArrayList<>();
        this.frameEnums.add(baseSqlEnum);
    }

    /**
     * 构造
     * @param frameEnums 不同库自身对应的SQL框架
     */
    SqlComEnum(SqlFrameBase baseSqlEnum, List<String> frameParamList, SqlFrameBase... frameEnums) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        List<SqlFrameBase> frameEnumsList = new ArrayList<>(Arrays.asList(frameEnums));
        frameEnumsList.add(baseSqlEnum);
        this.frameEnums = frameEnumsList;
    }

    SqlComEnum(SqlFrameBase... frameEnums) {
        this.frameEnums = Arrays.asList(frameEnums);
    }

    /**
     * 获取子类的枚举
     * @return 返回子类枚举
     */
    public SqlFrameBase getSqlFrameEnum(String dbEncode){
        if(this.getFrameEnums() != null){
            for (SqlFrameBase sqlEnum : this.getFrameEnums()) {
                if(sqlEnum.getDbEncode().equals(dbEncode)){
                    return sqlEnum;
                }
            }
        }
        return null;
    }

    public SqlFrameBase getSqlFrameEnum(SqlFrameBase sqlFrameBase){
        if(this.getFrameEnums() != null){
            for (SqlFrameBase sqlEnum : this.getFrameEnums()) {
                if(sqlEnum.equals(sqlFrameBase)){
                    return sqlEnum;
                }
            }
        }
        return null;
    }

    public PrepSqlDTO getPrepSqlDto(DbSourceOrDbLink dataSourceMod, String table){
        SqlFrameBase sysTemSqlEnum = getSqlFrameEnum(dataSourceMod.init().getDbType());
        return sysTemSqlEnum.getPrepSqlDto(dataSourceMod, table);
    }

}
