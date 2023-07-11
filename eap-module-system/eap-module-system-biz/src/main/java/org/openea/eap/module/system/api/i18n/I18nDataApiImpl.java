package org.openea.eap.module.system.api.i18n;

import cn.hutool.json.JSONObject;
import org.openea.eap.module.system.service.language.I18nDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class I18nDataApiImpl implements I18nDataApi {

    @Resource
    private I18nDataService i18nDataService;

    @Override
    public List<String> getI18nSupportLangs() {
        return i18nDataService.getI18nSupportLangs();
    }

    @Override
    public Map<String, Map<String, String>> getI18nDataMap(String langs, String modules) {
        return i18nDataService.getI18nDataMap(langs, modules);
    }

    @Override
    public JSONObject getI18nDataJson(String langs, String modules) {
        JSONObject i18nJson = new JSONObject();
        i18nJson.putAll(getI18nDataMap(langs, modules));
        return i18nJson;
    }
}
