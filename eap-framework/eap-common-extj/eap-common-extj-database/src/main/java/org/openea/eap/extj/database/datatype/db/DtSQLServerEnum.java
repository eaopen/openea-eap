package org.openea.eap.extj.database.datatype.db;


import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;
import org.openea.eap.extj.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.datatype.limit.*;

/**
 * SQLServer数据类型
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DtSQLServerEnum implements DtInterface {

    /* =============================== 整数类型 =============================== */
    TINY_INT    ("tinyint",     new IntegerLimit()),
    SMALL_INT   ("smallint",    new IntegerLimit()),
    /**
     * 默认长度：无
     */
    INT         ("int",         new IntegerLimit()),
    /**
     * 默认长度：无
     */
    BIGINT      ("bigint",      new IntegerLimit()),
    /* =============================== 文本字符串类型 =============================== */
    CHAR        ("char",        new StringLimit(true).charLength(8000L, 50L)),

    /**
     * N 代表Unicode可变长度类型
     * NVARCHAR 字符存储，VARCHAR 字节存储
     * 推荐：使用nvarchar ，虽然varchar比nvarchar省空间，但空间相对来说已经不是奢侈的了，而且带来的问题也很严重，
     * 因为varchar是非Unicode所以系统读取数据都会进行一次Unicode转码，nvarchar不会转码，这是其一，其二如果操作系统是英文操作系统，
     * 那么用varchar存储的汉字会出现乱码的情况，varchar是单字节存储，nvarchar是双字节存储
     */
    NVARCHAR    ("nvarchar",    new StringLimit(true).charLength(8000L, 50L)),
    VARCHAR     ("varchar",     new StringLimit(true).charLength(4000L, 50L)),
    /*  Text 和 Image 是可能被 SQServer 以后的版本淘汰的数据类型
    varchar(max)-------text;
    nvarchar(max)-----ntext;
    varbinary(max)----p_w_picpath.
    nvarchar长度会显示-1
    查询出来只能显示nvarchar，长度-1的时候代表nvarchar(max)，项目中转换成text */
    /**
     * 默认长度：4005
     */
    VARCHAR_MAX ("nvarchar(max)",new StringLimit().fixedCharLength(4005L)),
    /**
     * 默认长度：无
     * 长度可变的非 Unicode 数据
     */
    TEXT        ("text",        new StringLimit()),
    /**
     * 长度可变的 Unicode 数据
     *
     */
    N_TEXT      ("ntext",       new StringLimit()),
    /**
     * 长度可变的二进制数据
     */
    IMAGE       ("image",       new StringLimit()),
    /* =============================== 浮点类型:  =============================== */
    /**
     * 显示 real
     */
    FLOAT       ("float",       new FloatLimit().precision(53, 18).scale(18, 3)),
    /* =============================== 日期时间类型:  =============================== */
    DATE        ("date",        new DateTimeLimit()),
    /**
     * 默认长度：4005
     */
    TIME        ("time",        new DateTimeLimit()),
    DATE_TIME   ("datetime",    new DateTimeLimit()),
    DATE_TIME2   ("datetime2",  new DateTimeLimit()),
    TIMESTAMP   ("timestamp",   new DateTimeLimit()),
    /* =============================== 定点数类型:  =============================== */
    DECIMAL     ("decimal",     new DecimalLimit(true).precision(38, 18).scale(18, 3)),
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
