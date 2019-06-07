package org.openea.eap.cloud.gateway.api.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient("oauth")
public interface  OauthClient {
    @RequestMapping("/oauth/public_key")
    @ResponseBody
    public Map<String, String> publicKey() ;
}
