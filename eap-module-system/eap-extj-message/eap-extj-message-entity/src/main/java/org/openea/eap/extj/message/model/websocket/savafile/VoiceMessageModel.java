package org.openea.eap.extj.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 语音消息模型
 */
@Data
public class VoiceMessageModel extends MessageTypeModel implements Serializable {

    private String length;

    public VoiceMessageModel(String length, String path) {
        this.length = length;
        super.path = path;
    }

}
