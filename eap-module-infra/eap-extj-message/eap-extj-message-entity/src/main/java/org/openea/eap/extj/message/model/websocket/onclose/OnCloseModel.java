package org.openea.eap.extj.message.model.websocket.onclose;

import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;

/**
 * 关闭连接model
 */
@Data
public class OnCloseModel extends MessageModel implements Serializable {

    private String userId;

    public OnCloseModel(String userId, String method) {
        this.userId = userId;
        super.method = method;
    }
}
