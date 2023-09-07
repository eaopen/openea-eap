package org.openea.eap.extj.message.util;

import org.openea.eap.extj.message.entity.MessageEntity;
import org.openea.eap.extj.message.entity.MessageReceiveEntity;
import org.openea.eap.extj.util.RandomUtil;

import java.util.Date;

/**
 * 消息实体类
 */
public class JnpfMessageUtil {
    public static MessageEntity setMessageEntity(String userId, String title, String bodyText, Integer recType) {
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(recType + 1);
        entity.setCreatorUser(userId);
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        return entity;
    }

    public static MessageReceiveEntity setMessageReceiveEntity(String messageId, String toUserId) {
        MessageReceiveEntity entity = new MessageReceiveEntity();
        entity.setId(RandomUtil.uuId());
        entity.setMessageId(messageId);
        entity.setUserId(toUserId);
        entity.setIsRead(0);
        return entity;
    }
}
