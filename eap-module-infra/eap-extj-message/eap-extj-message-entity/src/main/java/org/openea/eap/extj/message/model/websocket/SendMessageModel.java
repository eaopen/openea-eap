package org.openea.eap.extj.message.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.message.entity.MessageEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 发送消息到mq模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageModel implements Serializable {

    private List<String> toUserIds;

    private MessageEntity entity;

    private UserInfo userInfo;

    private Integer messageType;

}
