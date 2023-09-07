package org.openea.eap.extj.message.model;

import lombok.Data;

import java.util.Date;


@Data
public class ImReplyListModel {
    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String realName;

    /**
     * 头像
     */
    private String headIcon;

    /**
     * 最新消息
     */
    private String latestMessage;

    /**
     * 最新时间
     */
    private Date latestDate;

    /**
     * 未读消息
     */
    private Integer unreadMessage;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 账号
     */
    private String account;

    /**
     * UserId
     */
    private String userId;

    /**
     * sendDeleteMark
     */
    private String sendDeleteMark;

    /**
     * imreplySendDeleteMark
     */
    private String imreplySendDeleteMark;

    /**
     * deleteMark
     */
    private int deleteMark;


}
