package org.openea.eap.module.obpm.controller.admin.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * OpenBPM Proxy
 * 备用方案，生产环境需要采用gateway
 *
 */
@Tag(name = "OpenBPM Proxy")
@RestController
@Validated
@Slf4j
public class ObpmProxyController {

    @Resource
    private ObmpClientService obmpClientService;

    @SneakyThrows
    @RequestMapping( method = RequestMethod.GET,
            value={"/bpm/instance/**", "/bpm/task/**", "/bpm/form/**",
                    "/form/formDefData/**", "/form/formCustDialog/**",
                    "/form/formCustSql/**",
                    "/sys/tools/**","/sys/dataDict/**"})
    public JSONObject proxyGet(HttpServletRequest request, @RequestParam(required = false) String url, @RequestHeader Map<String, String> headers) {
        if(ObjectUtils.isEmpty(url)){
            url = request.getRequestURI();
            if(ObjectUtils.isNotEmpty(request.getQueryString())){
                url += "?" + request.getQueryString();
            }
        }
        String obpmUrl = obmpClientService.getProxyUrl(url);
        headers.put("host", getHeaderHost(obpmUrl));
        // 使用 HttpUtil 发送 GET 请求
        HttpResponse response = HttpUtil.createGet(obpmUrl)
                .addHeaders(headers) // 添加请求头
                .execute();
        return convertJsonResult(response.body());
    }

    @SneakyThrows
    @RequestMapping( method = RequestMethod.POST,
            value={"/bpm/instance/**", "/bpm/task/**", "/bpm/form/**",
                    "/form/formDefData/**", "/form/formCustDialog/**",
                    "/form/formCustSql/**",
                    "/sys/tools/**","/sys/dataDict/**"})
    public JSONObject proxyPost(HttpServletRequest request, @RequestParam(required = false) String url, @RequestBody(required = false) String body, @RequestHeader Map<String, String> headers) {
        if(ObjectUtils.isEmpty(url)){
            url = request.getRequestURI();
            if(ObjectUtils.isNotEmpty(request.getQueryString())){
                url += "?" + request.getQueryString();
            }
        }
        String obpmUrl = obmpClientService.getProxyUrl(url);
        headers.put("host", getHeaderHost(obpmUrl));
        // 使用 HttpUtil 发送 POST 请求
        HttpRequest request2 = HttpUtil.createPost(obpmUrl)
                .addHeaders(headers); // 添加请求头
        if(ObjectUtils.isNotEmpty(body) && !"{}".equals(body)){
            request2 = request2.body(body);
        }
        HttpResponse response = request2.execute();
        return convertJsonResult(response.body());
    }

    private JSONObject convertJsonResult(String responseBody){
        JSONObject jsonResult =  JSONObject.parseObject(responseBody);
        // code 转为数字
        if(jsonResult.containsKey("code")){
            int code = jsonResult.getInteger("code");
            jsonResult.put("code", code);
        }
        return jsonResult;
    }

    @SneakyThrows
    private String getHeaderHost(String url){
            String strHost = null;
            URL url2 = new URL(url);

        strHost = url2.getHost();
        int port = url2.getPort();
        if(port!=-1 && port!=80 && port!=443){
            strHost += ":"+port;
        }
        return strHost;
    }
}
