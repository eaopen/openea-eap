package org.openea.eap.extj.database.datatype.db;


import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.DateTimeLimit;
import org.openea.eap.extj.database.datatype.limit.FloatLimit;
import org.openea.eap.extj.database.datatype.limit.NumberLimit;
import org.openea.eap.extj.database.datatype.limit.StringLimit;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Oracle数据类型
 *
 *
 */
@Getter
@AllArgsConstructor
public enum DtOracleEnum implements DtInterface {

    /*================================== 整数型 ========================================*/
    /**
     * 长度:1---38
     * 精度:-84---127
     */
    NUMBER      ("NUMBER",      new NumberLimit(true).precision(38, 11).scale(127, -84)),
    /* =============================== 文本字符串类型 =============================== */
    CHAR        ("CHAR",        new StringLimit(true).charLength(2000L, 50L)),
    NCHAR       ("NCHAR",    new StringLimit(true).charLength(2000L, 50L)),
    /**
     * VARCHAR2支持4000个字节，4000个字母，1333个汉字，汉字:3个字节
     * NVARCHAR2支持2000个字符，2000个字母，2000个汉字，不管是汉字还是字母，每个字符的长度都是2个字节，不受数据库字符集
     */
    VARCHAR2    ("VARCHAR2",    new StringLimit(true).bitLength(4000L, 50L)),
    NVARCHAR2   ("NVARCHAR2",   new StringLimit(true).charLength(2000L, 50L)),
    /**
     * 默认长度：4000
     * TEXT与BLOB的主要差别就是BLOB保存二进制数据，TEXT保存字符数据
     */
    /**
     * 1.BLOB
     * BLOB全称为二进制大型对象（Binary Large Object)。它用于存储数据库中的大型二进制对象。可存储的最大大小为4G字节
     * 2.CLOB
     * CLOB全称为字符大型对象（Character Large Object)。它与LONG数据类型类似，只不过CLOB用于存储数据库中的大型单字节字符数据块，不支持宽度不等的字符集。可存储的最大大小为4G字节
     * 3.NCLOB
     * 基于国家语言字符集的NCLOB数据类型用于存储数据库中的固定宽度单字节或多字节字符的大型数据块，不支持宽度不等的字符集。可存储的最大大小为4G字节
     */
    CLOB        ("CLOB",        new StringLimit()),
    BLOB        ("BLOB",        new StringLimit()),
    NCLOB       ("NCLOB",       new StringLimit()),
    /* =============================== 浮点类型  =============================== */
    /**
     * 除了oracle其他库浮点型，都无法修改参数
     */
    FLOAT       ("FLOAT",       new FloatLimit(true).precision(126, 18).scale(126, 3)),
    /* =============================== 日期时间类型:  =============================== */
    /**
     * 默认长度：7
     */
    DATE        ("DATE",        new DateTimeLimit()),
    TIMESTAMP   ("TIMESTAMP",   new DateTimeLimit()),
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
