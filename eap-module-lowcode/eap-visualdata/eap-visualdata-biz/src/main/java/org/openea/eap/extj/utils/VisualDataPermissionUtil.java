package org.openea.eap.extj.utils;

import cn.dev33.satoken.stp.StpUtil;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.util.ServletUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VisualDataPermissionUtil {

    private static ConfigValueUtil configValueUtil;
    private static final String[] refererPath = new String[]{"**/DataV/view/{id}", "**/DataV/build/{id}"};

    public VisualDataPermissionUtil(ConfigValueUtil configValueUtil) {
        VisualDataPermissionUtil.configValueUtil = configValueUtil;
    }

    public static void checkByReferer() {
        if (configValueUtil.isEnablePreAuth()) {
            String referer = ServletUtil.getHeader("Referer");
            String id = null;
            for (String s : refererPath) {
                Map<String, String> pathVariables = ServletUtil.getPathVariables(s, referer);
                id = pathVariables.get("id");
                if (id != null) {
                    id = id.split("[?]")[0];
                    break;
                }
            }
            StpUtil.checkPermission(id);
        }
    }
}
