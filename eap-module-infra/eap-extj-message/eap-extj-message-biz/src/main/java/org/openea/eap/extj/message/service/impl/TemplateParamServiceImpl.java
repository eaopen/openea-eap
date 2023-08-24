package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.TemplateParamEntity;
import org.openea.eap.extj.message.mapper.TemplateParamMapper;
import org.openea.eap.extj.message.model.messagetemplateconfig.MessageTemplateConfigPagination;


import org.openea.eap.extj.message.service.TemplateParamService;
import org.openea.eap.extj.permission.service.AuthorizeService;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.openea.eap.extj.util.*;

import java.util.*;

/**
 * 消息模板（新）
 * */
@Service
public class TemplateParamServiceImpl extends SuperServiceImpl<TemplateParamMapper, TemplateParamEntity> implements TemplateParamService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<TemplateParamEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<TemplateParamEntity> templateParamQueryWrapper) {
        boolean pcPermission = false;
        boolean appPermission = false;
        boolean isPc = ServletUtil.getHeader("eap-origin").equals("pc");
        if (isPc) {
        }

        return templateParamQueryWrapper;
    }

    @Override
    public TemplateParamEntity getInfo(String id) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<TemplateParamEntity> getDetailListByParentId(String id) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<TemplateParamEntity> getParamList(String id,List<String> params) {
        QueryWrapper<TemplateParamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TemplateParamEntity::getTemplateId, id);
        queryWrapper.lambda().in(TemplateParamEntity::getField,params);
        return this.list(queryWrapper);
    }

}