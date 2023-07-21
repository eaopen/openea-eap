package org.openea.eap.module.system.api.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OAuth2.0 访问令牌的校验 Response DTO
 *
 */
@Data
public class OAuth2AccessTokenCheckRespDTO implements Serializable {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userKey;

    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 授权范围的数组
     */
    private List<String> scopes;

}
