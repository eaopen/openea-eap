package org.openea.eap.module.system.service.oauth2;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.controller.admin.oauth2.vo.client.OAuth2ClientCreateReqVO;
import org.openea.eap.module.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import org.openea.eap.module.system.controller.admin.oauth2.vo.client.OAuth2ClientUpdateReqVO;
import org.openea.eap.module.system.dal.dataobject.oauth2.OAuth2ClientDO;

import javax.validation.Valid;
import java.util.Collection;

/**
 * OAuth2.0 Client Service 接口
 *
 * 从功能上，和 JdbcClientDetailsService 的功能，提供客户端的操作
 *
 */
public interface OAuth2ClientService {

    /**
     * 创建 OAuth2 客户端
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOAuth2Client(@Valid OAuth2ClientCreateReqVO createReqVO);

    /**
     * 更新 OAuth2 客户端
     *
     * @param updateReqVO 更新信息
     */
    void updateOAuth2Client(@Valid OAuth2ClientUpdateReqVO updateReqVO);

    /**
     * 删除 OAuth2 客户端
     *
     * @param id 编号
     */
    void deleteOAuth2Client(Long id);

    /**
     * 获得 OAuth2 客户端
     *
     * @param id 编号
     * @return OAuth2 客户端
     */
    OAuth2ClientDO getOAuth2Client(Long id);

    /**
     * 获得 OAuth2 客户端，从缓存中
     *
     * @param clientId 客户端编号
     * @return OAuth2 客户端
     */
    OAuth2ClientDO getOAuth2ClientFromCache(String clientId);

    /**
     * 获得 OAuth2 客户端分页
     *
     * @param pageReqVO 分页查询
     * @return OAuth2 客户端分页
     */
    PageResult<OAuth2ClientDO> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO);

    /**
     * 从缓存中，校验客户端是否合法
     *
     * @return 客户端
     */
    default OAuth2ClientDO validOAuthClientFromCache(String clientId) {
        return validOAuthClientFromCache(clientId, null, null, null, null);
    }

    /**
     * 从缓存中，校验客户端是否合法
     *
     * 非空时，进行校验
     *
     * @param clientId 客户端编号
     * @param clientSecret 客户端密钥
     * @param authorizedGrantType 授权方式
     * @param scopes 授权范围
     * @param redirectUri 重定向地址
     * @return 客户端
     */
    OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                             Collection<String> scopes, String redirectUri);
}
