package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ObmpClientService {

    @Value("${eap.obpm.apiBaseUrl:/obpm-server}")
    private String obpmClientBaseUrl;

    private int timeoutMillSecs = 10000; //milliseconds

    @Resource
    private DataSource dataSource;

    public  JdbcTemplate getObpmJdbcTemplate(){
        JdbcTemplate obpmJdbcTemplate = null;
        String obpmDsName = "obpm";
        try{
            // strict mode
            if(((DynamicRoutingDataSource)dataSource).getDataSources().containsKey(obpmDsName)){
                DataSource obpmDs = ((DynamicRoutingDataSource)dataSource).getDataSource(obpmDsName);
                obpmJdbcTemplate = new JdbcTemplate(obpmDs);
                return obpmJdbcTemplate;
            }else{
                log.error("obpmJdbcTemplate fail: DataSource "+obpmDsName+" not exist.");
            }
        }catch (Throwable t){
            log.error("obpmJdbcTemplate fail:"+t.getMessage(), t);
        }
        return obpmJdbcTemplate;
    }


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
            jsonResult.set("token",obpmToken);
            jsonResult.set("user", resultData.getJSONObject("user"));
        }else{
            jsonResult.set("msg", resultObj.getStr("msg"));
        }
        return jsonResult;
    }

    public JSONObject queryUserInfo(String userKey, boolean withPassword){
        JSONObject userInfo = null;
        Map<String, Object> params = new HashMap<>();
        params.put("user", userKey);
        params.put("withPassword", withPassword);
        params.put("sign", eapSign(userKey));
        String result = HttpUtil.get(obpmClientBaseUrl+"/eap/userInfo", params, timeoutMillSecs);
        if(result.startsWith("<html>")){
            JSONObject logJson = new JSONObject();
            logJson.set("url", obpmClientBaseUrl+"/eap/userInfo");
            logJson.set("params", params);
            logJson.set("result", result);
            log.warn(logJson.toStringPretty());
            throw new RuntimeException("queryUserInfo fail, userKey="+userKey+", url="+obpmClientBaseUrl+"/eap/userInfo");
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

    public List<JSONObject> queryUserList(Date lastSyncTime){
        List<JSONObject> userList = null;
        String result = null;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("lastSyncTime", lastSyncTime);
            String userKey = "admin";
            params.put("user", userKey);
            params.put("sign", eapSign(userKey));
            result = HttpUtil.get(obpmClientBaseUrl+"/eap/userList", params, 60000);
            JSONObject resultObj = JSONUtil.parseObj(result);
            if(resultObj.getBool("isOk")){
                userList = resultObj.getBeanList("data", JSONObject.class);
            }else{
                //jsonResult.put("msg", resultObj.getString("msg"));
                throw new RuntimeException(resultObj.getStr("msg"));
            }
        }catch (Throwable t){
            log.warn("queryUserList fail:"+t.getMessage()+" , result="+result);
        }
        return userList;
    }

    public List<JSONObject> queryUserMenu(String userKey, String systemKey, boolean withButton){
        List<JSONObject> menuList = null;
        // get/post /eap/userMenu
        // user=[userKey]  system=[systemKey] withButton=[withButton]
        JSONObject resultObj = null;
        String result = null;
        try{
            Map<String, Object> params = new HashMap<>();
            params.put("user", userKey);
            params.put("system", systemKey);
            params.put("sign", eapSign(userKey));
            result = HttpUtil.get(obpmClientBaseUrl+"/eap/userMenu", params, timeoutMillSecs);
            resultObj = JSONUtil.parseObj(result);
            if(resultObj!=null && resultObj.getBool("isOk")){
                JSONObject resultData = resultObj.getJSONObject("data");
                JSONArray menuArray = resultData.getJSONArray("menuList");
                menuList = menuArray.toList(JSONObject.class);
                return menuList;
            }
        }catch (Exception e){
            //throw new ServiceException(1, e.getMessage());
            log.warn("queryUserMenu(user="+userKey+") fail: "+e.getMessage()+", result="+result);
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
            // 去掉 "/admin-api"
            if(url.startsWith("/admin-api")){
                url = url.substring(url.indexOf("/admin-api")+10);
            }
            // 检查obpm后端API前缀  /obpm-server, /obpm-api, /obpm
            // 检查obpm前端UI bpm-admin前缀 /obpm-admin, /obpm-web1
            // 前端代理性能差，仅用于开发或演示环境，生产可配置nginx反向代理
            if(url.indexOf("/obpm-server/")>=0){
                obpmUrl += url.substring(url.indexOf("/obpm-server")+12);
            }else if(url.indexOf("/obpm-api/")>=0){
                obpmUrl += url.substring(url.indexOf("/obpm-api")+9);
            }else if(url.indexOf("/obpm/")>=0) {
                obpmUrl += url.substring(url.indexOf("/obpm") + 5);

            }else if(url.indexOf("/obpm-admin/")>=0){
                obpmUrl += "/bpm-admin"+ url.substring(url.indexOf("/obpm-admin")+11);
            }else if(url.indexOf("/obpm-web1/")>=0){
                obpmUrl += "/bpm-admin"+ url.substring(url.indexOf("/obpm-web1")+10);

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
