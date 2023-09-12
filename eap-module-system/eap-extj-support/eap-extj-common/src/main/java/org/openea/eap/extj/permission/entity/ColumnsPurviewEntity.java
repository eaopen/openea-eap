package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperEntity;

/**
 * 模块列表权限
 *
 */
@Data
@TableName(value = "base_columnspurview")
public class ColumnsPurviewEntity extends SuperEntity<String> {

    /**
     * 列表字段数组
     */
    @TableField("F_FIELDLIST")
    private String fieldList;
    /**
     * 模块ID
     */
    @TableField("F_MODULEID")
    private String moduleId;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
