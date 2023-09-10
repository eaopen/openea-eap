package org.openea.eap.extj.form.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.form.VisualTableModel;
import org.openea.eap.extj.model.visualJson.TableFields;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;
import org.openea.eap.extj.model.visualJson.analysis.FormColumnTableModel;
import org.openea.eap.extj.model.visualJson.analysis.FormEnum;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 */
@Slf4j
@Component
public class VisualDevTableCre {
    private static String relationField = "f_id";
    private static String tableField = "f_foreignId";
    @Autowired
    private ServiceBaseUtil serviceUtil;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    /**
     * 表单赋值tableName
     *
     * @param jsonArray
     * @param tableModels
     */
    private void fieldsTableName(JSONArray jsonArray, List<TableModel> tableModels) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String jnpfkey = jsonObject.getJSONObject("__config__").getString("jnpfKey");
            List<String> childrenListAll = new ArrayList(){{
                add(FormEnum.card.getMessage());
                add(FormEnum.row.getMessage());
                add(FormEnum.tab.getMessage());
                add(FormEnum.collapse.getMessage());
                add(FormEnum.collapseItem.getMessage());
                add(FormEnum.tabItem.getMessage());
                add(FormEnum.tableGrid.getMessage());
                add(FormEnum.tableGridTr.getMessage());
                add(FormEnum.tableGridTd.getMessage());
            }};
            if (childrenListAll.contains(jnpfkey) || StringUtil.isEmpty(jnpfkey)) {
                JSONArray childArray = jsonObject.getJSONObject("__config__").getJSONArray("children");
                this.fieldsTableName(childArray, tableModels);
                jsonObject.getJSONObject("__config__" ).put("children" , childArray);
            } else if (FormEnum.table.getMessage().equals(jnpfkey)) {
                JSONArray childrenList = new JSONArray();
                JSONArray children = jsonObject.getJSONObject("__config__" ).getJSONArray("children" );
                String tableModel = "";
                for (int k = 0; k < children.size(); k++) {
                    JSONObject childrenObject = (JSONObject) children.get(k);
                    this.fieldsModel(childrenObject, tableModels);
                    if (StringUtil.isEmpty(tableModel)) {
                        tableModel = childrenObject.getJSONObject("__config__" ).getString("relationTable" );
                    }
                    childrenList.add(childrenObject);
                }
                jsonObject.getJSONObject("__config__" ).put("tableName" , tableModel);
                jsonObject.getJSONObject("__config__" ).put("children" , childrenList);
            } else {
                this.fieldsModel(jsonObject, tableModels);
            }
        }
    }

    /**
     * 赋值table
     *
     * @param jsonObject
     * @param tableModels
     */
    private TableModel fieldsModel(JSONObject jsonObject, List<TableModel> tableModels) {
        String vModel = jsonObject.getString("__vModel__" );
        String relationField = StringUtil.isNotEmpty(jsonObject.getString("relationField" )) ? jsonObject.getString("relationField" ) : "";
        String jnpfkey = jsonObject.getJSONObject("__config__" ).getString("jnpfKey" );
        TableModel tableName = tableModels.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().orElse(null);
        if (tableName != null) {
            jsonObject.getJSONObject("__config__" ).put("tableName" , tableName.getTable());
        }
        List<TableModel> childTableAll = tableModels.stream().filter(t -> "0".equals(t.getTypeId())).collect(Collectors.toList());
        TableModel childTableaa = childTableAll.stream().filter(t -> t.getFields().stream().filter(k -> k.getField().equals(vModel)).count() > 0).findFirst().orElse(null);
        if (childTableaa != null) {
            jsonObject.getJSONObject("__config__" ).put("relationTable" , childTableaa.getTable());
        }
        if (FormEnum.relationFormAttr.getMessage().equals(jnpfkey) || FormEnum.popupAttr.getMessage().equals(jnpfkey)) {
            if (StringUtil.isNotEmpty(relationField)) {
                Boolean isSubTable = jsonObject.getJSONObject("__config__" ).getBooleanValue("isSubTable" );
                String model = relationField.split("_jnpfTable_" )[0];
                jsonObject.put("relationField" , model + "_jnpfTable_" + tableName.getTable() + (isSubTable ? "0" : "1" ));
            }
        }
        return childTableaa;
    }

    /**
     * 创建表
     *
     * @return
     */
    public List<TableModel> tableList(VisualTableModel visualTableModel) throws WorkFlowException {
        //是否启用租户
        boolean isTenant = TenantDataSourceUtil.isTenantColumn();
        JSONArray jsonArray = visualTableModel.getJsonArray();
        List<FormAllModel> formAllModel = visualTableModel.getFormAllModel();
        String table = visualTableModel.getTable();
        String linkId = visualTableModel.getLinkId();
        String fullName = visualTableModel.getFullName();
        int primaryKey = visualTableModel.getPrimaryKey();
        List<TableModel> tableModelList = new LinkedList<>();
        Map<String, String> tableNameList = new HashMap<>();
        DbLinkEntity dbLink = serviceUtil.getDbLink(linkId);
        String type = dbLink != null ? dbLink.getDbType() : dataSourceUtil.getDbType();
        boolean isUpperCase = (DbBase.DM.equals(type) || DbBase.ORACLE.equals(type));
        boolean isLowerCase = (DbBase.POSTGRE_SQL.equals(type) || DbBase.KINGBASE_ES.equals(type));
        table = tableName(table, isUpperCase, isLowerCase);
        relationField = tableName(relationField, isUpperCase, isLowerCase);
        tableField = tableName(tableField, isUpperCase, isLowerCase);
        try {
            List<DbFieldModel> fieldList = new ArrayList<>();
            Map<String, List<DbFieldModel>> tableListAll = new HashMap<>();
            for (FormAllModel model : formAllModel) {
                if (FormEnum.mast.getMessage().equals(model.getJnpfKey())) {
                    FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                    this.fieldList(fieLdsModel, table, fieldList);
                } else if (FormEnum.table.getMessage().equals(model.getJnpfKey())) {
                    String tableName = "ct" + RandomUtil.uuId();
                    FormColumnTableModel fieLdsModel = model.getChildList();
                    List<DbFieldModel> tableList = new ArrayList<>();
                    String tableModel = fieLdsModel.getTableModel();
                    List<FieLdsModel> fieldsList = fieLdsModel.getChildList().stream().map(t -> t.getFieLdsModel()).collect(Collectors.toList());
                    for (FieLdsModel tableFieLdsModel : fieldsList) {
                        this.fieldList(tableFieLdsModel, tableName, tableList);
                    }
                    this.dbTableField(tableList, true, primaryKey);
                    tableNameList.put(tableModel, tableName);
                    tableListAll.put(tableModel, tableList);
                }
            }

            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.FLOWID));
            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.VERSION));
            fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.FLOWTASKID));
            if (isTenant) {//租户字段
                fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.TENANTID));
            }
            if(visualTableModel.getLogicalDelete()) {//删除标志字段
                fieldList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.DELETEMARK));
            }
            this.dbTableField(fieldList, false, primaryKey);
            List<DbTableFieldModel> dbTableList = new ArrayList<>();
            //创建子表
            for (String key : tableListAll.keySet()) {
                String tableName = tableName(tableNameList.get(key), isUpperCase, isLowerCase);
                List<DbFieldModel> datableList = tableListAll.get(key);
                this.tableModel(tableModelList, datableList, tableName, table, true);
                if (isTenant) {
                    datableList.add(ConcurrencyUtils.getDbFieldModel(TableFeildsEnum.TENANTID));
                }
                DbTableFieldModel dbTable = this.dbTable(linkId, tableName, datableList, true, fullName);
                dbTableList.add(dbTable);
            }
            this.tableModel(tableModelList, fieldList, table, table, false);
            DbTableFieldModel dbTable = this.dbTable(linkId, table, fieldList, false, fullName);
            dbTableList.add(dbTable);
            serviceUtil.createTable(dbTableList);
            this.fieldsTableName(jsonArray, tableModelList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("表新增错误:{}" , e.getMessage());
            throw new WorkFlowException("表新增错误:" + e.getMessage());
        }
        return tableModelList;
    }

    /**
     * 表名
     *
     * @param tableName
     * @param isUpperCase
     * @param isLowerCase
     * @return
     */
    private String tableName(String tableName, boolean isUpperCase, boolean isLowerCase) {
        String resultName = isUpperCase ? tableName.toUpperCase() : isLowerCase ? tableName.toLowerCase() : tableName;
        return resultName;
    }

    /**
     * 获取表单字段
     *
     * @param fieLdsModel
     * @param tableList
     */
    private void fieldList(FieLdsModel fieLdsModel, String table, List<DbFieldModel> tableList) {
        String vmodel = fieLdsModel.getVModel();
        String lable = fieLdsModel.getConfig().getLabel();
        String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
        fieLdsModel.getConfig().setTableName(table);
        if (StringUtil.isNotEmpty(vmodel)) {
            DbFieldModel fieldForm = new DbFieldModel();
            fieldForm.setNullSign(DbAliasConst.NULL);
            fieldForm.setDataType("varchar" );
            fieldForm.setLength("255" );
            fieldForm.setIsPrimaryKey(false);
            if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
                List<String> text = new ArrayList() {{
                    add(JnpfKeyConsts.UPLOADIMG);
                    add(JnpfKeyConsts.UPLOADFZ);
                }};
                if (text.contains(jnpfkey)) {
                    fieldForm.setDataType("text" );
                }
                if (jnpfkey.equals(JnpfKeyConsts.EDITOR)) {
                    fieldForm.setDataType("text" );
                }
                List<String> date = new ArrayList() {{
                    add(JnpfKeyConsts.MODIFYTIME);
                    add(JnpfKeyConsts.CREATETIME);
                    add(JnpfKeyConsts.DATE);
                }};
                if (date.contains(jnpfkey)) {
                    fieldForm.setDataType("datetime" );
                }
                if (JnpfKeyConsts.NUM_INPUT.equals(jnpfkey)) {
                    fieldForm.setDataType("decimal");
                    String precision = "0";
                    if (fieLdsModel.getPrecision() != null) {
                        precision=String.valueOf(fieLdsModel.getPrecision());
                    }
                    fieldForm.setLength("65," + precision);
                }
                fieldForm.setField(vmodel);
                fieldForm.setComment(lable);
                tableList.add(fieldForm);
            }
        }
    }

    /**
     * 创建主外键字段
     *
     * @param tableList
     * @param isforeign
     */
    private void dbTableField(List<DbFieldModel> tableList, boolean isforeign, int primaryKey) {
        boolean uuid = primaryKey == 1;
        DbFieldModel tableKey = new DbFieldModel();
        tableKey.setNullSign(DbAliasConst.NOT_NULL);
        tableKey.setDataType(!uuid ? "int" : "varchar" );
        tableKey.setLength("50" );
        tableKey.setIsPrimaryKey(true);
        tableKey.setField(relationField);
        tableKey.setIsAutoIncrement(!uuid);
        tableKey.setComment("主键" );
        tableList.add(tableKey);
        if (isforeign) {
            DbFieldModel tableForeignKey = new DbFieldModel();
            tableForeignKey.setNullSign(DbAliasConst.NULL);
            tableForeignKey.setDataType("varchar" );
            tableForeignKey.setLength("50" );
            tableForeignKey.setIsPrimaryKey(false);
            tableForeignKey.setField(tableField);
            tableForeignKey.setComment("外键" );
            tableList.add(tableForeignKey);
        }
    }

    /**
     * 组装字段list
     *
     * @param tableModelList
     * @param dbtable
     * @param table
     * @param mastTable
     * @param isforeign
     */
    private void tableModel(List<TableModel> tableModelList, List<DbFieldModel> dbtable, String table, String mastTable, boolean isforeign) {
        TableModel tableModel = new TableModel();
        tableModel.setRelationField(isforeign ? relationField : "" );
        tableModel.setRelationTable(isforeign ? mastTable : "" );
        tableModel.setTable(table);
        tableModel.setComment(isforeign ? "子表" : "主表" );
        tableModel.setTableField(isforeign ? tableField : "" );
        tableModel.setTypeId(isforeign ? "0" : "1" );
        tableModel.setFields(JsonUtil.getJsonToList(dbtable, TableFields.class));
        tableModelList.add(tableModel);
    }

    /**
     * 组装创表字段
     *
     * @param linkId
     * @param tableName
     * @param tableFieldList
     * @param isforeign
     * @return
     */
    private DbTableFieldModel dbTable(String linkId, String tableName, List<DbFieldModel> tableFieldList, boolean isforeign, String fullName) {
        DbTableFieldModel dbTable = new DbTableFieldModel();
        dbTable.setDbLinkId(linkId);
        dbTable.setTable(tableName);
        dbTable.setDbFieldModelList(tableFieldList);
        String s = isforeign ? "子表" : "主表";
        if (fullName.contains("&" )) {//自动生成表备注的时候带&符号创建不成功问题
            fullName = fullName.replace("&" , " " );
        }
        dbTable.setComment(String.format("%s-%s" , fullName, s));
        return dbTable;
    }
}
