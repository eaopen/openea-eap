package org.openea.eap.extj.message.service.impl;


import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.UserDeviceEntity;
import org.openea.eap.extj.message.mapper.UserDeviceMapper;

import org.openea.eap.extj.message.service.UserDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.openea.eap.extj.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 消息模板（新）
 * */
@Service
public class UserDeviceServiceImpl extends SuperServiceImpl<UserDeviceMapper, UserDeviceEntity> implements UserDeviceService {



    @Autowired
    private UserProvider userProvider;


    @Override
    public UserDeviceEntity getInfoByUserId(String userId){
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getUserId,userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<String> getCidList(String userId){
        List<String> cidList = new ArrayList<>();
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getUserId,userId);
        if(this.list(queryWrapper) != null && this.list(queryWrapper).size()>0) {
            cidList = this.list(queryWrapper).stream().map(t -> t.getClientId()).distinct().collect(Collectors.toList());
        }
        return cidList;
    }

    @Override
    public UserDeviceEntity getInfoByClientId(String clientId){
        QueryWrapper<UserDeviceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserDeviceEntity::getClientId,clientId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(UserDeviceEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, UserDeviceEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(UserDeviceEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}