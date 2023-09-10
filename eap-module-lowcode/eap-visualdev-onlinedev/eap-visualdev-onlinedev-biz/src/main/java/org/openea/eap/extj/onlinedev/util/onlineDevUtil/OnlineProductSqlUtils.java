package org.openea.eap.extj.onlinedev.util.onlineDevUtil;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Cleanup;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.ColumnListField;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.superQuery.ConditionJsonModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.OnlineColumnChildFieldModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.OnlineColumnFieldModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.model.authorize.OnlineDynamicSqlModel;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.openea.eap.extj.permission.service.UserRelationService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.form.util.TableFeildsEnum;
import org.openea.eap.extj.util.context.SpringContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成在线sql语句
 *
 */
public class OnlineProductSqlUtils {
    private static FlowFormDataUtil flowFormDataUtil = SpringContext.getBean(FlowFormDataUtil.class);
    private static UserService userService = SpringContext.getBean(UserService.class);
    private static OrganizeService organizeService = SpringContext.getBean(OrganizeService.class);
    private static UserRelationService userRelationService = SpringContext.getBean(UserRelationService.class);

    /**
     * 生成列表查询sql
     *
     * @param sqlModels
     * @param visualDevJsonModel
     * @param columnFieldList
     * @param linkEntity
     * @return
     */
    public static void getColumnListSql(List<OnlineDynamicSqlModel> sqlModels, VisualDevJsonModel visualDevJsonModel,
                                        List<String> columnFieldList, DbLinkEntity linkEntity) {
        List<OnlineColumnFieldModel> childFieldList;
        try {
            columnFieldList = columnFieldList.stream().distinct().collect(Collectors.toList());
            ColumnDataModel columnData = visualDevJsonModel.getColumnData();
            List<ColumnListField> modelList = JsonUtil.getJsonToList(columnData.getColumnList(), ColumnListField.class);

            @Cleanup Connection conn = ConnUtil.getConnOrDefault(linkEntity);
            List<TableModel> tableModelList = visualDevJsonModel.getVisualTables();
            String databaseProductName = conn.getMetaData().getDatabaseProductName().trim();
            boolean isClobDbType = databaseProductName.equalsIgnoreCase("oracle") || databaseProductName.equalsIgnoreCase("DM DBMS");
            //主表
            TableModel mainTable = tableModelList.stream().filter(model -> model.getTypeId().equals("1")).findFirst().orElse(null);
            //获取主键
            Integer primaryKeyPolicy = visualDevJsonModel.getFormData().getPrimaryKeyPolicy();
            String pKeyName = flowFormDataUtil.getKey(conn, mainTable.getTable(), primaryKeyPolicy);
            //列表中区别子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";

            //列表主表字段
            List<String> mainTableFields = columnFieldList.stream().filter(s -> !s.matches(reg)
                    && !s.toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());
            mainTableFields.add(pKeyName);
            if (!visualDevJsonModel.isFlowEnable() && primaryKeyPolicy == 2) {
                primaryKeyPolicy = 1;
                mainTableFields.add(flowFormDataUtil.getKey(conn, mainTable.getTable(), primaryKeyPolicy));
            }

            if (visualDevJsonModel.getFormData().getConcurrencyLock()) {
                mainTableFields.add(TableFeildsEnum.VERSION.getField());
            }
            //有开启流程得需要查询流程引擎信息
            if (visualDevJsonModel.isFlowEnable()) {
                String s = TableFeildsEnum.FLOWID.getField();
                if (isClobDbType) {
                    s = TableFeildsEnum.FLOWID.getField().toUpperCase();
                }
                mainTableFields.add(s);
            }


            if (columnData != null && ObjectUtil.isNotEmpty(columnData.getType()) && columnData.getType() == 3) {
                String groupField = visualDevJsonModel.getColumnData().getGroupField();
                boolean contains = columnFieldList.contains(groupField);
                if (!contains) {
                    if (groupField.startsWith("jnpf_")) {
                        columnFieldList.add(groupField);
                    } else {
                        mainTableFields.add(groupField);
                    }
                }
            }

            //列表子表字段
            childFieldList = columnFieldList.stream().filter(s -> s.matches(reg)).map(child -> {
                OnlineColumnFieldModel fieldModel = new OnlineColumnFieldModel();
                String s1 = child.substring(child.lastIndexOf("jnpf_")).replace("jnpf_", "");
                String s2 = child.substring(child.indexOf("_") + 1, child.lastIndexOf("_jnpf"));
                fieldModel.setTableName(s2);
                fieldModel.setField(s1);
                fieldModel.setOriginallyField(child);
                return fieldModel;
            }).collect(Collectors.toList());

            //取列表用到的表
            List<String> ColumnTableNameList = childFieldList.stream().map(t -> t.getTableName().toLowerCase()).collect(Collectors.toList());
            List<TableModel> tableModelList1 = tableModelList.stream().filter(t -> ColumnTableNameList.contains(t.getTable().toLowerCase())).collect(Collectors.toList());
            List<OnlineColumnChildFieldModel> classifyFieldList = new ArrayList<>(10);
            for (TableModel t : tableModelList1) {
                OnlineColumnChildFieldModel childFieldModel = new OnlineColumnChildFieldModel();
                childFieldModel.setTable(t.getTable());
                childFieldModel.setRelationField(t.getRelationField());
                childFieldModel.setTableField(t.getTableField());
                classifyFieldList.add(childFieldModel);
            }

            for (OnlineDynamicSqlModel dycModel : sqlModels) {
                if (dycModel.isMain()) {
                    List<BasicColumn> mainSqlColumns = getBasicColumns(mainTableFields, sqlModels, dycModel, modelList, isClobDbType);
                    dycModel.setColumns(mainSqlColumns);
                } else {
                    if (classifyFieldList.size() > 0) {
                        Map<String, List<OnlineColumnFieldModel>> mastTableCols = childFieldList.stream().collect(Collectors.groupingBy(OnlineColumnFieldModel::getTableName));
                        List<OnlineColumnFieldModel> onlineColumnFieldModels = Optional.ofNullable(mastTableCols.get(dycModel.getTableName())).orElse(new ArrayList<>());
                        List<BasicColumn> mastSqlCols = getBasicColumnsChild(modelList, dycModel, onlineColumnFieldModels, isClobDbType);
                        dycModel.setColumns(mastSqlCols);
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    public static List<BasicColumn> getGroupBySqlTable(List<OnlineDynamicSqlModel> sqlModels, VisualDevJsonModel visualDevJsonModel,
                                                       List<String> columnFieldList, boolean isClobDbType) {
        List<OnlineColumnFieldModel> childFieldList;
        List<BasicColumn> basicColumns = new ArrayList<>();
        try {
            columnFieldList = columnFieldList.stream().distinct().collect(Collectors.toList());

            List<TableModel> tableModelList = visualDevJsonModel.getVisualTables();

            //列表中区别子表正则
            String reg = "^[jnpf_]\\S*_jnpf\\S*";

            //列表主表字段
            List<String> mainTableFields = columnFieldList.stream().filter(s -> !s.matches(reg)
                    && !s.toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX)).collect(Collectors.toList());

            if (visualDevJsonModel.getFormData().getConcurrencyLock()) {
                mainTableFields.add(TableFeildsEnum.VERSION.getField());
            }

            //有开启流程得需要查询流程引擎信息
            if (visualDevJsonModel.isFlowEnable()) {
                String s = TableFeildsEnum.FLOWID.getField();
                if (isClobDbType) {
                    s = TableFeildsEnum.FLOWID.getField().toUpperCase();
                }
                mainTableFields.add(s);
            }

            //列表子表字段
            childFieldList = columnFieldList.stream().filter(s -> s.matches(reg)).map(child -> {
                OnlineColumnFieldModel fieldModel = new OnlineColumnFieldModel();
                String s1 = child.substring(child.lastIndexOf("jnpf_")).replace("jnpf_", "");
                String s2 = child.substring(child.indexOf("_") + 1, child.lastIndexOf("_jnpf"));
                fieldModel.setTableName(s2);
                fieldModel.setField(s1);
                fieldModel.setOriginallyField(child);
                return fieldModel;
            }).collect(Collectors.toList());

            //取列表用到的表
            List<String> ColumnTableNameList = childFieldList.stream().map(t -> t.getTableName().toLowerCase()).collect(Collectors.toList());
            List<TableModel> tableModelList1 = tableModelList.stream().filter(t -> ColumnTableNameList.contains(t.getTable().toLowerCase())).collect(Collectors.toList());
            List<OnlineColumnChildFieldModel> classifyFieldList = new ArrayList<>(10);
            for (TableModel t : tableModelList1) {
                OnlineColumnChildFieldModel childFieldModel = new OnlineColumnChildFieldModel();
                childFieldModel.setTable(t.getTable());
                childFieldModel.setRelationField(t.getRelationField());
                childFieldModel.setTableField(t.getTableField());
                classifyFieldList.add(childFieldModel);
            }

            List<ColumnListField> modelList = JsonUtil.getJsonToList(visualDevJsonModel.getColumnData().getColumnList(), ColumnListField.class);
            for (OnlineDynamicSqlModel dycModel : sqlModels) {
                if (dycModel.isMain()) {
                    List<BasicColumn> mainSqlColumns = getBasicColumns(mainTableFields, sqlModels, dycModel, modelList, isClobDbType);
                    basicColumns.addAll(mainSqlColumns);
                } else {
                    if (classifyFieldList.size() > 0) {
                        Map<String, List<OnlineColumnFieldModel>> mastTableCols = childFieldList.stream().collect(Collectors.groupingBy(OnlineColumnFieldModel::getTableName));
                        List<OnlineColumnFieldModel> onlineColumnFieldModels = Optional.ofNullable(mastTableCols.get(dycModel.getTableName())).orElse(new ArrayList<>());
                        List<BasicColumn> mastSqlCols = getBasicColumnsChild(modelList, dycModel, onlineColumnFieldModels, isClobDbType);
                        basicColumns.addAll(mastSqlCols);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return basicColumns;
    }

    /**
     *
     */
    public static void getConditionSql(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder form, String databaseProductName, List<VisualColumnSearchVO> searchVOList,
                                       List<OnlineDynamicSqlModel> sqlModelList) throws SQLException {
        //成对注释1---进来前有切库。切回主库查询用户相关信息
        DynamicDataSourceUtil.switchToDataSource(null);
        try {
            boolean isOracle = databaseProductName.equalsIgnoreCase("oracle");
            boolean isPostgre = databaseProductName.equalsIgnoreCase("PostgreSQL");
            boolean isSqlServer = databaseProductName.equalsIgnoreCase("Microsoft SQL Server");
            for (int k = 0; k < searchVOList.size(); k++) {
                VisualColumnSearchVO vo = searchVOList.get(k);
                String jnpfKey = "jnpfkey";
                if (Objects.nonNull(vo.getConfig())) {
                    jnpfKey = vo.getConfig().getJnpfKey();
                }
                String tableName = vo.getTable();
                OnlineDynamicSqlModel sqlModel = sqlModelList.stream().filter(sql -> sql.getTableName().equals(tableName)).findFirst().orElse(null);
                SqlTable sqlTable = sqlModel.getSqlTable();
                String searchType = vo.getSearchType();
                String vModel = vo.getField();
                String format;

                boolean isSearchMultiple = vo.getSearchMultiple() != null && vo.getSearchMultiple();
                //搜索或字段其中一个为多选, 都按照多选处理
                boolean isMultiple = isSearchMultiple || vo.getMultiple() != null && vo.getMultiple();

                List<String> dataValues = new ArrayList<>();
                SqlColumn<Object> sqlColumn = sqlTable.column(vModel);
                List<AndOrCriteriaGroup> groupList = new ArrayList<>();
                if (isMultiple) {
                    Object tmpValue = vo.getValue();
                    boolean isComSelect = JnpfKeyConsts.COMSELECT.equals(jnpfKey);
                    if (isComSelect) {
                        //左侧树情况左侧条件为单选查询, 所以组织查询全部转为字符串重新处理
                        //组织单独处理, 多选是二维数组, 单选是数组
                        tmpValue = JSONArray.toJSONString(vo.getValue());
                    }
                    if (tmpValue instanceof String) {
                        //多选的情况, 若为字符串则处理成多选的字符串数组
                        tmpValue = OnlineSwapDataUtils.convertValueToString((String) tmpValue, true, isComSelect);
                        dataValues = JSON.parseArray((String) tmpValue, String.class);
                    } else {
                        dataValues = JsonUtil.getJsonToList(tmpValue, String.class);
                    }
                } else {
                    boolean isCurOrg = JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey);
                    if (isCurOrg) {
                        //所属组织只保存了当前组织的ID, 没有存储组织数组, 将过滤条件中的组织数组取最后一个ID
                        vo.setValue(OnlineSwapDataUtils.getLastOrganizeId(vo.getValue()));
                    }
                    dataValues.add(vo.getValue().toString());
                }
                String value = vo.getValue().toString();
                //只要是查询多选全部做模糊查询==>表单控件多选和单选数据不同，所以只能模糊查询
                if (isMultiple || JnpfKeyConsts.CHECKBOX.equals(jnpfKey) || JnpfKeyConsts.CASCADER.equals(jnpfKey)
                        || JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey) || JnpfKeyConsts.ADDRESS.equals(jnpfKey) || JnpfKeyConsts.CUSTOMUSERSELECT.equals(jnpfKey)
                ) {
                    searchType = "2";
                }
                if (JnpfKeyConsts.COM_INPUT.equals(jnpfKey) || JnpfKeyConsts.TEXTAREA.equals(jnpfKey)) {
                    if ("3".equals(searchType)) {
                        searchType = "2";
                    }
                }
                if ("1".equals(searchType)) {
                    form.and(sqlColumn, SqlBuilder.isEqualTo(value));
                } else if ("2".equals(searchType)) {
                    if (JnpfKeyConsts.CUSTOMUSERSELECT.equals(jnpfKey)) {
                        // 分组 组织 岗位 角色 用户
                        for (String userVal : dataValues) {
                            convertUserSelectData(sqlColumn, groupList, userVal);
                        }
                    }
                    if (isMultiple) {
                        for (String val : dataValues) {
                            if(isSqlServer){
                                val = val.replaceAll("\\[", "[[]");
                            }
                            groupList.add(SqlBuilder.or(sqlColumn, SqlBuilder.isLike("%" + val + "%")));
                        }
                    }
                    if(isSqlServer){
                        value = value.replaceAll("\\[", "[[]");
                    }
                    value = "%" + value + "%";
                    if (groupList.size() > 0) {
                        form.and(sqlColumn, SqlBuilder.isLike(value), groupList);
                    } else {
                        form.and(sqlColumn, SqlBuilder.isLike(value));
                    }
                } else if ("3".equals(searchType)) {
                    switch (jnpfKey) {
                        case JnpfKeyConsts.MODIFYTIME:
                        case JnpfKeyConsts.CREATETIME:
                        case JnpfKeyConsts.DATE:
                            JSONArray timeStampArray = (JSONArray) vo.getValue();
                            Long o1 = (Long) timeStampArray.get(0);
                            Long o2 = (Long) timeStampArray.get(1);
                            format = StringUtil.isEmpty(vo.getFormat()) ? "yyyy-MM-dd HH:mm:ss" : vo.getFormat();
                            //时间戳转string格式
                            String startTime = OnlinePublicUtils.getDateByFormat(o1, format);
                            String endTime = OnlinePublicUtils.getDateByFormat(o2, format);
                            //处理创建和修改时间查询条件范围
                            if (JnpfKeyConsts.CREATETIME.equals(jnpfKey) || JnpfKeyConsts.MODIFYTIME.equals(jnpfKey)) {
                                endTime = endTime.substring(0, 10);
                            }
                            String firstTimeDate = OnlineDatabaseUtils.getTimeFormat(startTime);
                            String lastTimeDate = OnlineDatabaseUtils.getLastTimeFormat(endTime);
                            if (isOracle || isPostgre) {
                                form.and(sqlTable.column(vModel), SqlBuilder.isBetween(new Date(o1)).and(new Date(o2)));
                            } else {
                                form.and(sqlTable.column(vModel), SqlBuilder.isBetween(firstTimeDate).and(lastTimeDate));
                            }
                            break;
                        case JnpfKeyConsts.TIME:
                            List<String> stringList = JsonUtil.getJsonToList(value, String.class);
                            form.and(sqlTable.column(vModel), SqlBuilder.isBetween(stringList.get(0)).and(stringList.get(1)));
                            break;
                        case JnpfKeyConsts.NUM_INPUT:
                        case JnpfKeyConsts.CALCULATE:
                            BigDecimal firstValue = null;
                            BigDecimal secondValue = null;
                            JSONArray objects = (JSONArray) vo.getValue();
                            for (int i = 0; i < objects.size(); i++) {
                                Object n = objects.get(i);
                                if (ObjectUtil.isNotEmpty(n)) {
                                    if (i == 0) {
                                        firstValue = new BigDecimal(String.valueOf(n));
                                    } else {
                                        secondValue =  new BigDecimal(String.valueOf(n));
                                    }
                                }
                            }
                            if (firstValue != null) {
                                form.and(sqlTable.column(vModel), SqlBuilder.isGreaterThanOrEqualTo(firstValue));
                            }
                            if (secondValue != null) {
                                form.and(sqlTable.column(vModel), SqlBuilder.isLessThanOrEqualTo(secondValue));
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } finally {
            //成对注释1---完成后。切回数据库
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    /**
     * 高级查询哦
     *
     * @param where
     * @param superQueryList
     * @param matchLogic
     * @return
     */
    public static void getSuperSql(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, List<ConditionJsonModel> superQueryList, String matchLogic,
                                   List<OnlineDynamicSqlModel> sqlModelList, String databaseProductName) {
        boolean isOracle = databaseProductName.equalsIgnoreCase("oracle");
        boolean isPostgre = databaseProductName.equalsIgnoreCase("PostgreSQL");
        boolean isSqlServer = databaseProductName.equalsIgnoreCase("Microsoft SQL Server");
        String reg = "^[jnpf_]\\S*_jnpf\\S*";
        superQueryList.stream().filter(condition -> condition.getField().matches(reg)).forEach(su -> {
                    su.setTableName(su.getField().substring(su.getField().indexOf("_") + 1, su.getField().lastIndexOf("_jnpf")));
                    su.setField(su.getField().substring(su.getField().lastIndexOf("jnpf_")).replace("jnpf_", ""));
                }
        );
        BindableColumn<Object> firstColumn = null;
        VisitableCondition firstCondition = null;
        List<AndOrCriteriaGroup> groupList = new ArrayList<>();
        for (int i = 0; i < superQueryList.size(); i++) {
            ConditionJsonModel conditionJsonModel = superQueryList.get(i);
            String conditionValue = conditionJsonModel.getFieldValue();
            Object fieldValue = conditionValue;
            String symbol = getSymbol(conditionJsonModel.getSymbol());
            boolean isEmpty = StringUtil.isEmpty(conditionValue) || "[]".equals(conditionValue);
            String tableName = conditionJsonModel.getTableName();
            OnlineDynamicSqlModel onlineDynamicSqlModel = sqlModelList.stream().filter(sql -> sql.getTableName().equals(tableName)).findFirst().orElse(null);
            SqlTable sqlTable = onlineDynamicSqlModel.getSqlTable();
            String jnpfKey = conditionJsonModel.getJnpfKey();
            BindableColumn<Object> sqlColumn = sqlTable.column(conditionJsonModel.getField());

            if (jnpfKey.equals(JnpfKeyConsts.DATE) || jnpfKey.equals(JnpfKeyConsts.CREATETIME) || jnpfKey.equals(JnpfKeyConsts.MODIFYTIME)) {
                String format = "yyyy-MM-dd HH:mm:ss";
                if (fieldValue != null) {
                    Long o1 = Long.valueOf(fieldValue.toString());
                    String startTime = OnlinePublicUtils.getDateByFormat(o1, format);
                    fieldValue = startTime;
                    if (isOracle || isPostgre) {
                        fieldValue = Timestamp.valueOf(startTime);
                    }
                }
            } else if (jnpfKey.equals(JnpfKeyConsts.CURRORGANIZE)) {
                if (fieldValue != null) {
                    List<String> jsonToList = JsonUtil.getJsonToList(fieldValue, String.class);
                    fieldValue = jsonToList.get(jsonToList.size() - 1);
                }
            }
            if (isEmpty) {
                if (symbol.equals("not like")) {
                    symbol = "<>";
                } else if (symbol.equals("like")) {
                    symbol = "=";
                }
            }
            boolean isNull = false;
            boolean isAppend = false;
            if (isEmpty) {
                if (symbol.equals("not like") || symbol.equals("<>")) {
                    isAppend = true;
                    isNull = false;
                } else if (symbol.equals("like") || symbol.equals("=")) {
                    isAppend = true;
                    isNull = true;
                }
            } else {
                if (symbol.equals("not like") || symbol.equals("<>")) {
                    isAppend = true;
                    isNull = true;
                }
            }
            if(isSqlServer && symbol.equals("like") && fieldValue instanceof String){
                fieldValue = fieldValue.toString().replaceAll("\\[", "[[]");
            }

            VisitableCondition condition = getVisitableCondition(symbol, fieldValue);
            //处理用户选择控件
            if (JnpfKeyConsts.CUSTOMUSERSELECT.equals(jnpfKey)) {
                List<AndOrCriteriaGroup> userGroup = new ArrayList<>();
                convertUserSelectData(sqlColumn, userGroup, fieldValue.toString());
                AndOrCriteriaGroup[] userGroups = userGroup.toArray(new AndOrCriteriaGroup[userGroup.size()]);
                if (firstColumn == null) {
                    //如果是第一个条件在最前方添加1=1, 用户选择的所有条件作为一个条件组再添加
                    firstColumn = DerivedColumn.of("1");
                    //如果是OR方案 添加1=2忽略这个条件, 如果是AND方案 添加1=1使后续条件成立
                    firstCondition = SqlBuilder.isEqualTo(matchLogic.equalsIgnoreCase("and") ? 1 : 2);
                    ;
                }
                AndOrCriteriaGroup andOr;
                if (matchLogic.equalsIgnoreCase("and")) {
                    andOr = SqlBuilder.and(sqlColumn, condition, userGroups);
                } else {
                    andOr = SqlBuilder.or(sqlColumn, condition, userGroups);
                }
                groupList.add(andOr);
            } else {
                if (firstColumn == null) {
                    firstColumn = sqlColumn;
                    firstCondition = condition;
                } else {
                    AndOrCriteriaGroup andOr;
                    if (matchLogic.equalsIgnoreCase("and")) {
                        andOr = SqlBuilder.and(sqlColumn, condition);
                    } else {
                        andOr = SqlBuilder.or(sqlColumn, condition);
                    }
                    groupList.add(andOr);
                }
            }

            if (isAppend) {
                VisitableCondition appendCondition;
                AndOrCriteriaGroup group;
                if (isNull) {
                    appendCondition = SqlBuilder.isNull();
                } else {
                    appendCondition = SqlBuilder.isNotNull();
                }
                if (isEmpty && symbol.equals("<>")) {
                    group = SqlBuilder.and(sqlColumn, appendCondition);
                } else {
                    group = SqlBuilder.or(sqlColumn, appendCondition);
                }
                groupList.add(group);
            }
        }
        if (firstColumn != null) {
            if (groupList.size() > 0) {
                where.and(firstColumn, firstCondition, groupList);
            } else {
                where.and(firstColumn, firstCondition);
            }
        }
    }

    private static VisitableCondition getVisitableCondition(String symbol, Object fieldValue) {
        VisitableCondition visitableCondition = null;
        switch (symbol) {
            case "<>":
                visitableCondition = SqlBuilder.isNotEqualTo(fieldValue);
                break;
            case "=":
                visitableCondition = SqlBuilder.isEqualTo(fieldValue);
                break;
            case "not like":
                fieldValue = "%" + fieldValue + "%";
                visitableCondition = SqlBuilder.isNotLike(fieldValue);
                break;
            case "like":
                fieldValue = "%" + fieldValue + "%";
                visitableCondition = SqlBuilder.isLike(fieldValue);
                break;
            case ">":
                visitableCondition = SqlBuilder.isGreaterThan(fieldValue);
                break;
            case ">=":
                visitableCondition = SqlBuilder.isGreaterThanOrEqualTo(fieldValue);
                break;
            case "<":
                visitableCondition = SqlBuilder.isLessThan(fieldValue);
                break;
            case "<=":
                visitableCondition = SqlBuilder.isLessThanOrEqualTo(fieldValue);
                break;
            default:
                break;
        }
        return visitableCondition;
    }

    private static String getSymbol(String symbol) {
        if ("==".equals(symbol)) {
            return "=";
        } else if ("notLike".equals(symbol)) {
            return "not like";
        } else {
            return symbol;
        }
    }


    /**
     * 将用户选择控件的数据转换为Dynamic查询条件
     *
     * @param sqlColumn
     * @param userGroup
     * @param userVal
     */
    private static void convertUserSelectData(BindableColumn sqlColumn, List<AndOrCriteriaGroup> userGroup, String userVal) {
        // 分组 组织 岗位 角色 用户
        String userValue = userVal.substring(0, userVal.indexOf("--"));
        UserEntity userEntity = userService.getInfo(userValue);
        if (userEntity != null) {
            String idValue;
            //在用户关系表中取出
            // todo eap
//            List<UserRelationEntity> groupRel = Optional.ofNullable(userRelationService.getListByObjectType(userValue, PermissionConst.GROUP)).orElse(new ArrayList<>());
//            List<UserRelationEntity> orgRel = Optional.ofNullable(userRelationService.getListByObjectType(userValue, PermissionConst.ORGANIZE)).orElse(new ArrayList<>());
//            List<UserRelationEntity> posRel = Optional.ofNullable(userRelationService.getListByObjectType(userValue, PermissionConst.POSITION)).orElse(new ArrayList<>());
//            List<UserRelationEntity> roleRel = Optional.ofNullable(userRelationService.getListByObjectType(userValue, PermissionConst.ROLE)).orElse(new ArrayList<>());
//            if (groupRel.size() > 0) {
//                for (UserRelationEntity split : groupRel) {
//                    idValue = "%" + split.getObjectId() + "%";
//                    userGroup.add(SqlBuilder.or(sqlColumn, SqlBuilder.isLike(idValue)));
//                }
//            }
//            if (StringUtil.isNotEmpty(userEntity.getOrganizeId())) {
//                //向上递归 查出所有上级组织
//                List<String> allUpOrgIDs = new ArrayList<>();
//                organizeService.upWardRecursion(allUpOrgIDs, userEntity.getOrganizeId());
//                for (String orgID : allUpOrgIDs) {
//                    idValue = "%" + orgID + "%";
//                    userGroup.add(SqlBuilder.or(sqlColumn, SqlBuilder.isLike(idValue)));
//                }
//            }
//            if (posRel.size() > 0) {
//                for (UserRelationEntity split : posRel) {
//                    idValue = "%" + split.getObjectId() + "%";
//                    userGroup.add(SqlBuilder.or(sqlColumn, SqlBuilder.isLike(idValue)));
//                }
//            }
//            if (roleRel.size() > 0) {
//                for (UserRelationEntity split : roleRel) {
//                    idValue = "%" + split.getObjectId() + "%";
//                    userGroup.add(SqlBuilder.or(sqlColumn, SqlBuilder.isLike(idValue)));
//                }
//            }
        }
    }

    public static List<BasicColumn> getBasicColumns(List<String> mainTableFields, List<OnlineDynamicSqlModel> sqlModels, OnlineDynamicSqlModel dycModel,
                                                    List<ColumnListField> columnFieldListAll, boolean isClobDbType) {
        List<BasicColumn> mainSqlColumns = mainTableFields.stream().map(m -> {
            ColumnListField columnListField = columnFieldListAll.stream().filter(item -> item.getProp().equals(m)).findFirst().orElse(null);
            if (isClobDbType && columnListField != null) {
                String jnpfKey = columnListField.getJnpfKey();
                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                    if (sqlModels.size() > 1) {//连表会出现《表名.字段》clob处理需要包含表名
                        return SqlTable.of("dbms_lob.substr(" + dycModel.getTableName()).column(m + ")").as(m);
                    } else {
                        return SqlTable.of(dycModel.getTableName()).column("dbms_lob.substr(" + m + ")").as(m);
                    }
                }
            }
            return dycModel.getSqlTable().column(m);
        }).collect(Collectors.toList());
        return mainSqlColumns;
    }

    private static List<BasicColumn> getBasicColumnsChild(List<ColumnListField> modelList, OnlineDynamicSqlModel dycModel,
                                                          List<OnlineColumnFieldModel> onlineColumnFieldModels, boolean isClobDbType) {
        SqlTable mastSqlTable = dycModel.getSqlTable();
        List<BasicColumn> mastSqlCols = onlineColumnFieldModels.stream().map(m -> {
            ColumnListField columnListField = modelList.stream().filter(item -> item.getProp().equals(m.getOriginallyField())).findFirst().orElse(null);
            if (isClobDbType && columnListField != null) {
                String jnpfKey = columnListField.getConfig().getJnpfKey();
                if (jnpfKey.equals(JnpfKeyConsts.UPLOADFZ) || jnpfKey.equals(JnpfKeyConsts.UPLOADIMG) || jnpfKey.equals(JnpfKeyConsts.EDITOR)) {
                    return SqlTable.of("dbms_lob.substr(" + dycModel.getTableName()).column(m.getField() + ")").as(m.getOriginallyField());
                }
            }
            return mastSqlTable.column(m.getField()).as(m.getOriginallyField());
        }).collect(Collectors.toList());
        return mastSqlCols;
    }


}
