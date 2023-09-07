package org.openea.eap.extj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏基本配置
 *
 *
 */
@Data
@TableName("blade_visual_config")
public class VisualConfigEntity {
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 可视化表主键
     */
    @TableField("VISUAL_ID")
    private String visualId;

    /**
     * 配置json
     */
    @TableField("DETAIL")
    private String detail;

    /**
     * 组件json
     */
    @TableField("COMPONENT")
    private String component;

    /**
     * 租户id
     */
    @TableField("F_TENANTID")
    private String tenantId;

}
