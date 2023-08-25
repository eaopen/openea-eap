package org.openea.eap.module.obpm.service.org;

import org.openbpm.org.api.model.IUser;
import org.openbpm.org.api.model.IUserRole;
import org.openbpm.org.api.service.UserService;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "eap.obpm", name = "eap-adapter", havingValue = "true")
public class EapUserServiceImpl implements UserService{

    @Resource
    protected AdminUserService adminUserService;

    @Override
    public IUser getUserById(String userId) {
        return null;
    }

    @Override
    public IUser getUserByAccount(String account) {
        return null;
    }

    @Override
    public List<? extends IUser> getUserListByGroup(String groupType, String groupId) {
        return null;
    }

    @Override
    public List<? extends IUserRole> getUserRole(String userId) {
        return null;
    }

    @Override
    public List<? extends IUser> getAllUser() {
        return null;
    }

    @Override
    public List<? extends IUser> getUserListByGroupPath(String path) {
        return null;
    }
}
