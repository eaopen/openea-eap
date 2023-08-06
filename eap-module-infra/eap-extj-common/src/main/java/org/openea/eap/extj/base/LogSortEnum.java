package org.openea.eap.extj.base;

public enum LogSortEnum {
    Login(1, "登录"),
    Visit(2, "访问"),
    Operate(3, "操作"),
    Exception(4, "异常"),
    Request(5, "请求");

    private int code;
    private String message;

    private LogSortEnum(int code, String message) {
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

    public static String getMessageByCode(Integer code) {
        LogSortEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            LogSortEnum status = var1[var3];
            if (status.getCode().equals(code)) {
                return status.message;
            }
        }

        return null;
    }

    public static LogSortEnum getByCode(Integer code) {
        LogSortEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            LogSortEnum status = var1[var3];
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        return null;
    }
}
