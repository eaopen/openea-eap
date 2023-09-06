package org.openea.eap.extj.database.model.dbtable.base;

import lombok.Data;

/**
 * 类功能
 *
 * 
 */
@Data
public class DbTableModelBase {

    /**
     * 表名
     */
    private String table;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 大小
     */
    private String size;

    /**
     * 数据条数
     */
    private String sum;

    /**
     * 主键
     */
    private String primaryKeyField;

}
