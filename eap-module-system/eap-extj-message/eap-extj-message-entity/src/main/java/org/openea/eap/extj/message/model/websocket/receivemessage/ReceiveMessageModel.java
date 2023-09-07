package org.openea.eap.extj.message.model.websocket.receivemessage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;

/**
 * 接受消息模型
 */
@Data
public class ReceiveMessageModel extends MessageModel implements Serializable {

    private String formUserId;

    private Long dateTime;

    private String headIcon;

    private Long latestDate;

    private String realName;

    private String account;

    private String messageType;

    private Object formMessage;

    @JsonIgnore
    private String userId;

}
