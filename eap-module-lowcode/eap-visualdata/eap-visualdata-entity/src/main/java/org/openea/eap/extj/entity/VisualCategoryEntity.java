package org.openea.eap.extj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏分类
 *
 *
 */
@Data
@TableName("blade_visual_category")
public class VisualCategoryEntity {
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 分类值
     */
    @TableField("CATEGORY_KEY")
    private String categorykey;

    /**
     * 分类名称
     */
    @TableField("CATEGORY_VALUE")
    private String categoryvalue;

    /**
     * 是否删除
     */
    @TableField("IS_DELETED")
    private String isdeleted;

    /**
     * 租户id
     */
    @TableField("F_TENANTID")
    private String tenantId;

}
