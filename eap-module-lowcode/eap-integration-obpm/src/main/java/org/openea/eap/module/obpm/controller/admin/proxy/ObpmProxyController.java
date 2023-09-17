package org.openea.eap.module.obpm.controller.admin.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.framework.security.core.LoginUser;
import org.openea.eap.framework.security.core.util.SecurityFrameworkUtils;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.openea.eap.module.obpm.service.obpm.ObpmUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Map;

/**
 * OpenBPM Proxy
 * 备用方案，生产环境需要采用gateway
 *
 */
@Tag(name = "OpenBPM Proxy")
@RestController
@ConditionalOnProperty(prefix = "eap.obpm", name = "proxy", havingValue = "true")
@Validated
@Slf4j
public class ObpmProxyController {

    @Resource
    private ObmpClientService obmpClientService;

    @SneakyThrows
    @RequestMapping( method = RequestMethod.GET,
            value={"/obpm-admin/**","/obpm-web1/**"})
    public void proxyWeb(HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> headers){
        String url = checkRequestUrl(request);
        String obpmUrl = obmpClientService.getProxyUrl(url);
        checkHeaderHost(headers, obpmUrl);
        checkHeaderAuth(headers);
        // 使用 HttpUtil 发送 GET 请求
        HttpResponse webRes = HttpUtil.createGet(obpmUrl)
                .addHeaders(headers) // 添加请求头
                .execute();
        response.setStatus(webRes.getStatus());
        webRes.headers().forEach((key, value) -> value.forEach( it -> {response.setHeader(key, it);}));
        StreamUtils.copy(webRes.bodyStream(), response.getOutputStream());
    }

//    "/bpm/instance/**", "/bpm/task/**", "/bpm/form/**",
//    "/form/formDefData/**", "/form/formCustDialog/**",
//    "/form/formCustSql/**",
//    "/sys/tools/**","/sys/dataDict/**"
    @SneakyThrows
    @RequestMapping( method = RequestMethod.GET,
            value={"/obpm-server/**","/obpm-api/**","/obpm/**"})
    public JSONObject proxyGet(HttpServletRequest request, @RequestHeader Map<String, String> headers) {
        String url = checkRequestUrl(request);
        String obpmUrl = obmpClientService.getProxyUrl(url);
        checkHeaderHost(headers, obpmUrl);
        checkHeaderAuth(headers);
        // 使用 HttpUtil 发送 GET 请求
        HttpResponse response = HttpUtil.createGet(obpmUrl)
                .addHeaders(headers) // 添加请求头
                .execute();
        return convertJsonResult(response.body());
    }

    @SneakyThrows
    @RequestMapping( method = RequestMethod.POST,
            value={"/obpm-server/**","/obpm-api/**","/obpm/**"})
    public JSONObject proxyPost(HttpServletRequest request, @RequestBody(required = false) String body, @RequestHeader Map<String, String> headers) {
        String url = checkRequestUrl(request);
        String obpmUrl = obmpClientService.getProxyUrl(url);
        checkHeaderHost(headers, obpmUrl);
        checkHeaderAuth(headers);
        // 使用 HttpUtil 发送 POST 请求
        HttpRequest request2 = HttpUtil.createPost(obpmUrl)
                .addHeaders(headers); // 添加请求头
        if(ObjectUtils.isNotEmpty(body) && !"{}".equals(body)){
            request2 = request2.body(body);
        }else{
            // 兼容，总是加上json数据
            request2 = request2.body("{\"noKey\":\"\"}");
        }
        HttpResponse response = request2.execute();
        return convertJsonResult(response.body());
    }

    private String checkRequestUrl(HttpServletRequest request){
       String url = request.getRequestURI();
       if(ObjectUtils.isNotEmpty(request.getQueryString())){
           url += "?" + request.getQueryString();
       }
       return url;
    }

    @SneakyThrows
    private void checkHeaderHost(Map<String, String> headers, String url){
        String strHost = null;
        URL url2 = new URL(url);

        strHost = url2.getHost();
        int port = url2.getPort();
        if(port!=-1 && port!=80 && port!=443){
            strHost += ":"+port;
        }
        headers.put("host", strHost);
    }

    private void checkHeaderAuth(Map<String, String> headers){
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if(user!=null && ObjectUtils.isNotEmpty(user.getUserKey())){
            //header: eap-user, eap-sign
            String userKey = user.getUserKey();
            headers.put("eap-user", userKey);
            headers.put("eap-sign", ObpmUtil.eapSign(userKey));
        }else{
            // mark only
            headers.put("eap-proxy", "true");
        }
    }

    private JSONObject convertJsonResult(String responseBody){
        JSONObject jsonResult = JSONUtil.parseObj(responseBody);
        // code 转为数字
        if(jsonResult.containsKey("code")){
            int code = jsonResult.getInt("code");
            jsonResult.set("code", code);
        }
        return jsonResult;
    }
}
