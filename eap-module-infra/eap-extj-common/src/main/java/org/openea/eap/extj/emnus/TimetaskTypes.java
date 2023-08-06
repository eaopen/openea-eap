package org.openea.eap.extj.emnus;

public enum TimetaskTypes {
    One(1, "执行一次"),
    Two(2, "重复执行"),
    Three(3, "调度明细"),
    Four(4, "调度任务");

    private int code;
    private String message;

    private TimetaskTypes(int code, String message) {
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