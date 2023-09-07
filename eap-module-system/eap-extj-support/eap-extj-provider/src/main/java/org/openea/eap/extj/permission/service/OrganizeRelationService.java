package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.OrganizeRelationEntity;

import java.util.List;

/**
 * <p>
 * 组织关系 服务类
 * </p>
 *
 * @author YanYu
 * @since 2022-01-19
 */
public interface OrganizeRelationService extends SuperService<OrganizeRelationEntity> {

    /**
     * 获取组织关联对象
     *
     * @param organizeIds 组织id集合
     */
    List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds);

    /**
     * 获取组织关联对象
     *
     * @param organizeIds 组织id集合
     */
    List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds, String objectType);

    /**
     * 获取组织关联对象
     *
     * @param organizeIds 组织id集合
     */
    List<String> getPositionListByOrganizeId(List<String> organizeIds);

    /**
     * 获取组织角色关联对象
     *
     * @param roleId 角色ID
     * @return 关联对象集合
     */
    List<OrganizeRelationEntity> getRelationListByRoleId(String roleId);

    /**
     * 获取组织角色关联对象
     *
     * @param roleId 角色ID
     * @return 关联对象集合
     */
    List<OrganizeRelationEntity> getRelationListByRoleIdList(List<String> roleId);

    /**
     * 通过对象id获取组织关系
     *
     * @param objectType 关系
     * @param objectId 对象id
     * @return 关联对象集合
     */
    List<OrganizeRelationEntity> getRelationListByObjectIdAndType(String objectType, String objectId);

    /**
     * 是否存在组织角色关联关系
     * @param roleId
     * @param organizeId
     * @return
     */
    Boolean existByRoleIdAndOrgId(String roleId, String organizeId);

    Boolean existByObjTypeAndOrgId(String objectType, String organizeId);

    Boolean existByObjAndOrgId(String objectType, String objId , String organizeId);

    /**
     * 获取关联对象根据类型
     *
     * @return 关联对象集合
     */
    List<OrganizeRelationEntity> getRelationListByType(String objectType);

    List<OrganizeRelationEntity> getListByTypeAndOrgId(String objectType,String orgId);

    Boolean deleteAllByRoleId(String roleId);

    /*================ 切换组织后 -> 自动切换岗位 ===============*/

    /**
     * 自动获取当前组织下的默认岗位
     * @param userId 用户ID
     * @param changeToMajorOrgId 切换组织ID
     * @param currentMajorPosId 原本的岗位ID
     * @return 岗位ID
     */
    String autoGetMajorPositionId(String userId, String changeToMajorOrgId, String currentMajorPosId);

    /**
     *  自动获取有权限的组织ID
     * @param userId 用户ID
     * @param orgIds 组织ID集合
     * @param currentMajorOrgId 当前默认组织ID
     * @return 组织ID
     */
    String autoGetMajorOrganizeId(String userId, List<String> orgIds, String currentMajorOrgId);

    /**
     * 自动切换有权限的
     * @param userIds 用户ID集合
     */
    void autoSetOrganize(List<String> userIds);

    /**
     * 自动切换岗位
     * @param userIds 用户ID集合
     */
    void autoSetPosition(List<String> userIds);

    /**
     * 检查组织是否有权限
     * @param userId 用户ID
     * @param orgId 组织ID
     * @return true:存在
     */
    Boolean checkBasePermission(String userId, String orgId);
}
