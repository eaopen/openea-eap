package org.openea.eap.module.obpm.service.obpm;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.permission.PermissionService;
import org.openea.eap.module.system.service.permission.PermissionServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.Collections.singleton;

@Service
@ConditionalOnProperty(prefix = "eap", name = "enableOpenBpm", havingValue = "true")
@Slf4j
public class ObpmPermissionServiceImpl extends PermissionServiceImpl implements PermissionService {

    @Resource
    private ObmpClientService obmpClientService;
    @Override
    public List<MenuDO> getUserMenuListFromCache(Long userId, String userKey, Collection<Integer> menuTypes){
        // 1. eap 菜单
        // 获得角色列表
        Set<Long> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 获得菜单列表
        List<MenuDO> menuList = getRoleMenuListFromCache(roleIds,
                menuTypes,
                singleton(CommonStatusEnum.ENABLE.getStatus())); // 只要开启的

        // 2. obpm菜单
        boolean withButton = false;
        if(menuTypes.contains(MenuTypeEnum.BUTTON.getType())){
            withButton = true;
        }
        if(ObjectUtils.isEmpty(userKey)){
            AdminUserDO adminUserDO = userService.getUser(userId);
            if(adminUserDO!=null){
                userKey = adminUserDO.getUsername();
            }
        }
        List<MenuDO> menuList2 = getObpmUserMenuList(userKey, withButton);
        if(!menuList2.isEmpty()){
            // obpm 菜单所有id/parentId + 200000（避免同eap菜单冲突）
            for(MenuDO menu: menuList2){
                menu.setId(menu.getId()+20000);
                if(menu.getParentId()!=0L){
                    menu.setParentId(menu.getParentId()+20000);
                }
            }
            // eap 顶层菜单排序sort + 1000
            for(MenuDO menu: menuList){
                if(menu.getParentId()==0L){
                    menu.setSort(1000+menu.getSort());
                }
                menuList2.add(menu);
            }
            menuList = menuList2;
        }

        return menuList;
    }

    private List<MenuDO> getObpmUserMenuList(String userKey, boolean withButton) {
        List<MenuDO> listMenu = new ArrayList<>();
        List<JSONObject> listResource = obmpClientService.queryUserMenu(userKey, "", withButton);
        if(listResource!=null){
            for(JSONObject sysRes: listResource){
                // ignore button
                if(!withButton){
                    String type = sysRes.getStr("type");
                    if("button".equals(type)) {continue;}
                }
                listMenu.add(convertMenu(sysRes));
            }
        }
        return listMenu;
    }

