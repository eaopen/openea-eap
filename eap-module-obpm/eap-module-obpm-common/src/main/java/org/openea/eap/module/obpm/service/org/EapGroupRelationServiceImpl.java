package org.openea.eap.module.obpm.service.org;

import org.openbpm.org.api.model.IGroup;
import org.openbpm.org.api.service.GroupRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapGroupRelationServiceImpl implements GroupRelationService {
    @Override
    public List<? extends IGroup> getPostByGroupAndRoles(String orgIds, String roleKey) {
        return null;
    }

    @Override
    public List<? extends IGroup> getPostByGroupParentAndRoles(String orgIds, String roleKey) {
        return null;
    }

    @Override
    public List<? extends IGroup> getPostByGroupSpecicalTypeParentAndRoles(String orgIds, Integer parentOrgFilterType, String roleKey) {
        return null;
    }

    @Override
    public List<? extends IGroup> getPostByGroupSpecicalLevelParentAndRoles(String orgIds, Integer parentOrgSpecicalLevel, String roleKey) {
        return null;
    }

    @Override
    public List<? extends IGroup> getPostByGroupChildAndRoles(String orgIds, String roleKey) {
        return null;
    }

    @Override
    public List<? extends IGroup> getPostByGroupSpecicalTypeChildAndRoles(String orgIds, Integer parentOrgFilterType, String roleKey) {
        return null;
    }
}
