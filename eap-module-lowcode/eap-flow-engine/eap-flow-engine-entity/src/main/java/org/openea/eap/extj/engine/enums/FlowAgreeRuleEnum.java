package org.openea.eap.extj.engine.enums;

/**
 * 同意规则
 * 
 */
public enum FlowAgreeRuleEnum {
    /**
     * 不启用
     */
    notstart(1, "不启用"),
    /**
     * 审批人为发起人
     */
    initiator(2, "发起人"),
    /**
     * 审批人与上一审批节点处理人相同
     */
    node(3, "上一审批节点处理人相同"),
    /**
     * 审批人审批过
     */
    pass(4, "审批人有审批过");

    private int code;
    private String message;

    FlowAgreeRuleEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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
    public static FlowAgreeRuleEnum getByCode(Integer code) {
        for (FlowAgreeRuleEnum status : FlowAgreeRuleEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
