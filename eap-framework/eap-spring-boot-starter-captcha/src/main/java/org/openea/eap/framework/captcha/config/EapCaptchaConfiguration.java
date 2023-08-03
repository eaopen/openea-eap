package org.openea.eap.framework.captcha.config;


import com.xingyuv.captcha.properties.AjCaptchaProperties;
import com.xingyuv.captcha.service.impl.CaptchaServiceFactory;
import org.openea.eap.framework.captcha.core.service.RedisCaptchaServiceImpl;
import com.xingyuv.captcha.service.CaptchaCacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@AutoConfiguration
public class EapCaptchaConfiguration {


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Bean
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties config) {
        // 缓存类型 redis/local/....
        CaptchaCacheService ret = CaptchaServiceFactory.getCache(config.getCacheType().name());
        if (ret instanceof RedisCaptchaServiceImpl) {
            ((RedisCaptchaServiceImpl) ret).setStringRedisTemplate(stringRedisTemplate);
        }
        return ret;
    }


}
