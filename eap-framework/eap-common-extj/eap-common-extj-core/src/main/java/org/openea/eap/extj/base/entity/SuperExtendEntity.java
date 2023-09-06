package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperExtendEntity<T> extends SuperEntity<T>{

    /**
     * 有效标志 (0-默认，禁用，1-启用)
     */
    @TableField(value ="F_ENABLEDMARK",fill = FieldFill.INSERT)
    private Integer enabledMark;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode
    public static abstract class SuperExtendSortEntity<T> extends SuperExtendEntity<T>{

        /**
         * 排序码
         */
        @TableField("F_SORTCODE")
        private Long sortCode;

    }

}
