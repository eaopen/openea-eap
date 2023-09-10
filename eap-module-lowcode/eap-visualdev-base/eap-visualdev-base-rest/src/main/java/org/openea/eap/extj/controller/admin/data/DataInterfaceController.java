package org.openea.eap.extj.controller.admin.data;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.ActionResultCode;
import org.openea.eap.extj.base.NoDataSourceBind;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.DataInterfaceEntity;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.InterfaceOauthEntity;
import org.openea.eap.extj.base.model.dataInterface.*;
import org.openea.eap.extj.base.service.DataInterfaceService;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.service.InterfaceOauthService;
import org.openea.eap.extj.base.util.dbutil.TableUtil;
import org.openea.eap.extj.base.util.interfaceUtil.InterfaceUtil;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.util.enums.DictionaryDataEnum;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.DbSensitiveConstant;
import org.openea.eap.extj.util.file.FileExport;
import org.openea.eap.extj.util.treeutil.SumTree;
import org.openea.eap.extj.util.treeutil.newtreeutil.TreeDotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@Tag(name = "数据接口", description = "DataInterface")
@RestController
@RequestMapping(value = "/api/system/DataInterface")
public class DataInterfaceController extends SuperController<DataInterfaceService, DataInterfaceEntity> {
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private EapUserProvider userProvider;

