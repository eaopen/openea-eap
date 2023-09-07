package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 账号配置使用记录表
 *
 */
@Data
@TableName("base_message_send_record")
public class SendConfigRecordEntity extends SuperEntity<String>  {

    @TableField("F_SENDCONFIGID")
    private String sendConfigId;

    @TableField("F_MESSAGESOURCE")
    private String messageSource;

    @TableField("F_USEDID")
    private String usedId;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
