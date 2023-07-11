package org.openea.eap.framework.i18n.config;

import org.openea.eap.framework.i18n.core.EapMessageResource;
import org.openea.eap.framework.i18n.core.I18nUtil;
import org.openea.eap.module.system.api.i18n.I18nDataApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class EapI18nAutoConfiguration {

    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public I18nUtil i18nUtil(I18nDataApi i18nDataApi, EapMessageResource messageResource){
        I18nUtil.init(i18nDataApi, messageResource);
        return new I18nUtil();
    }
}
