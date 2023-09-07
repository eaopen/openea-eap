package org.openea.eap.extj.message.model.messagemonitor;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MessageMonitorListVO {
    @Schema(description = "主键")
    private String id;

    /**
     * 消息类型
     **/
    @Schema(description = "消息类型")
    @JSONField(name = "messageType")
    private String messageType;

    /**
     * 消息来源
     **/
    @Schema(description = "消息来源")
    @JsonProperty("messageSource")
    private String messageSource;

    /**
     * 发送时间
     **/
    @Schema(description = "发送时间")
    @JSONField(name = "sendTime")
    private Long sendTime;

    /**
     * 标题
     **/
    @Schema(description = "标题")
    @JSONField(name = "title")
    private String title;


}