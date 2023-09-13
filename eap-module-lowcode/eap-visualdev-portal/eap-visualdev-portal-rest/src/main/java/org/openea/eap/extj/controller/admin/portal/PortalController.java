package org.openea.eap.extj.controller.admin.portal;

import cn.hutool.core.map.MapUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.model.VisualFunctionModel;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.portal.constant.PortalConst;
import org.openea.eap.extj.portal.entity.PortalEntity;
import org.openea.eap.extj.portal.entity.PortalManageEntity;
import org.openea.eap.extj.portal.model.*;
import org.openea.eap.extj.portal.service.PortalDataService;
import org.openea.eap.extj.portal.service.PortalManageService;
import org.openea.eap.extj.portal.service.PortalService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ExportModelTypeEnum;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.openea.eap.extj.util.treeutil.newtreeutil.TreeDotUtils;
import org.openea.eap.module.system.dal.dataobject.permission.MenuDO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.enums.permission.MenuTypeEnum;
import org.openea.eap.module.system.service.permission.MenuService;
import org.openea.eap.module.system.service.permission.PermissionServiceImpl;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

import static org.openea.eap.framework.common.util.collection.CollectionUtils.filterList;
import static org.openea.eap.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;


/**
 * 可视化门户
 *
 */
@Slf4j
@RestController
@Tag(name = "可视化门户" , description = "Portal" )
@RequestMapping("/api/visualdev/Portal" )
public class PortalController extends SuperController<PortalService, PortalEntity> {

    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private PortalService portalService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private PortalDataService portalDataService;
    @Autowired
    private PortalManageService portalManageService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionServiceImpl permissionService;
    @Resource
    private AdminUserService userService;

    @Operation(summary = "门户列表" )
    @GetMapping("/list")
    //@SaCheckPermission("onlineDev.visualPortal" )
    public ActionResult list(PortalPagination portalPagination) {
        List<VisualFunctionModel> modelAll = portalService.getModelList(portalPagination);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(portalPagination, PaginationVO.class);
        return ActionResult.page(modelAll, paginationVO);
    }

    @Operation(summary = "门户树形列表" )
    @Parameters({
            @Parameter(name = "type" , description = "类型：0-门户设计,1-配置路径" ),
    })
    @GetMapping("/Selector" )
    public ActionResult<ListVO<PortalSelectVO>> listSelect(String platform) {
        List<PortalSelectModel> modelList = portalService.getModList(new PortalViewPrimary(platform, null));
        return ActionResult.success(new ListVO<>(
                JsonUtil.getJsonToList(TreeDotUtils.convertListToTreeDot(modelList),
                        PortalSelectVO.class)));
    }

    @Operation(summary = "门户详情" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @GetMapping("/{id}" )
    public ActionResult<PortalInfoVO> info(@PathVariable("id" ) String id, String platform) throws Exception {
        //StpUtil.checkPermissionOr("onlineDev.visualPortal" , id);
        PortalEntity entity = portalService.getInfo(id);
        PortalInfoVO vo = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToStringDateFormat(entity, "yyyy-MM-dd HH:mm:ss" ), PortalInfoVO.class);
        vo.setFormData(portalDataService.getModelDataForm(new PortalModPrimary(id)));
        List<PortalManageEntity> isReleaseList = portalManageService.list();
        vo.setPcIsRelease(isReleaseList.stream().anyMatch(r-> r.getPortalId().equalsIgnoreCase(id)
                && PortalConst.WEB.equalsIgnoreCase(r.getPlatform())) ? 1 : 0);
        vo.setAppIsRelease(isReleaseList.stream().anyMatch(r-> r.getPortalId().equalsIgnoreCase(id)
                && PortalConst.APP.equalsIgnoreCase(r.getPlatform())) ? 1 : 0);
        return ActionResult.success(vo);
    }

    @Operation(summary = "删除门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @DeleteMapping("/{id}" )
    //@SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ActionResult<String> delete(@PathVariable("id" ) String id) {
        PortalEntity entity = portalService.getInfo(id);
        if (entity != null) {
            try {
                portalService.delete(entity);
            } catch (Exception e) {
                return ActionResult.fail(e.getMessage());
            }
        }
        return ActionResult.success(MsgCode.SU003.get());
    }

