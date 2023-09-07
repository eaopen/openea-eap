package org.openea.eap.extj.message.model.websocket.receivemessage;

import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;

/**
 * 返回接受消息模型
 */
@Data
public class ReceiveMessageVO extends MessageModel implements Serializable {

    private String formUserId;

    private Long dateTime;

    private String headIcon;

    private Long latestDate;

    private String realName;

    private String account;

    private String messageType;

    private Object formMessage;

}
