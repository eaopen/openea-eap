package org.openea.eap.extj.message.util.weixingzh;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.TreeMap;

@Data
public class WxTemplateMsg {

    /**
     * 接收者openId
     */
    private String touser;
    /**
     * 模板ID
     */
    private String template_id;

    /**
     * 防重入id。对于同一个openid + client_msg_id, 只发送一条消息,10分钟有效,超过10分钟不保证效果。若无防重入需求，可不填
     */
//    private String client_msg_id;
    /**
     * 模板跳转链接
     */
    private String url;

    /**
     * 跳转app
     */
    private JSONObject miniprogram;

    // "miniprogram":{ 未加入
    // "appid":"xiaochengxuappid12345",
    // "pagepath":"index?foo=bar"
    // },

    /**
     * data数据
     */
    private TreeMap<String, TreeMap<String, String>> data;

    /**
     * 参数
     *
     * @param value 值
     * @param color 颜色 可不填
     * @return params
     */
    public static TreeMap<String, String> item(String value, String color) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("value", value);
        params.put("color", color);
        return params;
    }

    public static JSONObject miniprogramData(String appid, String pagepath) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", appid);
        jsonObject.put("pagepath", pagepath);
        return jsonObject;
    }
}
