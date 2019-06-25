package org.openea.cloud.oauth.config;

import org.openea.cloud.oauth.token.AuthTokenEnhancer;
import org.openea.cloud.oauth.token.CustomClaimAccessTokenConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;


@Configuration
public class EapAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsService oauthUserDetailsService;

    @Autowired
    private ClientDetailsService oauthClientDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EapOauthProperties eapOauthProperties;

    @Autowired
    private AuthTokenEnhancer authTokenEnhancer;

    @Autowired
    private CustomClaimAccessTokenConverter customClaimAccessTokenConverter;

    @Autowired
    private AuthorizationServerTokenServices defaultTokenServices;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
        configurer
                .authenticationManager(authenticationManager)
                .userDetailsService(oauthUserDetailsService)
                .tokenServices(defaultTokenServices)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter());

        // set RedirectResolver
        configurer.redirectResolver(new RedirectResolver(){
            @Override
            public String resolveRedirect(String requestedRedirect, ClientDetails clientDetails) throws OAuth2Exception {
                return requestedRedirect;
            }
        });
        // set requestValidator
        configurer.requestValidator(new OAuth2RequestValidator() {
            @Override
            public void validateScope(AuthorizationRequest authorizationRequest, ClientDetails clientDetails) throws InvalidScopeException {

            }

            @Override
            public void validateScope(TokenRequest tokenRequest, ClientDetails clientDetails) throws InvalidScopeException {

            }
        });
        configurer.pathMapping("/oauth/confirm_access","/custom/confirm_access");
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(oauthClientDetailsService);
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").passwordEncoder(passwordEncoder);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(eapOauthProperties.getAuth().getKfName()),
                eapOauthProperties.getAuth().getKsPass().toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("demo"));
        converter.setAccessTokenConverter(customClaimAccessTokenConverter);
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(authTokenEnhancer, accessTokenConverter()));
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);

        defaultTokenServices.setReuseRefreshToken(false);
        defaultTokenServices.setAccessTokenValiditySeconds(eapOauthProperties.getAuth().getDefaultAccessTokenTimeout());
        defaultTokenServices.setRefreshTokenValiditySeconds(eapOauthProperties.getAuth().getDefaultRefreshTokenTimeout());
        defaultTokenServices.setClientDetailsService(oauthClientDetailsService);

        defaultTokenServices.setAuthenticationManager(authenticationManager);

        return defaultTokenServices;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new AuthenticationManager(){
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                UserDetails userDetails = oauthUserDetailsService.loadUserByUsername(authentication.getName());

                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                        authentication.getName(), authentication.getCredentials(), userDetails.getAuthorities());
                token.setDetails(userDetails);
                return token;
            }
        };
    }

    @Bean
    @Primary
    @ConditionalOnExpression("'${eap-oauth.cust-pwd-encoder}'=='BCryptPasswordEncoder'")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
