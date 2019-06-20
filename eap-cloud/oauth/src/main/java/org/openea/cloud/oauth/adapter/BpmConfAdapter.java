package org.openea.cloud.oauth.adapter;

import org.openbpm.base.core.cache.ICache;
import org.openbpm.base.core.cache.impl.MemoryCache;
import org.openbpm.security.jwt.service.JWTService;
import org.openbpm.security.login.UserDetailsServiceImpl;
import org.openea.cloud.oauth.config.ApplicationConfig;
import org.openea.cloud.oauth.config.EapAuthorizationServerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

//@ConditionalOnExpression("'${app.oauth-adapter}'.toLowerCase() == 'bpm'")
@Configuration
@AutoConfigureBefore(EapAuthorizationServerConfig.class)
@AutoConfigureAfter(ApplicationConfig.class)
public class BpmConfAdapter {

    //@ConditionalOnProperty(prefix = "app", name = "oauth-adapter", havingValue = "bpm")
    @Bean
    public UserDetailsService bpmUserDetailsService() {
        UserDetailsService userDetailsService = new org.openbpm.security.login.UserDetailsServiceImpl();
        return userDetailsService;
    }


    @Bean("JWTService")
    protected JWTService JWTService() {
        JWTService jWTService = new JWTService();
        return jWTService;
    }


    @Bean
    public ICache memoryCache() {
        return new MemoryCache();
    }
}
