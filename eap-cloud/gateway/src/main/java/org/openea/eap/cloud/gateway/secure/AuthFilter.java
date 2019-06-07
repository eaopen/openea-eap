package org.openea.eap.cloud.gateway.secure;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openea.eap.cloud.gateway.config.ApplicationProperties;
import org.openea.eap.cloud.gateway.vo.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class AuthFilter implements GlobalFilter {
//
//    @Autowired
//    private JwtCheck jwtCheck;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route gatewayUrl = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        URI uri = gatewayUrl.getUri();
        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        HttpHeaders header = request.getHeaders();
        MultiValueMap params = request.getQueryParams();
        if (uri.toString().startsWith("lb://oauth")) {
            String referer = header.getFirst("Referer");
            ServerHttpRequest.Builder mutate = request.mutate();
//            mutate.header("Referer", referer);
            ServerHttpRequest buildReuqest = mutate.build();
            return chain.filter(exchange.mutate().request(buildReuqest).build());
        }
        String token = (String) params.getFirst("token");
        if (StringUtils.isBlank(token)) {
            token = (String) header.getFirst("token");
        }
        if (StringUtils.isBlank(token)) {
            String authorization = (String) header.getFirst("Authorization");
            if (StringUtils.isNotBlank(authorization)) {
                token = authorization.split("\\s")[1];
            }
        }
        if (StringUtils.isBlank(token)) {
            if (request.getCookies() != null && request.getCookies().size() > 0 && request.getCookies().containsKey("access_token")) {
                token = (String) request.getCookies().get("access_token").get(0).getValue();
            }
        }
        boolean pass = false;
        int status = 0;
        String errorInfo = null;
        Map<String, Object> claims = null;
        if (StringUtils.isNotBlank(token)) {
            claims = authorityService.decodeAccessToken(token);
            Object[] visitInfo = authorityService.canVisit(claims, request.getURI().getPath());
            status = (int) visitInfo[0];
            pass = (status == 0);
            if (visitInfo.length > 1) {
                errorInfo = (String) visitInfo[1];
            }
        } else {
            errorInfo = ApiError.MESSAGE_NO_AUTHORIZATION_HEADER;
        }
        if (pass) {
            ServerHttpRequest.Builder mutate = request.mutate();
            mutate.header("inner-user-name", claims.get("user_name").toString());
            mutate.header("inner-user-id", claims.get("user_id").toString());
            ServerHttpRequest buildReuqest = mutate.build();
            return chain.filter(exchange.mutate().request(buildReuqest).build());
        } else {
//            boolean isAjax = header.containsKey("X-Requested-With");
//            if (isAjax) {
            ServerHttpResponse response = (ServerHttpResponse) exchange.getResponse();
            JSONObject message = new JSONObject();
            message.put("status", status);
            message.put("data", errorInfo);
            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
//            } else {
//                String url = applicationProperties.getSsoServerUrl();
//                ServerHttpResponse response = exchange.getResponse();
//                //303状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
//                response.setStatusCode(HttpStatus.SEE_OTHER);
//                response.getHeaders().set(HttpHeaders.LOCATION, url);
//                return response.setComplete();
//            }
        }
    }
}
