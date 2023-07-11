package org.openea.eap.module.system.service.language;

import org.openea.eap.module.system.dal.mapper.language.I18nJsonDataMapper;
import org.openea.eap.module.system.dal.mapper.language.LangTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class I18nDataServiceImpl implements I18nDataService {

    private static List<String> langList = new ArrayList<>();
    static {
        // init by config
        langList.add("en_US");
        langList.add("zh_CN");
        langList.add("zh_HK");
        langList.add("ja_JP");
    }

    @Resource
    private LangTypeMapper langTypeMapper;

    @Resource
    private I18nJsonDataMapper i18nJsonDataMapper;


    @Override
    public List<String> getI18nSupportLangs() {
        return langList;
    }

    @Override
    public Map<String, Map<String, String>> getI18nDataMap(String langs, String modules) {
        return null;
    }
}
