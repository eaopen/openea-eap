package org.openea.eap.module.obpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        excludeName = {
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"}
)
@ComponentScan(basePackages={"org.openbpm.*","org.openea.eap.module.obpm.*"})
public class ObpmServer {
    public static void main(String[] args) {
        SpringApplication.run(ObpmServer.class, args);
    }
}
