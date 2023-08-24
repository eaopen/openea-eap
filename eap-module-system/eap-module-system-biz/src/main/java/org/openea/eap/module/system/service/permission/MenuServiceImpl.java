package org.openea.eap.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import org.openea.eap.framework.i18n.core.I18nUtil;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuCreateReqVO;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import org.openea.eap.module.system.convert.permission.MenuConvert;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.mysql.permission.MenuMapper;
import org.openea.eap.module.system.dal.redis.RedisKeyConstants;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.language.I18nDataService;
import org.openea.eap.module.system.service.language.I18nJsonDataService;
import org.openea.eap.module.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Semaphore;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.framework.common.util.collection.CollectionUtils.convertList;
import static org.openea.eap.module.system.dal.dataobject.permission.MenuDO.ID_ROOT;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.*;

/**
 * 菜单 Service 实现
 *
 */
@Service
@Slf4j
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private PermissionService permissionService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService tenantService;

    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private I18nDataService i18nDataService;

//    @Resource
//    @Lazy // 延迟，避免循环依赖报错
//    private I18nJsonDataService i18nJsonDataService;

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#reqVO.permission",
            condition = "#reqVO.permission != null")
    public Long createMenu(MenuCreateReqVO reqVO) {
        // 校验父菜单存在
        validateParentMenu(reqVO.getParentId(), null);
        // 校验菜单（自己）
        validateMenu(reqVO.getParentId(), reqVO.getName(), null);

        // 插入数据库
        MenuDO menu = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(menu);
        menuMapper.insert(menu);
        // 返回
        return menu.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为 permission 如果变更，涉及到新老两个 permission。直接清理，简单有效
    public void updateMenu(MenuUpdateReqVO reqVO) {
        // 校验更新的菜单是否存在
        if (menuMapper.selectById(reqVO.getId()) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // 校验父菜单存在
        validateParentMenu(reqVO.getParentId(), reqVO.getId());
        // 校验菜单（自己）
        validateMenu(reqVO.getParentId(), reqVO.getName(), reqVO.getId());

        // 更新到数据库
        MenuDO updateObject = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(updateObject);
        menuMapper.updateById(updateObject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为此时不知道 id 对应的 permission 是多少。直接清理，简单有效
    public void deleteMenu(Long id) {
        // 校验是否还有子菜单
        if (menuMapper.selectCountByParentId(id) > 0) {
            throw exception(MENU_EXISTS_CHILDREN);
        }
        // 校验删除的菜单是否存在
        if (menuMapper.selectById(id) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // 标记删除
        menuMapper.deleteById(id);
        // 删除授予给角色的权限
        permissionService.processMenuDeleted(id);
    }

    @Override
    public List<MenuDO> getMenuList() {
        return menuMapper.selectList();
    }

    @Override
    public List<MenuDO> getMenuListByTenant(MenuListReqVO reqVO) {
        List<MenuDO> menus = getMenuList(reqVO);
        // 开启多租户的情况下，需要过滤掉未开通的菜单
        tenantService.handleTenantMenu(menuIds -> menus.removeIf(menu -> !CollUtil.contains(menuIds, menu.getId())));
        return menus;
    }

    @Override
    @Cacheable(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#permission")
    public List<Long> getMenuIdListByPermissionFromCache(String permission) {
        List<MenuDO> menus = menuMapper.selectListByPermission(permission);
        return convertList(menus, MenuDO::getId);
    }

    @Override
    public List<MenuDO> getMenuList(MenuListReqVO reqVO) {
        List<MenuDO> menuList =  menuMapper.selectList(reqVO);
        return menuList;
    }

    @Override
    public MenuDO getMenu(Long id) {
        MenuDO menu = menuMapper.selectById(id);
        // query i18n
        String i18nKey = getI18nKey(menu);
        if(ObjectUtil.isNotEmpty(i18nKey)){
            JSONObject json = i18nDataService.getI18nJsonByKey(i18nKey);
            if(json!=null){
                menu.setI18nJson(json.toString());
            }
        }
        return menu;
    }

    @Override
    public List<MenuDO> getMenuList(Collection<Long> ids) {
        return menuMapper.selectBatchIds(ids);
    }

    @Override
    public List<MenuDO> toI18n(List<MenuDO> menus) {
        if(!I18nUtil.enableI18n() || menus==null){
            return menus;
        }
        for(MenuDO menu: menus){
            String i18nKey = getI18nKey(menu);
            String i18nLabel = I18nUtil.t(i18nKey, menu.getName());
            checkMissI18nKey(menu, i18nLabel);
            if(StrUtil.isNotEmpty(i18nLabel) && !i18nLabel.equals(menu.getName())){
                menu.setName(i18nLabel);
            }
        }
        return menus;
    }

    @Override
    @Async
    public Integer updateMenuI18n() {
        int count = 0;
        // 1. prepare will i18n menus
        // 范围包含db中菜单以及缺少翻译的菜单mapMissI18nMenu，两者有重复
        List<MenuDO> menuList =  this.getMenuList();
        Map<String, MenuDO> mapCheckI18n = new HashMap<>();
        for(MenuDO menu: menuList){
            String alias = menu.getAlias();
            if(ObjectUtil.isEmpty(alias)){
                alias = checkAlias(menu);
                if(ObjectUtil.isEmpty(alias)) {
                    alias = i18nDataService.convertEnKey(menu.getName(), "menu");
                }
                if(ObjectUtil.isNotEmpty(alias)) {
                    menu.setAlias(alias);
                    menuMapper.updateById(menu);
                }
            }
            if(ObjectUtil.isNotEmpty(alias)) {
                alias = menu.getType() + "__" + alias;
                if(!mapCheckI18n.containsKey(alias)){
                    mapCheckI18n.put(alias, menu);
                }
            }
        }
        for(String key: mapMissI18nMenu.keySet()){
            if(!mapCheckI18n.containsKey(key)){
                mapCheckI18n.put(key, mapMissI18nMenu.get(key));
            }
        }
        // 2. check i18n
        i18nDataService.translateMenu(mapCheckI18n.values());
        return count;
    }


    // 本地存储，如集群部署可存储到redis中
    private Map<String, MenuDO> mapMissI18nMenu = new Hashtable<>();

    /**
     * 国际化翻译补漏机制 - 检查是否需要补翻译
     *
     * 保持未翻译菜单由定时任务或其他操作执行翻译
     *
     * @param menu
     * @param i18nLabel
     */
    private void checkMissI18nKey(MenuDO menu, String i18nLabel){
        // 排除默认语言，默认为中文
        if("zh".equalsIgnoreCase(LocaleContextHolder.getLocale().getLanguage())){
            return;
        }
        // 已有不同的翻译，无需处理
        if(i18nLabel !=null && !i18nLabel.equals(menu.getName())){
            return;
        }
        String alias =  menu.getAlias();
        if(ObjectUtil.isNotEmpty(alias)) {
            alias = menu.getType() + "__" + alias;
            if(!mapMissI18nMenu.containsKey(alias)){
                mapMissI18nMenu.put(alias, menu);
            }
        }
    }

    public static String getI18nKey(MenuDO menu){
        String i18nKey = null;
        if(MenuTypeEnum.BUTTON.getType().equals(menu.getType())){
            i18nKey = "button.";
        }else{
            i18nKey = "menu.";
        }
        String alias =  checkAlias(menu);
        if(ObjectUtil.isEmpty(alias) || "null".equalsIgnoreCase(alias)){
            alias = null;
        }
        if(ObjectUtil.isNotEmpty(alias)){
            i18nKey += alias;
        }else{
            i18nKey = null;
        }
        return i18nKey;
    }

    /**
     * 校验父菜单是否合法
     *
     * 1. 不能设置自己为父菜单
     * 2. 父菜单不存在
     * 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 菜单类型
     *
     * @param parentId 父菜单编号
     * @param childId 当前菜单编号
     */
    @VisibleForTesting
    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || ID_ROOT.equals(parentId)) {
            return;
        }
        // 不能设置自己为父菜单
        if (parentId.equals(childId)) {
            throw exception(MENU_PARENT_ERROR);
        }
        MenuDO menu = menuMapper.selectById(parentId);
        // 父菜单不存在
        if (menu == null) {
            throw exception(MENU_PARENT_NOT_EXISTS);
        }
        // 父菜单必须是目录或者菜单类型
        if (!MenuTypeEnum.DIR.getType().equals(menu.getType())
            && !MenuTypeEnum.MENU.getType().equals(menu.getType())) {
            throw exception(MENU_PARENT_NOT_DIR_OR_MENU);
        }
    }

    /**
     * 校验菜单是否合法
     *
     * 1. 校验相同父菜单编号下，是否存在相同的菜单名
     *
     * @param name 菜单名字
     * @param parentId 父菜单编号
     * @param id 菜单编号
     */
    @VisibleForTesting
    void validateMenu(Long parentId, String name, Long id) {
        MenuDO menu = menuMapper.selectByParentIdAndName(parentId, name);
        if (menu == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的菜单
        if (id == null) {
            return;
        }
        if (!menu.getId().equals(id)) {
            throw exception(MENU_NAME_DUPLICATE);
        }
    }

    /**
     * 初始化菜单的通用属性。
     *
     * 例如说，只有目录或者菜单类型的菜单，才设置 icon
     *
     * @param menu 菜单
     */
    private void initMenuProperty(MenuDO menu) {
        // 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            menu.setComponent("");
            menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }

        // init alias for i18n
        checkAlias(menu);
    }

    private static String checkAlias(MenuDO menu){
        String alias =  menu.getAlias();
        if("null".equalsIgnoreCase(alias)){
            alias = null;
        }
        if(ObjectUtil.isNotEmpty(alias)){
            return alias;
        }
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            if(ObjectUtil.isNotEmpty(menu.getPermission()) ){
                // 转驼峰格式, 只保留后面2部分
                // system:user:query => userQuery
                // infra:api-access-log:query => apiAccessLogQuery
                alias = menu.getPermission();
                int last = 0;
                last = alias.lastIndexOf(":");
                if(last>0){
                    last = alias.substring(0, last-1).lastIndexOf(":");
                    if(last>0){
                        alias = alias.substring(last+1, alias.length());
                    }
                }
                alias = alias.replaceAll(":","-");
                alias = StrUtil.toCamelCase(alias,'-');
            }
        }
        if(ObjectUtil.isEmpty(alias) && ObjectUtil.isNotEmpty(menu.getPath())){
            if(!menu.getPath().startsWith("http")
                    && !menu.getPath().startsWith("#")){
                // /system 去首/
                alias = menu.getPath();
                if(alias.startsWith("/")){
                    alias = alias.substring(1, alias.length());
                }
                // 去除参数?
                if(alias.indexOf("?")>0){
                    alias = alias.substring(0, alias.indexOf("?"));
                }
                // 转驼峰格式
                alias = alias.replaceAll("/","-");
                alias = StrUtil.toCamelCase(alias,'-');
            }
        }
        if(ObjectUtil.isEmpty(alias)
                && MenuTypeEnum.MENU.getType().equals(menu.getType())){
            // menu
            if(ObjectUtil.isNotEmpty(menu.getComponentName()) ){
                // 首字母改小写
                alias = StrUtil.lowerFirst(menu.getComponentName());
            }else if(ObjectUtil.isNotEmpty(menu.getComponent()) ) {
                // 转驼峰格式 infra/job/index => infraJob
                alias = menu.getComponent();
                if(alias.endsWith("/index")){
                    alias = alias.substring(0, alias.length()-6);
                }
                alias = StrUtil.toCamelCase(alias,'/');
            }
        }
        if(ObjectUtil.isNotEmpty(alias)){
            menu.setAlias(alias);
        }
        return alias;
    }
}
