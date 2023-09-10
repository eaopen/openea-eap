package org.openea.eap.extj.generater.util.functionForm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.DbType;
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
import org.openea.eap.extj.base.model.print.PrintOption;
import org.openea.eap.extj.base.model.template6.AuthorityModel;
import org.openea.eap.extj.base.model.template6.BtnData;
import org.openea.eap.extj.base.util.SourceUtil;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.base.util.common.DataControlUtils;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.FileTypeConstant;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.generater.model.FormDesign.ColumnChildListModel;
import org.openea.eap.extj.generater.model.FormDesign.ColumnListDataModel;
import org.openea.eap.extj.generater.model.FormDesign.ColumnListModel;
import org.openea.eap.extj.generater.model.FormDesign.ListSearchGroupModel;
import org.openea.eap.extj.generater.model.GenBaseInfo;
import org.openea.eap.extj.generater.model.GenFileNameSuffix;
import org.openea.eap.extj.generater.model.SearchTypeModel;
import org.openea.eap.extj.generater.model.Template7Model;
import org.openea.eap.extj.generater.util.common.CodeGenerateUtil;
import org.openea.eap.extj.generater.util.common.FormCommonUtil;
import org.openea.eap.extj.generater.util.common.FunctionFormPublicUtil;
import org.openea.eap.extj.generater.util.common.SuperQueryUtil;
import org.openea.eap.extj.generater.util.custom.CustomGenerator;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
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
import org.openea.eap.extj.model.visualJson.FieLdsModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 列表表单
 *
 *
 * @Description 功能列表
 */
public class FormListUtil implements CodeGenerateUtil {

    private static FormListUtil formListUtil = new FormListUtil();


    private FormListUtil() {

    }

    public static FormListUtil getFormListUtil() {
        return formListUtil;
    }


    //------------------------------------界面----------------------------------