    @Operation(summary = "创建门户" )
    @PostMapping("/create")
    //@SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ActionResult<String> create(@RequestBody @Valid PortalCrForm portalCrForm) throws Exception {
        PortalEntity entity = JsonUtil.getJsonToBean(portalCrForm, PortalEntity.class);
        entity.setId(RandomUtil.uuId());
        //判断名称是否重复
        if (portalService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("门户" + MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (portalService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ActionResult.fail("门户" + MsgCode.EXIST002.get());
        }
        // 修改模板排版数据
        if(Objects.equals(entity.getType(),1)){
            entity.setEnabledLock(null);
        }
        portalService.create(entity);
        portalDataService.createOrUpdate(new PortalModPrimary(entity.getId()), portalCrForm.getFormData());
        return ActionResult.success(MsgCode.SU001.get(), entity.getId());
    }

    @Operation(summary = "复制功能" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PostMapping("/{id}/Actions/Copy" )
    //@SaCheckPermission("onlineDev.visualPortal" )
    public ActionResult copyInfo(@PathVariable("id" ) String id) throws Exception {
        PortalEntity entity = portalService.getInfo(id);
        entity.setEnabledMark(0);
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setLastModifyTime(null);
        entity.setLastModifyUserId(null);
        entity.setId(RandomUtil.uuId());
        entity.setEnCode(entity.getEnCode() + copyNum);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        PortalEntity entity1 = JsonUtil.getJsonToBean(entity, PortalEntity.class);
        if (entity1.getEnCode().length() > 50 || entity1.getFullName().length() > 50) {
            return ActionResult.fail("已到达该模板复制上限，请复制源模板" );
        }
        portalService.create(entity1);
        portalDataService.createOrUpdate(new PortalModPrimary(entity1.getId()),
                portalDataService.getModelDataForm(new PortalModPrimary(id)));
        return ActionResult.success(MsgCode.SU007.get());
    }

    @Operation(summary = "修改门户" )
    @Parameters({
            @Parameter(name = "id" , description = "主键" ),
    })
    @PutMapping("/{id}" )
    //@SaCheckPermission("onlineDev.visualPortal" )
    @DSTransactional
    public ActionResult<Object> update(@PathVariable("id" ) String id, @RequestBody @Valid PortalUpForm portalUpForm) throws Exception {
        PortalEntity originEntity = portalService.getInfo(portalUpForm.getId());
        //判断名称是否重复
        if (!originEntity.getFullName().equals(portalUpForm.getFullName())) {
            if (portalService.isExistByFullName(portalUpForm.getFullName(), portalUpForm.getId())) {
                return ActionResult.fail("门户" + MsgCode.EXIST001.get());
            }
        }
        //判断编码是否重复
        if (!originEntity.getEnCode().equals(portalUpForm.getEnCode())) {
            if (portalService.isExistByEnCode(portalUpForm.getEnCode(), portalUpForm.getId())) {
                return ActionResult.fail("门户" + MsgCode.EXIST002.get());
            }
        }
        // 修改排版数据
        if(Objects.equals(portalUpForm.getType(),1)){
            portalUpForm.setEnabledLock(null);
        }
        portalDataService.createOrUpdate(new PortalModPrimary(portalUpForm.getId()), portalUpForm.getFormData());
        portalService.update(id, JsonUtil.getJsonToBean(portalUpForm, PortalEntity.class));
        return ActionResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "门户导出" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
    })
    @PostMapping("/{modelId}/Actions/ExportData" )
    //@SaCheckPermission("onlineDev.visualPortal" )
    public ActionResult exportFunction(@PathVariable("modelId" ) String modelId) throws Exception {
        PortalEntity entity = portalService.getInfo(modelId);
        if (entity != null) {
            PortalExportDataVo vo = new PortalExportDataVo();
            BeanUtils.copyProperties(entity, vo);
            vo.setId(null);
            vo.setModelType(ExportModelTypeEnum.Portal.getMessage());
            vo.setFormData(portalDataService.getModelDataForm(new PortalModPrimary(entity.getId())));
            DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.VISUAL_PORTAL.getTableName());
            return ActionResult.success(downloadVO);
        } else {
            return ActionResult.success("并无该条数据" );
        }
    }

