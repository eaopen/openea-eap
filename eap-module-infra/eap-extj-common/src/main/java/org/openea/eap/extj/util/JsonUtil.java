package org.openea.eap.extj.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.openea.eap.extj.exception.DataException;

import java.util.List;
import java.util.Map;

public class JsonUtil {
    public JsonUtil() {
    }

    public static List listToJsonField(List lists) {
        String jsonStr = JSONArray.toJSONString(lists, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
        List list = (List)JSONArray.parseObject(jsonStr, List.class);
        return list;
    }

    public static Map<String, Object> entityToMap(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        Map<String, Object> map = (Map)JSONObject.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {
        }, new Feature[0]);
        return map;
    }

    public static Map<String, String> entityToMaps(Object object) {
        String jsonStr = JSONObject.toJSONString(object);
        Map<String, String> map = (Map)JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>() {
        }, new Feature[0]);
        return map;
    }

    public static Map<String, Object> stringToMap(String object) {
        Map<String, Object> map = (Map)JSONObject.parseObject(object, new TypeReference<Map<String, Object>>() {
        }, new Feature[0]);
        return map;
    }

    public static <T> T getJsonToBean(String jsonData, Class<T> clazz) {
        return JSON.parseObject(jsonData, clazz);
    }

    public static JSONArray getJsonToJsonArray(String json) {
        return JSONArray.parseArray(json);
    }

    public static <T> JSONArray getListToJsonArray(List<T> list) {
        return JSONArray.parseArray(getObjectToString(list));
    }

    public static String getObjectToString(Object object) {
        return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    public static String getObjectToStringAsDate(Object object) {
        return JSON.toJSONStringWithDateFormat(object, "yyy-MM-dd HH:mm:ss", new SerializerFeature[0]);
    }

    public static String getObjectToStringDateFormat(Object object, String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    public static <T> T getJsonToBeanEx(Object dto, Class<T> clazz) throws DataException {
        if (dto == null) {
            throw new DataException("此条数据不存在");
        } else {
            return JSON.parseObject(getObjectToString(dto), clazz);
        }
    }

    public static <T> List<T> getJsonToList(String jsonData, Class<T> clazz) {
        return JSON.parseArray(jsonData, clazz);
    }

    public static List<Map<String, Object>> getJsonToListMap(String jsonData) {
        return (List)JSON.parseObject(jsonData, new TypeReference<List<Map<String, Object>>>() {
        }, new Feature[0]);
    }

    public static List<Map<String, Object>> getJsonToList(JSONArray jsonArray) {
        return (List)JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Map<String, Object>>>() {
        }, new Feature[0]);
    }

    public static <T> T getJsonToBean(Object dto, Class<T> clazz) {
        return JSON.parseObject(getObjectToString(dto), clazz);
    }

    public static <T> List<T> getJsonToList(Object dto, Class<T> clazz) {
        return JSON.parseArray(getObjectToString(dto), clazz);
    }
}
