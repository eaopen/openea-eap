package org.openea.eap.extj.permission.service;



import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;

import java.util.List;

/**
 * 操作权限
 *
 */
public interface AuthorizeService
//        extends SuperService<AuthorizeEntity>
{
    Object getCondition(AuthorizeConditionModel messageDataType);

//    /**
//     * 获取权限（菜单、按钮、列表）
//     *
//     * @param userInfo 对象
//     * @return
//     */
//    AuthorizeVO getAuthorize(UserInfo userInfo) throws Exception;
//
//    /**
//     * 获取权限（菜单、按钮、列表）
//     *
//     * @param isCache 是否存在redis
//     * @return
//     */
//    AuthorizeVO getAuthorize(boolean isCache);
//
//    /**
//     * 创建
//     *
//     * @param objectId      对象主键
//     * @param authorizeList 实体对象
//     */
//    void save(String objectId, AuthorizeDataUpForm authorizeList);
//
//    /**
//     * 创建
//     *
//     * @param saveBatchForm    对象主键
//     */
//    void saveBatch(SaveBatchForm saveBatchForm, boolean isBatch);
//
//    /**
//     * 根据用户id获取列表
//     *
//     * @param isAdmin 是否管理员
//     * @param userId  用户主键
//     * @return
//     */
//    List<AuthorizeEntity> getListByUserId(boolean isAdmin, String userId);
//
//    /**
//     * 根据对象Id获取列表
//     *
//     * @param objectId 对象主键
//     * @return
//     */
//    List<AuthorizeEntity> getListByObjectId(String objectId);
//
//    Boolean existByObjId(String objectId);
//
//    /**
//     * 通过角色id集合获取系统
//     *
//     * @param roleIds
//     * @return
//     */
//    List<SystemBaeModel> systemListByRoleIds(List<String> roleIds);
//
//    /**
//     * 根据对象Id获取列表
//     *
//     * @param objectId 对象主键
//     * @param itemType 对象主键
//     * @return
//     */
//    List<AuthorizeEntity> getListByObjectId(String objectId, String itemType);
//
//    /**
//     * 根据对象Id获取列表
//     *
//     * @param objectType 对象主键
//     * @return
//     */
//    List<AuthorizeEntity> getListByObjectAndItem(String itemId, String objectType);
//
//    <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel);
//
//    boolean getConditionSql(UserInfo userInfo, String moduleId , String mainTable, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder from, List<OnlineDynamicSqlModel> sqlModelList);
//
//    List<SystemBaeModel> findSystem(List<String> roleIds);
//
//    void savePortalManage(String portalManageId, SaveAuthForm saveAuthForm);

}
