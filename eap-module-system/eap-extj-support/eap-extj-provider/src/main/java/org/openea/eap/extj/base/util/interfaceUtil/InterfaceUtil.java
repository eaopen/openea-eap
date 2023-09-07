package org.openea.eap.extj.base.util.interfaceUtil;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.ServletUtil;
import org.openea.eap.extj.util.StringUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 接口工具类
 *
 * 
 */
public class InterfaceUtil {
    public static final String ALGORITH_FORMAC = "HmacSHA256";
    public static final String HOST = "Host";
    public static final String YMDATE = "YmDate";
    public static final String CONTENT_TYPE = " Content-Type";
    public static final String CHARSET_NAME = "utf-8";

    /**
     * 验证签名
     * @param
     * @return
     */
    public static boolean verifySignature(String secret, String author) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String method = ServletUtil.getRequest().getMethod();
        String url = ServletUtil.getRequest().getRequestURI();
        String ymdate = ServletUtil.getRequest().getHeader(YMDATE);
        String host = ServletUtil.getRequest().getHeader(HOST);
        String source = new StringBuilder()
                .append(method).append('\n')
                .append(url).append('\n')
                .append(ymdate).append('\n')
                .append(host).append('\n').toString();
        Mac mac = Mac.getInstance(ALGORITH_FORMAC);
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(secret), ALGORITH_FORMAC);
        mac.init(secretKeySpec);
        String signature = Hex.encodeHexString(mac.doFinal(source.getBytes(CHARSET_NAME)));
        if (author.equals(signature)) {
            return true;
        }
        return false;
    }
    /**
     * map转 name=value&name=value格式
     * @param
     * @return
     */
    public static String createLinkStringByGet(Map<String, String> params) throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            value = URLEncoder.encode(value, "UTF-8");
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 判断map内有没有指定key的值
     * @param
     * @return
     */
    public static boolean checkParam(Map<String,String> map,String str){
        if(CollectionUtil.isEmpty(map)){
            return false;
        }
        if(StringUtil.isEmpty(str)){
            return false;
        }
        if(map.get(str)!=null&& StringUtil.isNotEmpty(map.get(str))){
            return true;
        }
        return false;
    }

//
    public static Map<String, String>   getAuthorization(String intefaceId, String appId, String appSecret, Map<String, String> map){
        Map<String, String> resultMap=new HashMap<>();
        try {
            String method = ServletUtil.getRequest().getMethod();
            String url ="/api/system/DataInterface/"+intefaceId+"/Actions/Response";
            String ymdate = ""+DateUtil.getNowDate().getTime();
            String host = ServletUtil.getRequest().getHeader(HOST);
            String source = new StringBuilder()
                    .append(method).append('\n')
                    .append(url).append('\n')
                    .append(ymdate).append('\n')
                    .append(host).append('\n').toString();
            Mac mac = Mac.getInstance(ALGORITH_FORMAC);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.decodeBase64(appSecret), ALGORITH_FORMAC);
            mac.init(secretKeySpec);
            String signature = Hex.encodeHexString(mac.doFinal(source.getBytes(CHARSET_NAME)));
            resultMap.put("YmDate",ymdate);
            resultMap.put("Authorization",appId+"::"+signature);
            return resultMap;
        }catch (Exception e){}
        return resultMap;
    }
}
