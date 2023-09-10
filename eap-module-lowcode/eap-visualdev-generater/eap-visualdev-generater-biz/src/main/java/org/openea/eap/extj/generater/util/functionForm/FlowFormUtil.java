package org.openea.eap.extj.generater.util.functionForm;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.google.common.base.CaseFormat;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.base.util.SourceUtil;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.base.util.common.DataControlUtils;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.FileTypeConstant;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.generater.model.FormDesign.ColumnListDataModel;
import org.openea.eap.extj.generater.model.GenBaseInfo;
import org.openea.eap.extj.generater.model.GenFileNameSuffix;
import org.openea.eap.extj.generater.model.Template7Model;
import org.openea.eap.extj.generater.util.common.CodeGenerateUtil;
import org.openea.eap.extj.generater.util.common.FormCommonUtil;
import org.openea.eap.extj.generater.util.common.FunctionFormPublicUtil;
import org.openea.eap.extj.generater.util.common.SuperQueryUtil;
import org.openea.eap.extj.generater.util.custom.CustomGenerator;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.fields.slot.SlotModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.model.visualJson.props.PropsModel;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.XSSEscape;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 纯表单(带流程)
 * 
 */
public class FlowFormUtil implements CodeGenerateUtil {

    private static FlowFormUtil flowFormUtil = new FlowFormUtil();

    private FlowFormUtil() {

    }

    public static FlowFormUtil getFormUtil() {
        return flowFormUtil;
    }

    //------------------------------------界面----------------------------------

    /**
     * 界面模板
     *
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param model            模型
     * @param templatePath     模板路径
     * @param userInfo         用户
     * @param configValueUtil  下载路径
     */
    @Override
    public void htmlTemplates(String fileName, VisualdevEntity entity, DownloadCodeForm downloadCodeForm, FormDataModel model, String templatePath,
                              UserInfo userInfo, ConfigValueUtil configValueUtil, String pKeyName) throws Exception {
        //自定义包名
        String modulePackageName = downloadCodeForm.getModulePackageName();
        Map<String, Object> map = new HashMap<>(16);
        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tablesList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(tablesList);
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);

        //form的属性
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        map.put("ableAll" , JsonUtil.getListToJsonArray(formAllModel));
        //form和model
        Template7Model temModel = new Template7Model();
        temModel.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        temModel.setCreateDate(DateUtil.daFormat(new Date()));
        temModel.setCreateUser(GenBaseInfo.AUTHOR);
        temModel.setCopyright(GenBaseInfo.COPYRIGHT);
        temModel.setClassName(DataControlUtils.captureName(downloadCodeForm.getClassName()));
        temModel.setVersion(GenBaseInfo.VERSION);
        temModel.setDescription(GenBaseInfo.DESCRIPTION);

        //取对应表的别名
        Map<String, String> tableNameRenames = FunctionFormPublicUtil.tableNameRename(downloadCodeForm, tablesList);

