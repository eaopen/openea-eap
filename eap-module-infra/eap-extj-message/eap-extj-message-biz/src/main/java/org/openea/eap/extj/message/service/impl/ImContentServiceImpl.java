package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.PageModel;
import org.openea.eap.extj.message.entity.ImContentEntity;
import org.openea.eap.extj.message.entity.ImReplyEntity;
import org.openea.eap.extj.message.mapper.ImContentMapper;
import org.openea.eap.extj.message.model.ImReplySavaModel;
import org.openea.eap.extj.message.model.ImUnreadNumModel;
import org.openea.eap.extj.message.service.ImContentService;
import org.openea.eap.extj.message.service.ImReplyService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 聊天内容
 *
 *
 */
@Service
public class ImContentServiceImpl extends SuperServiceImpl<ImContentMapper, ImContentEntity> implements ImContentService {

    @Autowired
    private ImReplyService imReplyService;
    @Autowired
    private UserProvider userProvider;
    @Override
    public List<ImContentEntity> getMessageList(String sendUserId, String receiveUserId, PageModel pageModel) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        //发件人、收件人
        if (!StringUtil.isEmpty(sendUserId) && !StringUtil.isEmpty(receiveUserId)) {

            queryWrapper.lambda().and(wrapper -> {
                wrapper.eq(ImContentEntity::getSendUserId, sendUserId);
                wrapper.eq(ImContentEntity::getReceiveUserId, receiveUserId);
                wrapper.or().eq(ImContentEntity::getSendUserId, receiveUserId);
                wrapper.eq(ImContentEntity::getReceiveUserId, sendUserId);
            });
            queryWrapper.lambda().and(wrapper -> {
                wrapper.isNull(ImContentEntity::getSendDeleteMark);
                wrapper.or(). ne(ImContentEntity::getSendDeleteMark,receiveUserId);
//                wrapper.ne(ImContentEntity::getDeleteMark, 1);
            });

        }
        //关键字查询
        if (pageModel != null && pageModel.getKeyword() != null) {
            queryWrapper.lambda().like(ImContentEntity::getContent, pageModel.getKeyword());
            //排序
            pageModel.setSidx("F_SendTime");
        }

        if (StringUtil.isEmpty(pageModel.getSidx())) {
            queryWrapper.lambda().orderByDesc(ImContentEntity::getSendTime);
        } else {
            queryWrapper = "asc".equals(pageModel.getSord().toLowerCase()) ? queryWrapper.orderByAsc(pageModel.getSidx()) : queryWrapper.orderByDesc(pageModel.getSidx());
        }
        Page<ImContentEntity> page = new Page<>(pageModel.getPage(), pageModel.getRows());
        IPage<ImContentEntity> iPage = this.page(page, queryWrapper);
        return pageModel.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<ImUnreadNumModel> getUnreadList(String receiveUserId) {
        List<ImUnreadNumModel> list = this.baseMapper.getUnreadList(receiveUserId);
        List<ImUnreadNumModel> list1 = this.baseMapper.getUnreadLists(receiveUserId);
        for (ImUnreadNumModel item : list) {
            Optional<ImUnreadNumModel> first = list1.stream().filter(q -> q.getSendUserId().equals(item.getSendUserId())).findFirst();
            if(first.isPresent()){
                ImUnreadNumModel defaultItem = first.get();
                item.setDefaultMessage(defaultItem.getDefaultMessage());
                item.setDefaultMessageType(defaultItem.getDefaultMessageType());
                item.setDefaultMessageTime(defaultItem.getDefaultMessageTime());
            }
        }
        return list;
    }

    @Override
    public int getUnreadCount(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, sendUserId).eq(ImContentEntity::getReceiveUserId, receiveUserId).eq(ImContentEntity::getState, 0);
        return (int) this.count(queryWrapper);
    }

    @Override
    @DSTransactional
    public void sendMessage(String sendUserId, String receiveUserId, String message, String messageType) {
        ImContentEntity entity = new ImContentEntity();
        entity.setId(RandomUtil.uuId());
        entity.setSendUserId(sendUserId);
        entity.setSendTime(new Date());
        entity.setReceiveUserId(receiveUserId);
        entity.setState(0);
        entity.setContent(message);
        entity.setContentType(messageType);
        this.save(entity);

        //写入到会话表中
        ImReplySavaModel imReplySavaModel = new ImReplySavaModel(sendUserId, receiveUserId, entity.getSendTime());
        ImReplyEntity imReplyEntity = JsonUtil.getJsonToBean(imReplySavaModel, ImReplyEntity.class);
        imReplyService.savaImReply(imReplyEntity);
    }

    @Override
    public void readMessage(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, sendUserId);
        queryWrapper.lambda().eq(ImContentEntity::getReceiveUserId, receiveUserId);
        queryWrapper.lambda().eq(ImContentEntity::getState, 0);
        List<ImContentEntity> list = this.list(queryWrapper);
        for (ImContentEntity entity : list) {
            entity.setState(1);
            entity.setReceiveTime(new Date());
            this.updateById(entity);
        }
    }

    //@Override
    public ImContentEntity getList(String userId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImContentEntity::getSendUserId, userId)
                .and(t -> t.eq(ImContentEntity::getReceiveUserId, receiveUserId)).orderByDesc(ImContentEntity::getReceiveTime);
        List<ImContentEntity> list = this.list(queryWrapper);
        return list.size() > 0 ? list.get(0) : null;
    }


    @Override
    public boolean deleteChatRecord(String sendUserId, String receiveUserId) {
        QueryWrapper<ImContentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(t-> {
            t.eq(ImContentEntity::getSendUserId, receiveUserId)
                    .eq(ImContentEntity::getReceiveUserId, sendUserId).or();
            t.eq(ImContentEntity::getReceiveUserId, receiveUserId)
                    .eq(ImContentEntity::getSendUserId, sendUserId);
        });
        List<ImContentEntity> list = this.list(queryWrapper);
        for (ImContentEntity entity : list) {
            if(entity.getSendDeleteMark()!=null){
                if(!entity.getSendDeleteMark().equals(sendUserId)) {
                    entity.setDeleteMark(1);
                    this.updateById(entity);
                }
            }
            entity.setSendDeleteMark(sendUserId);
            this.updateById(entity);
        }
        QueryWrapper<ImContentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ImContentEntity::getDeleteMark,1);
        this.remove(wrapper);
        return false;
    }
}
