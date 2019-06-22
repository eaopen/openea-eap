package org.openea.eapboot.modules.base.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.base.entity.MessageSend;

/**
 * 消息发送数据处理层
 */
public interface MessageSendDao extends EapBaseDao<MessageSend,String> {

    /**
     * 通过消息id删除
     * @param messageId
     */
    void deleteByMessageId(String messageId);
}