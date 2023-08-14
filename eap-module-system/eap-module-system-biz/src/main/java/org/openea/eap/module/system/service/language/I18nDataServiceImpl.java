package org.openea.eap.module.system.service.language;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.mysql.language.I18nJsonDataMapper;
import org.openea.eap.module.system.dal.mysql.language.LangTypeMapper;
import org.openea.eap.module.system.service.permission.MenuService;
import org.openea.eap.module.system.service.permission.MenuServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private MenuService menuService;


    @Override
    public List<String> getI18nSupportLangs() {
        return langList;
    }

    @Override
    public Map<String, Map<String, String>> getI18nDataMap(String langs, String modules) {
        // map<language, map<key, label>>
        Map<String, Map<String, String >> mLang = new HashMap<>();
        i18nJsonDataMapper.selectList(new QueryWrapper<I18nJsonDataDO>()).forEach(jsonData -> {
            String i18nKey = jsonData.getAlias();
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

    @Override
    public JSONObject getJsJson() {
        JSONObject jsJson = new JSONObject();
        // language
        Map<String, Map<String, String>> mapI18nData = getI18nDataMap("", "");
        mapI18nData.keySet().forEach( lang ->{
            jsJson.set(lang, i18n2JsJson(mapI18nData.get(lang)));
        });
        return jsJson;
    }

    @Override
    @Async
    public Integer autoTransMenu(Collection<MenuDO> menuList) {
        int count = 0;
        if(CollectionUtil.isEmpty(menuList)){
            menuList = menuService.getMenuList();
        }
        for(MenuDO menu: menuList){
            String i18nKey = ((MenuServiceImpl)menuService).getI18nKey(menu);
            I18nJsonDataDO menuJsonData = null;
            List<I18nJsonDataDO> jsonDataList = i18nJsonDataMapper.selectList(I18nJsonDataDO::getAlias, i18nKey);
            if(CollectionUtil.isEmpty(jsonDataList)){
                // 添加菜单翻译记录
                menuJsonData = new I18nJsonDataDO();
                menuJsonData.setModule("menu");
                menuJsonData.setAlias(i18nKey);
                // todo 翻译菜单名称
                String name = menu.getName();
            }else{
                menuJsonData = jsonDataList.get(0);
                // todo 检查翻译是否空缺
            }
        }
        return count;
    }

    private JSONObject i18n2JsJson(Map<String, String> mI18nData){
        JSONObject jsJson = new JSONObject();
        JSONObject pageJson = new JSONObject();
        mI18nData.keySet().forEach(key->{
            // menu/button
            if(key.startsWith("menu.") || key.startsWith("button.")){
                return;
            }
            // page
            if(key.startsWith("page.")){
                JSONUtil.putByPath(pageJson, key.substring(5, key.length()), mI18nData.get(key));
                return;
            }
            // other
            JSONUtil.putByPath(jsJson, key, mI18nData.get(key));
        });
        jsJson.putAll(pageJson);
        return jsJson;
    }

}
