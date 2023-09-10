package org.openea.eap.extj.form.util;

import org.openea.eap.extj.form.model.form.MultipleControlEnum;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;
import org.openea.eap.extj.model.visualJson.analysis.FormEnum;
import org.openea.eap.extj.model.visualJson.analysis.FormModel;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线开发公用
 *
 * 
 */
public class FormPublicUtils {

    /**
     * map key转小写
     *
     * @param requestMap
     * @return
     */
    public static Map<String, Object> mapKeyToLower(Map<String, ?> requestMap) {
        // 非空校验
        if (requestMap.isEmpty()) {
            return null;
        }
        // 初始化放转换后数据的Map
        Map<String, Object> responseMap = new HashMap<>(16);
        // 使用迭代器进行循环遍历
        Set<String> requestSet = requestMap.keySet();
        Iterator<String> iterator = requestSet.iterator();
        iterator.forEachRemaining(obj -> {
            // 判断Key对应的Value是否为Map
            if ((requestMap.get(obj) instanceof Map)) {
                // 递归调用，将value中的Map的key转小写
                responseMap.put(obj.toLowerCase(), mapKeyToLower((Map) requestMap.get(obj)));
            } else {
                // 直接将key小写放入responseMap
                responseMap.put(obj.toLowerCase(), requestMap.get(obj));
            }
        });

        return responseMap;
    }


    /**
     * 获取map中第一个数据值
     *
     * @param map 数据源
     * @return
     */
    public static Object getFirstOrNull(Map<String, Object> map) {
        Object obj = null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    /**
     * 去除列表里无用的控件
     *
     * @param fieldsModelList
     * @return
     */
    public static void removeUseless(List<FieLdsModel> fieldsModelList) {
        for (int i = 0; i < fieldsModelList.size(); i++) {
            if (fieldsModelList.get(i).getConfig().getJnpfKey() == null) {
                continue;
            }
            if (fieldsModelList.get(i).getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)) {
                continue;
            }
        }
    }


