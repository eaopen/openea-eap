package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;
import org.openea.eap.extj.database.datatype.limit.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.extj.database.datatype.limit.*;

/**
 * 类功能
 *
 */
@Getter
@AllArgsConstructor
public enum DtDorisEnum implements DtInterface {

    /* =============================== 整数类型: TINYINT、SMALLINT、MEDIUMINT、INT(INTEGER)、BIGINT =============================== */
    /* ============== 支持：TINYINT、INT、BIGINT ============= */
    /**
     * 短整型
     * 带符号：-128 ~ 127
     * 无符号：255
     */
    TINY_INT    ("tinyint",     new IntegerLimit().fixedPrecision(3)),
    /**
     * 与TINYINT一样，0代表false，1代表true
     */
    BOOLEAN     ("boolean",     new IntegerLimit().fixedPrecision(null)),
    /**
     * 字节有符号整数，范围：[-32768, 32767]
     */
    SMALL_INT   ("smallint",    new IntegerLimit().fixedPrecision(5)),
    /**
     * 整型
     * -2147483648 ~ 2147483647
     */
    INT         ("int",         new IntegerLimit().fixedPrecision(10)),
    /**
     * 长整型
     * -9223372036854775808 ~ 9223372036854775807
     */
    BIGINT      ("bigint",      new IntegerLimit().fixedPrecision(19)),
    LARGE_INT   ("largeint",    new IntegerLimit().fixedPrecision(null)),
    /* =============================== 文本字符串类型:  =============================== */
    /**
     * 定长字符串，M代表的是定长字符串的长度。M的范围是1-255
     */
    CHAR        ("char",        new StringLimit(true).charLength(255L,50L)),
    /**
     * 变长字符串，M代表的是变长字符串的字节长度。M的范围是1-65533
     * 变长字符串是以UTF-8编码存储的，因此英文字符占1个字节，中文字符占3个字节
     */
    VARCHAR     ("varchar",      new StringLimit(true).charLength(16170L, 50L)),
    /**
     * 变长字符串，最大支持2147483643字节(2GB-4)。String类型的长度还受BE配置参数 string_type_soft_limit的影响, 实际能存储的最大长度取两者最小值
     * String类型只能用在value列，不能用在key列和分区分桶列
     * 变长字符串是以UTF-8编码存储的，因此英文字符占1个字节，中文字符占3个字节
     */
    STRING      ("string",      new StringLimit()),
    /* =============================== 浮点类型:  =============================== */
    FLOAT       ("float",       new FloatLimit(true).precision(35, 18).scale(30, 3)),
    DOUBLE      ("double",      new FloatLimit(true).fixedPrecision(35).fixedScale(30)),
    /* =============================== 定点数类型:  =============================== */
    DECIMAL     ("decimal",     new DecimalLimit(true).precision(27, 18).scale(30, 3)),
    /* =============================== 日期时间类型:  =============================== */
    DATE        ("date",        new DateTimeLimit()),
    DATE_TIME   ("datetime",    new DateTimeLimit()),
    /* ======= 集合: ======= */
    BITMAP      ("BITMAP",      new StringLimit()),
    /* ======= 聚合类型: ======= */
    /**
     * HyperLogLog
     * 1~16385 个字节。HLL不能作为key列使用，建表时配合聚合类型为HLL_UNION。用户不需要指定长度和默认值。长度根据数据的聚合程度系统内控制
     * 并且HLL列只能通过配套的hll_union_agg、hll_raw_agg、hll_cardinality、hll_hash进行查询或使用
     * HLL是模糊去重，在数据量大的情况性能优于Count Distinct。HLL的误差通常在1%左右，有时会达到2%
     */
    HLL         ("HLL",         new StringLimit())
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
