package org.openea.eap.module.visualdev.portal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.module.visualdev.base.ActionResult;
import org.openea.eap.module.visualdev.base.UserInfo;
import org.openea.eap.module.visualdev.base.controller.SuperController;
import org.openea.eap.module.visualdev.base.service.PortalManageService;
import org.openea.eap.module.visualdev.base.service.SystemService;
import org.openea.eap.module.visualdev.constant.MsgCode;
import org.openea.eap.module.visualdev.permission.service.AuthorizeService;
import org.openea.eap.module.visualdev.permission.service.UserService;
import org.openea.eap.module.visualdev.portal.constant.PortalConst;
import org.openea.eap.module.visualdev.portal.entity.PortalEntity;
import org.openea.eap.module.visualdev.portal.model.*;
import org.openea.eap.module.visualdev.portal.service.PortalDataService;
import org.openea.eap.module.visualdev.portal.service.PortalService;
import org.openea.eap.module.visualdev.util.CacheKeyUtil;
import org.openea.eap.module.visualdev.util.RedisUtil;
import org.openea.eap.module.visualdev.util.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

/**
 * 可视化门户
 *

 */
@Slf4j
@RestController
@Tag(name = "门户展示界面" , description = "Portal" )
@RequestMapping("/api/visualdev/Portal" )
public class PortalDataController extends SuperController<PortalService, PortalEntity> {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private PortalService portalService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PortalManageService portalManageService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private PortalDataService portalDataService;

    @Operation(summary = "设置默认门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/{id}/Actions/SetDefault" )
    @SaCheckPermission("onlineDev.visualPortal" )
    @Transactional
    public ActionResult<String> SetDefault(@PathVariable("id") String id, String platform) {
        portalDataService.setCurrentDefault(platform, id);
        UserInfo userInfo = userProvider.get();
        if(PortalConst.WEB.equals(platform)){
            userInfo.setSystemId(id);
        }else {
            userInfo.setAppSystemId(id);
        }
        UserProvider.setLocalLoginUser(userInfo);
        UserProvider.setLoginUser(userInfo);
        return ActionResult.success("设置成功" );
    }

    @Operation(summary = "门户自定义保存" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/Custom/Save/{id}")
    public ActionResult<String> customSave(@PathVariable("id" ) String id, @RequestBody PortalDataForm portalDataForm) throws Exception {
        StpUtil.checkPermissionOr("onlineDev.visualPortal" , id);
        portalDataForm.setPortalId(id);
        portalDataService.createOrUpdate(
                new PortalCustomPrimary(portalDataForm.getPlatform(), portalDataForm.getPortalId()),
                portalDataForm.getFormData());
        return ActionResult.success(MsgCode.SU002.getMsg());
    }

    @Operation(summary = "门户发布(同步)" )
    @Parameters({
            @Parameter(name = "portalId" , description = "门户主键" ),
    })
    @PutMapping("/Actions/release/{portalId}" )
    public ActionResult<PortalReleaseVO> release(@PathVariable("portalId") String portalId, @RequestBody @Valid PortalReleaseForm form) throws Exception {
        if (form.getPc() == 1) portalDataService.release(PortalConst.WEB, portalId, form.getPcSystemId(),PortalConst.WEB);
        if (form.getApp() == 1) portalDataService.release(PortalConst.APP, portalId, form.getAppSystemId(),PortalConst.APP);
        return ActionResult.success(MsgCode.SU011.get());
    }

    @Operation(summary = "个人门户详情" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/{id}/auth" )
    public ActionResult<PortalInfoAuthVO> infoAuth(@PathVariable("id" ) String id, String platform, String systemId) {
        platform = platform.equalsIgnoreCase("pc") || platform.equalsIgnoreCase(PortalConst.WEB) ? PortalConst.WEB : PortalConst.APP;
        try{
            return ActionResult.success(portalDataService.getDataFormView(id, platform));
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
    }

}
