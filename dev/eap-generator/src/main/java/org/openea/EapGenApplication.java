package org.openea;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.openea.dao")
public class EapGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(EapGenApplication.class, args);
	}
}
