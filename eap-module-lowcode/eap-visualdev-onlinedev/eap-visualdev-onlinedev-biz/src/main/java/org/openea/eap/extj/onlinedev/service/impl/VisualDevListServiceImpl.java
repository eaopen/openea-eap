package org.openea.eap.extj.onlinedev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.aggregate.AbstractCount;
import org.mybatis.dynamic.sql.select.join.EqualTo;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.VisualdevShortLinkEntity;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.FilterService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.VisualdevShortLinkService;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.engine.service.FlowTemplateJsonService;
import org.openea.eap.extj.engine.service.FlowTemplateService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.mapper.FlowFormDataMapper;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.model.visualJson.superQuery.ConditionJsonModel;
import org.openea.eap.extj.model.visualJson.superQuery.SuperQueryJsonModel;
import org.openea.eap.extj.onlinedev.entity.VisualdevModelDataEntity;
import org.openea.eap.extj.onlinedev.mapper.VisualdevModelDataMapper;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.extj.onlinedev.service.VisualDevListService;
import org.openea.eap.extj.onlinedev.util.AutoFeildsUtil;
import org.openea.eap.extj.onlinedev.util.OnlineDevDbUtil;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.*;
import org.openea.eap.extj.permission.model.authorize.OnlineDynamicSqlModel;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.openea.eap.extj.form.util.TableFeildsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线开发列表
 */
@Service
public class VisualDevListServiceImpl extends SuperServiceImpl<VisualdevModelDataMapper, VisualdevModelDataEntity> implements VisualDevListService {

    public static List<String> needAllFieldsDB = new ArrayList() {{
        add("Microsoft SQL Server");
        add("DM DBMS");
        add("PostgreSQL");
        add("Oracle");
    }};
    public static List<String> needUpcaseFieldsDB = new ArrayList() {{
        add("DM DBMS");
        add("Oracle");
    }};
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private OnlineDevDbUtil onlineDevDbUtil;
    @Autowired
    private VisualdevShortLinkService visualdevShortLinkService;
    @Autowired
    private FlowTemplateService flowTemplateService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;
    @Autowired
    private FilterService filterService;

    @Override
    public List<Map<String, Object>> getDataList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {

        List<Map<String, Object>> realList;
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        TableModel mainTable = visualTables.stream().filter(vi -> vi.getTypeId().equals("1" )).findFirst().orElse(null);
        //解析所有控件
        RecursionForm recursionForm = new RecursionForm(fieLdsModels, visualTables);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);

        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        List<VisualColumnSearchVO> searchVOS = new ArrayList<>();
        //封装查询条件
        if (StringUtil.isNotEmpty(paginationModel.getQueryJson())) {
            Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
            searchVOList = JsonUtil.getJsonToList(columnDataModel.getSearchList(), VisualColumnSearchVO.class);
            searchVOList = searchVOList.stream().map(searchVO -> {
                searchVO.setValue(keyJsonMap.get(searchVO.getVModel()));
                return searchVO;
            }).filter(vo -> vo.getValue() != null).collect(Collectors.toList());
            //左侧树查询
            boolean b = false;
            if (columnDataModel.getTreeRelation() != null) {
                b = keyJsonMap.keySet().stream().anyMatch(t -> t.equalsIgnoreCase(String.valueOf(columnDataModel.getTreeRelation())));
            }
            if (b && keyJsonMap.size() > searchVOList.size()) {
                String relation = String.valueOf(columnDataModel.getTreeRelation());
                String treeDataSource = columnDataModel.getTreeDataSource();
                VisualColumnSearchVO vo = new VisualColumnSearchVO();
                //从列表控件中 赋予左侧树查询条件属性
                FieLdsModel fieLdsModel = AutoFeildsUtil.getTreeRelationSearch(fieLdsModels, relation);
                vo.setConfig(Objects.nonNull(fieLdsModel) ? fieLdsModel.getConfig() : null);
                Boolean multiple = fieLdsModel.getMultiple();
                if (treeDataSource.equals("organize" )) {
                    vo.setSearchType("1" );
                } else {
                    vo.setSearchType(multiple ? "2" : "1" );
                }
                vo.setVModel(relation);
                vo.setValue(keyJsonMap.get(relation));
                searchVOList.add(vo);
            }
        }
        //递归处理控件
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        visualDevJsonModel.setFormListModels(fields);
        //判断有无表
        if (visualTables.size() > 0) {
            //当前用户信息
            UserInfo userInfo = userProvider.get();
            //菜单id
            String moduleId = paginationModel.getMenuId();
            for (VisualColumnSearchVO vo : searchVOList) {
                String vModel = vo.getVModel();
                if (vModel.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {
                    vo.setField(vModel.substring(vModel.lastIndexOf("-" ) + 1));
                    String tablename=StringUtil.isNotEmpty(vo.getConfig().getRelationTable())?vo.getConfig().getRelationTable():"";
                    if(StringUtil.isEmpty(tablename)){
                        for(FormAllModel vi:formAllModel){
                            if("table".equals(vi.getJnpfKey())){
                                if(vi.getChildList().getTableModel().equals(vModel.substring(0, vModel.lastIndexOf("-" )))){
                                    tablename=vi.getChildList().getTableName();
                                }
                            }
                        }
                    }
                    vo.setTable(tablename);
                } else if (vModel.contains("jnpf_" )) {
                    String fieldName = vModel.substring(vModel.lastIndexOf("jnpf_" )).replace("jnpf_" , "" );
                    String tableName = vModel.substring(vModel.indexOf("_" ) + 1, vModel.lastIndexOf("_jnpf" ));
                    vo.setTable(tableName);
                    vo.setField(fieldName);
                } else {
                    vo.setField(vModel);
                    vo.setTable(mainTable.getTable());
                }
                searchVOS.add(vo);
            }
            realList = this.getListWithTable(visualDevJsonModel, paginationModel, userInfo, moduleId, searchVOS, null);
        } else {
            realList = this.getWithoutTableData(visualDevJsonModel.getId());
            realList = this.getList(realList, searchVOS, paginationModel);
        }

        if (realList.size() < 1) {
            return realList;
        }
        //编辑表格(行内编辑)
        boolean inlineEdit = columnDataModel.getType() != null && columnDataModel.getType() == 4;

        //复制父级字段+_id
        realList.forEach(item -> {
            item.put(columnDataModel.getParentField() + "_id" , item.get(columnDataModel.getParentField()));
        });
        //数据转换
        realList = onlineSwapDataUtils.getSwapList(realList, fields, visualDevJsonModel.getId(), inlineEdit, new ArrayList<>());

        //取回传主键
        String pkeyId = visualDevJsonModel.getPkeyId()!=null ? visualDevJsonModel.getPkeyId(): TableFeildsEnum.FID.getField();
        //结果集添加id
        for (Map<String, Object> objectMap : realList) {
            for(String key : objectMap.keySet()){
                if(pkeyId.equalsIgnoreCase(key)){
                    objectMap.put("id",objectMap.get(key));
                }
            }
        }
        //树形子字段key
        columnDataModel.setSubField(pkeyId);

        //添加流程状态
        if (visualDevJsonModel.isFlowEnable()) {
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(visualDevJsonModel.getId());
            if (templateInfo == null) {
                throw new WorkFlowException("该功能未配置流程不可用!" );
            }
            List<String> ids = realList.stream().map(i -> i.get("id" ).toString()).collect(Collectors.toList());
            List<FlowTaskEntity> tasks = flowTaskService.getInfosSubmit(ids.toArray(new String[]{}), FlowTaskEntity::getStatus, FlowTaskEntity::getId, FlowTaskEntity::getProcessId);
            realList.stream().forEach(m -> {
                String id = m.get("id" ).toString();
                m.put("flowState" , "" );
                tasks.forEach(i -> {
                    if (i.getId().equals(id) || i.getProcessId().equals(id)) {
                        m.put("flowState" , i.getStatus());
                    }
                });
            });
        }
        return realList;
    }

