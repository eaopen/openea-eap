package org.openea.eapboot.modules.scocial.controller;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.constant.SecurityConstant;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.modules.scocial.vo.GithubUserInfo;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.scocial.entity.Github;
import org.openea.eapboot.modules.scocial.service.GithubService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/
 */
@Slf4j
@Api(description = "Github登录接口")
@RequestMapping("/eapboot/social/github")
@Controller
public class GithubController {

    @Value("${eapboot.social.github.clientId}")
    private String clientId;

    @Value("${eapboot.social.github.clientSecret}")
    private String clientSecret;

    @Value("${eapboot.social.github.callbackUrl}")
    private String callbackUrl;

    @Value("${eapboot.social.callbackFeUrl}")
    private String callbackFeUrl;

    @Value("${eapboot.social.callbackFeRelateUrl}")
    private String callbackFeRelateUrl;

    @Autowired
    private GithubService githubService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Github认证服务器地址
     */
    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";

    /**
     * 申请令牌地址
     */
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

    /**
     * 获取用户信息地址
     */
    private static final String GET_USERINFO_URL = "https://api.github.com/user?access_token=";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ApiOperation(value = "获取github认证链接")
    @ResponseBody
    public Result<Object> login(){

        // 生成并保存state 忽略该参数有可能导致CSRF攻击
        String state = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(SecurityConstant.GITHUB_STATE + state, "VALID", 3L, TimeUnit.MINUTES);

        // 传递参数response_type、client_id、state、redirect_uri
        String url = AUTHORIZE_URL + "?response_type=code&" + "client_id=" + clientId + "&state=" + state
                + "&redirect_uri=" + callbackUrl;

        return new ResultUtil<Object>().setData(url);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ApiOperation(value = "获取accessToken")
    public String getAccessToken(@RequestParam(required = false) String code,
                                 @RequestParam(required = false) String state) throws UnsupportedEncodingException {

        if(StrUtil.isBlank(code)){
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("您未同意授权","utf-8");
        }
        // 验证state
        String v = redisTemplate.opsForValue().get(SecurityConstant.GITHUB_STATE + state);
        redisTemplate.delete(SecurityConstant.GITHUB_STATE + state);
        if(StrUtil.isBlank(v)) {
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("授权超时或state不正确","utf-8");
        }

        // 传递参数grant_type、code、redirect_uri、client_id
        String params = "grant_type=authorization_code&code=" + code + "&redirect_uri=" +
                callbackUrl + "&client_id=" + clientId + "&client_secret=" + clientSecret;

        // 申请令牌 post请求
        String result = HttpUtil.post(ACCESS_TOKEN_URL, params);

        if(!result.contains("access_token=")){
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("获取access_token失败","utf-8");
        }

        String accessToken = StrUtil.subBetween(result,"access_token=", "&scope");
        // 获取用户信息
        String userInfo = HttpUtil.get(GET_USERINFO_URL + accessToken);
        GithubUserInfo gu = new Gson().fromJson(userInfo, GithubUserInfo.class);
        // 存入数据库
        Github github = githubService.findByOpenId(gu.getId());
        if(github==null){
            Github g = new Github();
            g.setOpenId(gu.getId());
            g.setUsername(gu.getLogin());
            g.setAvatar(gu.getAvatar_url());
            github = githubService.save(g);
        }

        String url = "";
        // 判断是否绑定账号
        if(github.getIsRelated()&&StrUtil.isNotBlank(github.getRelateUsername())){
            // 已绑定 直接登录
            String JWT = securityUtil.getToken(github.getRelateUsername(), true);
            // 存入redis
            String JWTKey = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(JWTKey, JWT, 2L, TimeUnit.MINUTES);
            url = callbackFeUrl + "?related=1&JWTKey=" + JWTKey;
        }else{
            // 未绑定 Redis存入id
            String idToken = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(idToken, github.getId(), 5L, TimeUnit.MINUTES);
            url = callbackFeRelateUrl + "?socialType="+ CommonConstant.SOCIAL_TYPE_GITHUB +"&id=" + idToken;
        }
        return "redirect:" + url;
    }
}
