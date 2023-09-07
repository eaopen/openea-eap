package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 岗位信息
 */
@Data
@TableName("base_position")
public class PositionEntity extends PermissionEntityBase{

    /**
     * 岗位类型
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 机构主键
     */
    @TableField("F_ORGANIZEID")
    private String organizeId;
}