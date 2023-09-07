package org.openea.eap.extj.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("base_comfields")
public class ComFieldsEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    @TableField("F_FIELDNAME")
    private String fieldName;

    @TableField("F_FIELD")
    private String field;

    @TableField("F_DATATYPE")
    private String datatype;

    @TableField("F_DATALENGTH")
    private String datalength;

    @TableField("F_ALLOWNULL")
    private Integer allowNull;

    @TableField("F_DESCRIPTION")
    private String description;

    @TableField("F_SORTCODE")
    private Long sortcode;

    @TableField("F_ENABLEDMARK")
    private Integer enabledmark;

    @TableField("F_CREATORTIME")
    private Date creatortime;

    @TableField("F_CREATORUSERID")
    private String creatoruserid;

    @TableField("F_LASTMODIFYTIME")
    @JSONField(name = "F_LastModifyTime")
    private Date lastModifyTime;

    @TableField("F_LASTMODIFYUSERID")
    private String lastmodifyuserid;

    @TableField("F_DELETEMARK")
    private Integer deletemark;

    @TableField("F_DELETETIME")
    private Date deletetime;

    @TableField("F_DELETEUSERID")
    private String deleteuserid;

}
