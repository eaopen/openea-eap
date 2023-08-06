package org.openea.eap.extj.message.model.message;

import lombok.Data;

/**
 * 发送邮件配置模型
 */
@Data
public class EmailModel {
    private String emailPop3Host;
    private String emailPop3Port;
    private String emailSmtpHost;
    private String emailSmtpPort;
    private String emailSenderName;
    private String emailAccount;
    private String emailPassword;
    private String emailSsl;

    private String emailToUsers;
    private String emailContent;
    private String emailTitle;
}
