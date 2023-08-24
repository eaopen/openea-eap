package org.openea.eap.module.visualdev.base.util;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.ModuleEntity;
import org.openea.eap.extj.base.service.ModuleService;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.module.visualdev.base.model.online.PerColModels;
import org.openea.eap.module.visualdev.base.model.online.VisualMenuModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class PubulishUtil {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ModuleService moduleService;


 /**
 * 功能类型
 */
private final static Integer Type = 3;

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

        List<ModuleEntity> moduleList = moduleService.getModuleList(visualMenuModel.getId());

        ModuleEntity moduleEntity = new ModuleEntity();
        String uuid = RandomUtil.uuId();
        String appUuid = RandomUtil.uuId();

        PerColModels pcPerCols = visualMenuModel.getPcPerCols();
        PerColModels appPerCols = visualMenuModel.getAppPerCols();

        moduleEntity.setCategory(pcCategory);

//        moduleEntity.setFullName(visualMenuModel.getFullName());
//        moduleEntity.setEnCode(visualMenuModel.getEncode());
//        moduleEntity.setIcon(icon);
//        moduleEntity.setType(Type);
//        moduleEntity.setModuleId(visualMenuModel.getId());
//        PropertyJsonModel jsonModel = new PropertyJsonModel();
//        jsonModel.setModuleId(visualMenuModel.getId());
//        jsonModel.setIconBackgroundColor("" );
//        jsonModel.setIsTree(0);
//        moduleEntity.setPropertyJson(JsonUtil.getObjectToString(jsonModel));
//        moduleEntity.setSortCode((999L));
//        moduleEntity.setEnabledMark(1);
//        moduleEntity.setIsButtonAuthorize(1);
//        moduleEntity.setIsColumnAuthorize(1);
//        moduleEntity.setIsDataAuthorize(1);
//        moduleEntity.setIsFormAuthorize(1);
//        moduleEntity.setCreatorTime(DateUtil.getNowDate());
//        moduleEntity.setCreatorUserId(userInfo.getUserId());
//        moduleEntity.setId(uuid);
//        moduleEntity.setUrlAddress("model/" + moduleEntity.getEnCode());

        boolean menu = false;

//        if (1 == visualMenuModel.getPc()) {
//            List<ModuleEntity> pcModuleList = moduleList.stream().filter(module -> pcCategory.equals(module.getCategory())).collect(Collectors.toList());
//            //是否生成过菜单
//            if (pcModuleList.size() > 0) {
//                for (ModuleEntity entity : pcModuleList) {
//                    String menuId = entity.getId();
//                    //变更权限
//                    alterPer(entity, pcPerCols);
//                    moduleEntity.setParentId(entity.getParentId());
//                    moduleEntity.setSystemId(entity.getSystemId());
//                    moduleEntity.setId(menuId);
//                    moduleEntity.setEnCode(entity.getEnCode());
//                    moduleEntity.setUrlAddress("model/" + moduleEntity.getEnCode());
//                    //更新菜单
//                    menu = moduleService.update(entity.getId(), moduleEntity);
//                }
//            } else {
//                //创建菜单
//                moduleEntity.setParentId(visualMenuModel.getPcModuleParentId());
//                moduleEntity.setSystemId(visualMenuModel.getPcSystemId());
//                if (StringUtil.isEmpty(moduleEntity.getParentId())) {
//                    return 3;
//                }
//                menu = this.createMenu(moduleEntity);
//
//                batchCreatePermissions(pcPerCols, uuid);
//            }
//            if (!menu) {
//                return 2;
//            }
//        }
        moduleEntity.setCategory(appCategory);
        moduleEntity.setId(appUuid);
        moduleEntity.setUrlAddress("/pages/apply/dynamicModel/index?id=" + visualMenuModel.getId());
        moduleEntity.setEnCode(visualMenuModel.getEncode());
//        if (1 == visualMenuModel.getApp()) {
//            List<ModuleEntity> appModuleList = moduleList.stream().filter(module -> appCategory.equals(module.getCategory())).collect(Collectors.toList());
//            if (appModuleList.size() > 0) {
//                for (ModuleEntity entity : appModuleList) {
//                    String menuId = entity.getId();
//                    //变更权限
//                    alterPer(entity, appPerCols);
//                    moduleEntity.setParentId(entity.getParentId());
//                    moduleEntity.setSystemId(entity.getSystemId());
//                    moduleEntity.setId(menuId);
//                    moduleEntity.setEnCode(entity.getEnCode());
//                    //更新菜单
//                    menu = moduleService.update(entity.getId(), moduleEntity);
//                }
//            } else {
//                moduleEntity.setParentId(visualMenuModel.getAppModuleParentId());
//                moduleEntity.setSystemId(visualMenuModel.getAppSystemId());
//                if (StringUtil.isEmpty(moduleEntity.getParentId())) {
//                    return 3;
//                }
//                menu = this.createMenu(moduleEntity);
//                batchCreatePermissions(appPerCols, appUuid);
//            }
//        }

//        if (!menu) {
//            //创建失败，编码或名称是否重复
//            return 2;
//        }
        return 1;//同步成功
    }
}
