package org.openea.eap.extj.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片和语音共有属性
 */
@Data
public class MessageTypeModel implements Serializable {

    protected String path;

}
