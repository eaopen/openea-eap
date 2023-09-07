package org.openea.eap.extj.permission.model.authorize;

import lombok.Data;
import org.openea.eap.extj.base.model.base.SystemBaeModel;
import org.openea.eap.extj.base.model.button.ButtonModel;
import org.openea.eap.extj.base.model.column.ColumnModel;
import org.openea.eap.extj.base.model.form.ModuleFormModel;
import org.openea.eap.extj.base.model.module.ModuleModel;
import org.openea.eap.extj.base.model.resource.ResourceModel;

import java.util.List;

/**
 *
 *
 */
@Data
public class AuthorizeVO {
    // 菜单
//    private List<MenuModel> menuList;

    /**
     * 功能
     */
    private List<ModuleModel> moduleList;

    /**
     * 按钮
     */
    private List<ButtonModel> buttonList;

    /**
     * 视图
     */
    private List<ColumnModel> columnList;

    /**
     * 资源
     */
    private List<ResourceModel> resourceList;

    /**
     * 表单
     */
    private List<ModuleFormModel> formsList;

    /**
     * 系统
     */
    private List<SystemBaeModel> systemList;

    public AuthorizeVO(List<ModuleModel> moduleList, List<ButtonModel> buttonList, List<ColumnModel> columnList, List<ResourceModel> resourceList, List<ModuleFormModel> formsList, List<SystemBaeModel> systemList) {
//        this.menuList = menuList;
        this.moduleList = moduleList;
        this.buttonList = buttonList;
        this.columnList = columnList;
        this.resourceList = resourceList;
        this.formsList = formsList;
        this.systemList = systemList;
    }
}
