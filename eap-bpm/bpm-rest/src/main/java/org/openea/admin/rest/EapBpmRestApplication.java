package org.openea.admin.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 *
 */
@ComponentScan({"org.openea.bpm.**","org.openbpm.**"})
@SpringBootApplication
public class EapBpmRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(EapBpmRestApplication.class, args);
    }
    
    

}
