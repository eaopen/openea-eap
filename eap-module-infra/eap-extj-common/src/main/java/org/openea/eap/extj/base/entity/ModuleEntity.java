package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_module")
public class ModuleEntity extends SuperEntity<String> {

    /**
     * 功能上级
     */
    @TableField("F_PARENTID")
    private String parentId="0";

    /**
     * 功能类别
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 功能名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 功能编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 功能地址
     */
    @TableField("F_URLADDRESS")
    private String urlAddress;

    /**
     * 按钮权限
     */
    @TableField("F_ISBUTTONAUTHORIZE")
    private Integer isButtonAuthorize;

    /**
     * 列表权限
     */
    @TableField("F_ISCOLUMNAUTHORIZE")
    private Integer isColumnAuthorize;

    /**
     * 数据权限
     */
    @TableField("F_ISDATAAUTHORIZE")
    private Integer isDataAuthorize;

    /**
     * 表单权限
     */
    @TableField("F_ISFORMAUTHORIZE")
    private Integer isFormAuthorize;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTYJSON")
    private String propertyJson;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark=0;

    /**
     * 菜单图标
     */
    @TableField("F_ICON")
    private String icon;
    /**
     * 链接目标
     */
    @TableField("F_LINKTARGET")
    private String linkTarget;
    /**
     * 菜单分类 Web、App
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 关联功能id
     */
    @TableField("F_MODULEID")
    private String moduleId;

    /**
     * 关联系统id
     */
    @TableField("F_SYSTEMID")
    private String systemId;

}
