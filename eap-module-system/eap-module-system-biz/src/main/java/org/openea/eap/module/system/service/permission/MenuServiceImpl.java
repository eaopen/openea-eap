package org.openea.eap.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import org.openea.eap.framework.common.util.collection.CollectionUtils;
import org.openea.eap.framework.i18n.core.I18nUtil;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuCreateReqVO;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import org.openea.eap.module.system.convert.permission.MenuConvert;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.mysql.permission.MenuMapper;
import org.openea.eap.module.system.dal.redis.RedisKeyConstants;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#reqVO.permission")
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
        return menuMapper.selectList(reqVO);
    }

    @Override
    public MenuDO getMenu(Long id) {
        return menuMapper.selectById(id);
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
            if(StrUtil.isNotEmpty(i18nLabel) && !i18nLabel.equals(menu.getName())){
                menu.setName(i18nLabel);
            }
        }
        return menus;
    }

    public String getI18nKey(MenuDO menu){
        String i18nKey = null;
        if(MenuTypeEnum.BUTTON.getType().equals(menu.getType())){
            i18nKey = "button.";
        }else{
            i18nKey = "menu.";
        }
        createAlias(menu);
        String alias =  menu.getAlias();
        if(ObjectUtil.isNotEmpty(alias) && !"null".equalsIgnoreCase(alias)){
            i18nKey += menu.getAlias();
        }else{
            i18nKey += menu.getName();
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
            throw exception(MENU_NAME_DUPLICATE);
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
        createAlias(menu);
    }

    private String createAlias(MenuDO menu){
        String alias =  menu.getAlias();
        if("null".equalsIgnoreCase(alias)){
            alias = null;
        }
        if(ObjectUtil.isEmpty(alias)){
            if (MenuTypeEnum.MENU.getType().equals(menu.getType())) {
                // menu
                if(ObjectUtil.isNotEmpty(menu.getComponentName()) ){
                    alias = menu.getComponentName();
                }else if(ObjectUtil.isNotEmpty(menu.getComponent()) ) {
                    // 转驼峰格式 infra/job/index => infraJob
                    alias = menu.getComponent();
                    if(alias.endsWith("/index")){
                        alias = alias.substring(0, alias.length()-6);
                    }
                    alias = StrUtil.toCamelCase(alias,'/');
                }
            }
            if(ObjectUtil.isEmpty(alias)){
                if(ObjectUtil.isNotEmpty(menu.getPermission()) ){
                    // 转驼峰格式: system:user:query => systemUserQuery
                    alias = menu.getPermission();
                    alias = StrUtil.toCamelCase(alias,':');
                    alias = StrUtil.toCamelCase(alias,'-');
                }else if (ObjectUtil.isNotEmpty(menu.getPath()) ){
                    // /system 去首/
                    alias = menu.getPath();
                    if(alias.startsWith("/")){
                        alias = alias.substring(1, alias.length());
                    }
                    // 转驼峰格式
                    if(!alias.startsWith("http")){
                        alias = StrUtil.toCamelCase(alias,'/');
                        alias = StrUtil.toCamelCase(alias,'-');
                    }
                }
            }
            if(ObjectUtil.isEmpty(alias)){
                // TODO中文转拼音或英文？
                alias = menu.getName();
                //alias = PinyinUtil.getPinyin(alias);
            }
            if(ObjectUtil.isNotEmpty(alias)){
                menu.setAlias(alias);
            }
        }
        return alias;
    }

}
