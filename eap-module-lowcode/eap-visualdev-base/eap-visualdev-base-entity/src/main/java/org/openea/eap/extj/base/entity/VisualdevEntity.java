package org.openea.eap.extj.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 可视化开发功能表
 */
@Data
@TableName("base_visualdev")
public class VisualdevEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    @TableField("F_DESCRIPTION")
    private String description;

    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUser;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUser;

    @TableField(value = "F_DELETEMARK", updateStrategy = FieldStrategy.IGNORED)
    private Integer deleteMark;

    @TableField("F_DELETETIME")
    private Date deleteTime;

    @TableField("F_DELETEUSERID")
    private String deleteUserId;

    /**
     * 名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 关联的表
     */
    @TableField("F_TABLE")
    @JSONField(name = "tables")
    private String visualTables;

    /**
     * 分类(数据字典维护)
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 表单配置JSON
     */
    @TableField("F_FORMDATA")
    private String formData;

    /**
     * 列表配置JSON
     */
    @TableField("F_COLUMNDATA")
    private String columnData;

    /**
     * 列表配置JSON
     */
    @TableField("F_APPCOLUMNDATA")
    private String appColumnData;

    /**
     * 关联数据连接id
     */
    @TableField("F_DbLinkId")
    private String dbLinkId;
    /**
     * 页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）
     */
    @TableField("F_WebType")
    private Integer webType;

    /**
     * 是否启用流程
     */
    @TableField("F_ENABLEFLOW")
    private Integer enableFlow = 0;

    /**
     * 关联工作流连接id
     */
    @TableField("F_FlowId")
    private String flowId;

    /**
     * 数据接口id
     */
    @TableField("F_INTERFACEID")
    private String interfaceId;

    /**
     * 数据接口名称
     */
    @TableField("F_INTERFACENAME")
    private String interfaceName;
    /**
     * 数据接口参数json
     */
    @TableField("F_INTERFACEPARAM")
    private String interfaceParam;
}
