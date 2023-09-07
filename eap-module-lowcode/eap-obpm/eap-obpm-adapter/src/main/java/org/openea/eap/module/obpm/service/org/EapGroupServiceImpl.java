package org.openea.eap.module.obpm.service.org;

import org.openbpm.base.api.query.QueryFilter;
import org.openbpm.org.api.model.IGroup;
import org.openbpm.org.api.service.GroupService;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "eap.obpm", name = "eap-adapter", havingValue = "true")
public class EapGroupServiceImpl implements GroupService {

    @Resource
    protected AdminUserService adminUserService;

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
        AdminUserDO userDO = adminUserService.getUser(OrgConvertUtil.convertUserId(userId));
        //userDO.getDeptId();
        return null;
    }

    @Override
    public List<? extends IGroup> getRoleList(QueryFilter queryFilter) {
        return null;
    }
}
