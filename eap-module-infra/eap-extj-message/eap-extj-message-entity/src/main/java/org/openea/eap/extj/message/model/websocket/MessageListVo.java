package org.openea.eap.extj.message.model.websocket;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息列表单个模型
 */
@Data
public class MessageListVo implements Serializable {

    private String content;

    private String contentType;

    private String id;

    private Long receiveTime;

    private String receiveUserId;

    private Long sendTime;

    private String sendUserId;

    private Integer state;

}
