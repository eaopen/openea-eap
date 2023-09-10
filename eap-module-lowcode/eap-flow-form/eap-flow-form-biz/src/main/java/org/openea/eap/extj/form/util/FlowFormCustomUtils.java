package org.openea.eap.extj.form.util;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.mapper.FlowFormDataMapper;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;
import org.openea.eap.extj.model.visualJson.analysis.FormEnum;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.analysis.FormMastTableModel;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import lombok.Cleanup;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义流程表单处理
 *
 *
 */
@Component
public class FlowFormCustomUtils {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowFormDataUtil flowDataUtil;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;
    @Autowired
    private FormInfoUtils formInfoUtils;
    @Autowired
    private FormCheckUtils formCheckUtils;
    @Autowired
    private ServiceBaseUtil serviceUtil;

    public void create(FlowFormEntity flowFormEntity, String id, Map<String, Object> map, UserEntity delegateUser) throws WorkFlowException {
        FormDataModel formData = JsonUtil.getJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModels = JsonUtil.getJsonToList(flowFormEntity.getTableJson(), TableModel.class);
        DbLinkEntity linkEntity = serviceUtil.getDbLink(flowFormEntity.getDbLinkId());
        //是否开启并发锁
        Boolean concurrency = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            //初始化version值
            map.put(TableFeildsEnum.VERSION.getField(), 0);
            concurrency = true;
        }
        //单行唯一校验
        String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy, formData.getLogicalDelete(),null);
        if (StringUtil.isNotEmpty(b)) {
            throw new WorkFlowException(b + "不能重复");
        }
        String mainId = id;
        UserEntity userEntity;
        UserInfo userInfo = userProvider.get();
        if (delegateUser != null) {
            delegateUser.setId(userInfo.getUserId());
            userEntity = delegateUser;
        } else {
            userEntity = serviceUtil.getUserInfo(userInfo.getUserId());
        }
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels).mainId(mainId).link(linkEntity)
                .userEntity(userEntity).concurrencyLock(concurrency).primaryKeyPolicy(primaryKeyPolicy).flowEnable(true).build();
        flowDataUtil.create(dataModel);
    }

    public void update(FlowFormEntity flowFormEntity, String id, Map<String, Object> map) throws WorkFlowException, SQLException, DataException {
        FormDataModel formData = JsonUtil.getJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
        List<TableModel> tableModels = JsonUtil.getJsonToList(flowFormEntity.getTableJson(), TableModel.class);
        TableModel mainT = tableModels.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        DbLinkEntity linkEntity = serviceUtil.getDbLink(flowFormEntity.getDbLinkId());
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        //是否开启并发锁
        Boolean isConcurrencyLock = false;
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        if (formData.getConcurrencyLock()) {
            if (map.get(TableFeildsEnum.VERSION.getField()) == null) {
                map.put(TableFeildsEnum.VERSION.getField(), 0);
            } else {
                boolean version = flowDataUtil.getVersion(mainT.getTable(), linkEntity, map, id, primaryKeyPolicy);
                if (!version) {
                    throw new WorkFlowException("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
                } else {
                    Integer vs = Integer.valueOf(String.valueOf(map.get(TableFeildsEnum.VERSION.getField())));
                    map.put(TableFeildsEnum.VERSION.getField(), vs + 1);
                }
            }
            isConcurrencyLock = true;
        }

        String b = formCheckUtils.checkForm(list, map, linkEntity, tableModels, primaryKeyPolicy,formData.getLogicalDelete(), id);
        if (StringUtil.isNotEmpty(b)) {
            throw new WorkFlowException(b + "不能重复");
        }
        UserInfo userInfo = userProvider.get();
        UserEntity userEntity = serviceUtil.getUserInfo(userInfo.getUserId());
        ;
        DataModel dataModel = DataModel.builder().dataNewMap(map).fieLdsModelList(list).tableModelList(tableModels).mainId(id).link(linkEntity)
                .userEntity(userEntity).concurrencyLock(isConcurrencyLock).primaryKeyPolicy(primaryKeyPolicy).flowEnable(true).build();
        flowDataUtil.update(dataModel);
    }

    public void saveOrUpdate(FlowFormEntity flowFormEntity, String id, Map<String, Object> map, UserEntity delegateUser) throws WorkFlowException, SQLException, DataException {
        FormDataModel formData = JsonUtil.getJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
        List<TableModel> tableModels = JsonUtil.getJsonToList(flowFormEntity.getTableJson(), TableModel.class);
        TableModel mainT = tableModels.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        DbLinkEntity linkEntity = serviceUtil.getDbLink(flowFormEntity.getDbLinkId());
        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        SqlTable sqlTable = SqlTable.of(mainT.getTable());
        long count = formCheckUtils.getCount(id, sqlTable, mainT, linkEntity, primaryKeyPolicy);
        if (count > 0) {
            this.update(flowFormEntity, id, map);
        } else {
            this.create(flowFormEntity, id, map, delegateUser);
        }
    }

    public Map<String, Object> info(FlowFormEntity flowFormEntity, String id) {
        Map<String, Object> allDataMap = new HashMap<>();

        FormDataModel formData = JsonUtil.getJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
        //是否开启并发锁
        String version = "";
        if (formData.getConcurrencyLock()) {
            //查询
            version = TableFeildsEnum.VERSION.getField();
        }

        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        boolean isSnowFlake = primaryKeyPolicy == 1;

        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(flowFormEntity.getTableJson(), TableModel.class);
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setList(list);
        recursionForm.setTableModelList(tableModelList);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //form的属性
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());

        TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);

        DbLinkEntity linkEntity = serviceUtil.getDbLink(flowFormEntity.getDbLinkId());
        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
            String databaseProductName = conn.getMetaData().getDatabaseProductName();
            boolean oracle = databaseProductName.equalsIgnoreCase("oracle");
            boolean IS_DM = databaseProductName.equalsIgnoreCase("DM DBMS");
            //获取主键
            String pKeyName = flowDataUtil.getKey(conn, mainTable.getTable(), primaryKeyPolicy);
            SqlTable mainSqlTable = SqlTable.of(mainTable.getTable());
            SelectStatementProvider render = SqlBuilder.select(mainSqlTable.allColumns()).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mainAllMap = Optional.ofNullable(flowFormDataMapper.selectOneMappedRow(render)).orElse(new HashMap<>());
            if (mainAllMap.size() == 0) {
                return new HashMap<>();
            }
            //主表
            List<String> mainTableFields = mast.stream().filter(m -> StringUtil.isNotEmpty(m.getFormColumnModel().getFieLdsModel().getVModel()))
                    .map(s ->
                            {
                                String jnpfKey = s.getFormColumnModel().getFieLdsModel().getConfig().getJnpfKey();
                                String modelFiled = s.getFormColumnModel().getFieLdsModel().getVModel();
                                if (oracle || IS_DM) {
                                    List<String> keyList = new ArrayList(){{
                                        add(JnpfKeyConsts.UPLOADFZ);
                                        add(JnpfKeyConsts.UPLOADIMG);
                                        add(JnpfKeyConsts.EDITOR);
                                        add(JnpfKeyConsts.TEXTAREA);
                                    }};
                                    if (keyList.contains(jnpfKey)) {
                                        modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                    }
                                }
                                return modelFiled;
                            }
                    ).collect(Collectors.toList());
            if (StringUtil.isNotEmpty(version)) {
                mainTableFields.add(version);
            }
            mainTableFields.add(pKeyName);
            List<BasicColumn> mainTableBasicColumn = mainTableFields.stream().map(m -> {
                if (m.contains("(")) {
                    String replace = m.replace("dbms_lob.substr(", "");
                    String alisaName = replace.replace(")", "");
                    return SqlTable.of(mainTable.getTable()).column(m).as(alisaName);
                } else {
                    return SqlTable.of(mainTable.getTable()).column(m);
                }
            }).collect(Collectors.toList());
            SelectStatementProvider mainRender = SqlBuilder.select(mainTableBasicColumn).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mainMap = flowFormDataMapper.selectOneMappedRow(mainRender);
            if (ObjectUtil.isNotEmpty(mainMap)) {
                DynamicDataSourceUtil.clearSwitchDataSource();
                //转换主表里的数据
                List<FieLdsModel> mainFieldList = mast.stream().filter(m -> StringUtil.isNotEmpty(m.getFormColumnModel().getFieLdsModel().getVModel()))
                        .map(t -> t.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());
                mainMap = formInfoUtils.swapDataInfoType(mainFieldList, mainMap);
                allDataMap.putAll(mainMap);
            }

            //列表子表
            Map<String, List<FormMastTableModel>> groupByTableNames = mastTable.stream().map(mt -> mt.getFormMastTableModel()).collect(Collectors.groupingBy(ma -> ma.getTable()));
            Iterator<Map.Entry<String, List<FormMastTableModel>>> entryIterator = groupByTableNames.entrySet().iterator();
            while (entryIterator.hasNext()) {
                DynamicDataSourceUtil.switchToDataSource(linkEntity);
                Map.Entry<String, List<FormMastTableModel>> next = entryIterator.next();
                String childTableName = next.getKey();
                List<FormMastTableModel> childMastTableList = next.getValue();
                TableModel childTableModel = tableModelList.stream().filter(t -> t.getTable().equals(childTableName)).findFirst().orElse(null);
                SqlTable mastSqlTable = SqlTable.of(childTableName);
                List<BasicColumn> mastTableBasicColumn = childMastTableList.stream().filter(m -> StringUtil.isNotEmpty(m.getField()))
                        .map(m -> {
                            String jnpfKey = m.getMastTable().getFieLdsModel().getConfig().getJnpfKey();
                            String modelFiled = m.getField();
                            String aliasName = "";
                            if (oracle || IS_DM) {
                                List<String> keyList = new ArrayList(){{
                                    add(JnpfKeyConsts.UPLOADFZ);
                                    add(JnpfKeyConsts.UPLOADIMG);
                                    add(JnpfKeyConsts.EDITOR);
                                    add(JnpfKeyConsts.TEXTAREA);
                                }};
                                if (keyList.contains(jnpfKey)) {
                                    aliasName = m.getField();
                                    modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                }
                            }
                            return StringUtil.isEmpty(aliasName) ? mastSqlTable.column(modelFiled) : mastSqlTable.column(modelFiled).as(aliasName);
                        }).collect(Collectors.toList());
                String relation = isSnowFlake ? childTableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                String relationValue = String.valueOf(FormPublicUtils.mapKeyToLower(mainAllMap).get(relation));
                SelectStatementProvider mastRender = SqlBuilder.select(mastTableBasicColumn).from(mastSqlTable).where(mastSqlTable.column(childTableModel.getTableField()),
                        SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);
                Map<String, Object> soloDataMap = flowFormDataMapper.selectOneMappedRow(mastRender);
                if (ObjectUtil.isNotEmpty(soloDataMap)) {
                    Map<String, Object> renameKeyMap = new HashMap<>();
                    for (Map.Entry entry : soloDataMap.entrySet()) {
                        FormMastTableModel model = childMastTableList.stream().filter(child -> child.getField().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().orElse(null);
                        renameKeyMap.put(model.getVModel(), entry.getValue());
                    }
                    List<FieLdsModel> columnChildFields = childMastTableList.stream().map(cl -> cl.getMastTable().getFieLdsModel()).collect(Collectors.toList());
                    DynamicDataSourceUtil.clearSwitchDataSource();
                    renameKeyMap = formInfoUtils.swapDataInfoType(columnChildFields, renameKeyMap);
                    allDataMap.putAll(renameKeyMap);
                }
            }

            //设计子表
            table.stream().map(t -> t.getChildList()).forEach(
                    t1 -> {
                        try {
                            DynamicDataSourceUtil.switchToDataSource(linkEntity);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        try {
                            String childTableName = t1.getTableName();
                            TableModel tableModel = tableModelList.stream().filter(tm -> tm.getTable().equals(childTableName)).findFirst().orElse(null);
                            SqlTable childSqlTable = SqlTable.of(childTableName);
                            String childkey = flowDataUtil.getKey(conn, childTableName, primaryKeyPolicy);
                            List<BasicColumn> childFields = t1.getChildList().stream().filter(t2 -> StringUtil.isNotEmpty(t2.getFieLdsModel().getVModel()))
                                    .map(
                                            t2 -> {
                                                String jnpfKey = t2.getFieLdsModel().getConfig().getJnpfKey();
                                                String modelFiled = t2.getFieLdsModel().getVModel();
                                                String aliasName = "";
                                                if (oracle || IS_DM) {
                                                    List<String> keyList = new ArrayList(){{
                                                        add(JnpfKeyConsts.UPLOADFZ);
                                                        add(JnpfKeyConsts.UPLOADIMG);
                                                        add(JnpfKeyConsts.EDITOR);
                                                        add(JnpfKeyConsts.TEXTAREA);
                                                    }};
                                                    if (keyList.contains(jnpfKey)) {
                                                        aliasName = t2.getFieLdsModel().getVModel();
                                                        modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                                    }
                                                }
                                                return StringUtil.isEmpty(aliasName) ? childSqlTable.column(modelFiled) : childSqlTable.column(modelFiled).as(aliasName);
                                            }).collect(Collectors.toList());
                            childFields.add(childSqlTable.column(childkey));
                            String relation = isSnowFlake ? tableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                            String relationValue = String.valueOf(FormPublicUtils.mapKeyToLower(mainAllMap).get(relation));
                            SelectStatementProvider childRender = SqlBuilder.select(childFields).from(childSqlTable).where(childSqlTable.column(tableModel.getTableField()),
                                    SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);
                            List<Map<String, Object>> childMapList = flowFormDataMapper.selectManyMappedRows(childRender);
                            if (ObjectUtil.isNotEmpty(childMapList)) {
                                DynamicDataSourceUtil.clearSwitchDataSource();
                                List<FieLdsModel> childFieldModels = t1.getChildList().stream().map(t2 -> t2.getFieLdsModel()).collect(Collectors.toList());
                                childMapList = childMapList.stream().map(c1 -> {
                                    try {
                                        return formInfoUtils.swapDataInfoType(childFieldModels, c1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return c1;
                                }).collect(Collectors.toList());
                                Map<String, Object> childMap = new HashMap<>(1);
                                childMap.put(t1.getTableModel(), childMapList);
                                allDataMap.putAll(childMap);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            for (String key : allDataMap.keySet()) {
                if (pKeyName.equalsIgnoreCase(key)) {
                    allDataMap.put("id" , allDataMap.get(key));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return allDataMap;
    }
}
