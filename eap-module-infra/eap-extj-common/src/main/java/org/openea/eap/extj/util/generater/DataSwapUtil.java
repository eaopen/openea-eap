package org.openea.eap.extj.util.generater;

import com.alibaba.fastjson.JSONArray;
import org.openea.eap.extj.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DataSwapUtil {
    public DataSwapUtil() {
    }

    public static List convertToList(Object obj) {
        if (obj instanceof List) {
            List arrayList = (List)obj;
            return arrayList;
        } else {
            List arrayList = new ArrayList();
            arrayList.add(obj);
            return arrayList;
        }
    }

    public static String convertValueToString(String obj, boolean mult, boolean isOrg) {
        if (StringUtil.isNotEmpty(obj)) {
            String prefix = "[";
            if (isOrg) {
                prefix = "[[";
            }

            JSONArray arr;
            if (mult) {
                if (!obj.startsWith(prefix)) {
                    arr = new JSONArray();
                    if (isOrg) {
                        arr.add(JSONArray.parse(obj));
                    } else {
                        arr.add(obj);
                    }

                    return arr.toJSONString();
                }
            } else if (obj.startsWith(prefix)) {
                arr = JSONArray.parseArray(obj);
                return arr.size() > 0 ? arr.get(0).toString() : "";
            }
        }

        return obj;
    }
}
