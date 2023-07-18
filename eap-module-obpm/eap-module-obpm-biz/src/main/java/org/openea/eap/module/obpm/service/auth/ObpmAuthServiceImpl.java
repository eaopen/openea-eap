package org.openea.eap.module.obpm.service.auth;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.logger.LoginLogTypeEnum;
import org.openea.eap.module.system.enums.logger.LoginResultEnum;
import org.openea.eap.module.system.service.auth.AdminAuthService;
import org.openea.eap.module.system.service.auth.AdminAuthServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_USER_DISABLED;

@Service("obpmAuthService")
@ConditionalOnProperty(prefix = "eap", name = "userDataType", havingValue = "obpm")
public class ObpmAuthServiceImpl extends AdminAuthServiceImpl implements AdminAuthService {


    @Override
    public AdminUserDO authenticate(String username, String password) {
        // TODO 更改为通过obpm的用户名密码进行验证

        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_USERNAME;
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (ObjectUtil.notEqual(user.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

}
