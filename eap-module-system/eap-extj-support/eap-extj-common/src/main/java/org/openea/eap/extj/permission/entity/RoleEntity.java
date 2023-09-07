package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统角色
 *
 */
@Data
@TableName("base_role")
public class RoleEntity extends PermissionEntityBase{

    @TableId("F_id")
    private String id;

    /**
     * 角色类型
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 全局标识
     */
    @TableField("F_Global_Mark")
    private Integer globalMark;

}
