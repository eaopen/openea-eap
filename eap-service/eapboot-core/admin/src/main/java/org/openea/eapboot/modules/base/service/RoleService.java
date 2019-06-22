package org.openea.eapboot.modules.base.service;


import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.base.entity.Role;

import java.util.List;

/**
 * 角色接口
 */
public interface RoleService extends EapBaseService<Role,String> {

    /**
     * 获取默认角色
     * @param defaultRole
     * @return
     */
    List<Role> findByDefaultRole(Boolean defaultRole);
}
