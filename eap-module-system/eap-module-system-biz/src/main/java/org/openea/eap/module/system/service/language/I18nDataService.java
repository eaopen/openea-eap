package org.openea.eap.module.system.service.language;

import cn.hutool.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * i18n 国际化数据服务
 */
public interface I18nDataService {
    List<String> getI18nSupportLangs();

    Map<String, Map<String, String>> getI18nDataMap(String langs, String modules);

    JSONObject getJsJson();
}
