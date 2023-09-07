package org.openea.eap.extj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 大屏地图配置
 *
 *
 */
@Data
@TableName("blade_visual_map")
public class VisualMapEntity {
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 地图名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 地图数据
     */
    @TableField("DATA")
    private String data;

    /**
     * 租户id
     */
    @TableField("F_TENANTID")
    private String tenantId;
}
