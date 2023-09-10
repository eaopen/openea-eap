package org.openea.eap.extj.form.entity;

import org.openea.eap.extj.base.entity.SuperExtendEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程设计
 *
 *
 */
@Data
@TableName("flow_engineform")
public class FlowFormEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 表单编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 表单名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 流程类型：0-发起流程，1-功能流程
     */
    @TableField("F_FLOWTYPE")
    private Integer flowType;
    /**
     * 表单类型：1-系统表单，2-自定义表单
     */
    @TableField("F_FORMTYPE")
    private Integer formType;
    /**
     * 表单分类
     */
    @TableField("F_CATEGORY")
    private String category;
    /**
     * Web地址
     */
    @TableField("F_URLADDRESS")
    private String urlAddress;
    /**
     * APP地址
     */
    @TableField("F_APPURLADDRESS")
    private String appUrlAddress;
    /**
     * 接口路径
     */
    @TableField("F_INTERFACEURL")
    private String interfaceUrl;
    /**
     * 属性字段
     */
    @TableField("F_PROPERTYJSON")
    private String propertyJson;

    /**
     * 草稿版本json
     */
    @TableField("F_DRAFTJSON")
    private String draftJson;
    /**
     * 关联数据连接id
     */
    @TableField("F_DBLINKID")
    private String dbLinkId;
    /**
     * 关联的表
     */
    @TableField("F_TABLEJSON")
    private String tableJson;

    /**
     * 关联流程id
     */
    @TableField(value = "F_FLOWID")
    private String flowId;

}

