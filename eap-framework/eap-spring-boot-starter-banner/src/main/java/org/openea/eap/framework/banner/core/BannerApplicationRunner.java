package org.openea.eap.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "接口文档: \t{} \n\t" +
                            "开发文档: \t{} \n\t" +
                            "----------------------------------------------------------",
                    "https://doc.iocoder.cn/api-doc/",
                    "https://doc.iocoder.cn");

            // 数据报表
//            if (isNotPresent("org.openea.eap.module.report.framework.security.config.SecurityConfiguration")) {
//                System.out.println("[报表模块 eap-module-report - 已禁用]");
//            }
            // 工作流
            // flowable: org.openea.eap.framework.flowable.config.EapFlowableConfiguration
            // obpm: OpenBpm based on Activiti
            if (isNotPresent("org.openea.eap.module.obpm.config.EapObpmConfiguration")) {
                System.out.println("[工作流模块 eap-module-obpm - 已禁用]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
