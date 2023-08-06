package org.openea.eap.extj.message.util;

import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.util.wxutil.HttpUtil;

/**
 * 企业微信的接口类
 */
public class QyWebChatUtil {

    /**
     * 获取企业微信TOKEN的接口路径
     */
    public static final String TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";

    /**
     * 往企业微信发送消息的接口路径
     */
    public static final String SEND_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";


    /**
     * 获取接口访问凭证
     */
    public static JSONObject getAccessToken(String corpId, String corpSecret) {
        JSONObject retMsg = new JSONObject();
        JSONObject rstObj = HttpUtil.httpRequest(String.format(TOKEN, corpId, corpSecret), "GET", null);
//        JSONObject rstObj = HttpUtil.httpRequest(QyApi.getTokenUrl(corpId, corpSecret), "GET", null);
        return rstObj;
    }


    /**
     * 发送消息 20210416 Add By GongXishan
     * 不抛出异常，返回Json
     */
    public static JSONObject sendMessage(String message, String accessToken) {
        JSONObject retMsg = new JSONObject();
        boolean codeFlag = true;
        String errorMsg = "";
        JSONObject rstObj = HttpUtil.httpRequest(String.format(SEND_MESSAGE, accessToken), "POST", message);
//        JSONObject rstObj = HttpUtil.httpRequest(QyApi.sendMessage(accessToken), "POST", message);
        if (HttpUtil.isWxError(rstObj)) {
            codeFlag = false;
            errorMsg = rstObj.toString();
        }
        retMsg.put("code", codeFlag);
        retMsg.put("error", errorMsg);
        return retMsg;
    }


    /**
     * 向企业微信发送信息
     *
     * @param corpId
     * @param corpSecret
     * @param agentId
     * @param toUserId
     * @param contents
     * @return
     */
    public static JSONObject sendWxMessage(String corpId, String corpSecret, String agentId, String toUserId, String contents) {
        JSONObject retMsg = null;
        JSONObject message = null;
        JSONObject tokenObject = null;
        JSONObject content = null;

        message = new JSONObject();
        message.put("touser", toUserId);
        message.put("agentid", agentId);
        content = new JSONObject();
        content.put("content", contents);
        message.put("text", content);
        message.put("msgtype", "text");
        tokenObject = getAccessToken(corpId, corpSecret);
        if (tokenObject.getString("access_token") != null && !"".equals(tokenObject.getString("access_token"))) {
            retMsg = sendMessage(message.toJSONString(), tokenObject.getString("access_token"));
        } else {
            retMsg.put("code", false);
            retMsg.put("error", "access_token值为空,不能发送信息！");
        }
        return retMsg;
    }

}
