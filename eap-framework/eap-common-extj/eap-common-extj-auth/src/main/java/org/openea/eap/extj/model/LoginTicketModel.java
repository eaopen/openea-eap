package org.openea.eap.extj.model;

import org.openea.eap.extj.consts.LoginTicketStatus;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * 轮询登录模型
 */
@Data
@Accessors(chain = true)
public class LoginTicketModel {

    /**
     * 状态
     * @see LoginTicketStatus
     */
    @NonNull
    private int status = LoginTicketStatus.UnLogin.getStatus();

    /**
     * 额外的值, 登录Token、第三方登录的ID
     */
    private String value;

    /**
     * 前端主题
     */
    private String theme;

    /**
     * 票据有效期, 时间戳
     */
    private Long ticketTimeout;

}