    /**
     * 获取接口列表(分页)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取接口列表(分页)")
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping
    public ActionResult<PageListVO<DataInterfaceListVO>> getList(PaginationDataInterface pagination) {
        List<DataInterfaceEntity> data = dataInterfaceService.getList(pagination, null, false);
//        List<UserEntity> userList = userService.getUserName(data.stream().map(t -> t.getCreatorUser()).collect(Collectors.toList()));
//        for (DataInterfaceEntity entity : data) {
//            UserEntity userAllVO = userList.stream().filter(t -> t.getId().equals(entity.getCreatorUser())).findFirst().orElse(null);
//            entity.setCreatorUser(userAllVO != null ? userAllVO.getRealName() + "/" + userAllVO.getAccount() : "");
//        }
        List<DataInterfaceListVO> list = JsonUtil.getJsonToList(data, DataInterfaceListVO.class);
        // 添加tenantId字段
        for (DataInterfaceListVO vo : list) {
            if (StringUtil.isNotEmpty(userProvider.get().getTenantId())) {
                vo.setTenantId(userProvider.get().getTenantId());
            }
            // 类别转换
            if ("1".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("增加");
            } else if ("2".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("修改");
            } else if ("3".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("查询");
            } else if ("4".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("删除");
            } else if ("5".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("存储");
            } else if ("6".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("GET");
            } else if ("7".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("POST");
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 获取接口列表(工作流选择时调用)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取接口列表(工作流选择时调用)")
    @GetMapping("/getList")
    public ActionResult<PageListVO<DataInterfaceGetListVO>> getLists(PaginationDataInterfaceModel pagination) {
        List<DataInterfaceEntity> data = dataInterfaceService.getList(pagination, pagination.getDataType(), true);
        List<DataInterfaceGetListVO> list = JsonUtil.getJsonToList(data, DataInterfaceGetListVO.class);
        for (DataInterfaceGetListVO vo : list) {
            // 类别转换
            if ("1".equals(vo.getDataType())) {
                vo.setDataType("SQL操作");
            } else if ("2".equals(vo.getDataType())) {
                vo.setDataType("静态数据");
            } else if ("3".equals(vo.getDataType())) {
                vo.setDataType("API操作");
            }
            // 类别转换
            if ("1".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("增加");
            } else if ("2".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("修改");
            } else if ("3".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("查询");
            } else if ("4".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("删除");
            } else if ("5".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("存储");
            } else if ("6".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("GET");
            } else if ("7".equals(vo.getRequestMethod())) {
                vo.setRequestMethod("POST");
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 获取接口参数列表下拉框
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取接口参数列表下拉框")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/GetParam/{id}")
    public ActionResult getSelector(@PathVariable("id") String id) {
        DataInterfaceEntity info = dataInterfaceService.getInfo(id);
        if (info!=null) {
            String requestParameters = info.getRequestParameters();
            List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(requestParameters, DataInterfaceModel.class);
            return ActionResult.success(jsonToList == null ? new ArrayList<>() : jsonToList);
        }
        return ActionResult.fail("数据不存在");
    }

    /**
     * 获取接口列表下拉框
     *
     * @return ignore
     */
    @Operation(summary = "获取接口列表下拉框")
    @GetMapping("/Selector")
    public ActionResult<List<DataInterfaceTreeVO>> getSelector() {
        List<DataInterfaceTreeModel> tree = new ArrayList<>();
        List<DataInterfaceEntity> data = dataInterfaceService.getList(true);
        List<DictionaryDataEntity> dataEntityList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.SYSTEM_DATAINTERFACE.getDictionaryTypeId()).getId());
        // 获取数据接口外层菜单
        for (DictionaryDataEntity dictionaryDataEntity : dataEntityList) {
            DataInterfaceTreeModel firstModel = JsonUtil.getJsonToBean(dictionaryDataEntity, DataInterfaceTreeModel.class);
            firstModel.setId(dictionaryDataEntity.getId());
            firstModel.setCategoryId("0");
            long num = data.stream().filter(t -> t.getCategoryId().equals(dictionaryDataEntity.getId())).count();
            if (num > 0) {
                tree.add(firstModel);
            }
        }
        for (DataInterfaceEntity entity : data) {
            DataInterfaceTreeModel treeModel = JsonUtil.getJsonToBean(entity, DataInterfaceTreeModel.class);
            treeModel.setCategoryId("1");
            treeModel.setParentId(entity.getCategoryId());
            treeModel.setId(entity.getId());
            DictionaryDataEntity dataEntity = dictionaryDataService.getInfo(entity.getCategoryId());
            if (dataEntity != null) {
                tree.add(treeModel);
            }
        }
        List<SumTree<DataInterfaceTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(tree);
        List<DataInterfaceTreeVO> list = JsonUtil.getJsonToList(sumTrees, DataInterfaceTreeVO.class);
        ListVO<DataInterfaceTreeVO> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(list);
    }

    /**
     * 获取接口数据
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取接口数据")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/{id}")
    public ActionResult<DataInterfaceVo> getInfo(@PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        DataInterfaceVo vo = JsonUtilEx.getJsonToBeanEx(entity, DataInterfaceVo.class);
        if (vo.getCheckType() == null) {
            vo.setCheckType(0);
        }
        return ActionResult.success(vo);
    }

    /**
     * 添加接口
     *
     * @param dataInterfaceCrForm 添加数据接口模型
     * @return ignore
     */
    @Operation(summary = "添加接口")
    @Parameter(name = "dataInterfaceCrForm", description = "实体模型", required = true)
    //@SaCheckPermission("systemData.dataInterface")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DataInterfaceCrForm dataInterfaceCrForm) {
        DataInterfaceEntity entity = JsonUtil.getJsonToBean(dataInterfaceCrForm, DataInterfaceEntity.class);
        // 判断是否有敏感字
        if (entity.getDataType() == 1 && "3".equals(entity.getRequestMethod())) {
            String containsSensitive = containsSensitive(dataInterfaceCrForm.getQuery());
            if (StringUtil.isNotEmpty(containsSensitive)) {
                return ActionResult.fail("当前查询SQL包含敏感字：" + containsSensitive);
            }
        }
        if (StringUtil.isEmpty(entity.getQuerySql())) {
            entity.setQuerySql(" ");
        }
        if (dataInterfaceService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        if (dataInterfaceService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST002.get());
        }
        dataInterfaceService.create(entity);
        return ActionResult.success("接口创建成功");
    }

    /**
     * 判断是否有敏感字
     *
     * @param sql
     * @return
     */
    private String containsSensitive(String sql) {
        if (StringUtil.isNotEmpty(sql)) {
            String[] split = DbSensitiveConstant.SENSITIVE.split(",");
            for (String str : split) {
                str = str.trim();
                String[] matchStr = new String[]{str + " ", str.trim() + "-"};
                for (String s : matchStr) {
                    boolean contains = sql.toUpperCase().contains(s);
                    if (contains) {
                        return str.trim();
                    }
                }
            }
        }
        return "";
    }

    /**
     * 修改接口
     *
     * @param dataInterfaceUpForm 修改数据接口模型
     * @param id                  主键
     * @return ignore
     */
    @Operation(summary = "修改接口")
    @Parameters({
            @Parameter(name = "dataInterfaceUpForm", description = "实体模型", required = true),
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid DataInterfaceUpForm dataInterfaceUpForm, @PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = JsonUtilEx.getJsonToBeanEx(dataInterfaceUpForm, DataInterfaceEntity.class);
        // 判断是否有敏感字
        if (entity.getDataType() == 1 && "3".equals(entity.getRequestMethod())) {
            String containsSensitive = containsSensitive(dataInterfaceUpForm.getQuery());
            if (StringUtil.isNotEmpty(containsSensitive)) {
                return ActionResult.fail("当前查询SQL包含敏感字：" + containsSensitive);
            }
        }
        if (dataInterfaceService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        if (dataInterfaceService.isExistByEnCode(entity.getEnCode(), id)) {
            return ActionResult.fail(MsgCode.EXIST002.get());
        }
        boolean flag = dataInterfaceService.update(entity, id);
        if (flag == false) {
            return ActionResult.fail(MsgCode.FA013.get());
        }
        return ActionResult.success(MsgCode.SU013.get());
    }

    /**
     * 删除接口
     *
     * @param id
     * @return
     */
    @Operation(summary = "删除接口")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            dataInterfaceService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }

    /**
     * 更新接口状态
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "更新接口状态")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            if ("0".equals(String.valueOf(entity.getEnabledMark()))) {
                entity.setEnabledMark(1);
            } else {
                entity.setEnabledMark(0);
            }
            dataInterfaceService.update(entity, id);
            return ActionResult.success(MsgCode.SU014.get());
        }
        return ActionResult.fail(MsgCode.FA014.get());
    }

    /**
     * 获取接口分页数据
     *
     * @param id 主键
     * @param page 分页参数
     * @return
     */
    @Operation(summary = "获取接口分页数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "page", description = "分页参数", required = true)
    })
    @PostMapping("/{id}/Action/List")
    public ActionResult list(@PathVariable("id") String id, @RequestBody DataInterfacePage page) {
        ActionResult result = dataInterfaceService.infoToIdPageList(id, page);
        return result;
    }

