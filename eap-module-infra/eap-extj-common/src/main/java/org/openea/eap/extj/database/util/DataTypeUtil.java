package org.openea.eap.extj.database.util;

import java.util.regex.Pattern;

public class DataTypeUtil {

    /**
     * 数据类型判断
     */
    public static Boolean numFlag(String... nums){
        for (String num : nums) {
            if(!(Pattern.compile("^[-\\+]?[\\d]*$").matcher(num).matches())){
                return false;
            }
        }
        return true;
    }

}
