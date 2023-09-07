package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息中心模块数据类型表
 *
 */
@Data
@TableName("base_message_data_type")
public class MessageDataTypeEntity extends SuperEntity<String> {

    @TableField("F_TYPE")
    private String type;

    @TableField("F_FULLNAME")
    private String fullName;

    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
}
