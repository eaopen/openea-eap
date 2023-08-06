package org.openea.eap.extj.message.enums;

/**
 * 消息类型枚举
 *
 *
 */
public enum MessageTypeEnum {
    /**
     * 站内消息
     */
    SysMessage("1", "站内消息"),
    /**
     * 发送邮件
     */
    MailMessage("2", "发送邮件"),
    /**
     * 发送短信
     */
    SmsMessage("3", "发送短信"),
    /**
     * 钉钉消息
     */
    DingMessage("4", "发送钉钉消息"),
    /**
     * 企业微信
     */
    QyMessage("5", "发送企业微信消息"),
    /**
     * webhook
     */
    WebHookMessage("6", "发送webhook消息"),
    /**
     * 微信公众号
     */
    WechatMessage("7", "发送微信公众号消息");

    /**
     *  为防止与系统后续更新的功能的消息类型code冲突，客户自定义添加的消息类型code请以ZDY开头。例如：ZDY1
     */


    private String code;
    private String message;

    MessageTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据状态code获取枚举值
     *
     * @return
     */
    public static MessageTypeEnum getByCode(String code) {
        for (MessageTypeEnum status : MessageTypeEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
