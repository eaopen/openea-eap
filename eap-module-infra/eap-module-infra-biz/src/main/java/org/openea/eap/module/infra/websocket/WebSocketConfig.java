package org.openea.eap.module.infra.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket 配置
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Slf4j
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        log.info("load bean ServerEndpointExporter");
        return new ServerEndpointExporter();
    }
}
