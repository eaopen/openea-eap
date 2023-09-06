package org.openea.eap.extj.database.datatype.db;


import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;
import org.openea.eap.extj.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.datatype.limit.*;

/**
 * MySQL字段数据类型枚举
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DtMySQLEnum implements DtInterface {


    /* =============================== 整数类型: TINYINT、SMALLINT、MEDIUMINT、INT(INTEGER)、BIGINT =============================== */
    /* ============== 支持：TINYINT、INT、BIGINT ============= */
    /**
     * 短整型
     * 带符号：-128 ~ 127
     * 无符号：255
     */
    TINY_INT    ("tinyint",     new IntegerLimit().fixedPrecision(3)),
    SMALL_INT   ("smallint",    new IntegerLimit().fixedPrecision(5)),
    MEDIUM_INT  ("mediumint",   new IntegerLimit().fixedPrecision(7)),
    /**
     * 整型
     * -2147483648 ~ 2147483647
     */
    INT         ("int",         new IntegerLimit().fixedPrecision(10)),
    INT_UNSIGNED ("int unsigned",         new IntegerLimit().fixedPrecision(10)),
    /**
     * 长整型
     * -9223372036854775808 ~ 9223372036854775807
     */
    BIGINT      ("bigint",      new IntegerLimit().fixedPrecision(19)),
    BIGINT_UNSIGNED ("bigint unsigned",      new IntegerLimit().fixedPrecision(19)),
    /* =============================== 文本字符串类型:  =============================== */
    BIT         ("bit",         new StringLimit(true).charLength(15L, 64L)),
    BLOB        ("blob",        new StringLimit()),
    CHAR        ("char",        new StringLimit(true).charLength(50L,255L)),
    /**
     * 字符串
     * 默认最大长度16170（65535 bytes）
     * 标注:当varchar当主键的时候，最长是768（3072 bytes），当长度过长时推荐用text
     */
    VARCHAR     ("varchar",      new StringLimit(true).charLength(16170L, 50L)),
    /**
     * 文本
     * 默认长度：0
     */
    TINY_TEXT   ("tinytext",     new StringLimit().fixedCharLength(225L)),
    TEXT        ("text",         new StringLimit().fixedCharLength(65535L)),
    MEDIUM_TEXT ("mediumtext",   new StringLimit().fixedCharLength(16777215L)),
    /**
     * 长文本
     * 默认长度：0
     * longtext支持65535字节长度，utf8编码下最多支持21843个字符
     */
    LONG_TEXT   ("longtext",    new StringLimit().fixedBitLength(4294967295L)),
    /**
     * 二进制大对象 longblob (lang binary large object)
     */
    LONG_BLOB   ("longblob",    new StringLimit().fixedCharLength(0L)),
    /* =============================== 浮点类型:  =============================== */
    /**
     * float：单精度（4个字节,8位精度），
     * double：双精度（8个字节，16位精度）
     * 总结：double精度更高，占用更大，float精度更低，运算更快
     */
    FLOAT       ("float",       new FloatLimit(true).precision(255, 18).scale(30, 3)),
    DOUBLE      ("double",      new FloatLimit(true).precision(255, 18).scale(30, 3)),
    /* =============================== 定点数类型:  =============================== */
    DECIMAL     ("decimal",     new DecimalLimit(true).precision(65, 18).scale(30, 3)),
    /* =============================== 日期时间类型:  =============================== */
    /**
     * 示例：14:50:42.000000 (长度6)、14:50:42.0（长度1）
     */
    TIME        ("time",        new DateTimeLimit(true).scale(6, 6)),
    /**
     * 示例：2022-06-06 14:53:12.000000（长度6）、2022-06-06 14:53:12.0（长度1）
     */
    DATE_TIME   ("datetime",    new DateTimeLimit(true).scale(6, 6)),
    /**
     * 示例：2022-06-06 14:56:04.000000（长度6）、2022-06-06 14:56:04.0（长度1）
     */
    TIMESTAMP   ("timestamp",   new DateTimeLimit(true).scale(6, 6)),
    /**
     * 示例：1992、2020、2060
     */
    YEAR        ("year",        new DateTimeLimit(true).precision(4, 4)),
    DATE        ("date",        new DateTimeLimit()),


    /* ======= 枚举类型: ======= */
    /* ======= 集合类型: ======= */
    /* ======= 二进制字符串类型: ======= */
    /* ======= JSON类型: ======= */
    /* ======= 空间数据类型: ======= */
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
