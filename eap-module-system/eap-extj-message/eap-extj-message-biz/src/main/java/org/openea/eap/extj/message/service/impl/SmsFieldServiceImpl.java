package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.SmsFieldEntity;
import org.openea.eap.extj.message.mapper.SmsFieldMapper;
import org.openea.eap.extj.message.model.messagetemplateconfig.MessageTemplateConfigPagination;


import org.openea.eap.extj.message.service.SmsFieldService;
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
public class SmsFieldServiceImpl extends SuperServiceImpl<SmsFieldMapper, SmsFieldEntity> implements SmsFieldService {



    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public QueryWrapper<SmsFieldEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<SmsFieldEntity> smsFieldQueryWrapper) {
        boolean pcPermission = false;
        boolean appPermission = false;
        boolean isPc = ServletUtil.getHeader("eap-origin").equals("pc");
        if (isPc) {
        }

        return smsFieldQueryWrapper;
    }
    @Override
    public SmsFieldEntity getInfo(String id) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<SmsFieldEntity> getDetailListByParentId(String id) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<SmsFieldEntity> getParamList(String id,List<String> params) {
        QueryWrapper<SmsFieldEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SmsFieldEntity::getTemplateId, id);
        queryWrapper.lambda().in(SmsFieldEntity::getField,params);
        return this.list(queryWrapper);
    }

    @Override
    public Map<String,Object> getParamMap(String templateId,Map<String,Object> map) {
        Map<String,Object> paramMap = new HashMap<>();
        List<SmsFieldEntity> list = this.getDetailListByParentId(templateId);
        if (list != null && list.size() > 0) {
            for (SmsFieldEntity entity : list) {
                if (map.containsKey(entity.getField())) {
                    for (String key : map.keySet()) {
                        if (key.equals(entity.getField())) {
                            paramMap.put(entity.getSmsField(), map.get(key));
                            if(StringUtil.isNotEmpty(String.valueOf(entity.getIsTitle())) &&!"null".equals(String.valueOf(entity.getIsTitle())) &&  entity.getIsTitle()==1){
                                paramMap.put("title",map.get(key));
                            }
                        }
                    }
                    if(entity.getField().equals("@FlowLink")){
                        paramMap.put(entity.getSmsField(),"@FlowLink");
                    }
                }
            }
        }
        return paramMap;
    }
}
