package org.openea.eapboot.modules.base.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.base.entity.MessageSend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 消息发送接口
 */
public interface MessageSendService extends EapBaseService<MessageSend,String> {

    /**
     * 发送消息 带websock推送
     * @param messageSend
     * @return
     */
    MessageSend send(MessageSend messageSend);

    /**
     * 通过消息id删除
     * @param messageId
     */
    void deleteByMessageId(String messageId);

    /**
     * 多条件分页获取
     * @param messageSend
     * @param pageable
     * @return
     */
    Page<MessageSend> findByCondition(MessageSend messageSend, Pageable pageable);
}