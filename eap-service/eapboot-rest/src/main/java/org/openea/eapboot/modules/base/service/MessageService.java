package org.openea.eapboot.modules.base.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.base.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 消息内容接口
 */
public interface MessageService extends EapBaseService<Message,String> {

    /**
     * 多条件分页获取
     * @param message
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<Message> findByCondition(Message message, SearchVo searchVo, Pageable pageable);

    /**
     * 通过创建发送标识获取
     * @param createSend
     * @return
     */
    List<Message> findByCreateSend(Boolean createSend);
}