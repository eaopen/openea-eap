package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EapOrganizeService implements OrganizeService {
    @Override
    public List<String> getUnderOrganizations(String orgId) {
        return null;
    }

    @Override
    public Collection<Object> listByIds(List<String> orgIds) {
        return null;
    }

    @Override
    public OrganizeEntity getInfo(String organizeId) {
        return null;
    }

    @Override
    public List<OrganizeEntity> getOrgRedisList() {
        return null;
    }

    @Override
    public Map<String, Object> getOrgMap() {
        return null;
    }

    @Override
    public List<OrganizeEntity> getOrgEntityList(Set<String> orgList) {
        return null;
    }

    @Override
    public Map<String, Object> getOrgNameAndId(String s) {
        return null;
    }

    @Override
    public Map<String, Object> getOrgEncodeAndName(String department) {
        return null;
    }
}
