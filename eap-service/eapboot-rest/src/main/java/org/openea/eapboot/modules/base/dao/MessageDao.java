package org.openea.eapboot.modules.base.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.base.entity.Message;

import java.util.List;

/**
 * 消息内容数据处理层
 */
public interface MessageDao extends EapBaseDao<Message,String> {

    /**
     * 通过创建发送标识获取
     * @param createSend
     * @return
     */
    List<Message> findByCreateSend(Boolean createSend);
}