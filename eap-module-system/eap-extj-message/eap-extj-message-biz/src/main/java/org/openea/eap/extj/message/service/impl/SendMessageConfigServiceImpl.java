package org.openea.eap.extj.message.service.impl;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.SendConfigRecordEntity;
import org.openea.eap.extj.message.entity.SendConfigTemplateEntity;
import org.openea.eap.extj.message.entity.SendMessageConfigEntity;
import org.openea.eap.extj.message.mapper.SendMessageConfigMapper;
import org.openea.eap.extj.message.model.sendmessageconfig.*;
import org.openea.eap.extj.message.service.SendConfigRecordService;
import org.openea.eap.extj.message.service.SendConfigTemplateService;
import org.openea.eap.extj.message.service.SendMessageConfigService;
import org.openea.eap.extj.util.RandomUtil;
import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.permission.service.AuthorizeService;
import java.lang.reflect.Field;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.openea.eap.extj.util.*;

/**
 * 消息发送配置
 * */
@Service
public class SendMessageConfigServiceImpl extends SuperServiceImpl<SendMessageConfigMapper, SendMessageConfigEntity> implements SendMessageConfigService {



    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;

    @Autowired
    private SendConfigRecordService sendConfigRecordService;


    @Override
    public List<SendMessageConfigEntity> getList(SendMessageConfigPagination sendMessageConfigPagination) {
        return getTypeList(sendMessageConfigPagination, sendMessageConfigPagination.getDataType());
    }

    @Override
    public List<SendMessageConfigEntity> getTypeList(SendMessageConfigPagination sendMessageConfigPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int sendMessageConfigNum = 0;
        QueryWrapper<SendMessageConfigEntity> sendMessageConfigQueryWrapper = new QueryWrapper<>();
        int sendConfigTemplateNum = 0;
        QueryWrapper<SendConfigTemplateEntity> sendConfigTemplateQueryWrapper = new QueryWrapper<>();
        //关键字
        if (ObjectUtil.isNotEmpty(sendMessageConfigPagination.getKeyword())) {
            sendMessageConfigNum++;
            sendMessageConfigQueryWrapper.lambda().and(t -> t.like(SendMessageConfigEntity::getEnCode, sendMessageConfigPagination.getKeyword()).
                    or().like(SendMessageConfigEntity::getFullName, sendMessageConfigPagination.getKeyword()));
        }
        //模板类型
        if (ObjectUtil.isNotEmpty(sendMessageConfigPagination.getTemplateType())) {
            sendMessageConfigNum++;
            sendMessageConfigQueryWrapper.lambda().eq(SendMessageConfigEntity::getTemplateType, sendMessageConfigPagination.getTemplateType());
        }
        //状态
        if (ObjectUtil.isNotEmpty(sendMessageConfigPagination.getEnabledMark())) {
            sendMessageConfigNum++;
            int enabledMark = Integer.parseInt(sendMessageConfigPagination.getEnabledMark());
            sendMessageConfigQueryWrapper.lambda().eq(SendMessageConfigEntity::getEnabledMark, enabledMark);
        }
        //消息来源
        if (ObjectUtil.isNotEmpty(sendMessageConfigPagination.getMessageSource())) {
            sendMessageConfigNum++;
            sendMessageConfigQueryWrapper.lambda().eq(SendMessageConfigEntity::getMessageSource, sendMessageConfigPagination.getMessageSource());
        }

        //排序
        if (StringUtil.isEmpty(sendMessageConfigPagination.getSidx())) {
            sendMessageConfigQueryWrapper.lambda().orderByAsc(SendMessageConfigEntity::getSortCode).orderByDesc(SendMessageConfigEntity::getCreatorTime).orderByDesc(SendMessageConfigEntity::getLastModifyTime);;
        } else {
            try {
                String sidx = sendMessageConfigPagination.getSidx();
                SendMessageConfigEntity sendMessageConfigEntity = new SendMessageConfigEntity();
                Field declaredField = sendMessageConfigEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                sendMessageConfigQueryWrapper = "asc".equals(sendMessageConfigPagination.getSort().toLowerCase()) ? sendMessageConfigQueryWrapper.orderByAsc(value) : sendMessageConfigQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0  || total == 0) {
                Page<SendMessageConfigEntity> page = new Page<>(sendMessageConfigPagination.getCurrentPage(), sendMessageConfigPagination.getPageSize());
                IPage<SendMessageConfigEntity> userIPage = this.page(page, sendMessageConfigQueryWrapper);
                return sendMessageConfigPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<SendMessageConfigEntity> list = new ArrayList();
                return sendMessageConfigPagination.setData(list, list.size());
            }
        } else {
            return this.list(sendMessageConfigQueryWrapper);
        }
    }

