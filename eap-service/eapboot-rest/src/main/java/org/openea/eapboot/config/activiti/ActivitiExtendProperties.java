package org.openea.eapboot.config.activiti;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 */
@Data
@ConfigurationProperties(prefix = "spring.activiti.eapboot")
public class ActivitiExtendProperties {

    /**
     * 流程图字体配置
     */
    private String activityFontName = "宋体";

    /**
     * 流程图字体配置
     */
    private String labelFontName = "宋体";
}
