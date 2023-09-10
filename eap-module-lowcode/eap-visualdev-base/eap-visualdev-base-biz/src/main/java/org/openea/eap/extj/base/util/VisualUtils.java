package org.openea.eap.extj.base.util;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.util.StrUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.model.dbtable.JdbcTableModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.model.visualJson.ColumnListField;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.context.SpringContext;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.ExportSelectedModel;
import org.openea.eap.extj.extend.util.ExcelUtil;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 可视化工具类
 *
 */
@Slf4j
public class VisualUtils {


    private static DataSourceUtil dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);
    private static ConfigValueUtil configValueUtil = SpringContext.getBean(ConfigValueUtil.class);
    private static EapUserProvider userProvider = SpringContext.getBean(EapUserProvider.class);
    private static FlowFormDataUtil flowFormDataUtil = SpringContext.getBean(FlowFormDataUtil.class);


    /**
     * 去除多级嵌套控件
     *
     * @return
     */
    public static List<FieLdsModel> deleteMoreVmodel(FieLdsModel model) {
        if ("".equals(model.getVModel()) && model.getConfig().getChildren() != null) {
            List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);
            return childModelList;
        }
        return null;
    }

    public static List<FieLdsModel> deleteMore(List<FieLdsModel> modelList) {
        List<FieLdsModel> newModelList = new ArrayList<>();
        for (FieLdsModel model : modelList) {
            List<FieLdsModel> newList = deleteMoreVmodel(model);
            if (newList == null || JnpfKeyConsts.CHILD_TABLE.equals(model.getConfig().getJnpfKey())) {
                newModelList.add(model);
            } else {
                newModelList.addAll(deleteMore(newList));
            }
        }
        return newModelList;
    }

    /**
     * 返回主键名称
     *
     * @param dbSourceOrDbLink
     * @param mainTable
     * @return
     */
    public static String getpKey(DbSourceOrDbLink dbSourceOrDbLink, String mainTable) throws SQLException {
        String pKeyName = "f_id";
        String tmpKey = JdbcTableModel.getPrimary(dbSourceOrDbLink, mainTable);
        if(StrUtil.isNotEmpty(tmpKey)){
            pKeyName = tmpKey;
        }
        return pKeyName;
    }



    /**
     * 获取有表的数据库连接
     * @return
     */
    public static Connection getTableConn() {
        String tenId;
        if (!configValueUtil.isMultiTenancy()) {
            tenId = dataSourceUtil.getDbName();
        } else {
            if (!"PostgreSQL".equals(dataSourceUtil.getDbType())) {
                tenId = userProvider.get().getTenantDbConnectionString();
            } else {
                tenId = dataSourceUtil.getDbName();
            }
        }
        try {
            return ConnUtil.getConn(dataSourceUtil,tenId);
        } catch (DataException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在线开发多数据源连接
     * @return
     */
    public static Connection getDataConn(DbLinkEntity linkEntity) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnUtil.getConnOrDefault(linkEntity);
        } catch (DataException e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 导出在线开发的表格
     * @param visualdevEntity
     * @param list
     * @param keys
     * @return
     */
    public static DownloadVO createModelExcel(VisualdevEntity visualdevEntity, List<Map<String, Object>> list, Collection<String> keys, String sheetName) {
        //判断sheetName
        boolean SheetTitleWithField = !sheetName.equals("表单信息");
        DownloadVO vo = DownloadVO.builder().build();
        try {
            //去除空数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Map<String, Object> map : list){
                int i = 0;
                for (String key : keys){
                    //子表
                    if (key.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)){
                        String tableField = key.substring(0, key.indexOf("-" ));
                        String field = key.substring(key.indexOf("-") + 1);
                        Object o = map.get(tableField);
                        if (o!=null){
                            List<Map<String, Object>> childList = (List<Map<String, Object>>) o;
                            for (Map<String, Object> childMap : childList){
                                if (childMap.get(field)!=null){
                                    i++;
                                }
                            }
                        }
                    } else {
                        Object o = map.get(key);
                        if (o!= null){
                            i++;
                        }
                    }
                }
                if (i>0){
                    dataList.add(map);
                }
            }

            FormDataModel formDataModel = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
            List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
            //递归
            List<FieLdsModel> allFields = new ArrayList<>();
            recursionFields(fieLdsModelList,allFields);

            Map<String, String> mainMap = new HashMap<>();
            allFields.stream().filter(a->!a.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)).forEach(m->mainMap.put(m.getVModel(),m.getConfig().getLabel()));
            List<FieLdsModel> childFields = allFields.stream().filter(a -> a.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)).collect(Collectors.toList());
            //创建导出属性对象
            List<ExportSelectedModel> child = new ArrayList<>();
            List<ExportSelectedModel> allExportModelList = new ArrayList<>();
            for (String key : keys){
                ExportSelectedModel exportSelectedModel = new ExportSelectedModel();
                if (key.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX) ){
                    String tableField = key.substring(0, key.indexOf("-"));
                    String field = key.substring(key.indexOf("-")+1);
                    exportSelectedModel.setTableField(tableField);
                    exportSelectedModel.setField(field);
                    child.add(exportSelectedModel);
                } else {
                    exportSelectedModel.setField(key);
                    exportSelectedModel.setLabel(mainMap.get(key));
                    allExportModelList.add(exportSelectedModel);
                }
            }
            Map<String, List<ExportSelectedModel>> childGroups = child.stream()
                .collect(Collectors.groupingBy(ExportSelectedModel::getTableField,LinkedHashMap::new,Collectors.toList()));
            List<String> keyForIndex = new ArrayList<>();
            for (String key:keys){
                keyForIndex.add(key);
            }
            for (Map.Entry<String, List<ExportSelectedModel>> entry : childGroups.entrySet()){
                String key = entry.getKey();
                List<String> collect = keyForIndex.stream().filter(k -> k.startsWith(key)).collect(Collectors.toList());
                String s = keyForIndex.stream().filter(keyF -> keyF.startsWith(key)).findFirst().orElse("");
                int i = keyForIndex.indexOf(s);
                keyForIndex.removeAll(collect);
                List<ExportSelectedModel> value = entry.getValue();
                FieLdsModel fieLdsModel = childFields.stream().filter(c -> c.getVModel().equals(key)).findFirst().orElse(null);
                Map<String, String> childMap = new HashMap<>(16);
                fieLdsModel.getConfig().getChildren().stream().forEach(c->childMap.put(c.getVModel(),c.getConfig().getLabel()));
                value.stream().forEach(v->
                    v.setLabel(childMap.get(v.getField()))
                );
                ExportSelectedModel exportSelectedModel = new ExportSelectedModel();
                exportSelectedModel.setTableField(key);
                exportSelectedModel.setSelectedModelList(value);
                exportSelectedModel.setLabel(fieLdsModel.getConfig().getLabel());
                allExportModelList.add(i, exportSelectedModel);
            }

            List<ExcelExportEntity> entitys = new ArrayList<>();
            for (ExportSelectedModel selectModel : allExportModelList){
                ExcelExportEntity exportEntity;
                if (StringUtil.isNotEmpty(selectModel.getTableField())){
                    exportEntity =new ExcelExportEntity(selectModel.getLabel(),selectModel.getTableField());
                    //+"("+selectModel.getTableField()+"-"+m.getField()+")"
                    exportEntity.setList(selectModel.getSelectedModelList().stream().map(m-> new ExcelExportEntity(m.getLabel() + (SheetTitleWithField ? "("+selectModel.getTableField()+"-"+m.getField()+")" : "")
                        ,m.getField())).collect(Collectors.toList()));
                } else {
                    // +"("+selectModel.getField()+")"
                    exportEntity = new ExcelExportEntity(selectModel.getLabel() + (SheetTitleWithField ? "("+selectModel.getField()+")" : ""),selectModel.getField());
                }
                entitys.add(exportEntity);
            }

            if (sheetName.equals("错误报告")){
                entitys.add(new ExcelExportEntity("异常原因","errorsInfo"));
            }
            ExportParams exportParams = new ExportParams(null, sheetName);
            @Cleanup Workbook workbook = new HSSFWorkbook();
            if (entitys.size()>0){
                if (dataList.size()==0){
                    dataList.add(new HashMap<>());
                }
                workbook  = ExcelExportUtil.exportExcel(exportParams, entitys, dataList);
            }
            String fileName = sheetName + DateUtil.dateNow("yyyyMMddHHmmss") + ".xls";
            MultipartFile multipartFile = ExcelUtil.workbookToCommonsMultipartFile(workbook, fileName);
            String temporaryFilePath = configValueUtil.getTemporaryFilePath();
            FileInfo fileInfo = FileUploadUtils.uploadFile(multipartFile, temporaryFilePath, fileName);
            vo.setName(fileInfo.getFilename());
            vo.setUrl(UploaderUtil.uploaderFile(fileInfo.getFilename() + "#" + "Temporary") + "&name=" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    public static void recursionFields(List<FieLdsModel> fieLdsModelList, List<FieLdsModel> allFields){
        for (FieLdsModel fieLdsModel : fieLdsModelList){
            if (JnpfKeyConsts.CHILD_TABLE.equals(fieLdsModel.getConfig().getJnpfKey())){
                allFields.add(fieLdsModel);
            } else {
                if (fieLdsModel.getConfig().getChildren()!=null){
                    recursionFields(fieLdsModel.getConfig().getChildren(),allFields);
                }else {
                    if (StringUtil.isNotEmpty(fieLdsModel.getVModel())){
                        allFields.add(fieLdsModel);
                    }
                }
            }
        }
    }

    /**
     * 视图导出
     *
     * @param visualdevEntity
     * @param list
     * @param keys
     * @param sheetName
     * @return
     */
    public static DownloadVO createModelExcelApiData(VisualdevEntity visualdevEntity, List<Map<String, Object>> list, Collection<String> keys, String sheetName) {
        //判断sheetName
        DownloadVO vo = DownloadVO.builder().build();
        try {
            ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(visualdevEntity.getColumnData(), ColumnDataModel.class);
            List<ColumnListField> columnListAll = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListField.class);
            List<ExcelExportEntity> entitys = new ArrayList<>();

            for (ColumnListField selectModel : columnListAll) {
                if(keys.contains(selectModel.getProp())) {
                    ExcelExportEntity exportEntity = new ExcelExportEntity(selectModel.getLabel());
                    exportEntity.setKey(selectModel.getProp());
                    exportEntity.setName(selectModel.getLabel());
                    entitys.add(exportEntity);
                }
            }

            if (sheetName.equals("错误报告")) {
                entitys.add(new ExcelExportEntity("异常原因", "errorsInfo"));
            }
            ExportParams exportParams = new ExportParams(null, sheetName);
            @Cleanup Workbook workbook = new HSSFWorkbook();
            if (entitys.size() > 0) {
                if (list.size() == 0) {
                    list.add(new HashMap<>());
                }
                workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
            }
            String fileName = sheetName + DateUtil.dateNow("yyyyMMddHHmmss") + ".xls";
            MultipartFile multipartFile = ExcelUtil.workbookToCommonsMultipartFile(workbook, fileName);
            String temporaryFilePath = configValueUtil.getTemporaryFilePath();
            FileInfo fileInfo = FileUploadUtils.uploadFile(multipartFile, temporaryFilePath, fileName);
            vo.setName(fileInfo.getFilename());
            vo.setUrl(UploaderUtil.uploaderFile(fileInfo.getFilename() + "#" + "Temporary") + "&name=" + fileName);
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * @param mapList
     * @return List<Map < String, Object>>
     * @Date 21:51 2020/11/11
     * @Description 将map中的所有key转化为小写
     */
    public static List<Map<String, Object>> toLowerKeyList(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> newMapList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> resultMap = new HashMap(16);
            Set<String> sets = map.keySet();
            for (String key : sets) {
                resultMap.put(key.toLowerCase(), map.get(key));
            }
            newMapList.add(resultMap);
        }
        return newMapList;
    }


    /**
     * @param map
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Description 将map中的所有key转化为小写
     */
    public static Map<String, Object> toLowerKey(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>(16);
        Set<String> sets = map.keySet();
        for (String key : sets) {
            resultMap.put(key.toLowerCase(), map.get(key));
        }
        return resultMap;
    }

//    /**
//     * 审批流提交
//     * @param visualdevEntity
//     * @param flowTaskId
//     * @param formdata
//     * @param userInfo
//     */
//    public static void submitFlowTask(VisualdevEntity visualdevEntity, String flowTaskId, Object formdata, UserInfo userInfo, FlowModel flowModel) throws WorkFlowException {
//        //审批流
//        if (visualdevEntity.getWebType().equals(VisualWebTypeEnum.FLOW_FROM.getType() )){
//            try {
//                FlowEngineService flowEngineService = SpringContext.getBean(FlowEngineService.class);
//                FlowEngineEntity flowEngineEntity = flowEngineService.getInfo(visualdevEntity.getFlowId());
//                FlowTaskService flowTaskService = SpringContext.getBean(FlowTaskService.class);
//                FlowTaskNewService flowTaskNewService = SpringContext.getBean(FlowTaskNewService.class);
//                FlowTaskEntity flowTaskEntity = flowTaskService.getInfoSubmit(flowTaskId, FlowTaskEntity::getId);
//                String id = null;
//                if (flowTaskEntity != null) {
//                    id = flowTaskEntity.getId();
//                }
//                String flowTitle = userInfo.getUserName() +"的"+ visualdevEntity.getFullName();
//                String billNo ="#Visual"+ DateUtil.getNow();
//                flowModel.setId(id);
//                flowModel.setFlowId(flowEngineEntity.getId());
//                flowModel.setProcessId(flowTaskId);
//                flowModel.setFlowTitle(flowTitle);
//                flowModel.setBillNo(billNo);
//                Map<String, Object> data = JsonUtil.entityToMap(formdata);
//                flowModel.setFormData(data);
//                flowModel.setUserInfo(userInfo);
//                flowTaskNewService.submit(flowModel);
//            } catch (WorkFlowException e) {
//                throw new WorkFlowException(e.getCode(),e.getMessage());
//            }
//        }
//    }
    public static String exampleExcelMessage(FieLdsModel model){
        String message = "";
        String jnpfKey = model.getConfig().getJnpfKey();
        switch (jnpfKey)
        {
            case JnpfKeyConsts.CREATEUSER:
            case JnpfKeyConsts.MODIFYUSER:
            case JnpfKeyConsts.CREATETIME:
            case JnpfKeyConsts.MODIFYTIME:
            case JnpfKeyConsts.CURRORGANIZE:
            case JnpfKeyConsts.CURRPOSITION:
            case JnpfKeyConsts.CURRDEPT:
            case JnpfKeyConsts.BILLRULE:
                message = "系统自动生成";
                break;
            case JnpfKeyConsts.COMSELECT:
                message = model.getMultiple() ? "例:XX信息/产品部,XX信息/技术部" : "例:XX信息/技术部";
                break;
            case JnpfKeyConsts.DEPSELECT:
                message = model.getMultiple() ? "例:产品部/部门编码,技术部/部门编码" : "例:技术部/部门编码";
                break;
            case JnpfKeyConsts.POSSELECT:
                message = model.getMultiple() ? "例:技术经理/岗位编码,技术员/岗位编码" : "例:技术员/岗位编码";
                break;
            case JnpfKeyConsts.USERSELECT:
                message = model.getMultiple() ? "例:张三/账号,李四/账号" : "例:张三/账号";
                break;
            case JnpfKeyConsts.CUSTOMUSERSELECT:
                message = model.getMultiple() ? "例:方方/账号,技术部/部门编码" : "例:方方/账号";
                break;
            case JnpfKeyConsts.ROLESELECT:
                message = model.getMultiple()  ? "例:研发人员/角色编码,测试人员/角色编码" : "例:研发人员/角色编码";
                break;
            case JnpfKeyConsts.GROUPSELECT:
                message = model.getMultiple() ? "例:A分组/分组编码,B分组/分组编码" : "例:A分组/分组编码";
                break;
            case JnpfKeyConsts.DATE:
                message = String.format("例: %s", model.getFormat());
                break;
            case JnpfKeyConsts.TIME:
//                message = "例: HH:mm:ss";
                message = String.format("例: %s", model.getFormat());
                break;
            case JnpfKeyConsts.ADDRESS:
                switch (model.getLevel())
                {
                    case 0:
                        message =  model.getMultiple() ? "例:福建省,广东省" : "例:福建省";
                        break;
                    case 1:
                        message =  model.getMultiple() ? "例:福建省/莆田市,广东省/广州市" : "例:福建省/莆田市";
                        break;
                    case 2:
                        message =  model.getMultiple() ? "例:福建省/莆田市/城厢区,广东省/广州市/荔湾区" : "例:福建省/莆田市/城厢区";
                        break;
                    case 3:
                        message =  model.getMultiple() ? "例:福建省/莆田市/城厢区/霞林街道,广东省/广州市/荔湾区/沙面街道" : "例:福建省/莆田市/城厢区/霞林街道";
                        break;
                        default:
                            break;
                }
                break;
            default:
                break;
        }
        return message;
    }
}
