package org.openea.eap.extj.message.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 *
 * 消息模板表
 */
@Data
@TableName("base_message_template_config")
public class MessageTemplateConfigEntity extends SuperExtendEntity<String> {

    @TableField("F_FULLNAME")
    private String fullName;

    @TableField("F_ENCODE")
    private String enCode;

    @TableField("F_TEMPLATETYPE")
    private String templateType;

    @TableField("F_MESSAGESOURCE")
    private String messageSource;

    @TableField("F_MESSAGETYPE")
    private String messageType;

    @TableField("F_SORTCODE")
    private Integer sortCode;

    @TableField("F_TITLE")
    private String title;

    @TableField("F_CONTENT")
    private String content;

    @TableField("F_TEMPLATECODE")
    private String templateCode;

    @TableField("F_WXSKIP")
    private String wxSkip;

    @TableField("F_XCXAPPID")
    private String xcxAppId;

}
