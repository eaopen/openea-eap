package org.openea.eap.extj.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.SendConfigRecordEntity;
import org.openea.eap.extj.message.mapper.SendConfigRecordMapper;
import org.openea.eap.extj.message.model.sendconfigrecord.*;
import org.openea.eap.extj.message.service.SendConfigRecordService;
import org.openea.eap.extj.permission.service.AuthorizeService;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.openea.eap.extj.util.*;

/**
 * 发送配置使用记录
 * */
@Service
public class SendConfigRecordServiceImpl extends SuperServiceImpl<SendConfigRecordMapper, SendConfigRecordEntity>
        implements SendConfigRecordService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<SendConfigRecordEntity> getList(SendConfigRecordPagination sendConfigRecordPagination) {
        return getTypeList(sendConfigRecordPagination, sendConfigRecordPagination.getDataType());
    }

    @Override
    public List<SendConfigRecordEntity> getTypeList(SendConfigRecordPagination sendConfigRecordPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int sendConfigRecordNum = 0;
        QueryWrapper<SendConfigRecordEntity> sendConfigRecordQueryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(sendConfigRecordPagination.getMessageSource())) {
            sendConfigRecordNum++;
            sendConfigRecordQueryWrapper.lambda().like(SendConfigRecordEntity::getMessageSource, sendConfigRecordPagination.getMessageSource());
        }

        //排序
        if (StringUtil.isEmpty(sendConfigRecordPagination.getSidx())) {
            sendConfigRecordQueryWrapper.lambda().orderByDesc(SendConfigRecordEntity::getCreatorTime);
        } else {
            try {
                String sidx = sendConfigRecordPagination.getSidx();
                SendConfigRecordEntity sendConfigRecordEntity = new SendConfigRecordEntity();
                Field declaredField = sendConfigRecordEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                sendConfigRecordQueryWrapper = "asc".equals(sendConfigRecordPagination.getSort().toLowerCase()) ? sendConfigRecordQueryWrapper.orderByAsc(value) : sendConfigRecordQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if ("0".equals(dataType)) {
            if (total > 0  || total == 0) {
                Page<SendConfigRecordEntity> page = new Page<>(sendConfigRecordPagination.getCurrentPage(), sendConfigRecordPagination.getPageSize());
                IPage<SendConfigRecordEntity> userIPage = this.page(page, sendConfigRecordQueryWrapper);
                return sendConfigRecordPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<SendConfigRecordEntity> list = new ArrayList();
                return sendConfigRecordPagination.setData(list, list.size());
            }
        } else {
            return this.list(sendConfigRecordQueryWrapper);
        }
    }


    @Override
    public SendConfigRecordEntity getInfo(String id) {
        QueryWrapper<SendConfigRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigRecordEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public SendConfigRecordEntity getRecord(String sendConfigId, String usedId){
        QueryWrapper<SendConfigRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigRecordEntity::getSendConfigId, sendConfigId);
        queryWrapper.lambda().eq(SendConfigRecordEntity::getUsedId,usedId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(SendConfigRecordEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, SendConfigRecordEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(SendConfigRecordEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(SendConfigRecordForm form, int i) {
        int total = 0;
        if (total > i) {
            return true;
        }
        return false;
    }


}