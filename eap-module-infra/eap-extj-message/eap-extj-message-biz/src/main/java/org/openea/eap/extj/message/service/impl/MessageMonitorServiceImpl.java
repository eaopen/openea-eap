package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.openea.eap.extj.message.entity.MessageMonitorEntity;
import org.openea.eap.extj.message.mapper.MessageMonitorMapper;
import org.openea.eap.extj.message.model.messagemonitor.*;
import org.openea.eap.extj.message.service.MessageMonitorService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.permission.service.AuthorizeService;
import java.lang.reflect.Field;
import com.baomidou.mybatisplus.annotation.TableField;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.*;

/**
 * 消息监控
 * */
@Service
public class MessageMonitorServiceImpl extends SuperServiceImpl<MessageMonitorMapper, MessageMonitorEntity> implements MessageMonitorService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<MessageMonitorEntity> getList(MessageMonitorPagination messageMonitorPagination) {
        return getTypeList(messageMonitorPagination, messageMonitorPagination.getDataType());
    }

    @Override
    public List<MessageMonitorEntity> getTypeList(MessageMonitorPagination messageMonitorPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int messageMonitorNum = 0;
        QueryWrapper<MessageMonitorEntity> messageMonitorQueryWrapper = new QueryWrapper<>();
        //关键字
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getKeyword())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().and(t -> t.like(MessageMonitorEntity::getTitle, messageMonitorPagination.getKeyword()));
        }
        //消息类型
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getMessageType())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().eq(MessageMonitorEntity::getMessageType, messageMonitorPagination.getMessageType());
        }
        //发送时间
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getStartTime()) && ObjectUtil.isNotEmpty(messageMonitorPagination.getEndTime())) {
            messageMonitorNum++;
            Long fir = Long.valueOf(String.valueOf(messageMonitorPagination.getStartTime()));
            Long sec = Long.valueOf(String.valueOf(messageMonitorPagination.getEndTime()));

            messageMonitorQueryWrapper.lambda().ge(MessageMonitorEntity::getSendTime, new Date(fir))
                    .le(MessageMonitorEntity::getSendTime, DateUtil.stringToDate(DateUtil.daFormatYmd(sec) + " 23:59:59"));

        }
        //消息来源
        if (ObjectUtil.isNotEmpty(messageMonitorPagination.getMessageSource())) {
            messageMonitorNum++;
            messageMonitorQueryWrapper.lambda().eq(MessageMonitorEntity::getMessageSource, messageMonitorPagination.getMessageSource());
        }
        //排序
        if (StringUtil.isEmpty(messageMonitorPagination.getSidx())) {
            messageMonitorQueryWrapper.lambda().orderByDesc(MessageMonitorEntity::getSendTime);
        } else {
            try {
                String sidx = messageMonitorPagination.getSidx();
                MessageMonitorEntity messageMonitorEntity = new MessageMonitorEntity();
                Field declaredField = messageMonitorEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                messageMonitorQueryWrapper = "asc".equals(messageMonitorPagination.getSort().toLowerCase()) ? messageMonitorQueryWrapper.orderByAsc(value) : messageMonitorQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0 || total == 0) {
                Page<MessageMonitorEntity> page = new Page<>(messageMonitorPagination.getCurrentPage(), messageMonitorPagination.getPageSize());
                IPage<MessageMonitorEntity> userIPage = this.page(page, messageMonitorQueryWrapper);
                return messageMonitorPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<MessageMonitorEntity> list = new ArrayList();
                return messageMonitorPagination.setData(list, list.size());
            }
        } else {
            return this.list(messageMonitorQueryWrapper);
        }
    }


    @Override
    public MessageMonitorEntity getInfo(String id) {
        QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageMonitorEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(MessageMonitorEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageMonitorEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(MessageMonitorEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(MessageMonitorForm form, int i) {
        int total = 0;
        if (total > 0) {
            return true;
        }
        return false;
    }
    @Override
    public void emptyMonitor(){
        QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
        this.remove(queryWrapper);
    }


    @Override
    @DSTransactional
    public boolean delete(String[] ids) {
        if (ids.length > 0) {
            QueryWrapper<MessageMonitorEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(MessageMonitorEntity::getId, ids);
            return this.remove(queryWrapper);
        }
        return false;
    }
    /**
     * 用户id转名称(多选)
     *
     * @param ids
     * @return
     */
    @Override
    public String userSelectValues(String ids) {
        if (StringUtil.isEmpty(ids)) {
            return ids;
        }
        if (ids.contains("[")){
            List<String> nameList = new ArrayList<>();
            List<String> jsonToList = JsonUtil.getJsonToList(ids, String.class);
            for (String userId : jsonToList){
                UserEntity info = userService.getInfo(userId);
                nameList.add(Objects.nonNull(info) ? info.getRealName()+ "/" + info.getAccount() : userId);
            }
            return String.join(";", nameList);
        }else {
            List<String> userInfoList = new ArrayList<>();
            String[] idList = ids.split(",");
            if (idList.length > 0) {
                for (String id : idList) {
                    UserEntity userEntity = userService.getInfo(id);
                    if (ObjectUtil.isNotEmpty(userEntity)) {
                        String info = userEntity.getRealName() + "/" + userEntity.getAccount();
                        userInfoList.add(info);
                    }
                }
            }
            return String.join("-", userInfoList);
        }
    }


}