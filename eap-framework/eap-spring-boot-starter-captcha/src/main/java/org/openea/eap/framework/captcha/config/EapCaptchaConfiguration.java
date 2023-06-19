package org.openea.eap.framework.captcha.config;

import cn.hutool.core.util.ClassUtil;
import com.xingyuv.captcha.properties.AjCaptchaProperties;
import com.xingyuv.captcha.service.impl.CaptchaServiceFactory;
import org.openea.eap.framework.captcha.core.enums.CaptchaRedisKeyConstants;
import org.openea.eap.framework.captcha.core.service.RedisCaptchaServiceImpl;
import com.xingyuv.captcha.service.CaptchaCacheService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@AutoConfiguration
public class EapCaptchaConfiguration {

    static {
        // 手动加载 Lock4jRedisKeyConstants 类，因为它不会被使用到
        // 如果不加载，会导致 Redis 监控，看到它的 Redis Key 枚举
        ClassUtil.loadClass(CaptchaRedisKeyConstants.class.getName());
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Bean
//    public CaptchaCacheService captchaCacheService(StringRedisTemplate stringRedisTemplate) {
//        return new RedisCaptchaServiceImpl(stringRedisTemplate);
//    }
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties config) {
        // 缓存类型 redis/local/....
        CaptchaCacheService ret = CaptchaServiceFactory.getCache(config.getCacheType().name());
        if (ret instanceof RedisCaptchaServiceImpl) {
            ((RedisCaptchaServiceImpl) ret).setStringRedisTemplate(stringRedisTemplate);
        }
        return ret;
    }


}
