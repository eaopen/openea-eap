package org.openea.eap.extj.engine.enums;

/**
 * 提交状态
 *
 *
 */
public enum FlowStatusEnum {
    //不操作
    none("-1"),
    //保存
    save("1"),
    // 提交
    submit("0");

    private String message;

    FlowStatusEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据状态code获取枚举名称
     *
     * @return
     */
    public static FlowStatusEnum getByCode(Integer code) {
        for (FlowStatusEnum status : FlowStatusEnum.values()) {
            if (status.getMessage().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
