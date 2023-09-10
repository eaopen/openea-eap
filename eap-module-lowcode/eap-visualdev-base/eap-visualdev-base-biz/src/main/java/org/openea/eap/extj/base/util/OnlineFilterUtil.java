package org.openea.eap.extj.base.util;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.form.util.FormPublicUtils;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Data
public class OnlineFilterUtil {
    /**
     * 表字段名和对应的表对象映射
     */
    Map<String, SqlTable> subSqlTableMap;
    /**
     * 额外参数
     */
    Map<String,Object> params;
    /**
     * 字段说明
     */
    private String fieldName;
    /**
     * 运算符
     */
    private String operator;
    /**
     * 逻辑拼接符号
     */
    private String logic;
    /**
     * 组件标识
     */
    private String jnpfKey;
    /**
     * 字段key
     */
    private String field;
    /**
     * 自定义的值
     */
    private String fieldValue;
    /**
     * 自定义的值2
     */
    private String fieldValue2;

    private List<String> selectIgnore;

    /**
     * 显示类型
     */
    private String showLevel;


    /**
     * 数据库类型
     */
    private String dbType;
    /**
     * 日期格式
     */
    private String format;

    /**
     * 数字精度
     */
    private String precision;

    /**
     * @param where    where对象
     * @param sqlTable sql表对象,默认传主表
     * @return
     */
    public QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder solveValue(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable) {

        if(!this.preHandle()) return where;


        MyType myType = myControl(jnpfKey);
        if (fieldValue == null) {
            fieldValue = "";
        }
        try {
            ArrayList splitKey = new ArrayList<String>() {{
                add("date");
                add("time");
                add("numInput");
                add("createTime");
                add("modifyTime");
            }};

            if (splitKey.contains(jnpfKey) && "between".equals(operator)) {
                List<String> data = JsonUtil.getJsonToList(fieldValue, String.class);
                fieldValue = data.get(0);
                fieldValue2 = data.get(1);
            }
            // 显示组织还是部门,全部拿最后一级
            if (jnpfKey.equals("currOrganize") && StringUtils.isNoneBlank(fieldValue)) {
                List<String> data = JsonUtil.getJsonToList(fieldValue, String.class);
                fieldValue = data.get(data.size() - 1);
            }
            // 不用把外面的 [ 去掉
            selectIgnore = new ArrayList<String>() {{
                add("comSelect");
                add("address");
                add("cascader");
                add("checkbox");
                add("depSelect");
            }};



            String fieldKey = "";
            // 替换子表的sqlTable
            if (field.indexOf("-") > 0) {
                fieldKey = field.split("-")[0];
                sqlTable = this.subSqlTableMap.get(fieldKey);
                field = field.split("-")[1];
            }
            // 替换副表的字段
            if (field.indexOf("_jnpf_") > 0) {

                sqlTable = this.subSqlTableMap.get(field);
                field = field.split("_jnpf_")[1];
            }


            myType.judge(where, sqlTable, field);
            return where;
        } catch (Exception e) {
            return where;
        }
    }

    /**
     * 前置异常或边界情况处理
     */
    private boolean preHandle() {
        if(params!=null){
            // 判断是否只需处理子副表,忽略主表
            Boolean onlySubTable = (Boolean) params.get("onlySubTable");
            // 如果是主表
            if(onlySubTable && !field.contains("_jnpf_") && !field.contains("-")){
                return false;
            }
            // 不拼接副表
            if(onlySubTable && field.contains("_jnpf_")){
                return false;
            }
        }

        return true;
    }

    /**
     * 判断控件的所属类型
     *
     * @param jnpfKey 控件标识
     * @return 控件类型
     */
    public MyType myControl(String jnpfKey) {
        MyType myType;
        switch (jnpfKey) {
            case "comInput":
            case "textarea":
            case "billRule":
            case "popupTableSelect":
            case "relationForm":
            case "relationFormAttr":
            case "popupSelect":
            case "popupAttr":
                myType = new BasicControl();
                break;
            case "calculate":
            case "numInput":
                myType = new NumControl();
                break;
            case "date":
            case "createTime":
            case "modifyTime":
                myType = new DateControl();
                break;
            case "time":
                myType = new TimeControl();
                break;
            default:
                myType = new SelectControl();
        }
        return myType;
    }


