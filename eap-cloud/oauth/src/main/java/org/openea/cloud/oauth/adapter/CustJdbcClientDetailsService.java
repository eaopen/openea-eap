package org.openea.cloud.oauth.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

//@Service("oauthClientDetailsService")
public class CustJdbcClientDetailsService extends JdbcClientDetailsService {
    public CustJdbcClientDetailsService(@Autowired DataSource dataSource) {
        super(dataSource);
    }
}
