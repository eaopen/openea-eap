package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;
import org.openea.eap.extj.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.datatype.limit.*;

/**
 * 达梦数据类型
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DtDMEnum implements DtInterface {
    /*============================ 整型 ===========================*/
    TINY_INT    ("TINYINT",     new IntegerLimit()),
    SMALL_INT   ("SMALLINT",    new IntegerLimit()),
    /**
     * 默认长度：10
     */
    INT         ("INT",         new IntegerLimit()),
    /**
     * 默认长度：19
     */
    BIGINT      ("BIGINT",      new IntegerLimit()),
    /*============================ 文本字符串类型 ===========================*/
    CHAR        ("CHAR",        new StringLimit(true).charLength(32767L, 150L)),
    /**
     * 字节长度：32767，汉字:3字节，特殊字符:3字节
     * 达梦自带迁移使用的是 (MySQL varchar) -> VARCHAR
     */
    VARCHAR     ("VARCHAR",     new StringLimit(true).bitLength(32767L, 150L)),
    VARCHAR2    ("VARCHAR2",    new StringLimit(true).bitLength(32767L, 150L)),
    /**
     * 默认长度：2147483647
     */
    TEXT        ("TEXT",        new StringLimit().fixedCharLength(2147483647L)),
    /**
     * 默认长度：2147483647
     */
    CLOB        ("CLOB",        new StringLimit().fixedCharLength(2147483647L)),

    /*============================ 时间格式 ===========================*/
    DATE        ("DATE",        new DateTimeLimit()),
    /**
     * 默认长度：36，默认精度：6
     * Mysql datetime转达梦 TIMESTAMP,手动改成DATETIME数据会报错
     * 利用 PostGreSQL进行迁移
     */
    DATE_TIME   ("DATETIME",    new DateTimeLimit()),
    /**
     * 默认长度：36，默认精度：6
     * 默认工具mysql（Timestamp）--> Dm（DateTime）
     */
    TIME_STAMP  ("TIMESTAMP",   new DateTimeLimit()),
    /**
     * 默认长度：22，默认精度：6
     */
    TIME        ("TIME",        new DateTimeLimit()),
    /**
     * 默认长度：13
     */
    /*============================ 定点型 ===========================*/
    DECIMAL     ("DECIMAL",     new DecimalLimit(true)
            .precision(38, 18).scale(38, 6, 3)),
    DEC         ("DEC",         new DecimalLimit(true)
            .precision(38, 18).scale(38, 6, 3)),
    /*============================浮点===========================*/
    /**
     * 显示real
     */
    FLOAT       ("FLOAT",       new FloatLimit(true).precision(24, 18).scale(30, 3)),
    DOUBLE      ("DOUBLE",      new FloatLimit(true).precision(53, 18).scale(30, 3)),

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
