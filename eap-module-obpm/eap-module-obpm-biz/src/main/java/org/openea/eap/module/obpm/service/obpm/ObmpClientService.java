package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
            jsonResult.put("token", resultData.getString("token"));
            jsonResult.put("user", resultData.getJSONObject("user"));
        }else{
            jsonResult.put("msg", resultObj.getString("msg"));
        }
        return jsonResult;
    }

    public List<JSONObject> queryUserMenu(String userKey, String systemKey, boolean withButton){
        // get/post /eap/userMenu
        // user=[userKey]  system=[systemKey] withButton=[withButton]
        Map<String, Object> params = new HashMap<>();
        params.put("user", userKey);
        params.put("system", systemKey);
        params.put("sign", eapSign(userKey));
        String result = HttpUtil.post(obpmClientBaseUrl+"/eap/userMenu", params);
        JSONObject resultObj = JSONObject.parseObject(result);

        List<JSONObject> menuList = null;
        JSONObject jsonResult = new JSONObject();
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
}
