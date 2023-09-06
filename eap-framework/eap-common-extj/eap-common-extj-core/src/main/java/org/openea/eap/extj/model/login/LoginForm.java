package org.openea.eap.extj.model.login;

import lombok.Data;

/**
 *
 *
 */
@Data
public class LoginForm {
    private String account;
    private String password;
    /**
     * 登录类型
     */
    private String grantType;
    /**
     * 验证码标识
     */
    private String timestamp;
    /**
     * 验证码
     */
    private String code;

    public LoginForm() {
    }

    public LoginForm(String account, String password) {
        this.account = account;
        this.password = password;
    }
}
