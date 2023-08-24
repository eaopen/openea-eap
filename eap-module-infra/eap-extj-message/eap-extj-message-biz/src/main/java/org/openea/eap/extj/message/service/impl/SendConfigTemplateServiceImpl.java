package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.SendConfigTemplateEntity;
import org.openea.eap.extj.message.mapper.SendConfigTemplateMapper;
import org.openea.eap.extj.message.model.sendmessageconfig.SendMessageConfigPagination;
import org.openea.eap.extj.message.service.SendConfigTemplateService;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.openea.eap.extj.util.*;

import java.util.List;

/**
 * 消息发送配置
 * */
@Service
public class SendConfigTemplateServiceImpl extends SuperServiceImpl<SendConfigTemplateMapper, SendConfigTemplateEntity> implements SendConfigTemplateService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<SendConfigTemplateEntity> getChild(SendMessageConfigPagination pagination, QueryWrapper<SendConfigTemplateEntity> sendConfigTemplateQueryWrapper) {
        boolean pcPermission = false;
        boolean appPermission = false;
        boolean isPc = ServletUtil.getHeader("eap-origin").equals("pc");
        if (isPc) {
        }

        return sendConfigTemplateQueryWrapper;
    }

    @Override
    public SendConfigTemplateEntity getInfo(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SendConfigTemplateEntity> getDetailListByParentId(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<SendConfigTemplateEntity> getConfigTemplateListByConfigId(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getEnabledMark, 1);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isUsedAccount(String accountId) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getAccountConfigId, accountId);
        if (this.list(queryWrapper) != null && this.list(queryWrapper).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isUsedTemplate(String templateId) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getTemplateId, templateId);
        if (this.list(queryWrapper) != null && this.list(queryWrapper).size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}