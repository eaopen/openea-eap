package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class PcUserVO {
    @ApiModelProperty("菜单集合")
    private List<MenuTreeVO> menuList;
    @ApiModelProperty("权限集合")
    private List<PermissionModel> permissionList;
    @ApiModelProperty("用户信息")
    private UserCommonInfoVO userInfo;

    /**
     * 系统菜单树
     */
    @ApiModelProperty("系统菜单树")
    private List<AllMenuSelectVO> routerList;

    /**
     * 系统配置
     */
    @ApiModelProperty("系统配置")
    private SystemInfo sysConfigInfo;

    public PcUserVO() {
    }

    public PcUserVO(List<MenuTreeVO> menuList, List<PermissionModel> permissionList, UserCommonInfoVO userInfo, SystemInfo sysConfigInfo, List<AllMenuSelectVO> routerList) {
        this.menuList = menuList;
        this.permissionList = permissionList;
        this.userInfo = userInfo;
        this.sysConfigInfo = sysConfigInfo;
        this.routerList = routerList;
    }
}
