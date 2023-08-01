package org.openea.eap.module.obpm.service.org;

import org.openbpm.base.api.query.QueryFilter;
import org.openbpm.org.api.model.IGroup;
import org.openbpm.org.api.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EapGroupServiceImpl implements GroupService {
    @Override
    public List<? extends IGroup> getGroupsByGroupTypeUserId(String groupType, String userId) {
        return null;
    }

    @Override
    public Map<String, List<? extends IGroup>> getAllGroupByAccount(String account) {
        return null;
    }

    @Override
    public Map<String, List<? extends IGroup>> getAllGroupByUserId(String userId) {
        return null;
    }

    @Override
    public List<? extends IGroup> getGroupsByUserId(String userId) {
        return null;
    }

    @Override
    public List<? extends IGroup> getGroupsWithChildByUserId(String userId) {
        return null;
    }

    @Override
    public IGroup getById(String groupType, String groupId) {
        return null;
    }

    @Override
    public IGroup getByCode(String groupType, String code) {
        return null;
    }

    @Override
    public IGroup getMainGroup(String userId) {
        return null;
    }

    @Override
    public List<? extends IGroup> getRoleList(QueryFilter queryFilter) {
        return null;
    }
}
