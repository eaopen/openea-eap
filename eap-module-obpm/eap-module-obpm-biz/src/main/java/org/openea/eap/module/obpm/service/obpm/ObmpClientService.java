package org.openea.eap.module.obpm.service.obpm;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObmpClientService {
    public JSONObject login(String username, String password) {

        JSONObject jsonResult = new JSONObject();
        // token
        // msg
        // user

        // post /eap/login  account=username&password=password
        // sign=md5(userKey+day+"eap")
        return jsonResult;
    }

    public List<JSONObject> queryUserMenu(String userKey, String systemKey, boolean withButton){
        JSONObject jsonResult = new JSONObject();
        // menuList

        // get/post /eap/userMenu
        // user=[userKey]  system=[systemKey] withButton=[withButton]
        // sign=md5(userKey+day+"eap")

        return null;
    }
}
