package org.openea.eap.extj.database.datatype.viewshow;


import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;

/**
 * 数据库数据类型向前端显示
 * 转换规则
 *
 * 
 */
public enum DtViewEnum {
    /**
     * 整型
     */
    INT("int", new DtInterface[]{
                DtMySQLEnum.INT,
    }),
    /**
     * 长整型
     */
    BIGINT("bigint", new DtInterface[]{
                DtMySQLEnum.BIGINT,
    }),
    /**
     * 字符串
     */
    VARCHAR("varchar", new DtInterface[]{
                    DtMySQLEnum.CHAR,
                    DtMySQLEnum.VARCHAR,
    }),
    /**
     * 文本
     */
    TEXT("text", new DtInterface[]{
                    DtMySQLEnum.TINY_TEXT,
                    DtMySQLEnum.TEXT,
                    DtMySQLEnum.MEDIUM_TEXT,
                    DtMySQLEnum.LONG_TEXT,
    }),
    /**
     * 浮点型
     */
    DECIMAL("decimal", new DtInterface[]{
                    DtMySQLEnum.DECIMAL,
    }),
    /**
     * 日期时间
     */
    DATE_TIME("datetime", new DtInterface[]{
                    DtMySQLEnum.DATE,
                    DtMySQLEnum.DATE_TIME,
    }),
    ;

    DtViewEnum(String view, DtInterface[] dtEnums){}

}
