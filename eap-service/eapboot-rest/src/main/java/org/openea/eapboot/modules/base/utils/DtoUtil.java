package org.openea.eapboot.modules.base.utils;

import org.openea.eapboot.modules.base.entity.Permission;
import org.openea.eapboot.modules.base.vo.MenuVo;

/**
 */
public class DtoUtil {

    public static MenuVo permissionToMenuVo(Permission p){

        MenuVo menuVo = new MenuVo();

        menuVo.setId(p.getId());
        menuVo.setParentId(p.getParentId());
        menuVo.setName(p.getName());
        menuVo.setType(p.getType());
        menuVo.setTitle(p.getTitle());
        menuVo.setComponent(p.getComponent());
        menuVo.setPath(p.getPath());
        menuVo.setIcon(p.getIcon());
        menuVo.setUrl(p.getUrl());
        menuVo.setButtonType(p.getButtonType());

        return menuVo;
    }
}
