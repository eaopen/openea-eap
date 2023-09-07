package org.openea.eap.extj.message.util;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.taobao.api.ApiException;
import org.openea.eap.extj.util.RandomUtil;


/**
 * 通过钉钉用户ID串进行发送消息，传入就是接收人的钉钉用户ID串
 */
public class DingTalkUtil {

    /**
     * 钉钉发送消息的接口路径
     */
    public static final String SEND_MESSAGE = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";
    /**
     * 钉钉获取TOKEN的接口路径
     */
    public static final String TOKEN = "https://oapi.dingtalk.com/gettoken";

    /**
     * 获取token
     * @param appkey
     * @param appsecret
     * @return
     */
//    public static String getToken (String appkey,String appsecret){
//        DefaultDingTalkClient client = new
//                DefaultDingTalkClient(TOKEN);
//        OapiGettokenRequest request = new OapiGettokenRequest();
//        request.setAppkey(appkey);
//        request.setAppsecret(appsecret);
//        request.setHttpMethod("GET");
//        try {
//            OapiGettokenResponse response = client.execute(request);
////            LocalCacheClient.set("access_token", response.getAccessToken(),7200*1000);
//            return response.getAccessToken();
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    /**
     * 获取token
     *
     * @param appkey
     * @param appsecret
     * @return
     */
    public static JSONObject getAccessToken(String appkey, String appsecret) {
        JSONObject retMsg = new JSONObject();
        retMsg.put("code", true);
        retMsg.put("error", "");
        try {
            DingTalkClient client = new DefaultDingTalkClient(TOKEN);
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey(appkey);
            req.setAppsecret(appsecret);
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = client.execute(req);
            retMsg.put("access_token", rsp.getAccessToken());
            if (!rsp.isSuccess()) {
                retMsg.put("code", false);
                retMsg.put("error", rsp.getErrmsg());
                retMsg.put("access_token", "");
            }
        } catch (ApiException e) {
            retMsg.put("code", false);
            retMsg.put("error", e.toString());
            retMsg.put("access_token", "");
        }

        return retMsg;
    }

    /**
     * 给用户推送消息（文字消息）
     *
     * @param appkey
     * @param appsecret
     * @param agentid
     * @param userIds
     * @param content
     * @return 收到消息格式如下：
     * 发送的内容
     */
    public static JSONObject sendDingMessage(String appkey, String appsecret, String agentid, String userIds, String content) {
        JSONObject retMsg = new JSONObject();
        DingTalkClient client = new DefaultDingTalkClient(SEND_MESSAGE);

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userIds);
        request.setAgentId(Long.parseLong(agentid));
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        String randomCode = "随机验证码：" + RandomUtil.uuId();
        msg.getText().setContent(content + randomCode);
        request.setMsg(msg);

        try {
            retMsg = getAccessToken(appkey, appsecret);
            if (retMsg.getBoolean("code")) {
                // OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,getToken(appkey,appsecret));
                OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, retMsg.getString("access_token"));
                if (response.getErrcode() > 0) {
                    retMsg.put("code", false);
                    retMsg.put("error", response.getErrmsg());
                } else {
                    retMsg.put("code", true);
                    retMsg.put("error", "");
                }
            } else {
                retMsg.put("code", false);
                retMsg.put("error", "获取token失败:" + retMsg.getString("error"));
            }
            return retMsg;
        } catch (ApiException e) {
            retMsg.put("code", false);
            retMsg.put("error", e.toString());
            return retMsg;
        }
    }

}
