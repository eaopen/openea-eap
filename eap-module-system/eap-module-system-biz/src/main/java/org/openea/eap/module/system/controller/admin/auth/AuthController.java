package org.openea.eap.module.system.controller.admin.auth;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.framework.common.util.collection.SetUtils;
import org.openea.eap.framework.operatelog.core.annotations.OperateLog;
import org.openea.eap.framework.security.config.SecurityProperties;
import org.openea.eap.framework.security.core.LoginUser;
import org.openea.eap.module.system.controller.admin.auth.vo.*;
import org.openea.eap.module.system.convert.auth.AuthConvert;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.dataobject.permission.RoleDO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.logger.LoginLogTypeEnum;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.auth.AdminAuthService;
import org.openea.eap.module.system.service.permission.MenuService;
import org.openea.eap.module.system.service.permission.PermissionService;
import org.openea.eap.module.system.service.permission.RoleService;
import org.openea.eap.module.system.service.social.SocialUserService;
import org.openea.eap.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openea.eap.framework.common.pojo.CommonResult.success;
import static org.openea.eap.framework.common.util.collection.CollectionUtils.convertSet;
import static org.openea.eap.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static org.openea.eap.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static org.openea.eap.framework.security.core.util.SecurityFrameworkUtils.obtainAuthorization;
import static java.util.Collections.singleton;

@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SocialUserService socialUserService;
    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO, HttpServletRequest request) {
        return success(authService.login(reqVO, request));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = obtainAuthorization(request, securityProperties.getTokenHeader());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        if (user == null) {
            return null;
        }
        // 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserId());
        List<RoleDO> roleList = roleService.getRoleListFromCache(roleIds);
        roleList.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 获得菜单列表
//        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roleList, RoleDO::getId));
//        List<MenuDO> menuList = menuService.getMenuList(menuIds);
//        menuList.removeIf(menu -> !CommonStatusEnum.ENABLE.getStatus().equals(menu.getStatus()));
        List<MenuDO> menuList = permissionService.getUserMenuListByUser(user.getId(), user.getUsername());

        // i18n
        menuList = menuService.toI18n(menuList);
        // 拼接结果返回
        return success(AuthConvert.INSTANCE.convert(user, roleList, menuList));
    }

    /**
     * 获得登录用户的菜单列表
     * @return list(menu)
     * @deprecated  将合并到权限资源数据中get-permission-info，不再单独调用
     */
    @GetMapping("/list-menus")
    @Operation(summary = "获得登录用户的菜单列表")
    public CommonResult<List<AuthMenuRespVO>> getMenuList() {
        // 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserId());
        List<RoleDO> roleList = roleService.getRoleListFromCache(roleIds);
        roleList.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色
        // 获得用户拥有的菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roleList, RoleDO::getId));
        List<MenuDO> menuList = menuService.getMenuList(menuIds);
        menuList.removeIf(menu -> !CommonStatusEnum.ENABLE.getStatus().equals(menu.getStatus()));
        // i18n
        menuList = menuService.toI18n(menuList);
        // 转换成 Tree 结构返回
        List<AuthPermissionInfoRespVO.MenuVO> listMenuTree = AuthConvert.INSTANCE.buildMenuTree(menuList);
        // 数据格式转换
        List<AuthMenuRespVO> listMenuTree2 = new ArrayList<>();
        for(AuthPermissionInfoRespVO.MenuVO menuVO: listMenuTree){
            AuthMenuRespVO menuRespVO = new AuthMenuRespVO();
            BeanUtil.copyProperties(menuVO, menuRespVO);
        }
        return success(listMenuTree2);
    }

    // ========== 短信登录相关 ==========

    @PostMapping("/sms-login")
    @PermitAll
    @Operation(summary = "使用短信验证码登录")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> smsLogin(@RequestBody @Valid AuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    @PostMapping("/send-sms-code")
    @PermitAll
    @Operation(summary = "发送手机验证码")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return success(true);
    }

    // ========== 社交登录相关 ==========

    @GetMapping("/social-auth-redirect")
    @PermitAll
    @Operation(summary = "社交授权的跳转")
    @Parameters({
            @Parameter(name = "type", description = "社交类型", required = true),
            @Parameter(name = "redirectUri", description = "回调路径")
    })
    public CommonResult<String> socialLogin(@RequestParam("type") Integer type,
                                                    @RequestParam("redirectUri") String redirectUri) {
        return CommonResult.success(socialUserService.getAuthorizeUrl(type, redirectUri));
    }

    @PostMapping("/social-login")
    @PermitAll
    @Operation(summary = "社交快捷登录，使用 code 授权码", description = "适合未登录的用户，但是社交账号已绑定用户")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> socialQuickLogin(@RequestBody @Valid AuthSocialLoginReqVO reqVO) {
        return success(authService.socialLogin(reqVO));
    }

}
