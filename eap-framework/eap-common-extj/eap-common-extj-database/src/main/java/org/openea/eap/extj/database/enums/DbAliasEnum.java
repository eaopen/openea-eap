package org.openea.eap.extj.database.enums;

import org.openea.eap.extj.database.source.DbBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类功能
 *
 */
@AllArgsConstructor
@Getter
public enum  DbAliasEnum {

    /* =========================== 表 ============================ */
    /**
     * 表大小
     */
    TABLE_SIZE("F_TABLE_SIZE"),

    /**
     * 表名
     */
    TABLE_NAME("F_TABLE_NAME"),

    /**
     * 表注释
     */
    TABLE_COMMENT("F_TABLE_COMMENT"),

    /**
     * 类型
     */
    TABLE_TYPE("F_TABLE_TYPE"),

    /**
     * 表总数
     */
    TABLE_SUM("F_TABLE_SUM"),

    /* =========================== 字段 ============================ */
    /**
     * 字段名
     */
    FIELD("F_FIELD"),

    /**
     * 字段注释
     */
    FIELD_COMMENT("F_FIELD_COMMENT"),

    /**
     * 字段默认值
     */
    DEFAULT_VALUE("F_DEFAULT_VALUE"),

    /**
     * 自增
     */
    AUTO_INCREMENT("F_AUTO_INCREMENT"),

    /**
     * 默认值（用于判断自增长）
     */
    COLUMN_DEFAULT("F_COLUMN_DEFAULT"),

    /**
     * 自增
     */
    IS_IDENTITY("F_IS_IDENTITY"),

    /**
     * 允空
     */
    ALLOW_NULL("F_ALLOW_NULL"),

    /**
     * 主键
     */
    PRIMARY_KEY("F_PRIMARY_KEY"),

    /* ------------- 字段数据类型 ------------- */

    /**
     * 类型
     */
    DATA_TYPE("F_DATA_TYPE"),

    /**
     * 以字符为单位的最大长度，适于二进制数据、字符数据，或者文本和图像数据。否则，返回 NULL
     * 例如：text、varchar（int时为null），其中varchar为可变长度，text为固定长度
     */
    CHAR_LENGTH("F_CHAR_LENGTH"),

    /**
     * precision:数值精度（整个数值的长度）
     * 例如：decimal
     * 注意：int(i)类型时，无论i是多少，NUMERIC_PRECISION都是10，在填充0的时候i才会起作用
     */
    NUM_PRECISION("F_NUM_PRECISION"),

    /**
     * scale:数值标度（小数部分的长度）
     */
    NUM_SCALE("F_NUM_SCALE"),


    /* =========================== 其他 ============================ */

    /**
     * 总数返回
     */
    TOTAL_RECORD("totalRecord"),

    /**
     * 自增主键触发器判断
     */
    AUTO_TRIGGER("F_AUTO_TRIGGER"),


    ;
    private final String alias;

    public String getAlias(String dbEncode) {
        return getAlias(dbEncode, alias);
    }

    public static String getAlias(String dbEncode, String keyWord){
        if (DbBase.POSTGRE_SQL.equals(dbEncode)) {
            //postgre别名只能输出小写，Oracle只能大写
            //Mysql默认，SqlServer默认
            return keyWord.toLowerCase();
        } else if (DbBase.ORACLE.equals(dbEncode)) {
            return keyWord.toUpperCase();
        } else {
            return keyWord;
        }
    }

}