    @Override
    public List<Map<String, Object>> getDataListLink(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {
        List<Map<String, Object>> realList;
        VisualdevShortLinkEntity shortLinkEnt = visualdevShortLinkService.getById(visualDevJsonModel.getId());
        List<VisualColumnSearchVO> listCondition = StringUtil.isNotEmpty(shortLinkEnt.getColumnCondition()) ? JsonUtil.getJsonToList(shortLinkEnt.getColumnCondition(), VisualColumnSearchVO.class) : new ArrayList<>();
        List<FieLdsModel> listFields = StringUtil.isNotEmpty(shortLinkEnt.getColumnCondition()) ? JsonUtil.getJsonToList(shortLinkEnt.getColumnText(), FieLdsModel.class) : new ArrayList<>();
        visualDevJsonModel.setFormListModels(listFields);
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        TableModel mainTable = visualTables.stream().filter(vi -> vi.getTypeId().equals("1" )).findFirst().orElse(null);
        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        List<VisualColumnSearchVO> searchVOS = new ArrayList<>();
        //封装查询条件
        if (StringUtil.isNotEmpty(paginationModel.getQueryJson())) {
            Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
            searchVOList = listCondition;
            searchVOList = searchVOList.stream().map(searchVO -> {
                searchVO.setSearchType("2" );//外链写死模糊查询
                searchVO.setValue(keyJsonMap.get(searchVO.getVModel()));
                return searchVO;
            }).filter(vo -> vo.getValue() != null).collect(Collectors.toList());
        }

        //当前用户信息
        UserInfo userInfo = userProvider.get();
        //菜单id
        String moduleId = paginationModel.getMenuId();
        for (VisualColumnSearchVO vo : searchVOList) {
            String vModel = vo.getVModel();
            if (vModel.toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {
                vo.setField(vModel.substring(vModel.lastIndexOf("-" ) + 1));
                vo.setTable(vo.getConfig().getRelationTable());
            } else if (vModel.contains("jnpf_" )) {
                String fieldName = vModel.substring(vModel.lastIndexOf("jnpf_" )).replace("jnpf_" , "" );
                String tableName = vModel.substring(vModel.indexOf("_" ) + 1, vModel.lastIndexOf("_jnpf" ));
                vo.setTable(tableName);
                vo.setField(fieldName);
            } else {
                vo.setField(vModel);
                vo.setTable(mainTable.getTable());
            }
            searchVOS.add(vo);
        }

        ColumnDataModel columnDataModel = new ColumnDataModel();
        List<ColumnListField> list = JsonUtil.getJsonToList(shortLinkEnt.getColumnText(), ColumnListField.class);
        columnDataModel.setColumnList(JsonUtil.getListToJsonArray(list).toJSONString());//查询字段构造
        columnDataModel.setType(1);//普通列表
        visualDevJsonModel.setColumnData(columnDataModel);
        //查询
        realList = this.getListWithTable(visualDevJsonModel, paginationModel, userInfo, moduleId, searchVOS, null);
        if (realList.size() < 1) {
            return realList;
        }
        //数据转换
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        visualDevJsonModel.setFormListModels(fields);
        realList = onlineSwapDataUtils.getSwapList(realList, fields, visualDevJsonModel.getId(), false, new ArrayList<>());

        //添加流程状态
        if (visualDevJsonModel.isFlowEnable()) {
            FlowTemplateInfoVO templateInfo = flowTemplateService.info(visualDevJsonModel.getId());
            if (templateInfo == null) {
                throw new WorkFlowException("该功能未配置流程不可用!" );
            }
            List<String> ids = realList.stream().map(i -> i.get("id" ).toString()).collect(Collectors.toList());
            List<FlowTaskEntity> tasks = flowTaskService.getInfosSubmit(ids.toArray(new String[]{}), FlowTaskEntity::getStatus, FlowTaskEntity::getId, FlowTaskEntity::getProcessId);
            realList.stream().forEach(m -> {
                String id = m.get("id" ).toString();
                m.put("flowState" , "" );
                tasks.forEach(i -> {
                    if (i.getId().equals(id) || i.getProcessId().equals(id)) {
                        m.put("flowState" , i.getStatus());
                    }
                });
            });
        }
        return realList;
    }

    @Override
    public List<Map<String, Object>> getList(List<Map<String, Object>> noSwapDataList, List<VisualColumnSearchVO> searchVOList, PaginationModel paginationModel) {
        if (searchVOList.size() > 0) {
            //条件查询
            noSwapDataList = OnlineDevListUtils.getNoSwapList(noSwapDataList, searchVOList);
        }
        //排序
        if (noSwapDataList.size() > 0) {
            if (StringUtil.isNotEmpty(paginationModel.getSidx())) {
                //排序处理
                noSwapDataList.sort((o1, o2) -> {
                    Map<String, Object> i1 = o1;
                    Map<String, Object> i2 = o2;
                    String s1 = String.valueOf(i1.get(paginationModel.getSidx()));
                    String s2 = String.valueOf(i2.get(paginationModel.getSidx()));
                    if ("desc".equalsIgnoreCase(paginationModel.getSort())) {
                        return s2.compareTo(s1);
                    } else {
                        return s1.compareTo(s2);
                    }
                });
            }

            long total = noSwapDataList.size();

            //数据分页
            noSwapDataList = PageUtil.getListPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), noSwapDataList);
            noSwapDataList = paginationModel.setData(noSwapDataList, total);
        }
        return noSwapDataList;
    }

