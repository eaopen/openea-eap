package org.openea.eap.extj.model;

import lombok.Data;

import javax.websocket.Session;

/**
 *
 *
 */
@Data
public class OnlineUserModel {
    /**
     *  连接Id
     */
    private String connectionId;
    /**
     *  用户Id
     */
    private String userId;
    /**
     *  租户Id
     */
    private String tenantId;

    public String getTenantId() {
        return tenantId = tenantId == null ? "" : tenantId;
    }

    /**
     *  移动端
     */
    private Boolean isMobileDevice;
    /**
     * session
     */
    private String token;

    /**
     * session
     */
    private Session webSocket;
}
