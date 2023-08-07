package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo eap
 */
@Service
public class EapUserService implements UserService {
    @Override
    public UserEntity getInfo(String userId) {
        return null;
    }

    @Override
    public List<UserEntity> getUserName(List<String> collect) {
        return null;
    }

    @Override
    public List<String> getUserIdList(List<String> userIds) {
        return null;
    }

    @Override
    public List<String> getListId() {
        return null;
    }

    @Override
    public void updateById(UserEntity update) {

    }
}
