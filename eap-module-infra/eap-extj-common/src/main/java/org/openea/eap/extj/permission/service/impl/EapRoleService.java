package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.RoleEntity;
import org.openea.eap.extj.permission.model.role.RolePagination;
import org.openea.eap.extj.permission.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * todo eap
 */
@Service
public class EapRoleService implements RoleService {
    @Override
    public List<RoleEntity> getList() {
        return null;
    }

    @Override
    public RoleEntity getInfo(String roleId) {
        return null;
    }

    @Override
    public List<RoleEntity> getList(RolePagination page, Integer globalMark) {
        return null;
    }

    @Override
    public void create(RoleEntity entity) {

    }

    @Override
    public Boolean update(String id, RoleEntity entity) {
        return null;
    }

    @Override
    public void delete(RoleEntity entity) {

    }

    @Override
    public Collection<RoleEntity> getListByUserId(String userId) {
        return null;
    }
}