        //子表赋值
        List<Map<String, Object>> child = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            FormColumnTableModel childList = table.get(i).getChildList();
            List<FormColumnModel> tableList = childList.getChildList();
            String sClassName = tableNameRenames.get(childList.getTableName());
            List<String> thousandsField=new ArrayList<>();
            for (FormColumnModel columnModel : tableList) {
                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                if( fieLdsModel.isThousands()){
                    thousandsField.add(fieLdsModel.getVModel());
                }
                SlotModel slot = fieLdsModel.getSlot();
                if (slot != null) {
                    slot.setAppOptions(slot.getOptions());
                    fieLdsModel.setSlot(slot);
                }
                List<TemplateJsonModel> templateJsonAll = new ArrayList<>();
                templateJsonAll.addAll(fieLdsModel.getConfig().getTemplateJson());
                List<TemplateJsonModel> templateJsonModelList = JsonUtil.getJsonToList(fieLdsModel.getTemplateJson(), TemplateJsonModel.class);
                templateJsonAll.addAll(templateJsonModelList);
                for (TemplateJsonModel templateJsonModel : templateJsonAll) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        String[] fieldList = templateJsonModel.getRelationField().split("-" );
                        if (fieldList.length > 1) {
                            templateJsonModel.setRelationField(sClassName.toLowerCase() + "-" + fieldList[1]);
                        }
                    }
                }
                for (TemplateJsonModel templateJsonModel : templateJsonModelList) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        String[] fieldList = templateJsonModel.getRelationField().split("-" );
                        if (fieldList.length > 1) {
                            templateJsonModel.setRelationField(sClassName.toLowerCase() + "List-" + fieldList[1]);
                        }
                    }
                }
                fieLdsModel.setTemplateJson(JsonUtil.getObjectToString(templateJsonModelList));
                fieLdsModel.getConfig().setTemplateJson(templateJsonAll);
            }
            childList.setChildList(tableList);
            childList.setThousandsField(thousandsField);
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            String className = DataControlUtils.captureName(sClassName);
            childs.put("className" , className);
            child.add(childs);
        }
        //主表赋值
        for (int i = 0; i < mast.size(); i++) {
            FieLdsModel fieLdsModel = mast.get(i).getFormColumnModel().getFieLdsModel();
            ConfigModel configModel = fieLdsModel.getConfig();
            if (configModel.getDefaultValue() instanceof String) {
                configModel.setValueType("String" );
            }
            if (configModel.getDefaultValue() == null) {
                configModel.setValueType("undefined" );
            }
            fieLdsModel.setConfig(configModel);
            SlotModel slot = fieLdsModel.getSlot();
            String vmodel = fieLdsModel.getVModel();
            if (slot != null) {
                slot.setAppOptions(slot.getOptions());
                fieLdsModel.setSlot(slot);
            }
            if (StringUtil.isEmpty(vmodel)) {
                mast.remove(i);
                i--;
            }
        }
        mast.stream().forEach(ms -> {
            PropsModel propsModel = ms.getFormColumnModel().getFieLdsModel().getProps();
            if (ObjectUtil.isNotEmpty(propsModel)) {
                propsModel.setPropsModel(JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class));
            }
        });

        //表单子表模型
        List<ColumnListDataModel> formChildList = new ArrayList<>();
        //列表子表数据model
        Map<String, List<FormAllModel>> groupColumnDataMap = mastTable.stream().collect(Collectors.groupingBy(m -> m.getFormMastTableModel().getTable()));
        Iterator<Map.Entry<String, List<FormAllModel>>> entries = groupColumnDataMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<FormAllModel>> formEntries = entries.next();
            Map<String, Object> objectMap = new HashMap<>();
            String className = DataControlUtils.captureName(tableNameRenames.get(formEntries.getKey()));
            ColumnListDataModel columnListDataModel = new ColumnListDataModel();
            columnListDataModel.setModelName(className);
            columnListDataModel.setModelUpName(DataControlUtils.captureName(className));
            columnListDataModel.setModelLowName(DataControlUtils.initialLowercase(className));
            List<FormAllModel> allModels = formEntries.getValue();
            allModels.stream().forEach(m -> {
                String vModel = m.getFormMastTableModel().getField();
                m.getFormMastTableModel().getMastTable().getFieLdsModel().setVModel(vModel);
            });
            List<String> fields = allModels.stream().map(m ->
                    m.getFormMastTableModel().getField()).collect(Collectors.toList());
            columnListDataModel.setFieldList(fields);
            columnListDataModel.setFieLdsModelList(allModels.stream().map(al -> al.getFormMastTableModel()).collect(Collectors.toList()));
            columnListDataModel.setTableName(formEntries.getKey());
            formChildList.add(columnListDataModel);
            List<FormColumnModel> children = formEntries.getValue().stream().map(allModel ->
                    allModel.getFormMastTableModel().getMastTable()).collect(Collectors.toList());
            //表字段给的范围-转换json
            children.forEach(item -> {
                item.setFieLdsModel(DataControlUtils.setAbleIDs(item.getFieLdsModel()));
            });
            FormColumnTableModel formColumnTableModel = new FormColumnTableModel();
            formColumnTableModel.setChildList(children);
            objectMap.put("children" , formColumnTableModel);
            objectMap.put("genInfo" , temModel);
            objectMap.put("package" , modulePackageName);
            objectMap.put("module" , model.getAreasName());
            objectMap.put("className" , className);
            objectMap.put("isMast" , "mast" );
            childrenTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, objectMap, templatePath, className, downloadCodeForm);
        }
        //子表model
        for (int i = 0; i < table.size(); i++) {
            FormColumnTableModel childList = table.get(i).getChildList();
            //表字段给的范围-转换json
            childList.getChildList().forEach(item -> {
                item.setFieLdsModel(DataControlUtils.setAbleIDs(item.getFieLdsModel()));
            });
            Map<String, Object> objectMap = JsonUtil.entityToMap(childList);
            String className = DataControlUtils.captureName(tableNameRenames.get(childList.getTableName()));
            objectMap.put("children" , childList);
            objectMap.put("genInfo" , temModel);
            objectMap.put("package" , modulePackageName);
            objectMap.put("module" , model.getAreasName());
            objectMap.put("className" , className);
            childrenTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, objectMap, templatePath, className, downloadCodeForm);
        }

        //界面
        map.put("module" , downloadCodeForm.getModule());
        map.put("className" , DataControlUtils.captureName(model.getClassName()));
        map.put("formRef" , model.getFormRef());
        map.put("formModel" , model.getFormModel());
        map.put("size" , model.getSize());
        map.put("labelPosition" , model.getLabelPosition());
        map.put("generalWidth" , model.getGeneralWidth());
        map.put("formStyle" , model.getFormStyle());
        map.put("labelWidth" , model.getLabelWidth());
        map.put("formRules" , model.getFormRules());
        map.put("gutter" , model.getGutter());
        map.put("disabled" , model.getDisabled());
        map.put("span" , model.getSpan());
        map.put("formBtns" , model.getFormBtns());
        map.put("idGlobal" , model.getIdGlobal());
        map.put("popupType" , model.getPopupType());
        map.put("form" , formAllModel);
        map.put("fullScreenWidth" , model.getFullScreenWidth());
        map.put("groupColumnDataMap" , groupColumnDataMap);
        map.put("genInfo" , temModel);
        map.put("modelName" , model.getClassName());
        map.put("package" , modulePackageName);
        map.put("formModelName" , entity.getFullName());
        //共用
        map.put("children" , child);
        map.put("fields" , mast);
        map.put("mastTable" , mastTable);
        map.put("columnChildren" , formChildList);
        pKeyName = pKeyName.toLowerCase().trim().replaceAll("f_" , "" );
        map.put("pKeyName" , pKeyName);
        String modelPathName = downloadCodeForm.getClassName().toLowerCase();
        map.put("modelPathName" , modelPathName);
        map.put("formModelName" , entity.getFullName());
        map.put("formId" , entity.getId());
        htmlTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, map, templatePath, downloadCodeForm);

        /**
         * 生成表单设计json文件
         */
        String path = FormCommonUtil.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName;
        FlowFormEntity flowFormEntity = FunctionFormPublicUtil.exportFlowFormJson(entity, downloadCodeForm);
        SuperQueryUtil.CreateFlowFormJsonFile(JsonUtil.getObjectToString(flowFormEntity), path);
    }

    /**
     * 获取文件名
     *
     * @param path      路径
     * @param template  模板名称
     * @param className 文件名称
     * @return
     */
    private static String getFileName(String path, String template, String className, DownloadCodeForm downloadCodeForm) {
        //是否微服务路径
        String framePath = FormCommonUtil.getCloudPath("-entity" , downloadCodeForm);
        String modelPath = XSSEscape.escapePath(path + File.separator + "java" + File.separator + framePath + File.separator + "model"
                + File.separator + className.toLowerCase());
        String htmlPath = XSSEscape.escapePath(path + File.separator + "html" + File.separator + "web" + File.separator + className.toLowerCase());
        File htmlfile = new File(htmlPath);
        File modelfile = new File(modelPath);
        if (!htmlfile.exists()) {
            htmlfile.mkdirs();
        }
        if (!modelfile.exists()) {
            modelfile.mkdirs();
        }
        if (template.contains("index.vue.vm" )) {
            className = "index";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("form.vue.vm" )) {
            className = "form";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("CrForm.java.vm" )) {
            return modelPath + File.separator + className + "CrForm.java";
        }
        if (template.contains("InfoVO.java.vm" )) {
            return modelPath + File.separator + className + "InfoVO.java";
        }
        return null;
    }

    /**
     * 界面的模板
     *
     * @param template 模板集合
     * @return
     */
    private static List<String> getTemplates(String template) {
        List<String> templates = new ArrayList<>();
        templates.add(template + File.separator + "html" + File.separator + "index.vue.vm" );
        templates.add(template + File.separator + "html" + File.separator + "form.vue.vm" );
        templates.add(template + File.separator + "java" + File.separator + "CrForm.java.vm" );
        templates.add(template + File.separator + "java" + File.separator + "InfoVO.java.vm" );
        return templates;
    }

    /**
     * 渲染html模板
     *
     * @param path         路径
     * @param object       模板数据
     * @param templatePath 模板路径
     */
    private static void htmlTemplates(String path, Map<String, Object> object, String templatePath, DownloadCodeForm downloadCodeForm) throws Exception {
        List<String> templates = getTemplates(templatePath);
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context" , object);
        for (String template : templates) {
            try {
                // 渲染模板
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
                tpl.merge(context, sw);
                String className = object.get("className" ).toString();
                String fileNames = getFileName(path, template, className, downloadCodeForm);
                if (fileNames != null) {
                    File file = new File(XSSEscape.escapePath(fileNames));
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    @Cleanup FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.write(sw.toString(), fos, Constants.UTF_8);
                    IOUtils.closeQuietly(sw);
                    IOUtils.closeQuietly(fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("渲染模板失败，表名：" + e);
            }
        }
    }

    /**
     * 渲染html模板
     *
     * @param path         路径
     * @param object       模板数据
     * @param templatePath 模板路径
     */
    private static void childrenTemplates(String path, Map<String, Object> object, String templatePath, String className, DownloadCodeForm downloadCodeForm) throws Exception {
        //是否微服务路径
        String framePath = FormCommonUtil.getCloudPath("-entity" , downloadCodeForm);
        String model = downloadCodeForm.getClassName();
        List<String> templates = new ArrayList<>();
        templates.add(templatePath + File.separator + "java" + File.separator + "Model.java.vm" );
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context" , object);
        for (String template : templates) {
            try {
                // 渲染模板
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
                tpl.merge(context, sw);
                String dirNames = path + File.separator + "java" + File.separator + framePath + File.separator + "model" + File.separator + model.toLowerCase();
                String fileNames = dirNames + File.separator + className + "Model.java";
                File file = new File(XSSEscape.escapePath(fileNames));
                if (!file.exists()) {
                    File dirFile = new File(XSSEscape.escapePath(dirNames));
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    file.createNewFile();
                }
                @Cleanup FileOutputStream fos = new FileOutputStream(file);
                IOUtils.write(sw.toString(), fos, Constants.UTF_8);
                IOUtils.closeQuietly(sw);
                IOUtils.closeQuietly(fos);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("渲染模板失败，表名：" + e);
            }
        }
    }
    //-------------------------代码----------------------------------

    /**
     * 生成主表
     *
     * @param dataSourceUtil   数据源
     * @param path             路径
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param entity           实体
     * @param userInfo         用户
     * @param configValueUtil  下载路径
     */
    private static void setCode(DataSourceUtil dataSourceUtil, String path, String fileName, String templatePath, DownloadCodeForm downloadCodeForm, VisualdevEntity entity,
                                UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) throws SQLException {
        //自定义包名
        String modulePackageName = downloadCodeForm.getModulePackageName();
        //tableJson
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //赋值主键
        tableModelList.stream().forEach(t -> {
            try {
                t.setTableKey(VisualUtils.getpKey(linkEntity, t.getTable()));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
        Map<String, Object> columndata = new HashMap<>(16);
        Template7Model model = new Template7Model();
        model.setTableName(downloadCodeForm.getClassName());
        model.setClassName(DataControlUtils.captureName(downloadCodeForm.getClassName()));
        model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        model.setCreateDate(DateUtil.daFormat(new Date()));
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(downloadCodeForm.getDescription());

        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
        //app 列表对象
        ColumnDataModel appColumnDataModel = JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //主表数据
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //列表子表数据model
        Map<String, List<FormAllModel>> groupColumnDataMap = mastTable.stream().collect(Collectors.groupingBy(m -> m.getFormMastTableModel().getTable()));
        //取对应表的别名
        Map<String, String> tableNameRenames = FunctionFormPublicUtil.tableNameRename(downloadCodeForm, tableModelList);
        //子表集合
        List<TableModel> childTableNameList = new ArrayList<>();
        //全部表
        List<TableModel> allTableNameList = new ArrayList<>();
        for (TableModel tableModel : tableModelList) {
            TableModel Model = new TableModel();
            Model.setInitName(tableModel.getTable());
            Model.setTable(tableNameRenames.get(tableModel.getTable()));
            Model.setTableField(DataControlUtils.captureName(tableModel.getTableField()));
            Model.setTypeId(tableModel.getTypeId());
            allTableNameList.add(Model);
            if ("0".equals(tableModel.getTypeId())) {
                childTableNameList.add(Model);
            }
        }

        //表单子表模型
        List<ColumnListDataModel> formChildList = new ArrayList<>();
        //列表子表数据model
        Iterator<Map.Entry<String, List<FormAllModel>>> entries = groupColumnDataMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<FormAllModel>> formEntries = entries.next();
            String className = DataControlUtils.captureName(tableNameRenames.get(formEntries.getKey()));
            ColumnListDataModel columnListDataModel = new ColumnListDataModel();
            columnListDataModel.setModelName(className);
            columnListDataModel.setModelUpName(DataControlUtils.captureName(className));
            columnListDataModel.setModelLowName(DataControlUtils.initialLowercase(className));
            List<FormAllModel> allModels = formEntries.getValue();
            List<String> fields = allModels.stream().map(m ->
                    m.getFormMastTableModel().getField()).collect(Collectors.toList());
            columnListDataModel.setFieldList(fields);
            columnListDataModel.setFieLdsModelList(allModels.stream().map(al -> al.getFormMastTableModel()).collect(Collectors.toList()));
            columnListDataModel.setTableName(formEntries.getKey());
            formChildList.add(columnListDataModel);
        }
        formChildList.stream().forEach(f -> {
            TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(f.getTableName())).findFirst().orElse(null);
            if (ObjectUtil.isNotEmpty(tableModel)) {
                f.setMainKey(tableModel.getRelationField());
                f.setRelationField(tableModel.getTableField());
                f.setMainUpKey(DataControlUtils.captureName(tableModel.getRelationField()));
                f.setRelationUpField(DataControlUtils.captureName(tableModel.getTableField()));
                String tableKey = tableModel.getTableKey().toLowerCase().replace("f_" , "" );
                tableKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableKey);
                f.setMainField(DataControlUtils.captureName(tableKey));
            }
        });

        //主表的字段
        Optional<TableModel> first = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst();
        if (!first.isPresent()) {
            throw new SQLException(MsgCode.COD001.get());
        }
        String tableName = first.get().getTable();
        String billNo = "";
        List<FieLdsModel> system = new ArrayList<>();
        for (int i = 0; i < mast.size(); i++) {
            FormAllModel mastModel = mast.get(i);
            FieLdsModel fieLdsModel = mastModel.getFormColumnModel().getFieLdsModel();
            PropsModel propsModel = fieLdsModel.getProps();
            if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                if (propsModel != null) {
                    PropsBeanModel props = JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class);
                    propsModel.setPropsModel(props);
                }
                fieLdsModel.setProps(propsModel);
                system.add(fieLdsModel);
            }
        }

        //列表子表
        List<FieLdsModel> childSystem = new ArrayList<>();
        for (int i = 0; i < mastTable.size(); i++) {
            FormAllModel mastModel = mastTable.get(i);
            FieLdsModel fieLdsModel = mastModel.getFormMastTableModel().getMastTable().getFieLdsModel();
            PropsModel propsModel = fieLdsModel.getProps();
            String vModel = fieLdsModel.getVModel();
            if (StringUtil.isNotEmpty(vModel)) {
                if (propsModel != null) {
                    PropsBeanModel props = JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class);
                    propsModel.setPropsModel(props);
                }
                fieLdsModel.setVModel(vModel.substring(vModel.lastIndexOf("jnpf_" )).replace("jnpf_" , "" ));
                fieLdsModel.setProps(propsModel);
                childSystem.add(fieLdsModel);
            }
        }

        //获取主表主键
        String pKeyName = VisualUtils.getpKey(linkEntity, tableName).toLowerCase().trim().replaceAll("f_" , "" );
        //子表的属性
        List<Map<String, Object>> child = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            FormColumnTableModel childList = table.get(i).getChildList();
            String className = DataControlUtils.captureName(tableNameRenames.get(childList.getTableName()));
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            Optional<TableModel> first1 = tableModelList.stream().filter(t -> t.getTable().equals(childList.getTableName())).findFirst();
            if (!first1.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first1.get();
            //获取主表主键
            String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
            String tableField = tableModel.getTableField().trim().replaceAll(":\"f_" , ":\"" );
            childs.put("tableField" , tableField);
            String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_" , ":\"" );
            childs.put("relationField" , relationField);
            childs.put("className" , className);
            String keyName = chidKeyName.trim().toLowerCase().replaceAll("f_" , "" );
            childs.put("chidKeyName" , keyName);
            child.add(childs);
        }

        //后台
        columndata.put("module" , downloadCodeForm.getModule());
        columndata.put("genInfo" , model);
        columndata.put("modelName" , model.getClassName());
        columndata.put("typeId" , 1);
        columndata.put("system" , system);
        columndata.put("childSystem" , childSystem);
        columndata.put("child" , child);
        columndata.put("childTableNameList" , childTableNameList);
        columndata.put("billNo" , billNo);
        columndata.put("pKeyName" , pKeyName);
        columndata.put("fieldsSize" , system.size());
        columndata.put("main" , true);
        columndata.put("mast" , mast);
        columndata.put("childtable" , table);
        columndata.put("mastTable" , mastTable);
        columndata.put("groupColumnDataMap" , groupColumnDataMap);
        columndata.put("columnChildren" , formChildList);

        // 是否存在过滤条件
        columndata.put("hasFilter" , false);
        if (columnDataModel != null) {
            List ruleList = JsonUtil.getJsonToList(columnDataModel.getRuleList(), Map.class);
            if (ruleList != null && ruleList.size() > 0) {
                columndata.put("hasFilter" , true);
            }
        }
        columndata.put("hasAppFilter" , false);
        if (appColumnDataModel != null) {
            List ruleListApp = JsonUtil.getJsonToList(appColumnDataModel.getRuleList(), Map.class);
            if (ruleListApp != null && ruleListApp.size() > 0) {
                columndata.put("hasAppFilter" , true);
            }
        }
        //雪花
        columndata.put("snowflake" , formData.getPrimaryKeyPolicy() == 1);
        //微服务标识
        columndata.put("isCloud" , FormCommonUtil.IS_CLOUD);
        //数据源
        if (linkEntity != null) {
            columndata.put("DS" , linkEntity.getFullName());
        }
        CustomGenerator mpg = new CustomGenerator(columndata);
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor(userInfo.getUserName() + "/" + userInfo.getUserAccount());
        gc.setOpen(false);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setEntityName(model.getClassName() + GenFileNameSuffix.ENTITY);
        gc.setMapperName(model.getClassName() + GenFileNameSuffix.MAPPER);
        gc.setXmlName(model.getClassName() + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(model.getClassName() + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(model.getClassName() + GenFileNameSuffix.SERVICEIMPL);
        gc.setControllerName(model.getClassName() + GenFileNameSuffix.CONTROLLER);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        mpg.setDataSource(SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(tableName);
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(modulePackageName);
        mpg.setPackageInfo(pc);
        // 包名
        columndata.put("modulePackageName" , modulePackageName);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = FormCommonUtil.getLocalBasePath() + model.getServiceDirectory();
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Controller.java.vm" , "controller" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Entity.java.vm" , "entity" , formData.getConcurrencyLock()));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.java.vm" , "mapper" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.xml.vm" , "xml" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Service.java.vm" , "service" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "ServiceImpl.java.vm" , "impl" , false));

        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
    }

    /**
     * 生成子表
     *
     * @param downloadCodeForm 表单信息
     * @param dataSourceUtil   数据源
     * @param path             路径
     * @param fileName         文件夹名称
     * @param className        文件名称
     * @param table            子表
     * @param userInfo         用户
     * @param configValueUtil  下载路径
     */
    private static void childTable(DataSourceUtil dataSourceUtil, String path, String fileName, String templatePath, DownloadCodeForm downloadCodeForm,
                                   String className, String table, UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) {
        //自定义包名
        String modulePackageName = downloadCodeForm.getModulePackageName();
        Map<String, Object> columndata = new HashMap<>(16);

        Template7Model model = new Template7Model();
        model.setClassName(table);
        model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        model.setCreateDate(DateUtil.daFormat(new Date()));
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(table);

        //数据源
        if (ObjectUtil.isNotEmpty(linkEntity)) {
            columndata.put("DS" , linkEntity.getFullName());
        }
        columndata.put("genInfo" , model);
        CustomGenerator mpg = new CustomGenerator(columndata);
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor(model.getCreateUser());
        gc.setOpen(false);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setEntityName(className + GenFileNameSuffix.ENTITY);
        gc.setMapperName(className + GenFileNameSuffix.MAPPER);
        gc.setXmlName(className + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(className + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(className + GenFileNameSuffix.SERVICEIMPL);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        mpg.setDataSource(SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(table);
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(modulePackageName);
        mpg.setPackageInfo(pc);
        //包名
        columndata.put("modulePackageName" , modulePackageName);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = FormCommonUtil.getLocalBasePath() + model.getServiceDirectory();
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Entity.java.vm" , "entity" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.java.vm" , "mapper" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.xml.vm" , "xml" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Service.java.vm" , "service" , false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "ServiceImpl.java.vm" , "impl" , false));

        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
    }

    /**
     * 生成表集合
     *
     * @param entity           实体
     * @param dataSourceUtil   数据源
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param userInfo         用户
     * @param configValueUtil  下载路径
     */
    @Override
    public void generate(VisualdevEntity entity, DataSourceUtil dataSourceUtil, String fileName, String templatePath, DownloadCodeForm downloadCodeForm,
                         UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) throws SQLException {
        List<TableModel> list = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //生成代码
        int i = 0;
        for (TableModel model : list) {
            if ("1".equals(model.getTypeId())) {
                setCode(dataSourceUtil, FormCommonUtil.getLocalBasePath() + FormCommonUtil.getPath(FileTypeConstant.TEMPLATECODEPATH),
                        fileName, templatePath, downloadCodeForm, entity, userInfo, configValueUtil, linkEntity);
            } else if ("0".equals(model.getTypeId())) {
                String name = downloadCodeForm.getSubClassName().split("," )[i];
                String className = DataControlUtils.captureName(name);
                childTable(dataSourceUtil, FormCommonUtil.getLocalBasePath() + FormCommonUtil.getPath(FileTypeConstant.TEMPLATECODEPATH),
                        fileName, templatePath, downloadCodeForm, className, model.getTable(), userInfo, configValueUtil, linkEntity);
                i++;
            }
        }
    }


}
