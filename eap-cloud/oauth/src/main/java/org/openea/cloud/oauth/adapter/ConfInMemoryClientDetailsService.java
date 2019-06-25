package org.openea.cloud.oauth.adapter;

import org.openea.cloud.oauth.config.EapOauthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("oauthClientDetailsService")
public class ConfInMemoryClientDetailsService
        // extends InMemoryClientDetailsService
        implements ClientDetailsService {

    @Autowired
    private EapOauthProperties eapOauthProps;

    private void initClient(){
        if(clientDetailsStore!=null && !clientDetailsStore.isEmpty()){
            return;
        }
        clientDetailsStore = new HashMap();

        List authorizedGrantTypes = Arrays.asList("authorization_code", "password", "client_credentials", "implicit", "refresh_token");
        List authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        Set registeredRedirectUri = Collections.emptySet();
        for(OAuth2ClientProperties props: eapOauthProps.getClients()){
            BaseClientDetails details = new BaseClientDetails();
            if (props.getClientId() == null) {
                props.setClientId(UUID.randomUUID().toString());
            }
            details.setClientId(props.getClientId());
            details.setClientSecret(props.getClientSecret());
            details.setAuthorizedGrantTypes(authorizedGrantTypes);
            details.setAuthorities(authorities);
            details.setRegisteredRedirectUri(registeredRedirectUri);

            clientDetailsStore.put(details.getClientId(), details);
        }
    }


    public ConfInMemoryClientDetailsService() {
    }

    private Map<String, ClientDetails> clientDetailsStore;



    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        initClient();
        ClientDetails details = (ClientDetails)this.clientDetailsStore.get(clientId);
        if (details == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        } else {
            return details;
        }
    }

    public void setClientDetailsStore(Map<String, ? extends ClientDetails> clientDetailsStore) {
        this.clientDetailsStore = new HashMap(clientDetailsStore);
    }

}
