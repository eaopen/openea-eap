package org.openea.eap.module.obpm.service.auth;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.framework.tenant.core.context.TenantContextHolder;
import org.openea.eap.module.obpm.service.obpm.ObpmUtil;
import org.openea.eap.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.openea.eap.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import org.openea.eap.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import org.openea.eap.module.system.service.oauth2.OAuth2TokenService;
import org.openea.eap.module.system.service.oauth2.OAuth2TokenServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(prefix = "eap", name = "enableOpenBpm", havingValue = "true")
public class ObpmOAuth2TokenServiceImpl extends OAuth2TokenServiceImpl implements OAuth2TokenService {
    @Override
    protected OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        String accessToken = generateAccessToken();
        // 首先检查accessToken是否已存在，集成有重复可能性
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if(accessTokenDO!=null){
            accessTokenDO.setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
            oauth2AccessTokenMapper.updateById(accessTokenDO);
        }else {
            accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(accessToken)
                    .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                    .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                    .setRefreshToken(refreshTokenDO.getRefreshToken())
                    .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
            accessTokenDO.setTenantId(TenantContextHolder.getTenantId()); // 手动设置租户编号，避免缓存到 Redis 的时候，无对应的租户编号
            oauth2AccessTokenMapper.insert(accessTokenDO);
        }
        // 记录到 Redis 中
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    protected static String generateAccessToken() {
        // 优先使用obpm登录获取的token
        String accessToken = ObpmUtil.getObpmToken();
        if(ObjectUtil.isEmpty(accessToken)){
            accessToken = IdUtil.fastSimpleUUID();
        }
        return accessToken;
    }
}
