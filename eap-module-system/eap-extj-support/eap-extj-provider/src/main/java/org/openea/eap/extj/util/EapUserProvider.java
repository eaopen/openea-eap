package org.openea.eap.extj.util;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.framework.common.util.spring.EapAppUtil;
import org.openea.eap.module.system.api.oauth2.OAuth2TokenApi;
import org.openea.eap.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * todo auth
 */

@Component("userProvider")
public class EapUserProvider extends UserProvider {
    private static final Logger log = LoggerFactory.getLogger(EapUserProvider.class);

    public EapUserProvider(RedisUtil redisUtil, CacheKeyUtil cacheKeyUtil) {
        super(redisUtil, cacheKeyUtil);
    }


    public static UserInfo getUser(String token) {
        // 参考 TokenAuthenticationFilter
        UserInfo userInfo = new UserInfo();
        if(ObjectUtil.isNotEmpty(token)){
            try{
                OAuth2TokenApi oauth2TokenApi = (OAuth2TokenApi) EapAppUtil.getBean("oauth2TokenApi");
                OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token);
                if(accessToken!=null){
                    userInfo.setId(""+accessToken.getUserId());
                    userInfo.setUserId(""+accessToken.getUserId());
                    userInfo.setUserAccount(accessToken.getUserKey());
                }
            }catch (Exception e){
                log.warn(e.getMessage());
            }

        }
        if (userInfo.getUserId() != null) {
            USER_CACHE.set(userInfo);
        }
        return userInfo;
    }

}