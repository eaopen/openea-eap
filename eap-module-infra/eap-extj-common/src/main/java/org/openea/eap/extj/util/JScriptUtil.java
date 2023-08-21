package org.openea.eap.extj.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

public class JScriptUtil {
    public static final String JSCONTENT = "var method = function(data) {${jsContent}};var result = method(${data});if(typeof(result)=='object'){JSON.stringify(result);}else{result;};;";

    public JScriptUtil() {
    }

    public static Object callJs(String script) throws ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
        return scriptEngine.eval(script);
    }

    public static Object callJs(String dataProcessing, Object data) throws ScriptException {
        String jsContent = getJsContent(dataProcessing);
        if (StringUtil.isEmpty(dataProcessing)) {
            return data;
        } else {
            String replace = "var method = function(data) {${jsContent}};var result = method(${data});if(typeof(result)=='object'){JSON.stringify(result);}else{result;};;".replace("${jsContent}", jsContent);
            replace = replace.replace("${data}", JsonUtil.getObjectToString(data));
            Object result = null;

            try {
                result = callJs(replace);
            } catch (Exception var9) {
                throw var9;
            }

            try {
                List<Map<String, Object>> jsonToListMap = JsonUtil.getJsonToListMap(result.toString());
                return jsonToListMap;
            } catch (Exception var8) {
                try {
                    Map<String, Object> map = JsonUtil.stringToMap(result.toString());
                    return map;
                } catch (Exception var7) {
                    return result;
                }
            }
        }
    }

    public static String getJsContent(String dataProcessing) {
        if (StringUtil.isNotEmpty(dataProcessing) && dataProcessing.length() > 0) {
            int indexOf = dataProcessing.indexOf("{");
            if (indexOf > -1) {
                dataProcessing = dataProcessing.substring(indexOf + 1);
            }

            int lastIndexOf = dataProcessing.lastIndexOf("}");
            if (lastIndexOf > -1) {
                dataProcessing = dataProcessing.substring(0, lastIndexOf);
            }

            return dataProcessing;
        } else {
            return "";
        }
    }
}