    @Override
    public List<Map<String, Object>> getWithoutTableData(String modelId) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getVisualDevId, modelId);
        List<VisualdevModelDataEntity> list = this.list(queryWrapper);
        List<Map<String, Object>> dataVoList = list.parallelStream().map(t -> {
            Map<String, Object> dataMap = JsonUtil.stringToMap(t.getData());
            dataMap.put("id" , t.getId());
            return dataMap;
        }).collect(Collectors.toList());
        return dataVoList;
    }

    @Override
    public List<Map<String, Object>> getListWithTable(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel,
                                                      UserInfo userInfo, String moduleId, List<VisualColumnSearchVO> searchVOList, List<String> columnPropList) {
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        List<Map<String, Object>> dataList = new ArrayList<>();
        //数据源
        DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());
        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection connection = ConnUtil.getConnOrDefault(linkEntity);
            String databaseProductName = connection.getMetaData().getDatabaseProductName().trim();
            List<TableModel> tableModelList = visualDevJsonModel.getVisualTables();
            //主表
            TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1" )).findFirst().orElse(null);

            List<ColumnListField> modelList = JsonUtil.getJsonToList(columnDataModel.getColumnList(), ColumnListField.class);

            FormDataModel formData = visualDevJsonModel.getFormData();
            List<FieLdsModel> jsonToList = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            //递归处理控件
            List<FieLdsModel> allFieLds = new ArrayList<>();
            OnlinePublicUtils.recursionFields(allFieLds, jsonToList);
            //列表中区别子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";
            //所有字段
            List<String> collect = columnPropList != null ? columnPropList : modelList.stream().map(mode -> mode.getProp()).collect(Collectors.toList());
            if (OnlineDevData.TYPE_FIVE_COLUMNDATA.equals(columnDataModel.getType()) && !collect.contains(columnDataModel.getParentField())) {
                collect.add(columnDataModel.getParentField());
            }

            Map<String, String> tableFieldAndTableName = new HashMap<>(8);
            Map<String, String> tableNameAndTableField = new HashMap<>(8);
            allFieLds.stream().filter(f -> f.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)).forEach(f -> {
                tableFieldAndTableName.put(f.getVModel(), f.getConfig().getTableName());
                tableNameAndTableField.put(f.getConfig().getTableName(), f.getVModel());
            });
            Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
            if(!visualDevJsonModel.isFlowEnable() && primaryKeyPolicy==2){
                primaryKeyPolicy=1;
            }
            String pkeyId = flowFormDataUtil.getKey(connection, mainTable.getTable(), primaryKeyPolicy);
            String childpkeyId = pkeyId;
            visualDevJsonModel.setPkeyId(pkeyId);
            List<OnlineDynamicSqlModel> sqlModelList = new ArrayList<>();
            //根据表字段创建sqltable
            for (TableModel model : tableModelList) {
                OnlineDynamicSqlModel sqlModel = new OnlineDynamicSqlModel();
                sqlModel.setSqlTable(SqlTable.of(model.getTable()));
                sqlModel.setTableName(model.getTable());
                if (model.getTypeId().equals("1" )) {
                    sqlModel.setMain(true);
                } else {
                    sqlModel.setForeign(model.getTableField());
                    sqlModel.setRelationKey(model.getRelationField());
                    sqlModel.setMain(false);
                }
                sqlModelList.add(sqlModel);
            }

            OnlineProductSqlUtils.getColumnListSql(sqlModelList, visualDevJsonModel, collect, linkEntity);
            //主表
            OnlineDynamicSqlModel mainSqlModel = sqlModelList.stream().filter(OnlineDynamicSqlModel::isMain).findFirst().orElse(null);
            //非主表
            List<OnlineDynamicSqlModel> dycList = sqlModelList.stream().filter(dyc -> !dyc.isMain()).collect(Collectors.toList());
            List<BasicColumn> sqlColumns = new ArrayList<>();
            Map<String, String> aliasMap = new HashMap<>();
            boolean isOracle = databaseProductName.equalsIgnoreCase("oracle" );
            boolean isDm =  databaseProductName.equalsIgnoreCase("DM DBMS" );
            for (OnlineDynamicSqlModel dynamicSqlModel : sqlModelList) {
                List<BasicColumn> basicColumns = Optional.ofNullable(dynamicSqlModel.getColumns()).orElse(new ArrayList<>());
                //达梦或者oracle 别名太长转换-底下有方法进行还原
                if (isOracle||isDm) {
                    for (int i = 0; i < basicColumns.size(); i++) {
                        BasicColumn item = basicColumns.get(i);
                        String alias = item.alias().orElse(null);
                        if (StringUtil.isNotEmpty(alias)) {
                            String aliasNewName = "A" + RandomUtil.uuId();
                            aliasMap.put(aliasNewName, alias);
                            basicColumns.set(i, item.as(aliasNewName));
                        }
                    }
                }
                sqlColumns.addAll(basicColumns);
            }
            QueryExpressionDSL<SelectModel> from = SqlBuilder.select(sqlColumns).from(mainSqlModel.getSqlTable());
            AbstractCount countDistinct;
            if(tableModelList.size()>1){
                countDistinct=SqlBuilder.countDistinct(mainSqlModel.getSqlTable().column(pkeyId));
            }else {
                countDistinct = SqlBuilder.count(mainSqlModel.getSqlTable().column(pkeyId));
            }
            QueryExpressionDSL<SelectModel> fromcount = SqlBuilder.select(countDistinct).from(mainSqlModel.getSqlTable());

            QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where = from.where();
            QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder whereCount = fromcount.where();

            //逻辑删除不展示
            if (visualDevJsonModel.getFormData().getLogicalDelete()) {
                where.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DELETEMARK.getField()), SqlBuilder.isNull());
                whereCount.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DELETEMARK.getField()), SqlBuilder.isNull());
            }
            if (searchVOList.size() > 0) {
                //查询条件sql
                OnlineProductSqlUtils.getConditionSql(where, databaseProductName, searchVOList, sqlModelList);
                OnlineProductSqlUtils.getConditionSql(whereCount, databaseProductName, searchVOList, sqlModelList);
            }
            //数据权限
            if (columnDataModel.getUseDataPermission() != null && columnDataModel.getUseDataPermission()) {
                //数据权限
                if (!userInfo.getIsAdministrator()) {
                    boolean conditionSql = authorizeService.getConditionSql(userInfo, moduleId, null, where, sqlModelList);
                   authorizeService.getConditionSql(userInfo, moduleId, null, whereCount, sqlModelList);
                    if (!conditionSql) {
                        //未分配方案不显示数据
                        return Collections.EMPTY_LIST;
                    }
                }
            }

            //高级查询
            String matchLogic = "";
            List<ConditionJsonModel> superQueryList = new ArrayList<>();
            if (StringUtil.isNotEmpty(paginationModel.getSuperQueryJson())) {
                SuperQueryJsonModel superQueryJsonModel = JsonUtil.getJsonToBean(paginationModel.getSuperQueryJson(), SuperQueryJsonModel.class);
                matchLogic = superQueryJsonModel.getMatchLogic();
                superQueryList = JsonUtil.getJsonToList(superQueryJsonModel.getConditionJson(), ConditionJsonModel.class);
                superQueryList.stream().forEach(sup -> {
                    FieLdsModel fieLdsModel = JsonUtil.getJsonToBean(sup.getAttr(), FieLdsModel.class);
                    sup.setTableName(fieLdsModel.getConfig().getRelationTable());
                    String field = sup.getField();
                    String mastKey = fieLdsModel.getConfig().getJnpfKey();
                    if (field.toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {
                        String substring = field.substring(field.lastIndexOf("-" ) + 1);
                        String tableField = field.substring(0, field.indexOf("-" ));
                        sup.setField(substring);
                        sup.setTableName(tableFieldAndTableName.get(tableField));
                    } else if (StringUtil.isEmpty(sup.getTableName())) {
                        sup.setTableName(mainTable.getTable());
                    }

                    if (mastKey.equals(JnpfKeyConsts.CHECKBOX) || mastKey.equals(JnpfKeyConsts.ADDRESS) || mastKey.equals(JnpfKeyConsts.CASCADER) || mastKey.equals(JnpfKeyConsts.COMSELECT)) {
                        fieLdsModel.setMultiple(true);
                    }
                    if (fieLdsModel.getMultiple() && StringUtil.isEmpty(sup.getFieldValue())) {
                        sup.setFieldValue("[]" );
                    }
                });
                OnlineProductSqlUtils.getSuperSql(where, superQueryList, matchLogic, sqlModelList, databaseProductName);
                OnlineProductSqlUtils.getSuperSql(whereCount, superQueryList, matchLogic, sqlModelList, databaseProductName);
            }

            //排序
            String sort = StringUtil.isNotEmpty(paginationModel.getSort()) ? paginationModel.getSort() : "ASC";
            String sidx = StringUtil.isNotEmpty(paginationModel.getSidx()) ? paginationModel.getSidx() : pkeyId;
            String sortTableName;
            String sortField;
            if (sidx.matches(reg)) {
                sortField = sidx.substring(sidx.lastIndexOf("jnpf_" )).replace("jnpf_" , "" );
                sortTableName = sidx.substring(sidx.indexOf("_" ) + 1, sidx.lastIndexOf("_jnpf" ));
            } else {
                sortTableName = mainTable.getTable();
                sortField = sidx;
            }
            SqlColumn<Object> sortCol = SqlTable.of(sortTableName).column(sortField);
            SortSpecification sortSpecification = SqlBuilder.sortColumn(sortTableName, sortCol);
            if (sort.equalsIgnoreCase("ASC" )) {
                where.orderBy(sortSpecification);
            } else {
                where.orderBy(sortSpecification.descending());
            }

            if(tableModelList.size()>1) {
                if (needAllFieldsDB.contains(databaseProductName)) {
                    List<BasicColumn> groupBySqlTable = OnlineProductSqlUtils.getGroupBySqlTable(sqlModelList, visualDevJsonModel, collect,needUpcaseFieldsDB.contains(databaseProductName));
                    //判断列表有无FLOWTASKID FLOWID字段强拼
                    sqlColumns.stream().forEach(t -> {
                        SqlColumn n = (SqlColumn) t;
                        getGroupFeild(TableFeildsEnum.FLOWTASKID, n, databaseProductName, groupBySqlTable, mainTable);
                        getGroupFeild(TableFeildsEnum.FLOWID, n, databaseProductName, groupBySqlTable, mainTable);
                    });
                    groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(pkeyId));
                    where.groupBy(groupBySqlTable);
                } else {
                    where.groupBy(SqlTable.of(mainTable.getTable()).column(pkeyId));
                }
            }

            // 构造table和table下字段的分组
            Map<String, List<String>> tableFieldGroup = new HashMap<>(8);
            allFieLds.forEach(f->{
                tableFieldGroup.computeIfAbsent(f.getConfig().getTableName(), k -> new ArrayList<>()).add(
                        "table".equals(f.getConfig().getType()) ? f.getConfig().getTableName() : f.getVModel()
                );
            });
            Map<String, SqlTable> subSqlTableMap = new HashMap<>();
            if (dycList.size() > 0) {
                for (OnlineDynamicSqlModel sqlModel : dycList) {
                    String relationKey = primaryKeyPolicy == 2 ? childpkeyId : sqlModel.getRelationKey();
                    from.leftJoin(sqlModel.getSqlTable())
                            .on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(relationKey)));
                    fromcount.leftJoin(sqlModel.getSqlTable())
                            .on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(relationKey)));
                    String tableName = sqlModel.getTableName();
                    List<String> fieldList = tableFieldGroup.get(tableName);
                    if(fieldList!=null){
                        fieldList.forEach(fieldKey->{
                            subSqlTableMap.put(fieldKey, sqlModel.getSqlTable());
                        });
                    }
                }
            }
            // 增加过滤条件
            filterService.handleWhereCondition(mainSqlModel.getSqlTable(), where, visualDevJsonModel.getId(),subSqlTableMap, databaseProductName);
            filterService.handleWhereCondition(mainSqlModel.getSqlTable(), whereCount, visualDevJsonModel.getId(),subSqlTableMap, databaseProductName);

            // 1导出全部 0导出当前页 null 列表分页
            long count=0;
            if (paginationModel.getDataType() == null) {
                SelectStatementProvider renderCount = whereCount.build().render(RenderingStrategies.MYBATIS3);
                count= flowFormDataMapper.count(renderCount);
                if(count == 0){
                    return Collections.EMPTY_LIST;
                }
                //树形和分组不需要分页。有脏数据传添加判断
                if(columnDataModel.getType()!=5 && columnDataModel.getType()!=3){
                    PageHelper.startPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), false);
                }
            } else if ("0".equals(paginationModel.getDataType()) && columnDataModel.getType()!=5 && columnDataModel.getType()!=3) {
                PageHelper.startPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize(), false);
            }
            //分页语句放在最后执行, 后面不允许查询数据库, 否则分页失效
            SelectStatementProvider render = where.build().render(RenderingStrategies.MYBATIS3);
            dataList = flowFormDataMapper.selectManyMappedRows(render);

            String finalPkeyId = childpkeyId;
            List<String> idStringList = dataList.stream().map(m -> m.get(finalPkeyId).toString()).distinct().collect(Collectors.toList());
            if (idStringList.size() > 0) {
                //处理子表
                List<String> tableFields = collect.stream().filter(c -> c.toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
                List<TableModel> childTableModels = tableModelList.stream().filter(t -> t.getTypeId().equals("0" )).collect(Collectors.toList());

                Map<String, List<String>> tableMap = tableFields.stream().collect(Collectors.groupingBy(t -> t.substring(0, t.lastIndexOf("-" ))));
                for (TableModel tableModel : childTableModels) {
                    String table = tableModel.getTable();
                    String tableField = tableNameAndTableField.get(table);
                    String fogID = tableModel.getTableField();
                    List<String> childFields = tableMap.get(tableField);
                    FieLdsModel fieLdsModel = allFieLds.stream().filter(item -> item.getVModel().equals(tableField)).findFirst().orElse(null);
                    List<FieLdsModel> childrenFieLdsModel = fieLdsModel != null ? fieLdsModel.getConfig().getChildren() : Collections.EMPTY_LIST;
                    if (childFields != null) {
                        OnlineDynamicSqlModel onlineDynamicSqlModel = sqlModelList.stream().filter(s -> s.getTableName().equalsIgnoreCase(table)).findFirst().orElse(null);
                        SqlTable childSqlTable = onlineDynamicSqlModel.getSqlTable();
                        List<BasicColumn> childSqlColumns = new ArrayList<>();
                        for (String c : childFields) {
                            String childT = c.substring(0, c.lastIndexOf("-" ));
                            String childF = c.substring(c.lastIndexOf("-" ) + 1);
                            FieLdsModel thisFieLdsModel = childrenFieLdsModel.stream().filter(item -> item.getVModel().equals(childF)).findFirst().orElse(null);
                            SqlColumn<Object> column = childSqlTable.column(childF);
                            if ((isDm||isOracle) && thisFieLdsModel != null) {
                                String jnpfKey = thisFieLdsModel.getConfig().getJnpfKey();
                                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                                    column = childSqlTable.column("dbms_lob.substr(" + childF + ")" ).as(childF);
                                }
                            }
                            childSqlColumns.add(column);
                        }
                        childSqlColumns.add(childSqlTable.column(fogID));
                        QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder childWhere = SqlBuilder.select(childSqlColumns).from(childSqlTable).where();
                        childWhere.and(childSqlTable.column(fogID), SqlBuilder.isIn(idStringList));
                        if (superQueryList.size() > 0) {
                            List<ConditionJsonModel> childSuperQueryList = superQueryList.stream().filter(sup -> sup.getTableName().equals(table)).collect(Collectors.toList());
                            if(childSuperQueryList.size() > 0) {
                                OnlineProductSqlUtils.getSuperSql(childWhere, childSuperQueryList, matchLogic, sqlModelList,databaseProductName);
                            }
                        }
                        if (searchVOList.size() > 0) {
                            List<VisualColumnSearchVO> childSearchList = searchVOList.stream().filter(se -> se.getTable().equals(table)).collect(Collectors.toList());
                            if(childSearchList.size() > 0) {
                                OnlineProductSqlUtils.getConditionSql(childWhere, databaseProductName, childSearchList, sqlModelList);
                            }
                        }
                        authorizeService.getConditionSql(userInfo, moduleId, table, childWhere, sqlModelList);
                        Map params = new HashMap<>();
                        params.put("onlySubTable",true);
                        filterService.handleWhereCondition(mainSqlModel.getSqlTable(), childWhere, visualDevJsonModel.getId(),subSqlTableMap, databaseProductName,params);

                        SelectStatementProvider childRender = childWhere.build().render(RenderingStrategies.MYBATIS3);
                        List<Map<String, Object>> mapList = flowFormDataMapper.selectManyMappedRows(childRender);
                        Map<String, List<Map<String, Object>>> idMap = mapList.stream().collect(Collectors.groupingBy(m -> m.get(fogID).toString()));

                        for (Map<String, Object> m : dataList) {
                            String s = m.get(childpkeyId).toString();
                            Map<String, Object> valueMap = new HashMap<>();
                            valueMap.put(tableField, idMap.get(s));
                            m.putAll(valueMap);
                        }
                    }
                }
            } else {
                return Collections.EMPTY_LIST;
            }
            dataList.stream().forEach(data -> {
                Map<String, Object> map = new CaseInsensitiveMap(data);
                data.put("id" , String.valueOf(map.get(finalPkeyId)));
                if (visualDevJsonModel.isFlowEnable()) {//有开启流程需要查询流程引擎id
                    data.put("flowId" , map.get(TableFeildsEnum.FLOWID.getField()) != null ? String.valueOf(map.get(TableFeildsEnum.FLOWID.getField())) : null);
                }
            });

            //别名key还原
            setAliasKey(dataList, aliasMap);

            PageInfo pageInfo = new PageInfo(dataList);
            paginationModel.setTotal(count);
            paginationModel.setCurrentPage(pageInfo.getPageNum());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return dataList;
    }

    private void getGroupFeild(TableFeildsEnum flowid, SqlColumn n, String databaseProductName, List<BasicColumn> groupBySqlTable, TableModel mainTable) {
        if (flowid.getField().equalsIgnoreCase(n.name())) {
            if (needUpcaseFieldsDB.contains(databaseProductName)) {
                groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(flowid.getField().toUpperCase()));
            }else{
                groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(flowid.getField()));
            }
        }
    }

    @Override
    public List<Map<String, Object>> getRelationFormList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) {
        FormDataModel formData = visualDevJsonModel.getFormData();
        List<String> collect = Arrays.asList(paginationModel.getColumnOptions().split("," ));
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);

        List<FieLdsModel> mainFieldModelList = new ArrayList<>();


        List<Map<String, Object>> noSwapDataList = new ArrayList<>();
        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        //列表中区别子表正则
        String reg = "^[jnpf_]\\S*_jnpf\\S*";
        //查询的关键字
        String keyword = paginationModel.getKeyword();
        //判断有无表
        if (visualDevJsonModel.getVisualTables().size() > 0) {
            try {
                List<TableModel> tableModelList = JsonUtil.getJsonToList(visualDevJsonModel.getVisualTables(), TableModel.class);


                OnlinePublicUtils.recursionFields(mainFieldModelList, fieLdsModels);

                //主表
                TableModel mainTable = tableModelList.stream().filter(t -> t.getTypeId().equals("1" )).findFirst().orElse(null);

                DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());

                List<OnlineDynamicSqlModel> sqlModelList = new ArrayList<>();
                //根据表字段创建sqltable
                for (TableModel model : tableModelList) {
                    OnlineDynamicSqlModel sqlModel = new OnlineDynamicSqlModel();
                    sqlModel.setSqlTable(SqlTable.of(model.getTable()));
                    sqlModel.setTableName(model.getTable());
                    if (model.getTypeId().equals("1" )) {
                        sqlModel.setMain(true);
                    } else {
                        sqlModel.setForeign(model.getTableField());
                        sqlModel.setRelationKey(model.getRelationField());
                        sqlModel.setMain(false);
                    }
                    sqlModelList.add(sqlModel);
                }

                //判断是否分页
                Boolean isPage = paginationModel.getPageSize() > 500 ? false : true;
                OnlineProductSqlUtils.getColumnListSql(sqlModelList, visualDevJsonModel, collect, linkEntity);
                DynamicDataSourceUtil.switchToDataSource(linkEntity);
                @Cleanup Connection connection = ConnUtil.getConnOrDefault(linkEntity);
                //主表
                OnlineDynamicSqlModel mainSqlModel = sqlModelList.stream().filter(OnlineDynamicSqlModel::isMain).findFirst().orElse(null);
                //非主表
                List<OnlineDynamicSqlModel> dycList = sqlModelList.stream().filter(dyc -> !dyc.isMain()).collect(Collectors.toList());
                List<BasicColumn> sqlColumns = new ArrayList<>();
                for (OnlineDynamicSqlModel dynamicSqlModel : sqlModelList) {
                    List<BasicColumn> basicColumns = Optional.ofNullable(dynamicSqlModel.getColumns()).orElse(new ArrayList<>());
                    sqlColumns.addAll(basicColumns);
                }
                QueryExpressionDSL<SelectModel> from = SqlBuilder.select(sqlColumns).from(mainSqlModel.getSqlTable());

                if (dycList.size() > 0) {
                    for (OnlineDynamicSqlModel sqlModel : dycList) {
                        from.leftJoin(sqlModel.getSqlTable()).on(sqlModel.getSqlTable().column(sqlModel.getForeign()), new EqualTo(mainSqlModel.getSqlTable().column(sqlModel.getRelationKey())));
                    }
                }
                QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where = from.where();

                //逻辑删除不展示
                if (visualDevJsonModel.getFormData().getLogicalDelete()) {
                    where.and(mainSqlModel.getSqlTable().column(TableFeildsEnum.DELETEMARK.getField()), SqlBuilder.isNull());
                }

                Integer primaryKeyPolicy = visualDevJsonModel.getFormData().getPrimaryKeyPolicy();
                if(primaryKeyPolicy==2 && !visualDevJsonModel.isFlowEnable()){
                    primaryKeyPolicy=1;
                }
                String pkeyId = flowFormDataUtil.getKey(connection, mainTable.getTable(), primaryKeyPolicy);
                visualDevJsonModel.setPkeyId(pkeyId);

                //排序
                String sort = StringUtil.isNotEmpty(paginationModel.getSort()) ? paginationModel.getSort() : "ASC";
                String sidx = StringUtil.isNotEmpty(paginationModel.getSidx()) ? paginationModel.getSidx() : pkeyId;
                String sortTableName;
                String sortField;
                if (sidx.matches(reg)) {
                    sortField = sidx.substring(sidx.lastIndexOf("jnpf_" )).replace("jnpf_" , "" );
                    sortTableName = sidx.substring(sidx.indexOf("_" ) + 1, sidx.lastIndexOf("_jnpf" ));
                } else {
                    sortTableName = mainTable.getTable();
                    sortField = sidx;
                }
                SqlColumn<Object> sortCol = SqlTable.of(sortTableName).column(sortField);
                if (sort.equalsIgnoreCase("ASC" )) {
                    where.orderBy(sortCol);
                } else {
                    where.orderBy(sortCol.descending());
                }

                if(tableModelList.size()>1) {
                    String databaseProductName = connection.getMetaData().getDatabaseProductName().trim();
                    if (needAllFieldsDB.contains(databaseProductName)) {
                        List<BasicColumn> groupBySqlTable = OnlineProductSqlUtils.getGroupBySqlTable(sqlModelList, visualDevJsonModel, collect,needUpcaseFieldsDB.contains(databaseProductName));
                        //判断列表有无FLOWTASKID FLOWID字段强拼
                        sqlColumns.stream().forEach(t -> {
                            SqlColumn n = (SqlColumn) t;
                            getGroupFeild(TableFeildsEnum.FLOWTASKID, n, databaseProductName, groupBySqlTable, mainTable);
                            getGroupFeild(TableFeildsEnum.FLOWID, n, databaseProductName, groupBySqlTable, mainTable);
                        });
                        groupBySqlTable.add(SqlTable.of(mainTable.getTable()).column(pkeyId));
                        where.groupBy(groupBySqlTable);
                    } else {
                        where.groupBy(SqlTable.of(mainTable.getTable()).column(pkeyId));
                    }
                }

                //是否导出全部数据(是否分页)
//                PageHelper.startPage((int) paginationModel.getCurrentPage(), (int) paginationModel.getPageSize());
//				where.limit(paginationModel.getPageSize()).offset((paginationModel.getCurrentPage()-1) * paginationModel.getPageSize());
                SelectStatementProvider render = where.build().render(RenderingStrategies.MYBATIS3);
                List<Map<String, Object>> dataList = flowFormDataMapper.selectManyMappedRows(render);

                noSwapDataList = dataList.stream().map(data -> {
                    data.put("id" , String.valueOf(data.get(pkeyId)));
                    return data;
                }).collect(Collectors.toList());

                //第二种 有关键字不分页
                if (StringUtil.isNotEmpty(keyword)) {
                    for (FieLdsModel fieldsModel : mainFieldModelList) {
                        if (fieldsModel.getVModel() != null) {
                            boolean b = collect.stream().anyMatch(c -> fieldsModel.getVModel().equalsIgnoreCase(c));
                            //组装为查询条件
                            if (b) {
                                VisualColumnSearchVO vo = new VisualColumnSearchVO();
                                vo.setSearchType("2" );
                                vo.setVModel(fieldsModel.getVModel());
                                vo.setValue(keyword);
                                vo.setConfig(fieldsModel.getConfig());
                                Boolean multiple = fieldsModel.getMultiple();
                                vo.setMultiple(multiple);
                                searchVOList.add(vo);
                            }
                        }
                    }
                    noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, mainFieldModelList, visualDevJsonModel.getId(), false, new ArrayList<>());

                    noSwapDataList = RelationFormUtils.getRelationListByKeyword(noSwapDataList, searchVOList);
                } else {
                    noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, mainFieldModelList, visualDevJsonModel.getId(), false, new ArrayList<>());
                }
                //假分页
                if(isPage){
                    if (CollectionUtils.isNotEmpty(noSwapDataList)) {
                        paginationModel.setTotal(noSwapDataList.size());
                        List<List<Map<String, Object>>> partition = Lists.partition(noSwapDataList, (int) paginationModel.getPageSize());
                        noSwapDataList = partition.get((int) paginationModel.getCurrentPage() - 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        }
        if (noSwapDataList.size() < 1) {
            return Collections.EMPTY_LIST;
        }
        return noSwapDataList;
    }

    /**
     *  达梦或者oracle 别名太长转换-别名还原
     * @param
     * @return
     */
    private void setAliasKey(List<Map<String, Object>> dataList, Map<String, String> aliasMap) {
        if (dataList.size() > 0 && aliasMap.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> objMap = dataList.get(i);
                Set<String> aliasKey = aliasMap.keySet();
                Map<String, Object> newObj = new HashMap<>();
                for (String key : objMap.keySet()) {
                    Object value = objMap.get(key);
                    String oldKey = aliasMap.get(key);
                    if (aliasKey.contains(key)) {
                        newObj.put(oldKey, value);
                    } else {
                        newObj.put(key, value);
                    }
                }
                dataList.remove(i);
                dataList.add(i, newObj);
            }
        }
    }
}
