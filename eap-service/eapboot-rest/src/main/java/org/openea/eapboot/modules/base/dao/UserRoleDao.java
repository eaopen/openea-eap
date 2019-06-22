package org.openea.eapboot.modules.base.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.base.entity.UserRole;

import java.util.List;

/**
 * 用户角色数据处理层
 */
public interface UserRoleDao extends EapBaseDao<UserRole,String> {

    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    void deleteByUserId(String userId);
}