    private MenuDO convertMenu(JSONObject sysRes){
        MenuDO menu = new MenuDO();

        // 1. copy
        // 1.1 same name
        // id, parentId, name, alias, icon, type
        //BeanUtils.copyProperties(sysRes, menu);
        menu.setId(sysRes.getLong("id"));
        menu.setParentId(sysRes.getLong("parentId"));
        if(menu.getParentId()==null){
            menu.setParentId(0L);
        }
        menu.setName(sysRes.getStr("name"));
        menu.setAlias(sysRes.getStr("alias"));

        //menuDO.setPermission( bean.getPermission() );
        // status/visible/keepAlive/alwaysShow
        menu.setStatus(CommonStatusEnum.ENABLE.getStatus());
        menu.setVisible(true);
        menu.setKeepAlive(true);
        menu.setAlwaysShow(true);

        // check type
        String type = sysRes.getStr("type");
        if("menu".equals(type)){
            menu.setType(2);
        }else if("button".equals(type)){
            menu.setType(3);
            menu.setVisible(false);
        }else{
            menu.setType(0);
        }
        // check sort (sn -> sort)
        int sort = 0;
        if(sysRes.containsKey("sn")){
            sort = sysRes.getInt("sn");
        }
        menu.setSort(sort);
        // icon
        String icon = sysRes.getStr("icon");
        if(ObjectUtils.isEmpty(icon)){
            if(menu.getParentId()==0L){
                // top default icon
                icon = "system";
            }
        }else{
            icon = convertIcon(icon);
        }
        if(ObjectUtils.isNotEmpty(icon)){
            menu.setIcon(icon);
        }


        // 1.3 default prop


        // 2. 菜单转换 path
        // path (url -> path)
        String resUrl = sysRes.getStr("url");

        // 2.1 obpm web, default setting
        //String componentName = null;
        String component = null;
        String path = resUrl;
        if(ObjectUtils.isEmpty(path)){
            path = menu.getAlias();
            // 子元素只有1个时可省略中间层级
            menu.setAlwaysShow(false);
        }else{
            component = "obpm/web";
//            if(!path.startsWith("http") && !path.startsWith("/")){
//                path = "/" + path;
//            }
        }

        if(ObjectUtils.isNotEmpty(resUrl)){
            // 2.2 grid
            // /form/formCustSql/view/formCustSqlView.html?code=xxx
            if(resUrl.indexOf("form/formCustSql/view/formCustSqlView.html")>=0){
                component = "obpm/agGrid";
                String code = getParamValueFromPath(path, "code");
                path = "obpm/listGrid/"+code
                        +path.substring(path.indexOf("formCustSqlView.html?")+20);
            }
            // 2.3 form
            // /form/formDef/vueFormDefPreview.html?key=xxx
            if(resUrl.indexOf("form/formDef/vueFormDefPreview.html")>=0){
                component = "obpm/easyForm";
                String key = getParamValueFromPath(path, "key");
                path = "obpm/easyForm/"+key
                        +path.substring(path.indexOf("vueFormDefPreview.html?")+22);
            }
            // 2.4 dialog

            // 2.5 task
            // /bpm/vueForm/instanceDetail.html?id=xxx
            if(resUrl.indexOf("bpm/vueForm/instanceDetail.html")>=0){
                component = "obpm/instanceDetail";
                String id = getParamValueFromPath(path, "id");
                path = "obpm/instanceDetail/"+id
                        +path.substring(path.indexOf("instanceDetail.html?")+19);
            }
            // /bpm/vueForm/taskComplete.html?taskId=xxx
            if(resUrl.indexOf("bpm/vueForm/taskComplete.html")>=0){
                component = "obpm/taskDetail";
                String taskId = getParamValueFromPath(path, "taskId");
                path = "obpm/taskComplete/"+taskId
                        + path.substring(path.indexOf("taskComplete.html?")+17);
            }
            // /bpm/vueForm/start.html?defId=xxx
            // /bpm/vueForm/start.html?instanceId=xxx
            if(resUrl.indexOf("bpm/vueForm/start.html")>=0){
                component = "obpm/taskStart";
                Map<String, String> mParam = getPathParams(path);
                String id = mParam.get("defId");
                if(ObjectUtils.isEmpty(id)){
                    id = mParam.get("instanceId");
                }
                path = "obpm/taskStart/"+id
                        +path.substring(path.indexOf("start.html?")+10);
            }


            //2.6
            // admin(todo)
        }

        //2.9
        if(menu.getParentId()==0L){
            if(!path.startsWith("http") && !path.startsWith("/")){
                path = "/" + path;
            }
        }
        menu.setPath(path);
        if(ObjectUtils.isNotEmpty(component)){
            menu.setComponent(component);
        }

        return menu;
    }

    private String convertIcon(String origin){
        // todo
        return "row";
    }

    private String[] getQueryParam4route(String path, String key){
        String keyValue = "nul";
        String queryPath = "";
        List<NameValuePair> listParam = URLEncodedUtils.parse(path.substring(path.indexOf("?")+1), Charset.forName("UTF-8"));
        for(NameValuePair pair: listParam){
            if(pair.getName().equals(key)){
                keyValue = pair.getValue();
            }else{
                queryPath += "/"+pair.getName()+"/"+pair.getValue();
            }
        }
        return new String[]{keyValue, queryPath};
    }

    private String getParamValueFromPath(String path, String key){
        Map<String, String> mParam = getPathParams(path);
        return  mParam.get(key);
    }
    private Map<String, String> getPathParams(String path) {
        Map<String, String> mParam = new HashMap<>();
        List<NameValuePair> listParam = URLEncodedUtils.parse(path.substring(path.indexOf("?")+1), Charset.forName("UTF-8"));
        for(NameValuePair pair: listParam){
            mParam.put(pair.getName(), pair.getValue());
        }
        return mParam;
    }
}
