package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

import java.util.Date;


@Data
@TableName("base_filter")
public class FilterEntity extends SuperBaseEntity.SuperTBaseEntity {
    @TableId("f_id")
    private String id;
    /**
     * 在线和代码生成记录主键
     */
    @TableField("f_moduleId")
    private String moduleId;
    /**
     * 过滤配置
     */
    @TableField("f_config")
    private String config;

    @TableField("f_createTime")
    private Date createTime;

    @TableField("f_updateTime")
    private Date updateTime;
    @TableField("f_configApp")
    private String configApp;
}