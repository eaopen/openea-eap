package org.openea.eap.extj.extend.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.base.MailAccount;
import org.openea.eap.extj.base.PaginationTime;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.extend.entity.EmailConfigEntity;
import org.openea.eap.extj.extend.entity.EmailReceiveEntity;
import org.openea.eap.extj.extend.entity.EmailSendEntity;
import org.openea.eap.extj.extend.mapper.EmailReceiveMapper;
import org.openea.eap.extj.extend.model.MailFile;
import org.openea.eap.extj.extend.model.MailModel;
import org.openea.eap.extj.extend.service.EmailConfigService;
import org.openea.eap.extj.extend.service.EmailReceiveService;
import org.openea.eap.extj.extend.service.EmailSendService;
import org.openea.eap.extj.extend.util.Pop3Util;
import org.openea.eap.extj.extend.util.SmtpUtil;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收取邮件
 *
 * 待合并到eap中
 * org.openea.eap.module.system.service.mail
 *
 */
@Slf4j
@Service
public class EmailReceiveServiceImpl extends SuperServiceImpl<EmailReceiveMapper, EmailReceiveEntity> implements EmailReceiveService {

    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private EmailConfigService emailConfigService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private Pop3Util pop3Util;

    @Override
    public List<EmailReceiveEntity> getReceiveList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailReceiveEntity::getFdate, startTimes).le(EmailReceiveEntity::getFdate, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailReceiveEntity::getSender, word)
                            .or().like(EmailReceiveEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailReceiveEntity::getFdate);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailReceiveEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailReceiveEntity> userIpage = this.page(page, queryWrapper);
        return paginationTime.setData(userIpage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailReceiveEntity> getReceiveList() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId).eq(EmailReceiveEntity::getIsRead,0).orderByDesc(EmailReceiveEntity::getCreatorTime);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<EmailReceiveEntity> getDashboardReceiveList() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId).eq(EmailReceiveEntity::getIsRead,0).orderByDesc(EmailReceiveEntity::getCreatorTime);
        Page<EmailReceiveEntity> page = new Page<>(1, 20);
        IPage<EmailReceiveEntity> iPage = this.page(page, queryWrapper);
        return iPage.getRecords();
    }

    @Override
    public List<EmailReceiveEntity> getStarredList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId).eq(EmailReceiveEntity::getStarred, 1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailReceiveEntity::getCreatorTime, startTimes).le(EmailReceiveEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailReceiveEntity::getSender, word)
                            .or().like(EmailReceiveEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailReceiveEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailReceiveEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailReceiveEntity> userIpage = this.page(page, queryWrapper);
        return paginationTime.setData(userIpage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailSendEntity> getDraftList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailSendEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailSendEntity::getCreatorUserId, userId).eq(EmailSendEntity::getState, -1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getEndTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailSendEntity::getCreatorTime, startTimes).le(EmailSendEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailSendEntity::getSender, word)
                            .or().like(EmailSendEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailSendEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailSendEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailSendEntity> userIpage = emailSendService.page(page, queryWrapper);
        return paginationTime.setData(userIpage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailSendEntity> getSentList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailSendEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailSendEntity::getCreatorUserId, userId).ne(EmailSendEntity::getState, -1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailSendEntity::getCreatorTime, startTimes).le(EmailSendEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? String.valueOf(paginationTime.getKeyword()) : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailSendEntity::getSender, word)
                            .or().like(EmailSendEntity::getSubject, word)
            );
        }
        //排序
        String sort = paginationTime.getSort() != null ? paginationTime.getSort() : null;
        if (!StringUtils.isEmpty(sort)) {
            queryWrapper.lambda().orderByDesc(EmailSendEntity::getCreatorTime);
        }
        Page<EmailSendEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailSendEntity> userIpage = emailSendService.page(page, queryWrapper);
        return paginationTime.setData(userIpage.getRecords(), page.getTotal());
    }

    @Override
    public EmailConfigEntity getConfigInfo() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailConfigEntity::getCreatorUserId, userId);
        return emailConfigService.getOne(queryWrapper);
    }

    @Override
    public EmailConfigEntity getConfigInfo(String userId) {
        QueryWrapper<EmailConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailConfigEntity::getCreatorUserId, userId);
        return emailConfigService.getOne(queryWrapper);
    }

    @Override
    public Object getInfo(String id) {
        EmailReceiveEntity receiveInfo = this.getById(id);
        Object object;
        if (receiveInfo != null) {
            //解析内容
            receiveInfo.setBodyText(receiveInfo.getBodyText());
            //更新已读
            receiveInfo.setIsRead(1);
            receiveInfo.setLastModifyTime(new Date());
            receiveInfo.setLastModifyUserId(userProvider.get().getUserId());
            this.updateById(receiveInfo);
            object = receiveInfo;
        } else {
            EmailSendEntity sendInfo = emailSendService.getById(id);
            object = sendInfo;
        }
        return object;
    }

    @Override
    public boolean delete(String id) {
        Object object = getInfo(id);
        if (object!=null&&object instanceof EmailReceiveEntity) {
            //删除邮件
            EmailConfigEntity mailConfig = getConfigInfo();
            EmailReceiveEntity mailReceiveEntity = (EmailReceiveEntity) object;
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(mailConfig.getAccount());
            mailAccount.setPassword(mailConfig.getPassword());
            mailAccount.setPop3Port(mailConfig.getPop3Port());
            mailAccount.setPop3Host(mailConfig.getPop3Host());
            pop3Util.deleteMessage(mailAccount, mailReceiveEntity.getMId());
            this.removeById(mailReceiveEntity.getId());
            return true;
        } else if (object!=null){
            //删除数据
            EmailSendEntity entity = (EmailSendEntity) object;
            emailSendService.removeById(entity.getId());
            return true;
        }
        return false;
    }

    @Override
    @DSTransactional
    public void saveDraft(EmailSendEntity entity) {
        entity.setState(-1);
        if (entity.getId() != null) {
            entity.setLastModifyTime(new Date());
            entity.setLastModifyUserId(userProvider.get().getUserId());
            emailSendService.updateById(entity);
        } else {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            emailSendService.save(entity);
        }
    }

    @Override
    public boolean receiveRead(String id, int isRead) {
        EmailReceiveEntity entity = (EmailReceiveEntity) getInfo(id);
        if(entity!=null){
            entity.setIsRead(isRead);
            return this.updateById(entity);
        }
        return false;
    }

    @Override
    public boolean receiveStarred(String id, int isStarred) {
        EmailReceiveEntity entity = (EmailReceiveEntity) getInfo(id);
        if(entity!=null){
            entity.setStarred(isStarred);
            return this.updateById(entity);
        }
        return false;
    }



    @Override
    public void saveConfig(EmailConfigEntity configEntity) throws DataException {
        EmailConfigEntity emailConfigEntity= getConfigInfo(userProvider.get().getUserId());
        if (emailConfigEntity == null) {
            configEntity.setId(RandomUtil.uuId());
            configEntity.setCreatorTime(new Date());
            configEntity.setCreatorUserId(userProvider.get().getUserId());
            emailConfigService.save(configEntity);
        } else if(userProvider.get().getUserId()!=null){
            configEntity.setId(emailConfigEntity.getId());
            emailConfigService.updateById(configEntity);
        }else{
            throw new DataException("保存失败，请重新登陆");
        }
    }

    @Override
    public int saveSent(EmailSendEntity entity, EmailConfigEntity mailConfig) {
        int flag = 1;
        //拷贝文件,注意：从临时文件夹拷贝到邮件文件夹
        List<MailFile> attachmentList = JsonUtil.getJsonToList(entity.getAttachment(), MailFile.class);
        String temporaryFile = configValueUtil.getTemporaryFilePath();
        String mailFilePath = configValueUtil.getEmailFilePath();
        for (MailFile mailFile : attachmentList) {
            byte[] bytes = FileUploadUtils.downloadFileByte(configValueUtil.getTemporaryFilePath(), mailFile.getFileName(), false);
            FileUploadUtils.uploadFile(bytes, configValueUtil.getEmailFilePath(), mailFile.getFileName());
        }
        try {
            //写入数据
            //发送邮件
            //邮件发送信息
            MailModel mailModel = new MailModel();
            mailModel.setFrom(entity.getSender());
            mailModel.setRecipient(entity.getRecipient());
            mailModel.setCc(entity.getCc());
            mailModel.setBcc(entity.getBcc());
            mailModel.setSubject(entity.getSubject());
            mailModel.setBodyText(entity.getBodyText());
            mailModel.setAttachment(attachmentList);
            mailModel.setFromName(mailConfig.getSenderName());
            //账号验证信息
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(mailConfig.getAccount());
            mailAccount.setPassword(mailConfig.getPassword());
            mailAccount.setPop3Host(mailConfig.getPop3Host());
            mailAccount.setPop3Port(mailConfig.getPop3Port());
            mailAccount.setSmtpHost(mailConfig.getSmtpHost());
            mailAccount.setSmtpPort(mailConfig.getSmtpPort());
            mailAccount.setSsl(mailConfig.getEmailSsl() == 1 ? true : false);
            mailAccount.setAccountName(mailConfig.getSenderName());
            SmtpUtil smtpUtil = new SmtpUtil(mailAccount);
            smtpUtil.sendMail(null, mailFilePath, mailModel);
            flag = 0;
            //插入数据库
            if (entity.getId() != null) {
                entity.setState(1);
                emailSendService.updateById(entity);
            } else {
                entity.setId(RandomUtil.uuId());
                entity.setCreatorUserId(userProvider.get().getUserId());
                if (mailConfig.getAccount() != null) {
                    entity.setSender(mailConfig.getAccount());
                }
                entity.setState(1);
                emailSendService.save(entity);
            }
        } catch (Exception e) {
            for (MailFile mailFile : attachmentList) {
                FileUtil.deleteFile(mailFilePath + mailFile.getFileId());
            }
            log.error(e.getMessage());
        }
        return flag;
    }

    @Override
    @DSTransactional
    public int receive(EmailConfigEntity mailConfig) {
        //账号验证信息
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAccount(mailConfig.getAccount());
        mailAccount.setPassword(mailConfig.getPassword());
        mailAccount.setPop3Host(mailConfig.getPop3Host());
        mailAccount.setPop3Port(mailConfig.getPop3Port());
        mailAccount.setSmtpHost(mailConfig.getSmtpHost());
        mailAccount.setSmtpPort(mailConfig.getSmtpPort());
        if ("1".equals(mailConfig.getEmailSsl().toString())) {
            mailAccount.setSsl(true);
        } else {
            mailAccount.setSsl(false);
        }
        Map<String, Object> map = pop3Util.popMail(mailAccount);
        int receiveCount = 0;
        if (map.get("receiveCount") != null) {
            receiveCount = (int) map.get("receiveCount");
        }
        List<EmailReceiveEntity> mailList = new ArrayList<>();
        if (map.get("mailList") != null) {
            mailList = (List<EmailReceiveEntity>) map.get("mailList");
        }
        if (mailList.size() > 0) {
            List<String> mids = mailList.stream().map(u -> u.getMId()).collect(Collectors.toList());
            //查询数据库状态
            QueryWrapper<EmailReceiveEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(EmailReceiveEntity::getMId, mids);
            List<EmailReceiveEntity> emails = this.list(wrapper);
            this.remove(wrapper);
            //邮件赋值状态
            for (int i = 0; i < mailList.size(); i++) {
                EmailReceiveEntity entity = mailList.get(i);
                entity.setCreatorUserId(userProvider.get().getUserId());
                //通过数据库进行赋值，没有就默认0
                int stat = emails.stream().anyMatch(m -> m.getMId().equals(entity.getMId())) ? emails.stream().filter(m -> m.getMId().equals(entity.getMId())).findFirst().get().getIsRead() : 0;
                long count = emails.stream().filter(m -> m.getMId().equals(entity.getMId())).count();
                entity.setIsRead(stat);
                if (count != 0) {
                    receiveCount--;
                }
                this.save(entity);
            }
        }
        return receiveCount;
    }
}
