package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;
import org.openea.eap.extj.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.datatype.limit.*;

/**
 * 金仓数据类型
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DtKingbaseESEnum implements DtInterface {

    /*================================== 整数型 ========================================*/
    TINY_INT    ("tinyint",     new IntegerLimit()),
    SMALL_INT   ("smallint",    new IntegerLimit()),
    /**
     * 默认长度：无
     */
    INTEGER     ("integer",     new IntegerLimit()),
    /**
     * 默认长度：无
     */
    BIGINT      ("bigint",      new IntegerLimit()),
    /*================================== 文本字符串类型 ========================================*/
    CHAR        ("character",   new StringLimit(true).charLength(10485760L, 50L)),
    /**
     * 显示：character varying
     */
    VARCHAR     ("varchar",     new StringLimit(true).charLength(10485760L, 50L)),
    /**
     * 默认长度：无
     */
    TEXT        ("text",        new StringLimit()),
    /*================================== 浮点型 ========================================*/
    /**
     * 显示：real（不允许修改,与sqlserver一样）
     */
    FLOAT4      ("float4",      new FloatLimit()),
    /**
     * 显示: double precision
     */
    FLOAT8      ("float8",      new FloatLimit()),
    /*================================== 定点型 ========================================*/
    /**
     * 默认长度：无
     */
    NUMERIC     ("numeric",     new DecimalLimit(true)
            .precision(1000, 38).scale(1000, 38)),
    /* =============================== 日期时间类型:  =============================== */
    /**
     * 显示：time without time zone
     */
    TIME        ("time",        new DateTimeLimit()),
    DATE        ("date",        new DateTimeLimit()),
    /**
     * 有些版本不支持datetime
     */
    DATE_TIME   ("datetime",    new DateTimeLimit()),
    /**
     * 默认长度：无
     * 显示：timestamp without time zone
     */
    TIMESTAMP   ("timestamp",   new DateTimeLimit()),

    ;

    /**
     * 数据库字段类型
     */
    private final String dataType;

    /**
     * 长度、精度、标度规则模型
     */
    private final DtLimitBase dtLimit;

}
