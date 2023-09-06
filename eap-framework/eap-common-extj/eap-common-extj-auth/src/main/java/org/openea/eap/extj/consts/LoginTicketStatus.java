package org.openea.eap.extj.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum LoginTicketStatus {

    /**
     * 登录成功
     */
    Success(1),
    /**
     * 未登录
     */
    UnLogin(2),
    /**
     * 登录失败
     */
    ErrLogin(3),
    /**
     * 未绑定
     */
    UnBind(4),
    /**
     * 失效
     */
    Invalid(5),
    /**
     * 失效
     */
    Multitenancy(6);



    private int status;
}
