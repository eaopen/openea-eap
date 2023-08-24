package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.permission.entity.RoleEntity;
import org.openea.eap.extj.permission.model.role.RolePagination;

import java.util.Collection;
import java.util.List;

public interface RoleService
//        extends SuperService<RoleEntity>
{
    /*============================= get接口 ================================*/

    /**
     * 列表
     *
     * @return 角色对象集合
     */
    List<RoleEntity> getList();












    /**
     * 信息
     *
     * @param roleId 角色ID
     * @return 角色对象
     */
    RoleEntity getInfo(String roleId);











    /**
     * 列表
     *
     * @param page 条件
     */
    List<RoleEntity> getList(RolePagination page, Integer globalMark);



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

    Collection<RoleEntity> getListByUserId(String userId);
}
