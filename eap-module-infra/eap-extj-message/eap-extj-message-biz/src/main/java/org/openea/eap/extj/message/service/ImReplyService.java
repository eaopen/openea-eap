package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.eap.extj.message.entity.ImReplyEntity;
import org.openea.eap.extj.message.model.ImReplyListModel;

import java.util.List;

/**
 * 聊天会话
 *
 *
 */
public interface ImReplyService extends SuperService<ImReplyEntity> {

    /**
     * 获取消息会话列表
     *
     * @return
     */
    List<ImReplyEntity> getList();

    /**
     * 保存聊天会话
     *
     * @param entity
     * @return
     */
    boolean savaImReply(ImReplyEntity entity);

    /**
     * 获取聊天会话列表
     *
     * @return
     */
    List<ImReplyListModel> getImReplyList();

    /**
     * 移除聊天会话列表
     *
     * @return
     */
    boolean relocation(String sendUserId,String receiveUserId);

}
