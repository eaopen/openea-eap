package org.openea.eap.module.obpm.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

@Configurable
public class EapObpmConfiguration {

    @Value("${eap.obpm.apiBaseUrl:/obpm-api}")
    private String obpmApiBaseUrl;

//    @Value("${eap.obpm.webBaseUrl:/obpm}")
//    private String obpmWebBaseUrl;
//
//    @Value("${eap.obpm.adminBaseUrl:/obpm}")
//    private String obpmAdminBaseUrl;
}
