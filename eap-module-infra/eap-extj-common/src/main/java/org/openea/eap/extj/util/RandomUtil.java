package org.openea.eap.extj.util;

import cn.hutool.core.util.IdUtil;

public class RandomUtil extends IdUtil {

    public static String enUuId() {
        return IdUtil.simpleUUID();
    }

    public static String uuId() {
        return IdUtil.randomUUID();
    }
}