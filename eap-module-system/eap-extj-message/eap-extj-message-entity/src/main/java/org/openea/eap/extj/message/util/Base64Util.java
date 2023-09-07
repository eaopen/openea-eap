package org.openea.eap.extj.message.util;


import org.apache.commons.codec.binary.Base64;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.framework.common.util.spring.EapAppUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Base64Util {

    private static ConfigValueUtil configValueUtil = EapAppUtil.getBean(ConfigValueUtil.class);


    /**
     * 把文件转化为base64.
     *
     * @param filePath 源文件路径
     */
    public static String fileToBase64(String filePath) {
        if (!StringUtil.isEmpty(filePath)) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                return Base64.encodeBase64String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
