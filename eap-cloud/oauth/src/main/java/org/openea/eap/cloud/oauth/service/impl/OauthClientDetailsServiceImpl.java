package org.openea.eap.cloud.oauth.service.impl;

import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

public class OauthClientDetailsServiceImpl extends JdbcClientDetailsService {
    public OauthClientDetailsServiceImpl(DataSource dataSource) {
        super(dataSource);
        this.setSelectClientDetailsSql("select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove from GE_OAUTH_CLIENT_DETAILS where client_id = ?");
    }
}
