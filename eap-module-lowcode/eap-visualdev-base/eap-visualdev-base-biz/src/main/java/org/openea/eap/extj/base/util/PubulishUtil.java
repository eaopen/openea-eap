package org.openea.eap.extj.base.util;

import com.xingyuv.http.util.StringUtil;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.base.entity.ModuleEntity;
import org.openea.eap.extj.base.service.ModuleService;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.base.model.online.PerColModels;
import org.openea.eap.extj.base.model.online.VisualMenuModel;
import org.openea.eap.module.system.controller.admin.permission.vo.menu.MenuBaseVO;
import org.openea.eap.module.system.convert.permission.MenuConvert;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.mysql.permission.MenuMapper;
import org.openea.eap.module.system.service.permission.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PubulishUtil {

    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private MenuService menuService;

    /**
     * 功能类型
     */
    private final static Integer Type = 2;

    /**
     * pc父级菜单 默认
     */
    private static final String pcCate = "功能示例";


    /**
     * app父级菜单 默认
     */
    private static final String appCate = "移动应用";

    /**
     * pc端分类
     */
    private static final String pcCategory = "Web";

    /**
     * app端分类
     */
    private static final String appCategory = "App";

    /**
     * pc父级菜单id 默认
     */
    private String parentId;

    /**
     * app父级菜单id
     */
    private String appParentId;

    /**
     * 图标
     */
    private final static String icon = "icon-ym icon-ym-webForm";

    public Integer publishMenu(VisualMenuModel visualMenuModel) {
        UserInfo userInfo = userProvider.get();
        MenuDO menuDO1 = menuMapper.selectById(visualMenuModel.getId());
        List<ModuleEntity> moduleList = moduleService.getModuleList(visualMenuModel.getId());

        MenuDO menuDO = new MenuDO();
        menuDO.setId(Long.valueOf(RandomUtil.uuId()));
        menuDO.setName(visualMenuModel.getFullName());
        menuDO.setType(Type);
        menuDO.setSort(999);
        menuDO.setParentId(Long.valueOf(visualMenuModel.getPcModuleParentId()));
        menuDO.setPath("model/" + visualMenuModel.getEncode());
        menuDO.setIcon(icon);
        menuDO.setStatus(0);
        menuDO.setVisible(true);
        menuDO.setKeepAlive(true);
        menuDO.setAlwaysShow(true);
        if (menuDO.getType() == 1) {//功能
            menuDO.setComponent("extn/dataInterface");
        } else if (menuDO.getType() == 2) {//表单
            menuDO.setComponent("extn/dynamicModel");
        } else if (menuDO.getType() == 3) {//外部链接
            menuDO.setComponent("extn/externalLink");
        } else if (menuDO.getType() == 4) {//字典
            menuDO.setComponent("extn/dynamicDictionary");
        } else if (menuDO.getType() == 5) {//报表
            menuDO.setComponent("extn/dynamicDataReport");
        } else {//门户
            menuDO.setComponent("extn/dynamicPortal");
        }

        boolean menu = false;

        if (1 == visualMenuModel.getPc()) {
            //是否生成过菜单
            if (menuDO1 != null) {
                //更新菜单
                return 2;
            } else {
                //创建菜单
                if (StringUtil.isEmpty(String.valueOf(menuDO.getParentId()))) {
                    return 3;
                }
                menuMapper.insert(menuDO);
            }
        }
        return 1;//同步成功
    }
}

