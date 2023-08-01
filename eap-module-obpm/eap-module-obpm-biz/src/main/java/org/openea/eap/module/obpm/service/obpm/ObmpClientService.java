package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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
        JSONObject resultObj = JSONUtil.parseObj(result);

        JSONObject jsonResult = new JSONObject();
        if(resultObj.getBool("isOk")){
            JSONObject resultData = resultObj.getJSONObject("data");
            String obpmToken =  resultData.getStr("token");
            ObpmUtil.setObpmToken(obpmToken);
            jsonResult.put("token",obpmToken);
            jsonResult.put("user", resultData.getJSONObject("user"));
        }else{
            jsonResult.put("msg", resultObj.getStr("msg"));
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
        if(result.startsWith("<html>")){
            throw new RuntimeException(result);
        }
        JSONObject resultObj = JSONUtil.parseObj(result);
        if(resultObj.getBool("isOk")){
            userInfo = resultObj.getJSONObject("data");
        }else{
            //jsonResult.put("msg", resultObj.getString("msg"));
            throw new RuntimeException(resultObj.getStr("msg"));
        }
        return userInfo;
    }

    public List<JSONObject> queryUserMenu(String userKey, String systemKey, boolean withButton){
        List<JSONObject> menuList = null;
        // get/post /eap/userMenu
        // user=[userKey]  system=[systemKey] withButton=[withButton]
        JSONObject resultObj = null;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("user", userKey);
            params.put("system", systemKey);
            params.put("sign", eapSign(userKey));
            String result = HttpUtil.get(obpmClientBaseUrl+"/eap/userMenu", params, 6000);
            resultObj = JSONUtil.parseObj(result);
            if(resultObj!=null && resultObj.getBool("isOk")){
                JSONObject resultData = resultObj.getJSONObject("data");
                JSONArray menuArray = resultData.getJSONArray("menuList");
                menuList = menuArray.toList(JSONObject.class);
                return menuList;
            }
        }catch (Exception e){
            //throw new ServiceException(1, e.getMessage());
            log.warn("queryUserMenu(user="+userKey+") fail: "+e.getMessage());
        }
        if(resultObj!=null && resultObj.containsKey("msg")){
            //throw new ServiceException(1, resultObj.getString("msg"));
            log.warn("queryUserMenu(user="+userKey+") return: "+resultObj.getStr("msg"));
        }
        return menuList;
    }

    private String eapSign(String user){
        return ObpmUtil.eapSign(user);
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
        // test only
        // etech url=http://10.9.8.162:6110
        if(obpmClientBaseUrl.indexOf("10.9.8.162")>0 || obpmClientBaseUrl.indexOf("10.8.1.81")>0){
            obpmUrl = obpmUrl.replace("/form/formCustSql/view/vo_","/etech/formCustSql/view/vo3_");
            obpmUrl = obpmUrl.replace("/form/formCustSql/view/list_","/etech/formCustSql/view/list_");
        }
        return obpmUrl;
    }

}
