package org.openea.eapboot.config.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ActivitiExtendProperties.class)
public class ActivitiConfig {

    @Autowired
    private ActivitiExtendProperties properties;

    @Bean
    public ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer(){

        ProcessEngineConfigurationConfigurer configurer = new ProcessEngineConfigurationConfigurer() {
            @Override
            public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {

                processEngineConfiguration.setActivityFontName(properties.getActivityFontName());
                processEngineConfiguration.setAnnotationFontName(properties.getActivityFontName());
                processEngineConfiguration.setLabelFontName(properties.getLabelFontName());
            }
        };

        return configurer;
    }
}
