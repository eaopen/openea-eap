package org.openea.eap.extj.controller.admin.onlinedev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.model.VisualWebTypeEnum;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.VisualdevReleaseService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.base.util.VisualUtil;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.engine.service.FlowTemplateJsonService;
import org.openea.eap.extj.engine.service.FlowTemplateService;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.extend.util.ExcelUtil;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.form.service.FormDataService;
import org.openea.eap.extj.form.util.FormCheckUtils;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.onlinedev.entity.VisualdevModelDataEntity;
import org.openea.eap.extj.onlinedev.model.*;
import org.openea.eap.extj.onlinedev.model.OnlineImport.ExcelImportModel;
import org.openea.eap.extj.onlinedev.model.OnlineImport.ImportExcelFieldModel;
import org.openea.eap.extj.onlinedev.model.OnlineImport.VisualImportModel;
import org.openea.eap.extj.onlinedev.service.VisualDevInfoService;
import org.openea.eap.extj.onlinedev.service.VisualDevListService;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.onlinedev.util.AutoFeildsUtil;
import org.openea.eap.extj.onlinedev.util.OnlineDevDbUtil;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineDevListUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ExportModelTypeEnum;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0代码无表开发
 *
 */
@Slf4j
@Tag(name = "0代码无表开发" , description = "OnlineDev" )
@RestController
@RequestMapping("/api/visualdev/OnlineDev" )
public class VisualdevModelDataController extends SuperController<VisualdevModelDataService, VisualdevModelDataEntity> {

    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private VisualDevListService visualDevListService;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private VisualDevInfoService visualDevInfoService;
    @Autowired
    private FormCheckUtils formCheckUtils;
    @Autowired
    private OnlineDevDbUtil onlineDevDbUtil;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private UserService userService;
    @Autowired
    private FormDataService formDataService;

    @Operation(summary = "获取数据列表" )
	@Parameters({
			@Parameter(name = "modelId",description = "模板id"),
	})
    @PostMapping("/{modelId}/List" )
    public ActionResult list(@PathVariable("modelId" ) String modelId, @RequestBody PaginationModel paginationModel) throws WorkFlowException {
		//StpUtil.checkPermission(modelId);

		VisualdevReleaseEntity visualdevEntity = visualdevReleaseService.getById(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin" );

        if (!"pc".equals(header)) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        ColumnDataModel columnDataModel = visualJsonModel.getColumnData();
        List<Map<String, Object>> realList;
        if (VisualWebTypeEnum.FORM.getType().equals(visualdevEntity.getWebType())) {
            realList = new ArrayList<>();
        } else if (VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//
            //数据视图的接口数据获取、
            realList = onlineSwapDataUtils.getInterfaceData(visualdevEntity, paginationModel, columnDataModel);
        } else {
            realList = visualDevListService.getDataList(visualJsonModel, paginationModel);
        }

        //判断数据是否分组
        if (OnlineDevData.TYPE_THREE_COLUMNDATA.equals(columnDataModel.getType())) {
            realList = OnlineDevListUtils.groupData(realList, columnDataModel);
        }
        //树形列表
        if (OnlineDevData.TYPE_FIVE_COLUMNDATA.equals(columnDataModel.getType())) {
            realList = OnlineDevListUtils.treeListData(realList, columnDataModel);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationModel, PaginationVO.class);

        return ActionResult.page(realList, paginationVO);
    }

    @Operation(summary = "树形异步查询子列表接口" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
            @Parameter(name = "id" , description = "数据id" ),
    })
    @PostMapping("/{modelId}/List/{id}" )
    public ActionResult listTree(@PathVariable("modelId" ) String modelId, @RequestBody PaginationModel paginationModel, @PathVariable("id" ) String id) throws WorkFlowException {
        //StpUtil.checkPermission(modelId);

        VisualdevReleaseEntity visualdevEntity = visualdevReleaseService.getById(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin" );
        if (!"pc".equals(header)) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        VisualdevEntity ve = visualdevService.getReleaseInfo(modelId);
        VisualdevModelDataInfoVO editDataInfo = visualDevInfoService.getEditDataInfo(id, ve);
        //提取父级对象的子字段当成查询条件  字段以id作为pid值
        visualJsonModel.setChildValue(id);
        visualJsonModel.setChildSearch(true);
        List<Map<String, Object>> realList = visualDevListService.getDataList(visualJsonModel, paginationModel);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationModel, PaginationVO.class);
        return ActionResult.page(realList, paginationVO);
    }


