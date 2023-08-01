package org.openea.eap.module.obpm.service.org;

import org.openbpm.org.api.model.system.ISubsystem;
import org.openbpm.org.api.model.system.ISysResource;
import org.openbpm.org.api.service.SysResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@ConditionalOnProperty(prefix = "eap.obpm", name = "eap-adapter", havingValue = "true")
public class EapSysResourceServiceImpl implements SysResourceService {
    @Override
    public List<ISubsystem> getCurrentUserSystem() {
        return null;
    }

    @Override
    public ISubsystem getDefaultSystem(String currentUserId) {
        return null;
    }

    @Override
    public List<ISysResource> getBySystemId(String systemId) {
        return null;
    }

    @Override
    public List<ISysResource> getBySystemAndUser(String systemId, String userId) {
        return null;
    }

    @Override
    public Set<String> getAccessRoleByUrl(String url) {
        return null;
    }
}