    /**
     * 基础类型
     */
    class BasicControl extends MyType {

        @Override
        void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field) {
            if ("&&".equals(logic)) {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("")));
                        where.and(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("")));
                        where.and(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.and(sqlTable.column(field), SqlBuilder.isEqualTo(fieldValue));
                        break;
                    case "<>":
                        where.and(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));
                        break;
                    case "like":
                        convertSqlServerLike();
                        where.and(sqlTable.column(field), SqlBuilder.isLike("%" + fieldValue + "%"));
                        break;
                    case "notLike":
                        convertSqlServerLike();
                        where.and(sqlTable.column(field), SqlBuilder.isNotLike("%" + fieldValue + "%"));
                        break;
                }

            } else {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("")));
                        where.or(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("")));
                        where.or(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.or(sqlTable.column(field), SqlBuilder.isEqualTo(fieldValue));
                        break;
                    case "<>":

                        where.or(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));

                        break;
                    case "like":
                        convertSqlServerLike();
                        where.or(sqlTable.column(field), SqlBuilder.isLike("%" + fieldValue + "%"));
                        break;
                    case "notLike":
                        convertSqlServerLike();
                        where.or(sqlTable.column(field), SqlBuilder.isNotLike("%" + fieldValue + "%"));
                        break;
                }


            }
        }
    }

    class NumControl extends MyType {
        @Override
        void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field) {

            BigDecimal num1 = new BigDecimal(fieldValue);
            BigDecimal num2 = null;
            if(fieldValue2!=null){
                num2  = new BigDecimal(fieldValue2);
            }
            // 精度处理
            String fieldPrecisionValue;
            String fieldPrecisionValue2;
            if(StringUtils.isNotBlank(precision)){
                String zeroNum = "0."+ StringUtils.repeat("0", Integer.parseInt(precision));
                DecimalFormat numFormat = new DecimalFormat(zeroNum);
                fieldPrecisionValue = numFormat.format(new BigDecimal(fieldValue));
                num1 = new BigDecimal(fieldPrecisionValue);
                if(fieldValue2 != null ){
                    fieldPrecisionValue2 = numFormat.format(new BigDecimal(fieldValue2));
                    num2 = new BigDecimal(fieldPrecisionValue2);
                }
            }

            if ("&&".equals(logic)) {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.and(sqlTable.column(field), SqlBuilder.isEqualTo(num1));
                        break;
                    case "<>":

                        where.and(sqlTable.column(field), SqlBuilder.isNotEqualTo(num1));

                        break;
                    case ">":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThan(num1));

                        break;
                    case "<":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThan(num1));
                        break;
                    case ">=":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(num1));
                        break;
                    case "<=":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(num1));
                        break;
                    case "between":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(num1));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(num2));
                        break;
                }
            } else {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.or(sqlTable.column(field), SqlBuilder.isEqualTo(num1));
                        break;
                    case "<>":
                        where.or(sqlTable.column(field), SqlBuilder.isNotEqualTo(num1));

                        break;
                    case ">":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThan(num1));
                        break;
                    case "<":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThan(num1));
                        break;
                    case ">=":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(num1));
                        break;
                    case "<=":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(num1));
                        break;
                    case "between":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(num1));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(num2));
                        break;
                }
            }
        }


    }

    class DateControl extends MyType {
        @Override
        void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field) {
            long time = 0;
            Date date = new Date();
            String startDateTime="";
            String endDateTime="";
            if (StringUtils.isNoneBlank(fieldValue)) {
                time = Long.parseLong(fieldValue);
                date.setTime(time);
                startDateTime = DateUtil.dateFormat(date);
                if(StringUtils.isNoneBlank(format)){
                    //开始时间获取
                    startDateTime= FormPublicUtils.getTimeFormat(DateUtil.dateToString(date,format));
                }else{
                    format="yyyy-MM-dd HH:mm:ss";
                }
                //结束时间获取
                if(StringUtil.isNotEmpty(fieldValue2)){
                    endDateTime= FormPublicUtils.getLastTimeFormat(DateUtil.dateToString(new Date(Long.parseLong(fieldValue2)),format));
                }else{
                    endDateTime= FormPublicUtils.getLastTimeFormat(DateUtil.dateToString(date,format));
                }
            }

            boolean dataTimestamp = startDateTime.length() > 10;//带日期+时间格式
            String dateFunc = dataTimestamp ? "to_timestamp" : "to_date";
            if(DbType.ORACLE.getDb().equalsIgnoreCase(dbType) ){
                startDateTime = dateFunc + "('" + startDateTime + "','yyyy-mm-dd HH24:mi:ss')";
                endDateTime = dateFunc + "('" + endDateTime + "','yyyy-mm-dd HH24:mi:ss')";
            }


            if ("&&".equals(logic)) {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.and(sqlTable.column(field), SqlBuilder.isEqualTo(startDateTime));
//                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
//                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(DateUtil.daFormat(time + 60 * 60 * 24 * 1000)));
                        break;
                    case "<>":
                        where.and(sqlTable.column(field), SqlBuilder.isNotEqualTo(startDateTime));
                        break;
                    case ">":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThan(startDateTime));
                        break;
                    case "<":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThan(startDateTime));
                        break;
                    case ">=":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(startDateTime));
                        break;
                    case "<=":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(startDateTime));
                        break;
                    case "between":