    /**
     * 递归控件
     *
     * @param
     * @return
     */
    public static List<FieLdsModel> recursionFields(List<TableModel> tableModels, List<FieLdsModel> fieLdsModelList) {
        List<FieLdsModel> allFields = new ArrayList<>();
        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(tableModels);
        recursionForm.setList(fieLdsModelList);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);
        //主表数据
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());

        mast.stream().forEach(formModel -> allFields.add(formModel.getFormColumnModel().getFieLdsModel()));

        //列表子表数据
        List<FormAllModel> mastTable = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());

        mastTable.stream().forEach(formModel -> allFields.add(formModel.getFormMastTableModel().getMastTable().getFieLdsModel()));

        //子表
        List<FormAllModel> childTable = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        childTable.stream().forEach(formModel -> {
            String jnpfKey = formModel.getJnpfKey();
            String vModel = formModel.getChildList().getTableModel();
            List<FieLdsModel> childs = formModel.getChildList().getChildList().stream().map(c -> c.getFieLdsModel()).collect(Collectors.toList());
            FieLdsModel fieLdsModel = new FieLdsModel();
            ConfigModel configModel = new ConfigModel();
            configModel.setJnpfKey(jnpfKey);
            configModel.setChildren(childs);
            configModel.setTableName(formModel.getChildList().getTableName());
            fieLdsModel.setConfig(configModel);
            fieLdsModel.setVModel(vModel);
            allFields.add(fieLdsModel);
        });
        return allFields;
    }

    /**
     * 判断字符串是否以某个字符开头
     *
     * @param var1 完整字符串
     * @param var2 统计字符
     * @return
     */
    public static Boolean getMultiple(String var1, String var2) {
        if (var1.startsWith(var2)) {
            return true;
        }
        return false;
    }

    /**
     * 数据字典处理（从缓存中取出）
     *
     * @param dataList
     * @param swapModel
     * @return
     */
    public static Map<String, Object> getDataMap(List<Map<String, Object>> dataList, FieLdsModel swapModel) {
        String label;
        String value;
        if (swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.CASCADER) || swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.TREESELECT)) {
            PropsBeanModel propsModel = swapModel.getProps().getPropsModel();
            if (propsModel != null) {
                label = swapModel.getProps().getPropsModel().getLabel();
                value = swapModel.getProps().getPropsModel().getValue();
            } else {
                label = swapModel.getProps().getProps().getLabel();
                value = swapModel.getProps().getProps().getValue();
            }
        } else {
            label = swapModel.getConfig().getProps().getLabel();
            value = swapModel.getConfig().getProps().getValue();
        }
        Map<String, Object> dataInterfaceMap = new HashMap<>();
        dataList.stream().forEach(data -> {
            dataInterfaceMap.put(String.valueOf(data.get(value)), String.valueOf(data.get(label)));
        });
        return dataInterfaceMap;
    }

    /**
     * 获取时间(+8)
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateByFormat(Long date, String format) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(format);
        String dateString = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("+8")));
        return dateString;
    }

    /**
     * 递归表单控件
     *
     * @param modelList   所有控件
     * @param mainFields  主表
     * @param childFields 子表
     * @param models      二维码 条形码
     */
    public static void recurseFiled(List<FieLdsModel> modelList, List<FieLdsModel> mainFields, List<FieLdsModel> childFields, List<FormModel> models) {
        for (FieLdsModel fieLdsModel : modelList) {
            ConfigModel config = fieLdsModel.getConfig();
            String jnpfkey = config.getJnpfKey();
            List<FieLdsModel> childrenList = config.getChildren();
            boolean isJnpfKey = StringUtil.isEmpty(jnpfkey);
            if (FormEnum.row.getMessage().equals(jnpfkey) || FormEnum.card.getMessage().equals(jnpfkey)
                    || FormEnum.tab.getMessage().equals(jnpfkey) || FormEnum.collapse.getMessage().equals(jnpfkey)
                    || isJnpfKey) {
                if (childrenList.size() > 0) {
                    recurseFiled(childrenList, mainFields, childFields, models);
                } else {
                    mainFields.add(fieLdsModel);
                }
            } else if (FormEnum.table.getMessage().equals(jnpfkey)) {
                childFields.add(fieLdsModel);
            } else if (FormEnum.groupTitle.getMessage().equals(jnpfkey) || FormEnum.divider.getMessage().equals(jnpfkey) || FormEnum.JNPFText.getMessage().equals(jnpfkey)) {

            } else if (FormEnum.QR_CODE.getMessage().equals(jnpfkey) || FormEnum.BARCODE.getMessage().equals(jnpfkey)) {
                FormModel formModel = JsonUtil.getJsonToBean(fieLdsModel, FormModel.class);
                models.add(formModel);
            } else {
                mainFields.add(fieLdsModel);
            }
        }
    }

    public static void recurseOnlineFiled(List<FieLdsModel> modelList, List<FieLdsModel> mainFields, List<FieLdsModel> childFields) {
        for (FieLdsModel fieLdsModel : modelList) {
            ConfigModel config = fieLdsModel.getConfig();
            String jnpfkey = config.getJnpfKey();
            List<FieLdsModel> childrenList = config.getChildren();
            boolean isJnpfKey = StringUtil.isEmpty(jnpfkey);
            if (FormEnum.row.getMessage().equals(jnpfkey) || FormEnum.card.getMessage().equals(jnpfkey)
                    || FormEnum.tab.getMessage().equals(jnpfkey) || FormEnum.collapse.getMessage().equals(jnpfkey)
                    || isJnpfKey) {
                if (childrenList.size() > 0) {
                    recurseOnlineFiled(childrenList, mainFields, childFields);
                } else {
                    mainFields.add(fieLdsModel);
                }
            } else if (FormEnum.table.getMessage().equals(jnpfkey)) {
                childFields.add(fieLdsModel);
            } else if (FormEnum.groupTitle.getMessage().equals(jnpfkey) || FormEnum.divider.getMessage().equals(jnpfkey) || FormEnum.JNPFText.getMessage().equals(jnpfkey)) {

            } else {
                mainFields.add(fieLdsModel);
            }
        }
    }

    /**
     * @param redisMap   缓存集合
     * @param modelData  数据
     * @param isMultiple 是否多选
     * @return
     */
    public static String getDataInMethod(Map<String, Object> redisMap, Object modelData, Boolean isMultiple) {
        if (redisMap == null || redisMap.isEmpty()) {
            return modelData.toString();
        }
        String Separator = isMultiple ? ";" : "/";
        String s2;
        if (FormPublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] data = JsonUtil.getJsonToBean(String.valueOf(modelData), String[][].class);
            List<String> addList = new ArrayList<>();
            for (String[] AddressData : data) {
                List<String> adList = new ArrayList<>();
                for (String s : AddressData) {
                    adList.add(String.valueOf(redisMap.get(s)));
                }
                addList.add(String.join("/" , adList));
            }
            s2 = String.join(";" , addList);
        } else if (FormPublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
            List<String> modelDataList = JsonUtil.getJsonToList(String.valueOf(modelData), String.class);
            modelDataList = modelDataList.stream().map(s -> String.valueOf(redisMap.get(s))).collect(Collectors.toList());
            s2 = String.join(Separator, modelDataList);
        } else {
            String[] modelDatas = String.valueOf(modelData).split(",");
            StringBuilder dynamicData = new StringBuilder();
            for (int i = 0; i < modelDatas.length; i++) {
                modelDatas[i] = String.valueOf(redisMap.get(modelDatas[i]));
                dynamicData.append(modelDatas[i] + Separator);
            }
            s2 = dynamicData.deleteCharAt(dynamicData.length() - 1).toString();
        }
        return StringUtil.isEmpty(s2) ? modelData.toString() : s2;
    }

    /**
     * @param mapList
     * @return List<Map < String, Object>>
     * @Date 21:51 2020/11/11
     * @Description 将map中的所有key转化为小写
     */
    public static List<Map<String, Object>> toLowerKeyList(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> newMapList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> resultMap = new HashMap(16);
            Set<String> sets = map.keySet();
            for (String key : sets) {
                resultMap.put(key.toLowerCase(), map.get(key));
            }
            newMapList.add(resultMap);
        }
        return newMapList;
    }

    /**
     * 递归控件，除子表外控件全部提到同级
     * 取子集，子表不外提
     *
     * @return
     */
    public static void recursionFieldsExceptChild(List<FieLdsModel> allFields, List<FieLdsModel> fieLdsModelList) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            ConfigModel config = fieLdsModel.getConfig();
            String jnpfKey = config.getJnpfKey();
            if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)) {
                allFields.add(fieLdsModel);
                continue;
            } else {
                if (config.getChildren() != null) {
                    recursionFieldsExceptChild(allFields, config.getChildren());
                } else {
                    if (jnpfKey == null) {
                        continue;
                    }
                    allFields.add(fieLdsModel);
                }
            }
        }
    }

    /**
     * 转换时间格式
     *
     * @param time
     * @return
     */
    public static String getTimeFormat(String time) {
        String result;
        switch (time.length()) {
            case 16:
                result = time + ":00";
                break;
            case 19:
                result = time;
                break;
            case 21:
                result = time.substring(0, time.length() - 2);
                break;
            case 10:
                result = time + " 00:00:00";
                break;
            case 8:
                result = "2000-01-01 " + time;
                break;
            case 7:
                result = time + "-01 00:00:00";
                break;
            case 4:
                result = time + "-01-01 00:00:00";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    public static String getLastTimeFormat(String time) {
        String result;
        switch (time.length()) {
            case 16:
                result = time + ":00";
                break;
            case 19:
                result = time;
                break;
            case 10:
                result = time + " 23:59:59";
                break;
            case 8:
                result = "2000-01-01 " + time;
                break;
            case 7:
                //获取月份最后一天
                String[] split = time.split("-");
                Calendar cale = Calendar.getInstance();
                cale.set(Calendar.YEAR, Integer.valueOf(split[0]));//赋值年份
                cale.set(Calendar.MONTH, Integer.valueOf(split[1]) - 1);//赋值月份
                int lastDay = cale.getActualMaximum(Calendar.DAY_OF_MONTH);//获取月最大天数
                cale.set(Calendar.DAY_OF_MONTH, lastDay);//设置日历中月份的最大天数
                cale.set(Calendar.HOUR_OF_DAY, 23);
                cale.set(Calendar.SECOND, 59);
                cale.set(Calendar.MINUTE, 59);
                result = DateUtil.daFormatHHMMSS(cale.getTime().getTime());
                break;
            case 4:
                result = time + "-12-31 23:59:59";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 判断时间是否在设置范围内
     *
     * @param swapDataVo
     * @param format
     * @param value
     * @param data
     * @param jnpfKey
     * @return
     */
    public static boolean dateTimeCondition(FieLdsModel swapDataVo, String format, Object value, Map<String, Object> data, String jnpfKey) {
        long valueTimeLong;
        //输入值转long
        if (value instanceof String) {
            valueTimeLong = cn.hutool.core.date.DateUtil.parse(String.valueOf(value), format).getTime();
        } else {
            //输入值按格式补全
            String timeFormat = getTimeFormat(String.valueOf(value));
            valueTimeLong = DateUtil.stringToDate(timeFormat).getTime();
        }
        boolean timeHasRangeError = false;
        //开始时间判断
        if ((swapDataVo.getConfig().getStartTimeRule() && StringUtil.isNotEmpty(swapDataVo.getConfig().getStartTimeValue()))
                || (swapDataVo.getConfig().getStartTimeRule()) && StringUtil.isNotEmpty(swapDataVo.getConfig().getStartRelationField())) {
            String startTimeValue = swapDataVo.getConfig().getStartTimeValue();
            String startTimeType = swapDataVo.getConfig().getStartTimeType();
            String startTimeTarget = swapDataVo.getConfig().getStartTimeTarget();
            String startTimeRelationField = swapDataVo.getConfig().getStartRelationField();
            //根据类型获取开始时间戳
            long startTimeLong = getDateTimeLong(data, jnpfKey, startTimeValue, startTimeType, startTimeTarget, startTimeRelationField);
            if (startTimeLong != 0 && valueTimeLong < startTimeLong) {
                timeHasRangeError = true;
            }
        }
        //结束时间判断
        if ((swapDataVo.getConfig().getEndTimeRule() && StringUtil.isNotEmpty(swapDataVo.getConfig().getEndTimeValue()))
                || (swapDataVo.getConfig().getEndTimeRule()) && StringUtil.isNotEmpty(swapDataVo.getConfig().getEndRelationField())) {
            String endTimeValue = swapDataVo.getConfig().getEndTimeValue();
            String endTimeType = swapDataVo.getConfig().getEndTimeType();
            String endTimeTarget = swapDataVo.getConfig().getEndTimeTarget();
            String endTimeRelationField = swapDataVo.getConfig().getEndRelationField();
            //根据类型获取开始时间戳
            long endTimeLong = getDateTimeLong(data, jnpfKey, endTimeValue, endTimeType, endTimeTarget, endTimeRelationField);
            if (endTimeLong != 0 && valueTimeLong > endTimeLong) {
                timeHasRangeError = true;
            }
        }
        return timeHasRangeError;
    }

    /**
     * 根据类型获取时间戳
     *
     * @param data
     * @param jnpfKey
     * @param timeValue
     * @param timeType
     * @param timeTarget
     * @return
     */
    private static long getDateTimeLong(Map<String, Object> data, String jnpfKey, String timeValue, String timeType, String timeTarget,
                                        String stringimeRelationField) {
        long startTimeLong = 0;
        switch (timeType) {
            case "1"://特定时间
                startTimeLong = Long.parseLong(timeValue);
                break;
            case "2"://表单字段
                if (stringimeRelationField != null) {
                    String fieldValue = "";
                    if (stringimeRelationField.toLowerCase().contains(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {//子
                        String[] split = stringimeRelationField.split("-");
                        fieldValue = data.get(split[1]) != null ? data.get(split[1]).toString() : "";
                    } else {//主副
                        Map<String, Object> mainAndMast = data.get("mainAndMast") != null ? JsonUtil.entityToMap(data.get("mainAndMast")) : data;
                        fieldValue = mainAndMast.get(stringimeRelationField) != null ? mainAndMast.get(stringimeRelationField).toString() : "";
                    }
                    if (StringUtil.isNotEmpty(fieldValue)) {
                        String timeFormat = getTimeFormat(fieldValue);
                        startTimeLong = cn.hutool.core.date.DateUtil.parse(timeFormat, "yyyy-MM-dd HH:mm:ss").getTime();
                    }
                }
                break;
            case "3"://填写当前时间
                startTimeLong = new Date().getTime();
                break;
            case "4"://当前时间前
                Calendar caledel = Calendar.getInstance();
                if (JnpfKeyConsts.DATE.equals(jnpfKey)) {
                    switch (timeTarget) {
                        case "1"://年
                            caledel.set(Calendar.YEAR, caledel.get(Calendar.YEAR) - Integer.valueOf(timeValue));//赋值年份
                            break;
                        case "2"://月
                            caledel.set(Calendar.MONTH, caledel.get(Calendar.MONTH) - Integer.valueOf(timeValue));
                            break;
                        case "3"://日
                            caledel.set(Calendar.DAY_OF_MONTH, caledel.get(Calendar.DAY_OF_MONTH) - Integer.valueOf(timeValue));
                            break;
                    }
                } else {
                    switch (timeTarget) {
                        case "1"://时
                            caledel.set(Calendar.HOUR_OF_DAY, caledel.get(Calendar.HOUR_OF_DAY) - Integer.valueOf(timeValue));
                            break;
                        case "2"://分
                            caledel.set(Calendar.MINUTE, caledel.get(Calendar.MINUTE) - Integer.valueOf(timeValue));
                            break;
                        case "3"://秒
                            caledel.set(Calendar.SECOND, caledel.get(Calendar.SECOND) - Integer.valueOf(timeValue));
                            break;
                    }
                }
                startTimeLong = caledel.getTime().getTime();
                break;
            case "5"://当前时间后
                Calendar cale = Calendar.getInstance();
                if (JnpfKeyConsts.DATE.equals(jnpfKey)) {
                    switch (timeTarget) {
                        case "1"://年
                            cale.set(Calendar.YEAR, cale.get(Calendar.YEAR) + Integer.valueOf(timeValue));//赋值年份
                            break;
                        case "2"://月
                            cale.set(Calendar.MONTH, cale.get(Calendar.MONTH) + Integer.valueOf(timeValue));
                            break;
                        case "3"://日
                            cale.set(Calendar.DAY_OF_MONTH, cale.get(Calendar.DAY_OF_MONTH) + Integer.valueOf(timeValue));
                            break;
                    }
                } else {
                    switch (timeTarget) {
                        case "1"://时
                            cale.set(Calendar.HOUR_OF_DAY, cale.get(Calendar.HOUR_OF_DAY) + Integer.valueOf(timeValue));
                            break;
                        case "2"://分
                            cale.set(Calendar.MINUTE, cale.get(Calendar.MINUTE) + Integer.valueOf(timeValue));
                            break;
                        case "3"://秒
                            cale.set(Calendar.SECOND, cale.get(Calendar.SECOND) + Integer.valueOf(timeValue));
                            break;
                    }
                }
                startTimeLong = cale.getTime().getTime();
                break;
            default:
                break;
        }
        return startTimeLong;
    }

    /**
     * 字符串转数组
     *
     * @param value 值
     * @return
     */
    public static Object getDataConversion(Object value) {
        Object dataValue = getDataConversion(null, value);
        return dataValue;
    }

    /**
     * 字符串转数组
     *
     * @param redis 转换对象
     * @param value 值
     * @return
     */
    public static Object getDataConversion(Map<String, Object> redis, Object value) {
        Object dataValue = value;
        boolean iszhuanhuan = redis != null;
        try {
            List<List> list = JsonUtil.getJsonToList(String.valueOf(value), List.class);
            dataValue = list;
            if (iszhuanhuan) {
                StringJoiner joiner = new StringJoiner(",");
                for (List listChild : list) {
                    StringJoiner aa = new StringJoiner("/");
                    for (Object object : listChild) {
                        String value1 = redis.get(String.valueOf(object)) != null ? String.valueOf(redis.get(String.valueOf(object))) : "";
                        if (StringUtil.isNotEmpty(value1)) {
                            aa.add(value1);
                        }
                    }
                    joiner.add(aa.toString());
                }
                dataValue = joiner.toString();
            }
        } catch (Exception e) {
            try {
                List<String> list = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                dataValue = list;
                if (iszhuanhuan) {
                    StringJoiner joiner = new StringJoiner(",");
                    for (Object listChild : list) {
                        String value1 = redis.get(String.valueOf(listChild)) != null ? String.valueOf(redis.get(String.valueOf(listChild))) : "";
                        if (StringUtil.isNotEmpty(value1)) {
                            joiner.add(value1);
                        }
                    }
                    dataValue = joiner.toString();
                }
            } catch (Exception e1) {
                dataValue = String.valueOf(value);
                if(iszhuanhuan){
                    dataValue = redis.get(String.valueOf(value)) != null ? String.valueOf(redis.get(String.valueOf(value))) : "";
                }
            }
        }
        return dataValue;
    }


}