    @Operation(summary = "获取列表表单配置JSON" )
    @GetMapping("/{modelId}/Config" )
    public ActionResult getData(@PathVariable("modelId" ) String modelId, @RequestParam(value = "type" , required = false) String type) throws WorkFlowException {
        VisualdevEntity entity;
        //线上版本
        if ("0".equals(type)) {
            entity = visualdevService.getInfo(modelId);
        } else {
            VisualdevReleaseEntity releaseEntity = visualdevReleaseService.getById(modelId);
            entity = JsonUtil.getJsonToBean(releaseEntity, VisualdevEntity.class);
        }
        if (entity == null) {
            return ActionResult.fail("功能不存在" );
        }
        if (!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())) {
            String s = VisualUtil.checkPublishVisualModel(entity, "预览" );
            if (s != null) {
                return ActionResult.fail(s);
            }
        }
        DataInfoVO vo = JsonUtil.getJsonToBean(entity, DataInfoVO.class);
        if (entity.getEnableFlow() == 1) {
            FlowFormEntity byId = flowFormService.getById(entity.getId());
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(byId.getFlowId());
            if (templateInfo == null) {
                return ActionResult.fail(MsgCode.VS403.get());
            }
            if (OnlineDevData.STATE_DISABLE == templateInfo.getEnabledMark()) {
                return ActionResult.fail(MsgCode.VS406.get());
            }
            vo.setFlowId(templateInfo.getId());
//            FlowFormEntity flowFormEntity = flowFormApi.getById(entity.getId());
//            if (flowFormEntity == null || flowFormEntity.getFlowId() == null) {
//                return ActionResult.fail(MsgCode.VS403.get());
//            } else {
//                FlowTemplateJsonEntity templateJsonEntity = flowTaskApi.getFlowTemplateJsonEntity(flowFormEntity.getFlowId());
//                FlowTemplateEntity templateEntity = flowTaskApi.getFlowTemplateEntity(templateJsonEntity.getTemplateId());
//                boolean b = Optional.ofNullable(templateEntity.getEnabledMark()).orElse(0) == 0;
//                if (b) {
//                    return ActionResult.fail(MsgCode.VS406.get());
//                }
//            }
//            FlowTemplateJsonEntity info = flowTaskApi.getFlowTemplateJsonEntity(flowFormEntity.getFlowId());
//            vo.setFlowTemplateJson(info.getFlowTemplateJson());
//            vo.setFlowId(info.getId());
//            vo.setFlowEnCode(vo.getFlowEnCode());
        }

