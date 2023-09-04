package org.openea.eap.extj.util;

import cn.hutool.core.util.IdUtil;

import java.util.Random;

public class RandomUtil extends IdUtil {

    public static String enUuId() {
        return IdUtil.simpleUUID();
    }

    public static String uuId() {
        return IdUtil.randomUUID();
    }

    public static Long parses() {
        Long time = 0L;
        return time;
    }

    public static String getRandomCode() {
        String code = "";
        Random rand = new Random();

        for(int i = 0; i < 6; ++i) {
            int ran = rand.nextInt(10);
            code = code + ran;
        }

        return code;
    }
}