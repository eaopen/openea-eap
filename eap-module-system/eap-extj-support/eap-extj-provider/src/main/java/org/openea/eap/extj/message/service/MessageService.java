package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.message.entity.MessageEntity;
import org.openea.eap.extj.message.entity.MessageReceiveEntity;
import org.openea.eap.extj.message.entity.MessageTemplateEntity;
import org.openea.eap.extj.message.model.NoticePagination;
import org.openea.eap.extj.message.model.message.SentMessageForm;

import java.util.List;
import java.util.Map;

/**
 * 消息实例
 *
 */
public interface MessageService extends SuperService<MessageEntity> {

    /**
     * 列表（通知公告）
     *
     * @param pagination
     * @return
     */
    List<MessageEntity> getNoticeList(NoticePagination pagination);

    /**
     * 列表（通知公告）
     *
     * @return
     */
    List<MessageEntity> getNoticeList();

    /**
     * 列表（通知公告）
     * 门户专用
     *
     * @return
     */
    List<MessageEntity> getDashboardNoticeList(List<String> typeList);

    /**
     * 列表（通知公告/系统消息/私信消息）
     *
     * @param pagination
     * @param type       类别
     * @return
     */
    List<MessageEntity> getMessageList1(Pagination pagination, Integer type,Integer isRead);

    /**
     * 获取全部数据
     *
     * @param pagination
     * @param type       类别
     * @return
     */
    List<MessageEntity> getMessageList3(Pagination pagination, Integer type, String user, Integer isRead);

    /**
     * 列表（通知公告/系统消息/私信消息）
     *
     * @param pagination
     * @param type       类别
     * @return
     */
    List<MessageEntity> getMessageList(Pagination pagination, Integer type,String userId);

    /**
     * 列表（通知公告/系统消息/私信消息）
     *
     * @param pagination
     * @return
     */
    List<MessageEntity> getMessageList(Pagination pagination);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    MessageEntity getinfo(String id);

    /**
     * 默认消息
     *
     * @param type 类别:1-通知公告/2-系统消息
     * @return
     */
    MessageEntity getInfoDefault(int type);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(MessageEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(MessageEntity entity);

    /**
     * 更新
     *
     * @param entity 实体对象
     */
    boolean update(String id, MessageEntity entity);

    /**
     * 消息已读（单条）
     *
     * @param messageId 消息主键
     */
    MessageReceiveEntity messageRead(String messageId);

    /**
     * 消息已读（全部）
     */
    void messageRead(List<String> idList);

    /**
     * 删除记录
     *
     * @param messageIds 消息Id
     */
    void deleteRecord(List<String> messageIds);

    /**
     * 获取未读数量（含 通知公告、系统消息）
     *
     * @param userId 用户主键
     */
    int getUnreadCount(String userId);

    /**
     * 获取公告未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadNoticeCount(String userId);

    /**
     * 获取消息未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadCount(String userId,Integer type);

    /**
     * 获取消息未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadMessageCount(String userId);

    /**
     * 获取系统消息未读数量
     *
     * @param userId 用户主键
     * @return
     */
    int getUnreadSystemMessageCount(String userId);

    /**
     * 发送公告
     *
     * @param toUserIds 发送用户
     * @param entity    消息信息
     */
    void sentNotice(List<String> toUserIds, MessageEntity entity);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     */
    void sentMessage(List<String> toUserIds, String title);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     * @param bodyText  内容
     */
    void sentMessage(List<String> toUserIds, String title, String bodyText);

    /**
     * 发送消息
     *
     * @param toUserIds  发送用户
     * @param title      标题
     * @param bodyText   内容
     * @param contentMsg 站内信息
     */
    void sentMessage(List<String> toUserIds, String title, String bodyText, Map<String, String> contentMsg, UserInfo userInfo);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     * @param bodyText  内容
     */
    void sentMessage(List<String> toUserIds, String title, String bodyText, UserInfo userInfo, Integer source,Integer type);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param title     标题
     * @param bodyText  内容
     * @param testMessage  是否为测试消息
     */
    void sentMessage(List<String> toUserIds, String title, String bodyText, UserInfo userInfo, Integer source,Integer type, boolean testMessage);

    /**
     * 发送消息
     *
     * @param toUserIds 发送用户
     * @param entity    消息实体
     * @param content   内容
     */
    void sentFlowMessage(List<String> toUserIds, MessageTemplateEntity entity, String content);

    /**
     * 退出在线的WebSocket 可选参数
     * @param token Token 精准退出用户
     * @param userId 退出用户的全部会话
     */
    void logoutWebsocketByToken(String token, String userId);

    /**
     * 委托发送消息
     *
     * @param toUserIds  发送用户
     * @param title      标题
     * @param bodyText   内容
     * @param contentMsg 站内信息
     * @param userInfo
     */
    void sentDelegateMessage(List<String> toUserIds, String title, String bodyText, Map<String, String> contentMsg, UserInfo userInfo);

    /**
     * 日程发送消息
     */
    void sendScheduleMessage(SentMessageForm sentMessageForm);

    /**
     * 通过过期时间刷新状态
     *
     * @return
     */
    Boolean updateEnabledMark();
}
