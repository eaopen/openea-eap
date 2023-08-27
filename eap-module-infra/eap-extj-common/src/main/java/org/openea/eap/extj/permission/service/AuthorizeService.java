package org.openea.eap.extj.permission.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.permission.entity.AuthorizeEntity;
import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;
import org.openea.eap.extj.permission.model.authorize.OnlineDynamicSqlModel;

import java.util.List;

/**
 * 操作权限
 *
 */
public interface AuthorizeService
//        extends SuperService<AuthorizeEntity>
{


    <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel);


    List<AuthorizeEntity> getListByObjectId(String roleId, String authorizePortalManage);

    boolean getConditionSql(UserInfo userInfo, String moduleId, Object o, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, List<OnlineDynamicSqlModel> sqlModelList);
}
