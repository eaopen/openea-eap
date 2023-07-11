package org.openea.eap.module.system.api.i18n;

import cn.hutool.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 语言/I18n/翻译 API
 *
 */
public interface I18nDataApi {

    /**
     * 获取I8n支持的语言
     * @return list(iso language)
     */
    List<String> getI18nSupportLangs();

    /**
     * 获取i18n数据
     * @param langs 获取指定语言列表，多个逗号分割，默认全部
     * @param modules 获取指定模块列表，多个逗号分割，默认全部
     * @return i18n数据
     * json or map(language, map(key, label))
     */
    Map<String, Map<String, String>> getI18nDataMap(String langs, String modules);
    JSONObject getI18nDataJson(String langs, String modules);

}
