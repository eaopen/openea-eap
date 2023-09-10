package org.openea.eap.extj.onlinedev.service.impl;

import cn.hutool.core.util.ObjectUtil;
import lombok.Cleanup;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.form.mapper.FlowFormDataMapper;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;
import org.openea.eap.extj.onlinedev.service.VisualDevInfoService;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineDevInfoUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.openea.eap.extj.form.util.TableFeildsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisualDevInfoServiceImpl implements VisualDevInfoService {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;
    @Autowired
    private FlowFormDataMapper visualDataMapper;

    @Override
    public VisualdevModelDataInfoVO getEditDataInfo(String id, VisualdevEntity visualdevEntity) {
        VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
        Map<String, Object> allDataMap = new HashMap<>();

        FormDataModel formData = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        //是否开启并发锁
        String version = "";
        if (formData.getConcurrencyLock()) {
            //查询
            version = TableFeildsEnum.VERSION.getField();
        }

        Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
        boolean isSnowFlake = visualdevEntity.getEnableFlow() == 0;
        if (primaryKeyPolicy == 2 && isSnowFlake) {
            primaryKeyPolicy = 1;
        }
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
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

        DbLinkEntity linkEntity = dblinkService.getInfo(visualdevEntity.getDbLinkId());
        try {
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
            String databaseProductName = conn.getMetaData().getDatabaseProductName();
            boolean oracle = databaseProductName.equalsIgnoreCase("oracle");
            boolean IS_DM = databaseProductName.equalsIgnoreCase("DM DBMS");
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            if (visualdevEntity.getEnableFlow() == 0 && formData.getPrimaryKeyPolicy() == 2) {
                primaryKeyPolicy = 1;
            }
            //获取主键
            String pKeyName = flowFormDataUtil.getKey(conn, mainTable.getTable(), primaryKeyPolicy);
            //主表所有数据
            SqlTable mainSqlTable = SqlTable.of(mainTable.getTable());
            SelectStatementProvider render = SqlBuilder.select(mainSqlTable.allColumns()).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mainAllMap = Optional.ofNullable(visualDataMapper.selectOneMappedRow(render)).orElse(new HashMap<>());
            if (mainAllMap.size() == 0) {
                return vo;
            }
            //主表
            List<String> mainTableFields = mast.stream().filter(m -> StringUtil.isNotEmpty(m.getFormColumnModel().getFieLdsModel().getVModel()))
                    .map(s ->
                            {
                                String jnpfKey = s.getFormColumnModel().getFieLdsModel().getConfig().getJnpfKey();
                                String modelFiled = s.getFormColumnModel().getFieLdsModel().getVModel();
                                if (oracle || IS_DM) {
                                    if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                        modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                    }
                                }
                                return modelFiled;
                            }
                    ).collect(Collectors.toList());
            if (StringUtil.isNotEmpty(version)) {
                mainTableFields.add(version);
            }
            List<BasicColumn> mainTableBasicColumn = mainTableFields.stream().map(m -> {
                if (m.contains("(")) {
                    String replace = m.replace("dbms_lob.substr(", "");
                    String alisaName = replace.replace(")", "");
                    return SqlTable.of(mainTable.getTable()).column(m).as(alisaName);
                } else {
                    return SqlTable.of(mainTable.getTable()).column(m);
                }
            }).collect(Collectors.toList());
            //无字段时查询主键
            mainTableBasicColumn.add(SqlTable.of(mainTable.getTable()).column(pKeyName));

            SelectStatementProvider mainRender = SqlBuilder.select(mainTableBasicColumn).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mainMap = visualDataMapper.selectOneMappedRow(mainRender);
            if (ObjectUtil.isNotEmpty(mainMap)) {
                //转换主表里的数据
                List<FieLdsModel> mainFieldList = mast.stream().filter(m -> StringUtil.isNotEmpty(m.getFormColumnModel().getFieLdsModel().getVModel()))
                        .map(t -> t.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());
                mainMap = OnlineDevInfoUtils.swapDataInfoType(mainFieldList, mainMap);
                allDataMap.putAll(mainMap);
            }

            //列表子表
            Map<String, List<FormMastTableModel>> groupByTableNames = mastTable.stream().map(mt -> mt.getFormMastTableModel()).collect(Collectors.groupingBy(ma -> ma.getTable()));
            Iterator<Map.Entry<String, List<FormMastTableModel>>> entryIterator = groupByTableNames.entrySet().iterator();
            while (entryIterator.hasNext()) {
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
                                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                    aliasName = m.getField();
                                    modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                }
                            }
                            return StringUtil.isEmpty(aliasName) ? mastSqlTable.column(modelFiled) : mastSqlTable.column(modelFiled).as(aliasName);
                        }).collect(Collectors.toList());
                //添加副表关联字段，不然数据会空没有字段名称
                mastTableBasicColumn.add(mastSqlTable.column(childTableModel.getTableField()));
                String relation = isSnowFlake ? childTableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                String relationValue = String.valueOf(OnlinePublicUtils.mapKeyToLower(mainAllMap).get(relation));
                SelectStatementProvider mastRender = SqlBuilder.select(mastTableBasicColumn).from(mastSqlTable).where(mastSqlTable.column(childTableModel.getTableField()),
                        SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);
                Map<String, Object> soloDataMap = visualDataMapper.selectOneMappedRow(mastRender);
                if (ObjectUtil.isNotEmpty(soloDataMap)) {
                    Map<String, Object> renameKeyMap = new HashMap<>();
                    for (Map.Entry entry : soloDataMap.entrySet()) {
                        FormMastTableModel model = childMastTableList.stream().filter(child -> child.getField().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().orElse(null);
                        if (model != null) {
                            renameKeyMap.put(model.getVModel(), entry.getValue());
                        }
                    }
                    List<FieLdsModel> columnChildFields = childMastTableList.stream().map(cl -> cl.getMastTable().getFieLdsModel()).collect(Collectors.toList());
                    renameKeyMap = OnlineDevInfoUtils.swapDataInfoType(columnChildFields, renameKeyMap);
                    allDataMap.putAll(renameKeyMap);
                }
            }

            //设计子表
            table.stream().map(t -> t.getChildList()).forEach(
                    t1 -> {
                        String childTableName = t1.getTableName();
                        TableModel tableModel = tableModelList.stream().filter(tm -> tm.getTable().equals(childTableName)).findFirst().orElse(null);
                        SqlTable childSqlTable = SqlTable.of(childTableName);
                        List<BasicColumn> childFields = t1.getChildList().stream().filter(t2 -> StringUtil.isNotEmpty(t2.getFieLdsModel().getVModel()))
                                .map(
                                        t2 -> {
                                            String jnpfKey = t2.getFieLdsModel().getConfig().getJnpfKey();
                                            String modelFiled = t2.getFieLdsModel().getVModel();
                                            String aliasName = "";
                                            if (oracle || IS_DM) {
                                                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG)
                                                        || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                                    aliasName = t2.getFieLdsModel().getVModel();
                                                    modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                                }
                                            }
                                            return StringUtil.isEmpty(aliasName) ? childSqlTable.column(modelFiled) : childSqlTable.column(modelFiled).as(aliasName);
                                        }).collect(Collectors.toList());
                        String relation = isSnowFlake ? tableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                        String relationValue = String.valueOf(OnlinePublicUtils.mapKeyToLower(mainAllMap).get(relation));
                        SelectStatementProvider childRender = SqlBuilder.select(childFields).from(childSqlTable).where(childSqlTable.column(tableModel.getTableField()),
                                SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);
                        List<Map<String, Object>> childMapList = visualDataMapper.selectManyMappedRows(childRender);
                        Map<String, Object> childMap = new HashMap<>(1);
                        if (ObjectUtil.isNotEmpty(childMapList)) {
                            List<FieLdsModel> childFieldModels = t1.getChildList().stream().map(t2 -> t2.getFieLdsModel()).collect(Collectors.toList());
                            childMapList = childMapList.stream().map(c1 -> {
                                try {
                                    return OnlineDevInfoUtils.swapDataInfoType(childFieldModels, c1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return c1;
                            }).collect(Collectors.toList());
                            childMap.put(t1.getTableModel(), childMapList);
                        } else {
                            childMap.put(t1.getTableModel(), new ArrayList<>());
                        }
                        allDataMap.putAll(childMap);
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        vo.setId(id);
        vo.setData(JsonUtilEx.getObjectToString(allDataMap));
        return vo;
    }

    @Override
    public VisualdevModelDataInfoVO getDetailsDataInfo(String id, VisualdevEntity visualdevEntity) {
        VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
        Map<String, Object> allDataMap = new HashMap<>();
        FormDataModel formData = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<TableModel> tableModelList = JsonUtil.getJsonToList(visualdevEntity.getVisualTables(), TableModel.class);
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setList(list);
        recursionForm.setTableModelList(tableModelList);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //form的属性
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> table = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormModel> codeList = formAllModel.stream().filter(t -> t.getJnpfKey().equals(FormEnum.BARCODE.getMessage())
                || t.getJnpfKey().equals(FormEnum.QR_CODE.getMessage())).map(formModel -> formModel.getFormModel()).collect(Collectors.toList());

        TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1")).findFirst().orElse(null);
        boolean isSnowFlake = visualdevEntity.getEnableFlow() == 0;
        DbLinkEntity linkEntity = dblinkService.getInfo(visualdevEntity.getDbLinkId());
        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
            String databaseProductName = conn.getMetaData().getDatabaseProductName();
            boolean oracle = databaseProductName.equalsIgnoreCase("oracle");
            boolean IS_DM = databaseProductName.equalsIgnoreCase("DM DBMS");
            //获取主键
            Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
            if (primaryKeyPolicy == 2 && isSnowFlake) {
                primaryKeyPolicy = 1;
            }
            String pKeyName = flowFormDataUtil.getKey(conn, mainTable.getTable(), primaryKeyPolicy);
            //主表所有数据
            SqlTable mainSqlTable = SqlTable.of(mainTable.getTable());
            SelectStatementProvider render = SqlBuilder.select(mainSqlTable.allColumns()).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            Map<String, Object> mainAllMap = Optional.ofNullable(visualDataMapper.selectOneMappedRow(render)).orElse(new HashMap<>());
            if (mainAllMap.size() == 0) {
                return vo;
            }
            //主表
            List<String> mainTableFields = mast.stream().filter(m -> StringUtil.isNotEmpty(m.getFormColumnModel().getFieLdsModel().getVModel()))
                    .map(s ->
                            {
                                String jnpfKey = s.getFormColumnModel().getFieLdsModel().getConfig().getJnpfKey();
                                String modelFiled = s.getFormColumnModel().getFieLdsModel().getVModel();
                                if (oracle || IS_DM) {
                                    if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)
                                            || jnpfKey.equals(JnpfKeyConsts.ADDRESS)) {
                                        modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                    }
                                }
                                return modelFiled;
                            }
                    ).collect(Collectors.toList());
            List<BasicColumn> mainTableBasicColumn = mainTableFields.stream().map(m -> {
                if (m.contains("(")) {
                    String replace = m.replace("dbms_lob.substr(", "");
                    String alisaName = replace.replace(")", "");
                    return SqlTable.of(mainTable.getTable()).column(m).as(alisaName);
                } else {
                    return SqlTable.of(mainTable.getTable()).column(m);
                }
            }).collect(Collectors.toList());
            //无字段时查询主键
            mainTableBasicColumn.add(SqlTable.of(mainTable.getTable()).column(pKeyName));

            SelectStatementProvider mainRender = SqlBuilder.select(mainTableBasicColumn).from(mainSqlTable).where(mainSqlTable.column(pKeyName),
                    SqlBuilder.isEqualTo(id)).build().render(RenderingStrategies.MYBATIS3);
            List<Map<String, Object>> mapList = visualDataMapper.selectManyMappedRows(mainRender);

            //转换主表里的数据
            List<FieLdsModel> mainFieldList = mast.stream()
                    .map(t -> t.getFormColumnModel().getFieLdsModel()).collect(Collectors.toList());
            mainFieldList = mainFieldList.stream().map(fieLdsModel -> {
                if (ObjectUtil.isNotEmpty(fieLdsModel.getProps())) {
                    if (ObjectUtil.isNotEmpty(fieLdsModel.getProps().getProps())) {
                        PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                        fieLdsModel.getProps().setPropsModel(propsBeanModel);
                    }
                }
                return fieLdsModel;
            }).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(mapList) && mapList.size() > 0) {
                mapList = onlineSwapDataUtils.getSwapInfo(mapList, mainFieldList, visualdevEntity.getId(), false, codeList);
                if (mapList.size() > 0) {
                    allDataMap.putAll(mapList.get(0));
                }
            }

            //列表子表
            Map<String, List<FormMastTableModel>> groupByTableNames = mastTable.stream().map(mt -> mt.getFormMastTableModel()).collect(Collectors.groupingBy(ma -> ma.getTable()));
            Iterator<Map.Entry<String, List<FormMastTableModel>>> entryIterator = groupByTableNames.entrySet().iterator();
            while (entryIterator.hasNext()) {
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
                                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                    aliasName = m.getField();
                                    modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                }
                            }
                            return StringUtil.isEmpty(aliasName) ? mastSqlTable.column(modelFiled) : mastSqlTable.column(modelFiled).as(aliasName);
                        }).collect(Collectors.toList());
                //添加副表关联字段，不然数据会空没有字段名称
                mastTableBasicColumn.add(mastSqlTable.column(childTableModel.getTableField()));
                String relation = isSnowFlake ? childTableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                String relationValue = String.valueOf(OnlinePublicUtils.mapKeyToLower(mainAllMap).get(relation));
                SelectStatementProvider mastRender = SqlBuilder.select(mastTableBasicColumn).from(mastSqlTable).where(mastSqlTable.column(childTableModel.getTableField()),
                        SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);
                Map<String, Object> soloDataMap = visualDataMapper.selectOneMappedRow(mastRender);
                if (ObjectUtil.isNotEmpty(soloDataMap)) {
                    Map<String, Object> renameKeyMap = new HashMap<>();
                    for (Map.Entry entry : soloDataMap.entrySet()) {
                        FormMastTableModel model = childMastTableList.stream().filter(child -> child.getField().equalsIgnoreCase(String.valueOf(entry.getKey()))).findFirst().orElse(null);
                        if (model != null) {
                            renameKeyMap.put(model.getVModel(), entry.getValue());
                        }
                    }
                    List<FieLdsModel> columnChildFields = childMastTableList.stream().map(cl -> cl.getMastTable().getFieLdsModel()).collect(Collectors.toList());
                    columnChildFields = columnChildFields.stream().map(fieLdsModel -> {
                        if (ObjectUtil.isNotEmpty(fieLdsModel.getProps())) {
                            if (ObjectUtil.isNotEmpty(fieLdsModel.getProps().getProps())) {
                                PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                                fieLdsModel.getProps().setPropsModel(propsBeanModel);
                            }
                        }
                        return fieLdsModel;
                    }).collect(Collectors.toList());
                    List<Map<String, Object>> mapList1 = new ArrayList<>();
                    mapList1.add(renameKeyMap);
                    mapList1 = onlineSwapDataUtils.getSwapInfo(mapList1, columnChildFields, visualdevEntity.getId(), false, codeList);
                    allDataMap.putAll(mapList1.get(0));
                }
            }

            //设计子表
            table.stream().map(t -> t.getChildList()).forEach(
                    t1 -> {
                        String childTableName = t1.getTableName();
                        TableModel tableModel = tableModelList.stream().filter(tm -> tm.getTable().equals(childTableName)).findFirst().orElse(null);
                        SqlTable childSqlTable = SqlTable.of(childTableName);
                        List<BasicColumn> childFields = t1.getChildList().stream().filter(t2 -> StringUtil.isNotEmpty(t2.getFieLdsModel().getVModel()))
                                .map(
                                        t2 -> {
                                            String jnpfKey = t2.getFieLdsModel().getConfig().getJnpfKey();
                                            String modelFiled = t2.getFieLdsModel().getVModel();
                                            String aliasName = "";
                                            if (oracle || IS_DM) {
                                                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG)
                                                        || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                                    aliasName = t2.getFieLdsModel().getVModel();
                                                    modelFiled = "dbms_lob.substr( " + modelFiled + ")";
                                                }
                                            }
                                            return StringUtil.isEmpty(aliasName) ? childSqlTable.column(modelFiled) : childSqlTable.column(modelFiled).as(aliasName);
                                        }).collect(Collectors.toList());
                        String relation = isSnowFlake ? tableModel.getRelationField().toLowerCase() : TableFeildsEnum.FLOWTASKID.getField();
                        String relationValue = String.valueOf(OnlinePublicUtils.mapKeyToLower(mainAllMap).get(relation));

                        SelectStatementProvider childRender = SqlBuilder.select(childFields).from(childSqlTable).where(childSqlTable.column(tableModel.getTableField()),
                                SqlBuilder.isEqualTo(relationValue)).build().render(RenderingStrategies.MYBATIS3);

                        List<Map<String, Object>> childMapList = visualDataMapper.selectManyMappedRows(childRender);
                        if (ObjectUtil.isNotEmpty(childMapList)) {
                            List<FieLdsModel> childFieldModels = t1.getChildList().stream().map(t2 -> t2.getFieLdsModel()).collect(Collectors.toList());
                            childMapList = onlineSwapDataUtils.getSwapInfo(childMapList, childFieldModels, visualdevEntity.getId(), false, codeList);
                        }
                        Map<String, Object> childMap = new HashMap<>(1);
                        childMap.put(t1.getTableModel(), childMapList);
                        allDataMap.putAll(childMap);

                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        vo.setId(id);
        vo.setData(JsonUtilEx.getObjectToString(allDataMap));
        return vo;
    }
}
