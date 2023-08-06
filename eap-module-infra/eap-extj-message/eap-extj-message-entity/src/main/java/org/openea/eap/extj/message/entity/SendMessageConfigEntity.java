package org.openea.eap.extj.message.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 消息发送配置表
 *
 *
 */
@Data
@TableName("base_message_send_config")
public class SendMessageConfigEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    @TableField("F_FULLNAME")
    private String fullName;

    @TableField("F_ENCODE")
    private String enCode;

//    @TableField("F_MESSAGETYPE")
//
//    private String messageType;

    @TableField("F_TEMPLATETYPE")
    private String templateType;

    @TableField("F_MESSAGESOURCE")
    private String messageSource;

    @TableField("F_USEDID")
    private String usedId;

}
