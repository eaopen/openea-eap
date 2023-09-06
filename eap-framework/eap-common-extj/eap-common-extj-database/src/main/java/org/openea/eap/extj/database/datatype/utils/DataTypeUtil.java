package org.openea.eap.extj.database.datatype.utils;

import java.util.regex.Pattern;

/**
 * 类功能
 *
 * 
 */
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
