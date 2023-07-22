package org.openea.eap.module.obpm.service.obpm;

public class ObpmUtil {

    private static ThreadLocal<String> obpmTokenThread = new ThreadLocal<>();

    public static String getObpmToken() {
        return obpmTokenThread.get();
    }

    public static void setObpmToken(String token) {
        obpmTokenThread.set(token);
    }
}
