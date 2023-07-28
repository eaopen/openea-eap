package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;

public class ObpmUtil {

    private static ThreadLocal<String> obpmTokenThread = new ThreadLocal<>();

    public static String getObpmToken() {
        return obpmTokenThread.get();
    }

    public static void setObpmToken(String token) {
        obpmTokenThread.set(token);
    }

    public static String eapSign(String user){
        //sign=md5(userKey+day+sysPassword)
        String sign = DigestUtil.md5Hex(user + DateUtil.today() +"eap");
        return sign;
    }
}
