package org.openea.eap.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 */
@ConfigurationProperties(prefix = "eap.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "eap.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
