package org.openea.eap.extj.database.datatype.utils;

import java.util.regex.Pattern;

public class DataTypeUtil {
    public DataTypeUtil() {
    }

    public static Boolean numFlag(String... nums) {
        String[] var1 = nums;
        int len = nums.length;

        for(int i = 0; i < len; ++i) {
            String num = var1[i];
            if (!Pattern.compile("^[-\\+]?[\\d]*$").matcher(num).matches()) {
                return false;
            }
        }

        return true;
    }
}
