package org.openea.eap.extj.form.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.GeneralInsertDSL;
import org.mybatis.dynamic.sql.insert.render.GeneralInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.mapper.FlowFormDataMapper;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FlowFormDataUtil {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private ServiceBaseUtil serviceUtil;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;


    /**
     * 返回主键名称
     *
     * @param conn
     * @param mainTable
     * @return
     */
    public String getKey(Connection conn, String mainTable, Integer primaryKeyPolicy) throws SQLException {
        String pKeyName = TableFeildsEnum.FID.getField();
        if (primaryKeyPolicy == 2) {
            pKeyName = TableFeildsEnum.FLOWTASKID.getField();
        } else {
            //catalog 数据库名
            String catalog = conn.getCatalog();
            @Cleanup ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(catalog, null, mainTable);
            while (primaryKeyResultSet.next()) {
                pKeyName = primaryKeyResultSet.getString("COLUMN_NAME" );
            }
            primaryKeyResultSet.close();
        }
        String databaseProductName = conn.getMetaData().getDatabaseProductName().trim();
        if (databaseProductName.contains("Oracle" ) || databaseProductName.contains("DM DBMS" )) {
            pKeyName = pKeyName.toUpperCase();
        }
        return pKeyName;
    }

    /**
     * 除流程外主键全用f_id
     *
     * @param isflow    是否开启流程
     * @param mainTable
     * @return
     */
    public String getKey(Connection conn, String mainTable, Integer primaryKeyPolicy, boolean isflow) throws SQLException {
        //获取主键
        if (primaryKeyPolicy == 2 && !isflow) {
            primaryKeyPolicy = 1;
        }
        return this.getKey(conn, mainTable, primaryKeyPolicy);
    }
    //---------------------------------------------信息---------------------------------------------

    /**
     * 信息
     *
     * @param dataModel
     * @return
     * @throws WorkFlowException
     */
    public Map<String, Object> info(DataModel dataModel) throws WorkFlowException {
        Map<String, Object> result = new HashMap<>();
        try {
            List<FieLdsModel> fieLdsModelList = dataModel.getFieLdsModelList();
            List<TableModel> tableModelList = dataModel.getTableModelList();
            RecursionForm recursionForm = new RecursionForm(fieLdsModelList, tableModelList);
            List<FormAllModel> formAllModel = new ArrayList<>();
            //递归遍历模板
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
            result = this.infoDataList(dataModel, formAllModel);
        } catch (WorkFlowException e) {
            log.error("查询异常：" + e.getMessage());
            throw new WorkFlowException(e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有数据
     *
     * @param dataModel
     * @param formAllModel
     * @return
     * @throws WorkFlowException
     */
    private Map<String, Object> infoDataList(DataModel dataModel, List<FormAllModel> formAllModel) throws WorkFlowException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<TableModel> tableModelList = dataModel.getTableModelList();
        if (tableModelList.size() > 0) {
            result = this.tableData(dataModel, formAllModel);
        } else {
            result = this.data(dataModel, formAllModel);
        }
        return result;
    }

    /**
     * 有表数据
     *
     * @return
     * @throws WorkFlowException
     */
    private Map<String, Object> tableData(DataModel dataModel, List<FormAllModel> formAllModel) throws WorkFlowException {
        Map<String, Object> data = new HashMap<>();
        String mainId = dataModel.getMainId();
        try {
            DbLinkEntity link = dataModel.getLink();
            DynamicDataSourceUtil.switchToDataSource(link);
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(link);
            List<TableModel> tableList = dataModel.getTableModelList();
            boolean isPolicy = !dataModel.getFlowEnable() && dataModel.getPrimaryKeyPolicy() == 2;
            Integer primaryKeyPolicy = isPolicy ? 1 : dataModel.getPrimaryKeyPolicy();
            if (isPolicy) {
                dataModel.setPrimaryKeyPolicy(primaryKeyPolicy);
            }
            Optional<TableModel> first = tableList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst();
            if (!first.isPresent()) {
                throw new WorkFlowException(MsgCode.COD001.get());
            }
            String mastTableName = first.get().getTable();
            List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
            List<String> mastFile = mastForm.stream().filter(t -> StringUtil.isNotEmpty(t.getFormColumnModel().getFieLdsModel().getVModel())).map(t -> t.getFormColumnModel().getFieLdsModel().getVModel()).collect(Collectors.toList());
            String pKeyName = this.getKey(conn, mastTableName, primaryKeyPolicy);
            mastFile.add(pKeyName);
            if (dataModel.getConcurrencyLock()) {
                mastFile.add(TableFeildsEnum.VERSION.getField());
            }
            String tenantDb = "";
            if ("PostgreSQL".equals(dataSourceUtil.getDbType()) && configValueUtil.isMultiTenancy()) {
                tenantDb = userProvider.get().getTenantDbConnectionString() + ".";
            }
            //主表数据
            SqlTable mastSqlTable = SqlTable.of(mastTableName);
            SelectStatementProvider mastRender = SqlBuilder.select(mastFile.stream().map(m -> mastSqlTable.column(m)).collect(Collectors.toList())).from(mastSqlTable).where(mastSqlTable.column(pKeyName), SqlBuilder.isEqualTo(mainId)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mastData = flowFormDataMapper.selectOneMappedRow(mastRender);
            Map<String, Object> mastDataAll = new HashMap<>();
            for (String key : mastData.keySet()) {
                Object value = mastData.get(key);
                FormAllModel formAll = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel().toLowerCase())).findFirst().orElse(null);
                String dataKey = key;
                if (formAll != null) {
                    FieLdsModel fieLdsModel = formAll.getFormColumnModel().getFieLdsModel();
                    dataKey = fieLdsModel.getVModel();
                    value = this.info(fieLdsModel, value, true);
                }
                mastDataAll.put(dataKey, value);
            }
            data.putAll(mastDataAll);
            //子表数据
            List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
            Map<String, Object> childData = new HashMap<>();
            for (FormAllModel model : tableForm) {
                FormColumnTableModel childList = model.getChildList();
                String tableName = childList.getTableName();
                String tableModel = childList.getTableModel();
                String childKey = this.getKey(conn, tableName, 1);
                List<String> childFile = childList.getChildList().stream().filter(t -> StringUtil.isNotEmpty(t.getFieLdsModel().getVModel())).map(t -> t.getFieLdsModel().getVModel()).collect(Collectors.toList());
                Optional<TableModel> first1 = tableList.stream().filter(t -> t.getTable().equals(tableName)).findFirst();
                if (!first1.isPresent()) {
                    throw new WorkFlowException(MsgCode.COD001.get());
                }
                TableModel table = first1.get();
                SqlTable childSqlTable = SqlTable.of(tableName);
                SelectStatementProvider childRender = SqlBuilder.select(childFile.stream().map(m -> childSqlTable.column(m)).collect(Collectors.toList()))
                        .from(childSqlTable).where(childSqlTable.column(table.getTableField()), SqlBuilder.isEqualTo(mainId))
                        .orderBy(childSqlTable.column(childKey))
                        .build().render(RenderingStrategies.MYBATIS3);
                List<Map<String, Object>> tableDataList = flowFormDataMapper.selectManyMappedRows(childRender);
                List<Map<String, Object>> tableDataAll = new LinkedList<>();
                //子表赋值
                for (Map<String, Object> tableData : tableDataList) {
                    Map<String, Object> childDataOne = new HashMap<>();
                    for (String key : tableData.keySet()) {
                        Object value = tableData.get(key);
                        FieLdsModel fieLdsModel = childList.getChildList().stream().filter(t -> key.equals(t.getFieLdsModel().getVModel().toLowerCase())).map(t -> t.getFieLdsModel()).findFirst().orElse(null);
                        value = this.info(fieLdsModel, value, true);
                        String dataKey = fieLdsModel.getVModel();
                        childDataOne.put(dataKey, value);
                    }
                    tableDataAll.add(childDataOne);
                }
                childData.put(tableModel, tableDataAll);
            }
            data.putAll(childData);
            //副表
            Map<String, List<FormAllModel>> mastTableAll = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
            for (String key : mastTableAll.keySet()) {
                Optional<TableModel> first1 = tableList.stream().filter(t -> t.getTable().equals(key)).findFirst();
                if (!first1.isPresent()) {
                    throw new WorkFlowException(MsgCode.COD001.get());
                }
                TableModel tableModel = first1.get();
                String table = tableModel.getTable();
                List<FormAllModel> mastTableList = mastTableAll.get(key);
                List<String> field = mastTableList.stream().filter(t -> StringUtil.isNotEmpty(t.getFormMastTableModel().getField())).map(t -> t.getFormMastTableModel().getField()).collect(Collectors.toList());
                SqlTable matable = SqlTable.of(table);
                SelectStatementProvider childRender = SqlBuilder.select(field.stream().map(m -> matable.column(m)).collect(Collectors.toList()))
                        .from(matable).where(matable.column(tableModel.getTableField()), SqlBuilder.isEqualTo(mainId))
                        .build().render(RenderingStrategies.MYBATIS3);
                Map<String, Object> dataAll = flowFormDataMapper.selectOneMappedRow(childRender);
                Map<String, Object> mastTable = new HashMap<>();
                for (String mastKey : dataAll.keySet()) {
                    Object value = dataAll.get(mastKey);
                    FieLdsModel fieLdsModel = mastTableList.stream().filter(t -> mastKey.equals(t.getFormMastTableModel().getField().toLowerCase())).map(t -> t.getFormMastTableModel().getMastTable().getFieLdsModel()).findFirst().orElse(null);
                    value = this.info(fieLdsModel, value, true);
                    String dataKey = fieLdsModel.getVModel();
                    mastTable.put(dataKey, value);
                }
                data.putAll(mastTable);
            }
        } catch (SQLException | DataException e) {
            log.error("查询异常：{}" , e.getMessage());
            throw new WorkFlowException(e.getMessage());
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return data;
    }

    /**
     * 无表数据
     *
     * @return
     */
    private Map<String, Object> data(DataModel dataModel, List<FormAllModel> formAllModel) {
        Map<String, Object> dataMap = dataModel.getDataNewMap();
        Map<String, Object> result = new HashMap<>();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : dataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                Object data = dataMap.get(key);
                data = this.info(fieLdsModel, data, false);
                result.put(key, data);
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    String childKeyName = childModel.getChildList().getTableModel();
                    List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) dataMap.get(key);
                    List<Map<String, Object>> childdataAll = new ArrayList<>();
                    for (Map<String, Object> child : childDataMap) {
                        Map<String, Object> tablValue = new HashMap<>(16);
                        for (String childKey : child.keySet()) {
                            FormColumnModel columnModel = childModel.getChildList().getChildList().stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                Object childValue = child.get(childKey);
                                childValue = this.info(fieLdsModel, childValue, false);
                                tablValue.put(childKey, childValue);
                            }
                        }
                        childdataAll.add(tablValue);
                    }
                    result.put(childKeyName, childdataAll);
                }
            }
        }
        return result;
    }

    /**
     * 系统转换赋值
     **/
    private Object info(FieLdsModel fieLdsModel, Object dataValue, boolean isTable) {
        Object value = dataValue;
        String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
        String format = fieLdsModel.getFormat();
        boolean multiple = fieLdsModel.getMultiple();
        String showLevel = fieLdsModel.getShowLevel();
        switch (jnpfKey) {
            case JnpfKeyConsts.CURRORGANIZE:
            case JnpfKeyConsts.CURRDEPT:
                if (ObjectUtil.isNotEmpty(value)) {
                    OrganizeEntity organizeEntity = serviceUtil.getOrganizeInfo(String.valueOf(value));
                    if ("all".equals(showLevel)) {
                        if (organizeEntity != null) {
                            List<OrganizeEntity> organizeList = serviceUtil.getOrganizeId(organizeEntity.getId());
                            value = organizeList.stream().map(OrganizeEntity::getFullName).collect(Collectors.joining("/" ));
                        }
                    } else {
                        if (organizeEntity != null) {
                            value = organizeEntity.getFullName();
                        } else {
                            value = " ";
                        }
                    }
                }
                break;
            case JnpfKeyConsts.CREATEUSER:
            case JnpfKeyConsts.MODIFYUSER:
                if (ObjectUtil.isNotEmpty(value)) {
                    UserEntity userEntity = serviceUtil.getUserInfo(String.valueOf(value));
                    if (userEntity != null) {
                        value = userEntity.getRealName() + "/" + userEntity.getAccount();
                    }
                } else {
                    value = JnpfKeyConsts.CREATEUSER.equals(jnpfKey) ? " " : null;
                }
                break;
            case JnpfKeyConsts.CURRPOSITION:
                if (ObjectUtil.isNotEmpty(value)) {
                    PositionEntity positionEntity = serviceUtil.getPositionInfo(String.valueOf(value));
                    if (positionEntity != null) {
                        value = positionEntity.getFullName();
                    }
                } else {
                    value = " ";
                }
                break;
            case JnpfKeyConsts.UPLOADFZ:
            case JnpfKeyConsts.UPLOADIMG:
                if (value == null) {
                    value = new ArrayList<>();
                } else {
                    if (isTable) {
                        value = JsonUtil.getJsonToListMap(String.valueOf(value));
                    }
                }
                break;
            case JnpfKeyConsts.CHECKBOX:
            case JnpfKeyConsts.DATERANGE:
            case JnpfKeyConsts.TIMERANGE:
                if (value == null) {
                    value = new ArrayList<>();
                } else {
                    if (isTable) {
                        value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                    }
                }
                break;
            case JnpfKeyConsts.COMSELECT:
            case JnpfKeyConsts.ADDRESS:
                if (isTable) {
                    if (multiple) {
                        value = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
                    } else {
                        value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                    }
                }
                break;
            case JnpfKeyConsts.SELECT:
            case JnpfKeyConsts.USERSELECT:
            case JnpfKeyConsts.CUSTOMUSERSELECT:
            case JnpfKeyConsts.DEPSELECT:
            case JnpfKeyConsts.POSSELECT:
            case JnpfKeyConsts.TREESELECT:
            case JnpfKeyConsts.ROLESELECT:
            case JnpfKeyConsts.GROUPSELECT:
                if (isTable) {
                    if (multiple) {
                        value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                    }
                }
                break;
            case JnpfKeyConsts.POPUPTABLESELECT:
                if (isTable) {
                    try {
                        value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                    } catch (Exception e) {
                    }
                }
                break;
            case JnpfKeyConsts.DATE:
                if (isTable) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        value = sdf.parse(String.valueOf(value)).getTime();
                    } catch (Exception e) {
                    }
                }
                break;
            case JnpfKeyConsts.SLIDER:
            case JnpfKeyConsts.SWITCH:
                if (isTable) {
                    try {
                        value = Integer.valueOf(String.valueOf(value));
                    } catch (Exception e) {
                    }
                }
                break;
            case JnpfKeyConsts.CASCADER:
                if (value == null) {
                    value = new ArrayList<>();
                } else {
                    if (isTable) {
                        PropsBeanModel propsModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                        if (propsModel.getMultiple()) {
                            value = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
                        } else {
                            value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return value;
    }

    //---------------------------------------------新增---------------------------------------------

    /**
     * 新增数据处理
     **/
    public Map<String, Object> create(DataModel dataModel) throws WorkFlowException {
        try {
            List<FieLdsModel> fieLdsModelList = dataModel.getFieLdsModelList();
            List<TableModel> tableModelList = dataModel.getTableModelList();
            RecursionForm recursionForm = new RecursionForm(fieLdsModelList, tableModelList);
            List<FormAllModel> formAllModel = new ArrayList<>();
            //递归遍历模板
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
//			addField(tableModelList, dataModel.getLink());
            //处理好的数据
            Map<String, Object> result = this.createDataList(dataModel, formAllModel);
            return result;
        } catch (Exception e) {
            //close
            e.printStackTrace();
            log.error("新增异常：{}" , e.getMessage());
            throw new WorkFlowException(e.getMessage());
        }
    }

    /**
     * 新增数据
     **/
    @DSTransactional
    public Map<String, Object> createDataList(DataModel dataModel, List<FormAllModel> formAllModel) throws SQLException, DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<TableModel> tableModelList = dataModel.getTableModelList();
        //有表数据处理
        if (tableModelList.size() > 0) {
            DbLinkEntity link = dataModel.getLink();
            DynamicDataSourceUtil.switchToDataSource(link);
            try {
                @Cleanup Connection conn = ConnUtil.getConnOrDefault(link);
                String databaseProductName = conn.getMetaData().getDatabaseProductName().trim();
                boolean oracle = databaseProductName.contains("Oracle" ) || databaseProductName.contains("PostgreSQL" );
                dataModel.setIsOracle(oracle);
                //主表
                this.createMast(formAllModel, dataModel, conn, result);
                //子表
                this.createTable(formAllModel, dataModel, conn, result);
                //副表
                this.createMastTable(formAllModel, dataModel, conn, result);
            } finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        } else {
            //无表数据处理
            result = this.createAll(dataModel, formAllModel);
        }
        return result;
    }

    /**
     * 子表数据
     **/
    private void createTable(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        String mainId = dataModel.getMainId();
        UserEntity userEntity = dataModel.getUserEntity();
        //子表
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        Map<String, List<FormColumnModel>> childMap = new HashMap<>();
        Map<String, TableModel> chidTable = new HashMap<>();
        tableForm.stream().forEach(t -> {
            FormColumnTableModel childListAll = t.getChildList();
            String tableModel = childListAll.getTableModel();
            List<FormColumnModel> childList = childListAll.getChildList().stream().filter(g -> StringUtil.isNotEmpty(g.getFieLdsModel().getVModel())).collect(Collectors.toList());
            childMap.put(tableModel, childList);
            String tableName = childListAll.getTableName();
            Optional<TableModel> first = tableModelList.stream().filter(k -> k.getTable().equals(tableName)).findFirst();
            if (first.isPresent()) {
                TableModel childTable = first.get();
                chidTable.put(tableModel, childTable);
            }
        });
        for (String key : childMap.keySet()) {
            //子表数据
            List<Map<String, Object>> chidList = dataNewMap.get(key) != null ? (List<Map<String, Object>>) dataNewMap.get(key) : new ArrayList<>();
            List<FormColumnModel> formColumnModels = childMap.get(key);
            Map<String, FieLdsModel> columMap = new HashMap<>();
            List<String> filedModel = new ArrayList<>();
            //子表主键
            TableModel tableModel = chidTable.get(key);
            String table = tableModel.getTable();
            String childKeyName = this.getKey(conn, table, 1);
            //获取子表对象的类型
            for (FormColumnModel column : formColumnModels) {
                FieLdsModel fieLdsModel = column.getFieLdsModel();
                String vmodel = fieLdsModel.getVModel();
                columMap.put(vmodel, fieLdsModel);
            }
            //关联字段
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(table)).findFirst();
            String mastKeyName = "";
            if (first.isPresent()) {
                mastKeyName = first.get().getTableField();
            }
            SqlTable sqlTable = SqlTable.of(table);
            for (Map<String, Object> objectMap : chidList) {
                GeneralInsertDSL generalInsertDSL = SqlBuilder.insertInto(sqlTable).set(sqlTable.column(mastKeyName)).toValue(mainId);
                for (String childKey : columMap.keySet()) {
                    FieLdsModel fieLdsModel = columMap.get(childKey);
                    String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                    Object data = objectMap.get(childKey);
                    //处理系统自动生成
                    data = this.create(fieLdsModel, data, true, userEntity, dataModel.isLinkOpen());
                    getDSL(isOracle, sqlTable, null,generalInsertDSL, fieLdsModel.getFormat(), jnpfkey, childKey, data);
                }
                if (primaryKeyPolicy == 1) {
                    generalInsertDSL = generalInsertDSL.set(sqlTable.column(childKeyName)).toValue(RandomUtil.uuId());
                }
                GeneralInsertStatementProvider insertRender = generalInsertDSL.build().render(RenderingStrategies.MYBATIS3);
                flowFormDataMapper.generalInsert(insertRender);
            }
            String tenantDb = "";
            if ("PostgreSQL".equals(dataSourceUtil.getDbType()) && configValueUtil.isMultiTenancy()) {
                tenantDb = userProvider.get().getTenantDbConnectionString() + ".";
            }
        }
    }

    /**
     * 副表数据
     **/
    private void createMastTable(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        String mainId = dataModel.getMainId();
        UserEntity userEntity = dataModel.getUserEntity();
        //副表
        Map<String, List<FormAllModel>> mastTableAll = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
        for (String key : mastTableAll.keySet()) {
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(key)).findFirst();
            if (!first.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first.get();
            String tableModelTable = tableModel.getTable();
            String childKeyName = this.getKey(conn, tableModelTable, 1);
            //关联字段
            String mastKeyName = tableModel.getTableField();
            List<FormAllModel> masTableList = mastTableAll.get(key);
            SqlTable sqlTable = SqlTable.of(tableModelTable);
            GeneralInsertDSL generalInsertDSL = SqlBuilder.insertInto(sqlTable).set(sqlTable.column(mastKeyName)).toValue(mainId);

            for (FormAllModel model : masTableList) {
                FormMastTableModel formMastTableModel = model.getFormMastTableModel();
                FormColumnModel mastTable = formMastTableModel.getMastTable();
                FieLdsModel fieLdsModel = mastTable.getFieLdsModel();
                String mostTableKey = fieLdsModel.getVModel();
                if (StringUtil.isEmpty(mostTableKey)) {
                    continue;
                }
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                Object data = dataNewMap.get(mostTableKey);
                //处理系统自动生成
                data = this.create(fieLdsModel, data, true, userEntity, dataModel.isLinkOpen());
                //返回值
                result.put(mostTableKey, data);
                //添加字段
                String field = formMastTableModel.getField();
                getDSL(isOracle, sqlTable, null,generalInsertDSL, fieLdsModel.getFormat(), jnpfkey, field, data);
            }
            //sql主键
            if (primaryKeyPolicy == 1) {
                generalInsertDSL = generalInsertDSL.set(sqlTable.column(childKeyName)).toValue(RandomUtil.uuId());
            }
            String tenantDb = "";
            if ("PostgreSQL".equals(dataSourceUtil.getDbType()) && configValueUtil.isMultiTenancy()) {
                tenantDb = userProvider.get().getTenantDbConnectionString() + ".";
            }
            GeneralInsertStatementProvider insertRender = generalInsertDSL.build().render(RenderingStrategies.MYBATIS3);
            flowFormDataMapper.generalInsert(insertRender);
        }
    }

    /**
     * 主表数据
     **/
    private void createMast(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        String mainId = dataModel.getMainId();
        UserEntity userEntity = dataModel.getUserEntity();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        Optional<TableModel> first = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst();
        if (!first.isPresent()) {
            throw new SQLException(MsgCode.COD001.get());
        }
        TableModel tableModel = first.get();
        String mastTableName = tableModel.getTable();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).filter(t -> StringUtil.isNotEmpty(t.getFormColumnModel().getFieLdsModel().getVModel())).collect(Collectors.toList());
        //新增字段
        List<String> filedModel = new ArrayList<>();
        List<Object> mastData = new LinkedList<>();
        String keyName = this.getKey(conn, mastTableName, primaryKeyPolicy);

        SqlTable sqlTable = SqlTable.of(mastTableName);
        GeneralInsertDSL generalInsertDSL = SqlBuilder.insertInto(sqlTable).set(sqlTable.column(keyName)).toValue(mainId);

        for (FormAllModel model : mastForm) {
            FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
            String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
            String field = fieLdsModel.getVModel();
            Object data = dataNewMap.get(field);
            data = data instanceof List ? JsonUtil.getObjectToString(data) : data;
            //处理系统自动生成
            data = this.create(fieLdsModel, data, true, userEntity, dataModel.isLinkOpen());
            getDSL(isOracle, sqlTable, null,generalInsertDSL, fieLdsModel.getFormat(), jnpfkey, field, data);
        }
        //判断是否开启锁
        if (dataModel.getConcurrencyLock()) {
            generalInsertDSL = generalInsertDSL.set(sqlTable.column(TableFeildsEnum.VERSION.getField())).toValue(dataNewMap.get(TableFeildsEnum.VERSION.getField()));
        }
        //添加流程引擎信息
        if (dataNewMap.get(FlowFormConstant.FLOWID) != null && StringUtil.isNotEmpty(dataNewMap.get(FlowFormConstant.FLOWID).toString())) {
            generalInsertDSL = generalInsertDSL.set(sqlTable.column(TableFeildsEnum.FLOWID.getField())).toValue(dataNewMap.get(FlowFormConstant.FLOWID));
        }

        //新增sql语句
        String tenantDb = "";
        if ("PostgreSQL".equals(dataSourceUtil.getDbType()) && configValueUtil.isMultiTenancy()) {
            tenantDb = userProvider.get().getTenantDbConnectionString() + ".";
        }

        GeneralInsertStatementProvider insertRender = generalInsertDSL.build().render(RenderingStrategies.MYBATIS3);
        flowFormDataMapper.generalInsert(insertRender);
        Boolean flowEnable = dataModel.getFlowEnable();
        if (!flowEnable && primaryKeyPolicy == 2) {
            List<String> mastFile = new ArrayList<>();
            mastFile.add(keyName);
            String idName = this.getKey(conn, mastTableName, 1);
            mastFile.add(idName);
            SqlTable mastSqlTable = SqlTable.of(mastTableName);
            SelectStatementProvider mastRender = SqlBuilder.select(mastFile.stream().map(m -> mastSqlTable.column(m)).collect(Collectors.toList())).from(mastSqlTable).
                    where(mastSqlTable.column(keyName), SqlBuilder.isEqualTo(mainId)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> map = flowFormDataMapper.selectOneMappedRow(mastRender);
            dataModel.setMainId(map.get(idName) != null ? String.valueOf(map.get(idName)) : dataModel.getMainId());
        }
    }

    /**
     * 新增系统赋值
     **/
    private Object create(FieLdsModel fieLdsModel, Object dataValue, boolean isTable, UserEntity userEntity, boolean isLink) {
        String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
        String rule = fieLdsModel.getConfig().getRule();
        Object value = dataValue;
        //外链跳过系统参数生成
        if (isLink) {
            value = linkDefaultValue(dataValue, isTable, jnpfKey, value);
            return value;
        }
        switch (jnpfKey) {
            case JnpfKeyConsts.CREATEUSER:
                value = userEntity.getId();
                break;
            case JnpfKeyConsts.CREATETIME:
                value = DateUtil.getNow("+8" );
                break;
            case JnpfKeyConsts.CURRORGANIZE:
            case JnpfKeyConsts.CURRDEPT:
                value = userEntity.getOrganizeId();
                if (ObjectUtil.isNotEmpty(value)) {
                    if (!"all".equals(fieLdsModel.getShowLevel())) {
                        OrganizeEntity organizeInfo = serviceUtil.getOrganizeInfo(String.valueOf(value));
                        if ("company".equals(organizeInfo.getCategory())) {
                            value = null;
                        }
                    }
                }
                break;
            case JnpfKeyConsts.MODIFYTIME:
                value = null;
                break;
            case JnpfKeyConsts.MODIFYUSER:
                value = null;
                break;
            case JnpfKeyConsts.CURRPOSITION:
                value = userEntity.getPositionId();
                break;
            case JnpfKeyConsts.BILLRULE:
                try {
                    value = serviceUtil.getBillNumber(rule);
                } catch (Exception e) {
                    value = null;
                }
                break;
            case JnpfKeyConsts.DATE:
                if (isTable) {
                    try {
                        if (dataValue == null || "".equals(dataValue)) {
                            return null;
                        }
                        if(dataValue instanceof String){
                            //日期不同格式转换
                            value=FormPublicUtils.getTimeFormat(dataValue.toString());
                        }else{
                            value = DateUtil.dateToString(new Date(Long.valueOf(String.valueOf(dataValue))), "yyyy-MM-dd HH:mm:ss" );
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case JnpfKeyConsts.NUM_INPUT:
            case JnpfKeyConsts.CALCULATE:
                if (isTable) {
                    try {
                        value = new BigDecimal(String.valueOf(dataValue));
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                if (isTable) {
                    if (value instanceof List || value instanceof String[][]) {
                        value = JsonUtil.getObjectToString(value);
                    } else if (value instanceof CharSequence) {
                        if (StrUtil.isEmpty((CharSequence) value)) {
                            value = null;
                        }
                    }
                }
                break;
        }
        return value;
    }

    /**
     * 外链控件字段数据转换
     *
     * @param
     * @return
     */
    private Object linkDefaultValue(Object dataValue, boolean isTable, String jnpfKey, Object value) {
        switch (jnpfKey) {
            case JnpfKeyConsts.DATE:
                if (isTable) {
                    try {
                        if (dataValue == null || "".equals(dataValue)) {
                            return null;
                        }
                        value = DateUtil.dateToString(new Date(Long.valueOf(String.valueOf(dataValue))), "yyyy-MM-dd HH:mm:ss" );
                    } catch (Exception e) {

                    }
                }
                break;
            case JnpfKeyConsts.NUM_INPUT:
            case JnpfKeyConsts.CALCULATE:
                if (isTable) {
                    try {
                        value = new BigDecimal(String.valueOf(dataValue));
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                if (isTable) {
                    if (value instanceof List || value instanceof String[][]) {
                        value = JsonUtil.getObjectToString(value);
                    } else if (value instanceof CharSequence) {
                        if (StrUtil.isEmpty((CharSequence) value)) {
                            value = null;
                        }
                    }
                }
                break;
        }
        return value;
    }

    /**
     * 无表插入数据
     **/
    private Map<String, Object> createAll(DataModel dataModel, List<FormAllModel> formAllModel) {
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        UserEntity userEntity = dataModel.getUserEntity();
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : dataNewMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                Object data = dataNewMap.get(key);
                //处理系统自动生成
                data = this.create(fieLdsModel, data, false, userEntity, dataModel.isLinkOpen());
                result.put(key, data);
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    //子表主键
                    List<FormColumnModel> childList = childModel.getChildList().getChildList();
                    List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) dataNewMap.get(key);
                    //子表处理的数据
                    List<Map<String, Object>> childResult = new ArrayList<>();
                    for (Map<String, Object> objectMap : childDataMap) {
                        //子表单体处理的数据
                        Map<String, Object> childOneResult = new HashMap<>(16);
                        for (String childKey : objectMap.keySet()) {
                            FormColumnModel columnModel = childList.stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                Object data = objectMap.get(childKey);
                                //处理系统自动生成
                                data = this.create(fieLdsModel, data, false, userEntity, dataModel.isLinkOpen());
                                childOneResult.put(childKey, data);
                            }
                        }
                        childResult.add(childOneResult);
                    }
                    result.put(key, childResult);
                }
            }
        }
        return result;
    }

    //--------------------------------------------修改 ----------------------------------------------------

    /**
     * 修改数据处理
     **/
    public Map<String, Object> update(DataModel dataModel) throws WorkFlowException {
        try {
            List<FieLdsModel> fieLdsModelList = dataModel.getFieLdsModelList();
            List<TableModel> tableModelList = dataModel.getTableModelList();
            RecursionForm recursionForm = new RecursionForm(fieLdsModelList, tableModelList);
            List<FormAllModel> formAllModel = new ArrayList<>();
            //递归遍历模板
            FormCloumnUtil.recursionForm(recursionForm, formAllModel);
//			addField(tableModelList, dataModel.getLink());
            //处理好的数据
            Map<String, Object> result = this.updateDataList(dataModel, formAllModel);
            return result;
        } catch (Exception e) {
            //close
            e.printStackTrace();
            log.error("修改异常：{}" , e.getMessage());
            throw new WorkFlowException(e.getMessage());
        }
    }

    /**
     * 修改数据
     **/
    @DSTransactional
    public Map<String, Object> updateDataList(DataModel dataModel, List<FormAllModel> formAllModel) throws SQLException, DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<TableModel> tableModelList = dataModel.getTableModelList();
        //有表数据处理
        if (tableModelList.size() > 0) {
            DbLinkEntity link = dataModel.getLink();
            DynamicDataSourceUtil.switchToDataSource(link);
            try {
                //系统数据
                @Cleanup Connection conn = ConnUtil.getConnOrDefault(link);
                String databaseProductName = conn.getMetaData().getDatabaseProductName().trim();
                boolean oracle = databaseProductName.contains("Oracle" ) || databaseProductName.contains("PostgreSQL" );
                dataModel.setIsOracle(oracle);
                conn.setAutoCommit(false);
                //主表
                this.updateMast(formAllModel, dataModel, conn, result);
                //子表
                this.updateTable(formAllModel, dataModel, conn, result);
                //副表
                this.updateMastTable(formAllModel, dataModel, conn, result);
            } finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        } else {
            //无表数据处理
            result = this.updateAll(dataModel, formAllModel);
        }
        return result;
    }

    /**
     * 子表数据
     **/
    private void updateTable(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        String mainId = dataModel.getMainId();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        //子表
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        Map<String, List<FormColumnModel>> childMap = new HashMap<>();
        Map<String, TableModel> chidTable = new HashMap<>();
        tableForm.stream().forEach(t -> {
            FormColumnTableModel childListAll = t.getChildList();
            String tableModel = childListAll.getTableModel();
            List<FormColumnModel> childList = childListAll.getChildList().stream().filter(g -> StringUtil.isNotEmpty(g.getFieLdsModel().getVModel())).collect(Collectors.toList());
            childMap.put(tableModel, childList);
            String tableName = childListAll.getTableName();
            Optional<TableModel> first = tableModelList.stream().filter(k -> k.getTable().equals(tableName)).findFirst();
            if (first.isPresent()) {
                TableModel childTable = first.get();
                chidTable.put(tableModel, childTable);
            }
        });

        for (String key : childMap.keySet()) {
            //子表数据
            List<Map<String, Object>> chidList = dataNewMap.get(key) != null ? (List<Map<String, Object>>) dataNewMap.get(key) : new ArrayList<>();
            List<FormColumnModel> formColumnModels = childMap.get(key);
            Map<String, FieLdsModel> columMap = new HashMap<>();
            List<String> filedModel = new ArrayList<>();
            //子表主键
            TableModel tableModel = chidTable.get(key);
            String table = tableModel.getTable();
            String childKeyName = this.getKey(conn, table, 1);
            //获取子表对象的类型
            for (FormColumnModel column : formColumnModels) {
                FieLdsModel fieLdsModel = column.getFieLdsModel();
                String vmodel = fieLdsModel.getVModel();
                columMap.put(vmodel, fieLdsModel);
            }
            for (String childKey : columMap.keySet()) {
                //添加字段
                filedModel.add(childKey);
            }
            //主键
            if (primaryKeyPolicy == 1) {
                filedModel.add(childKeyName);
            }
            //关联字段
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(table)).findFirst();
            if (!first.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            String mastKeyName = first.get().getTableField();

            SqlTable sqlTable = SqlTable.of(table);
            String childPrimary=this.getKey(conn,table,1);
            SelectStatementProvider render = SqlBuilder.select(sqlTable.column(childPrimary)).from(sqlTable).where(sqlTable.column(mastKeyName), SqlBuilder.isEqualTo(mainId))
                    .build().render(RenderingStrategies.MYBATIS3);
            List<Object> childIdList = flowFormDataMapper.selectManyMappedRows(render).stream().map(t -> t.get(childPrimary)).collect(Collectors.toList());
            List<Object> formDataIdList = chidList.stream().filter(t->t.containsKey(childPrimary)).map(t -> t.get(childPrimary).toString()).collect(Collectors.toList());
            List<Object> deleteList= childIdList.stream().filter(t -> !formDataIdList.contains(t)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(deleteList)){//删除子表id数据
                DeleteStatementProvider deleteRender = SqlBuilder.deleteFrom(sqlTable).where(sqlTable.column(childPrimary), SqlBuilder.isIn(deleteList))
                        .build().render(RenderingStrategies.MYBATIS3);
                flowFormDataMapper.delete(deleteRender);
            }
//            DeleteStatementProvider deleteRender = SqlBuilder.deleteFrom(sqlTable).where(sqlTable.column(mastKeyName), SqlBuilder.isEqualTo(mainId))
//                    .build().render(RenderingStrategies.MYBATIS3);
//            flowFormDataMapper.delete(deleteRender);

            for (Map<String, Object> objectMap : chidList) {
                GeneralInsertDSL generalInsertDSL = SqlBuilder.insertInto(sqlTable).set(sqlTable.column(mastKeyName)).toValue(mainId);
                UpdateDSL<UpdateModel> updateModelUpdateDSL = SqlBuilder.update(sqlTable).set(sqlTable.column(mastKeyName)).equalTo(mainId);
                for (String childKey : columMap.keySet()) {
                    FieLdsModel fieLdsModel = columMap.get(childKey);
                    String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                    Object data = objectMap.get(childKey);
                    if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfkey) || JnpfKeyConsts.CURRPOSITION.equals(jnpfkey) || JnpfKeyConsts.CREATETIME.equals(jnpfkey) || JnpfKeyConsts.CREATEUSER.equals(jnpfkey)) {
                        continue;
                    }
                    //处理系统自动生成
                    data = this.update(fieLdsModel, data, true);
                    getDSL(isOracle, sqlTable, null,generalInsertDSL, fieLdsModel.getFormat(), jnpfkey, childKey, data);
                    getDSL(isOracle, sqlTable, updateModelUpdateDSL,null, fieLdsModel.getFormat(), jnpfkey, childKey, data);
                }

                if(childIdList.contains(objectMap.get(childPrimary))){//修改
                    if (primaryKeyPolicy == 2) {
                        //自增转换ID类型
                        objectMap.put(childPrimary, Long.parseLong(objectMap.get(childPrimary).toString()));
                    }
                    updateModelUpdateDSL.where(sqlTable.column(childPrimary),SqlBuilder.isEqualTo(objectMap.get(childPrimary)));
                    UpdateStatementProvider render1 = updateModelUpdateDSL.build().render(RenderingStrategies.MYBATIS3);
                    flowFormDataMapper.update(render1);
                }else {//新增
                    //添加主键值和外键值
                    if (primaryKeyPolicy == 1) {
                        generalInsertDSL = generalInsertDSL.set(sqlTable.column(childKeyName)).toValue(RandomUtil.uuId());
                    }
                    GeneralInsertStatementProvider insertRender = generalInsertDSL.build().render(RenderingStrategies.MYBATIS3);
                    flowFormDataMapper.generalInsert(insertRender);
                }
            }
        }
    }

    /**
     * 副表数据
     **/
    private void updateMastTable(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        String mainId = dataModel.getMainId();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        //副表
        Map<String, List<FormAllModel>> mastTableAll = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.groupingBy(e -> e.getFormMastTableModel().getTable()));
        for (String key : mastTableAll.keySet()) {
            //副表
            Optional<TableModel> first = tableModelList.stream().filter(t -> t.getTable().equals(key)).findFirst();
            if (!first.isPresent()) {
                throw new SQLException(MsgCode.COD001.get());
            }
            TableModel tableModel = first.get();
            String tableModelTable = tableModel.getTable();
            String childKeyName = this.getKey(conn, tableModelTable, 1);
            //关联字段
            String mastKeyName = tableModel.getTableField();
            List<FormAllModel> masTableList = mastTableAll.get(key);

            SqlTable sqlTable = SqlTable.of(tableModelTable);
            UpdateDSL<UpdateModel> updateModelUpdateDSL = SqlBuilder.update(sqlTable).set(sqlTable.column(mastKeyName)).equalTo(mainId);

            for (FormAllModel model : masTableList) {
                FormMastTableModel formMastTableModel = model.getFormMastTableModel();
                FormColumnModel mastTable = formMastTableModel.getMastTable();
                FieLdsModel fieLdsModel = mastTable.getFieLdsModel();
                String mostTableKey = fieLdsModel.getVModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                Object data = dataNewMap.get(mostTableKey);
                String field = formMastTableModel.getField();
                if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfkey) || JnpfKeyConsts.CURRPOSITION.equals(jnpfkey) || JnpfKeyConsts.CREATETIME.equals(jnpfkey) || JnpfKeyConsts.CREATEUSER.equals(jnpfkey)) {
                    continue;
                }
                //处理系统自动生成
                data = this.update(fieLdsModel, data, true);
                getDSL(isOracle, sqlTable, updateModelUpdateDSL,null, fieLdsModel.getFormat(), jnpfkey, field, data);
            }
            //sql主键
            if (primaryKeyPolicy == 1) {
                updateModelUpdateDSL = updateModelUpdateDSL.set(sqlTable.column(childKeyName)).equalTo(RandomUtil.uuId());
            }
            UpdateStatementProvider updateStatementProvider = updateModelUpdateDSL.where(sqlTable.column(mastKeyName), SqlBuilder.isEqualTo(mainId)).build().render(RenderingStrategies.MYBATIS3);
            flowFormDataMapper.update(updateStatementProvider);
        }
    }

    /**
     * 主表数据
     **/
    private void updateMast(List<FormAllModel> formAllModel, DataModel dataModel, Connection conn, Map<String, Object> result) throws SQLException {
        List<TableModel> tableModelList = dataModel.getTableModelList();
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        Boolean isOracle = dataModel.getIsOracle();
        String mainId = dataModel.getMainId();
        Integer primaryKeyPolicy = dataModel.getPrimaryKeyPolicy();
        Optional<TableModel> first = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst();
        if (!first.isPresent()) {
            throw new SQLException(MsgCode.COD001.get());
        }
        TableModel tableModel = first.get();
        String mastTableName = tableModel.getTable();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).filter(t -> StringUtil.isNotEmpty(t.getFormColumnModel().getFieLdsModel().getVModel())).collect(Collectors.toList());
        //修改字段
        List<String> filed = new ArrayList<>();
        List<Object> mastData = new LinkedList<>();
        Boolean flowEnable = dataModel.getFlowEnable();
        if (!flowEnable && primaryKeyPolicy == 2) {
            primaryKeyPolicy = 1;
        }
        String keyName = this.getKey(conn, mastTableName, primaryKeyPolicy);
        SqlTable sqlTable = SqlTable.of(mastTableName);
        UpdateDSL<UpdateModel> updateModelUpdateDSL = SqlBuilder.update(sqlTable).set(sqlTable.column(keyName)).equalTo(mainId);
        for (FormAllModel model : mastForm) {
            FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
            String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
            String field = fieLdsModel.getVModel();
            Object data = dataNewMap.get(field);
            if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfkey) || JnpfKeyConsts.CURRPOSITION.equals(jnpfkey) || JnpfKeyConsts.CREATETIME.equals(jnpfkey) || JnpfKeyConsts.CREATEUSER.equals(jnpfkey)) {
                continue;
            }
            //处理系统自动生成
            data = this.update(fieLdsModel, data, true);
            getDSL(isOracle, sqlTable, updateModelUpdateDSL,null, fieLdsModel.getFormat(), jnpfkey, field, data);
        }
        //判断是否开启锁
        if (dataModel.getConcurrencyLock()) {
            updateModelUpdateDSL = updateModelUpdateDSL.set(sqlTable.column(TableFeildsEnum.VERSION.getField())).equalTo(dataNewMap.get(TableFeildsEnum.VERSION.getField()));
        }
        UpdateStatementProvider updateStatementProvider = updateModelUpdateDSL.where(sqlTable.column(keyName), SqlBuilder.isEqualTo(mainId)).build().render(RenderingStrategies.MYBATIS3);
        flowFormDataMapper.update(updateStatementProvider);
    }

    /**
     * 修改无表数据
     **/
    private Map<String, Object> updateAll(DataModel dataModel, List<FormAllModel> formAllModel) {
        Map<String, Object> dataNewMap = dataModel.getDataNewMap();
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        //系统数据
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : dataNewMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                Object data = dataNewMap.get(key);
                //处理系统自动生成
                data = this.update(fieLdsModel, data, false);
                result.put(key, data);
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) dataNewMap.get(key);
                    //子表处理的数据
                    List<Map<String, Object>> childResult = new ArrayList<>();
                    for (Map<String, Object> objectMap : childDataMap) {
                        //子表单体处理的数据
                        Map<String, Object> childOneResult = new HashMap<>(16);
                        for (String childKey : objectMap.keySet()) {
                            FormColumnModel columnModel = childModel.getChildList().getChildList().stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                Object data = objectMap.get(childKey);
                                data = this.update(fieLdsModel, data, false);
                                childOneResult.put(childKey, data);
                            }
                        }
                        childResult.add(childOneResult);
                    }
                    result.put(key, childResult);
                }
            }
        }
        return result;
    }

    /**
     * 修改系统赋值
     **/
    private Object update(FieLdsModel fieLdsModel, Object dataValue, boolean isTable) {
        String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
        String rule = fieLdsModel.getConfig().getRule();
        UserInfo userInfo = userProvider.get();
        Object value = dataValue;
        switch (jnpfKey) {
            case JnpfKeyConsts.CREATEUSER:
                if (!ObjectUtil.isEmpty(value)) {
                    String name = String.valueOf(value).split("/" )[0];
                    UserEntity userEntity = serviceUtil.getByRealName(name);
                    value = userEntity != null ? userEntity.getId() : userInfo.getUserId();
                } else {
                    value = userInfo.getUserId();
                }
                break;
            case JnpfKeyConsts.CREATETIME:
                if (ObjectUtil.isEmpty(value)) {
                    value = DateUtil.getNow("+8" );
                }
                break;
            case JnpfKeyConsts.CURRORGANIZE:
            case JnpfKeyConsts.CURRDEPT:
                if (!ObjectUtil.isEmpty(value)) {
                    String posValue = String.valueOf(value);
                    //多级组织取最后一级
                    if (posValue.contains("/" )) {
                        posValue = posValue.substring(posValue.lastIndexOf("/" ) + 1);
                    }
                    OrganizeEntity organizeEntity = serviceUtil.getOrganizeFullName(posValue);
                    value = organizeEntity != null ? organizeEntity.getId() : value;
                } else {
                    value = StringUtil.isNotEmpty(userInfo.getDepartmentId()) ? userInfo.getDepartmentId() : userInfo.getOrganizeId();
                }
                break;
            case JnpfKeyConsts.MODIFYTIME:
                value = DateUtil.getNow("+8" );
                break;
            case JnpfKeyConsts.MODIFYUSER:
                value = userInfo.getUserId();
                break;
            case JnpfKeyConsts.CURRPOSITION:
                if (!ObjectUtil.isEmpty(value)) {
                    PositionEntity positionEntity = serviceUtil.getPositionFullName(String.valueOf(value));
                    value = positionEntity != null ? positionEntity.getId() : "";
                } else {
                    value = userInfo.getPositionIds().length > 0 ? userInfo.getPositionIds()[0] : "";
                }
                break;
            case JnpfKeyConsts.BILLRULE:
                if (ObjectUtil.isEmpty(value)) {
                    try {
                        value = serviceUtil.getBillNumber(rule);
                    } catch (Exception e) {
                        value = null;
                    }
                }
                break;
            case JnpfKeyConsts.DATE:
                if (isTable) {
                    try {
                        if (dataValue == null || "".equals(dataValue)) {
                            return null;
                        }
                        value = DateUtil.dateToString(new Date(Long.valueOf(String.valueOf(dataValue))), "yyyy-MM-dd HH:mm:ss" );
                    } catch (Exception e) {

                    }
                }
                break;
            case JnpfKeyConsts.NUM_INPUT:
            case JnpfKeyConsts.CALCULATE:
                if (isTable) {
                    try {
                        value = new BigDecimal(String.valueOf(dataValue));
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                if (isTable) {
                    if (value instanceof List || value instanceof String[][]) {
                        value = JsonUtil.getObjectToString(value);
                    } else if (value instanceof CharSequence) {
                        if (StrUtil.isEmpty((CharSequence) value)) {
                            value = null;
                        }
                    }
                }
                break;
        }
        return value;
    }


    /**
     * @param id
     * @param primaryKeyPolicy
     * @param tableModels
     * @param linkEntity
     * @return
     * @throws Exception
     */
    @DSTransactional
    public boolean deleteTable(String id, Integer primaryKeyPolicy, List<TableModel> tableModels, DbLinkEntity linkEntity) throws Exception {
        boolean isSnowFlake = primaryKeyPolicy == 1;

        //主表
        TableModel mainTableModel = tableModels.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().orElse(null);
        String mainTable = mainTableModel.getTable();
        DynamicDataSourceUtil.switchToDataSource(linkEntity);
        @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
        //获取主键
        String pKeyName = this.getKey(conn, mainTable, primaryKeyPolicy);

        SelectStatementProvider queryMain = SqlBuilder.select(SqlTable.of(mainTable).allColumns()).from(SqlTable.of(mainTable))
                .where(SqlTable.of(mainTable).column(pKeyName), SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
        List<Map<String, Object>> mainMapList = flowFormDataMapper.selectManyMappedRows(queryMain);
        mainMapList = FormPublicUtils.toLowerKeyList(mainMapList);

        DeleteStatementProvider mainDelete = SqlBuilder.deleteFrom(SqlTable.of(mainTable))
                .where(SqlTable.of(mainTable).column(pKeyName), SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
        flowFormDataMapper.delete(mainDelete);
        if (mainMapList.size() > 0) {
            if (tableModels.size() > 1) {
                //去除主表
                tableModels.remove(mainTableModel);
                for (TableModel table : tableModels) {
                    //主表字段
                    String relationField = isSnowFlake ? table.getRelationField() : TableFeildsEnum.FLOWTASKID.getField();
                    String relationFieldValue = mainMapList.get(0).get(relationField.toLowerCase()).toString();
                    //子表字段
                    String tableField = table.getTableField();
                    DeleteStatementProvider childDeleteProvider = SqlBuilder.deleteFrom(SqlTable.of(table.getTable()))
                            .where(SqlTable.of(table.getTable()).column(tableField), SqlBuilder.isEqualTo(relationFieldValue)).build().render(RenderingStrategies.MYBATIS3);
                    flowFormDataMapper.delete(childDeleteProvider);
                }
            }
        }
        DynamicDataSourceUtil.clearSwitchDataSource();
        return true;
    }


    /**
     * 执行sql语句
     **/
    private void sql(String sql, List<List<Object>> dataAll, String[] del, Connection conn, boolean isCommit) throws SQLException {
        try {
            if (del.length > 0) {
                @Cleanup PreparedStatement delete = conn.prepareStatement(del[0]);
                delete.setObject(1, del[1]);
                delete.addBatch();
                delete.executeBatch();
            }
            @Cleanup PreparedStatement save = conn.prepareStatement(sql);
            for (List<Object> childData : dataAll) {
                for (int i = 0; i < childData.size(); i++) {
                    Object data = childData.get(i);
                    save.setObject(i + 1, data);
                }
                save.addBatch();
                save.executeBatch();
            }
            if (isCommit) {
                conn.commit();
            }
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("sql语句异常：" + e.getMessage());
            throw new SQLException(e.getMessage());
        }
    }

    public Boolean getVersion(String table, DbLinkEntity linkEntity, Map dataMap, String id, Integer primaryKey) {
        boolean canUpdate = true;
        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
            String pKeyName = this.getKey(conn, table, primaryKey);
            SqlTable sqlTable = SqlTable.of(table);
            SelectStatementProvider render = SqlBuilder.select(sqlTable.column(TableFeildsEnum.VERSION.getField())).from(sqlTable).where(sqlTable.column(pKeyName), SqlBuilder.isEqualTo(id))
                    .and(sqlTable.column(TableFeildsEnum.VERSION.getField()), SqlBuilder.isEqualTo(dataMap.get(TableFeildsEnum.VERSION.getField()))).build().render(RenderingStrategies.MYBATIS3);
            int count = flowFormDataMapper.selectManyMappedRows(render).size();
            canUpdate = count > 0;
        } catch (DataException | SQLException e) {
            log.error("切换数据源异常" );
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return canUpdate;
    }

    /**
     * 添加sql语句
     * @param
     * @return
     */
    private void getDSL(Boolean isOracle, SqlTable sqlTable, UpdateDSL<UpdateModel> updateModelUpdateDSL, GeneralInsertDSL generalInsertDSL,
                        String format, String jnpfkey, String field, Object data) {
        boolean dataTimestamp = "yyyy-MM-dd HH:mm:ss".equals(format);
        String dateFunc = dataTimestamp ? "to_timestamp" : "to_date";
        if (data == null || StringUtil.isEmpty(data.toString())) {
            if(generalInsertDSL!=null){
                generalInsertDSL = generalInsertDSL.set(sqlTable.column(field)).toNull();
            }else{
                updateModelUpdateDSL = updateModelUpdateDSL.set(sqlTable.column(field)).equalToNull();
            }
        } else {
            if (isOracle && (JnpfKeyConsts.DATE.equals(jnpfkey) || JnpfKeyConsts.MODIFYTIME.equals(jnpfkey) || JnpfKeyConsts.CREATETIME.equals(jnpfkey))) {
                String constanct = dateFunc + "('" + data.toString() + "','yyyy-mm-dd HH24:mi:ss')";
                if(generalInsertDSL!=null){
                    generalInsertDSL = generalInsertDSL.set(sqlTable.column(field)).toConstant(constanct);
                }else{
                    updateModelUpdateDSL = updateModelUpdateDSL.set(sqlTable.column(field)).equalToConstant(constanct);
                }

            } else {
                if(generalInsertDSL!=null){
                    generalInsertDSL = generalInsertDSL.set(sqlTable.column(field)).toValue(data);
                }else {
                    updateModelUpdateDSL = updateModelUpdateDSL.set(sqlTable.column(field)).equalTo(data);
                }
            }
        }
    }
}
