package org.openea.eap.extj.message.model.websocket.savafile;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片消息模型
 */
@Data
public class ImageMessageModel extends MessageTypeModel implements Serializable {

    private String width;

    private String height;

    public ImageMessageModel(String width, String height, String path) {
        this.width = width;
        this.height = height;
        super.path = path;
    }

}
