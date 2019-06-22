package org.openea.eapboot.modules.base.controller.manage;

import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.modules.base.vo.*;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.oss.vo.OssSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 */
@Slf4j
@RestController
@Api(description = "基本配置接口")
@RequestMapping("/eapboot/setting")
public class SettingController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/seeSecret/{settingName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看私密配置")
    public Result<Object> seeSecret(@PathVariable String settingName) {

        String result = "", v;
        if(settingName.equals(SettingConstant.QINIU_OSS)){
            // 七牛云
            v = redisTemplate.opsForValue().get(SettingConstant.QINIU_OSS);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, OssSetting.class).getSecretKey();
            }
        }else if(settingName.equals(SettingConstant.ALI_OSS)){
            // 阿里云OSS
            v = redisTemplate.opsForValue().get(SettingConstant.ALI_OSS);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, OssSetting.class).getSecretKey();
            }
        }else if(settingName.equals(SettingConstant.TENCENT_OSS)){
            // 腾讯云COS
            v = redisTemplate.opsForValue().get(SettingConstant.TENCENT_OSS);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, OssSetting.class).getSecretKey();
            }
        }else if(settingName.equals(SettingConstant.MINIO_OSS)){
            // MINIOOSS
            v = redisTemplate.opsForValue().get(SettingConstant.MINIO_OSS);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, OssSetting.class).getSecretKey();
            }
        }else if(settingName.equals(SettingConstant.ALI_SMS)){
            // 阿里云短信
            v = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, SmsSetting.class).getSecretKey();
            }
        }else if(settingName.equals(SettingConstant.EMAIL_SETTING)){
            // 邮件
            v = redisTemplate.opsForValue().get(SettingConstant.EMAIL_SETTING);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, EmailSetting.class).getPassword();
            }
        }else if(settingName.equals(SettingConstant.VAPTCHA_SETTING)){
            // vaptcha验证码
            v = redisTemplate.opsForValue().get(SettingConstant.VAPTCHA_SETTING);
            if(StrUtil.isNotBlank(v)){
                result = new Gson().fromJson(v, VaptchaSetting.class).getSecretKey();
            }
        }
        return new ResultUtil<Object>().setData(result);
    }

    @RequestMapping(value = "/oss/check", method = RequestMethod.GET)
    @ApiOperation(value = "检查OSS配置")
    public Result<Object> osscheck() {

        String used = redisTemplate.opsForValue().get(SettingConstant.OSS_USED);
        if(StrUtil.isBlank(used)){
            return new ResultUtil<Object>().setErrorMsg(501, "您还未配置第三方OSS服务");
        }
        return new ResultUtil<Object>().setData(used);
    }

    @RequestMapping(value = "/oss/{serviceName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看OSS配置")
    public Result<OssSetting> oss(@PathVariable String serviceName) {

        String v = "";
        if(serviceName.equals(SettingConstant.QINIU_OSS)){
            v = redisTemplate.opsForValue().get(SettingConstant.QINIU_OSS);
        }else if(serviceName.equals(SettingConstant.ALI_OSS)){
            v = redisTemplate.opsForValue().get(SettingConstant.ALI_OSS);
        }else if(serviceName.equals(SettingConstant.TENCENT_OSS)){
            v = redisTemplate.opsForValue().get(SettingConstant.TENCENT_OSS);
        }else if(serviceName.equals(SettingConstant.LOCAL_OSS)){
            v = redisTemplate.opsForValue().get(SettingConstant.LOCAL_OSS);
        }else if(serviceName.equals(SettingConstant.MINIO_OSS)){
            v = redisTemplate.opsForValue().get(SettingConstant.MINIO_OSS);
        }
        if(StrUtil.isBlank(v)){
            return new ResultUtil<OssSetting>().setData(null);
        }
        OssSetting ossSetting = new Gson().fromJson(v, OssSetting.class);
        ossSetting.setSecretKey("**********");
        return new ResultUtil<OssSetting>().setData(ossSetting);
    }

    @RequestMapping(value = "/sms/{serviceName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看短信配置")
    public Result<SmsSetting> sms(@PathVariable String serviceName) {

        String v = "";
        if(serviceName.equals(SettingConstant.ALI_SMS)){
            v = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS);
        }
        if(StrUtil.isBlank(v)){
            return new ResultUtil<SmsSetting>().setData(null);
        }
        SmsSetting smsSetting = new Gson().fromJson(v, SmsSetting.class);
        smsSetting.setSecretKey("**********");
        if(smsSetting.getType()!=null){
            String code = redisTemplate.opsForValue().get(getSmsType(smsSetting.getType()));
            smsSetting.setTemplateCode(code);
        }
        return new ResultUtil<SmsSetting>().setData(smsSetting);
    }

    @RequestMapping(value = "/vaptcha", method = RequestMethod.GET)
    @ApiOperation(value = "查看vaptcha配置")
    public Result<VaptchaSetting> vaptcha() {

        String v = redisTemplate.opsForValue().get(SettingConstant.VAPTCHA_SETTING);
        if(StrUtil.isBlank(v)){
            return new ResultUtil<VaptchaSetting>().setData(null);
        }
        VaptchaSetting vaptchaSetting = new Gson().fromJson(v, VaptchaSetting.class);
        vaptchaSetting.setSecretKey("**********");
        return new ResultUtil<VaptchaSetting>().setData(vaptchaSetting);
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    @ApiOperation(value = "查看email配置")
    public Result<EmailSetting> email() {

        String v = redisTemplate.opsForValue().get(SettingConstant.EMAIL_SETTING);
        if(StrUtil.isBlank(v)){
            return new ResultUtil<EmailSetting>().setData(null);
        }
        EmailSetting emailSetting = new Gson().fromJson(v, EmailSetting.class);
        emailSetting.setPassword("**********");
        return new ResultUtil<EmailSetting>().setData(emailSetting);
    }

    @RequestMapping(value = "/other", method = RequestMethod.GET)
    @ApiOperation(value = "查看其他配置")
    public Result<OtherSetting> other() {

        String v = redisTemplate.opsForValue().get(SettingConstant.OTHER_SETTING);
        if(StrUtil.isBlank(v)){
            return new ResultUtil<OtherSetting>().setData(null);
        }
        OtherSetting otherSetting = new Gson().fromJson(v, OtherSetting.class);
        return new ResultUtil<OtherSetting>().setData(otherSetting);
    }

    @RequestMapping(value = "/sms/templateCode/{type}", method = RequestMethod.GET)
    @ApiOperation(value = "查看短信模板配置")
    public Result<String> smsTemplateCode(@PathVariable Integer type) {

        String templateCode = "";
        if(type!=null){
            String template = getSmsType(type);
            templateCode = redisTemplate.opsForValue().get(template);
        }
        return new ResultUtil<String>().setData(templateCode);
    }

    @RequestMapping(value = "/oss/set", method = RequestMethod.POST)
    @ApiOperation(value = "OSS配置")
    public Result<Object> ossSet(@ModelAttribute OssSetting ossSetting) {

        if(ossSetting.getServiceName().equals(SettingConstant.QINIU_OSS)){
            // 七牛
            String v = redisTemplate.opsForValue().get(SettingConstant.QINIU_OSS);
            if(StrUtil.isNotBlank(v)&&!ossSetting.getChanged()){
                String secrectKey = new Gson().fromJson(v, OssSetting.class).getSecretKey();
                ossSetting.setSecretKey(secrectKey);
            }
            redisTemplate.opsForValue().set(SettingConstant.QINIU_OSS, new Gson().toJson(ossSetting));
            redisTemplate.opsForValue().set(SettingConstant.OSS_USED, SettingConstant.QINIU_OSS);
        }else if(ossSetting.getServiceName().equals(SettingConstant.ALI_OSS)){
            // 阿里
            String v = redisTemplate.opsForValue().get(SettingConstant.ALI_OSS);
            if(StrUtil.isNotBlank(v)&&!ossSetting.getChanged()){
                String secrectKey = new Gson().fromJson(v, OssSetting.class).getSecretKey();
                ossSetting.setSecretKey(secrectKey);
            }
            redisTemplate.opsForValue().set(SettingConstant.ALI_OSS, new Gson().toJson(ossSetting));
            redisTemplate.opsForValue().set(SettingConstant.OSS_USED, SettingConstant.ALI_OSS);
        }else if(ossSetting.getServiceName().equals(SettingConstant.TENCENT_OSS)){
            // 腾讯
            String v = redisTemplate.opsForValue().get(SettingConstant.TENCENT_OSS);
            if(StrUtil.isNotBlank(v)&&!ossSetting.getChanged()){
                String secrectKey = new Gson().fromJson(v, OssSetting.class).getSecretKey();
                ossSetting.setSecretKey(secrectKey);
            }
            redisTemplate.opsForValue().set(SettingConstant.TENCENT_OSS, new Gson().toJson(ossSetting));
            redisTemplate.opsForValue().set(SettingConstant.OSS_USED, SettingConstant.TENCENT_OSS);
        }else if(ossSetting.getServiceName().equals(SettingConstant.LOCAL_OSS)){
            // 本地
            redisTemplate.opsForValue().set(SettingConstant.LOCAL_OSS, new Gson().toJson(ossSetting));
            redisTemplate.opsForValue().set(SettingConstant.OSS_USED, SettingConstant.LOCAL_OSS);
        }else if(ossSetting.getServiceName().equals(SettingConstant.MINIO_OSS)){
            // 本地
            redisTemplate.opsForValue().set(SettingConstant.MINIO_OSS, new Gson().toJson(ossSetting));
            redisTemplate.opsForValue().set(SettingConstant.OSS_USED, SettingConstant.MINIO_OSS);
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/sms/set", method = RequestMethod.POST)
    @ApiOperation(value = "短信配置")
    public Result<Object> smsSet(@ModelAttribute SmsSetting smsSetting) {

        if(smsSetting.getServiceName().equals(SettingConstant.ALI_SMS)){
            // 阿里
            String v = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS);
            if(StrUtil.isNotBlank(v)&&!smsSetting.getChanged()){
                String secrectKey = new Gson().fromJson(v, SmsSetting.class).getSecretKey();
                smsSetting.setSecretKey(secrectKey);
            }
            if(smsSetting.getType()!=null){
                String type = getSmsType(smsSetting.getType());
                redisTemplate.opsForValue().set(type, smsSetting.getTemplateCode());
            }
            smsSetting.setType(null);
            smsSetting.setTemplateCode(null);
            redisTemplate.opsForValue().set(SettingConstant.ALI_SMS, new Gson().toJson(smsSetting));
            redisTemplate.opsForValue().set(SettingConstant.SMS_USED, SettingConstant.ALI_SMS);
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/email/set", method = RequestMethod.POST)
    @ApiOperation(value = "email配置")
    public Result<Object> emailSet(@ModelAttribute EmailSetting emailSetting) {

        String v = redisTemplate.opsForValue().get(SettingConstant.EMAIL_SETTING);
        if(StrUtil.isNotBlank(v)&&!emailSetting.getChanged()){
            String password = new Gson().fromJson(v, EmailSetting.class).getPassword();
            emailSetting.setPassword(password);
        }
        redisTemplate.opsForValue().set(SettingConstant.EMAIL_SETTING, new Gson().toJson(emailSetting));
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/vaptcha/set", method = RequestMethod.POST)
    @ApiOperation(value = "vaptcha配置")
    public Result<Object> vaptchaSet(@ModelAttribute VaptchaSetting vaptchaSetting) {

        String v = redisTemplate.opsForValue().get(SettingConstant.VAPTCHA_SETTING);
        if(StrUtil.isNotBlank(v)&&!vaptchaSetting.getChanged()){
            String key = new Gson().fromJson(v, VaptchaSetting.class).getSecretKey();
            vaptchaSetting.setSecretKey(key);
        }
        redisTemplate.opsForValue().set(SettingConstant.VAPTCHA_SETTING, new Gson().toJson(vaptchaSetting));
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/other/set", method = RequestMethod.POST)
    @ApiOperation(value = "其他配置")
    public Result<Object> otherSet(@ModelAttribute OtherSetting otherSetting) {

        String v = redisTemplate.opsForValue().get(SettingConstant.OTHER_SETTING);
        redisTemplate.opsForValue().set(SettingConstant.OTHER_SETTING, new Gson().toJson(otherSetting));
        return new ResultUtil<Object>().setData(null);
    }

    public String getSmsType(Integer type){
        switch (type){
            case -1:
                return SettingConstant.ALI_SMS_COMMON;
            case 0:
                return SettingConstant.ALI_SMS_REGIST;
            case 1:
                return SettingConstant.ALI_SMS_LOGIN;
            case 2:
                return SettingConstant.ALI_SMS_CHANGE_MOBILE;
            case 3:
                return SettingConstant.ALI_SMS_CHANG_PASS;
            case 4:
                return SettingConstant.ALI_SMS_RESET_PASS;
            case 5:
                return SettingConstant.ALI_SMS_ACTIVITI;
            default:
                return SettingConstant.ALI_SMS_COMMON;
        }
    }
}
