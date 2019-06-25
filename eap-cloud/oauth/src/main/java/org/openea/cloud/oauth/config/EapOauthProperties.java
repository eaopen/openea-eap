package org.openea.cloud.oauth.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ConfigurationProperties(
        prefix = "eap-oauth"
)
@Configuration
public class EapOauthProperties {


    private String oauthAdapter;  //bpm

    private String custUsersByUsernameQuery;
    private String custPwdEncoder;

    private String loginProcessUrl = "/auth/authorize";
    private String loginPageTitle = "/auth/authorize";

    @NestedConfigurationProperty
    private Auth auth;

    @NestedConfigurationProperty
    private List<OAuth2ClientProperties> clients;

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
