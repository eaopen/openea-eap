package org.openea.eap.extj.message.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openea.eap.extj.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WebHookUtil {
    /**
     * post请求以及参数是json
     *
     * @param url
     * @param jsonParams
     * @return
     */
    public static JSONObject doPostForJson(String url, String jsonParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonObject = null;
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            httpPost.setEntity(new StringEntity(jsonParams, ContentType.create("application/json", "utf-8")));
            System.out.println("request parameters" + EntityUtils.toString(httpPost.getEntity()));
            System.out.println("httpPost:" + httpPost);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("result:" + result);
                jsonObject = JSONObject.parseObject(result);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        }
    }

    /**
     * 基础验证
     *
     * @param url     webhook地址
     * @param msgList 消息内容
     */
    public static void sendMsgBasic(String url, List<String> msgList, String userName, String password) {
        //飞书机器人url 通过webhook将自定义服务的消息推送至飞书
        String content = userName + ":" + password;
        String token = "Basic " + Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("msg_type", "text");
        Map<String, Object> contentMap = new HashMap<>();
        params.put("content", contentMap);

        StringBuilder stringBuilder = new StringBuilder();
        msgList.forEach(e -> {
            stringBuilder.append(e + "\n");
        });

        contentMap.put("text", stringBuilder.toString());

        doPostForJsonObject(url, JSON.toJSONString(params), token);
    }


    /**
     * post请求以及参数是json
     *
     * @param url
     * @param jsonParams
     * @return
     */
    public static JSONObject doPostForJsonObject(String url, String jsonParams, String token) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonObject = null;
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(180 * 1000).setConnectionRequestTimeout(180 * 1000)
                .setSocketTimeout(180 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", token);
        try {
            httpPost.setEntity(new StringEntity(jsonParams, ContentType.create("application/json", "utf-8")));
            System.out.println("request parameters" + EntityUtils.toString(httpPost.getEntity()));
            System.out.println("httpPost:" + httpPost);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                System.out.println("result:" + result);
                jsonObject = JSONObject.parseObject(result);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        }
    }

    /**
     * 把timestamp+"\n"+密钥当做签名字符串并计算签名
     *
     * @param secret    bearer令牌
     * @param timestamp 当前时间的时间戳格式
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String GenSign(String secret, Long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        //把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;

        //使用HmacSHA256算法计算签名
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(signData));
    }

    /**
     * 对接飞书机器人发送消息（webhook），bearer令牌类型
     *
     * @param url     飞书机器人webhook的值
     * @param msgList 消息内容
     * @param Secret  bearer令牌
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static void sendMsg(String url, List<String> msgList, String Secret) throws NoSuchAlgorithmException, InvalidKeyException {
        //飞书机器人url 通过webhook和密钥将自定义服务的消息推送至飞书
        Map<String, Object> params = new LinkedHashMap<>();
        Long timestamp = DateUtil.getTime(DateUtil.getNowDate());
        String sign = GenSign(Secret, timestamp);
        params.put("msg_type", "text");
        params.put("timestamp", timestamp);
        params.put("sign", sign);
        Map<String, Object> contentMap = new HashMap<>();
        params.put("content", contentMap);

        StringBuilder stringBuilder = new StringBuilder();
        msgList.forEach(e -> {
            stringBuilder.append(e + "\n");
        });

        contentMap.put("text", stringBuilder.toString());


        doPostForJson(url, JSON.toJSONString(params));
    }

    /**
     * 对接飞书机器人发送消息（webhook），无bearer令牌
     *
     * @param url     飞书机器人webhook的值
     * @param msgList 消息内容
     */
    public static void sendMsgNoSecret(String url, List<String> msgList) {
        //飞书机器人url 通过webhook将自定义服务的消息推送至飞书
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("msg_type", "text");
        Map<String, Object> contentMap = new HashMap<>();
        params.put("content", contentMap);

        StringBuilder stringBuilder = new StringBuilder();
        msgList.forEach(e -> {
            stringBuilder.append(e + "\n");
        });

        contentMap.put("text", stringBuilder.toString());

        doPostForJson(url, JSON.toJSONString(params));
    }


    private static Logger logger = LoggerFactory.getLogger(WebHookUtil.class);

    /**
     * 发送POST请求，参数是Map, contentType=x-www-form-urlencoded
     *
     * @param url
     * @param mapParam
     * @return
     */
    public static String sendPostByMap(String url, Map<String, Object> mapParam) {
        Map<String, String> headParam = new HashMap();
        headParam.put("Content-type", "application/json;charset=UTF-8");
        return sendPost(url, mapParam, headParam);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, Object> param, Map<String, String> headParam) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性 请求头
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Fiddler");

            if (headParam != null) {
                for (Map.Entry<String, String> entry : headParam.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(JSON.toJSONString(param));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 发送文字消息
     *
     * @param msg 需要发送的消息
     * @return
     * @throws Exception
     */
    public static String sendTextMsg(String msg) {
        JSONObject text = new JSONObject();
        text.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "text");
        reqBody.put("text", text);
        reqBody.put("safe", 0);

        return reqBody.toString();
    }


    /**
     * 对接企业微信机器人发送消息（webhook）
     *
     * @content：要发送的消息 WECHAT_GROUP：机器人的webhook
     */
    public static JSONObject callWeChatBot(String url, String content) {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        content = sendTextMsg(content);
        RequestBody body = RequestBody.create(content, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        String result = "";
        try {
            response = client.newCall(request).execute();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(response.body().byteStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONObject.parseObject(result);
    }


    /**
     * 对接钉钉机器人发送消息（webhook）
     *
     * @param url 钉钉上获取到的webhook的值
     * @param msg 发送的消息内容
     */
    public static JSONObject sendDDMessage(String url, String msg) {
        //钉钉的webhook
        //请求的JSON数据，这里用map在工具类里转成json格式
        Map<String, Object> json = new HashMap();
        Map<String, Object> text = new HashMap();
        json.put("msgtype", "text");
        text.put("content", msg);
        json.put("text", text);
        //发送post请求
        String response = WebHookUtil.sendPostByMap(url, json);
        return JSONObject.parseObject(response);
    }

    /**
     * 通过加签方式调用钉钉机器人发送消息给所有人
     */
    public static JSONObject sendDingDing(String url, String Secret, String content) {

        try {
            //钉钉机器人地址（配置机器人的webhook）
            Long timestamp = System.currentTimeMillis();
            String sign = GenSign(Secret, timestamp);
            String stringToSign = timestamp + "\n" + Secret;
            String dingUrl = url + "&timestamp=" + timestamp + "&sign=" + URLEncoder.encode(sign, "UTF-8");

            //是否通知所有人
            boolean isAtAll = true;
            //通知具体人的手机号码列表
//            List<String> mobileList = Lists.newArrayList();
            //mobileList.add("+86-159*******");


            //消息内容
            Map<String, String> contentMap = Maps.newHashMap();
            contentMap.put("content", content);
            //通知人
            Map<String, Object> atMap = Maps.newHashMap();
            //1.是否通知所有人
            atMap.put("isAtAll", isAtAll);
            //2.通知具体人的手机号码列表
//            atMap.put("atMobiles", mobileList);

            Map<String, Object> reqMap = Maps.newHashMap();
            reqMap.put("msgtype", "text");
            reqMap.put("text", contentMap);
            reqMap.put("at", atMap);
            //推送消息（http请求）
            String result = sendPostByMap(dingUrl, reqMap);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
