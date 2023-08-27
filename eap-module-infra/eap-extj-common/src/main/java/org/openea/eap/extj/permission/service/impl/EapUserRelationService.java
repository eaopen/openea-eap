package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.permission.service.UserRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapUserRelationService implements UserRelationService {
    @Override
    public void syncDingUserRelation(String sysObjId, List<Long> deptIdList) {

    }

    @Override
    public List<UserRelationEntity> getListByObjectIdAll(List<String> objectId) {
        return null;
    }

    @Override
    public List<UserRelationEntity> getListByUserId(String userId, String department) {
        return null;
    }
}
