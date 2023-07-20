package org.openea.eap.module.obpm.service.auth;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.openea.eap.module.system.api.social.dto.SocialUserBindReqDTO;
import org.openea.eap.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import org.openea.eap.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.logger.LoginLogTypeEnum;
import org.openea.eap.module.system.enums.logger.LoginResultEnum;
import org.openea.eap.module.system.service.auth.AdminAuthService;
import org.openea.eap.module.system.service.auth.AdminAuthServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;


@Service("obpmAuthService")
@ConditionalOnProperty(prefix = "eap", name = "userDataType", havingValue = "obpm")
@Slf4j
public class ObpmAuthServiceImpl extends AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private ObmpClientService obmpClientService;
    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO) {
        // 校验验证码
        validateCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword());

        // 如果 socialType 非空，说明需要绑定社交用户
        if (reqVO.getSocialType() != null) {
            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        }
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    @Override
    public AdminUserDO authenticate(String username, String password) {
        // TODO 更改为通过obpm的用户名密码进行验证

        JSONObject jsonResult = obpmLogin(username, password);
        if(jsonResult.containsKey("token")){
            // login success
        }else{
            // fail
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        if (user == null) {
            //不存在先从obpm验证
        }
        // TODO 从obpm获取信息进行验证

        // 如之前不存在，则需要新增用户到本地

        return user;
    }

    private JSONObject obpmLogin(String username, String password){
        return obmpClientService.login(username, password);
    }

}
