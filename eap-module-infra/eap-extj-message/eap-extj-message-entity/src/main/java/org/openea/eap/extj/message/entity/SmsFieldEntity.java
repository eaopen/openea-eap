package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 短信变量表
 */
@Data
@TableName("base_message_sms_field")
public class SmsFieldEntity extends SuperEntity<String>  {

    /** 模板 **/
    @TableField("F_TEMPLATEID")
    private String templateId;

    /** 参数id **/
    @TableField("F_FIELDID")
    private String fieldId;

    /** 短信变量 **/
    @TableField("F_SMSFIELD")
    private String smsField;

    /** 参数 **/
    @TableField("F_FIELD")
    private String field;

    /** 是否标题 **/
    @TableField("F_ISTITLE")
    private Integer isTitle;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
}