    @Operation(summary = "门户导入" )
    @Parameters({
            @Parameter(name = "file" , description = "导入文件" ),
    })
    @PostMapping(value = "/Model/Actions/ImportData" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@SaCheckPermission("onlineDev.visualPortal" )
    public ActionResult importFunction(@RequestPart("file" ) MultipartFile multipartFile) throws Exception {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_PORTAL.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        PortalExportDataVo vo = JsonUtil.getJsonToBean(fileContent, PortalExportDataVo.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.Portal.getMessage())) {
            return ActionResult.fail("请导入对应功能的json文件" );
        }
        //判断名称是否重复
        if (portalService.isExistByFullName(vo.getFullName(), null)) {
            return ActionResult.fail("门户" + MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (portalService.isExistByEnCode(vo.getEnCode(), null)) {
            return ActionResult.fail("门户" + MsgCode.EXIST002.get());
        }
        PortalEntity entity = JsonUtil.getJsonToBean(fileContent, PortalEntity.class);
        entity.setId(null);
        entity.setEnabledMark(0);
        entity.setSortCode(0l);
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setLastModifyTime(null);
        entity.setLastModifyUserId(null);
        portalService.create(entity);
        portalDataService.createOrUpdate(new PortalModPrimary(entity.getId()), vo.getFormData());
        return ActionResult.success(MsgCode.IMP001.get());
    }

    @Operation(summary = "门户管理下拉列表" )
    @GetMapping("/manage/Selector/{systemId}" )
    public ActionResult<PageListVO<PortalSelectVO>> getManageSelectorList(@PathVariable String systemId, PortalPagination portalPagination) {
        portalPagination.setType(null); // 门户设计、配置路径。全选
        List<PortalSelectVO> voList = portalService.getManageSelectorPage(portalPagination, systemId);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(portalPagination, PaginationVO.class);
        return ActionResult.page(voList, paginationVO);
    }

    @Operation(summary = "获取菜单下拉列表")
    @GetMapping("/Selector/All")
    public ActionResult<Map> getSelectorAll(@RequestParam(required = false) String type){
        AdminUserDO user = userService.getUser(getLoginUserId());
        List<MenuDO> menuList = permissionService.getUserMenuListByUser(user.getId(), user.getUsername());
        // i18n
        menuList = menuService.toI18n(menuList);
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.BUTTON.getType()));
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));
        Map<Long, Map> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), transToMeanue(menu)));
        treeNodeMap.values().stream().filter(node -> !MapUtil.getStr(node,"parentId").equals(MenuDO.ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            Map parentNode = treeNodeMap.get(Long.parseLong(MapUtil.getStr(childNode,"parentId")));
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).warn("[buildRouterTree][resource({}) 找不到父资源({})]",
                        MapUtil.getStr(childNode,"id"), MapUtil.getStr(childNode,"parentId"));
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.get("children") == null) {
                parentNode.put("children",new ArrayList<>());
            }
            List<Map<String,Object>> children = (List<Map<String, Object>>) parentNode.get("children");
            if (type==null || StringUtil.isEmpty(type)) {
                children.add(childNode);
            }else if (MapUtil.getStr(childNode,"type").equals(type)){
                children.add(childNode);
            }
            parentNode.put("children",children);
        });
        List<Map> rootMenue = filterList(treeNodeMap.values(), node -> MenuDO.ID_ROOT.toString().equals(MapUtil.getStr(node, "parentId")));
        Map<String,Object> map=new HashMap<>();
        map.put("list",rootMenue);
        return ActionResult.success(map);
    }
    Map transToMeanue(@NotNull MenuDO menuDO){
        Map<String, Object> map = new HashMap<>();
        map.put("enCode", menuDO.getAlias());
        map.put("enabledMark", 1);
        map.put("fullName", menuDO.getName());
        map.put("hasChildren", true);
        map.put("hasModule", false);
        map.put("icon", menuDO.getIcon());
        map.put("id", menuDO.getId());
        map.put("linkTarget", null);
        map.put("parentId", menuDO.getParentId());
        map.put("propertyJson", null);
        map.put("sortCode", menuDO.getSort());
        map.put("systemId", null);
        map.put("type", menuDO.getType());
        map.put("urlAddress", menuDO.getPath());
        return map;
    }
}
