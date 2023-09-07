package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 消息监控表
 *
 */
@Data
@TableName("base_message_monitor")
public class MessageMonitorEntity extends SuperEntity<String>  {

    @TableField("F_ACCOUNTID")
    private String accountId;

    @TableField("F_ACCOUNTNAME")
    private String accountName;

    @TableField("F_ACCOUNTCODE")
    private String accountCode;

    @TableField("F_MESSAGETYPE")
    private String messageType;

    @TableField("F_MESSAGESOURCE")
    private String messageSource;

    @TableField("F_SENDTIME")
    private Date sendTime;

    @TableField("F_MESSAGETEMPLATEID")
    private String messageTemplateId;

    @TableField("F_TITLE")
    private String title;

    @TableField("F_RECEIVEUSER")
    private String receiveUser;

    @TableField("F_CONTENT")
    private String content;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
