package org.openea.eap.extj.message.util.weixingzh;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.io.SAXReader;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.wxutil.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 微信的接口类
 */
@Slf4j
public class WXGZHWebChatUtil {

    /**
     * api
     */
    public static final String API_DEFAULT_HOST_URL = "https://api.weixin.qq.com";

    /**
     * mq
     */
    public static final String MP_DEFAULT_HOST_URL = "https://mp.weixin.qq.com";

    /**
     * open
     */
    public static final String OPEN_DEFAULT_HOST_URL = "https://open.weixin.qq.com";


    /**
     * 获取接口访问凭证
     *
     * @param appID
     * @param appsecret
     * @return 数据格式
     * {
     * "access_token": "token",
     * "expires_in": 7200
     * }
     */
    public static String getAccessToken(String appID, String appsecret) {
        String requestUrl = API_DEFAULT_HOST_URL + "/cgi-bin/token?grant_type=client_credential&appid=" + appID + "&secret=" + appsecret;
        JSONObject rstObj = HttpUtil.httpRequest(requestUrl, "GET", null);
        JSONObject rst = new JSONObject();
        String token = null;
        if (rstObj.containsKey("access_token")) {
            token = rstObj.getString("access_token");
        }
        return token;
    }

    /**
     * 获取用户列表
     *
     * @param accessToken token
     * @param nextOpenId  第一个拉取的OPENID，不填默认从头开始拉取,一次只能拉取100个用户,最后一个用户openId需要放进来拉取接下来的数据
     * @return 数据格式
     * {
     * "total": 2,
     * "count": 2,
     * "data": {
     * "openid": [
     * "oD09N5rLh6juzOPA55C3YTo-hYJk",
     * "oD09N5jXQ47rNed4oyAlqQz9X348"
     * ]
     * },
     * "next_openid": "oD09N5jXQ47rNed4oyAlqQz9X348"
     * }
     */
    public static JSONObject getUsetList(String accessToken, String nextOpenId) {
        String requestUrl = API_DEFAULT_HOST_URL + "/cgi-bin/user/get?access_token=" + accessToken + "&next_openid=" + nextOpenId;
        JSONObject rstObj = HttpUtil.httpRequest(requestUrl, "GET", null);
        return rstObj;
    }

    /**
     * 获取用户信息
     *
     * @param accessToken token
     * @param openid      openid
     * @return 数据格式
     * {
     * "subscribe": 1,
     * "openid": "o6FJX1tay8i-431nidzispv2xrhs",
     * "nickname": "",
     * "sex": 0,
     * "language": "zh_CN",
     * "city": "",
     * "province": "",
     * "country": "",
     * "headimgurl": "",
     * "subscribe_time": 1648562407,
     * "unionid": "oear46TQmAj0UZmM3jMYVYU1rS8o",
     * "remark": "",
     * "groupid": 0,
     * "tagid_list": [ ],
     * "subscribe_scene": "ADD_SCENE_QR_CODE",
     * "qr_scene": 0,
     * "qr_scene_str": ""
     * }
     */
    public static JSONObject getUsetInfo(String accessToken, String openid) {
        String requestUrl = API_DEFAULT_HOST_URL + "/cgi-bin/user/info?access_token=" + accessToken + "&openid=" + openid;
        JSONObject rstObj = HttpUtil.httpRequest(requestUrl, "GET", null);
        return rstObj;
    }

    /**
     * 返回消息模板列表
     *
     * @param accessToken
     * @return
     */
    public static JSONObject getMessageList(String accessToken) {
        String requestUrl = API_DEFAULT_HOST_URL + "/cgi-bin/template/get_all_private_template?access_token=" + accessToken;
        JSONObject rstObj = HttpUtil.httpRequest(requestUrl, "GET", null);
        return rstObj;
    }

    /**
     * 发送消息
     *
     * @param accessToken
     * @return
     */
    public static JSONObject sendMessage(String accessToken, String message) {

        String messageUrl = API_DEFAULT_HOST_URL + "/cgi-bin/message/template/send?access_token=" + accessToken;

        JSONObject rst = new JSONObject();
        log.info("传递微信公众号参数" + message.toString());

        JSONObject rstMessageObj = HttpUtil.httpRequest(messageUrl, "POST", message.toString());
        log.info("接收微信公众号参数" + rstMessageObj.toString());

        boolean codeFlag = true;
        if (Integer.parseInt(rstMessageObj.get("errcode").toString()) != 0) {
            codeFlag = false;
        }
        rst.put("code", codeFlag);
        rst.put("error", rstMessageObj.get("errmsg").toString());
        return rst;
    }

