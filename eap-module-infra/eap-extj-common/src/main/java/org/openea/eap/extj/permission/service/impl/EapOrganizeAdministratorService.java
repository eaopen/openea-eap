package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.OrganizeAdministratorEntity;
import org.openea.eap.extj.permission.service.OrganizeAdministratorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapOrganizeAdministratorService implements OrganizeAdministratorService {
    @Override
    public List<OrganizeAdministratorEntity> getListByUserID(String userId) {
        return null;
    }
}
