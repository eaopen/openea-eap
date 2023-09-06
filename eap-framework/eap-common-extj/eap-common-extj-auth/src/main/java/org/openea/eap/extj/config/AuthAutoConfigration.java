package org.openea.eap.extj.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @deprecated
 */
@Configuration
@EnableConfigurationProperties(JnpfOauthConfig.class)
public class AuthAutoConfigration {


    @Primary
    @Bean
    @ConfigurationProperties(prefix = "oauth.login")
    public SaTokenConfig getJnpfTokenConfig() {
        return new JnpfTokenConfig();
    }

    @Primary
    @Bean
    public StpLogic getJnpfTokenJwtLogic() {
        return new StpLogicJwtForSimple();
    }
}
