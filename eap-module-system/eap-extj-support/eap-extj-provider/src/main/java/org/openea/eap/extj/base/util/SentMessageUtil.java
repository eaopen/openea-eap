package org.openea.eap.extj.base.util;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.config.JnpfOauthConfig;
import org.openea.eap.extj.message.entity.*;
import org.openea.eap.extj.message.enums.MessageTypeEnum;
import org.openea.eap.extj.message.model.message.SentMessageForm;
import org.openea.eap.extj.message.service.*;
import org.openea.eap.extj.message.util.WebHookUtil;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SentMessageUtil {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessagereceiveService messagereceiveService;
//    @Autowired
//    private SynThirdInfoService synThirdInfoService;

//    @Autowired
//    private MessageTemplateService messageTemplateService;
//    @Autowired
//    private SmsTemplateService smsTemplateService;
    @Autowired
    private SendMessageConfigService sendMessageConfigService;

    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private MessageTemplateConfigService messageTemplateConfigService;
    @Autowired
    private MessageMonitorService messageMonitorService;
    @Autowired
    private SmsFieldService smsFieldService;
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private JnpfOauthConfig jnpfOauthConfig;
//    @Autowired
//    private WechatUserService wechatUserService;
    @Autowired
    private TemplateParamService templateParamService;
//    @Autowired
//    protected AuthUtil authUtil;

    /**
     * 发送消息
     *
     * @param sentMessageForm
     */
    public void sendMessage(SentMessageForm sentMessageForm) {
        List<String> toUserIdsList = sentMessageForm.getToUserIds();
        // 模板id
        String templateId = sentMessageForm.getTemplateId();
        // 参数
        Map<String, Object> parameterMap = sentMessageForm.getParameterMap();
        UserInfo userInfo = sentMessageForm.getUserInfo();
        boolean flag = true;
        if (!(toUserIdsList != null && toUserIdsList.size() > 0)) {
            log.error("接收人员为空");
            flag = false;
        }
        if (StringUtil.isEmpty(templateId)) {
            log.error("模板Id为空");
            flag = false;
        }
        if (flag) {
            // 获取发送配置详情
//            MessageTemplateEntity entity = messageTemplateService.getInfo(templateId);
            SendMessageConfigEntity entity = sendMessageConfigService.getInfoByEnCode(templateId);
            if(entity!= null){
                templateId = entity.getId();
            }else {
                entity = sendMessageConfigService.getInfo(templateId);
            }
            if(entity != null) {
                List<SendConfigTemplateEntity> list = sendConfigTemplateService.getDetailListByParentId(templateId);
                if(list != null && list.size()>0) {
                    for (SendConfigTemplateEntity entity1 : list) {
                        parameterMap.put(entity1.getId()+"@Title",sentMessageForm.getTitle());
                        parameterMap.put(entity1.getId()+"@CreatorUserName",sentMessageForm.getUserInfo().getUserName());
                        parameterMap.put(entity1.getId()+"@FlowLink","");
                        if ("1".equals(String.valueOf(entity1.getEnabledMark()))) {
                            String sendType = entity1.getMessageType();
                            MessageTypeEnum typeEnum = MessageTypeEnum.getByCode(sendType);
                            Map<String, String> contentMsg = sentMessageForm.getContentMsg();
                            switch (typeEnum) {
                                case SysMessage:
                                    // 站内消息、
                                    for(String toUserId : toUserIdsList) {
                                        List<String> toUser = new ArrayList<>();
                                        String content = sentMessageForm.getContent();
                                        MessageTemplateConfigEntity templateConfigEntity = messageTemplateConfigService.getInfo(entity1.getTemplateId());
                                        String title = sentMessageForm.getTitle();
                                        String appLink = "";
                                        if(templateConfigEntity != null){
                                            title = templateConfigEntity.getTitle();
                                            if("2".equals(templateConfigEntity.getMessageSource())){
                                                Map<String, Object> msgMap = new HashMap<>();
                                                msgMap.put("@Title",sentMessageForm.getTitle());
                                                StringSubstitutor strSubstitutor = new StringSubstitutor(msgMap, "{", "}");
                                                title = strSubstitutor.replace(title);
                                            }
                                            if (!"1".equals(templateConfigEntity.getTemplateType())) {
                                                String msg = contentMsg.get(toUserId) != null ? contentMsg.get(toUserId) : "{}";
                                                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                                                String encode = Base64.getEncoder().encodeToString(bytes);
                                                //流程审批页面链接地址
                                                //流程审批页面链接地址
                                                String pcLink = jnpfOauthConfig.getJnpfFrontDomain()+"/workFlowDetail?config=" + encode;
                                                appLink = jnpfOauthConfig.getJnpfAppDomain()+"/pages/workFlow/flowBefore/index?config="+encode;
                                                //转换为短链
                                                String shortLink = shortLinkService.shortLink(pcLink+toUserId+templateConfigEntity.getMessageType());
                                                shortLink = getShortLink(pcLink,toUserId,shortLink,templateConfigEntity.getMessageType());
                                                String link = jnpfOauthConfig.getJnpfDomain() +"/api/message/ShortLink/" + shortLink;
                                                if(StringUtil.isNotBlank(userInfo.getTenantId())){
                                                    link = link+"/"+userInfo.getTenantId();
                                                }
                                                if(title.contains("{@FlowLink}")) {
                                                    title = title.replace("{@FlowLink}", link+ " ");
                                                    //链接数据保存
                                                    this.saveShortLink(pcLink,appLink,shortLink,userInfo,toUserId,msg);
                                                }
                                                Map<String, Object> msgMap = new HashMap<>();
                                                msgMap = getParamMap(entity1.getId(), parameterMap);
                                                if (StringUtil.isNotEmpty(title)) {
                                                    StringSubstitutor strSubstitutor = new StringSubstitutor(msgMap, "{", "}");
                                                    title = strSubstitutor.replace(title);
                                                }
                                            }
                                        }
                                        toUser.add(toUserId);
                                        messageService.sentMessage(toUser, title, content, contentMsg, userInfo);
                                        //消息监控写入
//                                        MessageMonitorEntity monitorEntity = new MessageMonitorEntity();
//                                        monitorEntity.setId(RandomUtil.uuId());
//                                        monitorEntity.setReceiveUser(JsonUtil.getObjectToString(toUser));
//                                        monitorEntity.setSendTime(DateUtil.getNowDate());
//                                        monitorEntity.setCreatorTime(DateUtil.getNowDate());
//                                        monitorEntity.setCreatorUserId(userInfo.getUserId());
//                                        createMessageMonitor(monitorEntity, templateConfigEntity, null, null, userInfo, toUser, title);
//                                        messageMonitorService.create(monitorEntity);
                                    }
                                    break;
                                case SmsMessage:
                                    // 发送短信
                                    sendSms(toUserIdsList, null, entity1, parameterMap,contentMsg);
                                    break;
                                case MailMessage:
                                    // 邮件
                                    SendMail(toUserIdsList, userInfo, sendType, entity1, parameterMap,contentMsg);
                                    break;
                                case QyMessage:
                                    // 企业微信
                                    JSONObject jsonObject = SendQyWebChat(toUserIdsList, userInfo, sendType, entity1, parameterMap,contentMsg);
                                    if (!(Boolean) jsonObject.get("code")) {
                                        log.error("发送企业微信消息失败，错误：" + jsonObject.get("error"));
                                    }
                                    break;
                                case DingMessage:
                                    // 钉钉
                                    JSONObject jsonObject1 = SendDingTalk(toUserIdsList, userInfo, sendType, entity1, parameterMap,contentMsg);
                                    if (!(Boolean) jsonObject1.get("code")) {
                                        log.error("发送企业微信消息失败，错误：" + jsonObject1.get("error"));
                                    }
                                    break;
                                case WebHookMessage:
                                    // webhook
                                    SendWebHook(sendType, userInfo, entity1, parameterMap, new HashMap<>());
                                    break;
                                case WechatMessage:
                                    // 微信公众号
                                    SendWXGzhChat(toUserIdsList,userInfo, sendType, entity1,contentMsg, parameterMap);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            else {
                // 消息监控
            }
        }
    }


    public void SendWebHook(String sendType, UserInfo userInfo, SendConfigTemplateEntity entity, Map<String, Object> parameterMap, Map<String, String> contentMsg){
        MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(entity.getTemplateId());
        AccountConfigEntity accountEntity = accountConfigService.getInfo(entity.getAccountConfigId());
        MessageMonitorEntity monitorEntity = new MessageMonitorEntity();
        monitorEntity.setId(RandomUtil.uuId());
        monitorEntity.setSendTime(DateUtil.getNowDate());
        monitorEntity.setCreatorTime(DateUtil.getNowDate());
        monitorEntity.setCreatorUserId(userInfo.getUserId());
        String content = msgTemEntity.getContent();
        //获取消息模板参数
        parameterMap = getParamMap(entity.getId(),parameterMap);
        // 替换参数
        if (StringUtil.isNotEmpty(content)) {
            StringSubstitutor strSubstitutor = new StringSubstitutor(parameterMap, "{", "}");
            content = strSubstitutor.replace(content);
        }
        String title = msgTemEntity.getTitle();
        if (StringUtil.isNotEmpty(title)) {
            StringSubstitutor strSubstitutor = new StringSubstitutor(parameterMap, "{", "}");
            title = strSubstitutor.replace(title);
        }
        title = systemParam(parameterMap, contentMsg, title, userInfo);
        content = systemParam(parameterMap, contentMsg, content, userInfo);
        if(entity != null) {
            if(accountEntity != null) {
                //创建消息监控
//                monitorEntity = createMessageMonitor(monitorEntity,msgTemEntity,accountEntity,content,userInfo,null,title);
//                messageMonitorService.create(monitorEntity);
                switch (accountEntity.getWebhookType()) {
                    case "1":
                        //钉钉
                        if ("1".equals(accountEntity.getApproveType())) {
                            WebHookUtil.sendDDMessage(accountEntity.getWebhookAddress(), content);
                        } else if ("2".equals(accountEntity.getApproveType())) {
                            WebHookUtil.sendDingDing(accountEntity.getWebhookAddress(), accountEntity.getBearer(), content);
                        }
                        break;
                    case "2":
                        if ("1".equals(accountEntity.getApproveType())) {
                            WebHookUtil.callWeChatBot(accountEntity.getWebhookAddress(), content);
                        }
                        break;
                    default:
                        break;
                }
            }else {
//                monitorEntity = createMessageMonitor(monitorEntity,msgTemEntity,null,content,userInfo,null,title);
//                messageMonitorService.create(monitorEntity);
            }
        }else {
//            monitorEntity = createMessageMonitor(monitorEntity,msgTemEntity,null,content,userInfo,null,title);
//            messageMonitorService.create(monitorEntity);
        }
    }

    /**
     * 发送企业微信消息
     *
     * @param toUserIdsList
     * @param userInfo
     * @param sendType
     * @param entity
     * @param parameterMap
     * @return
     */
    public JSONObject SendQyWebChat(List<String> toUserIdsList, UserInfo userInfo, String sendType, SendConfigTemplateEntity entity, Map<String, Object> parameterMap, Map<String, String> contentMsg) {
        // TODO
        return null;
    }

    /**
     * List<String> toUserIdsList, UserInfo userInfo, String sendType, MessageTemplateEntity entity, Map<String, String> parameterMap
     *
     * @param toUserIdsList
     * @param userInfo
     * @param sendType
     * @param entity
     * @param parameterMap
     * @return
     */
    public JSONObject SendDingTalk(List<String> toUserIdsList, UserInfo userInfo, String sendType, SendConfigTemplateEntity entity, Map<String, Object> parameterMap, Map<String, String> contentMsg) {
        // todo
        return null;
    }

    /**
     * 发送邮件
     *
     * @param toUserIdsList
     * @param userInfo
     * @param sendType
     * @param entity
     * @param parameterMap
     * @return
     */
    public void SendMail(List<String> toUserIdsList, UserInfo userInfo, String sendType, SendConfigTemplateEntity entity, Map<String, Object> parameterMap, Map<String, String> contentMsg) {
        // todo
    }

    /**
     * 发送短信
     *
     * @param toUserIdsList
     * @param entity
     * @param parameterMap
     * @return
     */
    public void sendSms(List<String> toUserIdsList, UserInfo userInfo, SendConfigTemplateEntity entity, Map<String, Object> parameterMap,Map<String, String> contentMsg) {
        // todo
    }

    /**
     * 发送微信公众号消息
     *
     * @param toUserIdsList
     * @param userInfo
     * @param sendType
     * @param entity
     * @param parameterMap
     * @return
     */
    public JSONObject SendWXGzhChat(List<String> toUserIdsList, UserInfo userInfo, String sendType,SendConfigTemplateEntity entity, Map<String, String> contentMsg, Map<String, Object> parameterMap) {
        // todo
        return null;
    }

    /**
     * 获取系统配置
     */
    private Map<String, String> getSystemConfig() {
        // 获取系统配置
        // TODO eap
        Map<String, String> objModel = new HashMap<>(16);
        return objModel;
    }

    private Map<String,Object> getParamMap(String templateId, Map<String,Object> paramMap){
        Map<String,Object> map = new HashMap<>();
        for(String key:paramMap.keySet()){
            if(key.contains(templateId)){
                map.put(key.substring(templateId.length()),paramMap.get(key));
            }
        }
        return map;
    }



    private String getShortLink(String pcLink,String userId,String shortLink,String type) {
        if (StringUtil.isNotBlank(shortLink)) {
            ShortLinkEntity entity = shortLinkService.getInfoByLink(shortLink);
            if (entity != null) {
                if (pcLink.equals(entity.getRealPcLink())) {
                    return shortLink;
                } else {
                    shortLink = shortLinkService.shortLink(pcLink+userId+type);
                    return getShortLink(pcLink, userId, shortLink,type);
                }
            } else {
                return shortLink;
            }
        }else {
            shortLink = shortLinkService.shortLink(pcLink+userId+type);
            return getShortLink(pcLink, userId, shortLink,type);
        }
    }

    private void saveShortLink(String pcLink,String appLink,String shortLink,UserInfo userInfo,String userId,String bodyText){
        ShortLinkEntity shortLinkEntity = shortLinkService.getInfoByLink(shortLink);
        if(shortLinkEntity == null) {
            ShortLinkEntity entity = new ShortLinkEntity();
            Map<String, String> sysConfig = getSystemConfig();
            String linkTime = sysConfig.get("linkTime");
            Integer isClick = 0;
            if(StringUtil.isNotBlank(sysConfig.get("isClick")) && !"null".equals(sysConfig.get("isClick"))) {
                isClick  = Integer.parseInt(sysConfig.get("isClick"));
            }
            int unClickNum = 20;
            if(StringUtil.isNotBlank(sysConfig.get("unClickNum")) && !"null".equals(sysConfig.get("unClickNum"))) {
                unClickNum = Integer.parseInt(sysConfig.get("unClickNum"));
            }
            entity.setId(RandomUtil.uuId());
            entity.setRealPcLink(pcLink);
            entity.setRealAppLink(appLink);
            entity.setShortLink(shortLink);
            entity.setBodyText(bodyText);
//            entity.setTenantId(userInfo.getTenantId());
            entity.setUserId(userId);
            entity.setIsUsed(isClick);
            entity.setUnableNum(unClickNum);
            entity.setClickNum(0);
            if (StringUtil.isNotEmpty(linkTime)) {
                Date unableTime = getUnableTime(linkTime);
                entity.setUnableTime(unableTime);
            } else {
                entity.setUnableTime(DateUtil.dateAddHours(DateUtil.getNowDate(), 24));
            }
            entity.setCreatorTime(DateUtil.getNowDate());
            entity.setCreatorUserId(userInfo.getUserId());
            shortLinkService.save(entity);
        }
    }

    private Date getUnableTime(String linkTime){
        Double time = Double.parseDouble(linkTime);
        int second = Double.valueOf(time*60*60).intValue();
        Date unableTime =  DateUtil.dateAddSeconds(DateUtil.getNowDate(),second);
        return unableTime;
    }

    public static boolean isPhone(String phone){
        if (StringUtil.isNotBlank(phone) && !"null".equals(phone)) {
            return Pattern.matches("^1[3-9]\\d{9}$", phone);
        }
        return false;
    }

    public void sendDelegateMsg(SentMessageForm sentMessageForm){
        messageService.sentDelegateMessage(sentMessageForm.getToUserIds(),sentMessageForm.getTitle(),null,sentMessageForm.getContentMsg(),sentMessageForm.getUserInfo());
    }

    public void sendScheduleMessage(SentMessageForm sentMessageForm, String type) {
        UserInfo userInfo = sentMessageForm.getUserInfo();
        String templateId = sentMessageForm.getTemplateId();
        String title = sentMessageForm.getTitle();
        List<String> toUserIds = sentMessageForm.getToUserIds();
        //获取发送配置详情
        SendMessageConfigEntity configEntity = sendMessageConfigService.getInfoByEnCode(templateId);
        if (configEntity != null) {
            templateId = configEntity.getId();
        } else {
            configEntity = sendMessageConfigService.getInfo(templateId);
        }
        List<SendConfigTemplateEntity> list = sendConfigTemplateService.getDetailListByParentId(templateId);
        Map<String, Object> parameterMap = sentMessageForm.getParameterMap();
        Map<String, String> contentMsg = new HashMap<>();
        for(String key : parameterMap.keySet()){
            contentMsg.put(key,String.valueOf(parameterMap.get(key)));
        }
        if (configEntity != null) {
            for (SendConfigTemplateEntity sendConfigTemplateEntity : list) {
                String sendType = sendConfigTemplateEntity.getMessageType();
                switch (sendType) {
                    case "1":
                        MessageTemplateConfigEntity templateConfigEntity = messageTemplateConfigService.getInfo(sendConfigTemplateEntity.getTemplateId());
                        String messageTitle = StringUtil.isNotEmpty(templateConfigEntity.getTitle()) ? templateConfigEntity.getTitle() : "";
                        String content = StringUtil.isNotEmpty(templateConfigEntity.getContent()) ? templateConfigEntity.getContent() : "";
                        StringSubstitutor strSubstitutor = new StringSubstitutor(parameterMap, "{", "}");
                        messageTitle = strSubstitutor.replace(messageTitle);
                        content = strSubstitutor.replace(content);
                        sentMessageForm.setTitle(messageTitle);
                        sentMessageForm.setContent(content);
                        // 站内消息
                        messageService.sendScheduleMessage(sentMessageForm);
                        break;
                    case "2":
                        // 邮件
                        SendMail(toUserIds, userInfo, sendType, sendConfigTemplateEntity, new HashMap<>(), contentMsg);
                        break;
                    case "3":
                        // 发送短信
                        sendSms(toUserIds, null, sendConfigTemplateEntity, parameterMap, new HashMap<>());
                        break;
                    case "4":
                        // 钉钉
                        JSONObject jsonObject1 = SendDingTalk(toUserIds, userInfo, sendType, sendConfigTemplateEntity, new HashMap<>(), contentMsg);
                        if (!(Boolean) jsonObject1.get("code")) {
                            log.error("发送企业微信消息失败，错误：" + jsonObject1.get("error"));
                        }
                        break;
                    case "5":
                        // 企业微信
                        JSONObject jsonObject = SendQyWebChat(toUserIds, userInfo, sendType, sendConfigTemplateEntity, new HashMap<>(), contentMsg);
                        if (!(Boolean) jsonObject.get("code")) {
                            log.error("发送企业微信消息失败，错误：" + jsonObject.get("error"));
                        }
                        break;
                    case "6":
                        // webhook
                        SendWebHook(sendType, userInfo, sendConfigTemplateEntity, new HashMap<>(),contentMsg);
                        break;
                    case "7":
                        // 微信公众号
                        SendWXGzhChat(toUserIds, userInfo, sendType, sendConfigTemplateEntity, new HashMap<>(),parameterMap);
                        break;
                    default:
                        break;
                }
            }
        }
    }



    /**
     * 系统参数替换
     *
     * @param parameterMap
     * @param contentMsg
     * @param title
     * @param userInfo
     * @return
     */
    private String systemParam(Map<String, Object> parameterMap,Map<String, String> contentMsg, String title, UserInfo userInfo) {
        if (parameterMap.isEmpty()) {
            return title = title.replaceAll("\\{@Title}", contentMsg.get("Title"))
                    .replaceAll("\\{@CreatorUserName}", userInfo.getUserName())
                    .replaceAll("\\{@Content}", contentMsg.get("Content"))
                    .replaceAll("\\{@Remark}", contentMsg.get("Remark"))
                    .replaceAll("\\{@StartDate}", contentMsg.get("StartDate"))
                    .replaceAll("\\{@StartTime}", contentMsg.get("StartTime"))
                    .replaceAll("\\{@EndDate}", contentMsg.get("EndDate"))
                    .replaceAll("\\{@FlowLink}", "")
                    .replaceAll("\\{@EndTime}", contentMsg.get("EndTime"));
        }
        return title;
    }
}
