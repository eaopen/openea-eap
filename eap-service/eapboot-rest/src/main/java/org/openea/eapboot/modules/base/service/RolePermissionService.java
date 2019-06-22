package org.openea.eapboot.modules.base.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.base.entity.RolePermission;

import java.util.List;

/**
 * 角色权限接口
 */
public interface RolePermissionService extends EapBaseService<RolePermission,String> {

    /**
     * 通过permissionId获取
     * @param permissionId
     * @return
     */
    List<RolePermission> findByPermissionId(String permissionId);

    /**
     * 通过roleId获取
     * @param roleId
     * @return
     */
    List<RolePermission> findByRoleId(String roleId);

    /**
     * 通过roleId删除
     * @param roleId
     */
    void deleteByRoleId(String roleId);
}