package org.openea.eap.extj.engine.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 流程引擎
 *
 *
 */
@Data
@TableName("flow_template")
public class FlowTemplateEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 流程编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 流程名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 流程类型(0.发起流程 1.功能流程)
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 流程分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 图标背景色
     */
    @TableField("F_ICONBACKGROUND")
    private String iconBackground;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    @JSONField(name = "creatorUser")
    private String creatorUserId;

}
