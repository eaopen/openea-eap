package org.openea.eap.extj.message.util;

import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.message.model.message.EmailModel;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 邮件类
 */
public class EmailUtil {
    public static boolean isEmail(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        Boolean b = email.matches(EMAIL_REGEX);
        return b;
    }

    public static JSONObject sendMail(EmailModel emailModel) {
        JSONObject retMsg = new JSONObject();
        // 邮件发送人
        String from = emailModel.getEmailAccount();
        // 邮件接收人的邮件地址
        String to = emailModel.getEmailToUsers();

        //定义Properties对象,设置环境信息
        Properties props = System.getProperties();

        // 设置邮件服务器的地址
        // 指定的smtp服务器
        props.setProperty("mail.smtp.host", emailModel.getEmailSmtpHost());
        props.setProperty("mail.smtp.auth", "true");
        //ssl安全链接
        props.setProperty("mail.smtp.ssl.enable", emailModel.getEmailSsl());
        //设置发送邮件使用的协议
        props.setProperty("mail.transport.protocol", "smtp");
        if ("587".equals(emailModel.getEmailSmtpPort())) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        //创建Session对象,session对象表示整个邮件的环境信息
        Session session = Session.getInstance(props);
        //设置输出调试信息
        session.setDebug(true);
        try {
            // Message的实例对象表示一封电子邮件
            MimeMessage message = new MimeMessage(session);
            // 设置发件人的地址
            message.setFrom(new InternetAddress(from, emailModel.getEmailSenderName(), "UTF-8"));
            // 设置收件人信息
            InternetAddress[] sendTo = InternetAddress.parse(to);
            message.setRecipients(MimeMessage.RecipientType.TO, sendTo);

            // 设置主题
            message.setSubject(emailModel.getEmailTitle());
            // 设置邮件的文本内容
            message.setContent((emailModel.getEmailContent()), "text/html;charset=utf-8");

            // 设置附件
            //message.setDataHandler(dh);

            // 获取发送邮件的对象
            Transport transport = session.getTransport();
            // 连接邮件服务器
            transport.connect(emailModel.getEmailSmtpHost(), Integer.parseInt(emailModel.getEmailSmtpPort()), emailModel.getEmailAccount(), emailModel.getEmailPassword());
            // 发送消息
            transport.sendMessage(message, sendTo);

            transport.close();

            retMsg.put("code", true);
            retMsg.put("error", "");
            return retMsg;

//            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            retMsg.put("code", false);
            retMsg.put("error", e.toString());
            return retMsg;
//            e.printStackTrace();
//            return false;
        }
    }
}

