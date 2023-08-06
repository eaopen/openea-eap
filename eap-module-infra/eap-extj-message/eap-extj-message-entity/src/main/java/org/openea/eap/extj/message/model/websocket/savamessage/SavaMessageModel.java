package org.openea.eap.extj.message.model.websocket.savamessage;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.openea.eap.extj.message.model.websocket.model.MessageModel;

import java.io.Serializable;

/**
 * 保存消息模型
 */
@Data
public class SavaMessageModel extends MessageModel implements Serializable {

    @JSONField(name = "UserId")
    private String userId;

    private String toUserId;

    private Long dateTime;

    private String headIcon;

    private Long latestDate;

    private String realName;

    private String account;

    private String toAccount;

    private String toRealName;

    private String toHeadIcon;

    private String messageType;

    private Object toMessage;

}
