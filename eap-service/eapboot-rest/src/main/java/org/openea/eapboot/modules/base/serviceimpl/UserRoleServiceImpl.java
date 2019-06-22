package org.openea.eapboot.modules.base.serviceimpl;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.modules.base.dao.UserDao;
import org.openea.eapboot.modules.base.dao.UserRoleDao;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.entity.UserRole;
import org.openea.eapboot.modules.base.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色接口实现
 */
@Slf4j
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserDao userDao;

    @Override
    public UserRoleDao getRepository() {
        return userRoleDao;
    }

    @Override
    public List<UserRole> findByRoleId(String roleId) {
        return userRoleDao.findByRoleId(roleId);
    }

    @Override
    public List<User> findUserByRoleId(String roleId) {

        List<UserRole> userRoleList = userRoleDao.findByRoleId(roleId);
        List<User> list = new ArrayList<>();
        for(UserRole ur : userRoleList){
            User u = userDao.getOne(ur.getUserId());
            if(u!=null&&CommonConstant.USER_STATUS_NORMAL.equals(u.getStatus())){
                list.add(u);
            }
        }
        return list;
    }

    @Override
    public void deleteByUserId(String userId) {
        userRoleDao.deleteByUserId(userId);
    }
}
