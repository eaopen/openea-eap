package org.openea.eap.module.system.service.language.translate;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslateUtil {

    public static String translateText(String text, String sourceLang, String targetLang){
        String result = null;
        try {
            result =  GT.getInstance().translateText(text, sourceLang, targetLang);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static JSONObject queryMenuI18n(String type, String key, String name, int len) {
        JSONObject json = null;
        String strJson = null;
        try{
            strJson = ChatGPT.getInstance().queryMenuI18n(type, key, name, len);
            json = JSONUtil.parseObj(strJson);
        }catch (Exception e){
            log.warn(e.getMessage()+"\r\n"+strJson);
            log.debug(String.format("queryMenuI18n key=%s name=%s",key, name),e);
        }
        return json;
    }
}
