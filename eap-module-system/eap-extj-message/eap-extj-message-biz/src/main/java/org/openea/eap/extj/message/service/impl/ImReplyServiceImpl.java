package org.openea.eap.extj.message.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.ImReplyEntity;
import org.openea.eap.extj.message.mapper.ImReplyMapper;
import org.openea.eap.extj.message.model.ImReplyListModel;
import org.openea.eap.extj.message.service.ImReplyService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ImReplyServiceImpl extends SuperServiceImpl<ImReplyMapper, ImReplyEntity> implements ImReplyService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;

    @Override
    public List<ImReplyEntity> getList() {
        QueryWrapper<ImReplyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImReplyEntity::getUserId, userProvider.get().getUserId()).or()
                .eq(ImReplyEntity::getReceiveUserId, userProvider.get().getUserId())
                .orderByDesc(ImReplyEntity::getUserId);
        return this.list();
    }

    @Override
    public boolean savaImReply(ImReplyEntity entity) {
        QueryWrapper<ImReplyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ImReplyEntity::getUserId, entity.getUserId()).eq(ImReplyEntity::getReceiveUserId, entity.getReceiveUserId()).isNull(ImReplyEntity::getImreplySendDeleteMark);
        //判断数据是否存在
        ImReplyEntity imReplyEntity = this.getOne(queryWrapper);
        if (imReplyEntity!=null) {
            entity.setId(imReplyEntity.getId());
            this.updateById(entity);
            return true;
        }
        if(ObjectUtil.isEmpty(entity.getId())){
            entity.setId(IdUtil.getSnowflakeNextIdStr());
        }
        this.save(entity);
        return true;

    }

    @Override
    public List<ImReplyListModel> getImReplyList() {
        List<ImReplyListModel> imReplyList = this.baseMapper.getImReplyList();
        List<ImReplyListModel> imReplyLists = new ArrayList<>(imReplyList);
        // 过滤掉用户id和接收id相同的
        imReplyLists = imReplyList.stream().filter(t ->{
            if(t.getImreplySendDeleteMark() == null){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
//        // 过滤掉用户id和接收id相同的
//        List<ImReplyListModel> imReplyListModels = new ArrayList<>(imReplyList);
//        for (int i = 0; i < imReplyList.size(); i++) {
//            ImReplyListModel imReplyListModel = imReplyList.get(i);
//            // 不和自己比
//            imReplyListModels.remove(imReplyList.get(i));
//            List<ImReplyListModel> irs = new ArrayList<>(imReplyListModels);
//            ImReplyListModel model = irs.stream().filter(t -> t.getUserId().equals(imReplyListModel.getUserId()) && t.getId().equals(imReplyListModel.getId())).findFirst().orElse(null);
//            if (model != null) {
//                imReplyLists.remove(model);
//            }
//        }
        //我发给别人
        List<ImReplyListModel> collect = imReplyLists.stream().filter(t -> t.getUserId().equals(userProvider.get().getUserId())).collect(Collectors.toList());
        //头像替换成对方的
        for (ImReplyListModel imReplyListModel : collect) {
            UserEntity entity = userService.getInfo(imReplyListModel.getId());
            imReplyListModel.setHeadIcon(entity != null ? entity.getHeadIcon() : "");
//            imReplyListModel.setSendDeleteMark(imReplyListModel.getSendDeleteMark());
//            imReplyListModel.setImreplySendDeleteMark(imReplyListModel.getImreplySendDeleteMark());
//            imReplyListModel.setDeleteMark(imReplyListModel.getDeleteMark());
        }
        //别人发给我
        List<ImReplyListModel> list = imReplyLists.stream().filter(t -> t.getId().equals(userProvider.get().getUserId())).collect(Collectors.toList());
        for (ImReplyListModel model : list) {
            //移除掉互发的
            List<ImReplyListModel> collect1 = collect.stream().filter(t -> t.getId().equals(model.getUserId())).collect(Collectors.toList());
            if (collect1.size() > 0) {
                //判断我发给别人的时间和接收的时间大小
                //接收的大于发送的
                if (model.getLatestDate().getTime() > collect1.get(0).getLatestDate().getTime()) {
                    collect.remove(collect1.get(0));
                } else { //发送的大于接收的则跳过
                    continue;
                }
            }
            ImReplyListModel imReplyListModel = new ImReplyListModel();
            UserEntity entity = userService.getInfo(model.getUserId());
            if(entity != null) {
                imReplyListModel.setHeadIcon(entity.getHeadIcon());
                imReplyListModel.setUserId(userProvider.get().getUserId());
                imReplyListModel.setId(entity.getId());
                imReplyListModel.setLatestDate(model.getLatestDate());
                imReplyListModel.setLatestMessage(model.getLatestMessage());
                imReplyListModel.setMessageType(model.getMessageType());
                if (model.getImreplySendDeleteMark() != null && !model.getImreplySendDeleteMark().equals(userProvider.get().getUserId())) {
                    imReplyListModel.setSendDeleteMark(model.getSendDeleteMark());
                    imReplyListModel.setImreplySendDeleteMark(model.getImreplySendDeleteMark());
                    imReplyListModel.setDeleteMark(model.getDeleteMark());
                }

                collect.add(imReplyListModel);
            }
        }
        return collect;
    }

    @Override
    public boolean relocation(String sendUserId, String receiveUserId) {

        QueryWrapper<ImReplyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(t-> {
            t.eq(ImReplyEntity::getUserId, receiveUserId)
                    .eq(ImReplyEntity::getReceiveUserId, sendUserId).or();
            t.eq(ImReplyEntity::getReceiveUserId, receiveUserId)
                    .eq(ImReplyEntity::getUserId, sendUserId);
        });
        List<ImReplyEntity> list = this.list(queryWrapper);
        for (ImReplyEntity entity : list) {
            if(entity.getImreplySendDeleteMark()!=null){
                if(!entity.getImreplySendDeleteMark().equals(sendUserId)) {
                    entity.setImreplyDeleteMark(1);
                    this.updateById(entity);
                }
            }
            entity.setImreplySendDeleteMark(sendUserId);
            this.updateById(entity);
        }
        QueryWrapper<ImReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ImReplyEntity::getImreplyDeleteMark,1);
        this.remove(wrapper);
        return false;
    }

}
