package org.openea.eap.extj.util;

import java.util.HashMap;
import java.util.Map;

public class LockObjectUtil {

    private static Map<Object, Object> lockMap = new HashMap<>();

    public static synchronized Object addLockKey(Object key){
        Object val = lockMap.get(key);
        if(val == null){
            lockMap.put(key, key);
            val = key;
        }
        return val;
    }


}
