package org.openea.cloud.oauth.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Service("oauthUserDetailsService")
public class CustJdbcUserDetailsService extends JdbcDaoImpl {

    @Value("${eap-oauth.cust-users-by-username-query:select username,password,enabled from users where username = ?}")
    private String custUsersByUsernameQuery;

    private void checkUsersByUsernameQuery(){
        if(this.custUsersByUsernameQuery!=null && !"".equals(this.custUsersByUsernameQuery)
                && !this.getUsersByUsernameQuery().equals(this.custUsersByUsernameQuery)){
            this.setUsersByUsernameQuery(this.custUsersByUsernameQuery);
        }
    }

    private List<GrantedAuthority> defaultAuthorities;

    public CustJdbcUserDetailsService(@Autowired DataSource dataSource){
        this.setDataSource(dataSource);

        defaultAuthorities = new ArrayList<GrantedAuthority>();
        defaultAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        checkUsersByUsernameQuery();
        return super.loadUsersByUsername(username);
    }

    @Override
    protected List<GrantedAuthority> loadUserAuthorities(String username) {
        return defaultAuthorities;
    }

    @Override
    protected List<GrantedAuthority> loadGroupAuthorities(String username) {
        return defaultAuthorities;
    }

}
