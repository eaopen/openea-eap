package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 消息模板参数表
 */
@Data
@TableName("base_message_template_param")
public class TemplateParamEntity extends SuperEntity<String>  {

    /** 消息模板id **/
    @TableField("F_TEMPLATEID")
    private String templateId;

    /** 参数 **/
    @TableField("F_FIELD")
    private String field;

    /** 参数说明 **/
    @TableField("F_FIELDNAME")
    private String fieldName;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
