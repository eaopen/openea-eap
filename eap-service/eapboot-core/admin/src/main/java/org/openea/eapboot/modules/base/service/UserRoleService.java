package org.openea.eapboot.modules.base.service;


import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.entity.UserRole;

import java.util.List;

/**
 * 用户角色接口
 */
public interface UserRoleService extends EapBaseService<UserRole,String> {

    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 通过roleId查找用户
     * @param roleId
     * @return
     */
    List<User> findUserByRoleId(String roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    void deleteByUserId(String userId);
}
