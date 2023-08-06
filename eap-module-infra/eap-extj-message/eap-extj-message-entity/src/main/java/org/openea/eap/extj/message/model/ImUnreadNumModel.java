package org.openea.eap.extj.message.model;

import lombok.Data;

/**
 * 未读消息模型
 */
@Data
public class ImUnreadNumModel {
    /**
     * 发送者Id
     */
    private String sendUserId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 未读数量
     */
    private int unreadNum;

    /**
     * 默认消息
     */
    private String defaultMessage;

    /**
     * 默认消息类型
     */
    private String defaultMessageType;

    /**
     * 默认消息时间
     */
    private String defaultMessageTime;
}
