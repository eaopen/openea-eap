package org.openea.cloud.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan({"org.openea.cloud.oauth.**","org.openbpm.**"})
@EnableAuthorizationServer
@Configuration
@EnableTransactionManagement
@SpringBootApplication
public class EapOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EapOauthApplication.class, args);
    }

}