//    /**
//     * 获取接口详情数据
//     *
//     * @param id 主键
//     * @param page 分页参数
//     * @return
//     */
//    @ApiOperation("获取接口详情数据")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "主键", required = true)
//    })
//    @GetMapping("/{id}/Action/Info")
//    public ActionResult info(@PathVariable("id") String id, DataInterfacePage page) {
//        Map<String, Object> data = dataInterfaceService.infoToInfo(id, page);
//        return ActionResult.success(data);
//    }

    /**
     * 获取接口详情数据
     *
     * @param id 主键
     * @param page 分页参数
     * @return
     */
    @Operation(summary = "获取接口详情数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "page", description = "分页参数", required = true)
    })
    @PostMapping("/{id}/Action/InfoByIds")
    public ActionResult infoByIds(@PathVariable("id") String id, @RequestBody DataInterfacePage page) {
        List<Map<String, Object>> data = dataInterfaceService.infoToInfo(id, page);
        return ActionResult.success(data);
    }

    /**
     * 预览
     *
     * @param id
     * @return
     */
    @Operation(summary = "预览")
    @GetMapping("/{id}/Preview")
    public ActionResult Actions(@PathVariable("id") String id) {
        String escape = XSSEscape.escape(id);
        Object preview = dataInterfaceService.preview(escape);
        if (preview instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) preview;
            if ("1".equals(jsonObject.get("errorCode"))) {
                return ActionResult.fail(String.valueOf(jsonObject.get("errorMsg")));
            }
        }
        return ActionResult.success(preview);
    }

    /**
     * 测试接口
     *
     * @param id
     * @return
     */
    @Operation(summary = "测试接口")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "objectMap", description = "参数、参数值对象")
    })
    @PostMapping("/{id}/Actions/Preview")
    @NoDataSourceBind
    public ActionResult callPreview(@PathVariable("id") String id, @RequestBody(required = false) Map<String, Object> objectMap) {
        DataInterfaceParamModel model = JsonUtil.getJsonToBean(objectMap, DataInterfaceParamModel.class);
        Map<String, String> map = null;
        String name = null;
        if (model != null) {
            if (configValueUtil.isMultiTenancy()) {
                // 判断是不是从外面直接请求
                if (StringUtil.isNotEmpty(model.getTenantId())) {
                    //切换成租户库
                    try{
                        TenantDataSourceUtil.switchTenant(model.getTenantId());
                    }catch (Exception e){
                        return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                    }
                } else {
                    UserInfo userInfo = EapUserProvider.getUser();
                    if (Objects.isNull(userInfo.getUserId())) {
                        return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                    }
                    DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString(), userInfo.isAssignDataSource());
                }
            }
            if (model.getParamList() != null) {
                map = new HashMap<>(16);
                List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(model.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    map.put(dataInterfaceModel.getField(), dataInterfaceModel.getDefaultValue());
                }
            }
        }
        ActionResult actionResult = dataInterfaceService.infoToId(id, name, map);
        if (model != null && "preview".equals(model.getOrigin())) {
            return actionResult;
        }
        if (actionResult.getCode() == 200) {
            actionResult.setMsg("接口请求成功");
        }
        return actionResult;
    }


