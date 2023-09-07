package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("base_organize_relation")
@Schema(description = "OrganizeRelation对象", name = "组织关系")
public class OrganizeRelationEntity extends SuperBaseEntity.SuperTBaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableField("F_Organize_Id")
    private String organizeId;

    /**
     * 对象类型（岗位：position、角色：role）
     */
    @TableField("F_Object_Type")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_Object_Id")
    private String objectId;

    /**
     * 创建时间
     */
    @TableField("F_Creator_Time")
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField("F_Creator_User_Id")
    private String creatorUserId;

}

