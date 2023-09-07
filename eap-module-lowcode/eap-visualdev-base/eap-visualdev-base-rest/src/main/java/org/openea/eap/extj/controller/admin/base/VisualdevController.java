package org.openea.eap.extj.controller.admin.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.ModuleEntity;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.*;
import org.openea.eap.extj.base.model.online.VisualMenuModel;
import org.openea.eap.extj.base.model.template6.BtnData;
import org.openea.eap.extj.base.service.*;
import org.openea.eap.extj.base.util.PubulishUtil;
import org.openea.eap.extj.base.util.VisualFlowFormUtil;
import org.openea.eap.extj.base.util.VisualUtil;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.JsonUtilEx;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化基础模块
 */
@Tag(name = "可视化基础模块", description = "Base")
@RestController
@RequestMapping("/api/visualdev/Base")
public class VisualdevController extends SuperController<VisualdevService, VisualdevEntity> {

    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private PubulishUtil pubulishUtil;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private VisualFlowFormUtil visualFlowFormUtil;

    @Autowired
    private FilterService filterService;

    @Operation(summary = "获取功能列表")
    @GetMapping
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult<PageListVO<VisualFunctionModel>> list(PaginationVisualdev paginationVisualdev) {
        // 全部功能表单模板
        List<VisualdevEntity> data = visualdevService.getList(paginationVisualdev);
        List<String> userId = data.stream().map(t -> t.getCreatorUser()).collect(Collectors.toList());
        List<String> lastUserId = data.stream().map(t -> t.getLastModifyUser()).collect(Collectors.toList());
        List<UserEntity> userEntities = userService.getUserName(userId);
        List<UserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        // 表单类型
        List<DictionaryDataEntity> dictionList = visualFlowFormUtil.getListByTypeDataCode(paginationVisualdev.getType());
        List<VisualFunctionModel> modelAll = new LinkedList<>();

        // 遍历功能表单模板
        for (VisualdevEntity entity : data) {
            VisualFunctionModel model = JsonUtil.getJsonToBean(entity, VisualFunctionModel.class);
            // 是否在表单类型中存在，若存在进行装配
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getId().equals(entity.getCategory())).findFirst().orElse(null);
            //避免导入的功能丢失
            model.setCategory(dataEntity != null ? dataEntity.getFullName() : null);
            UserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(model.getCreatorUser())).findFirst().orElse(null);
            model.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : "");
            UserEntity lastmodifyuser = lastUserIdEntities.stream().filter(t -> t.getId().equals(model.getLastModifyUser())).findFirst().orElse(null);
            model.setLastModifyUser(lastmodifyuser != null ? lastmodifyuser.getRealName() + "/" + lastmodifyuser.getAccount() : "");
            List<ModuleEntity> moduleList = moduleService.getModuleList(entity.getId());
            model.setAppIsRelease(0);
            model.setPcIsRelease(0);
            if (moduleList.size() > 0) {
                ModuleEntity appModuleEntity = moduleList.stream().filter(module -> "app".equalsIgnoreCase(module.getCategory())).findFirst().orElse(null);
                ModuleEntity pcModuleEntity = moduleList.stream().filter(module -> "web".equalsIgnoreCase(module.getCategory())).findFirst().orElse(null);
                model.setAppIsRelease(Objects.nonNull(appModuleEntity) ? 1 : 0);
                model.setPcIsRelease(Objects.nonNull(pcModuleEntity) ? 1 : 0);
            }
            boolean released = visualdevReleaseService.beenReleased(entity.getId()) > 0;
            model.setIsRelease(released ? 1 : 0);
            if (Objects.equals(entity.getType(), 4)) {
                model.setHasPackage(true);
            }
            modelAll.add(model);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationVisualdev, PaginationVO.class);
        return ActionResult.page(modelAll, paginationVO);
    }

    @Operation(summary = "获取功能列表下拉框")
    @Parameters({
            @Parameter(name = "type", description = "类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)"),
            @Parameter(name = "isRelease", description = "是否发布"),
            @Parameter(name = "webType", description = "页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）"),
            @Parameter(name = "enableFlow", description = "是否启用流程")
    })
    @GetMapping("/Selector")
    public ActionResult selectorList(Integer type, Integer isRelease, String webType, Integer enableFlow) {
        List<VisualdevEntity> allList;
        List<VisualdevEntity> list = new ArrayList<>();
        if (isRelease != null) {
            List<VisualdevReleaseEntity> releaseEntities = visualdevReleaseService.list();
            allList = JsonUtil.getJsonToList(releaseEntities, VisualdevEntity.class);
        } else {
            allList = visualdevService.getList();
        }
        if (webType != null) {
            String[] webTypes = webType.split(",");
            for (String wbType : webTypes) {
                List<VisualdevEntity> collect;
                if (enableFlow != null) {
                    collect = allList.stream().filter(l -> l.getWebType().equals(Integer.valueOf(wbType)) && l.getEnableFlow().equals(enableFlow)).collect(Collectors.toList());
                } else {
                    collect = allList.stream().filter(l -> l.getWebType().equals(Integer.valueOf(wbType))).collect(Collectors.toList());
                }
                list.addAll(collect);
            }
        } else {
            list = allList;
        }
        List<DictionaryDataEntity> dataEntityList = new ArrayList<>();
        List<VisualdevTreeVO> voList = new ArrayList<>();
        HashSet<String> cate = new HashSet<>(16);
        if (type != null) {
            list = list.stream().filter(t -> type.equals(t.getType())).collect(Collectors.toList());
            dataEntityList = visualFlowFormUtil.getListByTypeDataCode(type);
            // 遍历数据字典得到外部分类
            for (DictionaryDataEntity dataEntity : dataEntityList) {
                List<VisualdevEntity> num = list.stream().filter(t -> dataEntity.getId().equals(t.getCategory())).collect(Collectors.toList());
                if (num.size() <= 0) {
                    continue;
                }
                int i = cate.size();
                cate.add(dataEntity.getId());
                if (cate.size() == i + 1) {
                    VisualdevTreeVO visualdevTreeVO = new VisualdevTreeVO();
                    visualdevTreeVO.setId(dataEntity.getId());
                    visualdevTreeVO.setFullName(dataEntity.getFullName());
                    visualdevTreeVO.setHasChildren(true);
                    voList.add(visualdevTreeVO);
                }
            }
        } else {
            // type为空时
            for (VisualdevEntity entity : list) {
                DictionaryDataEntity dataEntity = visualFlowFormUtil.getdictionaryDataInfo(entity.getCategory());
                if (dataEntity != null) {
                    int i = cate.size();
                    cate.add(dataEntity.getId());
                    if (cate.size() == i + 1) {
                        VisualdevTreeVO visualdevTreeVO = new VisualdevTreeVO();
                        visualdevTreeVO.setId(entity.getCategory());
                        visualdevTreeVO.setFullName(dataEntity.getFullName());
                        visualdevTreeVO.setHasChildren(true);
                        voList.add(visualdevTreeVO);
                    }
                }

            }
        }
        for (VisualdevTreeVO vo : voList) {
            List<VisualdevTreeChildModel> visualdevTreeChildModelList = new ArrayList<>();
            for (VisualdevEntity entity : list) {
                if (vo.getId().equals(entity.getCategory())) {
                    VisualdevTreeChildModel model = JsonUtil.getJsonToBean(entity, VisualdevTreeChildModel.class);
                    visualdevTreeChildModelList.add(model);
                }
            }
            vo.setChildren(visualdevTreeChildModelList);
        }
        ListVO listVO = new ListVO();
        listVO.setList(voList);
        return ActionResult.success(listVO);
    }

    @Operation(summary = "获取功能信息")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @GetMapping("/{id}")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        VisualdevEntity entity = visualdevService.getInfo(id);
        VisualDevInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualDevInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 获取表单主表属性下拉框
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取表单主表属性下拉框")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
            @Parameter(name = "filterType", description = "过滤类型：1-按键事件选择字段列表过滤"),
    })
    @GetMapping("/{id}/FormDataFields")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult getFormData(@PathVariable("id") String id, @RequestParam(value = "filterType", required = false) Integer filterType) {
        List<FormDataField> fieldList = visualdevModelDataService.fieldList(id, filterType);
        ListVO<FormDataField> listVO = new ListVO();
        listVO.setList(fieldList);
        return ActionResult.success(listVO);
    }

    /**
     * 关联数据分页数据
     *
     * @param id
     * @param paginationModel
     * @return
     */
    @Operation(summary = "关联数据分页数据")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @GetMapping("/{id}/FieldDataSelect")
    public ActionResult getFormData(@PathVariable("id") String id, PaginationModel paginationModel) {
        VisualdevEntity entity = visualdevService.getInfo(id);
        List<Map<String, Object>> realList = visualdevModelDataService.getPageList(entity, paginationModel);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationModel, PaginationVO.class);
        return ActionResult.page(realList, paginationVO);
    }


    /**
     * 复制功能
     *
     * @param id
     * @return
     */
    @Operation(summary = "复制功能")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/Copy")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult copyInfo(@PathVariable("id") String id) throws WorkFlowException {
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        boolean b = releaseEntity != null;
        VisualdevEntity entity;
        //已发布取发布版本
        if (b) {
            entity = JsonUtil.getJsonToBean(releaseEntity, VisualdevEntity.class);
        } else {
            entity = visualdevService.getInfo(id);
        }
        String copyNum = UUID.randomUUID().toString().substring(0, 5);
        entity.setFullName(entity.getFullName() + ".副本" + copyNum);
        entity.setLastModifyTime(null);
        entity.setLastModifyUser(null);
        entity.setCreatorTime(null);
        entity.setId(RandomUtil.uuId());
        entity.setEnCode(entity.getEnCode() + copyNum);
        VisualdevEntity entity1 = JsonUtil.getJsonToBean(entity, VisualdevEntity.class);
        if (entity1.getEnCode().length() > 50 || entity1.getFullName().length() > 50) {
            return ActionResult.fail("已到达该模板复制上限，请复制源模板");
        }
        //启用流程，流程先保存如果不成功直接提示。//>3属于代码生成。不自动创建
        if (OnlineDevData.STATE_ENABLE == entity1.getEnableFlow() && entity1.getType() < 3) {
            //生成流程
            ActionResult result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity1, OnlineDevData.STATE_ENABLE, true);
            if (200 != result.getCode()) {
                return ActionResult.fail("同步到流程时，" + result.getMsg());
            }
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity1, OnlineDevData.STATE_ENABLE, true);
        }
        visualdevService.create(entity1);
        return ActionResult.success("复制功能成功");
    }


    /**
     * 更新功能状态
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "更新功能状态")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PutMapping("/{id}/Actions/State")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult update(@PathVariable("id") String id) throws Exception {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            boolean flag = visualdevService.update(entity.getId(), entity);
            if (flag == false) {
                return ActionResult.fail("更新失败，任务不存在");
            }
        }
        return ActionResult.success(MsgCode.SU004.get());
    }


    @Operation(summary = "新建功能")
    @PostMapping
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult create(@RequestBody VisualDevCrForm visualDevCrForm) throws Exception {
        VisualdevEntity entity = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToString(visualDevCrForm), VisualdevEntity.class);
        if (visualdevService.getObjByEncode(entity.getEnCode(), entity.getType()) > 0) {
            return ActionResult.fail("编码重复");
        }
        if (visualdevService.getCountByName(entity.getFullName(), entity.getType()) > 0) {
            return ActionResult.fail("名称重复");
        }
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);

        //判断子表是否复用
        RecursionForm recursionForm = new RecursionForm();
        if (ObjectUtil.isNotNull(formData)) {
            //表单主键策略验证
            if (tableModelList.size() > 0) {
                boolean isIncre = Objects.equals(formData.getPrimaryKeyPolicy(), 2);
                String strategy = !isIncre ? "[雪花ID]" : "[自增长id]";
                for (TableModel tableModel : tableModelList) {
                    Boolean isAutoIncre = visualdevService.getPrimaryDbField(entity.getDbLinkId(), tableModel.getTable());
                    if (isAutoIncre == null) {
                        return ActionResult.fail("表[" + tableModel.getTable() + "]无主键!");
                    }
                    if (isIncre != isAutoIncre) {
                        return ActionResult.fail("主键策略:" + strategy + "，与表[" + tableModel.getTable() + "]主键策略不一致!");
                    }
                }
            }
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            recursionForm.setList(list);
            recursionForm.setTableModelList(tableModelList);
            if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                return ActionResult.fail("子表重复");
            }
        }
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        //启用流程，流程先保存如果不成功直接提示。//>3属于代码生成。不自动创建
        if (OnlineDevData.STATE_ENABLE == entity.getEnableFlow() && entity.getType() < 3) {
            //生成流程
            ActionResult result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity, OnlineDevData.STATE_ENABLE, true);
            if (200 != result.getCode()) {
                return ActionResult.fail("同步到流程时，" + result.getMsg());
            }
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, true);
        }
        visualdevService.create(entity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "修改功能")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PutMapping("/{id}")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult update(@PathVariable("id") String id, @RequestBody VisualDevUpForm visualDevUpForm) throws Exception {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String enCode = visualdevEntity.getEnCode();
        String fullName = visualdevEntity.getFullName();
        VisualdevEntity entity = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToString(visualDevUpForm), VisualdevEntity.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);

        Map<String, String> tableMap = visualdevService.getTableMap(entity.getFormData());
        // 如果不是在线的,默认更新所有配置
        if (!"1".equals(visualDevUpForm.getType())) {
            filterService.updateRuleList(id, entity, 1, 1, tableMap);
        }

        if (!enCode.equals(visualDevUpForm.getEnCode())) {
            if (visualdevService.getObjByEncode(entity.getEnCode(), entity.getType()) > 0) {
                return ActionResult.fail("编码重复");
            }
        }
        if (!fullName.equals(visualDevUpForm.getFullName())) {
            if (visualdevService.getCountByName(entity.getFullName(), entity.getType()) > 0) {
                return ActionResult.fail("名称重复");
            }
        }
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        //是否发布
        if (releaseEntity != null && !VisualWebTypeEnum.DATA_VIEW.getType().equals(releaseEntity.getWebType())) {
            if (tableModelList.size() == 0) {
                return ActionResult.fail(MsgCode.VS408.get());
            }
        }

        //判断子表是否复用
        if (tableModelList.size() > 0) {
            RecursionForm recursionForm = new RecursionForm();
            FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
            if (ObjectUtil.isNotNull(formData)) {
                List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
                recursionForm.setList(list);
                recursionForm.setTableModelList(tableModelList);
                if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                    return ActionResult.fail("子表重复");
                }
            }
        }
        //先不启用流程
        entity.setEnableFlow(0);
        //修改流程表单同步信息 type<3属于功能设计
        if (OnlineDevData.STATE_ENABLE == entity.getEnableFlow() && entity.getType() < 3) {
            entity.setId(id);
            //生成表单
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, true);
            //启用流程，修改流程基础信息
            FlowTemplateInfoVO templateInfo = visualFlowFormUtil.getTemplateInfo(visualdevEntity.getId());
            //编辑时不改变流程基础信息,若没有流程则创建
            if (templateInfo == null) {
                ActionResult result = visualFlowFormUtil.saveOrUpdateFlowTemp(entity, entity.getEnableFlow(), true);
                if (200 != result.getCode()) {
                    return ActionResult.fail("同步到流程时，" + result.getMsg());
                }
            }
            visualFlowFormUtil.saveOrUpdateForm(entity, OnlineDevData.STATE_ENABLE, false);
        }
        boolean flag = visualdevService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

    @Operation(summary = "删除功能")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @DeleteMapping("/{id}")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult delete(@PathVariable("id") String id) throws Exception {
        VisualdevEntity entity = visualdevService.getInfo(id);
        if (entity != null) {
            visualdevService.delete(entity);
            visualdevReleaseService.removeById(id);
            //启用流程的情况，需要删除流程和表单,删除成功与否不管。
            if (OnlineDevData.STATE_ENABLE == entity.getEnableFlow()) {
                visualFlowFormUtil.deleteFlowForm(entity.getId());
                visualFlowFormUtil.deleteTemplateInfo(entity.getId());
            }
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }

    @Operation(summary = "获取模板按钮和列表字段")
    @Parameters({
            @Parameter(name = "moduleId", description = "模板id"),
    })
    @GetMapping("/ModuleBtn")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult getModuleBtn(String moduleId) {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(moduleId);
        //去除模板中的F_
        VisualUtil.delfKey(visualdevEntity);
        List<BtnData> btnData = new ArrayList<>();
        Map<String, Object> column = JsonUtil.stringToMap(visualdevEntity.getColumnData());
        if (column.get("columnBtnsList") != null) {
            btnData.addAll(JsonUtil.getJsonToList(JsonUtil.getJsonToListMap(column.get("columnBtnsList").toString()), BtnData.class));
        }
        if (column.get("btnsList") != null) {
            btnData.addAll(JsonUtil.getJsonToList(JsonUtil.getJsonToListMap(column.get("btnsList").toString()), BtnData.class));
        }
        return ActionResult.success(btnData);
    }

    @Operation(summary = "发布模板")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/Release")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult publish(@PathVariable("id") String id, @RequestBody VisualDevPubModel visualDevPubModel) throws Exception {
        //草稿
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        //启用流程判断流程是否设计完成
        if (Objects.equals(OnlineDevData.STATE_ENABLE, visualdevEntity.getEnableFlow())) {
            FlowTemplateInfoVO templateInfo = visualFlowFormUtil.getTemplateInfo(id);
            if (templateInfo == null || StringUtil.isEmpty(templateInfo.getFlowTemplateJson()) || "[]".equals(templateInfo.getFlowTemplateJson())) {
                return ActionResult.fail("发布失败，流程未设计！");
            }
        }

        List<TableModel> tableModels = JsonUtil.getJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        //数据视图不判断formdata
        if (!VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {
            String s = VisualUtil.checkPublishVisualModel(visualdevEntity, "发布");
            if (s != null) {
                return ActionResult.fail(s);
            }
            if (tableModels.size() == 0) {
                try {
                    visualdevService.createTable(visualdevEntity);
                } catch (WorkFlowException e) {
                    e.printStackTrace();
                    return ActionResult.fail("无表生成有表失败");
                }
            }
            Map<String, String> tableMap = visualdevService.getTableMap(visualdevEntity.getFormData());
            filterService.updateRuleList(id, visualdevEntity, visualDevPubModel.getApp(), visualDevPubModel.getPc(), tableMap);
        }
        //线上
        VisualdevEntity clone = new VisualdevEntity();
        BeanUtil.copyProperties(visualdevEntity, clone);
        // 更新功能-表写入 菜单创建成功后
        visualdevService.update(id, visualdevEntity);

        //将线上版本发布
        VisualMenuModel visual = VisualUtil.getVisual(clone, visualDevPubModel);
        visual.setApp(visualDevPubModel.getApp());
        visual.setPc(visualDevPubModel.getPc());
        visual.setAppModuleParentId(visualDevPubModel.getAppModuleParentId());
        visual.setPcModuleParentId(visualDevPubModel.getPcModuleParentId());
        visual.setPcSystemId(Optional.ofNullable(visualDevPubModel.getPcSystemId()).orElse(visualDevPubModel.getPcModuleParentId()));
        visual.setAppSystemId(Optional.ofNullable(visualDevPubModel.getAppSystemId()).orElse(visualDevPubModel.getAppModuleParentId()));
        Integer integer = pubulishUtil.publishMenu(visual);
        if (integer == 2) {
            return ActionResult.fail("同步失败,检查编码或名称是否重复");
        }
        if (integer == 3) {
            return ActionResult.fail("请选择父级菜单");
        }
        VisualdevReleaseEntity releaseEntity = JsonUtil.getJsonToBean(clone, VisualdevReleaseEntity.class);
        visualdevReleaseService.saveOrUpdateIgnoreLogic(releaseEntity);
        // 启用流程 在表单新增一条 提供给流程使用
        if (OnlineDevData.STATE_ENABLE == visualdevEntity.getEnableFlow()) {
            visualFlowFormUtil.saveOrUpdateForm(clone, OnlineDevData.STATE_ENABLE, false);
            visualFlowFormUtil.saveOrUpdateFlowTemp(visualdevEntity, 1, false);
        }
        return ActionResult.success("同步成功");
    }

    @Operation(summary = "回滚模板")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @GetMapping("/{id}/Actions/RollbackTemplate")
    //@SaCheckPermission(value = {"onlineDev.webDesign" , "generator.webForm" , "generator.flowForm"}, mode = SaMode.OR)
    public ActionResult RollbackTemplate(@PathVariable("id") String id) {
        VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(id);
        boolean b = releaseEntity == null;
        if (b) {
            return ActionResult.fail("回滚失败,暂无线上版本");
        } else {
            VisualdevEntity visualdevEntity = JsonUtil.getJsonToBean(releaseEntity, VisualdevEntity.class);
            visualdevService.updateById(visualdevEntity);
        }
        return ActionResult.success("回滚成功");
    }

}
