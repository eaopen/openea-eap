package org.openea.eap.extj.util.generater;

import com.alibaba.fastjson.JSONArray;
import org.openea.eap.extj.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线开发，代码生成通用方法
 *
 *
 */
public class DataSwapUtil {
    /**
     * json（string）对象转list
     * @param obj 对象
     * @return
     */
    public static List convertToList(Object obj) {
        if (obj instanceof List) {
            List arrayList = (List) obj;
            return arrayList;
        } else {
            List arrayList = new ArrayList();
            arrayList.add(obj);
            return arrayList;
        }
    }
    /**
     * json（list）对象转string
     * @param mult 多选
     * @param isOrg 是否组织选择（组织选择存在‘[[’）
     * @return
     */
    public static String convertValueToString(String obj, boolean mult, boolean isOrg) {
        if (StringUtil.isNotEmpty(obj)) {
            String prefix = "[";
            if(isOrg){
                prefix = "[[";
            }
            if (mult) {
                if (!obj.startsWith(prefix)) {
                    JSONArray arr = new JSONArray();
                    if(isOrg){
                        //组织多选为二维数组
                        arr.add(JSONArray.parse(obj));
                    }else {
                        arr.add(obj);
                    }
                    return arr.toJSONString();
                }
            } else {
                if (obj.startsWith(prefix)) {
                    JSONArray objects = JSONArray.parseArray(obj);
                    return objects.size() > 0 ? objects.get(0).toString() : "";
                }
            }
        }
        return obj;
    }
}
