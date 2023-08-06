package org.openea.eap.extj.base;

public enum ActionResultCode {
    Success(200, "成功"),
    Fail(400, "失败"),
    ValidateError(401, "验证错误"),
    Exception(500, "异常"),
    SessionOverdue(600, "登录过期,请重新登录"),
    SessionOffLine(601, "您的帐号在其他地方已登录,被强制踢出"),
    SessionError(602, "Token验证失败");

    private int code;
    private String message;

    private ActionResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}