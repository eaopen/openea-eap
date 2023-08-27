package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.OrganizeRelationEntity;
import org.openea.eap.extj.permission.service.OrganizeRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapOrganizeRelationService implements OrganizeRelationService {
    @Override
    public List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds, String objectType) {
        return null;
    }

    @Override
    public List<String> getPositionListByOrganizeId(List<String> organizeIds) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByRoleId(String roleId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByRoleIdList(List<String> roleId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByObjectIdAndType(String objectType, String objectId) {
        return null;
    }

    @Override
    public Boolean existByRoleIdAndOrgId(String roleId, String organizeId) {
        return null;
    }

    @Override
    public Boolean existByObjTypeAndOrgId(String objectType, String organizeId) {
        return null;
    }

    @Override
    public Boolean existByObjAndOrgId(String objectType, String objId, String organizeId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByType(String objectType) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getListByTypeAndOrgId(String objectType, String orgId) {
        return null;
    }

    @Override
    public Boolean deleteAllByRoleId(String roleId) {
        return null;
    }

    @Override
    public String autoGetMajorPositionId(String userId, String changeToMajorOrgId, String currentMajorPosId) {
        return null;
    }

    @Override
    public String autoGetMajorOrganizeId(String userId, List<String> orgIds, String currentMajorOrgId) {
        return null;
    }

    @Override
    public void autoSetOrganize(List<String> userIds) {

    }

    @Override
    public void autoSetPosition(List<String> userIds) {

    }

    @Override
    public Boolean checkBasePermission(String userId, String orgId) {
        return null;
    }
}