        //处理默认值
        Map<String, Integer> havaDefaultCurrentValue = new HashMap<String, Integer>();
        UserInfo userInfo = userProvider.get();
        if (StringUtil.isNotEmpty(vo.getFormData())) {
            vo.setFormData(formDataService.setDefaultCurrentValue(vo.getFormData(), havaDefaultCurrentValue));
        }
        if (StringUtil.isNotEmpty(vo.getColumnData())) {
            vo.setColumnData(formDataService.setDefaultCurrentValue(vo.getColumnData(), havaDefaultCurrentValue));
        }
        if (StringUtil.isNotEmpty(vo.getAppColumnData())) {
            vo.setAppColumnData(formDataService.setDefaultCurrentValue(vo.getAppColumnData(), havaDefaultCurrentValue));
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "获取列表配置JSON" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
    })
    @GetMapping("/{modelId}/ColumnData" )
    public ActionResult getColumnData(@PathVariable("modelId" ) String modelId) {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity entity = visualdevService.getInfo(modelId);
        FormDataInfoVO vo = JsonUtil.getJsonToBean(entity, FormDataInfoVO.class);
        return ActionResult.success(vo);
    }

    @Operation(summary = "获取表单配置JSON" )
    @Parameters({
            @Parameter(name = "modelId" , description = "模板id" ),
    })
    @GetMapping("/{modelId}/FormData" )
    public ActionResult<ColumnDataInfoVO> getFormData(@PathVariable("modelId" ) String modelId) {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity entity = visualdevService.getInfo(modelId);
        ColumnDataInfoVO vo = JsonUtil.getJsonToBean(entity, ColumnDataInfoVO.class);
        return ActionResult.success(vo);
    }

    @Operation(summary = "获取数据信息" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
	})
    @GetMapping("/{modelId}/{id}" )
    public ActionResult info(@PathVariable("id" ) String id, @PathVariable("modelId" ) String modelId) throws DataException {
		//StpUtil.checkPermission(modelId);

		id = XSSEscape.escape(id);
        modelId = XSSEscape.escape(modelId);
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataInfoVO editDataInfo = visualDevInfoService.getEditDataInfo(id, visualdevEntity);
            return ActionResult.success(editDataInfo);
        }
        //无表
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        Map<String, Object> formData = JsonUtil.stringToMap(visualdevEntity.getFormData());
        List<FieLdsModel> modelList = JsonUtil.getJsonToList(formData.get("fields" ).toString(), FieLdsModel.class);
        //去除模板多级控件
        modelList = VisualUtils.deleteMore(modelList);
        String data = AutoFeildsUtil.autoFeilds(modelList, entity.getData());
        entity.setData(data);
        VisualdevModelDataInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualdevModelDataInfoVO.class);
        return ActionResult.success(vo);
    }

    @Operation(summary = "获取数据信息(带转换数据)" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "id", description = "数据id"),
	})
    @GetMapping("/{modelId}/{id}/DataChange" )
    public ActionResult infoWithDataChange(@PathVariable("modelId" ) String modelId, @PathVariable("id" ) String id) throws DataException, ParseException, IOException, SQLException {
        modelId = XSSEscape.escape(modelId);
        id = XSSEscape.escape(id);
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataInfoVO vo = visualDevInfoService.getDetailsDataInfo(id, visualdevEntity);
            return ActionResult.success(vo);
        }
        //无表
        VisualdevModelDataInfoVO vo = visualdevModelDataService.infoDataChange(id, visualdevEntity);
        return ActionResult.success(vo);
    }


    @Operation(summary = "添加数据" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "visualdevModelDataCrForm", description = "功能数据创建表单"),
	})
    @PostMapping("/{modelId}" )
    public ActionResult create(@PathVariable("modelId" ) String modelId, @RequestBody VisualdevModelDataCrForm visualdevModelDataCrForm) throws WorkFlowException {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        Map<String, Object> map = JsonUtil.stringToMap(visualdevModelDataCrForm.getData());
        FormDataModel formData = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModels = JsonUtil.getJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        DbLinkEntity linkEntity = StringUtil.isNotEmpty(visualdevEntity.getDbLinkId()) ? dblinkService.getInfo(visualdevEntity.getDbLinkId()) : null;
        //是否开启并发锁
        Boolean concurrency = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            //初始化version值
            map.put("f_version" , 0);
            concurrency = true;
        }
        //单行唯一校验
        String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy, formData.getLogicalDelete(), null);
        if (StringUtil.isNotEmpty(b)) {
            throw new WorkFlowException(b + "不能重复" );
        }
        OnlineSwapDataUtils.swapDatetime(list,map);
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        UserEntity info = userService.getInfo(userInfo.getUserId());
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels).mainId(mainId).link(linkEntity).userEntity(info).concurrencyLock(concurrency).primaryKeyPolicy(primaryKeyPolicy).flowEnable(visualdevEntity.getEnableFlow() == 1).build();
        ActionResult actionResult = visualdevModelDataService.visualCreate(visualdevEntity, dataModel);
        return actionResult;
    }


    @Operation(summary = "修改数据" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "id", description = "数据id"),
			@Parameter(name = "visualdevModelDataUpForm", description = "功能数据修改表单"),
	})
    @PutMapping("/{modelId}/{id}" )
    public ActionResult update(@PathVariable("id" ) String id, @PathVariable("modelId" ) String modelId, @RequestBody VisualdevModelDataUpForm visualdevModelDataUpForm) throws DataException, SQLException, WorkFlowException {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        FormDataModel formData = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        boolean inlineEdit = columnDataModel.getType() != null && columnDataModel.getType() == 4;
        if (inlineEdit) {
            list = JsonUtil.getJsonToList(columnDataModel.getColumnList(), FieLdsModel.class);
            list = list.stream().filter(f -> !f.getVModel().toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX )).collect(Collectors.toList());
        }
        Map<String, Object> map = JsonUtil.stringToMap(visualdevModelDataUpForm.getData());
        List<TableModel> tableModels = JsonUtil.getJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        TableModel mainT = tableModels.stream().filter(t -> t.getTypeId().equals("1" )).findFirst().orElse(null);
        DbLinkEntity linkEntity = StringUtil.isNotEmpty(visualdevEntity.getDbLinkId()) ? dblinkService.getInfo(visualdevEntity.getDbLinkId()) : null;
        //是否开启并发锁
        Boolean isConcurrencyLock = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            if (map.get("f_version" ) == null) {
                map.put("f_version" , 0);
            } else {
                boolean version = onlineDevDbUtil.getVersion(mainT.getTable(), linkEntity, map, id, primaryKeyPolicy);
                if (!version) {
                    return ActionResult.fail(MsgCode.VS405.get());
                } else {
                    Integer vs = Integer.valueOf(String.valueOf(map.get("f_version" )));
                    map.put("f_version" , vs + 1);
                }
            }
            isConcurrencyLock = true;
        }

        String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy, formData.getLogicalDelete(), id);
        if (StringUtil.isNotEmpty(b)) {
            throw new WorkFlowException(b + "不能重复" );
        }
        OnlineSwapDataUtils.swapDatetime(list,map);
        UserInfo userInfo = userProvider.get();
        UserEntity info = userService.getInfo(userInfo.getUserId());
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels).mainId(id).link(linkEntity).userEntity(info).concurrencyLock(isConcurrencyLock).primaryKeyPolicy(primaryKeyPolicy).flowEnable(visualdevEntity.getEnableFlow() == 1).build();
        ActionResult actionResult = visualdevModelDataService.visualUpdate(visualdevEntity, dataModel);
        return actionResult;
    }


    @Operation(summary = "删除数据" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "id", description = "数据id"),
	})
    @DeleteMapping("/{modelId}/{id}" )
    public ActionResult delete(@PathVariable("id" ) String id, @PathVariable("modelId" ) String modelId) throws Exception {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin" );

        if (!"pc".equals(header)) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(id, FlowTaskEntity::getId, FlowTaskEntity::getStatus, FlowTaskEntity::getFullName, FlowTaskEntity::getParentId);
            if (taskEntity != null) {
                if (!"0".equals(taskEntity.getParentId())) {
                    return ActionResult.fail(taskEntity.getFullName() + "不能删除" );
                }
                if (taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4)) {
                    flowTaskService.delete(taskEntity);
                }
            }
            boolean result = visualdevModelDataService.tableDelete(id, visualJsonModel);
            if (result) {
                return ActionResult.success(MsgCode.SU003.get());
            } else {
                return ActionResult.fail(MsgCode.FA003.get());
            }
        }
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        if (entity != null) {
            visualdevModelDataService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }

    @Operation(summary = "批量删除数据" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "idsVo", description = "批量处理参数"),
	})
    @PostMapping("/batchDelete/{modelId}" )
    public ActionResult batchDelete(@RequestBody BatchRemoveIdsVo idsVo, @PathVariable("modelId" ) String modelId) throws Exception {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);

        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin" );

        if (!"pc".equals(header)) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        List<String> idsList = new ArrayList<>();
        List<String> idsVoList = Arrays.asList(idsVo.getIds());
        if (visualdevEntity.getEnableFlow() == 1) {
            for (String id : idsVoList) {
                FlowTaskEntity taskEntity = flowTaskService.getInfoSubmit(id, FlowTaskEntity::getId, FlowTaskEntity::getStatus);
                if (taskEntity != null) {
                    if (taskEntity.getStatus().equals(0) || taskEntity.getStatus().equals(4)) {
                        idsList.add(id);
                        flowTaskService.delete(taskEntity);
                    }
                } else {
                    idsList.add(id);
                }
            }
        } else {
            idsList = idsVoList;
        }
        if (idsList.size() == 0) {
            return ActionResult.fail("该流程已发起，无法删除" );
        }
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            ActionResult result = visualdevModelDataService.tableDeleteMore(idsList, visualJsonModel);
            return result;
        }
        if (visualdevModelDataService.removeByIds(idsList)) {
            return ActionResult.success(MsgCode.SU003.get());
        } else if (visualdevEntity.getEnableFlow() == 1 && idsList.size() > 0) {
            //分组页面
            return ActionResult.fail("该流程已发起，无法删除" );
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }


    @Operation(summary = "导入数据" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "visualImportModel", description = "导入参数"),
	})
    @PostMapping("{modelId}/ImportData" )
    public ActionResult<ExcelImportModel> imports(@PathVariable("modelId" ) String modelId, @RequestBody VisualImportModel visualImportModel) throws WorkFlowException {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        FormDataModel formData = visualJsonModel.getFormData();
        List<FieLdsModel> fieldsModelList = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<FieLdsModel> allFieLds = new ArrayList<>();
        VisualUtils.recursionFields(fieldsModelList, allFieLds);
        visualJsonModel.setFormListModels(allFieLds);

        ExcelImportModel excelData = new ExcelImportModel();
        excelData = onlineSwapDataUtils.createExcelData(visualImportModel.getList(), visualJsonModel);
        return ActionResult.success(excelData);
    }

    @Operation(summary = "导入" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
	})
    @PostMapping("/Model/{modelId}/Actions/Import" )
    public ActionResult imports(@PathVariable("modelId" ) String modelId) {
		//StpUtil.checkPermission(modelId);

		VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(modelId);
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().contains(".xlsx" )) {
            String filePath = configValueUtil.getTemporaryFilePath();
            String fileName = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //保存文件
            FileUtil.upFile(file, filePath, fileName);
            File temporary = new File(XSSEscape.escapePath(filePath + fileName));
            return ActionResult.success(MsgCode.IMP001.get());
        } else {
            return ActionResult.fail("选择文件不符合导入" );
        }
    }

    @Operation(summary = "导出" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "paginationModelExport", description = "导出参数"),
	})
    @PostMapping("/{modelId}/Actions/Export" )
    public ActionResult export(@PathVariable("modelId" ) String modelId, @RequestBody PaginationModelExport paginationModelExport) throws ParseException, IOException, SQLException, DataException {
        //StpUtil.checkPermission(modelId);

        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);

        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(visualdevEntity);
        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin");
        if (!"pc".equals(header)) {
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }

        String[] keys = paginationModelExport.getSelectKey();
        //关键字过滤
        List<Map<String, Object>> realList;
        DownloadVO vo;
        if (VisualWebTypeEnum.DATA_VIEW.getType().equals(visualdevEntity.getWebType())) {//视图查询数据
            VisualdevReleaseEntity visualdevREntity = JsonUtil.getJsonToBean(visualdevEntity, VisualdevReleaseEntity.class);
            realList = onlineSwapDataUtils.getInterfaceData(visualdevREntity, paginationModelExport, visualJsonModel.getColumnData());
            vo = VisualUtils.createModelExcelApiData(visualdevEntity, realList, Arrays.asList(keys), "表单信息");
        } else {
            realList = visualdevModelDataService.exportData(keys, paginationModelExport, visualJsonModel);
            vo = VisualUtils.createModelExcel(visualdevEntity, realList, Arrays.asList(keys), "表单信息");
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "功能导出" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
	})
    @PostMapping("/{modelId}/Actions/ExportData" )
    //@SaCheckPermission("onlineDev.webDesign")
    public ActionResult exportData(@PathVariable("modelId" ) String modelId) {
        VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        BaseDevModelVO vo = JsonUtil.getJsonToBean(visualdevEntity, BaseDevModelVO.class);
        vo.setModelType(ExportModelTypeEnum.Design.getMessage());
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), visualdevEntity.getFullName(), ModuleTypeEnum.VISUAL_DEV.getTableName());
        return ActionResult.success(downloadVO);
    }

    @Operation(summary = "功能导入" )
    @PostMapping(value = "/Model/Actions/ImportData" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	//@SaCheckPermission("onlineDev.webDesign")
    public ActionResult ImportData(@RequestPart("file" ) MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_DEV.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        BaseDevModelVO vo = JsonUtil.getJsonToBean(fileContent, BaseDevModelVO.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.Design.getMessage())) {
            return ActionResult.fail("请导入对应功能的json文件" );
        }
        VisualdevEntity visualdevEntity = JsonUtil.getJsonToBean(vo, VisualdevEntity.class);
        String modelId = visualdevEntity.getId();
        if (StringUtil.isNotEmpty(modelId)) {
            VisualdevEntity entity = visualdevService.getInfo(modelId);
            if (entity != null) {
                return ActionResult.fail("已存在相同功能" );
            }
        }
        visualdevEntity.setCreatorTime(DateUtil.getNowDate());
        visualdevEntity.setLastModifyTime(null);
        visualdevService.create(visualdevEntity);
        return ActionResult.success(MsgCode.IMP001.get());
    }

    /**
     * 模板下载
     *
     * @return
     */
    @Operation(summary = "模板下载" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
	})
    @GetMapping("/{modelId}/TemplateDownload" )
    public ActionResult<DownloadVO> templateDownload(@PathVariable("modelId" ) String modelId) {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        FormDataModel formDataModel = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> allFieLds = new ArrayList<>();
        VisualUtils.recursionFields(fieLdsModels, allFieLds);
        List<String> selectKey = uploaderTemplateModel.getSelectKey();
        Map<String, Object> dataMap = new HashMap<>();
        //子表
        List<FieLdsModel> childFields = allFieLds.stream().filter(f -> f.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)).collect(Collectors.toList());
        for (FieLdsModel child : childFields) {
            List<String> childList = selectKey.stream().filter(s -> s.startsWith(child.getVModel())).collect(Collectors.toList());
            childList.stream().forEach(c -> c.replace(child.getVModel() + "-" , "" ));
            List<FieLdsModel> children = child.getConfig().getChildren();
            List<Map<String, Object>> childData = new ArrayList<>();
            Map<String, Object> childMap = new HashMap<>();
            for (String cl : childList) {
                String substring = cl.substring(cl.indexOf("-" ) + 1);
                FieLdsModel fieLdsModel = children.stream().filter(c -> c.getVModel().equals(substring)).findFirst().orElse(null);
                childMap.put(substring, VisualUtils.exampleExcelMessage(fieLdsModel));
            }
            childData.add(childMap);
            dataMap.put(child.getVModel(), childData);
        }

        for (String s : selectKey.stream().filter(s -> !s.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX )).collect(Collectors.toList())) {
            FieLdsModel fieLdsModel = allFieLds.stream().filter(c -> c.getVModel().equals(s)).findFirst().orElse(null);
            dataMap.put(s, VisualUtils.exampleExcelMessage(fieLdsModel));
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(dataMap);
        DownloadVO vo = VisualUtils.createModelExcel(visualdevEntity, dataList, selectKey, visualdevEntity.getFullName() + "模板" );
        return ActionResult.success(vo);
    }

    @Operation(summary = "上传文件" )
    @PostMapping("/Uploader" )
	//@SaCheckPermission("onlineDev.webDesign")
    public ActionResult<Object> Uploader() {
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().endsWith(".xlsx" ) || file.getOriginalFilename().endsWith(".xls" )) {
            String filePath = XSSEscape.escape(configValueUtil.getTemporaryFilePath());
            String fileName = XSSEscape.escape(RandomUtil.uuId() + "." + UpUtil.getFileType(file));
            //上传文件
            FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, fileName);
            DownloadVO vo = DownloadVO.builder().build();
            vo.setName(fileInfo.getFilename());
            return ActionResult.success(vo);
        } else {
            return ActionResult.fail("选择文件不符合导入" );
        }
    }

    /**
     * 导入预览
     *
     * @return
     */
    @Operation(summary = "导入预览" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "fileName", description = "文件名"),
	})
    @GetMapping("/{modelId}/ImportPreview" )
    public ActionResult<Map<String, Object>> ImportPreview(@PathVariable("modelId" ) String modelId, String fileName) throws Exception {
		//StpUtil.checkPermission(modelId);

		Map<String, Object> previewMap = null;
        try {
            VisualdevReleaseEntity entity = visualdevReleaseService.getById(modelId);
            ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
            FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
            UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
            List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
            List<FieLdsModel> allFields = new ArrayList<>();
            OnlinePublicUtils.recursionFormFields(allFields, fieLdsModels);

            List<String> selectKey = uploaderTemplateModel.getSelectKey();

            //子表tableField
            Set<String> tablefield1 = selectKey.stream().filter(s -> s.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX )).map(s -> s.substring(0, s.indexOf("-" ))).collect(Collectors.toSet());

            String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemporaryFilePath();
            FileUploadUtils.downLocal(configValueUtil.getTemporaryFilePath(), filePath, fileName);
            File temporary = new File(XSSEscape.escapePath(filePath + fileName));
            //判断有无子表
            String tablefield = selectKey.stream().filter(s -> s.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX )).findFirst().orElse(null);
            //有子表需要取第二行的表头
            Integer i = tablefield != null ? 2 : 1;
            //读取excel中数据
            List<Map> excelDataList = ExcelUtil.importExcel(temporary, 0, i, Map.class);
            //todo 备用方案，读取不到时间暂用此方法
            ExcelUtil.imoportExcelToMap(temporary, i, excelDataList);
            //取出导出选项中的子表字段label
            Map<String, Object> valueMap = new HashMap<>();
            for (FieLdsModel fieLdsModel : allFields) {
                String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
                //子表
                if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)) {
                    List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                    List<FieLdsModel> collect = children.stream().filter(c -> StringUtil.isNotEmpty(c.getVModel())).collect(Collectors.toList());
                    valueMap.put(fieLdsModel.getVModel(), collect.stream().collect(Collectors.toMap(FieLdsModel::getVModel, c -> c.getConfig().getLabel())));
                } else {
                    valueMap.put(fieLdsModel.getVModel(), fieLdsModel.getConfig().getLabel());
                }
            }
            //列表字段
            List<Map<String, Object>> columns = new ArrayList<>();
            List<ImportExcelFieldModel> chiImList = new ArrayList<>();
            List<ImportExcelFieldModel> allImList = new ArrayList<>();
            selectKey.stream().forEach(s -> {
                ImportExcelFieldModel importExcel = new ImportExcelFieldModel();
                if (s.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX )) {
                    String table = s.substring(0, s.indexOf("-" ));
                    String field = s.substring(s.indexOf("-" ) + 1);
                    importExcel.setField(field);
                    importExcel.setTableField(table);
                    Map map = (Map) valueMap.get(table);
                    importExcel.setFullName(map.get(field).toString());
                    chiImList.add(importExcel);
                } else {
                    importExcel.setField(s);
                    importExcel.setFullName(valueMap.get(s).toString());
                    allImList.add(importExcel);
                }
            });
            Map<String, List<ImportExcelFieldModel>> groups = chiImList.stream().collect(Collectors.groupingBy(ImportExcelFieldModel::getTableField));

            for (Map.Entry<String, List<ImportExcelFieldModel>> entry : groups.entrySet()) {
                ImportExcelFieldModel importExcel = new ImportExcelFieldModel();

                List<ImportExcelFieldModel> value = entry.getValue();
                ImportExcelFieldModel im = value.get(0);
                FieLdsModel fieLdsModel = allFields.stream().filter(f -> entry.getKey().equals(f.getVModel())).findFirst().orElse(null);
                String tableName = fieLdsModel.getConfig().getLabel();
                importExcel.setField(entry.getKey());
                importExcel.setFullName(tableName);
                //            value.stream().forEach(im1->im1.setFullName(im1.getFullName().replace(tableName+"-","")));
                importExcel.setChildren(value);
                allImList.add(importExcel);
            }

            for (ImportExcelFieldModel importExcel : allImList) {
                Map<String, Object> selectMap = new HashMap<>(16);
                selectMap.put("id" , importExcel.getField());
                selectMap.put("fullName" , importExcel.getFullName());
                if (importExcel.getChildren() != null) {
                    List<ImportExcelFieldModel> children = importExcel.getChildren();
                    List<Map<String, Object>> childMapList = new ArrayList<>();
                    for (ImportExcelFieldModel childIm : children) {
                        Map<String, Object> childMap = new HashMap<>(16);
                        childMap.put("id" , childIm.getField());
                        childMap.put("fullName" , childIm.getFullName());
                        childMapList.add(childMap);
                    }
                    selectMap.put("children" , childMapList);
                }
                columns.add(selectMap);
            }
            List<Map<String, Object>> allDataList = new ArrayList<>();

            for (int z = 0; z < excelDataList.size(); z++) {
                Map<String, Object> dataMap = new HashMap<>(16);
                Map m = excelDataList.get(z);
                List results = new ArrayList<>(m.entrySet());
                //取出的数据最后一行 不带行标签
                int resultsize = z == excelDataList.size() - 1 ? results.size() : m.containsKey("excelRowNum" ) ? results.size() - 1 : results.size();
                if (resultsize < selectKey.size()) {
                    throw new WorkFlowException(MsgCode.VS407.get());
                }
                for (int e = 0; e < resultsize; e++) {
                    Map.Entry o = (Map.Entry) results.get(e);
                    String entryKey = o.getKey().toString();
                    String substring = entryKey.substring(entryKey.indexOf("(" ) + 1, entryKey.indexOf(")" ));
                    boolean contains = selectKey.contains(substring);
                    if (!contains) {
                        throw new WorkFlowException(MsgCode.VS407.get());
                    }
                    dataMap.put(substring, o.getValue());
                }
                allDataList.add(dataMap);
            }

            //存放在主表数据的下标位置
            List<Map<String, List<Map<String, Object>>>> IndexMap = new ArrayList<>();
