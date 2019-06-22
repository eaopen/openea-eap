package org.openea.eapboot.common.utils;

import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.modules.base.vo.SmsSetting;
import org.openea.eapboot.common.exception.EapbootException;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 */
@Component
@Slf4j
public class SmsUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public SmsSetting getSmsSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("您还未配置阿里云短信");
        }
        return new Gson().fromJson(v, SmsSetting.class);
    }

    /**
     * 发送验证码 模版变量为 code
     * @param mobile
     * @param code
     * @param templateCode
     * @return
     * @throws ClientException
     */
    public SendSmsResponse sendCode(String mobile, String code, String templateCode) throws ClientException {

        return sendSms(mobile, "code", code, templateCode);
    }

    /**
     * 发送工作流消息 模版变量为 content
     * @param mobile
     * @param content
     * @return
     * @throws ClientException
     */
    public SendSmsResponse sendActMessage(String mobile, String content) throws ClientException {

        // 获取工作流消息模板
        String templateCode = redisTemplate.opsForValue().get(SettingConstant.ALI_SMS_ACTIVITI);
        return sendSms(mobile, "content", content, templateCode);
    }

    /**
     * 发送短信
     * @param mobile 手机号
     * @param param 替换短信模板 变量
     * @param value 变量值
     * @param templateCode 短信模板code
     * @return
     * @throws ClientException
     */
    public SendSmsResponse sendSms(String mobile, String param, String value, String templateCode) throws ClientException {

        SmsSetting s = getSmsSetting();

        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        final String product = "Dysmsapi";
        final String domain = "dysmsapi.aliyuncs.com";
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", s.getAccessKey(),
                s.getSecretKey());
        DefaultProfile.addEndpoint("cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setSysMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(s.getSignName());
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\""+ param +"\":\""+ value +"\"}");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }
}
