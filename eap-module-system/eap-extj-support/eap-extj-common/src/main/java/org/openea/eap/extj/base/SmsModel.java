package org.openea.eap.extj.base;

import lombok.Data;

@Data
public class SmsModel {

    private String aliAccessKey;
    private String aliSecret;
    private String tencentSecretId;
    private String tencentSecretKey;
    private String tencentAppId;
    private String tencentAppKey;

}
