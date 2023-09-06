package org.openea.eap.extj.database.constant;

/**
 * 打印模板-结果集字段Key
 *
 * 
 */
public interface RsColumnKeyConst {

    /**
     * 表类别
     */
    String TABLE_CAT = "TABLE_CAT";

    /**
     * 表模式
     */
    String TABLE_SCHEM = "TABLE_SCHEM";

    /**
     * 表名称
     */
    String TABLE_NAME = "TABLE_NAME";

    /**
     * 列名称
     */
    String COLUMN_NAME = "COLUMN_NAME";

    /**
     * 来自 java.sql.Types 的 SQL 类型
     */
    String DATA_TYPE = "DATA_TYPE";

    /**
     * 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
     */
    String TYPE_NAME = "TYPE_NAME";

    /**
     * 列的大小
     * charLength(字符串长度)/Precision（精度）
     */
    String COLUMN_SIZE = "COLUMN_SIZE";

    /**
     * 对于 char 类型，该长度是列中的最大字节数
     */
    String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";

    /**
     * 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
     * Scale（标度）
     */
    String DECIMAL_DIGITS = "DECIMAL_DIGITS";

    /**
     * 基数（通常为 10 或 2）
     * 整数值，可以是10(表示精确的数字数据类型)，2(表示近似的数字数据类型)
     *
     * 详解：计算一个列所能容纳的最大数时所采用的位数或数字个数。如果是近似数字数据类型，则此列包含的值为 2，
     * 表明位数。对于精确数字数据类型，此列包含的值为 10，表明十进制数字的个数。否则此列为 NULL。通过将精度和基数相结合，
     * 应用程序可以计算出列所能容纳的最大数。
     */
    String NUM_PREC_RADIX = "NUM_PREC_RADIX";

    /**
     * 是否允许使用 NULL。 columnNoNulls - 可能不允许使用NULL值， columnNullable - 明确允许使用NULL值， columnNullableUnknown - 不知道是否可使用 null
     */
    String NULLABLE = "NULLABLE";

    /**
     * 描述列的注释（可为null）
     */
    String REMARKS = "REMARKS";

    /**
     * 该列的默认值，当值在单引号内时应被解释为一个字符串（可为null）
     */
    String COLUMN_DEF = "COLUMN_DEF";

    /**
     * 未使用
     */
    String SQL_DATA_TYPE = "SQL_DATA_TYPE";
    /**
     * 未使用
     */
    String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
;
    /**
     * 表中的列的索引（从 1 开始）
     */
    String ORDINAL_POSITION = "ORDINAL_POSITION";

    /**
     * 是否允许使用 NULL， columnNoNulls - 可能不允许使用NULL值， columnNullable - 明确允许使用NULL值， columnNullableUnknown - 不知道是否可使用 null
     */
    String IS_NULLABLE = "IS_NULLABLE";

    /**
     * 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为null）
     */
    String SCOPE_CATLOG = "SCOPE_CATLOG";

    /**
     * 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为null）
     */
    String SCOPE_SCHEMA = "SCOPE_SCHEMA";

    /**
     * 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为null）
     */
    String SCOPE_TABLE = "SCOPE_TABLE";

    /**
     * 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为null）
     */
    String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";

    /**
     * 指示此列是否自动增加，YES --- 如果该列自动增加 ， NO --- 如果该列不自动增加， 空字符串 --- 如果不能确定该列是否是自动增加参数
     */
    String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
}