//                        long time2 = Long.parseLong(fieldValue2);
//                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
//                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(DateUtil.daFormat(time2)));
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(startDateTime));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(endDateTime));
                        break;
                }


            } else {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(DateUtil.daFormat(time + 60 * 60 * 24 * 1000)));
                        break;
                    case "<>":
                        where.or(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));
                        break;
                    case ">":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThan(fieldValue));
                        break;
                    case "<":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThan(fieldValue));
                        break;
                    case ">=":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        break;
                    case "<=":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(fieldValue));
                        break;
                    case "between":
                        long time2 = Long.parseLong(fieldValue2);
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(DateUtil.daFormat(time2)));
                        break;
                }


            }
        }

    }

    class TimeControl extends MyType {
        @Override
        void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field) {
            if ("&&".equals(logic)) {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.and(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.and(sqlTable.column(field), SqlBuilder.isEqualTo(fieldValue));
                        break;
                    case "<>":
                        where.and(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));
                        break;
                    case ">":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThan(fieldValue));
                        break;
                    case "<":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThan(fieldValue));
                        break;
                    case ">=":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        break;
                    case "<=":
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(fieldValue));
                        break;
                    case "between":
                        where.and(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(fieldValue2));
                        break;
                }

            } else {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        where.or(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.or(sqlTable.column(field), SqlBuilder.isEqualTo(fieldValue));
                        break;
                    case "<>":

                        where.or(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));
                        break;
                    case ">":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThan(fieldValue));
                        break;
                    case "<":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThan(fieldValue));
                        break;
                    case ">=":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        break;
                    case "<=":
                        where.or(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(fieldValue));
                        break;
                    case "between":
                        where.or(sqlTable.column(field), SqlBuilder.isGreaterThanOrEqualTo(fieldValue));
                        where.and(sqlTable.column(field), SqlBuilder.isLessThanOrEqualTo(fieldValue2));
                        break;
                }

            }
        }
    }

    /**
     * 下拉控件类型
     */
    class SelectControl extends MyType {
        @Override
        void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field) {
            List list = new ArrayList<>();
            if (StringUtils.isNoneBlank(fieldValue) && fieldValue.charAt(0) == '[' && !selectIgnore.contains(jnpfKey)) {
                list = JSONUtil.toList(fieldValue, String.class);
                if (!Objects.equals(operator, "in") && !Objects.equals(operator, "notIn")) {
                    fieldValue = String.join(",", list);
                }
            }
            if (selectIgnore.contains(jnpfKey) && StringUtils.isBlank(fieldValue)) {
                fieldValue = "[]";
            }
            if ("&&".equals(logic)) {
                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("")));
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("[]")));
                        where.and(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("")));
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("[]")));
                        where.and(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        convertSqlServerLike();
                        where.and(sqlTable.column(field), SqlBuilder.isLike(fieldValue));
                        break;
                    case "<>":
                        where.and(sqlTable.column(field), SqlBuilder.isNotLike(fieldValue));

                        break;
                    case "like":
                        convertSqlServerLike();
                        where.and(sqlTable.column(field), SqlBuilder.isLike("%" + fieldValue + "%"));
                        break;
                    case "notLike":
                        convertSqlServerLike();
                        where.and(sqlTable.column(field), SqlBuilder.isNotLike("%" + fieldValue + "%"));
                        break;
                    case "in":
                        List<String> dataList = JsonUtil.getJsonToList(fieldValue, String.class);
                        if (dataList.size() > 0) {
                            List<AndOrCriteriaGroup> group3 = new ArrayList<>();
                            String valueFirst = "";
                            for (int i = 0; i < dataList.size(); i++) {
                                String value = dataList.get(i);
                                value = convertSqlServerLike(value);
                                AndOrCriteriaGroup condition = null;
                                if (i == 0) {
                                    valueFirst = value;
                                } else {
                                    condition = SqlBuilder.or(sqlTable.column(field), SqlBuilder.isLike("%" + value + "%"));
                                    group3.add(condition);
                                }

                            }
                            where.and(sqlTable.column(field), SqlBuilder.isLike("%" + valueFirst + "%"), group3);
                        }

                        break;
                    case "notIn":
                        List<String> dataList2 = JsonUtil.getJsonToList(fieldValue, String.class);
                        if (dataList2.size() > 0) {
                            for (int i = 0; i < dataList2.size(); i++) {
                                String value = dataList2.get(i);
                                where.and(sqlTable.column(field), SqlBuilder.isNotLike("%" + value + "%"));
                            }
                        }

                        break;
                }

            } else {

                switch (operator) {
                    case "null":
                        List<AndOrCriteriaGroup> group = new ArrayList<>();
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("")));
                        group.add(SqlBuilder.or(sqlTable.column(field), SqlBuilder.isEqualTo("[]")));
                        where.or(sqlTable.column(field), SqlBuilder.isNull(), group);
                        break;
                    case "notNull":
                        List<AndOrCriteriaGroup> group2 = new ArrayList<>();
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("")));
                        group2.add(SqlBuilder.and(sqlTable.column(field), SqlBuilder.isNotEqualTo("[]")));
                        where.or(sqlTable.column(field), SqlBuilder.isNotNull(), group2);
                        break;
                    case "==":
                        where.or(sqlTable.column(field), SqlBuilder.isEqualTo(fieldValue));
                        break;
                    case "<>":

                        where.or(sqlTable.column(field), SqlBuilder.isNotEqualTo(fieldValue));
                        break;
                    case "like":
                        convertSqlServerLike();
                        where.or(sqlTable.column(field), SqlBuilder.isLike("%" + fieldValue + "%"));
                        break;
                    case "notLike":
                        convertSqlServerLike();
                        where.or(sqlTable.column(field), SqlBuilder.isNotLike("%" + fieldValue + "%"));
                        break;
                    case "in":
                        if (selectIgnore.contains(jnpfKey)) {
                            convertSqlServerLike();
                            where.or(sqlTable.column(field), SqlBuilder.isLike(fieldValue));
                        } else {
                            if (list.size() > 0) {
                                where.or(sqlTable.column(field), SqlBuilder.isIn(list));
                            }

                        }
                        break;
                    case "notIn":
                        if (selectIgnore.contains(jnpfKey)) {
                            List<String> data = JsonUtil.getJsonToList(fieldValue, String.class);
                            if (data.size() > 0) {
                                where.or(sqlTable.column(field), SqlBuilder.isNotLike(data));
                            }

                        } else {
                            if (list != null && list.size() > 0) {
                                where.or(sqlTable.column(field), SqlBuilder.isNotIn(list));
                            }

                        }
                        break;
                }

            }
        }

    }

    private abstract class MyType {
        abstract void judge(QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder where, SqlTable sqlTable, String field);
    }

    /**
     * SQLSERVER数据库 like括号语法
     * @param val
     * @return
     */
    private String convertSqlServerLike(String val){
        if(DbBase.SQL_SERVER.equals(dbType)){
            val = val.replaceAll("\\[", "[[]");
        }
        return val;
    }

    private void convertSqlServerLike(){
        if(DbBase.SQL_SERVER.equals(dbType)){
            fieldValue = convertSqlServerLike(fieldValue);
        }
    }


}
