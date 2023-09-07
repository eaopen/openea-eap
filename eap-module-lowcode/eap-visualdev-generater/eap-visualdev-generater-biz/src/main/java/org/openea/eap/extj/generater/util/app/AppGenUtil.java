package org.openea.eap.extj.generater.util.app;

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
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.generater.model.GenBaseInfo;
import org.openea.eap.extj.generater.model.GenFileNameSuffix;
import org.openea.eap.extj.generater.model.Template7Model;
import org.openea.eap.extj.generater.util.common.FormCommonUtil;
import org.openea.eap.extj.generater.util.custom.CustomGenerator;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.model.visualJson.ColumnListField;
import org.openea.eap.extj.base.model.VisualWebTypeEnum;
import org.openea.eap.extj.base.model.template6.BtnData;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.base.util.common.DataControlUtils;
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

public class AppGenUtil {


    //+-----------------------------界面2021.8.13------------------------------------------------------------

    public void htmlTemplates(AppGenModel appGenModel) {
        Map<String, Object> map = new HashMap<>(16);
        VisualdevEntity entity = appGenModel.getEntity();
        List<FormAllModel> formAllModel = new ArrayList<>();
        Map<String, Set<String>> tempMap = new HashMap<>();
        Map<String, String> tableNameAll = this.forDataMode(appGenModel, formAllModel);

        FormDataModel model = appGenModel.getModel();
        String className = model.getClassName().substring(0, 1).toUpperCase() + model.getClassName().substring(1);
        Template7Model templateModel = templateModel(appGenModel, className);

        List<FormAllModel> mast = this.mast(formAllModel);
        List<Map<String, Object>> child = new ArrayList<>();
        this.childModel(formAllModel, child, tableNameAll, tempMap);

        Map<String, List<FormAllModel>> mastListAll = this.mastTableModel(formAllModel, map, tableNameAll);

        this.templateJson(formAllModel, tempMap);
        map.put("tempJson", tempMap);
        map.put("moduleId",appGenModel.getEntity().getId());
        map.put("children", child);
        map.put("groupTable", appGenModel.getGroupTable());
        map.put("type", appGenModel.getType());
        map.put("fields", mast);
        map.put("genInfo", templateModel);
        map.put("modelName", model.getClassName());
        map.put("package", "jnpf");
        map.put("isModel", "true");
        String modelPathName = model.getClassName().substring(0, 1).toLowerCase() + model.getClassName().substring(1);
        map.put("modelPathName", modelPathName);
        map.put("flowEnCode", entity.getEnCode());
        map.put("flowId", entity.getId());
        this.formData(map, appGenModel, formAllModel, tableNameAll);

        List<String> getTemplate = this.getTemplate(appGenModel, false);
        String path = FileUploadUtils.getLocalBasePath() + templateModel.getServiceDirectory() + appGenModel.getFileName();

        boolean type = this.isForm(appGenModel);
        this.htmlTemplates(map, getTemplate, path, templateModel.getClassName(), modelPathName, !type);

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
     * 获取模板
     *
     * @param appGenModel
     * @param isChild
     * @return
     */
    private List<String> getTemplate(AppGenModel appGenModel, boolean isChild) {
        List<String> templates = this.getTemplate(appGenModel, isChild, false);
        return templates;
    }

    /**
     * 获取模板
     *
     * @param appGenModel
     * @param isChild
     * @return
     */
    private List<String> getTemplate(AppGenModel appGenModel, boolean isChild, boolean isMastTable) {
        String template = this.tempPath(appGenModel);
        VisualdevEntity entity = appGenModel.getEntity();
        boolean isType = !VisualWebTypeEnum.FORM.getType().equals(entity.getWebType());
        List<String> templates = new ArrayList<>();
        templates.add(template + File.separator + "app" + File.separator + "form.vue.vm");
        //模板2
        if(VisualWebTypeEnum.FORM_LIST.getType().equals(entity.getWebType())) {
            ColumnDataModel appColumnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
            List<BtnData> columnBtnDataList = JsonUtil.getJsonToList(appColumnDataModel.getColumnBtnsList(), BtnData.class);
            boolean detail = columnBtnDataList.stream().filter(t -> "detail".equals(t.getValue())).count() > 0;
            if (entity.getEnableFlow() == 0 && isType && detail) {
                templates.add(template + File.separator + "app" + File.separator + "detail.vue.vm");
            }
        }
        //除了模板4,其他都有index的模板
        boolean index = !(VisualWebTypeEnum.FORM.getType().equals(entity.getWebType()) && entity.getEnableFlow() != 1);
        if (index) {
            templates.add(template + File.separator + "app" + File.separator + "index.vue.vm");
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
    private String getFileNames(String path, String template, String className, String modePath, boolean isIndex) {
        path = XSSEscape.escapePath(path);
        modePath = XSSEscape.escapePath(modePath);
        className = XSSEscape.escapePath(className);
        String pathName = className.substring(0, 1).toLowerCase() + className.substring(1);
        if (template.contains("index.vue.vm") || template.contains("detail.vue.vm")) {
            String indexHtmlPath = path + File.separator + "html" + File.separator + "app";
            indexHtmlPath += isIndex ? File.separator + "index" + File.separator + pathName : File.separator + pathName;
            File indexfile = new File(indexHtmlPath);
            if (!indexfile.exists()) {
                indexfile.mkdirs();
            }
            className = template.contains("index.vue.vm") ? "index" : "detail";
            return indexHtmlPath + File.separator + className + ".vue";
        }
        if (template.contains("form.vue.vm")) {
            String formHtmlPath = path + File.separator + "html" + File.separator + "app";
            formHtmlPath += isIndex ? File.separator + "form" + File.separator + pathName : File.separator + pathName;
            File formfile = new File(formHtmlPath);
            if (!formfile.exists()) {
                formfile.mkdirs();
            }
            className = isIndex ? "index" : "form";
            return formHtmlPath + File.separator + className + ".vue";
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
    private void htmlTemplates(Object object, List<String> templates, String path, String className, String modePath, boolean isIndex) {
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            // 渲染模板
            try {
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
                tpl.merge(context, sw);
                String fileNames = getFileNames(path, template, className, modePath, isIndex);
                if (fileNames != null) {
                    File file = new File(fileNames);
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
            this.model(fieLdsModel);
            String vmodel = fieLdsModel.getVModel();
            String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
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
                this.model(fieLdsModel);
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

    /**
     * 封装页面数据
     */
    private void formData(Map<String, Object> map, AppGenModel appGenModel, List<FormAllModel> formAllModel, Map<String, String> tableNameAll) {
        FormDataModel model = appGenModel.getModel();
        //界面
        map.put("module", model.getAreasName());
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
        map.put("infoVO", "1");

        //列表
        boolean isPage = this.isType(appGenModel);
        if (isPage) {
            List<BtnData> columnList = new ArrayList<>();
            String page = "1";
            String sort = "";
            int pageSize = 20;
            VisualdevEntity entity = appGenModel.getEntity();
            if (StringUtil.isNotEmpty(entity.getColumnData())) {
                String columnData = entity.getColumnData().trim().replaceAll(":\"f_", ":\"");
                ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(columnData, ColumnDataModel.class);
                page = columnDataModel.getHasPage() ? "0" : "1";
                sort = columnDataModel.getSort();
                pageSize = columnDataModel.getPageSize();
                this.columnData(formAllModel, columnDataModel, tableNameAll, map);
                List<BtnData> btns = StringUtil.isNotEmpty(columnDataModel.getBtnsList()) ? JsonUtil.getJsonToList(columnDataModel.getBtnsList(), BtnData.class) : new ArrayList<>();
                columnList = StringUtil.isNotEmpty(columnDataModel.getColumnBtnsList()) ? JsonUtil.getJsonToList(columnDataModel.getColumnBtnsList(), BtnData.class) : new ArrayList<>();
                columnList.addAll(btns);
            }
            //合计千分位
            List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
            List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
            List<String> thousandsField = FormCommonUtil.getSummaryThousandList(mast,mastTable, 4);
            map.put("page", page);
            map.put("sort", sort);
            map.put("pageSize", pageSize);
            map.put("columnBtnsList", columnList);
            map.put("thousandsField",JsonUtil.getObjectToString(thousandsField));
        }
        //共用
        String pKeyName = appGenModel.getPKeyName();
        map.put("pKeyName", pKeyName);
    }

    /**
     * 封装列表数据
     */
    private void columnData(List<FormAllModel> formAllModel, ColumnDataModel columnDataModel, Map<String, String> tableNameAll, Map<String, Object> map) {
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //显示数据
        List<ColumnListField> columnListAll = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListField.class);
        Map<String, List<FormAllModel>> mastTableList = new HashMap<>();
        Map<String, List<ColumnListField>> childColumnList = new HashMap<>();
        List<ColumnListField> columnMastList = new ArrayList<>();
        for (ColumnListField columnList : columnListAll) {
            String prop = columnList.getProp();
            FormAllModel model = mastTable.stream().filter(t -> t.getFormMastTableModel().getVModel().equals(prop)).findFirst().orElse(null);
            if (model == null) {
                String[] split = prop.split("-");
                if (split.length == 1) {
                    columnMastList.add(columnList);
                } else {
                    List<ColumnListField> childList = childColumnList.get(split[0]) != null ? childColumnList.get(split[0]) : new ArrayList<>();
                    String vModel = split[1];
                    columnList.setVModel(vModel);
                    childList.add(columnList);
                    childColumnList.put(split[0], childList);
                }
            } else {
                FormMastTableModel formMastTableModel = model.getFormMastTableModel();
                String tableName = formMastTableModel.getTable();
                List<FormAllModel> columnListList = mastTableList.get(tableName) != null ? mastTableList.get(tableName) : new ArrayList<>();
                PropsModel propsModel = formMastTableModel.getMastTable().getFieLdsModel().getProps();
                if (propsModel != null) {
                    PropsBeanModel props = JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class);
                    propsModel.setPropsModel(props);
                }
                formMastTableModel.getMastTable().getFieLdsModel().setProps(propsModel);
                model.setFormMastTableModel(formMastTableModel);
                columnListList.add(model);
                mastTableList.put(tableName, columnListList);
            }
        }
        map.put("childColumnList", childColumnList);
        map.put("columnList", columnMastList);
        map.put("columnMastList", mastTableList);
        map.put("AppColumnList", columnDataModel.getColumnList());
        //排序
        List<ColumnListField> sortListAll = JsonUtil.getJsonToList(columnDataModel.getSortList(), ColumnListField.class);
        List<ColumnListField> sortList = new ArrayList<>();
        for (int i = 0; i < sortListAll.size(); i++) {
            ColumnListField columnList = sortListAll.get(i);
            String prop = columnList.getProp();
            FormAllModel model = mast.stream().filter(t -> prop.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                sortList.add(columnList);
            }
        }
        map.put("sortList", sortList);
        boolean isSidx = sortList.stream().filter(t -> t.getProp().equals(columnDataModel.getDefaultSidx())).count() > 0;
        if (isSidx) {
            map.put("defaultSidx", columnDataModel.getDefaultSidx());
        }
        //搜索
        List<FieLdsModel> searchVOList = JsonUtil.getJsonToList(columnDataModel.getSearchList(), FieLdsModel.class);
        List<FieLdsModel> mastTableSearch = new ArrayList<>();
        List<FieLdsModel> childSearch = new ArrayList<>();
        List<FieLdsModel> mastSearch = new ArrayList<>();
        List<Map<String, Object>> searchAll = new LinkedList<>();
        for (FieLdsModel columnSearch : searchVOList) {
            List<TemplateJsonModel> templateJsonAll = new ArrayList<>();
            templateJsonAll.addAll(columnSearch.getConfig().getTemplateJson());
            List<TemplateJsonModel> templateJsonModelList = JsonUtil.getJsonToList(columnSearch.getTemplateJson(), TemplateJsonModel.class);
            templateJsonAll.addAll(templateJsonModelList);
            columnSearch.getConfig().setTemplateJson(templateJsonAll);
            Map<String, Object> column = new HashMap<>();
            String vmodel = columnSearch.getVModel();
            boolean isMast = mast.stream().filter(t -> vmodel.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).count() > 0;
            boolean isMastTable = mastTable.stream().filter(t -> vmodel.equals(t.getFormMastTableModel().getVModel())).count() > 0;
            if (isMast) {
                column.put("key", "mastSearch");
                mastSearch.add(columnSearch);
            } else if (isMastTable) {
                column.put("key", "mastTableSearch");
                mastTableSearch.add(columnSearch);
            } else {
                columnSearch.setVModel(columnSearch.getVModel().replaceAll("-", "_"));
                column.put("key", "childSearch");
                childSearch.add(columnSearch);
            }
            column.put("html", columnSearch);
            searchAll.add(column);
        }
        map.put("searchAll", searchAll);
        map.put("searchList", mastTableSearch);
        map.put("childSearch", childSearch);
        map.put("mastsearchList", mastSearch);
        map.put("useDataPermission", columnDataModel.getUseDataPermission() != null ? columnDataModel.getUseDataPermission() : false);
        map.put("useBtnPermission", columnDataModel.getUseBtnPermission() != null ? columnDataModel.getUseBtnPermission() : false);
        map.put("useFormPermission", columnDataModel.getUseFormPermission() != null ? columnDataModel.getUseFormPermission() : false);
        map.put("useColumnPermission", columnDataModel.getUseColumnPermission() != null ? columnDataModel.getUseColumnPermission() : false);
    }


    //----------------------------代码-------------------------------------------------------

    /**
     * 生成表集合
     *
     * @param appGenModel 对象
     * @throws SQLException
     */
    public void generate(AppGenModel appGenModel) throws SQLException {
        VisualdevEntity entity = appGenModel.getEntity();
        List<TableModel> list = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        DownloadCodeForm downloadCodeForm = appGenModel.getDownloadCodeForm();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        Map<String, String> tableNameAll = this.tableName(tableModelList, downloadCodeForm);
        //生成代码
        for (TableModel model : list) {
            String table = model.getTable();
            appGenModel.setTable(table);
            if ("1".equals(model.getTypeId())) {
                appGenModel.setClassName(downloadCodeForm.getClassName());
                this.setCode(appGenModel);
            } else if ("0".equals(model.getTypeId())) {
                String name = tableNameAll.get(table);
                String className = name.substring(0, 1).toUpperCase() + name.substring(1);
                appGenModel.setClassName(className);
                this.childTable(appGenModel);
            }
        }
    }

    /**
     * 生成主表
     *
     * @param appGenModel 对象
     * @throws SQLException
     */
    private void setCode(AppGenModel appGenModel) throws SQLException {
        DownloadCodeForm downloadCodeForm = appGenModel.getDownloadCodeForm();
        //tableJson
        Map<String, Object> columndata = new HashMap<>(16);
        String className = downloadCodeForm.getClassName().substring(0, 1).toUpperCase() + downloadCodeForm.getClassName().substring(1);
        Template7Model model = this.templateModel(appGenModel, className);
        this.columData(columndata, appGenModel, model);
        DbLinkEntity linkEntity = appGenModel.getLinkEntity();
        columndata.put("DSId", linkEntity != null ? linkEntity.getId() : "master");
        try {
            // 判断当前数据库类型
            columndata.put("dbType", DbTypeUtil.getDb(appGenModel.getDataSourceUtil()).getJnpfDbEncode());
        } catch (DataException e) {
            e.printStackTrace();
        }
        this.javaGenerate(columndata, model, appGenModel, true);
    }

    /**
     * 生成子表
     *
     * @param appGenModel 封装对象
     */
    private void childTable(AppGenModel appGenModel) {
        Template7Model model = this.templateModel(appGenModel, appGenModel.getClassName());
        Map<String, Object> columndata = new HashMap<>(16);
        columndata.put("genInfo", model);
        DbLinkEntity linkEntity = appGenModel.getLinkEntity();
        columndata.put("DSId", linkEntity != null ? linkEntity.getId() : "master");
        this.javaGenerate(columndata, model, appGenModel, false);
    }

    /**
     * 封装数据
     *
     * @param appGenModel
     * @param className
     * @return
     */
    private Template7Model templateModel(AppGenModel appGenModel, String className) {
        DownloadCodeForm downloadCodeForm = appGenModel.getDownloadCodeForm();
        Template7Model template7Model = new Template7Model();
        template7Model.setClassName(className);
        template7Model.setServiceDirectory(appGenModel.getServiceDirectory());
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
    private Map<String, String> forDataMode(AppGenModel appGenModel, List<FormAllModel> formAllModel) {
        VisualdevEntity entity = appGenModel.getEntity();
        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        RecursionForm recursionForm = new RecursionForm(list, tableModelList);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        Map<String, String> tableNameAll = this.tableName(tableModelList, appGenModel.getDownloadCodeForm());
        return tableNameAll;
    }

    /**
     * 封装数据
     *
     * @param columndata
     * @param model
     * @param appGenModel
     * @param isMast
     */
    private void javaGenerate(Map<String, Object> columndata, Template7Model model, AppGenModel appGenModel, boolean isMast) {
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
        UserInfo userInfo = appGenModel.getUserInfo();
        DbLinkEntity linkEntity = appGenModel.getLinkEntity();

        mpg.setDataSource(SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        String table = appGenModel.getTable();
        strategy.setInclude(table);
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("jnpf");
        if (columndata.get("isForm") != null) {
            pc.setParent("jnpf." + columndata.get("isForm"));
        }
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
        String templatePath = this.tempPath(appGenModel);
        String fileName = appGenModel.getFileName();
        String path = appGenModel.getTemplateCodePath();
        if (isMast) {
            focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Controller.java.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return javaPath + fileName + File.separator + "java" + File.separator + "controller" + File.separator + tableInfo.getControllerName() + StringPool.DOT_JAVA;
                }
            });
        }
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
                return javaPath + fileName + File.separator + "java" + File.separator + "entity" + File.separator + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Mapper.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "resources" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "Service.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + tableInfo.getServiceName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig(templatePath + File.separator + "java" + File.separator + "ServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + "impl" + File.separator + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
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
    private void mastTable(List<FormAllModel> formAllModel, Map<String, Object> columndata, AppGenModel appGenModel, Map<String, String> tableNameAll) throws SQLException {
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        Map<String, List<FormAllModel>> mastListAll = mastTable.stream().collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
        DbLinkEntity linkEntity = appGenModel.getLinkEntity();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(appGenModel.getEntity().getVisualTables(), TableModel.class);
        List<Map<String, Object>> mastTableNameAll = new ArrayList<>();
        for (String mastkey : mastListAll.keySet()) {
            Map<String, Object> childMap = new HashMap<>();
            String mastTableName = tableNameAll.get(mastkey);
            String className = mastTableName.substring(0, 1).toUpperCase() + mastTableName.substring(1);
            TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equals(mastkey)).findFirst().orElse(null);
            if (tableModel != null) {
                //获取主表主键
                String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
                String tableField = tableModel.getTableField().trim().replaceAll(":\"f_", ":\"");
                childMap.put("tableField", tableField);
                String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_", ":\"");
                childMap.put("relationField", relationField);
                childMap.put("className", className);
                String keyName = chidKeyName.trim().toLowerCase().replaceAll("f_", "");
                childMap.put("chidKeyName", keyName);
                childMap.put("childList", mastListAll.get(mastkey));
                childMap.put("table", mastkey);
                mastTableNameAll.add(childMap);
            }
        }
        columndata.put("tableNameAll", mastTableNameAll);
    }

    /**
     * 封装数据
     *
     * @param formAllModel
     * @param child
     * @param appGenModel
     * @throws SQLException
     */
    private void child(List<FormAllModel> formAllModel, List<Map<String, Object>> child, AppGenModel appGenModel, Map<String, String> tableNameAll) throws SQLException {
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        DbLinkEntity linkEntity = appGenModel.getLinkEntity();
        List<TableModel> tableModelList = JsonUtil.getJsonToList(appGenModel.getEntity().getVisualTables(), TableModel.class);
        for (FormAllModel tableModelAll : table) {
            FormColumnTableModel childList = tableModelAll.getChildList();
            String childTableName = childList.getTableName();
            String name = tableNameAll.get(childTableName);
            List<FormColumnModel> columnList = childList.getChildList();
            for (int i = 0; i < columnList.size(); i++) {
                FormColumnModel columnModel = columnList.get(i);
                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                String model = fieLdsModel.getVModel();
                String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
                if (StringUtil.isEmpty(model)) {
                    columnList.remove(i);
                    i--;
                }
            }
            childList.setChildList(columnList);
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            String childClassName = name.substring(0, 1).toUpperCase() + name.substring(1);
            TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equals(childList.getTableName())).findFirst().orElse(null);
            if (tableModel != null) {
                //获取主表主键
                String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
                String tableField = tableModel.getTableField().trim().replaceAll(":\"f_", ":\"");
                childs.put("tableField", tableField);
                String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_", ":\"");
                childs.put("relationField", relationField);
                childs.put("className", childClassName);
                String keyName = chidKeyName.trim().toLowerCase().replaceAll("f_", "");
                childs.put("chidKeyName", keyName);
                child.add(childs);
            }
        }
    }

    /**
     * 封装数据
     *
     * @param columndata
     * @param appGenModel
     * @param template7Model
     * @throws SQLException
     */
    private void columData(Map<String, Object> columndata, AppGenModel appGenModel, Template7Model template7Model) throws SQLException {
        VisualdevEntity entity = appGenModel.getEntity();
        List<FormAllModel> formAllModel = new ArrayList<>();
        Map<String, String> tableNameAll = this.forDataMode(appGenModel, formAllModel);
        //主表数据
        List<FieLdsModel> system = new ArrayList<>();
        this.system(formAllModel, system);
        //子表的属性
        List<Map<String, Object>> child = new ArrayList<>();
        this.child(formAllModel, child, appGenModel, tableNameAll);
        //表单子表
        this.mastTable(formAllModel, columndata, appGenModel, tableNameAll);

        boolean isPage = this.isType(appGenModel);
        if (isPage) {
            String columnData = entity.getColumnData().trim().replaceAll(":\"f_", ":\"");
            ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(columnData, ColumnDataModel.class);
            //分页
            columndata.put("sort", columnDataModel.getSort());
            columndata.put("page", columnDataModel.getHasPage() ? "0" : "1");
            columnData(formAllModel, columnDataModel, tableNameAll, columndata);
        }
        DownloadCodeForm downloadCodeForm = appGenModel.getDownloadCodeForm();
        columndata.put("genInfo", template7Model);
        columndata.put("areasName", downloadCodeForm.getModule());
        columndata.put("modelName", template7Model.getClassName());
        columndata.put("typeId", 1);
        columndata.put("system", system);
        columndata.put("child", child);
        String pKeyName = appGenModel.getPKeyName();
        columndata.put("pKeyName", pKeyName);
        columndata.put("isModel", "true");
        String modelPathName = downloadCodeForm.getClassName().substring(0, 1).toLowerCase() + downloadCodeForm.getClassName().substring(1);
        columndata.put("modelPathName", modelPathName);
    }


    private String tempPath(AppGenModel appGenModel) {
        String tempPath = appGenModel.getTemplatePath();
        VisualdevEntity entity = appGenModel.getEntity();
        if (VisualWebTypeEnum.FORM.getType().equals(entity.getWebType())) {
            tempPath = entity.getEnableFlow() == 1 ? "TemplateCode5" : "TemplateCode4";
        } else if (VisualWebTypeEnum.FORM_LIST.getType().equals(entity.getWebType())) {
            tempPath = entity.getEnableFlow() == 1 ? "TemplateCode3" : "TemplateCode2";
        }
        return tempPath;
    }

    private boolean isType(AppGenModel appGenModel) {
        VisualdevEntity entity = appGenModel.getEntity();
        boolean type = !VisualWebTypeEnum.FORM.getType().equals(entity.getWebType());
        return type;
    }

    private boolean isForm(AppGenModel appGenModel) {
        VisualdevEntity entity = appGenModel.getEntity();
        boolean type = (VisualWebTypeEnum.FORM_LIST.getType().equals(entity.getWebType()) && entity.getEnableFlow() == 0);
        return type;
    }


}
