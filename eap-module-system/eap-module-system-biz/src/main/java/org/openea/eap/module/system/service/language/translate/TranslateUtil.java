package org.openea.eap.module.system.service.language.translate;

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
}
