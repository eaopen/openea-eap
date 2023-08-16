package org.openea.eap.module.system.service.language;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.mysql.language.I18nJsonDataMapper;
import org.openea.eap.module.system.dal.mysql.language.LangTypeMapper;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.language.translate.TranslateUtil;
import org.openea.eap.module.system.service.permission.MenuService;
import org.openea.eap.module.system.service.permission.MenuServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
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
    public JSONObject getI18nJsonByKey(String key){
        I18nJsonDataDO jsonDataDO = i18nJsonDataMapper.queryI18nJsonDataByKey(key);
        if(jsonDataDO!=null){
            String strJson = jsonDataDO.getJson();
            if(ObjectUtil.isNotEmpty(strJson)){
                return JSONUtil.parseObj(strJson);
            }
        }
        return null;
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

    Semaphore semaphore=new Semaphore(1);
    @Override
    @Async
    public Integer asyncTranslateMenu(Collection<MenuDO> menuList) {
        int count = 0;
        // 异步调用增加到菜单翻译资源中
        int availablePermits=semaphore.availablePermits();
        if(availablePermits==0){
            log.debug("无资源，取消translateMenu");
            return count;
        }
        try{
            semaphore.acquire(1);
            // TODO 添加到JOB中
            //count =  translateMenu(menuList);
            log.info("translateMenu, count="+count);
        }catch (Throwable t){
            log.warn("asyncTranslateMenu "+t.getMessage());
        }finally {
            semaphore.release(1);
        }
        return count;
    }
    @Override
    public Integer translateMenu(Collection<MenuDO> menuList) {
        int count = 0;
        if(CollectionUtil.isEmpty(menuList)){
            menuList = menuService.getMenuList();
        }
        List<I18nJsonDataDO> listInsertJsonData = new ArrayList<>();
        for(MenuDO menu: menuList){
            String i18nKey = ((MenuServiceImpl)menuService).getI18nKey(menu);
            I18nJsonDataDO menuJsonData = null;
            List<I18nJsonDataDO> jsonDataList = i18nJsonDataMapper.selectList(I18nJsonDataDO::getAlias, i18nKey);
            if(CollectionUtil.isEmpty(jsonDataList)){
                // 添加菜单翻译记录
                menuJsonData = new I18nJsonDataDO();
                // id , auto
                String type = "menu";
                if(i18nKey.startsWith("button")){
                    type = "button";
                }
                menuJsonData.setModule("menu");
                menuJsonData.setAlias(i18nKey);
                menuJsonData.setName(type+"-"+menu.getName());
                JSONObject i18nJson = autoTransMenu(type,menu.getAlias(), menu.getName(), StrUtil.length(menu.getName()));
                if(ObjectUtil.isNotEmpty(i18nJson)){
                    menuJsonData.setJson(i18nJson.toString());
                    menuJsonData.setRemark("auto");
                }else {
                    // default
                    menuJsonData.setRemark("default");
                    i18nJson = new JSONObject();
                    i18nJson.set("zh-CN",  menu.getName());
                    menuJsonData.setJson(i18nJson.toString());
                }
                listInsertJsonData.add(menuJsonData);
                count++;
            }else{
                menuJsonData = jsonDataList.get(0);
                // todo 检查翻译是否空缺
            }
        }
        if(CollectionUtil.isNotEmpty(listInsertJsonData)){
            i18nJsonDataMapper.insertBatch(listInsertJsonData);
        }
        return count;
    }

    @Override
    public String convertEnKey(String name, String type) {
        String enKey = null;
        // 特别处理
        if("menu".equalsIgnoreCase(type)){

        }else if("button".equalsIgnoreCase(type)){

        }
        // AI翻译
        if(ObjectUtil.isEmpty(enKey)){
            // TODO AI翻译
        }
        // 词典翻译
        if(ObjectUtil.isEmpty(enKey)){
            // TODO 英文翻译
//            try{
//                enKey = TranslateUtil.translateText(name, "auto", "en-US");
//            }catch (Exception e){
//                log.debug(e.getMessage(), e);
//            }
            // 转拼音
            if(ObjectUtil.isEmpty(enKey) || enKey.equalsIgnoreCase(name)){
                enKey = PinyinUtil.getPinyin(name);
            }
        }
        // 默认处理
        if(ObjectUtil.isEmpty(enKey)){
            enKey = name;
        }
        return enKey;
    }

    @Override
    public JSONObject autoTransMenu(String type, String key, String name, int len) {
        JSONObject json = new JSONObject();
        // type: menu偏向名词 button偏向动词
        json.set("zh-CN", name);
        // TODO 根据语言类型进行翻译，调用LLM翻译
        return json;
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
