package org.openea.eap.module.obpm.controller.admin.proxy;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping( method = RequestMethod.GET,
            value={"/bpm/instance/**", "/bpm/task/**", "/bpm/form/**",
                    "/form/formDefData/**", "/form/formCustDialog/**",
                    "/form/formCustSql/**",
                    "/sys/tools/**","/sys/dataDict/**"})
    public String proxyGet(HttpServletRequest request, @RequestParam(required = false) String url, @RequestHeader Map<String, String> headers) {
        if(ObjectUtils.isEmpty(url)){
            url = request.getRequestURI();
            if(ObjectUtils.isNotEmpty(request.getQueryString())){
                url += "?" + request.getQueryString();
            }
        }
        String obpmUrl = obmpClientService.getProxyUrl(url);
        // 使用 HttpUtil 发送 GET 请求
        HttpResponse response = HttpUtil.createGet(obpmUrl)
                .addHeaders(headers) // 添加请求头
                .execute();
        return response.body();
    }

    @RequestMapping( method = RequestMethod.POST,
            value={"/bpm/instance/**", "/bpm/task/**", "/bpm/form/**",
                    "/form/formDefData/**", "/form/formCustDialog/**",
                    "/form/formCustSql/**",
                    "/sys/tools/**","/sys/dataDict/**"})
    public String proxyPost(HttpServletRequest request, @RequestParam(required = false) String url, @RequestBody String body, @RequestHeader Map<String, String> headers) {
        if(ObjectUtils.isEmpty(url)){
            url = request.getRequestURI();
            if(ObjectUtils.isNotEmpty(request.getQueryString())){
                url += "?" + request.getQueryString();
            }
        }
        String obpmUrl = obmpClientService.getProxyUrl(url);
        // 使用 HttpUtil 发送 POST 请求
        HttpResponse response = HttpUtil.createPost(obpmUrl)
                .addHeaders(headers) // 添加请求头
                .body(body)
                .execute();
        return response.body();
    }
}
