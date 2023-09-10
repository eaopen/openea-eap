package org.openea.eap.extj.form.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.form.model.form.MultipleControlEnum;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.TIMESTAMP;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 在线详情编辑工具类
 *
 *
 */
@Slf4j
@Component
public class FormInfoUtils {
    @Autowired
    private ServiceBaseUtil serviceUtil;

    /**
     * 转换数据格式(编辑页)
     *
     * @param modelList 控件
     * @param dataMap   数据
     * @return
     */
    public Map<String, Object> swapDataInfoType(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
        dataMap = Optional.ofNullable(dataMap).orElse(new HashMap<>());
        List<String> systemConditions = new ArrayList() {{
            add(JnpfKeyConsts.CURRORGANIZE);
            add(JnpfKeyConsts.CURRDEPT);
            add(JnpfKeyConsts.CURRPOSITION);
        }};
        for (FieLdsModel swapDataVo : modelList) {
            String jnpfKey = swapDataVo.getConfig().getJnpfKey();
            String vModel = swapDataVo.getVModel();
            Object value = dataMap.get(vModel);
            if (value == null || ObjectUtil.isEmpty(value)) {
                if (systemConditions.contains(jnpfKey)) {
                    dataMap.put(vModel, " ");
                }
                continue;
            }
            switch (jnpfKey) {
                case JnpfKeyConsts.UPLOADFZ:
                case JnpfKeyConsts.UPLOADIMG:
                    List<Map<String, Object>> fileList = JsonUtil.getJsonToListMap(String.valueOf(value));
                    dataMap.put(vModel, fileList.size() == 0 ? new ArrayList<>() : fileList);
                    break;

                case JnpfKeyConsts.DATE:
                    //判断是否为时间戳格式
                    Object dateObj = dataMap.get(vModel);
                    LocalDateTime dateTime = null;
                    if (dateObj instanceof LocalDateTime) {
                        dateTime = (LocalDateTime) dateObj;
                    } else if (dateObj instanceof Timestamp) {
                        dateTime = ((Timestamp) dateObj).toLocalDateTime();
                    } else {
                        dateTime = LocalDateTimeUtil.of(cn.hutool.core.date.DateUtil.parse(dateObj.toString()));
                    }
                    dataMap.put(vModel, dateTime != null ? DateUtil.localDateTime2Millis(dateTime) : null);
                    break;

                case JnpfKeyConsts.SWITCH:
                case JnpfKeyConsts.SLIDER:
                    dataMap.put(vModel, value != null ? Integer.parseInt(String.valueOf(value)) : null);
                    break;
                //系统自动生成控件
                case JnpfKeyConsts.CURRORGANIZE:
                case JnpfKeyConsts.CURRDEPT:
                    if ("all".equals(swapDataVo.getShowLevel())) {
                        //todo
                        String organizeName = "";
                        dataMap.put(vModel, organizeName);
                    } else {
                        OrganizeEntity organizeEntity = serviceUtil.getOrganizeInfo(String.valueOf(value));
                        dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : value);
                    }
                    break;

                case JnpfKeyConsts.CURRPOSITION:
                    PositionEntity positionEntity = serviceUtil.getPositionInfo(String.valueOf(value));
                    dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
                    break;

                case JnpfKeyConsts.CREATEUSER:
                case JnpfKeyConsts.MODIFYUSER:
                    UserEntity userEntity = serviceUtil.getUserInfo(String.valueOf(value));
                    String userValue = Objects.nonNull(userEntity) ? userEntity.getAccount().equalsIgnoreCase("admin")
                            ? "管理员/admin" : userEntity.getRealName() + "/" + userEntity.getAccount() : String.valueOf(value);
                    dataMap.put(vModel, userValue);
                    break;

                case JnpfKeyConsts.CREATETIME:
                case JnpfKeyConsts.MODIFYTIME:
                    dataMap.put(vModel, value);
                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    String dateValue = "";
                    if (ObjectUtil.isNotEmpty(value)) {
                        if (value instanceof TIMESTAMP) {
                            String s2 = value.toString();
                            String substring = s2.substring(0, s2.lastIndexOf("."));
                            dateValue = substring;
                        } else if (value instanceof Date) {
                            dateValue = DateUtil.dateToString((Date) value, pattern);
                        } else {
                            dateValue = DateUtil.dateToString(DateUtil.localDateTimeToDate((LocalDateTime) value), pattern);
                        }
                    }
                    dataMap.put(vModel, dateValue);
                    break;
                default:
                    if (FormPublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                        String[][] data = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
                        dataMap.put(vModel, data);
                    } else if (FormPublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                        List<String> list = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                        dataMap.put(vModel, list);
                    } else {
                        dataMap.put(vModel, value);
                    }
                    break;
            }
        }
        return dataMap;
    }

    /**
     * 转换数据格式(编辑页)
     *
     * @param modelList 控件
     * @param dataMap   数据
     * @return
     */
    public Map<String, Object> getInitLineData(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
        for (FieLdsModel swapDataVo : modelList) {
            String jnpfKey = swapDataVo.getConfig().getJnpfKey();
            String vModel = swapDataVo.getVModel();
            Object value = dataMap.get(vModel);
            if (value == null || ObjectUtil.isEmpty(value)) {
                continue;
            }
            switch (jnpfKey) {
                case JnpfKeyConsts.UPLOADFZ:
                case JnpfKeyConsts.UPLOADIMG:
                    List<Map<String, Object>> fileList = JsonUtil.getJsonToListMap(String.valueOf(value));
                    dataMap.put(vModel, fileList);
                    break;

                case JnpfKeyConsts.DATE:
                    //处理为时间戳
                    String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null
                            && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
                    String s1 = String.valueOf(value);
                    Long s = null;
                    try {
                        s = sdf.parse(s1).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dataMap.put(vModel, s);
                    break;

                case JnpfKeyConsts.SWITCH:
                case JnpfKeyConsts.SLIDER:
                    dataMap.put(vModel, value != null ? Integer.parseInt(String.valueOf(value)) : null);
                    break;
                //系统自动生成控件
                case JnpfKeyConsts.CURRORGANIZE:
                case JnpfKeyConsts.CURRDEPT:
                    if ("all".equals(swapDataVo.getShowLevel())) {
                        //todo PermissionUtil
                        String organizeName = "";
                        dataMap.put(vModel, organizeName);
                    } else {
                        OrganizeEntity organizeEntity = serviceUtil.getOrganizeInfo(String.valueOf(value));
                        dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : value);
                    }
                    break;

                case JnpfKeyConsts.CURRPOSITION:
                    PositionEntity positionEntity = serviceUtil.getPositionInfo(String.valueOf(value));
                    dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
                    break;

                case JnpfKeyConsts.CREATEUSER:
                case JnpfKeyConsts.MODIFYUSER:
                    UserEntity userEntity = serviceUtil.getUserInfo(String.valueOf(value));
                    String userValue = Objects.nonNull(userEntity) ? userEntity.getAccount().equalsIgnoreCase("admin")
                            ? "管理员" : userEntity.getRealName() : String.valueOf(value);
                    dataMap.put(vModel, userValue);
                    break;
                default:
                    if (FormPublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                        String[][] data = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
                        dataMap.put(vModel, data);
                    } else if (FormPublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                        List<String> list = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                        dataMap.put(vModel, list);
                    } else {
                        dataMap.put(vModel, value);
                    }
                    break;
            }
        }
        return dataMap;
    }
}
