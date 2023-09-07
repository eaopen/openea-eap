package org.openea.eap.extj.message.model.websocket.onconnettion;

import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;
import java.util.List;

/**
 * 刚连接websocket时推送的模型
 */
@Data
public class OnConnectionModel extends MessageModel implements Serializable {

    private List<String> onlineUsers;

    private List unreadNums;

    private Integer unreadNoticeCount;

    private String noticeDefaultText;

    private Integer unreadMessageCount;

    private Integer unreadScheduleCount;

    private Integer unreadSystemMessageCount;

    private String messageDefaultText;

    private Long messageDefaultTime;

    private Integer unreadTotalCount;

    private String userId;

}
