package org.openea.eap.module.system.service.language;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.dal.mapper.language.I18nJsonDataMapper;
import org.openea.eap.module.system.dal.mapper.language.LangTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class I18nDataServiceImpl implements I18nDataService {

    private static List<String> langList = new ArrayList<>();
    static {
        // init by config,  BCP 47
        langList.add("en-US");
        langList.add("zh-CN");
        langList.add("zh-HK");
        langList.add("ja-JP");
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
        // map<language, map<key, label>>
        Map<String, Map<String, String >> mLang = new HashMap<>();
        i18nJsonDataMapper.selectList(new QueryWrapper<I18nJsonDataDO>()).forEach(jsonData -> {
            String i18nKey = jsonData.getKey();
            if(StrUtil.isEmpty(i18nKey)){
                if(StrUtil.isNotEmpty(jsonData.getModule())){
                    i18nKey = jsonData.getModule() + "." + jsonData.getName();
                }else{
                    i18nKey = jsonData.getName();
                }
            }
            JSONObject json = JSONUtil.parseObj(jsonData.getJson());
            String finalI18nKey = i18nKey;
            json.keySet().forEach(lang ->{
                if(!mLang.containsKey(lang)){
                    mLang.put(lang, new HashMap<>());
                }
                mLang.get(lang).put(finalI18nKey, json.getStr(lang));
            });
        });
        return mLang;
    }
}
