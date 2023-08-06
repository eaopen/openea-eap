package org.openea.eap.extj.message.util;

/**
 * 消息类型
 */
public enum SendMessageTypeEnum {

    /**
     * 文本消息
     */
    MESSAGE_TEXT("text"),
    /**
     * 语音消息
     */
    MESSAGE_VOICE("voice"),
    /**
     * 图片消息
     */
    MESSAGE_IMAGE("image");

    SendMessageTypeEnum() {
    }

    private String message;

    SendMessageTypeEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
