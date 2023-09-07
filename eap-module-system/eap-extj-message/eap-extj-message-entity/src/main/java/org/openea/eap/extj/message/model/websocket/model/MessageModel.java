package org.openea.eap.extj.message.model.websocket.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息模型
 */
@Data
public class MessageModel implements Serializable {

    protected String method;

}
