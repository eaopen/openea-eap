package org.openea.eap.module.obpm.service.org;

import org.openbpm.org.api.model.IUser;
import org.openbpm.org.api.model.IUserRole;
import org.openbpm.org.api.service.UserService;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
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
        Long eapUserId = OrgConvertUtil.convertUserId(userId);
        AdminUserDO adminUserDO = adminUserService.getUser(eapUserId);
        return OrgConvertUtil.convertUser(adminUserDO);
    }

    @Override
    public IUser getUserByAccount(String account) {
        AdminUserDO adminUserDO = adminUserService.getUserByUsername(account);
        return OrgConvertUtil.convertUser(adminUserDO);
    }

    @Override
    public List<? extends IUser> getUserListByGroup(String groupType, String groupId) {

        //adminUserService.getUserListByDeptIds()
        //adminUserService.getUserListByPostIds()

        return null;
    }

    @Override
    public List<? extends IUserRole> getUserRole(String userId) {
        return null;
    }

    @Override
    public List<? extends IUser> getAllUser() {
        List<AdminUserDO> listUser =  adminUserService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return OrgConvertUtil.convertUsers(listUser);
    }

    @Override
    public List<? extends IUser> getUserListByGroupPath(String path) {
        return null;
    }
}
