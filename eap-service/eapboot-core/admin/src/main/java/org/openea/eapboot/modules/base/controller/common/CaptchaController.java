package org.openea.eapboot.modules.base.controller.common;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.limit.RedisRaterLimiter;
import org.openea.eapboot.common.utils.CreateVerifyCode;
import org.openea.eapboot.common.utils.IpInfoUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SmsUtil;
import org.openea.eapboot.common.vo.Captcha;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.base.vo.VaptchaSetting;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.service.UserService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 */
@Api(description = "验证码接口")
@RequestMapping("/eapboot/common/captcha")
@RestController
@Transactional
@Slf4j
public class CaptchaController {

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @Autowired
    private RedisRaterLimiter redisRaterLimiter;

    @Autowired
    private UserService userService;

    public VaptchaSetting getVaptchaSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.VAPTCHA_SETTING);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("系统还未配置Vaptcha验证码");
        }
        return new Gson().fromJson(v, VaptchaSetting.class);
    }

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    public Result<Object> initCaptcha() {

        String captchaId = UUID.randomUUID().toString().replace("-","");
        String code = new CreateVerifyCode().randomStr(4);
        Captcha captcha = new Captcha();
        captcha.setCaptchaId(captchaId);
        //缓存验证码
        redisTemplate.opsForValue().set(captchaId,code,3L, TimeUnit.MINUTES);
        return new ResultUtil<Object>().setData(captcha);
    }

    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {

        // 得到验证码 生成指定验证码
        String code=redisTemplate.opsForValue().get(captchaId);
        CreateVerifyCode vCode = new CreateVerifyCode(116,36,4,10,code);
        vCode.write(response.getOutputStream());
    }

    @RequestMapping(value = "/sendSms/{mobile}", method = RequestMethod.GET)
    @ApiOperation(value = "发送短信验证码")
    public Result<Object> sendSms(@PathVariable String mobile,
                                  @ApiParam("默认0发送给所有手机号 1只发送给注册手机") @RequestParam(required = false, defaultValue = "0") Integer type,
                                  HttpServletRequest request) {

        if(type==1&&userService.findByMobile(mobile)==null){
            return new ResultUtil<Object>().setErrorMsg("手机号未注册");
        }
        // IP限流 1分钟限1个请求
        String key = "sendSms:"+ipInfoUtil.getIpAddr(request);
        String value = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(value)) {
            return new ResultUtil<Object>().setErrorMsg("您发送的太频繁啦，请稍后再试");
        }
        // 生成6位数验证码
        String code = new CreateVerifyCode().getRandomNum();
        // 缓存验证码
        redisTemplate.opsForValue().set(CommonConstant.PRE_SMS + mobile, code,5L, TimeUnit.MINUTES);
        // 发送验证码
        try {
            // 获取模板
            String templateCode = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS_COMMON);
            SendSmsResponse response = smsUtil.sendCode(mobile, code, templateCode);
            if(response.getCode() != null && ("OK").equals(response.getMessage())) {
                // 请求成功 标记限流
                redisTemplate.opsForValue().set(key, "sended", 1L, TimeUnit.MINUTES);
                return new ResultUtil<Object>().setSuccessMsg("发送短信验证码成功");
            }else{
                return new ResultUtil<Object>().setErrorMsg("请求发送验证码失败，" + response.getMessage());
            }
        } catch (ClientException e) {
            log.error("请求发送短信验证码失败，" + e);
            return new ResultUtil<Object>().setErrorMsg("请求发送验证码失败，" + e.getMessage());
        }
    }

    @RequestMapping(value = "/sendResetSms", method = RequestMethod.POST)
    @ApiOperation(value = "发送重置密码验证码")
    public Result<Object> sendResetSms(@RequestParam String mobile,
                                       @RequestParam String token,
                                       HttpServletRequest request) {

        VaptchaSetting vs = getVaptchaSetting();
        // 验证vaptcha验证码
        String params = "id=" + vs.getVid() + "&secretkey=" + vs.getSecretKey() + "&token=" + token
                + "&ip=" + ipInfoUtil.getIpAddr(request);
        String result = HttpUtil.post(SettingConstant.VAPTCHA_URL, params);
        if(!result.contains("\"success\":1")){
            return new ResultUtil<Object>().setErrorMsg("Vaptcha验证码验证失败");
        }
        User u = userService.findByMobile(mobile);
        if(u==null){
            return new ResultUtil<Object>().setErrorMsg("该手机号未注册");
        }
        // IP限流 1分钟限1个请求
        String key = "sendSms:"+ipInfoUtil.getIpAddr(request);
        String value = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(value)) {
            return new ResultUtil<Object>().setErrorMsg("您发送的太频繁啦，请稍后再试");
        }
        // 生成6位数验证码
        String code = new CreateVerifyCode().getRandomNum();
        // 缓存验证码
        redisTemplate.opsForValue().set(CommonConstant.PRE_SMS + mobile, code,5L, TimeUnit.MINUTES);
        // 发送验证码
        try {
            // 获取重置密码模板
            String templateCode = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS_RESET_PASS);
            SendSmsResponse response = smsUtil.sendCode(mobile, code, templateCode);
            if(response.getCode() != null && ("OK").equals(response.getMessage())) {
                // 请求成功 标记限流
                redisTemplate.opsForValue().set(key, "sended", 1L, TimeUnit.MINUTES);
                return new ResultUtil<Object>().setSuccessMsg("发送短信验证码成功");
            }else{
                return new ResultUtil<Object>().setErrorMsg("请求发送验证码失败，" + response.getMessage());
            }
        } catch (ClientException e) {
            log.error("请求发送短信验证码失败，" + e);
            return new ResultUtil<Object>().setErrorMsg("请求发送验证码失败，" + e.getMessage());
        }
    }
}
