package org.openea.eap.extj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * todo eap
 */
@Component
@Data
public class JnpfOauthConfig {

    @Value("${config.ApiDomain}")
    private String jnpfDomain;
    @Value("${config.EnablePreAuth:false}")
    private Boolean enablePreAuth;
    private Boolean ssoEnabled = false;
    private String loginPath;
    private String defaultSSO = "cas";
    private long ticketTimeout = 60L;

    @Value("${config.FrontDomain}")
    private String jnpfFrontDomain;
    @Value("${config.AppDomain}")
    private String jnpfAppDomain;
}
