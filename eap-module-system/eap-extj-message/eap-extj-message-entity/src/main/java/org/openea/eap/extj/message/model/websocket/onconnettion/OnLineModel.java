package org.openea.eap.extj.message.model.websocket.onconnettion;

import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;

/**
 * 用户在线推送模型
 */
@Data
public class OnLineModel extends MessageModel implements Serializable {

    /**
     * 在线用户
     */
    private String userId;

    public OnLineModel(String method, String userId) {
        super.method = method;
        this.userId = userId;
    }
}
