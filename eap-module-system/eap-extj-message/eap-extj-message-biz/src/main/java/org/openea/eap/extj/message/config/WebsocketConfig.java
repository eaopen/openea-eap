package org.openea.eap.extj.message.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * see org.openea.eap.module.infra.websocket.WebSocketConfig
 */
//@Configuration
//@ComponentScan
//@EnableAutoConfiguration
public class WebsocketConfig {

    /**
     * 支持websocket
     * 如果不使用内置tomcat，则无需配置
     *
     * @return
     */
    //@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
