package org.openea.cloud.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(
        prefix = "eap-oauth"
)
@Configuration
public class EapOauthProperties {

    @NestedConfigurationProperty
    private Auth auth;
    private String oauthAdapter;  //bpm

    private String custUsersByUsernameQuery;
    private String custPwdEncoder;

    private String loginProcessUrl = "/auth/authorize";
    private String loginPageTitle = "/auth/authorize";

    @Data
    public static class Auth {

        private String resourceId;

        private String kfName;

        private String ksPass;

        private Integer defaultAccessTokenTimeout;

        private Integer defaultRefreshTokenTimeout;

        private Integer failedLoginAttemptAccountLockTimeout;

        private Integer maxFailedLoginAttemptsForAccountLock;

    }
}
