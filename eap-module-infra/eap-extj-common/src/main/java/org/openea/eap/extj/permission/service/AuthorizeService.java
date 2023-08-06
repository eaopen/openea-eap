package org.openea.eap.extj.permission.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        extends SuperService<AuthorizeEntity>
{

    /**
     * 根据用户id获取列表
     *
     * @param isAdmin 是否管理员
     * @param userId  用户主键
     * @return
     */
    List<AuthorizeEntity> getListByUserId(boolean isAdmin, String userId);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    List<AuthorizeEntity> getListByObjectId(String objectId);

    Boolean existByObjId(String objectId);

    /**
     * 通过角色id集合获取系统
     *
     * @param roleIds
     * @return
     */
    List<SystemBaeModel> systemListByRoleIds(List<String> roleIds);

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @param itemType 对象主键
     * @return
     */
    List<AuthorizeEntity> getListByObjectId(String objectId, String itemType);

    /**
     * 根据对象Id获取列表
     *
     * @param objectType 对象主键
     * @return
     */
    List<AuthorizeEntity> getListByObjectAndItem(String itemId, String objectType);

    <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel);

    boolean getConditionSql(UserInfo userInfo, String moduleId , String mainTable, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder from, List<OnlineDynamicSqlModel> sqlModelList);

    List<SystemBaeModel> findSystem(List<String> roleIds);


}
