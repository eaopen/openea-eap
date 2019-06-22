package org.openea.eapboot.modules.activiti.utils;

import org.openea.eapboot.common.constant.ActivitiConstant;
import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.utils.EmailUtil;
import org.openea.eapboot.common.utils.SmsUtil;
import org.openea.eapboot.modules.activiti.vo.EmailMessage;
import org.openea.eapboot.modules.base.entity.MessageSend;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.service.MessageSendService;
import org.openea.eapboot.modules.base.service.UserService;
import org.openea.eapboot.modules.base.vo.OtherSetting;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.exceptions.ClientException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Transactional
@Component
@Slf4j
public class MessageUtil {

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSendService messageSendService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public OtherSetting getOtherSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.OTHER_SETTING);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("系统未配置访问域名");
        }
        return new Gson().fromJson(v, OtherSetting.class);
    }

    /**
     * 发送工作流消息
     * @param userId 发送用户
     * @param content 消息内容
     * @param sendMessage 是否发站内信息
     * @param sendSms 是否发短信
     * @param sendEmail 是否发邮件
     */
    @Async
    public void sendActMessage(String userId, String content, Boolean sendMessage, Boolean sendSms, Boolean sendEmail){

        User user = userService.get(userId);
        if(user==null){
            return;
        }
        MessageSend messageSend = new MessageSend();
        messageSend.setUserId(user.getId());
        if(sendMessage&& ActivitiConstant.MESSAGE_TODO_CONTENT.equals(content)){
            // 待办
            messageSend.setMessageId(ActivitiConstant.MESSAGE_TODO_ID);
            messageSendService.send(messageSend);
        }else if(sendMessage&&ActivitiConstant.MESSAGE_PASS_CONTENT.equals(content)){
            // 通过
            messageSend.setMessageId(ActivitiConstant.MESSAGE_PASS_ID);
            messageSendService.send(messageSend);
        }else if(sendMessage&&ActivitiConstant.MESSAGE_BACK_CONTENT.equals(content)){
            // 驳回
            messageSend.setMessageId(ActivitiConstant.MESSAGE_BACK_ID);
            messageSendService.send(messageSend);
        }else if(sendMessage&&ActivitiConstant.MESSAGE_DELEGATE_CONTENT.equals(content)){
            // 委托
            messageSend.setMessageId(ActivitiConstant.MESSAGE_DELEGATE_ID);
            messageSendService.send(messageSend);
        }
        if(StrUtil.isNotBlank(user.getMobile())&&sendSms){
            try {
                smsUtil.sendActMessage(user.getMobile(), content);
            } catch (ClientException e) {
                log.error(e.toString());
            }
        }
        if(StrUtil.isNotBlank(user.getEmail())&&sendEmail){
            EmailMessage e = new EmailMessage();
            e.setUsername(user.getUsername());
            e.setContent(content);
            e.setFullUrl(getOtherSetting().getDomain());
            emailUtil.sendTemplateEmail(user.getEmail(), "【EapBoot】工作流通知提醒", "act-message-email", e);
        }
    }
}
