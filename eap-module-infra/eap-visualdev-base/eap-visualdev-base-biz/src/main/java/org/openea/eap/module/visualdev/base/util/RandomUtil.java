package org.openea.eap.module.visualdev.base.util;

//import com.github.yitter.idgen.YitIdHelper;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RandomUtil {
    private static final Logger log = LoggerFactory.getLogger(RandomUtil.class);

    public RandomUtil() {
    }

    public static String uuId() {
        // TODO
        //long newId = YitIdHelper.nextId();
        //return newId + "";
        return UUID.randomUUID().toString();
    }

    public static String enUuId() {
        String str = "";

        for(int i = 0; i < 6; ++i) {
            str = str + (char)((int)(Math.random() * 26.0 + 97.0));
        }

        return str;
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