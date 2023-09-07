package org.openea.eap.extj.onlinedev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

import java.util.Date;
@Data
@TableName("base_visualdev_modeldata")
public class VisualdevModelDataEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    @TableField("F_VISUALDEVID")
    private String visualDevId;

    @TableField("F_SORTCODE")
    private Long sortcode;

    @TableField("F_ENABLEDMARK")
    private Integer enabledmark;

    @TableField("F_CREATORTIME")
    private Date creatortime;

    @TableField("F_CREATORUSERID")
    private String creatoruserid;

    @TableField("F_LASTMODIFYTIME")
    private Date lastModifyTime;

    @TableField("F_LASTMODIFYUSERID")
    private String lastmodifyuserid;

    @TableField("F_DELETEMARK")
    private Integer deletemark;

    @TableField("F_DELETETIME")
    private Date deletetime;

    @TableField("F_DELETEUSERID")
    private String deleteuserid;
    @TableField("F_DATA")
    private String data;

}
