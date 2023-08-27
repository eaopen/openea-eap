package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.permission.entity.AuthorizeEntity;
import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;
import org.openea.eap.extj.permission.model.authorize.OnlineDynamicSqlModel;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapAuthorizeService implements AuthorizeService {
    @Override
    public <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel) {
        // TODO
        return null;
    }

    @Override
    public List<AuthorizeEntity> getListByObjectId(String roleId, String authorizePortalManage) {
        return null;
    }

    @Override
    public boolean getConditionSql(UserInfo userInfo, String moduleId, Object o, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, List<OnlineDynamicSqlModel> sqlModelList) {
        return false;
    }
}
