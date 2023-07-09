package org.openea.eap.module.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类

 */
@SpringBootApplication
public class InfraServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(InfraServerApplication.class, args);

        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
    }

}
