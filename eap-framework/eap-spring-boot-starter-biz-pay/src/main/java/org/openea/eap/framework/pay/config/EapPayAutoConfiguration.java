package org.openea.eap.framework.pay.config;

import org.openea.eap.framework.pay.core.client.PayClientFactory;
import org.openea.eap.framework.pay.core.client.impl.PayClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 支付配置类
 *
 */
@AutoConfiguration
@EnableConfigurationProperties(PayProperties.class)
public class EapPayAutoConfiguration {

    @Bean
    public PayClientFactory payClientFactory() {
        return new PayClientFactoryImpl();
    }

}
