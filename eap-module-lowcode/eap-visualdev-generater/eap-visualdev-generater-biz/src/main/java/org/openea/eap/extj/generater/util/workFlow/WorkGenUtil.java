package org.openea.eap.extj.generater.util.workFlow;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.google.common.base.CaseFormat;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.util.SourceUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.generater.model.GenBaseInfo;
import org.openea.eap.extj.generater.model.GenFileNameSuffix;
import org.openea.eap.extj.generater.model.Template7Model;
import org.openea.eap.extj.base.util.common.DataControlUtils;
import org.openea.eap.extj.generater.util.common.FunctionFormPublicUtil;
import org.openea.eap.extj.generater.util.common.SuperQueryUtil;
import org.openea.eap.extj.generater.util.custom.CustomGenerator;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.model.visualJson.props.PropsModel;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class WorkGenUtil {

    //------------------------------------界面----------------------------------

    /**
     * 下载页面
     */
    public void htmlTemplates(WorkGenModel workGenModel) {
        Map<String, Object> map = new HashMap<>(16);
        VisualdevEntity entity = workGenModel.getEntity();
        List<FormAllModel> formAllModel = new ArrayList<>();
        Map<String, String> tableNameAll = this.forDataMode(workGenModel, formAllModel);
        Map<String, Set<String>> tempMap = new HashMap<>();
        FormDataModel model = workGenModel.getModel();
        String className = model.getClassName().substring(0, 1).toUpperCase() + model.getClassName().substring(1);
        String urlAddress = model.getClassName().substring(0, 1).toLowerCase() + model.getClassName().substring(1);
        Template7Model templateModel = templateModel(workGenModel, className);
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();

        List<FormAllModel> mast = this.mast(formAllModel);
        List<Map<String, Object>> child = new ArrayList<>();
        this.childModel(formAllModel, child, tableNameAll, tempMap);

        Map<String, List<FormAllModel>> mastListAll = this.mastTableModel(formAllModel, map, tableNameAll);

        map.put("children", child);
        map.put("fields", mast);
        map.put("genInfo", templateModel);
        map.put("modelName", model.getClassName());
        map.put("package", downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
        map.put("isModel", "true");
        map.put("primaryKeyPolicy", model.getPrimaryKeyPolicy() == 1);
        map.put("concurrencyLock", model.getConcurrencyLock());
        String modelPathName = model.getClassName().substring(0, 1).toLowerCase() + model.getClassName().substring(1);
        map.put("modelPathName", modelPathName);
        map.put("flowEnCode", entity.getEnCode());
        map.put("flowId", entity.getId());
        this.formData(map, workGenModel, formAllModel, tableNameAll);

        List<String> getTemplate = this.getTemplate(workGenModel, false);
        String path = templateModel.getServiceDirectory() + workGenModel.getFileName();

        this.templateJson(formAllModel, tempMap);
        map.put("tempJson", tempMap);
        this.htmlTemplates(map, getTemplate, path, templateModel.getClassName(), modelPathName);

        for (Map<String, Object> objectMap : child) {
            objectMap.put("genInfo", templateModel);
            objectMap.put("package", downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
            objectMap.put("modelPathName", modelPathName);
            getTemplate = this.getTemplate(workGenModel, true);
            String childName = objectMap.get("className").toString();
            objectMap.put("className", childName);
            this.htmlTemplates(objectMap, getTemplate, path, childName, modelPathName);
        }

        for (String mastKey : mastListAll.keySet()) {
            List<FormMastTableModel> mastlist = mastListAll.get(mastKey).stream().map(t -> t.getFormMastTableModel()).collect(Collectors.toList());
            List<FormColumnModel> childList = new LinkedList<>();
            for (FormMastTableModel columnModel : mastlist) {
                String field = columnModel.getField();
                if (StringUtil.isNotEmpty(field)) {
                    FormColumnModel columnTable = columnModel.getMastTable();
                    FieLdsModel fieLdsModel = columnTable.getFieLdsModel();
                    fieLdsModel.setVModel(field);
                    columnTable.setFieLdsModel(fieLdsModel);
                    childList.add(columnTable);
                }
            }
            getTemplate = this.getTemplate(workGenModel, true);
            FormColumnTableModel mastTableModel = new FormColumnTableModel();
            mastTableModel.setChildList(childList);
            String name = tableNameAll.get(mastKey);
            String childName = name.substring(0, 1).toUpperCase() + name.substring(1);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("children", mastTableModel);
            objectMap.put("genInfo", templateModel);
            objectMap.put("package", downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
            objectMap.put("className", childName);
            objectMap.put("modelPathName", modelPathName);
            this.htmlTemplates(objectMap, getTemplate, path, childName, modelPathName);
        }

        /**
         * 生成表单设计json文件
         */
        FlowFormEntity flowFormEntity = FunctionFormPublicUtil.exportFlowFormJson(entity, downloadCodeForm);
        flowFormEntity.setFlowType(0);
        flowFormEntity.setInterfaceUrl("/api/workflow/form/" + className);
        flowFormEntity.setUrlAddress("workFlow/workFlowForm/" + urlAddress + "/");
        SuperQueryUtil.CreateFlowFormJsonFile(JsonUtil.getObjectToString(flowFormEntity), path);
    }

    private void templateJson(List<FormAllModel> formAllModel, Map<String, Set<String>> tempMap) {
        for (FormAllModel model : formAllModel) {
            if (FormEnum.mast.getMessage().equals(model.getJnpfKey())) {
                List<TemplateJsonModel> templateJsonAll = new ArrayList<>();
                templateJsonAll.addAll(model.getFormColumnModel().getFieLdsModel().getConfig().getTemplateJson());
                List<TemplateJsonModel> templateJsonModelList = JsonUtil.getJsonToList(model.getFormColumnModel().getFieLdsModel().getTemplateJson(), TemplateJsonModel.class);
                templateJsonAll.addAll(templateJsonModelList);
                String vModel = model.getFormColumnModel().getFieLdsModel().getVModel();
                for (TemplateJsonModel templateJsonModel : templateJsonAll) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        Set<String> fieldList = tempMap.get(templateJsonModel.getRelationField()) != null ? tempMap.get(templateJsonModel.getRelationField()) : new HashSet<>();
                        fieldList.add(vModel);
                        tempMap.put(templateJsonModel.getRelationField().toLowerCase(), fieldList);
                    }
                }
                model.getFormColumnModel().getFieLdsModel().getConfig().setTemplateJson(templateJsonAll);
            }

            if (FormEnum.mastTable.getMessage().equals(model.getJnpfKey())) {
                List<TemplateJsonModel> templateJsonAll = new ArrayList<>();
                templateJsonAll.addAll(model.getFormMastTableModel().getMastTable().getFieLdsModel().getConfig().getTemplateJson());
                List<TemplateJsonModel> templateJsonModelList = JsonUtil.getJsonToList(model.getFormMastTableModel().getMastTable().getFieLdsModel().getTemplateJson(), TemplateJsonModel.class);
                templateJsonAll.addAll(templateJsonModelList);
                String vModel = model.getFormMastTableModel().getMastTable().getFieLdsModel().getVModel();
                for (TemplateJsonModel templateJsonModel : templateJsonAll) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        Set<String> fieldList = tempMap.get(templateJsonModel.getRelationField()) != null ? tempMap.get(templateJsonModel.getRelationField()) : new HashSet<>();
                        fieldList.add(vModel);
                        tempMap.put(templateJsonModel.getRelationField().toLowerCase(), fieldList);
                    }
                }
                model.getFormMastTableModel().getMastTable().getFieLdsModel().getConfig().setTemplateJson(templateJsonAll);
            }
        }
    }

    /**
     * 封装页面数据
     */
    private void formData(Map<String, Object> map, WorkGenModel workGenModel, List<FormAllModel> formAllModel, Map<String, String> tableNameAll) {
        FormDataModel model = workGenModel.getModel();
        DownloadCodeForm codeForm = workGenModel.getDownloadCodeForm();
        //界面
        map.put("module", codeForm.getModule());
        map.put("className", model.getClassName().substring(0, 1).toUpperCase() + model.getClassName().substring(1));
        map.put("formRef", model.getFormRef());
        map.put("formModel", model.getFormModel());
        map.put("size", model.getSize());
        map.put("labelPosition", model.getLabelPosition());
        map.put("labelWidth", model.getLabelWidth());
        map.put("formRules", model.getFormRules());
        map.put("gutter", model.getGutter());
        map.put("disabled", model.getDisabled());
        map.put("span", model.getSpan());
        map.put("formBtns", model.getFormBtns());
        map.put("idGlobal", model.getIdGlobal());
        map.put("popupType", model.getPopupType());
        map.put("form", formAllModel);

        //共用
        String pKeyName = workGenModel.getPKeyName();
        map.put("pKeyName", pKeyName);
    }

    /**
     * 获取模板
     *
     * @param workGenModel
     * @param isChild
     * @return
     */
    private List<String> getTemplate(WorkGenModel workGenModel, boolean isChild) {
        String template = workGenModel.getTemplatePath();
        List<String> templates = new ArrayList<>();
        //子表
        if (isChild) {
            templates.add(template + File.separator + "java" + File.separator + "Model.java.vm");
        } else {
            templates.add(template + File.separator + "java" + File.separator + "Form.java.vm");
            templates.add(template + File.separator + "html" + File.separator + "form.vue.vm");
            templates.add(template + File.separator + "html" + File.separator + "app.vue.vm");
            templates.add(template + File.separator + "java" + File.separator + "InfoVO.java.vm");
        }
        return templates;
    }

    /**
     * 获取文件名
     *
     * @param path      路径
     * @param template  模板名称
     * @param className 文件名称
     * @return
     */
    private String getFileNames(String path, String template, String className, String modePath) {
        path = XSSEscape.escapePath(path);
        modePath = XSSEscape.escapePath(modePath);
        className = XSSEscape.escapePath(className);
        String pathName = className.substring(0, 1).toLowerCase() + className.substring(1);
        String pcHtmlPath = path + File.separator + "html" + File.separator + "web" + File.separator + pathName;
        //pc的页面
        File pcHtmlfile = new File(pcHtmlPath);
        if (template.contains("form.vue.vm")) {
            if (!pcHtmlfile.exists()) {
                pcHtmlfile.mkdirs();
            }
        }
        if (template.contains("form.vue.vm")) {
            className = "index";
            return pcHtmlfile + File.separator + className + ".vue";
        }
        //app的页面
        String appHtmlPath = path + File.separator + "html" + File.separator + "app" + File.separator + pathName;
        File appHtmlfile = new File(appHtmlPath);
        if (template.contains("app.vue.vm")) {
            if (!appHtmlfile.exists()) {
                appHtmlfile.mkdirs();
            }
        }
        if (template.contains("app.vue.vm")) {
            className = "index";
            return appHtmlfile + File.separator + className + ".vue";
        }
        //后台模型页面
        String modelPath = path + File.separator + "java" + File.separator + "jnpf-workflow" + File.separator + "jnpf-workflow-form" + File.separator + "jnpf-workflow-form-entity" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "jnpf" + File.separator + "form" + File.separator + "model";
        if (StringUtil.isNotEmpty(modePath)) {
            modelPath = modelPath + File.separator + modePath;
        }
        File modelfile = new File(modelPath);
        if (!modelfile.exists()) {
            modelfile.mkdirs();
        }
        if (template.contains("Form.java.vm")) {
            return modelPath + File.separator + className + "Form.java";
        }
        if (template.contains("InfoVO.java.vm")) {
            return modelPath + File.separator + className + "InfoVO.java";
        }
        if (template.contains("Model.java.vm")) {
            return modelPath + File.separator + className + "Model.java";
        }
        return null;
    }

    /**
     * 渲染html模板
     *
     * @param path   路径
     * @param object 模板数据
     * @param path   模板路径
     */
    private void htmlTemplates(Object object, List<String> templates, String path, String className, String modePath) {
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            try {
                // 渲染模板
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
                tpl.merge(context, sw);
                String fileNames = getFileNames(path, template, className, modePath);
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
     * 封装主表数据
     */
    private List<FormAllModel> mast(List<FormAllModel> formAllModel) {
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //主表赋值
        for (int i = 0; i < mast.size(); i++) {
            FieLdsModel fieLdsModel = mast.get(i).getFormColumnModel().getFieLdsModel();
            model(fieLdsModel);
            String vmodel = fieLdsModel.getVModel();
            if (StringUtil.isEmpty(vmodel)) {
                mast.remove(i);
                i--;
            }
        }
        return mast;
    }

    /**
     * 封装mastTable数据
     */
    private Map<String, List<FormAllModel>> mastTableModel(List<FormAllModel> formAllModel, Map<String, Object> map, Map<String, String> tableNameAll) {
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        Map<String, List<FormAllModel>> mastListAll = mastTable.stream().collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
        Map<String, String> mastTableNameAll = new HashMap<>();
        Map<String, List<FormAllModel>> mastTableList = new HashMap<>();
        //表单主表
        for (String mastkey : mastListAll.keySet()) {
            List<FormAllModel> mastList = mastListAll.get(mastkey);
            for (FormAllModel fieLdsList : mastList) {
                FieLdsModel fieLdsModel = fieLdsList.getFormMastTableModel().getMastTable().getFieLdsModel();
                model(fieLdsModel);
            }
            mastListAll.put(mastkey, mastList);
            String tableName = tableNameAll.get(mastkey);
            String name = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
            mastTableNameAll.put(mastkey, name);
            mastTableList.put(tableName.toLowerCase(), mastList);
        }
        map.put("mastTableName", mastTableNameAll);
        map.put("tableName", tableNameAll);
        map.put("mastTable", mastTableList);
        return mastListAll;
    }

    /**
     * 封装子表数据
     */
    private void childModel(List<FormAllModel> formAllModel, List<Map<String, Object>> child, Map<String, String> tableNameAll, Map<String, Set<String>> tempMap) {
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (FormAllModel formModel : table) {
            FormColumnTableModel childList = formModel.getChildList();
            List<String> thousandsField = new ArrayList<>();
            String tableName = childList.getTableName();
            String name = tableNameAll.get(tableName);
            String className = name.substring(0, 1).toUpperCase() + name.substring(1);
            List<FormColumnModel> tableList = childList.getChildList();
            for (int i = 0; i < tableList.size(); i++) {
                FormColumnModel columnModel = tableList.get(i);
                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                model(fieLdsModel);
                if(fieLdsModel.isThousands()) {
                    thousandsField.add(fieLdsModel.getVModel());
                }
                List<TemplateJsonModel> templateJsonAll = new ArrayList<>();
                templateJsonAll.addAll(fieLdsModel.getConfig().getTemplateJson());
                List<TemplateJsonModel> templateJsonModelList = JsonUtil.getJsonToList(fieLdsModel.getTemplateJson(), TemplateJsonModel.class);
                templateJsonAll.addAll(templateJsonModelList);
                for (TemplateJsonModel templateJsonModel : templateJsonAll) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        String[] fieldList = templateJsonModel.getRelationField().split("-");
                        if (fieldList.length > 1) {
                            templateJsonModel.setRelationField(className.toLowerCase() + "-" + fieldList[1]);
                        }
                    }
                }
                for (TemplateJsonModel templateJsonModel : templateJsonModelList) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        String[] fieldList = templateJsonModel.getRelationField().split("-");
                        if (fieldList.length > 1) {
                            templateJsonModel.setRelationField(className.toLowerCase() + "List-" + fieldList[1]);
                        }
                    }
                }
                fieLdsModel.setTemplateJson(JsonUtil.getObjectToString(templateJsonModelList));
                fieLdsModel.getConfig().setTemplateJson(templateJsonAll);
            }
            childList.setThousandsField(thousandsField);
            childList.setChildList(tableList);
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            childs.put("className", className);
            childs.put("children", childList);
            child.add(childs);
        }
    }

    /**
     * 封装model数据
     */
    private void model(FieLdsModel fieLdsModel) {
        ConfigModel configModel = fieLdsModel.getConfig();
        String jnpfKey = configModel.getJnpfKey();
        if (configModel.getDefaultValue() instanceof String) {
            configModel.setValueType("String");
        }
        if (configModel.getDefaultValue() == null) {
            configModel.setValueType("undefined");
            if (JnpfKeyConsts.NUM_INPUT.equals(jnpfKey)) {
                configModel.setDefaultValue(ObjectUtil.isNotEmpty(fieLdsModel.getMin()) ? fieLdsModel.getMin() : 0);
                configModel.setValueType(null);
            }
            if (JnpfKeyConsts.ADDRESS.equals(jnpfKey)) {
                configModel.setDefaultValue(new ArrayList<>());
                configModel.setValueType(null);
            }
        }
        if (JnpfKeyConsts.SWITCH.equals(jnpfKey)) {
            if (configModel.getDefaultValue() instanceof Boolean) {
                Boolean defaultValue = (Boolean) configModel.getDefaultValue();
                configModel.setDefaultValue(defaultValue ? 1 : 0);
            }
        }
        if (JnpfKeyConsts.TREESELECT.equals(jnpfKey)) {
            configModel.setValueType(fieLdsModel.getMultiple() ? configModel.getValueType() : "undefined");
        }
        fieLdsModel.setConfig(configModel);
    }

    /**
     * 封装表对应的输出名字
     */
    private Map<String, String> tableName(List<TableModel> tableModelList, DownloadCodeForm downloadCodeForm) {
        Map<String, String> tableClass = new HashMap<>(16);
        int i = 0;
        for (TableModel tableModel : tableModelList) {
            if ("0".equals(tableModel.getTypeId())) {
                String[] subClassName = downloadCodeForm.getSubClassName().split(",");
                tableClass.put(tableModel.getTable(), subClassName[i]);
                i++;
            }
        }
        return tableClass;
    }


    //----------------------------代码-------------------------------------------------------

    /**
     * 生成表集合
     *
     * @param workGenModel 对象
     * @throws SQLException
     */
    public void generate(WorkGenModel workGenModel) throws SQLException {
        VisualdevEntity entity = workGenModel.getEntity();
        List<TableModel> list = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        Map<String, String> tableNameAll = tableName(tableModelList, downloadCodeForm);
        //生成代码
        for (TableModel model : list) {
            String table = model.getTable();
            workGenModel.setTable(table);
            if ("1".equals(model.getTypeId())) {
                workGenModel.setClassName(downloadCodeForm.getClassName());
                this.setCode(workGenModel);
            } else if ("0".equals(model.getTypeId())) {
                String name = tableNameAll.get(table);
                String className = name.substring(0, 1).toUpperCase() + name.substring(1);
                workGenModel.setClassName(className);
                this.childTable(workGenModel);
            }
        }
    }

    /**
     * 生成主表
     *
     * @param workGenModel 对象
     * @throws SQLException
     */
    private void setCode(WorkGenModel workGenModel) throws SQLException {
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        //tableJson
        Map<String, Object> columndata = new HashMap<>(16);
        String className = downloadCodeForm.getClassName().substring(0, 1).toUpperCase() + downloadCodeForm.getClassName().substring(1);
        Template7Model model = this.templateModel(workGenModel, className);
        this.columData(columndata, workGenModel, model);
        DbLinkEntity linkEntity = workGenModel.getLinkEntity();
        columndata.put("DSId", linkEntity != null ? linkEntity.getId() : "master");
        FormDataModel formDataModel = workGenModel.getModel();
        columndata.put("primaryKeyPolicy", formDataModel.getPrimaryKeyPolicy() == 1);
        columndata.put("concurrencyLock", formDataModel.getConcurrencyLock());
        columndata.put("modulePackageName", downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
        this.javaGenerate(columndata, model, workGenModel, true);
    }

    /**
     * 生成子表
     *
     * @param workGenModel 封装对象
     */
    private void childTable(WorkGenModel workGenModel) {
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        Template7Model model = this.templateModel(workGenModel, workGenModel.getClassName());
        Map<String, Object> columndata = new HashMap<>(16);
        columndata.put("genInfo", model);
        DbLinkEntity linkEntity = workGenModel.getLinkEntity();
        columndata.put("DSId", linkEntity != null ? linkEntity.getId() : "master");
        columndata.put("modulePackageName", downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
        this.javaGenerate(columndata, model, workGenModel, false);
    }

    /**
     * 封装数据
     *
     * @param workGenModel
     * @param className
     * @return
     */
    private Template7Model templateModel(WorkGenModel workGenModel, String className) {
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        Template7Model template7Model = new Template7Model();
        template7Model.setClassName(className);
        template7Model.setServiceDirectory(workGenModel.getServiceDirectory());
        template7Model.setCreateDate(DateUtil.daFormat(new Date()));
        template7Model.setCreateUser(GenBaseInfo.AUTHOR);
        template7Model.setCopyright(GenBaseInfo.COPYRIGHT);
        template7Model.setDescription(downloadCodeForm.getDescription());
        return template7Model;
    }

    /**
     * 封装数据
     *
     * @param formAllModel
     */
    private Map<String, String> forDataMode(WorkGenModel workGenModel, List<FormAllModel> formAllModel) {
        VisualdevEntity entity = workGenModel.getEntity();
        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        RecursionForm recursionForm = new RecursionForm(list, tableModelList);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        Map<String, String> tableNameAll = tableName(tableModelList, workGenModel.getDownloadCodeForm());
        return tableNameAll;
    }

    /**
     * 封装数据
     *
     * @param columndata
     * @param model
     * @param workGenModel
     * @param isMast
     */
    private void javaGenerate(Map<String, Object> columndata, Template7Model model, WorkGenModel workGenModel, boolean isMast) {
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
        String className = model.getClassName();
        if (isMast) {
            gc.setControllerName(className + GenFileNameSuffix.CONTROLLER);
        }
        gc.setEntityName(className + GenFileNameSuffix.ENTITY);
        gc.setMapperName(className + GenFileNameSuffix.MAPPER);
        gc.setXmlName(className + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(className + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(className + GenFileNameSuffix.SERVICEIMPL);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        UserInfo userInfo = workGenModel.getUserInfo();
        DbLinkEntity linkEntity = workGenModel.getLinkEntity();

        mpg.setDataSource(SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        String table = workGenModel.getTable();
        strategy.setInclude(table);
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        pc.setParent(downloadCodeForm.getModulePackageName() + "." + downloadCodeForm.getModule());
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = model.getServiceDirectory();
        String templatePath = workGenModel.getTemplatePath();
        String fileName = workGenModel.getFileName();
        String path = workGenModel.getTemplateCodePath();
        String flowWorkPath = "jnpf-workflow" + File.separator + "jnpf-workflow-form" + File.separator + "jnpf-workflow-form-%s" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "jnpf" + File.separator + "form" + File.separator + "%s";
        if (isMast) {
            String controller = String.format(flowWorkPath, "controller", "controller");
            focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Controller.java.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return javaPath + fileName + File.separator + "java" + File.separator + controller+ File.separator + tableInfo.getControllerName() + StringPool.DOT_JAVA;
                }
            });
        }
        String entity = String.format(flowWorkPath, "entity", "entity");
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Entity.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                List<TableField> fieldAll = tableInfo.getFields();
                TableField mainTableField = fieldAll.stream().filter(tableField -> tableField.isKeyFlag()).findFirst().orElse(null);
                fieldAll = fieldAll.stream().filter(DataControlUtils.distinctByKey(t -> t.getName())).collect(Collectors.toList());
                if (mainTableField != null) {
                    fieldAll.stream().filter(tableField -> tableField.getName().equals(mainTableField.getName())).forEach(t -> t.setKeyFlag(mainTableField.isKeyFlag()));
                }
                for (TableField field : fieldAll) {
                    String name = field.getName().toLowerCase().replaceAll("f_", "");
                    field.setPropertyName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
                }
                tableInfo.setFields(fieldAll);
                return javaPath + fileName + File.separator + "java" + File.separator + entity + File.separator + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });
        String mapper = String.format(flowWorkPath, "biz", "mapper");
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Mapper.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + mapper + File.separator + tableInfo.getMapperName() + StringPool.DOT_JAVA;
            }
        });
        String mapperxml = "resources" + File.separator + "mapper";
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + mapperxml + File.separator + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        String service = String.format(flowWorkPath, "biz", "service");
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Service.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + service + File.separator + tableInfo.getServiceName() + StringPool.DOT_JAVA;
            }
        });
        String serviceImpl = String.format(flowWorkPath, "biz", "service" + File.separator + "impl");
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "ServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + serviceImpl + File.separator + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
    }

    /**
     * 封装数据
     *
     * @param formAllModel
     * @param system
     */
    private void system(List<FormAllModel> formAllModel, List<FieLdsModel> system) {
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (int i = 0; i < mast.size(); i++) {
            FormAllModel mastModel = mast.get(i);
            FieLdsModel fieLdsModel = mastModel.getFormColumnModel().getFieLdsModel();
            String model = fieLdsModel.getVModel();
            String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
            PropsModel propsModel = fieLdsModel.getProps();
            if (StringUtil.isNotEmpty(model)) {
                if (propsModel != null) {
                    PropsBeanModel props = JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class);
                    propsModel.setPropsModel(props);
                }
                fieLdsModel.setProps(propsModel);
                system.add(fieLdsModel);
            }
        }
    }

    /**
     * 封装数据
     */
    private void mastTable(List<FormAllModel> formAllModel, Map<String, Object> columndata, WorkGenModel workGenModel, Map<String, String> tableNameAll) throws SQLException {
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        Map<String, List<FormAllModel>> mastListAll = mastTable.stream().collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
        DbLinkEntity linkEntity = workGenModel.getLinkEntity();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(workGenModel.getEntity().getVisualTables(), TableModel.class);
        List<Map<String, Object>> mastTableNameAll = new ArrayList<>();
        for (String mastkey : mastListAll.keySet()) {
            Map<String, Object> childMap = new HashMap<>();
            String mastTableName = tableNameAll.get(mastkey);
            String className = mastTableName.substring(0, 1).toUpperCase() + mastTableName.substring(1);
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(mastkey)).findFirst();
            if (!first.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first.get();
            //获取主表主键
            String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
            String tableField = tableModel.getTableField().trim().replaceAll(":\"f_", ":\"");
            childMap.put("tableField", tableField);
            String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_", ":\"");
            childMap.put("relationField", relationField);
            childMap.put("className", className);
            String keyName = chidKeyName.trim().toLowerCase().replaceAll("f_", "");
            String mastTableKeyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, keyName);
            childMap.put("chidKeyName", mastTableKeyName);
            childMap.put("childList", mastListAll.get(mastkey));
            childMap.put("table", mastkey);
            mastTableNameAll.add(childMap);
        }
        columndata.put("tableNameAll", mastTableNameAll);
    }

    /**
     * 封装数据
     *
     * @param formAllModel
     * @param child
     * @param workGenModel
     * @throws SQLException
     */
    private void child(List<FormAllModel> formAllModel, List<Map<String, Object>> child, WorkGenModel workGenModel, Map<String, String> tableNameAll) throws SQLException {
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        DbLinkEntity linkEntity = workGenModel.getLinkEntity();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(workGenModel.getEntity().getVisualTables(), TableModel.class);
        for (FormAllModel tableModelAll : table) {
            FormColumnTableModel childList = tableModelAll.getChildList();
            String childTableName = childList.getTableName();
            String name = tableNameAll.get(childTableName);
            List<FormColumnModel> columnList = childList.getChildList();
            for (int i = 0; i < columnList.size(); i++) {
                String model = columnList.get(i).getFieLdsModel().getVModel();
                if (StringUtil.isEmpty(model)) {
                    columnList.remove(i);
                    i--;
                }
            }
            childList.setChildList(columnList);
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            String childClassName = name.substring(0, 1).toUpperCase() + name.substring(1);
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(childList.getTableName())).findFirst();
            if (!first.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first.get();
            //获取主表主键
            String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
            String tableField = tableModel.getTableField().trim().replaceAll(":\"f_", ":\"");
            childs.put("tableField", tableField);
            String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_", ":\"");
            childs.put("relationField", relationField);
            childs.put("className", childClassName);
            String keyName = chidKeyName.trim().toLowerCase().replaceAll("f_", "");
            String tableKeyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, keyName);
            childs.put("chidKeyName", tableKeyName);
            child.add(childs);
        }
    }

    /**
     * 封装数据
     *
     * @param columndata
     * @param workGenModel
     * @param template7Model
     * @throws SQLException
     */
    private void columData(Map<String, Object> columndata, WorkGenModel workGenModel, Template7Model template7Model) throws SQLException {
        List<FormAllModel> formAllModel = new ArrayList<>();
        Map<String, String> tableNameAll = this.forDataMode(workGenModel, formAllModel);
        //主表数据
        List<FieLdsModel> system = new ArrayList<>();
        this.system(formAllModel, system);
        //子表的属性
        List<Map<String, Object>> child = new ArrayList<>();
        this.child(formAllModel, child, workGenModel, tableNameAll);
        //表单子表
        this.mastTable(formAllModel, columndata, workGenModel, tableNameAll);

        DownloadCodeForm downloadCodeForm = workGenModel.getDownloadCodeForm();
        columndata.put("genInfo", template7Model);
        columndata.put("areasName", downloadCodeForm.getModule());
        columndata.put("modelName", template7Model.getClassName());
        columndata.put("typeId", 1);
        columndata.put("system", system);
        columndata.put("child", child);
        String pKeyName = workGenModel.getPKeyName();
        columndata.put("pKeyName", pKeyName);
        columndata.put("isModel", "true");
        String modelPathName = downloadCodeForm.getClassName().substring(0, 1).toLowerCase() + downloadCodeForm.getClassName().substring(1);
        columndata.put("modelPathName", modelPathName);
        columndata.put("module", downloadCodeForm.getModule());

    }

}
