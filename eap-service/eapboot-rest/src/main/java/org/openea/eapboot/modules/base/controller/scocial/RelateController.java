package org.openea.eapboot.modules.base.controller.scocial;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.utils.IpInfoUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.base.vo.VaptchaSetting;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.entity.social.Github;
import org.openea.eapboot.modules.base.entity.social.QQ;
import org.openea.eapboot.modules.base.entity.social.Weibo;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.modules.base.service.GithubService;
import org.openea.eapboot.modules.base.service.QQService;
import org.openea.eapboot.modules.base.service.UserService;
import org.openea.eapboot.modules.base.service.WeiboService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 */
@Slf4j
@Api(description = "绑定第三方账号接口")
@RequestMapping("/eapboot/social")
@RestController
public class RelateController {

    @Autowired
    private UserService userService;

    @Autowired
    private GithubService githubService;

    @Autowired
    private QQService qqService;

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    public VaptchaSetting getVaptchaSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.VAPTCHA_SETTING);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("系统还未配置Vaptcha验证码");
        }
        return new Gson().fromJson(v, VaptchaSetting.class);
    }

    @RequestMapping(value = "/relate", method = RequestMethod.POST)
    @ApiOperation(value = "绑定账号")
    public Result<Object> relate(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam Integer socialType,
                                 @RequestParam String id,
                                 @RequestParam String token,
                                 HttpServletRequest request){

        VaptchaSetting vs = getVaptchaSetting();
        // 验证vaptcha验证码
        String params = "id=" + vs.getVid() + "&secretkey=" + vs.getSecretKey() + "&token=" + token
                + "&ip=" + ipInfoUtil.getIpAddr(request);
        String result = HttpUtil.post(SettingConstant.VAPTCHA_URL, params);
        if(!result.contains("\"success\":1")){
            return new ResultUtil<Object>().setErrorMsg("Vaptcha验证码验证失败");
        }
        User user = userService.findByUsername(username);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("账号不存在");
        }
        if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        String JWT = "";
        // 从redis中获取表id
        String ID = redisTemplate.opsForValue().get(id);
        if(StrUtil.isBlank(ID)){
            return new ResultUtil<Object>().setErrorMsg("无效的id");
        }
        // 绑定github
        if(CommonConstant.SOCIAL_TYPE_GITHUB.equals(socialType)){
            Github g = githubService.findByRelateUsername(username);
            if(g!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有Github账号，请先进行解绑操作");
            }
            Github github = githubService.get(ID);
            if(github==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(github.getIsRelated()&&StrUtil.isNotBlank(github.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该Github账号已绑定有用户，请先进行解绑操作");
            }
            github.setIsRelated(true);
            github.setRelateUsername(username);
            githubService.update(github);
            JWT = securityUtil.getToken(username, true);
        }else if(CommonConstant.SOCIAL_TYPE_QQ.equals(socialType)){
            QQ q = qqService.findByRelateUsername(username);
            if(q!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有QQ账号，请先进行解绑操作");
            }
            QQ qq = qqService.get(ID);
            if(qq==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(qq.getIsRelated()&&StrUtil.isNotBlank(qq.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该QQ账号已绑定有用户，请先进行解绑操作");
            }
            qq.setIsRelated(true);
            qq.setRelateUsername(username);
            qqService.update(qq);
            JWT = securityUtil.getToken(username, true);
        }else if(CommonConstant.SOCIAL_TYPE_WEIBO.equals(socialType)){
            Weibo w = weiboService.findByRelateUsername(username);
            if(w!=null){
                return new ResultUtil<Object>().setErrorMsg("该账户已绑定有微博账号，请先进行解绑操作");
            }
            Weibo weibo = weiboService.get(ID);
            if(weibo==null){
                return new ResultUtil<Object>().setErrorMsg("绑定失败，请先进行第三方授权认证");
            }
            if(weibo.getIsRelated()&&StrUtil.isNotBlank(weibo.getRelateUsername())){
                return new ResultUtil<Object>().setErrorMsg("该微博账号已绑定有用户，请先进行解绑操作");
            }
            weibo.setIsRelated(true);
            weibo.setRelateUsername(username);
            weiboService.update(weibo);
            JWT = securityUtil.getToken(username, true);
        }
        // 存入redis
        String JWTKey = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(JWTKey, JWT, 2L, TimeUnit.MINUTES);
        // 绑定成功删除缓存
        redisTemplate.delete("relate::relatedInfo:" + username);
        return new ResultUtil<Object>().setData(JWTKey);
    }

    @RequestMapping(value = "/getJWT", method = RequestMethod.GET)
    @ApiOperation(value = "获取JWT")
    public Result<Object> getJWT(@RequestParam String JWTKey){

        String JWT = redisTemplate.opsForValue().get(JWTKey);
        if(StrUtil.isBlank(JWT)){
            return new ResultUtil<Object>().setErrorMsg("获取JWT失败");
        }
        return new ResultUtil<Object>().setData(JWT);
    }
}
