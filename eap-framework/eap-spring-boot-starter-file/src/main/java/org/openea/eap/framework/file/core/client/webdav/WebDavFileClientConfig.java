package org.openea.eap.framework.file.core.client.webdav;

import lombok.Data;
import org.openea.eap.framework.file.core.client.FileClientConfig;

@Data
public class WebDavFileClientConfig implements FileClientConfig {

    private String server;
    private String user;
    private String password;
    private String platform;
    private String domain;
    private String basePath;


}
