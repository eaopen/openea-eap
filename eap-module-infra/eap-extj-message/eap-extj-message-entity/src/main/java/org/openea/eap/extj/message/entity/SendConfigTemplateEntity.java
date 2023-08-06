package org.openea.eap.extj.message.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 发送配置模板表
 *
 *
 */
@Data
@TableName("base_message_send_template")
public class SendConfigTemplateEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /** 发送配置id **/
    @TableField("F_SENDCONFIGID")
    private String sendConfigId;
    /** 消息类型 **/
    @TableField("F_MESSAGETYPE")
    private String messageType;
    /** 消息模板id **/
    @TableField("F_TEMPLATEID")
    private String templateId;
    /** 账号配置id **/
    @TableField("F_ACCOUNTCONFIGID")
    private String accountConfigId;

    /** 消息模板编号 **/
    @TableField(exist = false)
    private String templateCode;

    /** 消息模板名称 **/
    @TableField(exist = false)
    private String templateName;

    /** 账号编码 **/
    @TableField(exist = false)
    private String accountCode;

    /** 账号名称 **/
    @TableField(exist = false)
    private String accountName;

}
