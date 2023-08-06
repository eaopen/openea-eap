package org.openea.eap.extj.message.util.unipush;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class UinPush {
    @Autowired
    private ConfigValueUtil configValueUtil;

    public void sendUniPush(List<String> cidList, String title, String content, String type, String text) {
        if (cidList != null && cidList.size() > 0) {
            String cid = StringUtils.join(cidList, ",");
            String url = configValueUtil.getAppPushUrl() + "?clientId=" + cid + "&title=" + title + "&content=" + content + "&text=" + text + "&create=true";
            cn.hutool.http.HttpUtil.get(url);
        }
    }

}
