package org.openea.eap.extj.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.MessageDataTypeEntity;
import org.openea.eap.extj.message.mapper.MessageDataTypeMapper;
import org.openea.eap.extj.message.model.messagedatatype.MessageDataTypeForm;
import org.openea.eap.extj.message.model.messagedatatype.MessageDataTypePagination;
import org.openea.eap.extj.message.service.MessageDataTypeService;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.superQuery.ConditionJsonModel;
import org.openea.eap.extj.model.visualJson.superQuery.SuperQueryConditionModel;
import org.openea.eap.extj.model.visualJson.superQuery.SuperQueryJsonModel;
import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息中心类型数据
 * */
@Service
public class MessageDataTypeServiceImpl extends SuperServiceImpl<MessageDataTypeMapper, MessageDataTypeEntity> implements MessageDataTypeService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<MessageDataTypeEntity> getList(MessageDataTypePagination messageDataTypePagination) {
        return getTypeList(messageDataTypePagination, messageDataTypePagination.getDataType());
    }

    @Override
    public List<MessageDataTypeEntity> getTypeList(MessageDataTypePagination messageDataTypePagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int messageDataTypeNum = 0;
        QueryWrapper<MessageDataTypeEntity> messageDataTypeQueryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(messageDataTypePagination.getSuperQueryJson())) {
            SuperQueryJsonModel superQueryJsonModel = JsonUtil.getJsonToBean(messageDataTypePagination.getSuperQueryJson(), SuperQueryJsonModel.class);
            String matchLogic = superQueryJsonModel.getMatchLogic();
            List<ConditionJsonModel> superQueryList = JsonUtil.getJsonToList(superQueryJsonModel.getConditionJson(), ConditionJsonModel.class);
            for (ConditionJsonModel conditionjson : superQueryList) {
                Map<String, Object> map = JsonUtil.stringToMap(conditionjson.getAttr());
                Map<String, Object> configMap = JsonUtil.stringToMap(map.get("__config__").toString());
                String tableName = configMap.get("relationTable") != null ? String.valueOf(configMap.get("relationTable")) : String.valueOf(configMap.get("tableName"));
                conditionjson.setTableName(tableName);
            }
            messageDataTypeNum = getCondition(new SuperQueryConditionModel(messageDataTypeQueryWrapper, superQueryList, matchLogic, "messageDataType")
                    , new MessageDataTypeEntity(), messageDataTypeNum);
        }
        boolean pcPermission = false;
        boolean appPermission = false;
        boolean isPc = ServletUtil.getHeader("eap-origin").equals("pc");
        if (isPc && pcPermission) {
            if (!userProvider.get().getIsAdministrator()) {
                Object messageDataTypeObj = authorizeService.getCondition(new AuthorizeConditionModel(messageDataTypeQueryWrapper, messageDataTypePagination.getMenuId(), "base_message_data_type"));
                if (ObjectUtil.isEmpty(messageDataTypeObj)) {
                    return new ArrayList<>();
                } else {
                    messageDataTypeQueryWrapper = (QueryWrapper<MessageDataTypeEntity>) messageDataTypeObj;
                    messageDataTypeNum++;
                }
            }
        }
        if (!isPc && appPermission) {
            if (!userProvider.get().getIsAdministrator()) {
                Object messageDataTypeObj = authorizeService.getCondition(new AuthorizeConditionModel(messageDataTypeQueryWrapper, messageDataTypePagination.getMenuId(), "messageDataType"));
                if (ObjectUtil.isEmpty(messageDataTypeObj)) {
                    return new ArrayList<>();
                } else {
                    messageDataTypeQueryWrapper = (QueryWrapper<MessageDataTypeEntity>) messageDataTypeObj;
                    messageDataTypeNum++;
                }


            }
        }
        if (isPc) {
            if (ObjectUtil.isNotEmpty(messageDataTypePagination.getName())) {
                messageDataTypeNum++;
                messageDataTypeQueryWrapper.lambda().like(MessageDataTypeEntity::getFullName, messageDataTypePagination.getName());
            }

            if (ObjectUtil.isNotEmpty(messageDataTypePagination.getCode())) {
                messageDataTypeNum++;
                messageDataTypeQueryWrapper.lambda().like(MessageDataTypeEntity::getEnCode, messageDataTypePagination.getCode());
            }

        }
        //排序
        if (StringUtil.isEmpty(messageDataTypePagination.getSidx())) {
            messageDataTypeQueryWrapper.lambda().orderByDesc(MessageDataTypeEntity::getCreatorTime);
        } else {
            try {
                String sidx = messageDataTypePagination.getSidx();
                MessageDataTypeEntity messageDataTypeEntity = new MessageDataTypeEntity();
                Field declaredField = messageDataTypeEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                messageDataTypeQueryWrapper = "asc".equals(messageDataTypePagination.getSort().toLowerCase()) ? messageDataTypeQueryWrapper.orderByAsc(value) : messageDataTypeQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if ("0".equals(dataType)) {
            if (total > 0 || total == 0) {
                Page<MessageDataTypeEntity> page = new Page<>(messageDataTypePagination.getCurrentPage(), messageDataTypePagination.getPageSize());
                IPage<MessageDataTypeEntity> userIPage = this.page(page, messageDataTypeQueryWrapper);
                return messageDataTypePagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<MessageDataTypeEntity> list = new ArrayList();
                return messageDataTypePagination.setData(list, list.size());
            }
        } else {
            return this.list(messageDataTypeQueryWrapper);
        }
    }


    @Override
    public List<MessageDataTypeEntity> getListByType(List<String> type){
        QueryWrapper<MessageDataTypeEntity> queryWrapper = new QueryWrapper<>();
        if(type != null && type.size()>0) {
            queryWrapper.lambda().in(MessageDataTypeEntity::getType, type);
        }else {
            queryWrapper.lambda().eq(MessageDataTypeEntity::getType, "-1");
        }
        queryWrapper.lambda().orderByAsc(MessageDataTypeEntity::getEnCode);
       return this.list(queryWrapper);
    }

    @Override
    public MessageDataTypeEntity getInfo(String id) {
        QueryWrapper<MessageDataTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageDataTypeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public MessageDataTypeEntity getInfo(String type,String code){
        QueryWrapper<MessageDataTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageDataTypeEntity::getType, type);
        queryWrapper.lambda().eq(MessageDataTypeEntity::getEnCode,code);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(MessageDataTypeEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, MessageDataTypeEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(MessageDataTypeEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(MessageDataTypeForm form, int i) {
        int total = 0;
        if (total > i) {
            return true;
        }
        return false;
    }

    /**
     * 高级查询
     *
     * @param conditionModel
     * @param entity
     * @param num
     * @return
     */
    public Integer getCondition(SuperQueryConditionModel conditionModel, Object entity, int num) {
        QueryWrapper<?> queryWrapper = conditionModel.getObj();
        List<ConditionJsonModel> queryConditionModels = conditionModel.getConditionList();
        String op = conditionModel.getMatchLogic();
        String tableName = conditionModel.getTableName();
        List<ConditionJsonModel> useCondition = new ArrayList<>();
        for (ConditionJsonModel queryConditionModel : queryConditionModels) {
            if (queryConditionModel.getTableName().equalsIgnoreCase(tableName)) {
                if (queryConditionModel.getField().contains("jnpf")) {
                    String child = queryConditionModel.getField();
                    String s1 = child.substring(child.lastIndexOf("jnpf_")).replace("jnpf_", "");
                    queryConditionModel.setField(s1);
                }
                if (queryConditionModel.getField().startsWith("tableField")) {
                    String child = queryConditionModel.getField();
                    String s1 = child.substring(child.indexOf("-") + 1);
                    queryConditionModel.setField(s1);
                }
                useCondition.add(queryConditionModel);
            }
        }

        if (queryConditionModels.size() < 1 || useCondition.size() < 1) {
            return num;
        }
        if (useCondition.size() > 0) {
            num += 1;
        }
        //处理控件 转换为有效值
        for (ConditionJsonModel queryConditionModel : useCondition) {
            String jnpfKey = queryConditionModel.getJnpfKey();
            String fieldValue = queryConditionModel.getFieldValue();
            if (jnpfKey.equals(JnpfKeyConsts.DATE)) {
                Long o1 = Long.valueOf(fieldValue);
                String startTime = DateUtil.daFormat(o1);
                queryConditionModel.setFieldValue(startTime);
            } else if (jnpfKey.equals(JnpfKeyConsts.CREATETIME) || jnpfKey.equals(JnpfKeyConsts.MODIFYTIME)) {
                Long o1 = Long.valueOf(fieldValue);
                String startTime = DateUtil.daFormatHHMMSS(o1);
                queryConditionModel.setFieldValue(startTime);
            } else if (jnpfKey.equals(JnpfKeyConsts.CURRORGANIZE)) {
                List<String> orgList = JsonUtil.getJsonToList(fieldValue, String.class);
                queryConditionModel.setFieldValue(orgList.get(orgList.size() - 1));
            }
        }
        //反射获取数据库实际字段
        Class<?> aClass = entity.getClass();

        queryWrapper.and(tw -> {
            for (ConditionJsonModel conditionJsonModel : useCondition) {
                String conditionField = conditionJsonModel.getField();
                Field declaredField = null;
                try {
                    declaredField = aClass.getDeclaredField(conditionField);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                declaredField.setAccessible(true);
                String field = declaredField.getAnnotation(TableField.class).value();
                String fieldValue = conditionJsonModel.getFieldValue();
                String symbol = conditionJsonModel.getSymbol();
                if ("AND".equalsIgnoreCase(op)) {
                    if (symbol.equals("==")) {
                        tw.eq(field, fieldValue);
                    } else if (symbol.equals(">=")) {
                        tw.ge(field, fieldValue);
                    } else if (symbol.equals("<=")) {
                        tw.le(field, fieldValue);
                        tw.and(
                                qw -> qw.ne(field, "")
                        );
                    } else if (symbol.equals(">")) {
                        tw.gt(field, fieldValue);
                    } else if (symbol.equals("<")) {
                        tw.lt(field, fieldValue);
                        tw.and(
                                qw -> qw.ne(field, "")
                        );
                    } else if (symbol.equals("<>")) {
                        tw.ne(field, fieldValue);
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("like")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.like(field, fieldValue);
                        } else {
                            tw.isNull(field);
                        }
                    } else if (symbol.equals("notLike")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.notLike(field, fieldValue);
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        } else {
                            tw.isNotNull(field);
                        }
                    }
                } else {
                    if (symbol.equals("==")) {
                        tw.or(
                                qw -> qw.eq(field, fieldValue)
                        );
                    } else if (symbol.equals(">=")) {
                        tw.or(
                                qw -> qw.ge(field, fieldValue)
                        );
                    } else if (symbol.equals("<=")) {
                        tw.or(
                                qw -> qw.le(field, fieldValue)
                        );
                    } else if (symbol.equals(">")) {
                        tw.or(
                                qw -> qw.gt(field, fieldValue)
                        );
                    } else if (symbol.equals("<")) {
                        tw.or(
                                qw -> qw.lt(field, fieldValue)
                        );
                    } else if (symbol.equals("<>")) {
                        tw.or(
                                qw -> qw.ne(field, fieldValue)
                        );
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("like")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.like(field, fieldValue)
                            );
                        } else {
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        }
                    } else if (symbol.equals("notLike")) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            tw.or(
                                    qw -> qw.notLike(field, fieldValue)
                            );
                            tw.or(
                                    qw -> qw.isNull(field)
                            );
                        } else {
                            tw.or(
                                    qw -> qw.isNotNull(field)
                            );
                        }
                    }
                }
            }
        });
        return num;
    }


}