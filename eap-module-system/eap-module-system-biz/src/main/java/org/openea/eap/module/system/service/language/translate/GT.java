package org.openea.eap.module.system.service.language.translate;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * google translate
 */
public class GT {
    private static GT _instance = null;

    private static int timeout = 5000;

    /**
     * 获取单例
     * @return
     */
    public static GT getInstance() {
        if( null == _instance){
            _instance = new GT();
            _instance.init();
        }
        return _instance;
    }

    /**
     * 翻译文本
     * @param text  文本内容
     * @param sourceLang  文本所属语言。如果不知道，可以使用auto
     * @param targetLang  目标语言。必须是明确的有效的目标语言
     * @return
     * @throws Exception
     */
    public String translateText(String text,String sourceLang, String targetLang) throws Exception{
        String retStr="";
        if(StringUtils.isEmpty(sourceLang)){
            sourceLang = "auto";
        }else{
            sourceLang = sourceLang.toLowerCase().replace("-","_");
        }
        if(!StringUtils.isEmpty(targetLang)){
            targetLang = targetLang.toLowerCase().replace("-","_");
            if("en_us".equalsIgnoreCase(targetLang)){
                targetLang = "en";
            }
            if("ja_jp".equalsIgnoreCase(targetLang)){
                targetLang = "ja";
            }
        }
        if( !( isSupport(sourceLang) || isSupport(targetLang) ) ){
            throw new Exception("不支持的语言类型");
        }
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("client", CLIENT);
        formMap.put("sl", sourceLang);
        formMap.put("tl", targetLang);
        formMap.put("dt", "t");
        formMap.put("q", text);
        String resp = HttpRequest.get(PATH).form(formMap)
                .header("User-Agent", USER_AGENT).charset("UTF8").timeout(timeout).execute().body();
//        String finalPath=PATH +"?client="+CLIENT+"&sl="+sourceLang+"&tl="+targetLang+"&dt=t&q="+ text ;
        if( null == resp ){
            throw  new Exception("网络异常");
        }
//        System.out.println( "==>返回内容：" + resp);
        JSONArray jsonObject = JSONArray.parseArray( resp );
        for (Iterator<Object> it = jsonObject.getJSONArray(0).iterator(); it.hasNext(); ) {
            JSONArray a = (JSONArray) it.next();
            retStr += a.getString(0);
        }
        return retStr;
    }

    /**
     * 判断语言是否支持
     * @param language
     * @return
     */
    public boolean isSupport(String language){
        if( null == LANGUAGE_MAP.get( language )){
            return false;
        }
        return true;
    }

    /** 获取 语言代码
     * ISO 639-1 code
     * @param desiredLang 语言
     * @return 如果返回null则标示不支持
     */
    public String getCode(String desiredLang){
        if( null != LANGUAGE_MAP.get(desiredLang)){
            return desiredLang;
        }
        String tmp = desiredLang.toLowerCase();
        for(Map.Entry<String, String> enter : LANGUAGE_MAP.entrySet() ){
            if( enter.getValue().equals( tmp)){
                return enter.getKey();
            }
        }
        return null;
    }

