package org.openea.eap.cloud.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("app")
@Configuration
public class ApplicationProperties {

    private Auth auth;

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
