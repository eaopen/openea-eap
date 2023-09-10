package org.openea.eap.extj.controller.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.entity.OperatorRecordEntity;
import org.openea.eap.extj.base.entity.PrintDevEntity;
import org.openea.eap.extj.base.model.PaginationPrint;
import org.openea.eap.extj.base.model.PrintTableTreeModel;
import org.openea.eap.extj.base.model.dto.PrintDevFormDTO;
import org.openea.eap.extj.base.model.print.PrintOption;
import org.openea.eap.extj.base.model.query.PrintDevDataQuery;
import org.openea.eap.extj.base.model.query.PrintDevFieldsQuery;
import org.openea.eap.extj.base.model.vo.PrintDevListVO;
import org.openea.eap.extj.base.model.vo.PrintDevVO;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.service.IPrintDevService;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.openea.eap.extj.util.treeutil.SumTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 打印模板 -控制器
 *
 * 
 */
@Tag(name = "打印模板", description = "print")
@RestController
@RequestMapping("/api/system/printDev")
public class PrintDevController extends SuperController<IPrintDevService, PrintDevEntity> {

    @Autowired
    private IPrintDevService iPrintDevService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private UserService userService;

    /**
     * 查询打印列表
     * @param ids
     * @return
     */
    @Operation(summary = "查询打印列表")
    @Parameters({
            @Parameter(name = "ids", description = "主键集合")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("getListById")
    public List<PrintOption> getListById(@RequestBody  List<String> ids) {


        return iPrintDevService.getPrintTemplateOptions(ids);
    }

    @Operation(summary = "查询打印列表")
    @Parameters({
            @Parameter(name = "data", description = "打印模板-数查询对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("getListOptions")
    public ActionResult getListOptions(@RequestBody PrintDevDataQuery data) {
        List<String> ids = data.getIds();
        QueryWrapper<PrintDevEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(PrintDevEntity::getId,ids);
        wrapper.lambda().eq(PrintDevEntity::getEnabledMark,1);
        List<PrintDevEntity> list = iPrintDevService.getBaseMapper().selectList(wrapper);
        List<PrintOption> options = JsonUtil.getJsonToList(list, PrintOption.class);
        return  ActionResult.success(options);
    }



    /*============增删改==============*/

    /**
     * 新增打印模板对象
     *
     * @param printDevForm 打印模板数据传输对象
     * @return 执行结果标识
     */
    @Operation(summary = "新增")
    @Parameters({
            @Parameter(name = "printDevForm", description = "打印模板数据传输对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping
    public ActionResult<PrintDevFormDTO> create(@RequestBody @Valid PrintDevFormDTO printDevForm) {
        PrintDevEntity printDevEntity = JsonUtil.getJsonToBean(printDevForm, PrintDevEntity.class);
        // 校验
        try{
            iPrintDevService.creUpdateCheck(printDevEntity, true, true);
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
        printDevEntity.setId(RandomUtil.uuId());
        iPrintDevService.save(printDevEntity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    @Operation(summary = "Sql数据获取")
    @GetMapping("/BatchData")
    public ActionResult getBatchData(PrintDevDataQuery printDevSqlDataQuery) {
        String id = XSSEscape.escape(printDevSqlDataQuery.getId());
        String formId = XSSEscape.escape(printDevSqlDataQuery.getFormId());
        PrintDevEntity entity = iPrintDevService.getById(id);
        if(entity == null){
            return ActionResult.fail(MsgCode.PRI001.get());
        }

        ArrayList<Object> list = new ArrayList<>();
        List<String> record = Arrays.asList(formId.split(","));
        record.forEach(rid->{
            list.add(iPrintDevService.getDataMap(entity,rid));
        });
        return ActionResult.success(list);
    }

    /**
     * 删除打印模板
     *
     * @param id           打印模板id
     * @param printDevForm 打印模板数据传输对象
     * @return 执行结果标识
     */
    @Operation(summary = "编辑")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id"),
            @Parameter(name = "printDevForm", description = "打印模板数据传输对象")
    })
    @SaCheckPermission("system.printDev")
    @PutMapping("/{id}")
    public ActionResult<PrintDevFormDTO> update(@PathVariable String id, @RequestBody @Valid PrintDevFormDTO printDevForm) {
        PrintDevEntity printDevEntity = JsonUtil.getJsonToBean(printDevForm, PrintDevEntity.class);
        PrintDevEntity originEntity = iPrintDevService.getById(id);
        try{
            iPrintDevService.creUpdateCheck(printDevEntity,
                    !originEntity.getFullName().equals(printDevForm.getFullName()),
                    !originEntity.getEnCode().equals(printDevForm.getEnCode()));
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
        printDevEntity.setId(id);
        iPrintDevService.updateById(printDevEntity);
        return ActionResult.success(MsgCode.SU004.get());
    }


    /**
     * 复制打印模板
     *
     * @param id 打印模板id
     * @return 执行结果标识
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult<PageListVO<PrintDevEntity>> copy(@PathVariable String id) {
        int randomNum = (int)(Math.random() * 100000);
        PrintDevEntity entity = iPrintDevService.getById(id);
        entity.setFullName(entity.getFullName() + ".副本" + randomNum);
        if(entity.getFullName().length() > 50){
            return ActionResult.fail(MsgCode.PRI006.get());
        }
        entity.setEnCode(entity.getEnCode() + "." + randomNum);
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(0);
        entity.setLastModifyTime(null);
        entity.setLastModifyUserId(null);
        entity.setId(RandomUtil.uuId());
        PrintDevEntity entityBean = JsonUtil.getJsonToBean(entity, PrintDevEntity.class);
        iPrintDevService.save(entityBean);
        return ActionResult.success(MsgCode.SU007.get());
    }

    /**
     * 删除打印模板
     *
     * @param id 打印模板id
     * @return ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @DeleteMapping("/{id}")
    public ActionResult<PrintDevFormDTO> delete(@PathVariable String id) {
        //对象存在判断
        if (iPrintDevService.getById(id) != null) {
            iPrintDevService.removeById(id);
            return ActionResult.success(MsgCode.SU003.get());
        } else {
            return ActionResult.fail(MsgCode.FA003.get());
        }
    }

    /**
     * 修改打印模板可用状态
     *
     * @param id 打印模板id
     * @return 执行结果标识
     */
    @Operation(summary = "修改状态")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @PutMapping("/{id}/Actions/State")
    public ActionResult<PageListVO<PrintDevEntity>> state(@PathVariable String id) {
        PrintDevEntity entity = iPrintDevService.getById(id);
        if (entity != null) {
            if ("1".equals(entity.getEnabledMark().toString())) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            iPrintDevService.updateById(entity);
            return ActionResult.success(MsgCode.SU004.get());
        }
        return ActionResult.fail(MsgCode.FA002.get());
    }

    /*============查询==============*/

    /**
     * 查看单个模板详情
     *
     * @param id 打印模板id
     * @return 单个模板对象
     */
    @Operation(summary = "预览")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @GetMapping("/{id}")
    public ActionResult<PrintDevEntity> info(@PathVariable String id) {
        return ActionResult.success(iPrintDevService.getById(id));
    }

    /**
     * 查看所有模板
     *
     * @return 所有模板集合
     */
    @Operation(summary = "列表")
    @SaCheckPermission("system.printDev")
    @GetMapping
    public ActionResult list(PaginationPrint paginationPrint) {
        List<PrintDevEntity> list = iPrintDevService.getList(paginationPrint);
        List<String> userId = list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        List<String> lastUserId = list.stream().map(t -> t.getLastModifyUserId()).collect(Collectors.toList());
        lastUserId.removeAll(Collections.singleton(null));
        List<UserEntity> userEntities = userService.getUserName(userId);
        List<UserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfoByEnCode("printDev");
        List<DictionaryDataEntity> typeList = dictionaryDataService.getList(typeEntity.getId());
        List<PrintDevListVO> listVOS = new ArrayList<>();
        for (PrintDevEntity entity : list) {
            PrintDevListVO vo = JsonUtil.getJsonToBean(entity, PrintDevListVO.class);
            DictionaryDataEntity dataEntity = typeList.stream().filter(t -> t.getEnCode().equals(entity.getCategory())).findFirst().orElse(null);
            if (dataEntity != null) {
                vo.setCategory(dataEntity.getFullName());
            } else {
                vo.setCategory("");
            }
            //创建者
            UserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(entity.getCreatorUserId())).findFirst().orElse(null);
            vo.setCreatorUser(creatorUser != null ? creatorUser.getRealName() + "/" + creatorUser.getAccount() : entity.getCreatorUserId());
            //修改人
            UserEntity lastModifyUser = lastUserIdEntities.stream().filter(t -> t.getId().equals(entity.getLastModifyUserId())).findFirst().orElse(null);
            vo.setLastModifyUser(lastModifyUser != null ? lastModifyUser.getRealName() + "/" + lastModifyUser.getAccount() : entity.getLastModifyUserId());
            listVOS.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationPrint, PaginationVO.class);
        return ActionResult.page(listVOS , paginationVO);
    }

    /**
     * 下拉列表
     *
     * @return 返回列表数据
     */
    @Operation(summary = "下拉列表")
    @GetMapping("/Selector")
    public ActionResult<ListVO<PrintDevVO>> selectorList(Integer type){
        ListVO<PrintDevVO> vo = new ListVO<>();
        try{
            vo.setList(iPrintDevService.getTreeModel(type));
            return ActionResult.success(vo);
        }catch (Exception e){
            return ActionResult.fail(e.getMessage());
        }
    }

    /**
     * 根据sql获取内容
     * @param printDevSqlDataQuery 打印模板查询对象
     * @return 打印模板对应内容
     */
    @Operation(summary = "Sql数据获取")
    @SaCheckPermission("system.printDev")
    @GetMapping("/Data")
    public ActionResult<Map<String, Object>> getFieldData(PrintDevDataQuery printDevSqlDataQuery) {
        String id = XSSEscape.escape(printDevSqlDataQuery.getId());
        String formId = XSSEscape.escape(printDevSqlDataQuery.getFormId());
        PrintDevEntity entity = iPrintDevService.getById(id);
        if(entity == null){
            return ActionResult.fail(MsgCode.PRI001.get());
        }
        Map<String, Object> printDataMap;
        try {
            printDataMap = iPrintDevService.getDataBySql(
                    entity.getDbLinkId(),
                    entity.getSqlTemplate().replaceAll("@formId", "'" + formId + "'"));
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.fail(e.getMessage());
        }
        List<Map<String, Object>> headTableList = (List<Map<String, Object>>) printDataMap.get("headTable");
        printDataMap.remove("headTable");
        for (Map map : headTableList) {
            printDataMap.putAll(map);
        }
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put("printData", printDataMap);
        dataMap.put("printTemplate", entity.getPrintTemplate());
        List<OperatorRecordEntity> operatorRecordList = iPrintDevService.getFlowTaskOperatorRecordList(formId);
        dataMap.put("operatorRecordList", operatorRecordList);
        return ActionResult.success(dataMap);
    }

    /**
     * 获取打印模块字段信息
     *
     * @param printDevFieldsQuery 打印模板查询对象
     * @return 字段信息数据
     */
    @Operation(summary = "Sql字段获取")
    @Parameters({
            @Parameter(name = "printDevFieldsQuery", description = "打印模板查询对象")
    })
    @SaCheckPermission("system.printDev")
    @PostMapping("/Fields")
    public ActionResult<List<SumTree<PrintTableTreeModel>>> getFields(@RequestBody PrintDevFieldsQuery printDevFieldsQuery) {
        String dbLinkId = XSSEscape.escape(printDevFieldsQuery.getDbLinkId());
        try {
            List<SumTree<PrintTableTreeModel>> printTableFields = iPrintDevService.getPintTabFieldStruct(dbLinkId,
                    printDevFieldsQuery.getSqlTemplate().replaceAll("@formId", " null "));

            return ActionResult.success(printTableFields);
        } catch (Exception e) {
            return ActionResult.fail(e.getMessage());
        }
    }

    /*==========行为============*/

    /**
     * 导出打印模板备份json
     *
     * @param id 打印模板id
     */
    @Operation(summary = "导出")
    @Parameters({
            @Parameter(name = "id", description = "打印模板id")
    })
    @SaCheckPermission("system.printDev")
    @GetMapping("/{id}/Actions/Export")
    public ActionResult<DownloadVO> export(@PathVariable String id) {
        PrintDevEntity entity = iPrintDevService.getById(id);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.SYSTEM_PRINT.getTableName());
        return ActionResult.success(downloadVO);
    }

    /**
     * 导入打印模板备份json
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @SaCheckPermission("system.printDev")
    @PostMapping(value = "/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult<PageListVO<PrintDevEntity>> importData(@RequestPart("file") MultipartFile multipartFile) throws DataException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.SYSTEM_PRINT.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //读取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        try {
            PrintDevEntity entity = JsonUtil.getJsonToBean(fileContent, PrintDevEntity.class);
            //id为空切名称不存在时
            if (iPrintDevService.getById(entity.getId()) == null &&
                    !iPrintDevService.checkNameExist(entity.getFullName(), entity.getId())) {
                iPrintDevService.saveOrUpdateIgnoreLogic(entity);
                return ActionResult.success(MsgCode.IMP001.get());
            }
        } catch (Exception e) {
            throw new DataException(MsgCode.IMP004.get());
        }
        return ActionResult.fail(MsgCode.IMP003.get());
    }

}