    /**
     * 生成主表
     *
     * @param dataSourceUtil   数据源
     * @param path             路径
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param entity           实体
     * @param userInfo         用户
     */
    private static void setCode(DataSourceUtil dataSourceUtil, String path, String fileName, String templatePath, DownloadCodeForm downloadCodeForm, VisualdevEntity entity,
                                UserInfo userInfo, DbLinkEntity linkEntity) throws SQLException {
        //自定义包名
        String modulePackageName = downloadCodeForm.getModulePackageName();

        //tableJson
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //主表
        TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

        //获取主表主键
        String pKeyName = VisualUtils.getpKey(linkEntity, mainTable.getTable()).toLowerCase().trim().replaceAll("f_", "");
        pKeyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pKeyName);

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
        model.setServiceDirectory(FormCommonUtil.getPath(FileTypeConstant.CODETEMP));
        model.setCreateDate(DateUtil.daFormat(new Date()));
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(downloadCodeForm.getDescription());
        //pc列表对象
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
        //app 列表对象
        ColumnDataModel appColumnDataModel = JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class);
        //按钮
        List<BtnData> btnDataList = JsonUtil.getJsonToList(columnDataModel.getBtnsList(), BtnData.class);
        List<BtnData> columnBtnDataList = JsonUtil.getJsonToList(columnDataModel.getColumnBtnsList(), BtnData.class);
        //是否有导入按钮
        boolean hasUploadBtn = btnDataList.stream().anyMatch(btn -> btn.getValue().equals("upload"));

        List<ColumnListField> columnList = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListField.class);
        List<FieLdsModel> searchList = JsonUtil.getJsonToList(columnDataModel.getSearchList(), FieLdsModel.class);

        //权限
        AuthorityModel authority = new AuthorityModel();
        BeanUtil.copyProperties(columnDataModel, authority);

        //取对应表的别名
        Map<String, String> tableNameRenames = FunctionFormPublicUtil.tableNameRename(downloadCodeForm, tableModelList);

        //子表集合
        List<TableModel> childTableNameList = new ArrayList<>();
        //全部表
        List<TableModel> allTableNameList = new ArrayList<>();

        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);

        List<FieLdsModel> tableModelFields = new ArrayList<>();
        FunctionFormPublicUtil.getTableModels(list, tableModelFields);

        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //主表数据
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //列表子表数据
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<String> subTableName = new ArrayList<>();
        for (FormAllModel allModel : table) {
            FormColumnTableModel childList = allModel.getChildList();
            if (childList != null) {
                subTableName.add(childList.getTableName());
            }
        }
        for (TableModel tableModel : tableModelList) {
            TableModel Model = new TableModel();
            Model.setInitName(tableModel.getTable());
            Model.setTable(tableNameRenames.get(tableModel.getTable()));
            Model.setTableField(DataControlUtils.captureName(tableModel.getTableField()));
            if (Model.getTable().equals(mainTable.getTable())) {
                Model.setTableTag("main");
            } else {
                Model.setTableTag(subTableName.contains(Model.getTable()) ? "sub" : "sub-jnpf");
            }
            allTableNameList.add(Model);
            if ("0".equals(tableModel.getTypeId())) {
                childTableNameList.add(Model);
            }
        }
        //子表（tableField,tableName）
        Map<String, String> childKeyTableNameMap = new HashMap<>(8);
        table.stream().forEach(t -> childKeyTableNameMap.put(t.getChildList().getTableModel(), t.getChildList().getTableName()));

        String treeRelationField = columnDataModel.getTreeRelation().replaceAll("-", "_");

        //查询条件
        List<String> jnpfkey = new ArrayList() {{
            add(JnpfKeyConsts.COM_INPUT);
            add(JnpfKeyConsts.TEXTAREA);
        }};
        List<SearchTypeModel> searchTypeModelList = new ArrayList<>();
        searchList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            String vModel = fieLdsModel.getVModel();
            vModel = vModel.replaceAll("-", "_");
            searchTypeModel.setVModel(vModel);
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            searchTypeModel.setMultiple((String.valueOf(fieLdsModel.getSearchMultiple())));
            searchTypeModelList.add(searchTypeModel);
        });
        TableModel mainTableModel = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        if (columnDataModel.getType() == 2) {
            SearchTypeModel searchTypeModel = searchTypeModelList.stream().filter(t -> t.getVModel().equals(treeRelationField)).findFirst().orElse(null);
            if (searchTypeModel == null) {
                searchTypeModel = new SearchTypeModel();
                searchTypeModel.setVModel(treeRelationField);
                searchTypeModel.setSearchType(2);
                if (columnDataModel.getTreeDataSource().equals("organize")) {
                    searchTypeModel.setJnpfKey(JnpfKeyConsts.COMSELECT);
                    searchTypeModel.setSearchType(1);
                }
                searchTypeModel.setLabel("tree");
                searchTypeModel.setTableName(mainTableModel.getTable());
                searchTypeModelList.add(searchTypeModel);
            }
        }

        List<ListSearchGroupModel> groupModels = new ArrayList<>();
        if (searchTypeModelList.size() > 0) {
            //主表
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
            //鉴别列表子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";
            searchTypeModelList.stream().filter(s -> s.getVModel().matches(reg)).forEach(cl -> {
                        String s = cl.getVModel();
                        String s1 = s.substring(s.lastIndexOf("jnpf_")).replace("jnpf_", "");
                        String s2 = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_jnpf"));
                        cl.setAfterVModel(s1);
                        cl.setTableName(s2);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(s2)).findFirst().orElse(null);
                        cl.setDataType(tableModel.getFields().stream().filter(t -> t.getField().equalsIgnoreCase(s1)).findFirst().orElse(null).getDataType());
                    }
            );
            //逻辑查询条件
            Map<String, List<SearchTypeModel>> collect = searchTypeModelList.stream().filter(s -> s.getVModel().matches(reg)).collect(Collectors.groupingBy(t -> t.getTableName()));
            groupModels = collect.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        groupModel.setModelName(tableNameRenames.get(c.getKey()));
                        groupModel.setTableName(c.getKey());
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(c.getKey())).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        groupModel.setSearchTypeModelList(c.getValue());
                        return groupModel;
                    }
            ).collect(Collectors.toList());

            //子表
            Map<String, List<SearchTypeModel>> collect1 = searchTypeModelList.stream().filter(s -> s.getVModel().toLowerCase().contains("tablefield"))
                    .collect(Collectors.groupingBy(t -> t.getVModel().substring(0, t.getVModel().lastIndexOf("_"))));
            List<ListSearchGroupModel> collect2 = collect1.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        String tableName = childKeyTableNameMap.get(c.getKey());
                        groupModel.setModelName(tableNameRenames.get(tableName));
                        groupModel.setTableName(tableName);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(tableName)).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        List<SearchTypeModel> value = c.getValue();
                        value.stream().forEach(v -> {
                            String vModel = v.getVModel();
                            String substring = vModel.substring(vModel.lastIndexOf("_") + 1);
                            v.setAfterVModel(substring);
                        });
                        groupModel.setSearchTypeModelList(value);
                        return groupModel;
                    }
            ).collect(Collectors.toList());
            groupModels.addAll(collect2);

            ListSearchGroupModel groupModel = new ListSearchGroupModel();
            groupModel.setSearchTypeModelList(searchTypeModelList.stream().filter(s -> !s.getVModel().matches(reg)
                    && !s.getVModel().toLowerCase().contains("tablefield")).collect(Collectors.toList()));
            groupModel.setTableName(mainTable.getTable());
            groupModel.setModelName(tableNameRenames.get(mainTable.getTable()));
            groupModels.add(groupModel);
        }


        //-----------------------------------------------------app state------------------------------------------
        //查询条件
        List<FieLdsModel> searchAppList = JsonUtil.getJsonToList(appColumnDataModel.getSearchList(), FieLdsModel.class);
        List<SearchTypeModel> searchTypeAppModelList = new ArrayList<>();
        searchAppList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            String vModel = fieLdsModel.getVModel();
            vModel = vModel.replaceAll("-", "_");
            searchTypeModel.setVModel(vModel);
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            searchTypeModel.setMultiple((String.valueOf(fieLdsModel.getSearchMultiple())));
            searchTypeAppModelList.add(searchTypeModel);
        });


        List<ListSearchGroupModel> groupAppModels = new ArrayList<>();
        if (searchTypeAppModelList.size() > 0) {
            //主表
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeAppModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
            //鉴别列表子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";
            searchTypeAppModelList.stream().filter(s -> s.getVModel().matches(reg)).forEach(cl -> {
                        String s = cl.getVModel();
                        String s1 = s.substring(s.lastIndexOf("jnpf_")).replace("jnpf_", "");
                        String s2 = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_jnpf"));
                        cl.setAfterVModel(s1);
                        cl.setTableName(s2);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(s2)).findFirst().orElse(null);
                        cl.setDataType(tableModel.getFields().stream().filter(t -> t.getField().equalsIgnoreCase(s1)).findFirst().orElse(null).getDataType());
                    }
            );
            //副表
            Map<String, List<SearchTypeModel>> collect = searchTypeAppModelList.stream().filter(s -> s.getVModel().matches(reg)).collect(Collectors.groupingBy(t -> t.getTableName()));
            groupAppModels = collect.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        groupModel.setModelName(tableNameRenames.get(c.getKey()));
                        groupModel.setTableName(c.getKey());
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(c.getKey())).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        groupModel.setSearchTypeModelList(c.getValue());
                        return groupModel;
                    }
            ).collect(Collectors.toList());

            //子表
            Map<String, List<SearchTypeModel>> collect1 = searchTypeAppModelList.stream().filter(s -> s.getVModel().toLowerCase().contains("tablefield"))
                    .collect(Collectors.groupingBy(t -> t.getVModel().substring(0, t.getVModel().lastIndexOf("_"))));
            List<ListSearchGroupModel> collect2 = collect1.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        String tableName = childKeyTableNameMap.get(c.getKey());
                        groupModel.setModelName(tableNameRenames.get(tableName));
                        groupModel.setTableName(tableName);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(tableName)).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        List<SearchTypeModel> value = c.getValue();
                        value.stream().forEach(v -> {
                            String vModel = v.getVModel();
                            String substring = vModel.substring(vModel.lastIndexOf("_") + 1);
                            v.setAfterVModel(substring);
                        });
                        groupModel.setSearchTypeModelList(value);
                        return groupModel;
                    }
            ).collect(Collectors.toList());
            groupAppModels.addAll(collect2);

            ListSearchGroupModel groupModel = new ListSearchGroupModel();
            groupModel.setSearchTypeModelList(searchTypeAppModelList.stream().filter(s ->
                    !s.getVModel().matches(reg) && !s.getVModel().toLowerCase().contains("tablefield")).collect(Collectors.toList()));
            groupModel.setTableName(mainTable.getTable());
            groupModel.setModelName(tableNameRenames.get(mainTable.getTable()));
            groupAppModels.add(groupModel);
        }

        //-----------------------------------------------------app end------------------------------------------


        //表单子表模型
        List<ColumnListDataModel> formChildList = new ArrayList<>();

        //列表子表数据model
        Map<String, List<FormAllModel>> groupColumnDataMap = mastTable.stream().collect(Collectors.groupingBy(m -> m.getFormMastTableModel().getTable()));
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

            List<FieLdsModel> collect = allModels.stream().map(all -> all.getFormMastTableModel().getMastTable().getFieLdsModel()).collect(Collectors.toList());
            collect.stream().forEach(c -> {
                PropsModel propsModel = c.getProps();
                if (propsModel != null) {
                    PropsBeanModel props = JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class);
                    propsModel.setPropsModel(props);
                }
                String vmodel = c.getVModel().substring(c.getVModel().lastIndexOf("jnpf_")).replace("jnpf_", "");
                c.setVModel(vmodel);
                c.setProps(propsModel);
                List<TemplateJsonModel> templateJson = c.getConfig().getTemplateJson();
                String json = templateJson.size() > 0 ? JSONObject.toJSONString(JsonUtil.getObjectToString(templateJson))
                        : JSONObject.toJSONString(JsonUtil.getObjectToString(JsonUtil.getJsonToList(c.getTemplateJson(), TemplateJsonModel.class)));
                c.setTemplateJson(json);
            });
            columnListDataModel.setFieLdsModels(collect);
            formChildList.add(columnListDataModel);
        }

        formChildList.stream().forEach(f -> {
            TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(f.getTableName())).findFirst().orElse(null);
            if (ObjectUtil.isNotEmpty(tableModel)) {
                f.setMainKey(tableModel.getRelationField());
                f.setRelationField(tableModel.getTableField());
                f.setMainUpKey(DataControlUtils.captureName(tableModel.getRelationField()));
                f.setRelationUpField(DataControlUtils.captureName(tableModel.getTableField()));
                String tableKey = tableModel.getTableKey().toLowerCase().replace("f_", "");
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
                List<TemplateJsonModel> templateJson = fieLdsModel.getConfig().getTemplateJson();
                String json = templateJson.size() > 0 ? JSONObject.toJSONString(JsonUtil.getObjectToString(templateJson))
                        : JSONObject.toJSONString(JsonUtil.getObjectToString(JsonUtil.getJsonToList(fieLdsModel.getTemplateJson(), TemplateJsonModel.class)));
                fieLdsModel.setTemplateJson(json);
                system.add(fieLdsModel);
            }
        }
        //详情 主表
        List<FieLdsModel> mainDeatail = mast.stream().map(mas -> mas.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());

        //子表的属性
        List<Map<String, Object>> child = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            FormColumnTableModel childList = table.get(i).getChildList();
            List<FormColumnModel> childListAll = childList.getChildList();
            for (FormColumnModel columnModel : childListAll) {
                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                List<TemplateJsonModel> templateJson = fieLdsModel.getConfig().getTemplateJson();
                String json = templateJson.size() > 0 ? JSONObject.toJSONString(JsonUtil.getObjectToString(templateJson))
                        : JSONObject.toJSONString(JsonUtil.getObjectToString(JsonUtil.getJsonToList(fieLdsModel.getTemplateJson(), TemplateJsonModel.class)));
                fieLdsModel.setTemplateJson(json);
            }
            String className = DataControlUtils.captureName(tableNameRenames.get(childList.getTableName()));
            childList.setAliasClassName(className);
            Map<String, Object> childs = JsonUtil.entityToMap(childList);
            Optional<TableModel> first1 = tableModelList.stream().filter(t -> t.getTable().equals(childList.getTableName())).findFirst();
            if (!first1.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first1.get();
            //获取主表主键
            String chidKeyName = VisualUtils.getpKey(linkEntity, tableModel.getTable());
            String tableField = tableModel.getTableField().trim().replaceAll(":\"f_", ":\"");
            childs.put("tableField", tableField);
            String relationField = tableModel.getRelationField().trim().replaceAll(":\"f_", ":\"");
            childs.put("relationField", relationField);
            childs.put("className", className);
            String chidKeyField = chidKeyName.trim().toLowerCase().replaceAll("f_", "");
            chidKeyField = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, chidKeyField);
            childs.put("chidKeyName", chidKeyField);
            child.add(childs);
        }

        //导入字段
        List<FieLdsModel> allUploadTemplates = new ArrayList<>();
        boolean imHasChildren = false;
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        if (hasUploadBtn && uploaderTemplateModel != null && uploaderTemplateModel.getSelectKey() != null) {
            List<ColumnChildListModel> cols = new ArrayList<>();
            for (String upload : uploaderTemplateModel.getSelectKey()) {
                ColumnChildListModel columnChildListModel = new ColumnChildListModel();
                String tableField;
                String vModel;
                ColumnListField field = columnList.stream().filter(c -> c.getProp().equals(upload)).findFirst().orElse(new ColumnListField());
                String label = field.getConfig() != null ? field.getConfig().getLabel() : "";
                if (upload.toLowerCase().startsWith("tablefield")) {
                    tableField = upload.substring(0, upload.indexOf("-"));
                    vModel = upload.substring(upload.indexOf("-") + 1);
                    imHasChildren = true;
                } else if (upload.toLowerCase().startsWith("jnpf_")) {
                    List<FieLdsModel> masField = mastTable.stream().map(m -> m.getFormMastTableModel().getMastTable().getFieLdsModel()).collect(Collectors.toList());
                    FieLdsModel masF = masField.stream().filter(mas ->
                            upload.substring(upload.lastIndexOf("jnpf_") + 5)
                                    .equals(mas.getVModel())
                    ).findFirst().orElse(new FieLdsModel());
                    TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equals(masF.getConfig().getTableName())).findFirst().orElse(null);
                    masF.setRelationTableForeign(tableModel.getTableField());
                    masF.setMainTableId(tableModel.getRelationField());
                    String name = tableModel.getTableKey().toLowerCase().replaceAll("f_", "");
                    masF.setChildMainKey(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
                    masF.setTableType(1);
                    masF.setBeforeVmodel(upload);
                    allUploadTemplates.add(masF);
                    tableField = "";
                    vModel = upload;
                } else {
                    tableField = "";
                    vModel = upload;
                    List<FieLdsModel> mastField = mast.stream().map(m -> m.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());
                    FieLdsModel mastF = mastField.stream().filter(mas -> mas.getVModel().equals(upload)).findFirst().orElse(new FieLdsModel());
                    mastF.setTableType(0);
                    allUploadTemplates.add(mastF);
                }
                columnChildListModel.setTableField(tableField);
                columnChildListModel.setLabel(label);
                columnChildListModel.setVModel(vModel);
                cols.add(columnChildListModel);
            }
            Map<String, List<ColumnChildListModel>> improtCollect = cols.stream().filter(col ->
                    StringUtil.isNotEmpty(col.getTableField())).collect(Collectors.groupingBy(ColumnChildListModel::getTableField));
            for (Map.Entry<String, List<ColumnChildListModel>> m : improtCollect.entrySet()) {
                String tableFiled = m.getKey();
                FieLdsModel fieLdsModel = tableModelFields.stream().filter(t -> t.getVModel().equals(tableFiled)).findFirst().orElse(null);
                List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                List<FieLdsModel> chlidFields = new ArrayList<>();
                for (ColumnChildListModel columnChildListModel : m.getValue()) {
                    FieLdsModel fieLdsModel2 = children.stream().filter(ch -> ch.getVModel().equals(columnChildListModel.getVModel())).findFirst().orElse(null);
                    chlidFields.add(fieLdsModel2);
                }
                String tableName1 = fieLdsModel.getConfig().getTableName();
                fieLdsModel.getConfig().setAliasClassName(tableNameRenames.get(tableName1));
                fieLdsModel.getConfig().setChildren(chlidFields);
                TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equals(fieLdsModel.getConfig().getTableName())).findFirst().orElse(null);
                fieLdsModel.setRelationTableForeign(tableModel.getTableField());
                fieLdsModel.setMainTableId(tableModel.getRelationField());
                String tableKey = tableModel.getTableKey();
                String name = tableKey.toLowerCase().replaceAll("f_", "");
                fieLdsModel.setChildMainKey(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
                fieLdsModel.setTableType(2);
                allUploadTemplates.add(fieLdsModel);
            }
        }

        //树形列表参数
        if (columnDataModel.getType() == 5) {
            columnDataModel.setHasPage(false);
        }
        String parentField = StringUtil.isNotEmpty(columnDataModel.getParentField()) ?
                columnDataModel.getParentField() : "";
        if (StringUtil.isNotEmpty(parentField)) {
            parentField = parentField.substring(0, 1).toUpperCase() + parentField.substring(1);
        }
        String subField = StringUtil.isNotEmpty(columnDataModel.getSubField()) ?
                columnDataModel.getSubField() : "";
        if (StringUtil.isNotEmpty(subField)) {
            subField = subField.substring(0, 1).toUpperCase() + subField.substring(1);
        }

        //导出字段属性转换
        List<ColumnListField> listOptions = new ArrayList<>();
        columnList.forEach(item -> {
            ColumnListField columnListField = new ColumnListField();
            BeanUtil.copyProperties(item, columnListField);
            if (item.getVModel().toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {
                columnListField.setTableType(2);
                columnListField.setVModel(item.getVModel().split("-")[1]);
            } else if (item.getVModel().toLowerCase().contains("_jnpf_")) {
                columnListField.setTableType(1);
                columnListField.setVModel(item.getVModel().split("_jnpf_")[1]);
            } else {
                columnListField.setTableType(0);
            }
            if ("static".equals(item.getConfig().getDataType())) {
                columnListField.setOptions(JsonUtil.getObjectToString(item.getOptions()));
                SlotModel slot = item.getSlot();
                if (item.getOptions() != null) {
                    String o1 = JsonUtil.getObjectToString(item.getOptions());
                    columnListField.getConfig().setOptions(o1);
                } else if (slot != null && slot.getOptions() != null) {
                    String o1 = JsonUtil.getObjectToString(item.getSlot().getOptions());
                    columnListField.getConfig().setOptions(o1);
                }
                if (item.getConfig() != null && item.getConfig().getProps() != null) {
                    columnListField.getProps().setProps(item.getConfig().getProps());
                }
                if (item.getJnpfKey().equals(JnpfKeyConsts.CHECKBOX)) {
                    columnListField.setMultiple(true);
                }
                if (item.getJnpfKey().equals(JnpfKeyConsts.CASCADER)) {
                    if (item.getProps().getProps() != null) {
                        columnListField.setMultiple(item.getProps().getProps().isMultiple());
                    }
                }
            }
            listOptions.add(columnListField);
        });

        columndata.put("treeTable", columnDataModel.getType() == 5);
        columndata.put("treeLazyType", true);
        columndata.put("parentField", parentField);
        columndata.put("subField", subField);

        // 是否存在列表子表数据
        columndata.put("hasSub", groupColumnDataMap.size() > 0 ? true : false);

        //后台
        columndata.put("importFields", allUploadTemplates);
        columndata.put("importHasChildren", imHasChildren);
        columndata.put("module", downloadCodeForm.getModule());
        columndata.put("genInfo", model);
        columndata.put("modelName", model.getClassName());
        columndata.put("typeId", 1);
        columndata.put("system", system);
        columndata.put("child", child);
        columndata.put("billNo", billNo);
        columndata.put("pKeyName", pKeyName);
        columndata.put("fieldsSize", system.size());
        columndata.put("btnsList", btnDataList);
        columndata.put("searchList", searchList);
        columndata.put("columnList", columnList);
        columndata.put("listOptions", listOptions);
        columndata.put("hasPage", columnDataModel.getHasPage());
        columndata.put("main", true);
        columndata.put("defaultSidx", columnDataModel.getDefaultSidx());
        columndata.put("sort", columnDataModel.getSort());
        columndata.put("searchTypeModelList", searchTypeModelList);
        columndata.put("columnBtnsList", columnBtnDataList);
        columndata.put("mast", mast);
        columndata.put("childtable", table);
        columndata.put("authority", authority);
        //app pc 数据权限是否开启
        columndata.put("pcDataPermisson", columnDataModel.getUseDataPermission());
        columndata.put("appDataPermisson", appColumnDataModel.getUseDataPermission());
        columndata.put("columnChildren", formChildList);
        columndata.put("groupModels", groupModels);
        columndata.put("groupAppModels", groupAppModels);
        columndata.put("childTableNameList", childTableNameList);
        columndata.put("allTableNameList", allTableNameList);
        columndata.put("mainDeatail", mainDeatail);
        columndata.put("superQuery", columnDataModel.getHasSuperQuery());
        columndata.put("groupTable", columnDataModel.getType() == 3);
        columndata.put("groupField", columnDataModel.getGroupField());
        columndata.put("lineEdit", columnDataModel.getType() == 4);
        columndata.put("formModelName", entity.getFullName());
        //乐观锁
        columndata.put("version", formData.getConcurrencyLock());
        //删除标志
        columndata.put("logicalDelete", formData.getLogicalDelete());
        //雪花
        columndata.put("snowflake", formData.getPrimaryKeyPolicy() == 1);
        //微服务标识
        columndata.put("isCloud", FormCommonUtil.IS_CLOUD);
        //数据源
        if (ObjectUtil.isNotEmpty(linkEntity)) {
            columndata.put("DS", linkEntity.getFullName());
        }

        // 是否存在过滤条件
        columndata.put("hasFilter", false);
        if (columnDataModel != null) {
            List ruleList = JsonUtil.getJsonToList(columnDataModel.getRuleList(), Map.class);
            if (ruleList != null && ruleList.size() > 0) {
                columndata.put("hasFilter", true);
            }
        }
        columndata.put("hasAppFilter", false);
        if (appColumnDataModel != null) {
            List ruleListApp = JsonUtil.getJsonToList(appColumnDataModel.getRuleList(), Map.class);
            if (ruleListApp != null && ruleListApp.size() > 0) {
                columndata.put("hasAppFilter", true);
            }
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
        DataSourceConfig dsc = SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity);
        if (dsc.getDbType().getDb().equalsIgnoreCase(DbType.ORACLE.getDb()) || dsc.getDbType().getDb().equalsIgnoreCase(DbType.KINGBASE_ES.getDb())) {
            String schema = linkEntity != null ? linkEntity.getUserName() : dataSourceUtil.getUserName();
            //oracle kingBase 默认 schema=username
            dsc.setSchemaName(schema);
        }
        mpg.setDataSource(dsc);
        //数据库类型
        columndata.put("dbType", dsc.getDbType().getDb());

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
        columndata.put("modulePackageName", modulePackageName);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = FormCommonUtil.getLocalBasePath() + model.getServiceDirectory();
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Controller.java.vm", "controller", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Entity.java.vm", "entity", formData.getConcurrencyLock()));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.java.vm", "mapper", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.xml.vm", "xml", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Service.java.vm", "service", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "ServiceImpl.java.vm", "impl", false));

        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
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
        String framePath = FormCommonUtil.getCloudPath("-entity", downloadCodeForm);
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
        if (template.contains("extraForm.vue.vm")) {
            className = "extraForm";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("Form.vue.vm")) {
            className = "Form";
            return htmlPath + File.separator + className.toLowerCase() + ".vue";
        }
        if (template.contains("index.vue.vm")) {
            className = "index";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("indexEdit.vue.vm")) {
            className = "index";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("ExportBox.vue.vm")) {
            className = "ExportBox";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("Detail.vue.vm")) {
            className = "Detail";
            return htmlPath + File.separator + className + ".vue";
        }
        if (template.contains("InfoVO.java.vm")) {
            return modelPath + File.separator + className + "InfoVO.java";
        }
        if (template.contains("Form.java.vm")) {
            return modelPath + File.separator + className + "Form.java";
        }
        if (template.contains("ListVO.java.vm")) {
            return modelPath + File.separator + className + "ListVO.java";
        }
        if (template.contains("GroupVO.java.vm")) {
            return modelPath + File.separator + className + "GroupVO.java";
        }
        if (template.contains("Pagination.java.vm")) {
            return modelPath + File.separator + className + "Pagination.java";
        }
        if (template.contains("ExcelVO.java.vm")) {
            return modelPath + File.separator + className + "ExcelVO.java";
        }
        if (template.contains("ExcelErrorVO.java.vm")) {
            return modelPath + File.separator + className + "ExcelErrorVO.java";
        }
        return null;
    }

    /**
     * 界面的模板
     *
     * @param template 模板集合
     * @return
     */
    private static List<String> getTemplates(String template, int type, boolean hasImport) {
        List<String> templates = new ArrayList<>();
        //前端页面
        if (type == 4) {
            templates.add(template + File.separator + "html" + File.separator + "indexEdit.vue.vm");
            templates.add(template + File.separator + "html" + File.separator + "extraForm.vue.vm");
        } else {
            templates.add(template + File.separator + "html" + File.separator + "index.vue.vm");
            templates.add(template + File.separator + "html" + File.separator + "Form.vue.vm");
        }
        templates.add(template + File.separator + "html" + File.separator + "ExportBox.vue.vm");
        templates.add(template + File.separator + "html" + File.separator + "Detail.vue.vm");
        //后端
        templates.add(template + File.separator + "java" + File.separator + "InfoVO.java.vm");
        templates.add(template + File.separator + "java" + File.separator + "Form.java.vm");
        templates.add(template + File.separator + "java" + File.separator + "ListVO.java.vm");
        if (type == 3) {
            templates.add(template + File.separator + "java" + File.separator + "GroupVO.java.vm");
        }
        if (hasImport) {
            templates.add(template + File.separator + "java" + File.separator + "ExcelVO.java.vm");
            templates.add(template + File.separator + "java" + File.separator + "ExcelErrorVO.java.vm");
        }
        templates.add(template + File.separator + "java" + File.separator + "Pagination.java.vm");
        return templates;
    }

    /**
     * 渲染html模板
     *
     * @param path         路径
     * @param object       模板数据
     * @param templatePath 模板路径
     */
    private static void htmlTemplates(String path, Map<String, Object> object, String templatePath, int type, boolean hasImport, DownloadCodeForm downloadCodeForm) throws Exception {
        List<String> templates = getTemplates(templatePath, type, hasImport);
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            try {
                // 渲染模板
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl;
                if (template.contains("index.vue.vm") && type == 4) {
                    String s = template.replace("index", "indexEdit");
                    tpl = Velocity.getTemplate(s, Constants.UTF_8);
                } else {
                    tpl = Velocity.getTemplate(template, Constants.UTF_8);
                }
                tpl.merge(context, sw);
                String className = object.get("className").toString();
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

    public static String getChildrenFileName(String dirNames, String template, String className) {
        String finallyPath = "";
        if (template.contains("Model.java.vm")) {
            finallyPath = dirNames + File.separator + className + "Model.java";
        } else if (template.contains("ListVO.java.vm")) {
            finallyPath = dirNames + File.separator + className + "ListVO.java";
        }
        return finallyPath;
    }

    /**
     * 渲染html模板
     *
     * @param path         路径
     * @param object       模板数据
     * @param templatePath 模板路径
     */
    private static void childrenTemplates(String path, Map<String, Object> object, String templatePath, String className,
                                          DownloadCodeForm downloadCodeForm, Boolean isChild) {
        //是否微服务路径
        String framePath = FormCommonUtil.getCloudPath("-entity", downloadCodeForm);
        String model = downloadCodeForm.getClassName();
        List<String> templates = new ArrayList<>();
        templates.add(templatePath + File.separator + "java" + File.separator + "Model.java.vm");
        if (isChild) {
            templates.add(templatePath + File.separator + "java" + File.separator + "ListVO.java.vm");
        }
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            try {
                // 渲染模板
                @Cleanup StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
                tpl.merge(context, sw);
                String dirNames = path + File.separator + "java" + File.separator + framePath + File.separator + "model" + File.separator + model.toLowerCase();
                String fileNames = getChildrenFileName(dirNames, template, className);
                if (fileNames != null) {
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
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("渲染模板失败，表名：" + e);
            }
        }
    }
    //-------------------------代码----------------------------------

    /**
     * 生成子表
     *
     * @param path
     * @param fileName
     * @param templatePath
     * @param entity
     * @param downloadCodeForm
     * @param table
     * @param userInfo
     * @param linkEntity
     * @throws SQLException
     */
    private static void childTable(DataSourceUtil dataSourceUtil, String path, String fileName, String templatePath, VisualdevEntity entity, DownloadCodeForm downloadCodeForm,
                                   String table, UserInfo userInfo, DbLinkEntity linkEntity) throws SQLException {
        //自定义包名
        String modulePackageName = downloadCodeForm.getModulePackageName();
        Map<String, Object> columndata = new HashMap<>(16);
        //tableJson
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //主表
        TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

        //pc列表对象
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
        //app 列表对象
        ColumnDataModel appColumnDataModel = JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class);

        List<FieLdsModel> searchList = JsonUtil.getJsonToList(columnDataModel.getSearchList(), FieLdsModel.class);

        //权限
        AuthorityModel authority = new AuthorityModel();
        BeanUtil.copyProperties(columnDataModel, authority);

        //取对应表的别名
        Map<String, String> tableNameRenames = FunctionFormPublicUtil.tableNameRename(downloadCodeForm, tableModelList);

        //子表集合
        List<TableModel> childTableNameList = new ArrayList<>();
        //全部表
        List<TableModel> allTableNameList = new ArrayList<>();

        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //主表数据
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tables = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //列表子表数据
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());

        String treeRelationField = columnDataModel.getTreeRelation().replaceAll("-", "_");
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
        //子表（tableField,tableName）
        Map<String, String> childKeyTableNameMap = new HashMap<>(8);
        tables.stream().forEach(t -> childKeyTableNameMap.put(t.getChildList().getTableModel(), t.getChildList().getTableName()));
        //查询条件
        List<String> jnpfkey = new ArrayList() {{
            add(JnpfKeyConsts.COM_INPUT);
            add(JnpfKeyConsts.TEXTAREA);
        }};
        List<SearchTypeModel> searchTypeModelList = new ArrayList<>();
        searchList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            String vModel = fieLdsModel.getVModel();
            vModel = vModel.replaceAll("-", "_");
            searchTypeModel.setVModel(vModel);
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            searchTypeModel.setMultiple((String.valueOf(fieLdsModel.getSearchMultiple())));
            searchTypeModelList.add(searchTypeModel);
        });
        TableModel mainTableModel = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        if (columnDataModel.getType() == 2) {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setVModel(treeRelationField);
            searchTypeModel.setSearchType(2);
            if (columnDataModel.getTreeDataSource().equals("organize")) {
                searchTypeModel.setJnpfKey(JnpfKeyConsts.COMSELECT);
                searchTypeModel.setSearchType(1);
            }
            searchTypeModel.setLabel("tree");
            searchTypeModel.setTableName(mainTableModel.getTable());
            searchTypeModelList.add(searchTypeModel);
        }

        List<ListSearchGroupModel> groupModels = new ArrayList<>();
        if (searchTypeModelList.size() > 0) {
            //主表
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
            //鉴别列表子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";
            searchTypeModelList.stream().filter(s -> s.getVModel().matches(reg)).forEach(cl -> {
                        String s = cl.getVModel();
                        String s1 = s.substring(s.lastIndexOf("jnpf_")).replace("jnpf_", "");
                        String s2 = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_jnpf"));
                        cl.setAfterVModel(s1);
                        cl.setTableName(s2);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(s2)).findFirst().orElse(null);
                        cl.setDataType(tableModel.getFields().stream().filter(t -> t.getField().equalsIgnoreCase(s1)).findFirst().orElse(null).getDataType());
                    }
            );
            //副表
            Map<String, List<SearchTypeModel>> collect = searchTypeModelList.stream().filter(s -> s.getVModel().matches(reg)).collect(Collectors.groupingBy(t -> t.getTableName()));
            groupModels = collect.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        groupModel.setModelName(tableNameRenames.get(c.getKey()));
                        groupModel.setTableName(c.getKey());
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(c.getKey())).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        groupModel.setSearchTypeModelList(c.getValue());
                        return groupModel;
                    }
            ).collect(Collectors.toList());

            //子表
            Map<String, List<SearchTypeModel>> collect1 = searchTypeModelList.stream().filter(s -> s.getVModel().toLowerCase().contains("tablefield"))
                    .collect(Collectors.groupingBy(t -> t.getVModel().substring(0, t.getVModel().lastIndexOf("_"))));
            List<ListSearchGroupModel> collect2 = collect1.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        String tableName = childKeyTableNameMap.get(c.getKey());
                        groupModel.setModelName(tableNameRenames.get(tableName));
                        groupModel.setTableName(tableName);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(tableName)).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        List<SearchTypeModel> value = c.getValue();
                        value.stream().forEach(v -> {
                            String vModel = v.getVModel();
                            String substring = vModel.substring(vModel.lastIndexOf("_") + 1);
                            v.setAfterVModel(substring);
                        });
                        groupModel.setSearchTypeModelList(value);
                        return groupModel;
                    }
            ).collect(Collectors.toList());
            groupModels.addAll(collect2);

            ListSearchGroupModel groupModel = new ListSearchGroupModel();
            groupModel.setSearchTypeModelList(searchTypeModelList.stream().filter(s -> !s.getVModel().matches(reg)
                    && !s.getVModel().toLowerCase().contains("tablefield")).collect(Collectors.toList()));
            groupModel.setTableName(mainTable.getTable());
            groupModel.setModelName(tableNameRenames.get(mainTable.getTable()));
            groupModels.add(groupModel);
        }

        //=======app
        //查询条件
        List<FieLdsModel> searchAppList = JsonUtil.getJsonToList(appColumnDataModel.getSearchList(), FieLdsModel.class);
        List<SearchTypeModel> searchTypeAppModelList = new ArrayList<>();
        searchAppList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            String vModel = fieLdsModel.getVModel();
            vModel = vModel.replaceAll("-", "_");
            searchTypeModel.setVModel(vModel);
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            searchTypeModel.setMultiple((String.valueOf(fieLdsModel.getSearchMultiple())));
            searchTypeAppModelList.add(searchTypeModel);
        });


        List<ListSearchGroupModel> groupAppModels = new ArrayList<>();
        if (searchTypeAppModelList.size() > 0) {
            //主表
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeAppModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
            //鉴别列表子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";
            searchTypeAppModelList.stream().filter(s -> s.getVModel().matches(reg)).forEach(cl -> {
                        String s = cl.getVModel();
                        String s1 = s.substring(s.lastIndexOf("jnpf_")).replace("jnpf_", "");
                        String s2 = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_jnpf"));
                        cl.setAfterVModel(s1);
                        cl.setTableName(s2);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(s2)).findFirst().orElse(null);
                        cl.setDataType(tableModel.getFields().stream().filter(t -> t.getField().equalsIgnoreCase(s1)).findFirst().orElse(null).getDataType());
                    }
            );
            //副表
            Map<String, List<SearchTypeModel>> collect = searchTypeAppModelList.stream().filter(s ->
                    s.getVModel().matches(reg)).collect(Collectors.groupingBy(t -> t.getTableName()));
            groupAppModels = collect.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        groupModel.setModelName(tableNameRenames.get(c.getKey()));
                        groupModel.setTableName(c.getKey());
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(c.getKey())).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        groupModel.setSearchTypeModelList(c.getValue());
                        return groupModel;
                    }
            ).collect(Collectors.toList());

            //子表
            Map<String, List<SearchTypeModel>> collect1 = searchTypeAppModelList.stream().filter(s -> s.getVModel().toLowerCase().contains("tablefield"))
                    .collect(Collectors.groupingBy(t -> t.getVModel().substring(0, t.getVModel().lastIndexOf("_"))));
            List<ListSearchGroupModel> collect2 = collect1.entrySet().stream().map(c -> {
                        ListSearchGroupModel groupModel = new ListSearchGroupModel();
                        String tableName = childKeyTableNameMap.get(c.getKey());
                        groupModel.setModelName(tableNameRenames.get(tableName));
                        groupModel.setTableName(tableName);
                        TableModel tableModel = tableModelList.stream().filter(t -> t.getTable().equalsIgnoreCase(tableName)).findFirst().orElse(null);
                        groupModel.setForeignKey(tableModel.getTableField());
                        groupModel.setMainKey(tableModel.getRelationField());
                        List<SearchTypeModel> value = c.getValue();
                        value.stream().forEach(v -> {
                            String vModel = v.getVModel();
                            String substring = vModel.substring(vModel.lastIndexOf("_") + 1);
                            v.setAfterVModel(substring);
                        });
                        groupModel.setSearchTypeModelList(value);
                        return groupModel;
                    }
            ).collect(Collectors.toList());
            groupAppModels.addAll(collect2);

            ListSearchGroupModel groupModel = new ListSearchGroupModel();
            groupModel.setSearchTypeModelList(searchTypeAppModelList.stream().filter(s -> !s.getVModel().matches(reg)
                    && !s.getVModel().toLowerCase().contains("tablefield")).collect(Collectors.toList()));
            groupModel.setTableName(mainTable.getTable());
            groupModel.setModelName(tableNameRenames.get(mainTable.getTable()));
            groupAppModels.add(groupModel);
        }
        //===============================end
        Template7Model model = new Template7Model();
        model.setClassName(DataControlUtils.captureName(tableNameRenames.get(table)));
        model.setServiceDirectory(FormCommonUtil.getPath(FileTypeConstant.CODETEMP));
        model.setCreateDate(DateUtil.daFormat(new Date()));
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(downloadCodeForm.getDescription());


        //后台
        if (columnDataModel.getType() == 5) {
            columnDataModel.setHasPage(false);
        }
        columndata.put("module", downloadCodeForm.getModule());
        columndata.put("genInfo", model);
        columndata.put("modelName", model.getClassName());
        columndata.put("typeId", 1);
        columndata.put("searchList", searchList);
        columndata.put("hasPage", columnDataModel.getHasPage());
        columndata.put("defaultSidx", columnDataModel.getDefaultSidx());
        columndata.put("sort", columnDataModel.getSort());
        columndata.put("searchTypeModelList", searchTypeModelList);
        columndata.put("mast", mast);
        columndata.put("childtable", table);
        columndata.put("authority", authority);
        //app pc 数据权限是否开启
        columndata.put("pcDataPermisson", columnDataModel.getUseDataPermission());
        columndata.put("appDataPermisson", appColumnDataModel.getUseDataPermission());
        columndata.put("groupModels", groupModels);
        columndata.put("groupAppModels", groupAppModels);
        columndata.put("childTableNameList", childTableNameList);
        columndata.put("allTableNameList", allTableNameList);
        columndata.put("superQuery", columnDataModel.getHasSuperQuery());
        TableModel maina = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        String className = DataControlUtils.captureName(tableNameRenames.get(maina.getTable()));
        columndata.put("mainModelName", className);
        columndata.put("isCloud", FormCommonUtil.IS_CLOUD);
        //数据源
        if (ObjectUtil.isNotEmpty(linkEntity)) {
            columndata.put("DS", linkEntity.getFullName());
        }
        columndata.put("genInfo", model);
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
        gc.setEntityName(model.getClassName() + GenFileNameSuffix.ENTITY);
        gc.setMapperName(model.getClassName() + GenFileNameSuffix.MAPPER);
        gc.setXmlName(model.getClassName() + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(model.getClassName() + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(model.getClassName() + GenFileNameSuffix.SERVICEIMPL);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = SourceUtil.dbConfig(TenantDataSourceUtil.getTenantSchema(), linkEntity);
        if (dsc.getDbType().getDb().equalsIgnoreCase(DbType.ORACLE.getDb()) || dsc.getDbType().getDb().equalsIgnoreCase(DbType.KINGBASE_ES.getDb())) {
            String schema = linkEntity != null ? linkEntity.getUserName() : dataSourceUtil.getUserName();
            //oracle kingBase 默认 schema=username
            dsc.setSchemaName(schema);
        }
        mpg.setDataSource(dsc);
        //数据库类型
        columndata.put("dbType", dsc.getDbType().getDb());

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
        columndata.put("modulePackageName", modulePackageName);
        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = FormCommonUtil.getLocalBasePath() + model.getServiceDirectory();
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Entity.java.vm", "entity", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.java.vm", "mapper", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Mapper.xml.vm", "xml", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "Service.java.vm", "service", false));
        focList.add(FormCommonUtil.getFileOutConfig(fileName, templatePath, downloadCodeForm, javaPath, "ServiceImpl.java.vm", "impl", false));

        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
    }

    /**
     * 界面模板
     *
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param model            模型
     * @param templatePath     模板路径
     * @param userInfo         用户
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
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
        List<ColumnListField> columnList = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListField.class);
        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);

        List<FieLdsModel> tableModelFields = new ArrayList<>();
        FunctionFormPublicUtil.getTableModels(list, tableModelFields);

        //form的属性
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        map.put("ableAll", JsonUtil.getListToJsonArray(formAllModel));
        //columnTempJson
        Map<String, Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());

        List<TableModel> tablesList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //列表字段
        List<ColumnListModel> columnListModelList = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListModel.class);
        List<ColumnListModel> notChildColList = columnListModelList.stream().filter(copy -> !copy.getVModel().toLowerCase().startsWith("tablefield")).collect(Collectors.toList());

        //按钮
        List<BtnData> btnDataList = JsonUtil.getJsonToList(columnDataModel.getBtnsList(), BtnData.class);
        boolean hasUploadBtn = btnDataList.stream().anyMatch(btn -> btn.getValue().equals("upload"));

        //列表子表折叠
        List<ColumnListModel> cldColModelList = new ArrayList<>();

        List<String> tablekeys = table.stream().map(t -> t.getChildList().getTableModel()).collect(Collectors.toList());
        Map<String, Integer> keyNum = new HashMap<>();
        for (String key : tablekeys) {
            ColumnListModel columnListModel = columnListModelList.stream().filter(c -> c.getProp().startsWith(key)).findFirst().orElse(null);
            int i = columnListModelList.indexOf(columnListModel);
            keyNum.put(key, i);
        }

        Map<String, List<ColumnListModel>> childFiledLists = columnListModelList.stream().filter(c -> c.getProp().toLowerCase()
                .contains("tablefield")).collect(Collectors.groupingBy(t -> t.getProp().substring(0, t.getProp().lastIndexOf("-"))));
        List<ColumnChildListModel> childModels = new ArrayList<>();
        Iterator<Map.Entry<String, List<ColumnListModel>>> colentries = childFiledLists.entrySet().iterator();
        while (colentries.hasNext()) {
            Map.Entry<String, List<ColumnListModel>> next = colentries.next();
            ColumnListModel listModel = new ColumnListModel();
            ColumnChildListModel mod = new ColumnChildListModel();
            mod.setTableField(next.getKey());
            List<ColumnListModel> value = next.getValue();
            value.get(0).setFirst(true);
            String label = value.get(0).getLabel();
            String substring = label.substring(0, label.lastIndexOf("-"));
            mod.setLabel(substring);
            value.stream().forEach(v -> {
                String prop = v.getProp();
                String valueLabel = v.getLabel();
                String propSub = prop.substring(prop.lastIndexOf("-") + 1);
                String labelSub = valueLabel.substring(valueLabel.lastIndexOf("-") + 1);
                v.setLabel(labelSub);
                v.setProp(propSub);
            });
            mod.setFields(value);
            listModel.setProp(next.getKey());
            listModel.setColumnChildListModel(mod);
            listModel.setJnpfKey(JnpfKeyConsts.CHILD_TABLE);
            childModels.add(mod);
            int i = keyNum.get(next.getKey());
            columnListModelList.set(i, listModel);
            cldColModelList.add(listModel);
        }

        columnListModelList = columnListModelList.stream().filter(c -> {
            boolean b = true;
            if (c.getVModel() != null && c.getVModel().toLowerCase().contains("tablefield")) {
                if (!c.getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)) {
                    b = false;
                }
            }
            return b;
        }).collect(Collectors.toList());

        //取对应表的别名
        Map<String, String> tableNameRenames = FunctionFormPublicUtil.tableNameRename(downloadCodeForm, tablesList);

        List<FieLdsModel> allColumnFiledModelList = new ArrayList<>();
        allColumnFiledModelList.addAll(mast.stream().map(ma -> ma.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList()));
        allColumnFiledModelList.addAll(mastTable.stream().map(mt -> mt.getFormMastTableModel().getMastTable().getFieLdsModel()).collect(Collectors.toList()));
        for (FormAllModel allModel : table) {
            List<FormColumnModel> childList = allModel.getChildList().getChildList();
            for (FormColumnModel formColumnModel : childList) {
                allColumnFiledModelList.add(formColumnModel.getFieLdsModel());
            }
        }
        columnListModelList.stream().forEach(c -> {
            String prop = c.getProp();
            FieLdsModel fieLdsModel = allColumnFiledModelList.stream().filter(f -> prop.equals(f.getVModel())).findFirst().orElse(null);
            if (fieLdsModel != null) {
                PropsBeanModel props = new PropsBeanModel();
                if (fieLdsModel.getProps() != null && fieLdsModel.getProps().getProps() != null) {
//					props.setValue(fieLdsModel.getConfig().getProps().getValue());
//					props.setLabel(fieLdsModel.getConfig().getProps().getLabel());
                    //属性位置改变
                    props.setValue(fieLdsModel.getProps().getProps().getValue());
                    props.setLabel(fieLdsModel.getProps().getProps().getLabel());
                }
                c.setDataType(fieLdsModel.getConfig().getDataType());
                c.setVModel(fieLdsModel.getVModel());
                c.setPropsModel(props);
                c.setMultiple(c.getMultiple() != null ? c.getMultiple() : fieLdsModel.getMultiple());
                //数字组件新增属性
                c.setAddonBefore(fieLdsModel.getAddonBefore());
                c.setAddonAfter(fieLdsModel.getAddonAfter());
                c.setIsAmountChinese(fieLdsModel.getIsAmountChinese());
                c.setThousands(fieLdsModel.isThousands());
                c.setControlsPosition(fieLdsModel.getControlsPosition());
                //时间控件添加字段
                c.setStartTime(fieLdsModel.getStartTime());
                c.setEndTime(fieLdsModel.getEndTime());
                c.setStartRelationField(fieLdsModel.getConfig().getStartRelationField());
                c.setEndRelationField(fieLdsModel.getConfig().getEndRelationField());

                if (fieLdsModel.getSelectType() != null) {
                    c.setSelectType(fieLdsModel.getSelectType());
                    c.setAbleIds(fieLdsModel.getAbleIds());
                    c.setAbleDepIds(fieLdsModel.getAbleDepIds());
                    c.setAbleGroupIds(fieLdsModel.getAbleGroupIds());
                    c.setAblePosIds(fieLdsModel.getAblePosIds());
                    c.setAbleRoleIds(fieLdsModel.getAbleRoleIds());
                    c.setAbleUserIds(fieLdsModel.getAbleUserIds());
                }
            }
        });

        //列表中区别子表正则
        String reg = "^[jnpf_]\\S*_jnpf\\S*";
        columnListModelList.stream().filter(c -> c.getProp().matches(reg)).forEach(
                cl -> {
                    String s = cl.getProp();
                    String s1 = s.substring(s.lastIndexOf("jnpf_")).replace("jnpf_", "");
                    String s2 = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_jnpf"));
                    cl.setNewProp(s2.toLowerCase() + "." + s1);
                    cl.setColumnTableName(s2);
                }
        );

        List<FieLdsModel> searchList = JsonUtil.getJsonToList(columnDataMap.get("searchList"), FieLdsModel.class);

        for (FieLdsModel fieLdsModel : searchList) {
            String vModel = fieLdsModel.getVModel();
            if (vModel.contains("-")) {
                vModel = vModel.replaceAll("-", "_");
            }
            fieLdsModel.setVModel(vModel);
        }

        //查询条件
        Map<String, String> searchAll = new HashMap<>();
        List<SearchTypeModel> searchTypeModelList = new ArrayList<>();
        List<String> jnpfkey = new ArrayList() {{
            add(JnpfKeyConsts.COM_INPUT);
            add(JnpfKeyConsts.TEXTAREA);
        }};
        searchList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            searchTypeModel.setVModel(fieLdsModel.getVModel());
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            String placeholder = fieLdsModel.getSearchType().equals(3) ? fieLdsModel.getPlaceholder()
                    : DataControlUtils.getPlaceholder(fieLdsModel.getConfig().getJnpfKey());
            if (searchTypeModel.getSearchType().equals(3)) {
                searchAll.put(searchTypeModel.getVModel(), searchTypeModel.getLabel());
            }
            searchTypeModel.setPlaceholder(placeholder);
            searchTypeModel.setConfig(fieLdsModel.getConfig());
            searchTypeModel.setShowLevel(fieLdsModel.getShowLevel());
            searchTypeModel.setMultiple(String.valueOf(fieLdsModel.getSearchMultiple()));
            searchTypeModelList.add(searchTypeModel);
        });
        TableModel mainTableModel = tablesList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

        if (searchTypeModelList.size() > 0) {
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
        }
        //--------------------------------------add state---------------------------------------------
        Map<String, Object> columnAppDataMap = JsonUtil.stringToMap(entity.getAppColumnData());
        List<FieLdsModel> searchAppList = JsonUtil.getJsonToList(columnAppDataMap.get("searchList"), FieLdsModel.class);
        for (FieLdsModel fieLdsModel : searchAppList) {
            String vModel = fieLdsModel.getVModel();
            if (vModel.contains("-")) {
                vModel = vModel.replaceAll("-", "_");
            }
            fieLdsModel.setVModel(vModel);
        }
        List<SearchTypeModel> searchTypeAppModelList = new ArrayList<>();
        searchAppList.stream().forEach(fieLdsModel -> {
            SearchTypeModel searchTypeModel = new SearchTypeModel();
            searchTypeModel.setJnpfKey(fieLdsModel.getConfig().getJnpfKey());
            Integer seachType = fieLdsModel.getSearchType();
            if (jnpfkey.contains(searchTypeModel.getJnpfKey()) && seachType.equals(3)) {
                seachType = 2;
            }
            searchTypeModel.setSearchType(seachType);
            searchTypeModel.setVModel(fieLdsModel.getVModel());
            searchTypeModel.setLabel(fieLdsModel.getConfig().getLabel());
            searchTypeModel.setFormat(fieLdsModel.getFormat());
            String placeholder = fieLdsModel.getSearchType().equals(3) ? fieLdsModel.getPlaceholder() : DataControlUtils.getPlaceholder(fieLdsModel.getConfig().getJnpfKey());
            if (searchTypeModel.getSearchType().equals(3)) {
                searchAll.put(searchTypeModel.getVModel(), searchTypeModel.getLabel());
            }
            searchTypeModel.setPlaceholder(placeholder);
            searchTypeModel.setConfig(fieLdsModel.getConfig());
            searchTypeModel.setShowLevel(fieLdsModel.getShowLevel());
            searchTypeModel.setMultiple(String.valueOf(fieLdsModel.getSearchMultiple()));
            searchTypeAppModelList.add(searchTypeModel);
        });

        if (searchTypeAppModelList.size() > 0) {
            for (TableFields tableFields : mainTableModel.getFields()) {
                searchTypeAppModelList.stream().forEach(searchTypeModel -> {
                    if (searchTypeModel.getVModel().equals(tableFields.getField())) {
                        searchTypeModel.setDataType(tableFields.getDataType());
                    }
                });
            }
        }

        List<SearchTypeModel> searchListAll = new ArrayList<>();
        List<String> modelAll = new ArrayList<>();
        for (SearchTypeModel searchTypeModel : searchTypeModelList) {
            if (searchAll.get(searchTypeModel.getVModel()) == null && !modelAll.contains(searchTypeModel.getVModel())) {
                searchListAll.add(searchTypeModel);
                modelAll.add(searchTypeModel.getVModel());
            }
        }
        for (SearchTypeModel searchTypeModel : searchTypeAppModelList) {
            if (searchAll.get(searchTypeModel.getVModel()) == null && !modelAll.contains(searchTypeModel.getVModel())) {
                searchListAll.add(searchTypeModel);
                modelAll.add(searchTypeModel.getVModel());
            }
        }
        for (String key : searchAll.keySet()) {
            SearchTypeModel typeModel = new SearchTypeModel();
            typeModel.setVModel(key);
            typeModel.setSearchType(3);
            typeModel.setLabel(searchAll.get(key));
            searchListAll.add(typeModel);
        }

        String treeRelationField = columnDataModel.getTreeRelation().replaceAll("-", "_");
        //--------------------------------------add end---------------------------------------------

        //--------------------------------------导入----------------------------------------------------
        List<FieLdsModel> allUploadTemplates = new ArrayList<>();
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(columnDataModel.getUploaderTemplateJson(), UploaderTemplateModel.class);
        if (hasUploadBtn && ObjectUtil.isNotNull(uploaderTemplateModel) && uploaderTemplateModel.getSelectKey() != null) {
            List<ColumnChildListModel> cols = new ArrayList<>();
            for (String upload : uploaderTemplateModel.getSelectKey()) {
                ColumnChildListModel columnChildListModel = new ColumnChildListModel();
                String tableField;
                String vModel;
                ColumnListField field = columnList.stream().filter(c -> c.getProp().equals(upload)).findFirst().orElse(new ColumnListField());
                String label = field.getConfig() != null ? field.getConfig().getLabel() : "";
                if (upload.toLowerCase().startsWith("tablefield")) {
                    tableField = upload.substring(0, upload.indexOf("-"));
                    vModel = upload.substring(upload.indexOf("-") + 1);
                } else if (upload.toLowerCase().startsWith("jnpf_")) {
                    FormAllModel thisTableModel = mastTable.stream().filter(mb -> upload.equals(mb.getFormMastTableModel().getVModel())).findFirst().orElse(null);
                    FieLdsModel masF = thisTableModel != null ? thisTableModel.getFormMastTableModel().getMastTable().getFieLdsModel() : new FieLdsModel();
                    if (masF.getConfig().getRegList() != null) {
                        String o1 = JSONObject.toJSONString(JsonUtil.getObjectToString(masF.getConfig().getRegList()));
                        masF.getConfig().setReg(o1);
                    }
                    SlotModel slot = masF.getSlot();
                    if (masF.getOptions() != null) {
                        String o1 = JsonUtil.getObjectToString(masF.getOptions());
                        masF.getConfig().setOptions(o1);
                    } else if (slot != null) {
                        if (slot.getOptions() != null) {
                            String o1 = JsonUtil.getObjectToString(masF.getSlot().getOptions());
                            masF.getConfig().setOptions(o1);
                        }
                    }
                    masF.setTableType(1);
                    masF.setBeforeVmodel(upload);
                    FieLdsModel masFNiuNiu = new FieLdsModel();
                    BeanUtil.copyProperties(masF, masFNiuNiu);
                    masFNiuNiu = DataControlUtils.setAbleIDs(masFNiuNiu);
                    allUploadTemplates.add(masFNiuNiu);
                    tableField = "";
                    vModel = upload;
                } else {
                    tableField = "";
                    vModel = upload;
                    List<FieLdsModel> mastField = mast.stream().map(m -> m.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());
                    FieLdsModel mastF = mastField.stream().filter(mas -> mas.getVModel().equals(upload)).findFirst().orElse(new FieLdsModel());
                    if (mastF.getConfig().getRegList() != null) {
                        String o1 = JSONObject.toJSONString(JsonUtil.getObjectToString(mastF.getConfig().getRegList()));
                        mastF.getConfig().setReg(o1);
                    }
                    SlotModel slot = mastF.getSlot();
                    if (mastF.getOptions() != null) {
                        String o1 = JsonUtil.getObjectToString(mastF.getOptions());
                        mastF.getConfig().setOptions(o1);
                    } else if (slot != null) {
                        if (slot.getOptions() != null) {
                            String o1 = JsonUtil.getObjectToString(mastF.getSlot().getOptions());
                            mastF.getConfig().setOptions(o1);
                        }
                    }
                    mastF.setTableType(0);
                    FieLdsModel masFNiuNiu = new FieLdsModel();
                    BeanUtil.copyProperties(mastF, masFNiuNiu);
                    masFNiuNiu = DataControlUtils.setAbleIDs(masFNiuNiu);
                    allUploadTemplates.add(masFNiuNiu);
                }
                columnChildListModel.setTableField(tableField);
                columnChildListModel.setLabel(label);
                columnChildListModel.setVModel(vModel);
                cols.add(columnChildListModel);
            }
            Map<String, List<ColumnChildListModel>> improtCollect = cols.stream().filter(col ->
                    StringUtil.isNotEmpty(col.getTableField())).collect(Collectors.groupingBy(ColumnChildListModel::getTableField));
            for (Map.Entry<String, List<ColumnChildListModel>> m : improtCollect.entrySet()) {
                String tableFiled = m.getKey();
                FieLdsModel fieLdsModel = tableModelFields.stream().filter(t -> t.getVModel().equals(tableFiled)).findFirst().orElse(null);
                List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                List<FieLdsModel> chlidFields = new ArrayList<>();
                String tableName1 = fieLdsModel.getConfig().getTableName();
                fieLdsModel.getConfig().setAliasClassName(tableNameRenames.get(tableName1));
                for (ColumnChildListModel columnChildListModel : m.getValue()) {
                    FieLdsModel fieLdsModel2 = children.stream().filter(ch -> ch.getVModel().equals(columnChildListModel.getVModel())).findFirst().orElse(null);
                    chlidFields.add(fieLdsModel2);
                }
                fieLdsModel.getConfig().setChildren(chlidFields);
                fieLdsModel.setChildrenSize(chlidFields.size());
                fieLdsModel.setTableType(2);
                allUploadTemplates.add(fieLdsModel);
            }
        }
        //----------------------------------------------------------------导入 end
        if (columnDataModel.getType() == 2) {
            SearchTypeModel searchTypeModel = searchTypeModelList.stream().filter(t -> t.getVModel().equals(treeRelationField)).findFirst().orElse(null);
            if (searchTypeModel == null) {
                searchTypeModel = new SearchTypeModel();
                searchTypeModel.setVModel(treeRelationField);
                searchTypeModel.setSearchType(2);
                if (columnDataModel.getTreeDataSource().equals("organize")) {
                    searchTypeModel.setJnpfKey(JnpfKeyConsts.COMSELECT);
                    searchTypeModel.setSearchType(1);
                }
                searchTypeModel.setLabel("tree");
                searchTypeModel.setTableName(mainTableModel.getTable());
                searchTypeModelList.add(searchTypeModel);
            }
        }

        //表单子表模型
        List<ColumnListDataModel> formChildList = new ArrayList<>();

        //form和model
        Template7Model temModel = new Template7Model();
        temModel.setServiceDirectory(FormCommonUtil.getPath(FileTypeConstant.CODETEMP));
        temModel.setCreateDate(DateUtil.daFormat(new Date()));
        temModel.setCreateUser(GenBaseInfo.AUTHOR);
        temModel.setCopyright(GenBaseInfo.COPYRIGHT);
        temModel.setClassName(DataControlUtils.captureName(downloadCodeForm.getClassName()));
        temModel.setDescription("");
        map.put("genInfo", temModel);
        map.put("modelName", model.getClassName());
        map.put("package", modulePackageName);

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
                        String[] fieldList = templateJsonModel.getRelationField().split("-");
                        if (fieldList.length > 1) {
                            templateJsonModel.setRelationField(sClassName.toLowerCase() + "-" + fieldList[1]);
                        }
                    }
                }
                for (TemplateJsonModel templateJsonModel : templateJsonModelList) {
                    if (StringUtil.isNotEmpty(templateJsonModel.getRelationField())) {
                        String[] fieldList = templateJsonModel.getRelationField().split("-");
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
            childs.put("className", className);
            child.add(childs);
        }
        //主表赋值
        for (int i = 0; i < mast.size(); i++) {
            FieLdsModel fieLdsModel = mast.get(i).getFormColumnModel().getFieLdsModel();
            ConfigModel configModel = fieLdsModel.getConfig();
            if (ObjectUtil.isNotEmpty(fieLdsModel.getProps())) {
                if (ObjectUtil.isNotEmpty(fieLdsModel.getProps().getProps())) {
                    PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                    fieLdsModel.getProps().setPropsModel(propsBeanModel);
                }
            }
            if (configModel.getDefaultValue() instanceof String) {
                configModel.setValueType("String");
            }
            if (configModel.getDefaultValue() == null) {
                configModel.setValueType("undefined");
            }
            fieLdsModel.setConfig(configModel);
            SlotModel slot = fieLdsModel.getSlot();
            String vmodel = fieLdsModel.getVModel();
            if (slot != null) {
                slot.setAppOptions(slot.getOptions());
                fieLdsModel.setSlot(slot);
            }
        }

        for (int i = 0; i < mastTable.size(); i++) {
            FieLdsModel fieLdsModel = mastTable.get(i).getFormMastTableModel().getMastTable().getFieLdsModel();
            if (ObjectUtil.isNotEmpty(fieLdsModel.getProps())) {
                if (ObjectUtil.isNotEmpty(fieLdsModel.getProps().getProps())) {
                    PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                    fieLdsModel.getProps().setPropsModel(propsBeanModel);
                }
            }
        }

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
            List<FormColumnModel> children = formEntries.getValue().stream().map(allModel -> allModel.getFormMastTableModel().getMastTable()).collect(Collectors.toList());
            //表字段给的范围-转换json
            children.forEach(item -> {
                item.setFieLdsModel(DataControlUtils.setAbleIDs(item.getFieLdsModel()));
            });
            FormColumnTableModel formColumnTableModel = new FormColumnTableModel();
            formColumnTableModel.setChildList(children);
            objectMap.put("children", formColumnTableModel);
            objectMap.put("genInfo", temModel);
            objectMap.put("package", modulePackageName);
            objectMap.put("module", model.getAreasName());
            objectMap.put("className", className);
            childrenTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, objectMap, templatePath, className, downloadCodeForm, true);
        }

        //子表model
        for (int i = 0; i < table.size(); i++) {
            FormColumnTableModel childList = table.get(i).getChildList();
            Map<String, Object> objectMap = JsonUtil.entityToMap(childList);
            String className = DataControlUtils.captureName(tableNameRenames.get(childList.getTableName()));
            //开启导入
            if (allUploadTemplates.size() > 0) {
                FieLdsModel fieLdsModel = allUploadTemplates.stream().filter(f -> f.getVModel().equals(childList.getTableModel())).findFirst().orElse(null);
                if (fieLdsModel != null) {
                    List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
                    for (FormColumnModel columnModel : childList.getChildList()) {
                        String vModel = columnModel.getFieLdsModel().getVModel();
                        if (columnModel.getFieLdsModel().getConfig().getRegList() != null) {
                            String o1 = JSONObject.toJSONString(JsonUtil.getObjectToString(columnModel.getFieLdsModel().getConfig().getRegList()));
                            columnModel.getFieLdsModel().getConfig().setReg(o1);
                        }
                        SlotModel slot = columnModel.getFieLdsModel().getSlot();
                        if (columnModel.getFieLdsModel().getOptions() != null) {
                            String o1 = JsonUtil.getObjectToString(columnModel.getFieLdsModel().getOptions());
                            columnModel.getFieLdsModel().getConfig().setOptions(o1);
                        } else if (slot != null) {
                            if (slot.getOptions() != null) {
                                String o1 = JsonUtil.getObjectToString(columnModel.getFieLdsModel().getSlot().getOptions());
                                columnModel.getFieLdsModel().getConfig().setOptions(o1);
                            }
                        }
                        boolean b = children.stream().anyMatch(c -> c.getVModel().equals(vModel));
                        if (b) {
                            columnModel.getFieLdsModel().setNeedImport(true);
                        }
                    }
                }
            }
            //表字段给的范围-转换json
            childList.getChildList().forEach(item -> {
                item.setFieLdsModel(DataControlUtils.setAbleIDs(item.getFieLdsModel()));
            });
            objectMap.put("children", childList);
            objectMap.put("genInfo", temModel);
            objectMap.put("package", modulePackageName);
            objectMap.put("module", model.getAreasName());
            objectMap.put("className", className);
            childrenTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, objectMap, templatePath, className, downloadCodeForm, false);
        }

        mast.stream().forEach(ms -> {
            PropsModel propsModel = ms.getFormColumnModel().getFieLdsModel().getProps();
            if (ObjectUtil.isNotEmpty(propsModel)) {
                propsModel.setPropsModel(JsonUtil.getJsonToBean(propsModel.getProps(), PropsBeanModel.class));
            }
        });
        //界面
        //是否开启行内编辑columnDataListFiled
        map.put("lineEdit", columnDataModel.getType() == 4);
        //是否开启分组表格
        if (columnDataModel.getType() == 3) {
            ColumnListModel columnListModel = columnListModelList.get(0);
            columnListModel.setFirst(true);
            columnListModelList.set(0, columnListModel);
        }

        //判断列表是否存在冻结
        boolean none = columnListModelList.stream().anyMatch(c -> "left".equals(c.getFixed()) || "right".equals(c.getFixed()));
        map.put("hasFixed", none);
        map.put("importFields", allUploadTemplates);
        Set<String> set = new HashSet<>();
        List<String> nameAgain = new ArrayList<>();
        for (FieLdsModel f : allUploadTemplates) {
            if (!set.add(f.getConfig().getLabel())) {
                if (f.getTableType() == 1) {
                    nameAgain.add(f.getVModel());
                } else {
                    nameAgain.add(f.getBeforeVmodel());
                }
            }
        }
        map.put("nameAgain", nameAgain);
        if (uploaderTemplateModel != null) {
            map.put("importIsUpdate", "2".equals(uploaderTemplateModel.getDataType()));
        }
        map.put("groupTable", columnDataModel.getType() == 3);
        //树形同步异步
        if (columnDataModel.getType() == 5) {
            columnDataModel.setHasPage(false);
            columnDataMap.replace("hasPage", false);
        }
        map.put("treeTable", columnDataModel.getType() == 5);
        map.put("treeLazyType", true);
        map.put("parentField", columnDataModel.getParentField());
        map.put("subField", columnDataModel.getSubField());
        //合计
        boolean configurationTotal = columnDataModel.isShowSummary();
        if (columnDataModel.getType() == 3 || columnDataModel.getType() == 5) {
            configurationTotal = false;
        }
        map.put("configurationTotal", configurationTotal);
        List<String> summaryList = FormCommonUtil.getSummaryList(columnDataModel.getSummaryField(), columnDataModel.getType());
        map.put("fieldsTotal", JsonUtil.getObjectToString(summaryList));

        //合计千分位
        List<String> thousandsField = FormCommonUtil.getSummaryThousandList(mast,mastTable, columnDataModel.getType());
        map.put("thousandsField", JsonUtil.getObjectToString(thousandsField));

        //乐观锁
        map.put("version", formData.getConcurrencyLock());
        map.put("logicalDelete", formData.getLogicalDelete());
        map.put("module", downloadCodeForm.getModule());
        map.put("className", DataControlUtils.captureName(model.getClassName()));
        map.put("formRef", model.getFormRef());
        map.put("formModel", model.getFormModel());
        map.put("size", model.getSize());
        map.put("labelPosition", model.getLabelPosition());
        map.put("generalWidth", model.getGeneralWidth());
        map.put("fullScreenWidth", model.getFullScreenWidth());
        map.put("drawerWidth", model.getDrawerWidth());
        map.put("formStyle", model.getFormStyle());
        map.put("labelWidth", model.getLabelWidth());
        map.put("formRules", model.getFormRules());
        map.put("gutter", model.getGutter());
        map.put("disabled", model.getDisabled());
        map.put("span", model.getSpan());
        map.put("formBtns", model.getFormBtns());
        map.put("idGlobal", model.getIdGlobal());
        map.put("popupType", model.getPopupType());
        map.put("form", formAllModel);
        map.put("columnData", columnDataMap);
        map.put("columnDataListFiled", columnListModelList);
        map.put("childModels", childModels);
        map.put("searchTypeList", searchListAll);
        map.put("searchList", searchList);
        map.put("childTableStyle", columnDataModel.getChildTableStyle());
        map.put("cldColModelList", cldColModelList);
        map.put("notChildColList", notChildColList);
        map.put("dataType", columnDataModel.getHasPage() ? 0 : 1);

        //按钮
        map.put("HasCancelBtn", formData.getHasCancelBtn());
        map.put("HasConfirmBtn", formData.getHasConfirmBtn());
        map.put("HasPrintBtn", formData.getHasPrintBtn());
        map.put("CancelButton", formData.getCancelButtonText());
        map.put("ConfirmButton", formData.getConfirmButtonText());
        map.put("PrintButton", formData.getPrintButtonText());
        String[] printId = formData.getPrintId() != null ? formData.getPrintId() : new String[]{};
        // 打印模板如果是有多个需要查询标题
        if (printId.length > 1) {
            List<PrintOption> printOptions = FormCommonUtil.getList(Arrays.asList(printId));
            map.put("printOptions", printOptions);
        }

        // 打印模板批量的标题
        List<String> printIds = JsonUtil.getJsonToList(columnDataMap.get("printIds"), String.class);
        if (printIds != null && printIds.size() > 0) {
            List<PrintOption> printOptions = FormCommonUtil.getList(printIds);
            map.put("printListOptions", printOptions);
        }

        String printIdAll = String.join(",", printId);
        map.put("printId", printIdAll);
        map.put("moduleId", entity.getId());

        map.put("groupField", columnDataModel.getGroupField());
        if (columnDataModel.getType() == 2) {
            map.put("treeRelationField", treeRelationField);
            SearchTypeModel searchTypeModel = searchListAll.stream().filter(t -> t.getVModel().equals(treeRelationField)).findFirst().orElse(null);
            if (searchTypeModel == null) {
                searchTypeModel = new SearchTypeModel();
                searchTypeModel.setVModel(treeRelationField);
                searchTypeModel.setSearchType(2);
                if (columnDataModel.getTreeDataSource().equals("organize")) {
                    searchTypeModel.setJnpfKey(JnpfKeyConsts.COMSELECT);
                    searchTypeModel.setSearchType(1);
                }
                searchTypeModel.setLabel("tree");
                searchTypeModel.setTableName(mainTableModel.getTable());
                map.put("hasTree", true);
                map.put("treeRelation", searchTypeModel);
            }
        }

        //共用
        map.put("children", child);
        map.put("mastTable", mastTable);
        map.put("fields", mast);
        map.put("columnChildren", formChildList);
        pKeyName = pKeyName.toLowerCase().trim().replaceAll("f_", "");
        pKeyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pKeyName);
        map.put("pKeyName", pKeyName);
        map.put("columnList", columnList);
        String modelPathName = downloadCodeForm.getClassName().toLowerCase();
        map.put("modelPathName", modelPathName);
        map.put("formModelName", entity.getFullName());
        map.put("isMain", true);

        //是否开启高级查询
        Boolean hasSuperQuery = columnDataModel.getHasSuperQuery();
        map.put("superQuery", hasSuperQuery);

        htmlTemplates(FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName, map, templatePath, columnDataModel.getType(), hasUploadBtn, downloadCodeForm);

        String path = FormCommonUtil.getLocalBasePath() + model.getServiceDirectory() + fileName;
        if (hasSuperQuery) {
            String superSqJsPath = path + File.separator + "html" + File.separator + "web" + File.separator + modelPathName + File.separator + "superQueryJson.js";
            String data = columnDataModel.getColumnOptions().replaceAll("\\\\", "");
            SuperQueryUtil.CreateJsFile(data, superSqJsPath, "superQueryJson");
        }
        String colData = columnDataModel.getColumnList().replaceAll("\\\\", "");
        String colListJsPath = path + File.separator + "html" + File.separator + "web" + File.separator + modelPathName + File.separator + "columnList.js";
        SuperQueryUtil.CreateJsFile(colData, colListJsPath, "columnList");
    }

    /**
     * 生成表集合
     *
     * @param entity           实体
     * @param dataSourceUtil   数据源
     * @param fileName         文件夹名称
     * @param downloadCodeForm 文件名称
     * @param userInfo         用户
     */
    @Override
    public void generate(VisualdevEntity entity, DataSourceUtil dataSourceUtil, String fileName, String templatePath,
                         DownloadCodeForm downloadCodeForm, UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) throws SQLException {
        List<TableModel> list = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
        //生成代码
        int i = 0;
        for (TableModel model : list) {
            if ("1".equals(model.getTypeId())) {
                setCode(dataSourceUtil, FormCommonUtil.getLocalBasePath() + FormCommonUtil.getPath(FileTypeConstant.TEMPLATECODEPATH),
                        fileName, templatePath, downloadCodeForm, entity, userInfo, linkEntity);
            } else if ("0".equals(model.getTypeId())) {
                childTable(dataSourceUtil, FormCommonUtil.getLocalBasePath() + FormCommonUtil.getPath(FileTypeConstant.TEMPLATECODEPATH),
                        fileName, templatePath, entity, downloadCodeForm, model.getTable(), userInfo, linkEntity);
                i++;
            }
        }
    }


}
