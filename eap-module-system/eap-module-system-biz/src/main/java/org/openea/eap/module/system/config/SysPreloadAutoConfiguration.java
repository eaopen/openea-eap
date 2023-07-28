package org.openea.eap.module.system.config;

import org.openea.eap.framework.security.config.EapSecurityAutoConfiguration;
import org.openea.eap.module.system.service.auth.AdminAuthService;
import org.openea.eap.module.system.service.auth.AdminAuthServiceImpl;
import org.openea.eap.module.system.service.oauth2.OAuth2TokenService;
import org.openea.eap.module.system.service.oauth2.OAuth2TokenServiceImpl;
import org.openea.eap.module.system.service.permission.PermissionService;
import org.openea.eap.module.system.service.permission.PermissionServiceImpl;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.openea.eap.module.system.service.user.AdminUserServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(EapSecurityAutoConfiguration.class)
public class SysPreloadAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public AdminUserService adminUserService(){
        return new AdminUserServiceImpl();
    }

    @ConditionalOnMissingBean
    @Bean
    public AdminAuthService adminAuthService(){
        return new AdminAuthServiceImpl();
    }

    @ConditionalOnMissingBean
    @Bean
    public PermissionService permissionService(){
        return new PermissionServiceImpl();
    }


}
