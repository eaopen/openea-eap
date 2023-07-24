package org.openea.eap.module.obpm.service.auth;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.enums.UserTypeEnum;
import org.openea.eap.framework.common.util.date.DateUtils;
import org.openea.eap.framework.security.core.LoginUser;
import org.openea.eap.framework.security.core.util.SecurityFrameworkUtils;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.openea.eap.module.obpm.service.obpm.ObpmUtil;
import org.openea.eap.module.system.api.social.dto.SocialUserBindReqDTO;
import org.openea.eap.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import org.openea.eap.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import org.openea.eap.module.system.convert.auth.AuthConvert;
import org.openea.eap.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.logger.LoginLogTypeEnum;
import org.openea.eap.module.system.enums.logger.LoginResultEnum;
import org.openea.eap.module.system.enums.oauth2.OAuth2ClientConstants;
import org.openea.eap.module.system.service.auth.AdminAuthService;
import org.openea.eap.module.system.service.auth.AdminAuthServiceImpl;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_BAD_CREDENTIALS;


@Service("obpmAuthService")
@ConditionalOnProperty(prefix = "eap", name = "userDataType", havingValue = "obpm")
@Slf4j
public class ObpmAuthServiceImpl extends AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private ObmpClientService obmpClientService;


    @Override
    public AuthLoginRespVO login(AuthLoginReqVO reqVO, HttpServletRequest request){
        // 校验验证码
        validateCaptcha(reqVO);

        // 使用账号密码，进行登录
        AdminUserDO user = authenticate(reqVO.getUsername(), reqVO.getPassword(), request);

        // 如果 socialType 非空，说明需要绑定社交用户
        if (reqVO.getSocialType() != null) {
            socialUserService.bindSocialUser(new SocialUserBindReqDTO(user.getId(), getUserType().getValue(),
                    reqVO.getSocialType(), reqVO.getSocialCode(), reqVO.getSocialState()));
        }
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user.getId(), reqVO.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME);
    }

    public AdminUserDO authenticate(String username, String password, HttpServletRequest request) {

        // 校验账号是否存在
        AdminUserDO user = userService.getUserByUsername(username);
        // 从obpm验证
        JSONObject jsonResult = obpmLogin(username, password);
        if(jsonResult.containsKey("user")){
            // login success
            // obpm/eap 同用一个token？
            String obpmToken = jsonResult.getString("token");
            JSONObject obpmUser = jsonResult.getJSONObject("user");
            if (user == null) {
                // 新增用户
                user = new AdminUserDO();
                user.setUsername(username);
                user.setId(obpmUser.getLong("id"));
            }
            // login user
            LoginUser loginUser = new LoginUser();
            loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
            loginUser.setId(user.getId());
            loginUser.setUserKey(username);
            SecurityFrameworkUtils.setLoginUser(loginUser, request);

        }else{
            // fail
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS, jsonResult.getString("msg"));
        }
        return user;
    }

    @Override
    protected AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);

        OAuth2AccessTokenDO accessTokenDO = null;
        // 检查accessToken是否已存在
        String accessToken = ObpmUtil.getObpmToken();
        if(ObjectUtils.isNotEmpty(accessToken)){
            accessTokenDO = oauth2TokenService.getAccessToken(accessToken);
            if(accessTokenDO!=null && DateUtils.isExpired(accessTokenDO.getExpiresTime())){
                // 已过期放弃重新生成
                accessTokenDO = null;
            }
        }
        // 创建访问令牌
        if(accessTokenDO==null) {
            accessTokenDO = oauth2TokenService.createAccessToken(userId, getUserType().getValue(),
                    OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        }
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    private JSONObject obpmLogin(String username, String password){
        return obmpClientService.login(username, password);
    }

}