    private static final String PATH="https://translate.googleapis.com/translate_a/single"; //地址
    private static final String CLIENT="gtx";
    private static final String USER_AGENT="Mozilla/5.0";
    //"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
    private static final Map<String,String> LANGUAGE_MAP=new HashMap();
    /**
     * 初始化语言类
     */
    private void init(){
        LANGUAGE_MAP.put("auto","Automatic");
        LANGUAGE_MAP.put("af","Afrikaans");
        LANGUAGE_MAP.put("sq","Albanian");
        LANGUAGE_MAP.put("am","Amharic");
        LANGUAGE_MAP.put("ar","Arabic");
        LANGUAGE_MAP.put("hy","Armenian");
        LANGUAGE_MAP.put("az","Azerbaijani");
        LANGUAGE_MAP.put("eu","Basque");
        LANGUAGE_MAP.put("be","Belarusian");
        LANGUAGE_MAP.put("bn","Bengali");
        LANGUAGE_MAP.put("bs","Bosnian");
        LANGUAGE_MAP.put("bg","Bulgarian");
        LANGUAGE_MAP.put("ca","Catalan");
        LANGUAGE_MAP.put("ceb","Cebuano");
        LANGUAGE_MAP.put("ny","Chichewa");
        LANGUAGE_MAP.put("zh_cn","Chinese Simplified");
        LANGUAGE_MAP.put("zh_tw","Chinese Traditional");
        LANGUAGE_MAP.put("co","Corsican");
        LANGUAGE_MAP.put("hr","Croatian");
        LANGUAGE_MAP.put("cs","Czech");
        LANGUAGE_MAP.put("da","Danish");
        LANGUAGE_MAP.put("nl","Dutch");
        LANGUAGE_MAP.put("en","English");
        LANGUAGE_MAP.put("eo","Esperanto");
        LANGUAGE_MAP.put("et","Estonian");
        LANGUAGE_MAP.put("tl","Filipino");
        LANGUAGE_MAP.put("fi","Finnish");
        LANGUAGE_MAP.put("fr","French");
        LANGUAGE_MAP.put("fy","Frisian");
        LANGUAGE_MAP.put("gl","Galician");
        LANGUAGE_MAP.put("ka","Georgian");
        LANGUAGE_MAP.put("de","German");
        LANGUAGE_MAP.put("el","Greek");
        LANGUAGE_MAP.put("gu","Gujarati");
        LANGUAGE_MAP.put("ht","Haitian Creole");
        LANGUAGE_MAP.put("ha","Hausa");
        LANGUAGE_MAP.put("haw","Hawaiian");
        LANGUAGE_MAP.put("iw","Hebrew");
        LANGUAGE_MAP.put("hi","Hindi");
        LANGUAGE_MAP.put("hmn","Hmong");
        LANGUAGE_MAP.put("hu","Hungarian");
        LANGUAGE_MAP.put("is","Icelandic");
        LANGUAGE_MAP.put("ig","Igbo");
        LANGUAGE_MAP.put("id","Indonesian");
        LANGUAGE_MAP.put("ga","Irish");
        LANGUAGE_MAP.put("it","Italian");
        LANGUAGE_MAP.put("ja","Japanese");
        LANGUAGE_MAP.put("jw","Javanese");
        LANGUAGE_MAP.put("kn","Kannada");
        LANGUAGE_MAP.put("kk","Kazakh");
        LANGUAGE_MAP.put("km","Khmer");
        LANGUAGE_MAP.put("ko","Korean");
        LANGUAGE_MAP.put("ku","Kurdish (Kurmanji)");
        LANGUAGE_MAP.put("ky","Kyrgyz");
        LANGUAGE_MAP.put("lo","Lao");
        LANGUAGE_MAP.put("la","Latin");
        LANGUAGE_MAP.put("lv","Latvian");
        LANGUAGE_MAP.put("lt","Lithuanian");
        LANGUAGE_MAP.put("lb","Luxembourgish");
        LANGUAGE_MAP.put("mk","Macedonian");
        LANGUAGE_MAP.put("mg","Malagasy");
        LANGUAGE_MAP.put("ms","Malay");
        LANGUAGE_MAP.put("ml","Malayalam");
        LANGUAGE_MAP.put("mt","Maltese");
        LANGUAGE_MAP.put("mi","Maori");
        LANGUAGE_MAP.put("mr","Marathi");
        LANGUAGE_MAP.put("mn","Mongolian");
        LANGUAGE_MAP.put("my","Myanmar (Burmese)");
        LANGUAGE_MAP.put("ne","Nepali");
        LANGUAGE_MAP.put("no","Norwegian");
        LANGUAGE_MAP.put("ps","Pashto");
        LANGUAGE_MAP.put("fa","Persian");
        LANGUAGE_MAP.put("pl","Polish");
        LANGUAGE_MAP.put("pt","Portuguese");
        LANGUAGE_MAP.put("ma","Punjabi");
        LANGUAGE_MAP.put("ro","Romanian");
        LANGUAGE_MAP.put("ru","Russian");
        LANGUAGE_MAP.put("sm","Samoan");
        LANGUAGE_MAP.put("gd","Scots Gaelic");
        LANGUAGE_MAP.put("sr","Serbian");
        LANGUAGE_MAP.put("st","Sesotho");
        LANGUAGE_MAP.put("sn","Shona");
        LANGUAGE_MAP.put("sd","Sindhi");
        LANGUAGE_MAP.put("si","Sinhala");
        LANGUAGE_MAP.put("sk","Slovak");
        LANGUAGE_MAP.put("sl","Slovenian");
        LANGUAGE_MAP.put("so","Somali");
        LANGUAGE_MAP.put("es","Spanish");
        LANGUAGE_MAP.put("su","Sundanese");
        LANGUAGE_MAP.put("sw","Swahili");
        LANGUAGE_MAP.put("sv","Swedish");
        LANGUAGE_MAP.put("tg","Tajik");
        LANGUAGE_MAP.put("ta","Tamil");
        LANGUAGE_MAP.put("te","Telugu");
        LANGUAGE_MAP.put("th","Thai");
        LANGUAGE_MAP.put("tr","Turkish");
        LANGUAGE_MAP.put("uk","Ukrainian");
        LANGUAGE_MAP.put("ur","Urdu");
        LANGUAGE_MAP.put("uz","Uzbek");
        LANGUAGE_MAP.put("vi","Vietnamese");
        LANGUAGE_MAP.put("cy","Welsh");
        LANGUAGE_MAP.put("xh","Xhosa");
        LANGUAGE_MAP.put("yi","Yiddish");
        LANGUAGE_MAP.put("yo","Yoruba");
        LANGUAGE_MAP.put("zu","Zulu");
    }

}
