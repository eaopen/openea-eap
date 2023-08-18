package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * todo eap
 */
@Service
public class EapUserService implements UserService {

    @Resource
    private AdminUserService adminUserService;

    private UserEntity covertUser(AdminUserDO adminUser){
        UserEntity user = new UserEntity();
        user.setId(""+adminUser.getId());
        user.setAccount(adminUser.getUsername());
        user.setRealName(adminUser.getNickname());
        user.setHeadIcon(adminUser.getAvatar());
        user.setEnabledMark(1);
        return user;
    }

    @Override
    public UserEntity getInfo(String userId) {
        AdminUserDO adminUserDO = adminUserService.getUser(new Long(userId));
        return covertUser(adminUserDO);
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