//			Map<Integer, Map<String, List<Map<String, Object>>>> IndexMap = new TreeMap<>();
            Map<String, List<Map<String, Object>>> childrenTabMap = new HashMap<>();
            for (String tab : tablefield1) {
                childrenTabMap.put(tab, new ArrayList<>());
            }

            List<Map<String, Object>> results = new ArrayList<>();
            for (int t = 0; t < allDataList.size(); t++) {
                boolean isLast = t == allDataList.size() - 1;
                //是否是上条数据的子表
                boolean isTogetherWithUp = false;
                //是否需要合并
                boolean needTogether = true;
                //这条数据是否需要添加
                boolean needAdd = true;
                Map<String, Object> dataMap = allDataList.get(t);
                //首条数据不合并
                if (t > 0) {
                    List<Map.Entry<String, Object>> tablefield2 = dataMap.entrySet().stream().filter(e -> !e.getKey().toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
                    //如果除子表外都为空 则需要合并
                    Map.Entry<String, Object> entry = tablefield2.stream().filter(ta -> ta.getValue() != null).findFirst().orElse(null);
                    if (entry == null) {
                        isTogetherWithUp = true;
                        needTogether = false;
                        needAdd = false;
                        if (isLast) {
                            needTogether = true;
                        }
                    }
                }

                //合并子表里的字段
                for (String tab : tablefield1) {
                    Map<String, Object> childObjMap = new HashMap<>(16);
                    //该条数据下的子表字段
                    List<Map.Entry<String, Object>> childList = dataMap.entrySet().stream().filter(e -> e.getKey().startsWith(tab)).collect(Collectors.toList());
                    for (Map.Entry<String, Object> entry : childList) {
                        String childFieldName = entry.getKey().replace(tab + "-" , "" );
                        childObjMap.put(childFieldName, entry.getValue());
                    }
                    List<Map<String, Object>> mapList = childrenTabMap.get(tab);
                    mapList.add(childObjMap);
                }
                if (needTogether && t != 0) {
                    Map<String, List<Map<String, Object>>> c = new HashMap<>(childrenTabMap);
                    Map<String, List<Map<String, Object>>> b = new HashMap<>();

                    for (String tab : tablefield1) {
                        //去掉最后一个 放到下条数据里
                        List<Map<String, Object>> mapList = c.get(tab);
                        Map<String, Object> map = mapList.get(mapList.size() - 1);
                        List<Map<String, Object>> aList = new ArrayList<>();
                        aList.add(map);
                        if (!isLast) {
                            mapList.remove(mapList.size() - 1);
                        }
                        childrenTabMap.put(tab, aList);
                        b.put(tab, mapList);
                    }
                    IndexMap.add(b);
                    if (isLast) {
                        IndexMap.add(childrenTabMap);
                    }
                } else {
                    if (isLast) {
                        IndexMap.add(childrenTabMap);
                    }
                }
                if (needAdd) {
                    Map<String, Object> m = new HashMap<>();
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        if (!entry.getKey().contains("-" )) {
                            m.put(entry.getKey(), entry.getValue());
                        }
                    }
                    results.add(m);
                }
            }

            //处理结果
            for (int r = 0; r < results.size(); r++) {
                Map<String, List<Map<String, Object>>> entry = IndexMap.get(r);
                Map<String, Object> map = results.get(r);
                for (Map.Entry<String, List<Map<String, Object>>> entry1 : entry.entrySet()) {
                    String tableField = entry1.getKey();
                    Object tableField1 = map.get(tableField);
                    List<Map<String, Object>> value1 = entry1.getValue();
                    if (tableField1 != null) {
                        List<Map<String, Object>> tfMap = (List<Map<String, Object>>) tableField1;
                        value1.addAll(tfMap);
                    }
                    map.put(tableField, value1);
                }
                results.set(r, map);
            }

            previewMap = new HashMap<>();
            previewMap.put("dataRow" , results);
            previewMap.put("headerRow" , columns);
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.fail(MsgCode.VS407.get());
        }
        return ActionResult.success(previewMap);
    }

    /**
     * 导出异常报告
     *
     * @return
     */
    @Operation(summary = "导出异常报告" )
	@Parameters({
			@Parameter(name = "modelId", description = "模板id"),
			@Parameter(name = "visualImportModel", description = "导出参数"),
	})
    @PostMapping("/{modelId}/ImportExceptionData" )
    public ActionResult<DownloadVO> ImportExceptionData(@PathVariable("modelId" ) String modelId, @RequestBody VisualImportModel visualImportModel) {
		//StpUtil.checkPermission(modelId);

		VisualdevEntity visualdevEntity = visualdevService.getReleaseInfo(modelId);
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        List<String> selectKey = uploaderTemplateModel.getSelectKey();
        DownloadVO vo = VisualUtils.createModelExcel(visualdevEntity, visualImportModel.getList(), selectKey, "错误报告" );
        return ActionResult.success(vo);
    }
}
