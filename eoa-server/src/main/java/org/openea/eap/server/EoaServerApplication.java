package org.openea.eap.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${eap.info.base-package}
@SpringBootApplication(scanBasePackages = {"org.openea.eap","${eap.info.base-package}.server", "${eap.info.base-package}.module"})
public class EoaServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(EoaServerApplication.class, args);
//        new SpringApplicationBuilder(EoaServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

        // 参考yudao
        // 如果你碰到启动的问题，请认真阅读 https://doc.iocoder.cn/quick-start/ 文章
    }

}
