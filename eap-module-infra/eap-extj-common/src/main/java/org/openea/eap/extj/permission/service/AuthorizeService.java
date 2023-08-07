package org.openea.eap.extj.permission.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.model.base.SystemBaeModel;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.AuthorizeEntity;
import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;

import java.util.List;

/**
 * 操作权限
 *
 */
public interface AuthorizeService
//        extends SuperService<AuthorizeEntity>
{


    <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel);


}
