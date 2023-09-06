package org.openea.eap.extj.model.visualJson;


import lombok.Data;

/**
 * 列表字段
 * 
 */
@Data
public class ColumnListField extends FieLdsModel {
    /**
     * 字段
     */
    private String prop;
    /**
     * 列名
     */
    private String label;
    /**
     * 对齐
     */
    private String align;

    private String jnpfKey;

    /**
     * 是否勾选
     */
    private Boolean checked;
}
