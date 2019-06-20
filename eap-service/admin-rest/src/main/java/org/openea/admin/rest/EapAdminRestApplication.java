package org.openea.admin.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan({"org.openea.admin.**","org.openbpm.**"})
@SpringBootApplication
public class EapAdminRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(EapAdminRestApplication.class, args);
    }
    
    

}
