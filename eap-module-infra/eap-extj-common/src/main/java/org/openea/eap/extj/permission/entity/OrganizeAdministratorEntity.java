package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 机构分级管理员
 */
@Data
@TableName("base_organizeadministrator")
public class OrganizeAdministratorEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    @TableField("F_USERID")
    private String userId;

    @TableField("F_ORGANIZEID")
    private String organizeId;

    @TableField("F_ORGANIZETYPE")
    private String organizeType;

    @TableField("F_THISLAYERADD")
    private Integer thisLayerAdd;

    @TableField("F_THISLAYEREDIT")
    private Integer thisLayerEdit;

    @TableField("F_THISLAYERDELETE")
    private Integer thisLayerDelete;

    @TableField("F_SUBLAYERADD")
    private Integer subLayerAdd;

    @TableField("F_SUBLAYEREDIT")
    private Integer subLayerEdit;

    @TableField("F_SUBLAYERDELETE")
    private Integer subLayerDelete;

    /**
     * 本层查看权限
     */
    @TableField("F_THISLAYERSELECT")
    private Integer thisLayerSelect;

    /**
     * 子级查看权限
     */
    @TableField("F_SUBLAYERSELECT")
    private Integer subLayerSelect;

}
