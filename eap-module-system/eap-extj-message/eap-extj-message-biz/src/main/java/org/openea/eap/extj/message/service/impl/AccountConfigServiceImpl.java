package org.openea.eap.extj.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.AccountConfigEntity;
import org.openea.eap.extj.message.mapper.AccountConfigMapper;
import org.openea.eap.extj.message.model.accountconfig.AccountConfigForm;
import org.openea.eap.extj.message.model.accountconfig.AccountConfigPagination;
import org.openea.eap.extj.message.service.AccountConfigService;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.superQuery.ConditionJsonModel;
import org.openea.eap.extj.model.visualJson.superQuery.SuperQueryConditionModel;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 账号配置功能
 * */
@Service
public class AccountConfigServiceImpl
        extends SuperServiceImpl<AccountConfigMapper, AccountConfigEntity>
        implements AccountConfigService {


    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AuthorizeService authorizeService;


    @Override
    public List<AccountConfigEntity> getList(AccountConfigPagination accountConfigPagination) {
        return getTypeList(accountConfigPagination, accountConfigPagination.getDataType());
    }

    @Override
    public List<AccountConfigEntity> getTypeList(AccountConfigPagination accountConfigPagination, String dataType) {
        String userId = userProvider.get().getUserId();
        int total = 0;
        int accountConfigNum = 0;
        QueryWrapper<AccountConfigEntity> accountConfigQueryWrapper = new QueryWrapper<>();

        //关键字
        if (StringUtil.isNotBlank(accountConfigPagination.getKeyword()) && !"null".equals(accountConfigPagination.getKeyword())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().and(t -> t.like(AccountConfigEntity::getEnCode, accountConfigPagination.getKeyword())
                    .or().like(AccountConfigEntity::getFullName, accountConfigPagination.getKeyword()).or().like(AccountConfigEntity::getAddressorName,accountConfigPagination.getKeyword())
                    .or().like(AccountConfigEntity::getSmtpUser,accountConfigPagination.getKeyword()).or().like(AccountConfigEntity::getSmsSignature,accountConfigPagination.getKeyword()));
        }
        //webhook类型
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getWebhookType())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getWebhookType, accountConfigPagination.getWebhookType());
        }
        //渠道
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getChannel())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getChannel, accountConfigPagination.getChannel());
        }
        //状态
        if(ObjectUtil.isNotEmpty(accountConfigPagination.getEnabledMark())){
            accountConfigNum++;
            int enabledMark = Integer.parseInt(accountConfigPagination.getEnabledMark());
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getEnabledMark, enabledMark);
        }
        //配置类型
        if (ObjectUtil.isNotEmpty(accountConfigPagination.getType())) {
            accountConfigNum++;
            accountConfigQueryWrapper.lambda().eq(AccountConfigEntity::getType, accountConfigPagination.getType());
        }

        //排序
        if (StringUtil.isEmpty(accountConfigPagination.getSidx())) {
            accountConfigQueryWrapper.lambda().orderByAsc(AccountConfigEntity::getSortCode).orderByDesc(AccountConfigEntity::getCreatorTime).orderByDesc(AccountConfigEntity::getLastModifyTime);
        } else {
            try {
                String sidx = accountConfigPagination.getSidx();
                AccountConfigEntity accountConfigEntity = new AccountConfigEntity();
                Field declaredField = accountConfigEntity.getClass().getDeclaredField(sidx);
                declaredField.setAccessible(true);
                String value = declaredField.getAnnotation(TableField.class).value();
                accountConfigQueryWrapper = "asc".equals(accountConfigPagination.getSort().toLowerCase()) ? accountConfigQueryWrapper.orderByAsc(value) : accountConfigQueryWrapper.orderByDesc(value);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (!"1".equals(dataType)) {
            if (total > 0 || total == 0) {
                Page<AccountConfigEntity> page = new Page<>(accountConfigPagination.getCurrentPage(), accountConfigPagination.getPageSize());
                IPage<AccountConfigEntity> userIPage = this.page(page, accountConfigQueryWrapper);
                return accountConfigPagination.setData(userIPage.getRecords(), userIPage.getTotal());
            } else {
                List<AccountConfigEntity> list = new ArrayList();
                return accountConfigPagination.setData(list, list.size());
            }
        } else {
            return this.list(accountConfigQueryWrapper);
        }
    }


    @Override
    public AccountConfigEntity getInfo(String id) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(AccountConfigEntity entity) {
        this.save(entity);
    }

    @Override
    public boolean update(String id, AccountConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(AccountConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
    //子表方法

    //列表子表数据方法


    //验证表单唯一字段
    @Override
    public boolean checkForm(AccountConfigForm form, int i,String type,String id) {
        int total = 0;
        if (ObjectUtil.isNotEmpty(form.getEnCode())) {
            QueryWrapper<AccountConfigEntity> codeWrapper = new QueryWrapper<>();
            codeWrapper.lambda().eq(AccountConfigEntity::getEnCode, form.getEnCode());
            codeWrapper.lambda().eq(AccountConfigEntity::getType,type);
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                codeWrapper.lambda().ne(AccountConfigEntity::getId, id);
            }
            total += (int) this.count(codeWrapper);
        }
        int c = 0;
        if (total > i + c) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkGzhId(String gzhId, int i,String type,String id) {
        int total = 0;
        if (StringUtil.isNotEmpty(gzhId) && !"null".equals(gzhId)) {
            QueryWrapper<AccountConfigEntity> codeWrapper = new QueryWrapper<>();
            codeWrapper.lambda().eq(AccountConfigEntity::getAppKey, gzhId);
            codeWrapper.lambda().eq(AccountConfigEntity::getType,type);
            if(StringUtil.isNotBlank(id) && !"null".equals(id)) {
                codeWrapper.lambda().ne(AccountConfigEntity::getId, id);
            }
            total += (int) this.count(codeWrapper);
        }
        int c = 0;
        if (total > i + c) {
            return true;
        }
        return false;
    }

    @Override
    public AccountConfigEntity getInfoByType(String appKey, String type) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getType, type);
        queryWrapper.lambda().eq(AccountConfigEntity::getAppKey,appKey);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<AccountConfigEntity> getListByType(String type){
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getType,type);
        queryWrapper.lambda().eq(AccountConfigEntity::getEnabledMark,1);
        return this.list(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(AccountConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id,String type) {
        QueryWrapper<AccountConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AccountConfigEntity::getEnCode, enCode);
        queryWrapper.lambda().eq(AccountConfigEntity::getType,type);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(AccountConfigEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public ActionResult ImportData(AccountConfigEntity entity) throws DataException {
        if (entity != null) {
//            if (isExistByFullName(entity.getFullName(), entity.getId())) {
//                return ActionResult.fail(MsgCode.EXIST001.get());
//            }
            if (isExistByEnCode(entity.getEnCode(), entity.getId(),entity.getType())) {
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