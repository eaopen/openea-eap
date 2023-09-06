package org.openea.eap.extj.database.model.dbfield.base;

import lombok.Data;


/**
 * 类功能
 *
 * 
 */
@Data
public class DbFieldModelBase {

    /**
     * 数据长度 (n,m) 或 (n)
     */
    protected String length;

    /**
     * 数据类型
     */
    protected String dataType;

    /**
     * 字段名
     */
    protected String field;

    /**
     * 是否主键
     */
    protected Boolean isPrimaryKey;

    /**
     * 是否非空
     * （允空非空及0与1较容易混淆，故使用标识传作参数）
     */
    protected String nullSign;

    /**
     * 是否自增
     */
    protected Boolean isAutoIncrement;

    /**
     * 注释
     */
    protected String comment;

    /**
     * 默认值
     */
    protected String defaultValue;

}
