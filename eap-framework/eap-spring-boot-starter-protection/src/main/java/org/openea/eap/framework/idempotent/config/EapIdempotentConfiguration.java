package org.openea.eap.framework.idempotent.config;

import org.openea.eap.framework.idempotent.core.aop.IdempotentAspect;
import org.openea.eap.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import org.openea.eap.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import org.openea.eap.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.openea.eap.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.openea.eap.framework.redis.config.EapRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = EapRedisAutoConfiguration.class)
public class EapIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}
