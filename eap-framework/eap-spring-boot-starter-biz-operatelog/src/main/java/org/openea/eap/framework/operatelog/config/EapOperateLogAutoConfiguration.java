package org.openea.eap.framework.operatelog.config;

import org.openea.eap.framework.operatelog.core.aop.OperateLogAspect;
import org.openea.eap.framework.operatelog.core.service.OperateLogFrameworkService;
import org.openea.eap.framework.operatelog.core.service.OperateLogFrameworkServiceImpl;
import org.openea.eap.module.system.api.logger.OperateLogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class EapOperateLogAutoConfiguration {

    @Bean
    public OperateLogAspect operateLogAspect() {
        return new OperateLogAspect();
    }

    @Bean
    public OperateLogFrameworkService operateLogFrameworkService(OperateLogApi operateLogApi) {
        return new OperateLogFrameworkServiceImpl(operateLogApi);
    }

}
