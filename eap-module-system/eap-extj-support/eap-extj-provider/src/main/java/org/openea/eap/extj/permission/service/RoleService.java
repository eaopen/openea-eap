package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.RoleEntity;
import org.openea.eap.extj.permission.model.role.RolePagination;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统角色
 *
 *
 */
public interface RoleService extends SuperService<RoleEntity> {

    /*============================= get接口 ================================*/

    /**
     * 列表
     *
     * @return 角色对象集合
     */
    List<RoleEntity> getList();

    /**
     * 获取全局角色集合
     *
     * @return 角色对象集合
     */
    List<RoleEntity> getGlobalList();

    /**
     * 列表
     *
     * @param userId 用户ID
     * @return 角色对象集合
     */
    List<RoleEntity> getListByUserId(String userId);

    /**
     * 组织底下所有角色
     * @param userId
     * @return
     */
    List<RoleEntity> getListByUserIdAndOrgId(String userId, String orgId);

    /**
     * 当前用户拥有的所有角色集合
     */
    List<String> getRoleIdsByCurrentUser();

    /**
     * 当前用户拥有的所有角色集合
     */
    List<String> getRoleIdsByCurrentUser(String orgId);

    /**
     * 获取用户组织底下，及全局的
     * @param userId
     * @param orgId
     * @return
     */
    List<String> getAllRoleIdsByUserIdAndOrgId(String userId, String orgId);

    /**
     * 信息
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    RoleEntity getInfo(String roleId);

    /**
     * 根据id集合返回角色对象集合
     *
     * @param roleIds 角色ID集合
     * @return 角色对象集合
     */
    List<RoleEntity> getListByIds(List<String> roleIds);

    /**
     * 根据id集合返回角色对象集合
     *
     * @param roleIds 角色ID集合
     * @return 角色对象集合
     */
    List<RoleEntity> getSwaptListByIds(Set<String> roleIds);

    Map<String,Object> getRoleMap();


    /**
     * 角色编码/name.id
     * @return
     */
    Map<String,Object> getRoleNameAndIdMap();

    /**
     * 获取角色实体
     *
     * @param fullName 角色名称
     * @return 角色对象
     */
    RoleEntity getInfoByFullName(String fullName);

    /**
     * 获取角色实体
     *
     * @param fullName 角色名称
     * @return 角色对象
     */
    RoleEntity getInfoByFullName(String fullName,String enCode);

    /**
     * 获取当前用户的默认组织下的所有角色集合
     *
     * @param orgId 组织ID
     * @return 角色对象集合
     */
    List<RoleEntity> getCurRolesByOrgId(String orgId);

    /**
     * 获取组织下的所有角色
     *
     * @param orgId 组织ID
     * @return 角色对象集合
     */
    List<RoleEntity> getRolesByOrgId(String orgId);

    String getBindInfo(String roleId, List<String> reduceOrgIds);

    /**
     * 列表
     *
     * @param page 条件
     */
    List<RoleEntity> getList(RolePagination page, Integer globalMark);

    /*============================ exist存在判断接口 =================================*/

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     */
    Boolean isExistByFullName(String fullName, String id, Integer globalMark);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     */
    Boolean isExistByEnCode(String enCode, String id);

    /**
     * 判断当前组织下是否存在角色
     *
     * @param orgId 组织ID
     */
    Boolean existCurRoleByOrgId(String orgId);

    /*============================ 存在判断接口 =================================*/

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(RoleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    Boolean update(String id, RoleEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(RoleEntity entity);









}