//    /**
//     * 访问接口
//     *
//     * @param id
//     * @return
//     */
//    @Operation(summary = "访问接口")
//    @GetMapping("/{id}/Actions/Response")
//    @NoDataSourceBind
//    public ActionResult get(@PathVariable("id") String id, String tenantId) {
//        String name = null;
//        if (configValueUtil.isMultiTenancy()) {
//            // 判断是不是从外面直接请求
//            if (StringUtil.isNotEmpty(tenantId)) {
//                //切换成租户库
//                JSONObject object = HttpUtil.httpRequest(props.getPortUrl() + tenantId, "GET", null);
//                if (object == null || StringUtil.isEmpty(object.getString("data"))) {
//                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
//                }
//                Map<String, Object> resulList = JsonUtil.stringToMap(object.getString("data"));
//                name = resulList.get("java") != null ? String.valueOf(resulList.get("java")) : String.valueOf(resulList.get("dbName"));
//                DataSourceContextHolder.setDatasource(tenantId, name);
//            } else {
//                if (Objects.isNull(userProvider.get())) {
//                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
//                }
//                DataSourceContextHolder.setDatasource(userProvider.get().getTenantId(), userProvider.get().getTenantDbConnectionString());
//            }
//        }
//        return dataInterfaceService.infoToId(id, name, null);
//    }


    /**
     * 访问接口GET
     *
     * @param id
     * @return
     */
    @Operation(summary = "访问接口GET")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @GetMapping("/{id}/Actions/Response")
    @NoDataSourceBind
    public ActionResult getResponse(@PathVariable("id") String id,@RequestParam(required = false) Map<String,String> map) {
        DataInterfaceActionModel entity;
        try{
            entity= dataInterfaceService.checkParams(map);
            entity.setInvokType("GET");
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
        String name = null;
        if (configValueUtil.isMultiTenancy()) {
            // 判断是不是从外面直接请求
            if (StringUtil.isNotEmpty(entity.getTenantId())) {
                //切换成租户库
                try{
                    TenantDataSourceUtil.switchTenant(entity.getTenantId());
                }catch (Exception e){
                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                }
            } else {
                UserInfo userInfo = EapUserProvider.getUser();
                if (Objects.isNull(userInfo.getUserId())) {
                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                }
                DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString(), userInfo.isAssignDataSource());
            }
        }
        return dataInterfaceService.infoToIdNew(id, name, entity);
    }
    /**
     * 访问接口POST
     *
     * @param id
     * @return
     */
    @Operation(summary = "访问接口POST")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @PostMapping("/{id}/Actions/Response")
    @NoDataSourceBind
    public ActionResult postResponse(@PathVariable("id") String id, @RequestBody(required = false) Map<String,String> map) {
        DataInterfaceActionModel entity;
        try{
            entity= dataInterfaceService.checkParams(map);
            entity.setInvokType("POST");
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
        String name = null;
        if (configValueUtil.isMultiTenancy()) {
            // 判断是不是从外面直接请求
            if (StringUtil.isNotEmpty(entity.getTenantId())) {
                //切换成租户库
                try{
                    TenantDataSourceUtil.switchTenant(entity.getTenantId());
                }catch (Exception e){
                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                }
            } else {
                UserInfo userInfo = EapUserProvider.getUser();
                if (Objects.isNull(userInfo.getUserId())) {
                    return ActionResult.fail(ActionResultCode.SessionOverdue.getMessage());
                }
                DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString(), userInfo.isAssignDataSource());
            }
        }
        return dataInterfaceService.infoToIdNew(id, name, entity);
    }

    /**
     * 外部接口获取authorization
     *
     * @param appId
     * @return
     */
    @Operation(summary = "外部接口获取authorization")
    @Parameters({
            @Parameter(name = "appId", description = "应用id", required = true),
            @Parameter(name = "intefaceId", description = "接口id"),
            @Parameter(name = "map", description = "参数、参数值对象")
    })
    @PostMapping("/Actions/GetAuth")
    @NoDataSourceBind
    public ActionResult getAuthorization(@RequestParam("appId") String appId,@RequestParam("intefaceId") String intefaceId, @RequestBody(required = false) Map<String,String> map) {
        InterfaceOauthEntity infoByAppId = interfaceOauthService.getInfoByAppId(appId);
        if(infoByAppId==null){
            return ActionResult.fail("appId参数错误");
        }
        Map<String, String> authorization = InterfaceUtil.getAuthorization(intefaceId,appId,infoByAppId.getAppSecret(), map);
        return ActionResult.success(MsgCode.SU005.get(),authorization);
    }
    @Autowired
    private InterfaceOauthService interfaceOauthService;
    /**
     * 数据接口导出功能
     *
     * @param id 接口id
     */
    @Operation(summary = "导出数据接口数据")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("/{id}/Action/Export")
    public ActionResult exportFile(@PathVariable("id") String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_DATAINTEFASE.getTableName());
        return ActionResult.success(downloadVO);
    }

    /**
     * 数据接口导入功能
     *
     * @param multipartFile
     * @return
     * @throws DataException
     */
    @Operation(summary = "数据接口导入功能")
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping(value = "/Action/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult importFile(@RequestPart("file") MultipartFile multipartFile) throws DataException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_DATAINTEFASE.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //读取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        try {
            DataInterfaceEntity entity = JsonUtil.getJsonToBean(fileContent, DataInterfaceEntity.class);
            //id为空切名称不存在时
            if (dictionaryDataService.getInfo(entity.getCategoryId()) == null) {
                return ActionResult.fail(MsgCode.IMP004.get());
            }
            if (dataInterfaceService.getInfo(entity.getId()) == null &&
                    !dataInterfaceService.isExistByFullName(entity.getFullName(), entity.getId()) &&
                    !dataInterfaceService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
                dataInterfaceService.create(entity);
                return ActionResult.success(MsgCode.IMP001.get());
            }

        } catch (Exception e) {
            throw new DataException(MsgCode.IMP004.get());
        }
        return ActionResult.fail(MsgCode.IMP003.get());
    }

    /**
     * 获取接口字段
     *
     * @param id 主键
     * @param objectMap 参数、参数值
     * @return
     */
    @Operation(summary = "获取接口字段")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "objectMap", description = "参数、参数值")
    })
    @SaCheckPermission(value = {"systemData.interfaceOauth", "systemData.dataInterface"}, mode = SaMode.OR)
    @PostMapping("/{id}/Actions/GetFields")
    public ActionResult getFields(@PathVariable("id") String id, @RequestBody(required = false) Map<String, Object> objectMap) {
//        DataInterfaceParamModel model = JsonUtil.getJsonToBean(objectMap, DataInterfaceParamModel.class);
//        Map<String, String> map = null;
//        if (model != null) {
//            if (model.getParamList() != null) {
//                map = new HashMap<>(16);
//                List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(model.getParamList(), DataInterfaceModel.class);
//                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
//                    map.put(dataInterfaceModel.getField(), dataInterfaceModel.getDefaultValue());
//                }
//            }
//        }
        DataInterfacePage model = JsonUtil.getJsonToBean(objectMap, DataInterfacePage.class);
        ActionResult actionResult = dataInterfaceService.infoToIdPageList(id, model);
//        ActionResult actionResult = dataInterfaceService.infoToId(id, null, map);
        if (actionResult.getCode() == 200) {
            try{
                Object data = actionResult.getData();
                if (data instanceof List) {
                    List<Map<String,Object>> list=(List)data;
                    List<String> listKey=new ArrayList();
                    for(String key:list.get(0).keySet()){
                        listKey.add(key);
                    }
                    actionResult.setData(listKey);
                }else{
                    Map<String,Object> map=JsonUtil.entityToMap(data);
                    List<Map<String,Object>> list=(List)map.get("list");
                    List<String> listKey=new ArrayList();
                    for(String key:list.get(0).keySet()){
                        listKey.add(key);
                    }
                    actionResult.setData(listKey);
                }
            }catch (Exception e){
                return ActionResult.fail("接口不符合规范！");
            }
        }
        return actionResult;
    }

    /**
     * 复制数据接口
     *
     * @param id 数据接口ID
     * @return 执行结构
     * @throws DataException ignore
     */
    @Operation(summary = "复制数据接口")
    @Parameters({
            @Parameter(name = "id", description = "数据接口ID", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult<?> Copy(@PathVariable("id") String id) throws DataException {
        String randomCode = TableUtil.getStringRandom(5);
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        entity.setFullName(entity.getFullName() + ".副本" + randomCode);
        if (entity.getFullName().length() > 50) {
            return ActionResult.fail(MsgCode.COPY001.get());
        }
        entity.setEnCode(entity.getEnCode() + "." + randomCode);
        entity.setEnabledMark(0);
        entity.setId(null);
        entity.setLastModifyTime(null);
        entity.setLastModifyUser(null);
        entity.setId(RandomUtil.uuId());
        dataInterfaceService.create(entity);
        return ActionResult.success(MsgCode.SU007.get());
    }

}