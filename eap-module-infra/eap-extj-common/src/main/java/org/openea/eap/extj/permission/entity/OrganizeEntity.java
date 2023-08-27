package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 组织机构
 */
@Data
@TableName("base_organize")
public class OrganizeEntity extends PermissionEntityBase{

    /**
     * 机构上级
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 机构分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 机构主管
     */
    @TableField("F_MANAGERID")
    private String manager;

    /**
     * 父级组织
     */
    @TableField("F_ORGANIZEIDTREE")
    private String organizeIdTree;

}
