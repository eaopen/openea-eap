package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObmpClientService {

    @Value("${eap.obpm.apiBaseUrl:/obpm-server}")
    private String obpmClientBaseUrl;

    public JSONObject login(String username, String password) {
        // post /eap/login  account=username&password=password
        // sign=md5(userKey+day+"eap")
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("sign", eapSign(username));
        String result = HttpUtil.post(obpmClientBaseUrl+"/eap/login", params);
        JSONObject resultObj = JSONObject.parseObject(result);

        JSONObject jsonResult = new JSONObject();
        if(resultObj.getBoolean("isOk")){
            JSONObject resultData = resultObj.getJSONObject("data");
            String obpmToken =  resultData.getString("token");
            ObpmUtil.setObpmToken(obpmToken);
            jsonResult.put("token",obpmToken);
            jsonResult.put("user", resultData.getJSONObject("user"));
        }else{
            jsonResult.put("msg", resultObj.getString("msg"));
        }
        return jsonResult;
    }

    public JSONObject queryUserInfo(String userKey, boolean withPassword){
        JSONObject userInfo = null;
        Map<String, Object> params = new HashMap<>();
        params.put("user", userKey);
        params.put("withPassword", withPassword);
        params.put("sign", eapSign(userKey));
        String result = HttpUtil.get(obpmClientBaseUrl+"/eap/userInfo", params);
        JSONObject resultObj = JSONObject.parseObject(result);
        if(resultObj.getBoolean("isOk")){
            userInfo = resultObj.getJSONObject("data");
        }else{
            //jsonResult.put("msg", resultObj.getString("msg"));
            throw new RuntimeException(resultObj.getString("msg"));
        }
        return userInfo;
    }

    public List<JSONObject> queryUserMenu(String userKey, String systemKey, boolean withButton){
        // get/post /eap/userMenu
        // user=[userKey]  system=[systemKey] withButton=[withButton]
        Map<String, Object> params = new HashMap<>();
        params.put("user", userKey);
        params.put("system", systemKey);
        params.put("sign", eapSign(userKey));
        String result = HttpUtil.get(obpmClientBaseUrl+"/eap/userMenu", params);
        JSONObject resultObj = JSONObject.parseObject(result);

        List<JSONObject> menuList = null;
        if(resultObj.getBoolean("isOk")){
            JSONObject resultData = resultObj.getJSONObject("data");
            JSONArray menuArray = resultData.getJSONArray("menuList");
            menuList = menuArray.toJavaList(JSONObject.class);
        }else{
            //jsonResult.put("msg", resultObj.getString("msg"));
            throw new RuntimeException(resultObj.getString("msg"));
        }
        return menuList;
    }

    private String eapSign(String user){
        //sign=md5(userKey+day+sysPassword)
        String sign = DigestUtil.md5Hex(user + DateUtil.today() +"eap");
        return sign;
    }



    public String getProxyUrl(String url) {
        String obpmUrl = obpmClientBaseUrl;
        if(ObjectUtils.isNotEmpty(url)){
            if(url.startsWith("/admin-api")){
                obpmUrl += url.substring(url.indexOf("/admin-api")+10);
            }else if(url.indexOf("/obpm/")>=0){
                obpmUrl += url.substring(url.indexOf("/obpm")+5);
            }else if(url.startsWith("/")){
                obpmUrl += url;
            }else{
                obpmUrl += "/" + url;
            }
        }
        return obpmUrl;
    }

}
