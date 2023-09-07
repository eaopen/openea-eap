package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.message.entity.*;
import org.openea.eap.extj.message.mapper.MessageMapper;
import org.openea.eap.extj.message.model.NoticePagination;
import org.openea.eap.extj.message.model.message.SentMessageForm;
import org.openea.eap.extj.message.service.*;
import org.openea.eap.extj.message.util.OnlineUserModel;
import org.openea.eap.extj.message.util.OnlineUserProvider;
import org.openea.eap.extj.base.util.SentMessageUtil;
import org.openea.eap.extj.message.util.unipush.UinPush;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 消息实例
 *
 *
 */
@Service
@Slf4j
public class MessageServiceImpl extends SuperServiceImpl<MessageMapper, MessageEntity> implements MessageService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private MessagereceiveService messagereceiveService;
    @Autowired
    private MessageMonitorService messageMonitorService;
    @Autowired
    private UinPush uinPush;
    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SentMessageUtil sentMessageUtil;
    @Autowired
    private SendMessageConfigService sendMessageConfigService;
    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;
    @Autowired
    private MessageTemplateConfigService messageTemplateConfigService;

    @Override
    public List<MessageEntity> getNoticeList(NoticePagination pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        //关键词（消息标题）
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().like(MessageEntity::getTitle, pagination.getKeyword());
        }
        // 类型
        if (pagination.getType() != null && pagination.getType().size() > 0) {
            queryWrapper.lambda().in(MessageEntity::getCategory, pagination.getType());
        } else {
            queryWrapper.lambda().eq(MessageEntity::getType, 1);
        }
        // 状态
        if (pagination.getEnabledMark() != null && pagination.getEnabledMark().size() > 0) {
            queryWrapper.lambda().in(MessageEntity::getEnabledMark, pagination.getEnabledMark());
        } else {
            queryWrapper.lambda().and(t->t.ne(MessageEntity::getEnabledMark, 3).or().isNull(MessageEntity::getEnabledMark));
        }
        //默认排序
        queryWrapper.lambda().orderByAsc(MessageEntity::getEnabledMark).orderByDesc(MessageEntity::getLastModifyTime).orderByDesc(MessageEntity::getCreatorTime);
        queryWrapper.lambda().select(MessageEntity::getId, MessageEntity::getCreatorUser, MessageEntity::getEnabledMark,
                MessageEntity::getLastModifyTime, MessageEntity::getTitle, MessageEntity::getCreatorTime,
                MessageEntity::getLastModifyUserId, MessageEntity::getExpirationTime, MessageEntity::getCategory);
        Page<MessageEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<MessageEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<MessageEntity> getNoticeList() {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getType, 1).eq(MessageEntity::getEnabledMark, 1);
        queryWrapper.lambda().orderByAsc(MessageEntity::getSortCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<MessageEntity> getDashboardNoticeList(List<String> typeList) {
        List<MessageReceiveEntity> list = new ArrayList<>(16);
        QueryWrapper<MessageEntity> queryWrapper1 = new QueryWrapper<>();
        if (!userProvider.get().getIsAdministrator()) {
            // 判断哪些消息是自己接收的
            QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userProvider.get().getUserId());
            list = messagereceiveService.list(queryWrapper);
            List<String> collect = new ArrayList<>(16);
            if (list.size() > 0) {
                collect = list.stream().map(MessageReceiveEntity::getMessageId).collect(Collectors.toList());
            } else {
                collect.add("");
            }
            queryWrapper1.lambda().in(MessageEntity::getId, collect);
        }
        // 得到公告内容
        if (typeList != null && typeList.size() > 0) {
            queryWrapper1.lambda().in(MessageEntity::getCategory, typeList);
        }
        queryWrapper1.lambda().eq(MessageEntity::getType, 1);
        // 需要判断当前时间大于失效时间
        queryWrapper1.lambda().eq(MessageEntity::getEnabledMark, 1);
        queryWrapper1.lambda().and(t-> t.gt(MessageEntity::getExpirationTime, new Date()).or().isNull(MessageEntity::getExpirationTime));
        queryWrapper1.lambda().orderByDesc(MessageEntity::getLastModifyTime);
        Page<MessageEntity> page = new Page<>(1, 20);
        IPage<MessageEntity> iPage = this.page(page, queryWrapper1);
        return iPage.getRecords();
    }

    @Override
    public List<MessageEntity> getMessageList1(Pagination pagination, Integer type, Integer isRead) {
        return getMessageList2(pagination, type, null,isRead);
    }

    public List<MessageEntity> getMessageList2(Pagination pagination, Integer type, String user,Integer isRead) {
        String userId = StringUtil.isEmpty(user) ? userProvider.get().getUserId() : user;
        Map<String, Object> map = new HashMap<>(16);
        map.put("userId", userId);
        //关键词（消息标题）
        String keyword = pagination.getKeyword();
        if (!StringUtil.isEmpty(keyword)) {
            map.put("keyword", "%" + keyword + "%");
        }
        //消息类别
        if (!ObjectUtils.isEmpty(type)) {
            map.put("type", Integer.valueOf(type));
        }
        //消息是否已读
        if (!ObjectUtils.isEmpty(isRead)) {
            map.put("isRead", Integer.valueOf(isRead));
        }
        List<MessageEntity> lists = this.baseMapper.getMessageList(map);
        lists.forEach(t -> {
            if (t.getType() != null && t.getType() == 6) {
                t.setType(1);
            }
            if (StringUtil.isNotEmpty(t.getDefaultTitle())) {
                t.setTitle(t.getDefaultTitle());
            }
        });
        return pagination.setData(PageUtil.getListPage((int) pagination.getCurrentPage(), (int) pagination.getPageSize(), lists), lists.size());
    }

    @Override
    public List<MessageEntity> getMessageList3(Pagination pagination, Integer type, String user,Integer isRead) {
        String userId = StringUtil.isEmpty(user) ? userProvider.get().getUserId() : user;
        Map<String, Object> map = new HashMap<>(16);
        map.put("userId", userId);
        //关键词（消息标题）
        String keyword = pagination.getKeyword();
        if (!StringUtil.isEmpty(keyword)) {
            map.put("keyword", "%" + keyword + "%");
        }
        //消息类别
        if (!ObjectUtils.isEmpty(type)) {
            map.put("type", Integer.valueOf(type));
        }
        //消息是否已读
        if (!ObjectUtils.isEmpty(isRead)) {
            map.put("isRead", Integer.valueOf(isRead));
        }
        List<MessageEntity> lists = this.baseMapper.getMessageList(map);
        return lists;
    }

    @Override
    public List<MessageEntity> getMessageList(Pagination pagination, Integer type, String user) {
        String userId = StringUtil.isEmpty(user) ? userProvider.get().getUserId() : user;
        Map<String, Object> map = new HashMap<>(16);
        map.put("userId", userId);
        //关键词（消息标题）
        String keyword = pagination.getKeyword();
        if (!StringUtil.isEmpty(keyword)) {
            map.put("keyword", "%" + keyword + "%");
        }
        //消息类别
        if (!ObjectUtils.isEmpty(type)) {
            map.put("type", Integer.valueOf(type));
        }
        List<MessageEntity> lists = this.baseMapper.getMessageList(map);
        return pagination.setData(PageUtil.getListPage((int) pagination.getCurrentPage(), (int) pagination.getPageSize(), lists), lists.size());
    }

    @Override
    public List<MessageEntity> getMessageList(Pagination pagination) {
        return this.getMessageList(pagination, null,null);
    }

    @Override
    public MessageEntity getinfo(String id) {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public MessageEntity getInfoDefault(int type) {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageEntity::getType, type).eq(MessageEntity::getEnabledMark, 1);
        if (type == 1) {
            queryWrapper.lambda().orderByDesc(MessageEntity::getCreatorTime);
        } else {
            queryWrapper.lambda().orderByDesc(MessageEntity::getLastModifyTime);
        }
        // 只查询id
        queryWrapper.lambda().select(MessageEntity::getId, MessageEntity::getTitle, MessageEntity::getCreatorTime);
        List<MessageEntity> list = this.page(new Page<>(1, 1, false), queryWrapper).getRecords();
        MessageEntity entity = new MessageEntity();
        if (list.size() > 0) {
            entity = list.get(0);
        }
        return entity;
    }

    @Override
    @DSTransactional
    public void delete(MessageEntity entity) {
        this.removeById(entity.getId());
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getMessageId, entity.getId());
        messagereceiveService.remove(queryWrapper);
    }

    @Override
    public void create(MessageEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setBodyText(XSSEscape.escapeImgOnlyBase64(entity.getBodyText()));
        entity.setType(1);
        entity.setFlowType(1);
        entity.setEnabledMark(0);
        entity.setCreatorUser(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageEntity entity) {
        entity.setId(id);
        entity.setBodyText(XSSEscape.escapeImgOnlyBase64(entity.getBodyText()));
        entity.setCreatorUser(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public MessageReceiveEntity messageRead(String messageId) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getMessageId, messageId);
        MessageReceiveEntity entity = messagereceiveService.getOne(queryWrapper);
        if (entity != null) {
            entity.setIsRead(1);
            entity.setReadCount(entity.getReadCount() == null ? 1 : entity.getReadCount() + 1);
            entity.setReadTime(new Date());
            messagereceiveService.updateById(entity);
        }
        return entity;
    }

    @Override
    @DSTransactional
    public void messageRead(List<String> idList) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getIsRead, 0);
        queryWrapper.lambda().in(MessageReceiveEntity::getMessageId,idList);
        List<MessageReceiveEntity> entitys = messagereceiveService.list(queryWrapper);
        if (entitys.size() > 0) {
            for (MessageReceiveEntity entity : entitys) {
                entity.setIsRead(1);
                entity.setReadCount(entity.getReadCount() == null ? 1 : entity.getReadCount() + 1);
                entity.setReadTime(new Date());
                messagereceiveService.updateById(entity);
            }
        }
    }

    @Override
    @DSTransactional
    public void deleteRecord(List<String> messageIds) {
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        // 通过id删除无需判断接收人
        // .eq(MessageReceiveEntity::getUserId, userId)
        queryWrapper.lambda().in(MessageReceiveEntity::getMessageId, messageIds);
        messagereceiveService.remove(queryWrapper);
        // 删除消息表
        QueryWrapper<MessageEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(MessageEntity::getId, messageIds);
        this.remove(queryWrapper1);
    }

    @Override
    public int getUnreadCount(String userId) {
        QueryWrapper<MessageReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageReceiveEntity::getUserId, userId).eq(MessageReceiveEntity::getIsRead, 0);
        return (int) messagereceiveService.count(queryWrapper);
    }

    @Override
    public int getUnreadNoticeCount(String userId) {
        int result = this.baseMapper.getUnreadNoticeCount(userId);
        return result;
    }

    @Override
    public int getUnreadCount(String userId, Integer type) {
        int result = this.baseMapper.getUnreadCount(userId,type);
        return result;
    }

    @Override
    public int getUnreadMessageCount(String userId) {
        int result = this.baseMapper.getUnreadMessageCount(userId);
        return result;
    }

    @Override
    public int getUnreadSystemMessageCount(String userId) {
        int result = this.baseMapper.getUnreadSystemMessageCount(userId);
        return result;
    }

    @Override
    @DSTransactional
    public void sentNotice(List<String> toUserIds, MessageEntity entity) {
        // 存到redis中的key对象
        UserInfo userInfo = userProvider.get();
        List<String> idList = new ArrayList<>();
        // 修改发送状态
        entity.setEnabledMark(1);
        entity.setFlowType(1);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userInfo.getUserId());
        this.updateById(entity);
        // 存到redis，生成Redis的key
        Callable executeInsert = () -> {
            try {
                executeInsert(toUserIds, idList);
            } catch (Exception e) {
                return false;
            }
            return true;
        };
        ThreadPoolTaskExecutor threadPoolExecutor = SpringContext.getBean(ThreadPoolTaskExecutor.class);
        Future<Boolean> submit = threadPoolExecutor.submit(executeInsert);
        try {
            if (submit.get()) {
                // 执行发送公告操作
                Runnable runnable = () -> executeBatch(idList, entity, userInfo);
                threadPoolExecutor.submit(runnable);
            }
        } catch (Exception e) {
            // 还原公告状态
            entity.setEnabledMark(0);
            entity.setLastModifyTime(null);
            entity.setLastModifyUserId(null);
            this.updateById(entity);
        }
    }

    /**
     * 数据存到redis中
     *
     * @param toUserIds 接受者id
     */
    private void executeInsert(List<String> toUserIds, List<String> idList) throws Exception {
        List<String> key = new ArrayList<>();
        try {
            int frequency = 10000;
            int count = toUserIds.size() / frequency + 1;
            if (toUserIds.size() < 1) return;
            for (int i = 0; i < count; i++) {
                // 生成redis的key
                String cacheKey = RandomUtil.uuId() + toUserIds.get(i);
                // 存到redis
                int endSize = Math.min(((i + 1) * frequency), toUserIds.size());
                redisUtil.insert(cacheKey, toUserIds.subList(i * frequency, endSize));
                key.add(cacheKey);
            }
        } catch (Exception e) {
            key.forEach(k->redisUtil.remove(k));
            key.clear();
            throw new Exception();
        }
        idList.addAll(key);
    }

    /**
     * 执行发送操作
     *
     * @param idList   存到redis中的key
     * @param entity
     * @param userInfo
     */
    private void executeBatch(List<String> idList, MessageEntity entity, UserInfo userInfo) {
        if (idList.size() == 0 || "3".equals(String.valueOf(entity.getRemindCategory()))) {
            return;
        }
        SentMessageForm sentMessageForm = new SentMessageForm();
        List<String> toUserId = new ArrayList<>();
        for (String cacheKey : idList) {
            List<String> cacheValue = (List)redisUtil.get(cacheKey, 0, -1);
            toUserId.addAll(cacheValue);
        }
        sentMessageForm.setToUserIds(toUserId);
        sentMessageForm.setTitle(entity.getTitle());
        sentMessageForm.setContent(entity.getBodyText());
        sentMessageForm.setContentMsg(Collections.EMPTY_MAP);
        sentMessageForm.setUserInfo(userInfo);
        sentMessageForm.setType(1);
        sentMessageForm.setId(entity.getId());

        // 站内信
        if ("1".equals(String.valueOf(entity.getRemindCategory()))) {
            sendScheduleMessage(sentMessageForm);
        } else if ("2".equals(String.valueOf(entity.getRemindCategory()))) {
            SendMessageConfigEntity sendMessageConfigEntity = sendMessageConfigService.getInfo(entity.getSendConfigId());
            if (sendMessageConfigEntity != null) {
                List<SendConfigTemplateEntity> configTemplateEntityList = sendConfigTemplateService.getDetailListByParentId(sendMessageConfigEntity.getId());
                for (SendConfigTemplateEntity sendConfigTemplateEntity : configTemplateEntityList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("Title", entity.getTitle());
                    map.put("Content", entity.getBodyText());
                    map.put("Remark", entity.getExcerpt());
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("@title", entity.getTitle());
                    paramMap.put("@Content", entity.getBodyText());
                    paramMap.put("@Remark", entity.getExcerpt());
                    switch (sendConfigTemplateEntity.getMessageType()) {
                        case "1" :
                            MessageTemplateConfigEntity configEntity = messageTemplateConfigService.getInfo(sendConfigTemplateEntity.getTemplateId());
                            if (configEntity != null) {
                                sentMessageForm.setTitle(configEntity.getTitle());
                            }
                            sendScheduleMessage(sentMessageForm);
                            break;
                        case "2":
                            // 邮件
                            sentMessageUtil.SendMail(toUserId, userInfo, "2", sendConfigTemplateEntity, new HashMap<>(), map);
                            break;
                        case "3":
                            // 发送短信
                            sentMessageUtil.sendSms(toUserId, userInfo, sendConfigTemplateEntity, paramMap, new HashMap<>());
                            break;
                        case "4":
                            // 钉钉
                            JSONObject jsonObject1 = sentMessageUtil.SendDingTalk(toUserId, userInfo, "4", sendConfigTemplateEntity, new HashMap<>(), map);
                            if (!(Boolean) jsonObject1.get("code")) {
                                log.error("发送企业微信消息失败，错误：" + jsonObject1.get("error"));
                            }
                            break;
                        case "5":
                            // 企业微信
                            JSONObject jsonObject = sentMessageUtil.SendQyWebChat(toUserId, userInfo, "5", sendConfigTemplateEntity, new HashMap<>(), map);
                            if (!(Boolean) jsonObject.get("code")) {
                                log.error("发送企业微信消息失败，错误：" + jsonObject.get("error"));
                            }
                            break;
                        case "6":
                            // webhook
                            sentMessageUtil.SendWebHook(null, userInfo, sendConfigTemplateEntity, new HashMap<>(), map);
                            break;
                        case "7":
                            // 微信公众号
                            sentMessageUtil.SendWXGzhChat(toUserId, userInfo, "7", sendConfigTemplateEntity, new HashMap<>(), paramMap);
                            break;
                        default:
                            break;
                    }
                }

            }
        }
    }


    @Override
    public void sentMessage(List<String> toUserIds, String title) {
        this.sentMessage(toUserIds, title, null);
    }

    @Override
    @DSTransactional
    public void sentMessage(List<String> toUserIds, String title, String bodyText) {
        UserInfo userInfo = userProvider.get();
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(2);
        entity.setFlowType(1);
        entity.setCreatorUser(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            messageReceiveEntity.setBodyText(bodyText);
            receiveEntityList.add(messageReceiveEntity);
        }
        this.save(entity);
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
        }
        //消息推送 - PC端
        for (int i = 0; i < toUserIds.size(); i++) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (toUserIds.get(i).equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject map = new JSONObject();
                    map.put("method", "messagePush");
                    map.put("unreadNoticeCount", 1);
                    map.put("messageType", 2);
                    map.put("userId", userInfo.getTenantId());
                    map.put("toUserId", toUserIds);
                    map.put("title", entity.getTitle());
                    map.put("messageDefaultTime", entity.getLastModifyTime() != null ? entity.getLastModifyTime().getTime() : null);
                    OnlineUserProvider.sendMessage(item, map);
                }
            }
        }
    }

    @Override
    @DSTransactional
    public void sentMessage(List<String> toUserIds, String title, String bodyText, Map<String, String> contentMsg, UserInfo userInfo) {
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(2);
        entity.setFlowType(1);
        entity.setEnabledMark(1);
        entity.setCreatorUser(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            String msg = contentMsg.get(item) != null ? contentMsg.get(item) : "{}";
            messageReceiveEntity.setBodyText(msg);
            receiveEntityList.add(messageReceiveEntity);
        }
        this.save(entity);
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
            /*List<String> cidList = userDeviceService.getCidList(messageReceiveEntity.getUserId());
            //个推
            if (cidList != null && cidList.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "2");
                jsonObject.put("id", entity.getId());
                jsonObject.put("title",title);
                String text = JSONObject.toJSONString(jsonObject);
                byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                text = Base64.getEncoder().encodeToString(bytes);
                uinPush.sendUniPush(cidList, title, "你有一条流程消息", "2", text);
            }*/
        }

        //消息推送 - PC端
        for (int i = 0; i < toUserIds.size(); i++) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (toUserIds.get(i).equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject map = new JSONObject();
                    map.put("method", "messagePush");
                    map.put("unreadNoticeCount", 1);
                    map.put("messageType", 2);
                    map.put("userId", userInfo.getTenantId());
                    map.put("toUserId", toUserIds);
                    map.put("title", entity.getTitle());
                    map.put("id",entity.getId());
                    map.put("messageDefaultTime", entity.getLastModifyTime() != null ? entity.getLastModifyTime().getTime() : null);
                    OnlineUserProvider.sendMessage(item, map);
                }
            }
        }
    }

    @Override
    @DSTransactional
    public void sentMessage(List<String> toUserIds, String title, String bodyText, UserInfo userInfo, Integer source, Integer type) {
        sentMessage(toUserIds, title, bodyText, userInfo, source, type, false);
    }

    @Override
    @DSTransactional
    public void sentMessage(List<String> toUserIds, String title, String bodyText, UserInfo userInfo, Integer source, Integer type, boolean testMessage) {
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(source);
        entity.setFlowType(1);
        entity.setCreatorUser(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        entity.setEnabledMark(1);
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            receiveEntityList.add(messageReceiveEntity);
        }
        if (testMessage && source == 1) {
            entity.setEnabledMark(3);
        }
        this.save(entity);
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
        }
        //消息监控写入
        MessageMonitorEntity monitorEntity = new MessageMonitorEntity();
        monitorEntity.setId(RandomUtil.uuId());
        monitorEntity.setTitle(title);
        monitorEntity.setMessageType(String.valueOf(type));
        monitorEntity.setMessageSource(String.valueOf(source));
        monitorEntity.setReceiveUser(JsonUtil.getObjectToString(toUserIds));
        monitorEntity.setSendTime(DateUtil.getNowDate());
        monitorEntity.setCreatorTime(DateUtil.getNowDate());
        monitorEntity.setCreatorUserId(userInfo.getUserId());
        messageMonitorService.create(monitorEntity);
        //消息推送 - PC端
        pushMessage(toUserIds, entity, userInfo, source);
    }

    @Override
    public void sentFlowMessage(List<String> toUserIds, MessageTemplateEntity entity, String content) {
        if (entity != null) {
            // 消息标题
            String title = entity.getTitle();
            this.sentMessage(toUserIds, title, content);
        }
    }

    @Override
    public void logoutWebsocketByToken(String token, String userId){
        if(StringUtil.isNotEmpty(token)) {
            OnlineUserProvider.removeWebSocketByToken(token.split(","));
        }else{
            OnlineUserProvider.removeWebSocketByUser(userId);
        }
    }

    @Override
    public void sentDelegateMessage(List<String> toUserIds, String title, String bodyText, Map<String, String> contentMsg, UserInfo userInfo) {
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        entity.setId(RandomUtil.uuId());
        entity.setType(2);
        entity.setFlowType(2);
        entity.setCreatorUser(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            String msg = contentMsg.get(item) != null ? contentMsg.get(item) : "{}";
            messageReceiveEntity.setBodyText(msg);
            receiveEntityList.add(messageReceiveEntity);
        }
        this.save(entity);
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
        }
        //消息监控写入
        MessageMonitorEntity monitorEntity = new MessageMonitorEntity();
        monitorEntity.setId(RandomUtil.uuId());
        monitorEntity.setTitle(title);
        monitorEntity.setMessageType("1");
        monitorEntity.setMessageSource("1");
        monitorEntity.setReceiveUser(JsonUtil.getObjectToString(toUserIds));
        monitorEntity.setSendTime(DateUtil.getNowDate());
        monitorEntity.setCreatorTime(DateUtil.getNowDate());
        monitorEntity.setCreatorUserId(userInfo.getUserId());
        messageMonitorService.create(monitorEntity);
        //消息推送 - PC端
        pushMessage(toUserIds, entity, userInfo, 2);
    }

    @Override
    public void sendScheduleMessage(SentMessageForm sentMessageForm) {
        List<String> toUserIds = sentMessageForm.getToUserIds();
        String title = sentMessageForm.getTitle();
        String bodyText = JsonUtil.getObjectToString(sentMessageForm.getContentMsg());
        String content = sentMessageForm.getContent();
        UserInfo userInfo = sentMessageForm.getUserInfo();
        Integer type = sentMessageForm.getType();
        MessageEntity entity = new MessageEntity();
        entity.setTitle(title);
        entity.setBodyText(bodyText);
        if (type == null) {
            entity.setType(4);
            entity.setId(RandomUtil.uuId());
        } else {
            entity.setId(sentMessageForm.getId());
            entity.setType(type);
        }
        entity.setCreatorUser(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setLastModifyTime(entity.getCreatorTime());
        entity.setLastModifyUserId(entity.getCreatorUser());
        List<MessageReceiveEntity> receiveEntityList = new ArrayList<>();
        for (String item : toUserIds) {
            MessageReceiveEntity messageReceiveEntity = new MessageReceiveEntity();
            messageReceiveEntity.setId(RandomUtil.uuId());
            messageReceiveEntity.setMessageId(entity.getId());
            messageReceiveEntity.setUserId(item);
            messageReceiveEntity.setIsRead(0);
            messageReceiveEntity.setBodyText(bodyText);
            receiveEntityList.add(messageReceiveEntity);
        }
        //消息监控写入
        MessageMonitorEntity monitorEntity = new MessageMonitorEntity();
        MessageEntity messageEntity = this.getinfo(entity.getId());
        if (!"1".equals(String.valueOf(entity.getType()))) {
            entity.setFlowType(1);
            monitorEntity.setMessageSource("4");
            this.save(entity);
            monitorEntity.setTitle(title);
        } else {
            monitorEntity.setMessageSource("1");
            messageEntity.setDefaultTitle(title.replaceAll("\\{@Title}", messageEntity.getTitle())
                    .replaceAll("\\{@CreatorUserName}", userInfo.getUserName())
                    .replaceAll("\\{@Content}", messageEntity.getBodyText())
                    .replaceAll("\\{@Remark}", messageEntity.getExcerpt()));
            this.updateById(messageEntity);
            monitorEntity.setTitle(messageEntity.getDefaultTitle());
        }
        for (MessageReceiveEntity messageReceiveEntity : receiveEntityList) {
            messagereceiveService.save(messageReceiveEntity);
        }
        monitorEntity.setId(RandomUtil.uuId());
        monitorEntity.setMessageType("1");
        monitorEntity.setReceiveUser(JsonUtil.getObjectToString(toUserIds));
        monitorEntity.setSendTime(DateUtil.getNowDate());
        monitorEntity.setCreatorTime(DateUtil.getNowDate());
        monitorEntity.setCreatorUserId(userInfo.getUserId());
        monitorEntity.setContent(content);
        messageMonitorService.create(monitorEntity);
        //消息推送 - PC端
        pushMessage(toUserIds, entity, userInfo, 4);
    }

    @Override
    public Boolean updateEnabledMark() {
        QueryWrapper<MessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(t->t.eq(MessageEntity::getEnabledMark, 1).lt(MessageEntity::getExpirationTime, new Date()));
        List<MessageEntity> list = this.list(queryWrapper);
        list.forEach(t -> {
            t.setEnabledMark(2);
            this.updateById(t);
        });
        return true;
    }

    /**
     * 工作流消息发送
     *
     * @param toUserIds
     * @param entity
     * @param userInfo
     */
    private static void pushMessage(List<String> toUserIds, MessageEntity entity, UserInfo userInfo,int messageType){
        for (int i = 0; i < toUserIds.size(); i++) {
            for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList()) {
                if (toUserIds.get(i).equals(item.getUserId()) && userInfo.getTenantId().equals(item.getTenantId())) {
                    JSONObject map = new JSONObject();
                    map.put("method", "messagePush");
                    map.put("unreadNoticeCount", 1);
                    map.put("messageType", messageType);
                    map.put("userId", userInfo.getTenantId());
                    map.put("toUserId", toUserIds);
                    map.put("title", entity.getTitle());
                    map.put("id",entity.getId());
                    map.put("messageDefaultTime", entity.getLastModifyTime() != null ? entity.getLastModifyTime().getTime() : null);
                    OnlineUserProvider.sendMessage(item, map);
                }
            }
        }
    }

}
