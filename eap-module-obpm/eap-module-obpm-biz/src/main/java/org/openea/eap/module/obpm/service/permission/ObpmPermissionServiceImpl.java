package org.openea.eap.module.obpm.service.permission;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.service.permission.PermissionService;
import org.openea.eap.module.system.service.permission.PermissionServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;

@Service
@ConditionalOnProperty(prefix = "eap", name = "userDataType", havingValue = "obpm")
@Slf4j
public class ObpmPermissionServiceImpl extends PermissionServiceImpl implements PermissionService {
    @Override
    public List<MenuDO> getUserMenuListFromCache(Long userId, String userKey, Collection<Integer> menuTypes){
        // 1. eap 菜单
        // 获得角色列表
        Set<Long> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 获得菜单列表
        List<MenuDO> menuList = getRoleMenuListFromCache(roleIds,
                menuTypes,
                singleton(CommonStatusEnum.ENABLE.getStatus())); // 只要开启的
        // 2. obpm菜单

        // 3. 合并

        return menuList;
    }
}
