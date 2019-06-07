package org.openea.eap.cloud.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("app")
@Configuration
public class ApplicationProperties {
    private String ssoServerUrl;
    private String jwtPublicKeyUrl;
    private String jwtPublicKey;
}
