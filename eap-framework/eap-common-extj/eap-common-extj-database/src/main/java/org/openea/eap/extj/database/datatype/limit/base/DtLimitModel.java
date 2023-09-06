package org.openea.eap.extj.database.datatype.limit.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 类功能
 *
 * 
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DtLimitModel {

    /**
     * 最大长度
     */
    private Object max;

    /**
     * 最小长度
     */
    private Object min;

    /**
     * 默认长度
     */
    private Object defaults;

    /**
     * 固定长度
     */
    private Object fixed;

    public DtLimitModel(Object fixed){
        this.fixed = fixed;
    }

    /**
     * 生成类型限制对象
     */
    public DtLimitModel(Object maxLength, Object minLength, Object defaultLength){
        this.max = maxLength;
        this.min = minLength;
        this.defaults = defaultLength;
        this.fixed = defaultLength;
    }

}