    @Override
    public List<SendMessageConfigEntity> getSelectorList(SendMessageConfigPagination sendMessageConfigPagination) {
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getMessageSource, 5).eq(SendMessageConfigEntity::getEnabledMark, 1);
        queryWrapper.lambda().eq(SendMessageConfigEntity::getTemplateType, 0);
        Page<SendMessageConfigEntity> page = new Page<>(sendMessageConfigPagination.getCurrentPage(), sendMessageConfigPagination.getPageSize());
        IPage<SendMessageConfigEntity> userIPage = this.page(page, queryWrapper);
        return sendMessageConfigPagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }


    @Override
    public SendMessageConfigEntity getInfo(String id) {
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public SendMessageConfigEntity getInfoByEnCode(String enCode){
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public SendMessageConfigEntity getSysConfig(String enCode,String type){
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getTemplateType, "1");
        queryWrapper.lambda().eq(SendMessageConfigEntity::getEnCode,enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(SendMessageConfigEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, SendMessageConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(SendMessageConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    //子表方法
    @Override
    public List<SendConfigTemplateEntity> getSendConfigTemplateList(String id, SendMessageConfigPagination sendMessageConfigPagination) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        return sendConfigTemplateService.list(sendConfigTemplateService.getChild(sendMessageConfigPagination, queryWrapper));
    }

    @Override
    public List<SendConfigTemplateEntity> getSendConfigTemplateList(String id) {
        QueryWrapper<SendConfigTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendConfigTemplateEntity::getSendConfigId, id);
        queryWrapper.lambda().orderByDesc(SendConfigTemplateEntity::getSortCode);
        return sendConfigTemplateService.list(queryWrapper);
    }

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(SendMessageConfigForm form, int i,String id) {
        int total = 0;
        if (ObjectUtil.isNotEmpty(form.getEnCode())) {
            QueryWrapper<SendMessageConfigEntity> enCodeWrapper = new QueryWrapper<>();
            enCodeWrapper.lambda().eq(SendMessageConfigEntity::getEnCode, form.getEnCode());
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                enCodeWrapper.lambda().ne(SendMessageConfigEntity::getId, id);
            }
            if ((int) this.count(enCodeWrapper) > i) {
                total++;
            }
        }
        if (form.getSendConfigTemplateList() != null) {
        }
        if (total > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SendMessageConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SendMessageConfigEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(SendMessageConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public ActionResult ImportData(SendMessageConfigEntity entity) throws DataException {
        if (entity != null) {
//            if (isExistByFullName(entity.getFullName(), null)) {
//                return ActionResult.fail(MsgCode.EXIST001.get());
//            }
            if (isExistByEnCode(entity.getEnCode(), entity.getId())) {
                return ActionResult.fail(MsgCode.EXIST002.get());
            }
            try {
                this.save(entity);
            } catch (Exception e) {
                throw new DataException(MsgCode.IMP003.get());
            }
            return ActionResult.success(MsgCode.IMP001.get());
        }
        return ActionResult.fail("导入数据格式不正确");
    }
    @Override
    public List<SendMessageConfigEntity> getList(List<String> idList){
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SendMessageConfigEntity::getId,idList);
        return this.list(queryWrapper);
    }


    public List<String> getIdList(String usedId){
        List<String> idList = new ArrayList<>();
        QueryWrapper<SendMessageConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(SendMessageConfigEntity::getUsedId,","+usedId+",");
        if(this.list(queryWrapper) != null && this.list(queryWrapper).size()>0){
            idList = this.list(queryWrapper).stream().distinct().map(t->t.getId()).collect(Collectors.toList());
        }
        return idList;
    }

    @Override
    public void updateUsed(String id,List<String> sendConfigIdList){
        UserInfo userInfo = userProvider.get();
        List<String> oldSendConfigList = this.getIdList(id);
        if(oldSendConfigList != null && oldSendConfigList.size()>0){
            List<String> subSendConfigIdList = this.subList(oldSendConfigList,sendConfigIdList);
            this.removeUsed(id,subSendConfigIdList);
        }
        if(sendConfigIdList != null && sendConfigIdList.size()>0) {
            List<SendMessageConfigEntity> sendConfigList = this.getList(sendConfigIdList);
            if (sendConfigIdList != null && sendConfigIdList.size() > 0) {
                for (SendMessageConfigEntity entity : sendConfigList) {
                    SendConfigRecordEntity recordEntity = new SendConfigRecordEntity();
                    List<String> usedIdList = new ArrayList<>();
                    String usedId = "";
                    if (StringUtil.isNotBlank(entity.getUsedId())) {
                        usedIdList = Arrays.asList(entity.getUsedId().split(","));
                        if(!usedIdList.contains(id)){
                            usedId = entity.getUsedId()+","+id;
                        }else {
                            usedId = entity.getUsedId();
                        }
                        entity.setUsedId(usedId);
                    }else {
                        entity.setUsedId(","+id+",");
                    }
                    recordEntity = sendConfigRecordService.getRecord(entity.getId(),id);
                    if(recordEntity != null){
                        recordEntity.setLastModifyTime(DateUtil.getNowDate());
                        recordEntity.setLastModifyUserId(userInfo.getUserId());
                        sendConfigRecordService.update(recordEntity.getId(),recordEntity);
                    }else {
                        recordEntity = new SendConfigRecordEntity();
                        recordEntity.setId(RandomUtil.uuId());
                        recordEntity.setSendConfigId(entity.getId());
                        recordEntity.setMessageSource(entity.getMessageSource());
                        recordEntity.setUsedId(id);
                        recordEntity.setCreatorTime(DateUtil.getNowDate());
                        recordEntity.setCreatorUserId(userInfo.getUserId());
                        sendConfigRecordService.create(recordEntity);
                    }
                }
                this.updateBatchById(sendConfigList);
            }
        }
    }

    @Override
    public void removeUsed(String id,List<String> sendConfigIdList){
        if(sendConfigIdList != null && sendConfigIdList.size()>0){
            List<SendMessageConfigEntity> subConfigList = this.getList(sendConfigIdList);
            if(subConfigList != null && subConfigList.size()>0){
                for(SendMessageConfigEntity subEntity : subConfigList) {
                    List<String> usedIdList = new ArrayList<>();
                    String usedId = "";
                    if(StringUtil.isNotBlank(subEntity.getUsedId())) {
                        usedIdList = Arrays.asList(subEntity.getUsedId().split(","));
                        if(usedIdList.size()>2){
                            usedId = subEntity.getUsedId().replace(","+id,"");
                        }
                        subEntity.setUsedId(usedId);
                    }
                    SendConfigRecordEntity recordEntity = sendConfigRecordService.getRecord(subEntity.getId(),id);
                    if(recordEntity != null){
                        sendConfigRecordService.delete(recordEntity);
                    }
                }
            }
            this.updateBatchById(subConfigList);
        }
    }

    @Override
    public List<String> subList(List<String> list1, List<String> list2) {
        //空间换时间 降低时间复杂度
        Map<String, String> tempMap = new HashMap<>();
        for(String str:list2){
            tempMap.put(str,str);
        }
        //LinkedList 频繁添加删除 也可以ArrayList容量初始化为List1.size(),防止数据量过大时频繁扩容以及数组复制
        List<String> resList = new LinkedList<>();
        for(String str:list1){
            if(!tempMap.containsKey(str)){
                resList.add(str);
            }
        }
        return resList;
    }
    @Override
    public boolean idUsed(String id){
        boolean flag = false;
        SendMessageConfigEntity entity = this.getInfo(id);
        if(StringUtil.isNotBlank(entity.getUsedId()) && !"null".equals(entity.getUsedId())){
            flag = true;
        }
        return flag;
    }

}