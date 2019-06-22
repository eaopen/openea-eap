package org.openea.eapboot.modules.scocial.controller;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.constant.SecurityConstant;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.modules.scocial.vo.QQUserInfo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.scocial.entity.QQ;
import org.openea.eapboot.modules.scocial.service.QQService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * http://wiki.connect.qq.com/
 */
@Slf4j
@Api(description = "QQ登录接口")
@RequestMapping("/eapboot/social/qq")
@Controller
public class QQController {

    @Value("${eapboot.social.qq.appId}")
    private String appId;

    @Value("${eapboot.social.qq.appKey}")
    private String appKey;

    @Value("${eapboot.social.qq.callbackUrl}")
    private String callbackUrl;

    @Value("${eapboot.social.callbackFeUrl}")
    private String callbackFeUrl;

    @Value("${eapboot.social.callbackFeRelateUrl}")
    private String callbackFeRelateUrl;

    @Autowired
    private QQService qqService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * QQ认证服务器地址
     */
    private static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize";

    /**
     * 申请令牌地址
     */
    private static final String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    /**
     * 获取openId地址
     */
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=";

    /**
     * 获取用户信息地址
     */
    private static final String GET_USERINFO_URL = "https://graph.qq.com/user/get_user_info";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ApiOperation(value = "获取qq认证链接")
    @ResponseBody
    public Result<Object> login(){

        // 生成并保存state 忽略该参数有可能导致CSRF攻击
        String state = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(SecurityConstant.QQ_STATE + state, "VALID", 3L, TimeUnit.MINUTES);

        // 传递参数response_type、client_id、state、redirect_uri
        String url = AUTHORIZE_URL + "?response_type=code&" + "client_id=" + appId + "&state=" + state
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
        String v = redisTemplate.opsForValue().get(SecurityConstant.QQ_STATE + state);
        redisTemplate.delete(SecurityConstant.QQ_STATE + state);
        if(StrUtil.isBlank(v)) {
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("授权超时或state不正确","utf-8");
        }

        // 传递参数grant_type、code、redirect_uri、client_id
        String params = "grant_type=authorization_code&code=" + code + "&redirect_uri=" +
                callbackUrl + "&client_id=" + appId + "&client_secret=" + appKey;

        // 申请令牌 post请求
        String result = HttpUtil.post(ACCESS_TOKEN_URL, params);
        if(!result.contains("access_token=")){
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("获取access_token失败","utf-8");
        }

        String accessToken = StrUtil.subBetween(result,"access_token=", "&expires_in");

        String resultOpenId = HttpUtil.get(OPEN_ID_URL + accessToken);
        if(!resultOpenId.contains("openid")){
            return "redirect:" + callbackFeUrl + "?error=" + URLEncoder.encode("获取openid失败","utf-8");
        }

        String openId = StrUtil.subBetween(resultOpenId,"\"openid\":\"", "\"}");

        // 获取用户信息
        String userInfoUrl = GET_USERINFO_URL + "?access_token=" + accessToken + "&oauth_consumer_key=" + appId
                + "&openid=" + openId;
        String userInfo = HttpUtil.get(userInfoUrl);
        QQUserInfo q = new Gson().fromJson(userInfo, QQUserInfo.class);
        // 存入数据库
        QQ qq = qqService.findByOpenId(openId);
        if(qq==null){
            QQ newqq = new QQ();
            newqq.setOpenId(openId);
            newqq.setUsername(q.getNickname());
            newqq.setAvatar(q.getFigureurl_1());
            qq = qqService.save(newqq);
        }

        String url = "";
        // 判断是否绑定账号
        if(qq.getIsRelated()&&StrUtil.isNotBlank(qq.getRelateUsername())){
            // 已绑定 直接登录
            String JWT = securityUtil.getToken(qq.getRelateUsername(), true);
            // 存入redis
            String JWTKey = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(JWTKey, JWT, 2L, TimeUnit.MINUTES);
            url = callbackFeUrl + "?related=1&JWTKey=" + JWTKey;
        }else{
            // 未绑定 Redis存入id
            String idToken = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(idToken, qq.getId(), 5L, TimeUnit.MINUTES);
            url = callbackFeRelateUrl + "?socialType="+ CommonConstant.SOCIAL_TYPE_QQ +"&id=" + idToken;
        }
        return "redirect:" + url;
    }
}
