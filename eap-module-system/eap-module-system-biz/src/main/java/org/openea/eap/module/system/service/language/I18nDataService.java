package org.openea.eap.module.system.service.language;

import cn.hutool.json.JSONObject;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.springframework.scheduling.annotation.Async;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * i18n 国际化数据服务
 */
public interface I18nDataService {
    /**
     * 获取支持语言列表
     * @return
     */
    List<String> getI18nSupportLangs();

    /**
     * 根据语言和模块获取国际化数据
     * @param langs
     * @param modules
     * @return map<language, map<key, label>>
     */
    Map<String, Map<String, String>> getI18nDataMap(String langs, String modules);


    /**
     * 获取指定key的国际化json
     * @param key
     * @return
     */
    JSONObject getI18nJsonByKey(String key);

    /**
     * 获取前端国际化js
     * @return
     */
    JSONObject getJsJson();

    /**
     * 增加菜单翻译
     * @param menuList
     * @return
     */
    Integer translateMenu(Collection<MenuDO> menuList);

    /**
     * 获取英文key
     * @param name
     * @param type menu,button,...
     * @return
     */
    String convertEnKey(String name, String type);

    /**
     * 翻译菜单
     * @param type menu,button,dir
     * @param key
     * @param name
     * @param len 期望结果字符长度，中文/日文字符按2个算
     * @return  json
     * menu: {"en-US":"LangMgt","zh-CN":"语言管理","ja-JP":"言語管理"}
     * button: {"en-US":"Reset","zh-CN":"重置","ja-JP":"リセットする"}
     */
    JSONObject autoTransMenu(String type, String key, String name, int len);

}
