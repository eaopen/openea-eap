package org.openea.eap.extj.base.model.Template6;



import lombok.Data;
import org.openea.eap.extj.model.visualJson.FieLdsModel;

/**
 * 列表字段
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

    /**
     * 是否勾选
     */
    private Boolean checked;

    private String jnpfKey;
}
