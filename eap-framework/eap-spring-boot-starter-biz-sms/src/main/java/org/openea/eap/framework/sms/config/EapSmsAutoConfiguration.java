package org.openea.eap.framework.sms.config;

import org.openea.eap.framework.sms.core.client.SmsClientFactory;
import org.openea.eap.framework.sms.core.client.impl.SmsClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 短信配置类
 *
 */
@AutoConfiguration
public class EapSmsAutoConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new SmsClientFactoryImpl();
    }

}
