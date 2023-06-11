package org.openea.eap.framework.file.config;

import org.openea.eap.framework.file.core.client.FileClientFactory;
import org.openea.eap.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 文件配置类
 *
 */
@AutoConfiguration
public class EapFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
