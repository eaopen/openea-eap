package org.openea.eap.extj.permission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * todo eap
 */
@Service
@Slf4j
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
        try{
            AdminUserDO adminUserDO = adminUserService.getUser(new Long(userId));
            return covertUser(adminUserDO);
        }catch (Throwable t){
            log.warn(t.getMessage());
        }
        return null;
    }

    @Override
    public List<UserEntity> getUserName(List<String> collect) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getUserIdList(List<String> userIds) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getListId() {
        return  Collections.emptyList();
    }

    @Override
    public void updateById(UserEntity update) {

    }

    @Override
    public Map<String, Object> getUserMap() {
        return null;
    }

    @Override
    public List<UserEntity> getUserNameList(Set<String> userList) {
        return  Collections.emptyList();
    }

    @Override
    public Map<String, Object> getUserNameAndIdMap() {
        return null;
    }
}
