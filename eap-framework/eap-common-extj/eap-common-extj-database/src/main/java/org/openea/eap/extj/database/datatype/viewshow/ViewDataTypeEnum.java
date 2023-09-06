package org.openea.eap.extj.database.datatype.viewshow;

import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.viewshow.constant.DtViewConst;
import org.openea.eap.extj.database.datatype.db.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段类型枚举
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum ViewDataTypeEnum {

    /* 如{主类型},{次类型}:({默认字符长度},{限制长度}(*:不允许设置))*/

    /**
     * 字符
     */
    VARCHAR(
            DtViewConst.VARCHAR,
            DtMySQLEnum.VARCHAR,
            DtOracleEnum.VARCHAR2,
            DtSQLServerEnum.VARCHAR,
            DtDMEnum.VARCHAR,
            DtKingbaseESEnum.VARCHAR,
            DtPostgreSQLEnum.VARCHAR,
            DtDorisEnum.VARCHAR
    ),
    /**
     * 日期时间
     * 日期统一不指定长度
     */
    DATE_TIME(
            DtViewConst.DATE_TIME,
            DtMySQLEnum.DATE_TIME,
            DtOracleEnum.TIMESTAMP,
            DtSQLServerEnum.DATE_TIME,
            DtDMEnum.DATE_TIME,
            DtKingbaseESEnum.TIMESTAMP,
            DtPostgreSQLEnum.TIMESTAMP,
            DtDorisEnum.DATE_TIME
    ),
    /**
     * 浮点
     */
    DECIMAL(
            DtViewConst.DECIMAL,
            DtMySQLEnum.DECIMAL,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.DECIMAL,
            DtDMEnum.DECIMAL,
            DtKingbaseESEnum.NUMERIC,
            DtPostgreSQLEnum.NUMERIC,
            DtDorisEnum.DECIMAL
    ),
    /**
     * 文本
     */
    TEXT(
            DtViewConst.TEXT,
            DtMySQLEnum.TEXT,
            DtOracleEnum.CLOB,
            DtSQLServerEnum.TEXT,
            DtDMEnum.TEXT,
            DtKingbaseESEnum.TEXT,
            DtPostgreSQLEnum.TEXT,
            DtDorisEnum.STRING
    ),
    /**
     * 整型
     * SqlServer、PostGre:int不能指定长度
     */
    INT(
            DtViewConst.INT,
            DtMySQLEnum.INT,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.INT,
            DtDMEnum.INT,
            DtKingbaseESEnum.INTEGER,
            DtPostgreSQLEnum.INT4,
            DtDorisEnum.INT
    ),
    /**
     * 长整型
     */
    BIGINT(
            DtViewConst.BIGINT,
            DtMySQLEnum.BIGINT,
            DtOracleEnum.NUMBER,
            DtSQLServerEnum.BIGINT,
            DtDMEnum.BIGINT,
            DtKingbaseESEnum.BIGINT,
            DtPostgreSQLEnum.INT8,
            DtDorisEnum.BIGINT
    ),
    /**
     * Oracle数字类型
     */
    ORACLE_NUMBER(
            DtViewConst.ORACLE_NUMBER,
            null,
            DtOracleEnum.NUMBER,
            null,
            null,
            null,
            null,
            null
    ),
    /**
     * Postgre，Longtext不支持
     */
    POSTGRE_LONGTEXT(
            DtViewConst.LONGTEXT,
            null,
            null,
            null,
            null,
            null,
            DtPostgreSQLEnum.TEXT,
            null
    );

    private final String viewFieldType;
    private final DtMySQLEnum dtMySQLEnum;
    private final DtOracleEnum dtOracleEnum;
    private final DtSQLServerEnum dtSQLServerEnum;
    private final DtDMEnum dtDMEnum;
    private final DtKingbaseESEnum dtKingbaseESEnum;
    private final DtPostgreSQLEnum dtPostgreSQLEnum;
    private final DtDorisEnum dtDorisEnum;

}
