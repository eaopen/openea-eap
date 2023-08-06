package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.eap.extj.base.PageModel;
import org.openea.eap.extj.message.entity.ImContentEntity;
import org.openea.eap.extj.message.model.ImUnreadNumModel;

import java.util.List;

/**
 * 聊天内容
 *
 *
 */
public interface ImContentService extends SuperService<ImContentEntity> {

    /**
     * 获取消息列表
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param pageModel
     * @return
     */
    List<ImContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    List<ImUnreadNumModel> getUnreadList(String receiveUserId);

    /**
     * 获取未读消息
     *
     * @param receiveUserId 接收者
     * @return
     */
    int getUnreadCount(String sendUserId, String receiveUserId);

    /**
     * 发送消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @param message       消息内容
     * @param messageType   消息类型
     * @return
     */
    void sendMessage(String sendUserId, String receiveUserId, String message, String messageType);

    /**
     * 已读消息
     *
     * @param sendUserId    发送者
     * @param receiveUserId 接收者
     * @return
     */
    void readMessage(String sendUserId, String receiveUserId);
    /**
     * 删除聊天记录
     *
     * @return
     */
    boolean deleteChatRecord(String sendUserId, String receiveUserId);

}