    /**
     * 发送消息--关注发送消息模板
     *
     * @param accessToken
     * @return
     */
    public static JSONObject sendMessageByGZ(String accessToken, String touser) {

        String messageUrl = API_DEFAULT_HOST_URL + "/cgi-bin/message/template/send?access_token=" + accessToken;

        JSONObject message = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject first = new JSONObject();
        JSONObject title = new JSONObject();
        JSONObject user = new JSONObject();
        JSONObject date = new JSONObject();
        JSONObject remark = new JSONObject();
        JSONObject rst = new JSONObject();

        message.put("touser", touser);
        message.put("template_id", "qfrvB432USum5H2E1fipzpR8nhGgcS8Ullw-ImTScUQ");
        Random r = new Random();
        message.put("client_msg_id", "MSG_" + r.nextInt(99999999));

        first.put("value", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx184aaa643b114a92&redirect_uri=http%3A%2F%2Facg53k.natappfree.cc&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect");
        first.put("color", "#173177");

        content.put("first", first);

        message.put("data", content);
        System.out.println(message.toString());

        JSONObject rstMessageObj = HttpUtil.httpRequest(messageUrl, "POST", message.toString());
        System.out.println(rstMessageObj);

        boolean codeFlag = true;
        String errorMsg = "";
        if (Integer.parseInt(rstMessageObj.get("errcode").toString()) != 0) {
            codeFlag = false;
        }
        rst.put("code", codeFlag);
        rst.put("error", rstMessageObj.get("errmsg").toString());
        System.out.println("a");
        return rst;
    }

    /**
     * 获取接口访问凭证
     *
     * @return 数据格式
     * {
     * "access_token": "token",
     * "expires_in": 7200
     * }
     */
    public static String getQRCodeTiket(String accessToken, String userId) {
        String requestUrl = API_DEFAULT_HOST_URL + "/cgi-bin/qrcode/create?access_token=" + accessToken;
        String a = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + userId + "}}}";
        JSONObject rstObj = HttpUtil.httpRequest(requestUrl, "GET", null);
        JSONObject rst = new JSONObject();
        String token = null;
        if (rstObj.containsKey("access_token")) {
            token = rstObj.getString("access_token");
        }
        return token;
    }

    /**
     * 参数排序
     *
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public static String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 字符串进行shal加密
     *
     * @param str
     * @return
     */
    public static String shal(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();//字节数组转换为十六进制数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * dom4j解析 xmL转换为map
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        //将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        //从request中取得输入流
        InputStream inputstream = request.getInputStream();
        //读取输入流
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(inputstream);
        //得到kmL根元素
        org.dom4j.Element root = document.getRootElement();
        //得到根元素的所有子节点
        List<org.dom4j.Element> elementList = root.elements();
        //遍历所有子节点
        for (org.dom4j.Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        //释放资源
        inputstream.close();
        inputstream = null;
        return map;
    }

    /**
     * 生成xml消息
     *
     * @param encrypt   加密后的消息密文
     * @param signature 安全签名
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 生成的xml字符串
     */
    public static String generate(String encrypt, String signature, String timestamp, String nonce) {

        String format = "<xml>\n" + "<Encrypt><![CDATA[%1$s]]></Encrypt>\n"
                + "<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n"
                + "<TimeStamp>%3$s</TimeStamp>\n" + "<Nonce><![CDATA[%4$s]]></Nonce>\n" + "</xml>";
        return String.format(format, encrypt, signature, timestamp, nonce);

    }

    /**
     * 消息json组装
     */
    public static String messageJson(String templateId, String openId, String xcxAppId, String pagepath, Map<String, Object> parameterMap, String title, String type, String link) {

        String color = "#173177";
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
        WxTemplateMsg wxTemplateMsg = new WxTemplateMsg();
        for (String key : parameterMap.keySet()) {
            String field = key;
            String fieldValue = String.valueOf(parameterMap.get(key));
            field = field.replace("{{", "");
            field = field.replace(".DATA}}", "");
            //根据具体模板参数组装
            if ("first".equals(field)) {
                params.put(field, WxTemplateMsg.item(fieldValue, color));
            } else {
                params.put(field, WxTemplateMsg.item(fieldValue, color));
            }
        }

        // 模版ID
        wxTemplateMsg.setTemplate_id(templateId);
        // openId
        wxTemplateMsg.setTouser(openId);
        // 关键字赋值
        wxTemplateMsg.setData(params);
        if (!"1".equals(type)) {
            wxTemplateMsg.setUrl(link);
        } else {
            // 跳转小程序
            wxTemplateMsg.setMiniprogram(WxTemplateMsg.miniprogramData(xcxAppId, pagepath));
            wxTemplateMsg.setUrl("");
        }
        String data = JsonUtil.getObjectToString(wxTemplateMsg);
        log.info("微信公众号消息参数封装" + data);
        return data;
    }

}
