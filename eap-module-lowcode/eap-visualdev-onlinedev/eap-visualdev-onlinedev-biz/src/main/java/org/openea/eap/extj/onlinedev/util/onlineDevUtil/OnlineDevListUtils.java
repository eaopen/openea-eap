package org.openea.eap.extj.onlinedev.util.onlineDevUtil;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceActionVo;
import org.openea.eap.extj.base.service.DataInterfaceService;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.OnlineDevData;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.CacheKeyEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.OnlineDevListDataVO;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;
import org.openea.eap.extj.onlinedev.service.VisualDevInfoService;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.permission.entity.*;
import org.openea.eap.extj.permission.service.*;
import org.openea.eap.extj.permission.util.PermissionUtil;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.context.SpringContext;
import org.openea.eap.extj.util.data.DataSourceContextHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OnlineDevListUtils {
    private static RedisUtil redisUtil;
    private static DictionaryDataService dictionaryDataService;
    private static UserService userService;
    private static PositionService positionService;
    //private static ProvinceService provinceService;
    private static OrganizeService organizeService;
    private static VisualdevService visualdevService;
    private static VisualdevModelDataService visualdevModelDataService;
    private static DataInterfaceService dataInterfaceService;
    private static VisualDevInfoService visualDevInfoService;
    private static RoleService roleService;
    private static GroupService groupService;

    /**
     * 初始化bean
     */
    public static void init() {
        visualdevModelDataService = SpringContext.getBean(VisualdevModelDataService.class);
        dictionaryDataService = SpringContext.getBean(DictionaryDataService.class);
        userService = SpringContext.getBean(UserService.class);
        positionService = SpringContext.getBean(PositionService.class);
        redisUtil = SpringContext.getBean(RedisUtil.class);
        //provinceService = SpringContext.getBean(ProvinceService.class);
        organizeService = SpringContext.getBean(OrganizeService.class);
        visualdevService = SpringContext.getBean(VisualdevService.class);
        dataInterfaceService = SpringContext.getBean(DataInterfaceService.class);
        visualDevInfoService = SpringContext.getBean(VisualDevInfoService.class);
        roleService = SpringContext.getBean(RoleService.class);
        groupService = SpringContext.getBean(GroupService.class);
    }

    /**
     * 查询条件
     *
     * @param list
     * @param searchList
     * @return
     */
    public static List<Map<String, Object>> getNoSwapList(List<Map<String, Object>> list, List<VisualColumnSearchVO> searchList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (searchList == null) {
            return list;
        }
        for (Map<String, Object> dataVo : list) {
            int i = 0;
            for (VisualColumnSearchVO vo : searchList) {
                Object dataModel = dataVo.get(vo.getVModel());
                if (dataModel == null || ObjectUtil.isEmpty(dataModel)) {
                    continue;
                }
                //多选框默认添加多选属性
                if (vo.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHECKBOX) || JnpfKeyConsts.CASCADER.equals(vo.getConfig().getJnpfKey())) {
                    vo.setMultiple(true);
                }
                if (vo.getSearchType().equals("1")) {
                    //多选框筛选
                    if (vo.getMultiple() != null && vo.getMultiple() == true) {
                        List<String> asList;
                        if (String.valueOf(dataModel).contains("[")) {
                            asList = JsonUtil.getJsonToList(String.valueOf(dataModel), String.class);
                        } else {
                            String[] multipleList = String.valueOf(dataModel).split(",");
                            asList = Arrays.asList(multipleList);
                        }
                        boolean b = asList.stream().anyMatch(t -> vo.getValue().toString().contains(t));
                        if (b) {
                            i++;
                        }
                    } else {
                        if (String.valueOf(vo.getValue()).equals(String.valueOf(dataModel))) {
                            i++;
                        }
                    }
                }
                if (vo.getSearchType().equals("2")) {
                    if (String.valueOf(dataModel).contains(String.valueOf(vo.getValue()))) {
                        i++;
                    }
                }
                if (vo.getSearchType().equals("3")) {
                    String key = vo.getConfig().getJnpfKey();
                    switch (key) {
                        case JnpfKeyConsts.MODIFYTIME:
                        case JnpfKeyConsts.CREATETIME:
                            JSONArray timeStampArray = (JSONArray) vo.getValue();
                            Long o1 = (Long) timeStampArray.get(0);
                            Long o2 = (Long) timeStampArray.get(1);

                            //时间戳转string格式
                            String startTime = DateUtil.daFormat(o1);
                            String endTime = DateUtil.daFormat(o2);
                            //处理时间查询条件范围
                            endTime = endTime.substring(0, 10);
                            String firstTimeDate = OnlineDatabaseUtils.getTimeFormat(startTime);
                            String lastTimeDate = OnlineDatabaseUtils.getLastTimeFormat(endTime);

                            String value = String.valueOf(dataModel);
                            if (value.contains(".")) {
                                value = value.substring(0, value.lastIndexOf("."));
                            }
                            //只判断到日期
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                boolean b = DateUtil.isEffectiveDate(sdf.parse(value), sdf.parse(firstTimeDate), sdf.parse(lastTimeDate));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case JnpfKeyConsts.NUM_INPUT:
                        case JnpfKeyConsts.CALCULATE:
                            Float firstValue = null;
                            Float secondValue = null;
                            JSONArray objects = (JSONArray) vo.getValue();
                            for (int k = 0; k < objects.size(); k++) {
                                Object n = objects.get(k);
                                if (ObjectUtil.isNotEmpty(n)) {
                                    if (k == 0) {
                                        firstValue = Float.parseFloat(String.valueOf(n));
                                    } else {
                                        secondValue = Float.parseFloat(String.valueOf(n));
                                    }
                                }
                            }
                            //数据
                            Float numValue = Float.parseFloat(String.valueOf(dataModel));

                            //条件1,2组合的情况
                            if (firstValue != null && secondValue == null) {
                                if (numValue >= firstValue) {
                                    i++;
                                }
                            }
                            if (firstValue != null && secondValue != null) {
                                if (numValue >= firstValue && numValue <= secondValue) {
                                    i++;
                                }
                            }
                            if (firstValue == null && secondValue != null) {
                                if (numValue <= secondValue) {
                                    i++;
                                }
                            }
                            break;
                        case JnpfKeyConsts.DATE:
                            String starTimeDates;
                            String endTimeDates;
                            if (dataModel == null) {
                                break;
                            }
                            //时间戳
                            if (!String.valueOf(vo.getValue()).contains(":") && !String.valueOf(vo.getValue()).contains("-")) {
                                JSONArray DateTimeStampArray = (JSONArray) vo.getValue();
                                Long d1 = (Long) DateTimeStampArray.get(0);
                                Long d2 = (Long) DateTimeStampArray.get(1);
                                long d1FirstTime = Long.parseLong(String.valueOf(d1));
                                long d2LastTime = Long.parseLong(String.valueOf(d2));

                                //时间戳转string格式
                                starTimeDates = DateUtil.daFormat(d1FirstTime);
                                endTimeDates = DateUtil.daFormat(d2LastTime);

                            } else {
                                //时间字符串
                                String[] keyArray = String.valueOf(vo.getValue()).split(",");
                                starTimeDates = keyArray[0];
                                endTimeDates = keyArray[1];
                            }
                            if (vo.getFormat() == null) {
                                starTimeDates = starTimeDates.substring(0, 10);
                                endTimeDates = endTimeDates.substring(0, 10);
                            }
                            starTimeDates = OnlineDatabaseUtils.getTimeFormat(starTimeDates);
                            endTimeDates = OnlineDatabaseUtils.getLastTimeFormat(endTimeDates);

                            String dateValue = dataModel.toString();
                            if (!dateValue.contains(":") && !dateValue.contains("-")) {
                                //时间戳
                                Long timeResult = (Long) dataModel;
                                dateValue = DateUtil.daFormat(timeResult);
                            }
                            if (dateValue.contains(".")) {
                                dateValue = dateValue.substring(0, dateValue.lastIndexOf("."));
                            }
                            dateValue = OnlineDatabaseUtils.getTimeFormat(dateValue);
                            //只判断到日期
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Boolean b = DateUtil.isEffectiveDate(sdfDate.parse(dateValue), sdfDate.parse(starTimeDates), sdfDate.parse(endTimeDates));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case JnpfKeyConsts.TIME:
                            JSONArray timeArray = (JSONArray) vo.getValue();
                            String start = String.valueOf(timeArray.get(0));
                            String end = String.valueOf(timeArray.get(1));
                            start = OnlineDatabaseUtils.getTimeFormat(start);
                            end = OnlineDatabaseUtils.getLastTimeFormat(end);
                            String timeValue = OnlineDatabaseUtils.getTimeFormat(String.valueOf(dataModel));
                            SimpleDateFormat timeSim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                boolean b = DateUtil.isEffectiveDate(timeSim.parse(timeValue), timeSim.parse(start), timeSim.parse(end));
                                if (b) {
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (i == searchList.size()) {
                    resultList.add(dataVo);
                }
            }
        }
        return resultList;
    }


    /**
     * 数据转换
     *
     * @param list
     * @param swapDataVoList
     * @return
     */
    public static List<OnlineDevListDataVO> getSwapList(List<OnlineDevListDataVO> list, List<FieLdsModel> swapDataVoList, String visualDevId, Boolean inlineEdit) {
        if (list.isEmpty()) {
            return list;
        }
        init();
        //预装数据后直接放入localCache 避免重新取Redis数据大量数据转换耗时
        Map<String, Object> localCache = new HashMap<>();
        Set<String> userList = new HashSet<>();
        Set<String> orgList = new HashSet<>();
        Set<String> posList = new HashSet<>();
        Set<String> allOrgList = new HashSet<>();
        Set<String> roleList = new HashSet<>();
        localCache.put("__user_list", userList);
        localCache.put("__org_list", orgList);
        localCache.put("__pos_list", posList);
        localCache.put("__allOrg_list", allOrgList);
        localCache.put("__role_list", roleList);
        //装填部分数据(用户,组织,岗位,角色)
        pageIdList(list, swapDataVoList, localCache);

        sysNeedSwapData(swapDataVoList, visualDevId, localCache);

        //redis key
        String dsName = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
        //组织
        Map<String, Object> orgMap = new HashMap<>(20);
        //组织需要递归显示
        Map<String, Object> allOrgMap = new HashMap<>(20);
        //岗位
        Map<String, Object> posMap = new HashMap<>(20);
        //人员
        Map<String, Object> userMap = new HashMap<>(20);
        //角色
        Map<String, Object> roleMap = new HashMap<>(20);

        //分组
        Map<String, Object> groupMap = new HashMap<>(20);
        //省市区
        Map<String, Object> proMap = new HashMap<>(20);

        for (FieLdsModel swapDataVo : swapDataVoList) {
            String jnpfKey = swapDataVo.getConfig().getJnpfKey();
            String swapVModel = swapDataVo.getVModel();
            String vModel =  inlineEdit ? swapDataVo.getVModel() + "_name" : swapDataVo.getVModel();
            String dataType = String.valueOf(swapDataVo.getConfig().getDataType());
            Boolean isMultiple = Objects.nonNull(swapDataVo.getMultiple()) ? swapDataVo.getMultiple() : false;
            for (OnlineDevListDataVO listVo : list) {
                try {
                    Map<String, Object> dataMap = listVo.getData();
                    if (StringUtil.isEmpty(String.valueOf(dataMap.get(swapVModel))) || String.valueOf(dataMap.get(swapVModel)).equals("[]")
                            || String.valueOf(dataMap.get(swapVModel)).equals("null")) {
                        if (jnpfKey.equals(JnpfKeyConsts.CHILD_TABLE)){
                            dataMap.put(vModel,new ArrayList<>());
                        } else {
                            if (inlineEdit){
                                dataMap.put(swapVModel,null);
                            }
                            dataMap.put(vModel, null);
                        }
                        continue;
                    } else {
                        switch (jnpfKey) {
                            //公司组件
                            case JnpfKeyConsts.COMSELECT:
                                //部门组件
                            case JnpfKeyConsts.DEPSELECT:
                                //所属部门
                            case JnpfKeyConsts.CURRDEPT:
                                //所属组织
                            case JnpfKeyConsts.CURRORGANIZE:
                                if (orgMap.size() == 0) {
                                    List<OrganizeEntity> organizeEntityList = organizeService.getOrgEntityList(orgList);
                                    organizeEntityList.stream().forEach(org -> {
                                        orgMap.put(org.getId(), org.getFullName());
                                    });
                                    //转成json存入redis
                                    String orgString = JsonUtil.getObjectToString(orgMap);
                                    redisUtil.insert(visualDevId + CacheKeyEnum.ORG.getName(), orgString, 60 * 5);
                                }

                                if ("all".equals(swapDataVo.getShowLevel())) {
                                    //多级组织
                                    if (allOrgMap.size() == 0) {
                                        //需要递归的组织控件放入缓存中
                                        for (String org : allOrgList) {
                                            String infoByOrgId = PermissionUtil.getLinkInfoByOrgId(org, organizeService, false);
                                            allOrgMap.put(org, infoByOrgId);
                                        }
                                        //转成json存入redis
                                        String allOrgString = JsonUtil.getObjectToString(allOrgMap);
                                        redisUtil.insert(visualDevId + CacheKeyEnum.AllORG.getName(), allOrgString, 60 * 5);
                                    }
                                    dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(allOrgMap, dataMap.get(swapVModel),isMultiple));
                                } else {
                                    dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(orgMap, dataMap.get(swapVModel),isMultiple));
                                }
                                break;

                            //岗位组件
                            case JnpfKeyConsts.POSSELECT:
                                //所属岗位
                            case JnpfKeyConsts.CURRPOSITION:
                                if (posMap.size() == 0) {
                                    List<PositionEntity> positionList = positionService.getPosList(posList);
                                    positionList.stream().forEach(positionEntity -> posMap.put(positionEntity.getId(), positionEntity.getFullName()));
                                    //转成json存入redis
                                    String posString = JsonUtil.getObjectToString(posMap);
                                    redisUtil.insert(visualDevId + CacheKeyEnum.POS.getName(), posString, 60 * 5);
                                }
                                String posData = OnlinePublicUtils.getDataInMethod(posMap, dataMap.get(swapVModel),isMultiple);
                                dataMap.put(vModel, posData);
                                break;

                            //用户组件
                            case JnpfKeyConsts.USERSELECT:
                                //创建用户
                            case JnpfKeyConsts.CREATEUSER:
                                //修改用户
                            case JnpfKeyConsts.MODIFYUSER:
                                if (userMap.size() == 0) {
                                    List<UserEntity> userEntityList = userService.getUserNameList(userList);
                                    userEntityList.stream().forEach(userEntity -> userMap.put(userEntity.getId(), userEntity.getRealName()));
                                    //转成json存入redis
                                    String userString = JsonUtil.getObjectToString(userMap);
                                    redisUtil.insert(visualDevId + CacheKeyEnum.USER.getName(), userString, 60 * 5);
                                }
                                String userData = OnlinePublicUtils.getDataInMethod(userMap, dataMap.get(swapVModel),isMultiple);
                                dataMap.put(vModel, userData);
                                break;
                            //角色选择
                            case JnpfKeyConsts.ROLESELECT:
                                if (roleMap.size()==0){
                                    List<RoleEntity> roleEntityList = roleService.getSwaptListByIds(roleList);
                                    roleEntityList.stream().forEach(Entity -> roleMap.put(Entity.getId(), Entity.getFullName()));
                                    //转成json存入redis
                                    String roleString = JsonUtil.getObjectToString(roleMap);
                                    redisUtil.insert(visualDevId + CacheKeyEnum.ROLE.getName(), roleString, 60 * 5);
                                }
                                String roleData = OnlinePublicUtils.getDataInMethod(roleMap, dataMap.get(vModel),isMultiple);
                                dataMap.put(vModel, roleData);
                                break;

                            case JnpfKeyConsts.GROUPSELECT:
                                if (groupMap.size() == 0) {
                                    List<GroupEntity> groupEntityList = groupService.list();
                                    groupEntityList.stream().forEach(groupEntity -> groupMap.put(groupEntity.getId(), groupEntity.getFullName()));
                                    //转成json存入redis
                                    String groupString = JsonUtil.getObjectToString(groupMap);
                                    redisUtil.insert(visualDevId + CacheKeyEnum.GROUP.getName(), groupString, 60 * 5);
                                }
                                String groupData = OnlinePublicUtils.getDataInMethod(userMap, dataMap.get(vModel),isMultiple);
                                dataMap.put(vModel, groupData);
                                break;

                            //省市区联动
                            case JnpfKeyConsts.ADDRESS:
                                String addressValue = String.valueOf(dataMap.get(swapVModel));
                                if (OnlinePublicUtils.getMultiple(addressValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
                                    String[][] data = JsonUtil.getJsonToBean(addressValue, String[][].class);
                                    List<String> addList = new ArrayList<>();
                                    for (String[] AddressData : data) {
                                        List<String> adList = new ArrayList<>();
                                        for (String s : AddressData) {
                                            adList.add(String.valueOf(proMap.get(s)));
                                        }
                                        addList.add(String.join("/", adList));
                                    }
                                    dataMap.put(vModel, String.join(";", addList));
                                } else if (OnlinePublicUtils.getMultiple(addressValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                                    List<String> proDataS = JsonUtil.getJsonToList(String.valueOf(dataMap.get(swapVModel)), String.class);
                                    proDataS = proDataS.stream().map(pro -> String.valueOf(proMap.get(pro))).collect(Collectors.toList());
                                    dataMap.put(vModel, String.join("/", proDataS));
                                }
                                break;
                            //开关
                            case JnpfKeyConsts.SWITCH:
                                String switchValue = String.valueOf(dataMap.get(vModel)).equals("1") ? swapDataVo.getActiveTxt() : swapDataVo.getInactiveTxt();
                                dataMap.put(vModel, switchValue);
                                break;
                            //级联
                            case JnpfKeyConsts.CASCADER:
                                String redisKey;
                                if (OnlineDataTypeEnum.STATIC.getType().equals(dataType)) {
                                    redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                                } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                                    redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl());
                                } else {
                                    redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                                }
                                Map<String, Object> cascaderMap;
                                if(dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())){
                                    List<Map<String,Object>> checkBoxList = (List<Map<String,Object>>) localCache.get(redisKey);
                                    cascaderMap= OnlinePublicUtils.getDataMap(checkBoxList,swapDataVo);
                                }else{
                                    cascaderMap=(Map<String, Object>) localCache.get(redisKey);
                                }
                                String s1 = OnlinePublicUtils.getDataInMethod(cascaderMap, dataMap.get(swapVModel),isMultiple);
                                dataMap.put(vModel, s1);
                                break;
                            case JnpfKeyConsts.CHECKBOX:
                                String checkBox;
                                if (OnlineDataTypeEnum.STATIC.getType().equals(dataType)) {
                                    checkBox = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                                } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                                    checkBox = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl());
                                } else {
                                    checkBox = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                                }
                                Map<String, Object> checkboxMap;
                                if(dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())){
                                    List<Map<String,Object>> checkBoxList = (List<Map<String,Object>>) localCache.get(checkBox);
                                    checkboxMap= OnlinePublicUtils.getDataMap(checkBoxList,swapDataVo);
                                }else{
                                    checkboxMap=(Map<String, Object>) localCache.get(checkBox);
                                }
                                String s2 = OnlinePublicUtils.getDataInMethod(checkboxMap, dataMap.get(swapVModel),isMultiple);
                                dataMap.put(vModel, s2);
                                break;
                            case JnpfKeyConsts.RELATIONFORM:
                                //取关联表单数据 按绑定功能加字段区分数据
                                redisKey = String.format("%s-%s-%s-%s-%s", dsName, JnpfKeyConsts.RELATIONFORM, swapDataVo.getModelId(), swapDataVo.getRelationField(), dataMap.get(vModel));
                                VisualdevModelDataInfoVO infoVO = null;
                                if (localCache.containsKey(redisKey) || redisUtil.exists(redisKey)) {
                                    if (localCache.containsKey(redisKey)) {
                                        infoVO = (VisualdevModelDataInfoVO) localCache.get(redisKey);
                                    } else {
                                        infoVO = JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), VisualdevModelDataInfoVO.class);
                                        localCache.put(redisKey, infoVO);
                                    }
                                } else {
                                    VisualdevEntity entity = visualdevService.getInfo(swapDataVo.getModelId());
                                    String keyId = String.valueOf(dataMap.get(swapVModel));
                                    if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
                                        infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity);
                                    } else {
                                        infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
                                    }
                                    redisUtil.insert(redisKey, infoVO, 60 * 5);
                                    localCache.put(redisKey, infoVO);
                                }
                                if (infoVO != null) {
                                    Map<String, Object> formDataMap = JsonUtil.stringToMap(infoVO.getData());
                                    String relationField = swapDataVo.getRelationField();
                                    if (formDataMap.size() > 0) {
                                        dataMap.put(vModel + "_id", dataMap.get(swapVModel));
                                        dataMap.put(vModel, formDataMap.get(relationField));
                                    }
                                }
                                break;
                            case JnpfKeyConsts.POPUPSELECT:
                            case JnpfKeyConsts.POPUPTABLESELECT:
                                redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getInterfaceId());
                                ActionResult data;
                                if (localCache.containsKey(redisKey) || redisUtil.exists(redisKey)) {
                                    if (localCache.containsKey(redisKey)) {
                                        data = (ActionResult) localCache.get(redisKey);
                                    } else {
                                        data = JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), ActionResult.class);
                                        localCache.put(redisKey, data);
                                    }
                                } else {
                                    data = dataInterfaceService.infoToId(swapDataVo.getInterfaceId(),null,null);
                                    redisUtil.insert(redisKey, JSONObject.toJSONString(data));
                                    localCache.put(redisKey, data);
                                }

                                if (data != null && data.getData() != null) {
                                    List<Map<String, Object>> mapList = new ArrayList<>();
                                    if (data.getData() instanceof DataInterfaceActionVo) {
                                        DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data.getData();
                                        if (actionVo.getData() instanceof List) {
                                            mapList = (List<Map<String, Object>>) actionVo.getData();
                                        }
                                    } else if (data.getData() instanceof List) {
                                        mapList = (List<Map<String, Object>>) data.getData();
                                    }else if (data.getData() instanceof JSONObject) {
                                        Map<String, Object> map = JsonUtil.entityToMap(data.getData());
                                        mapList = JsonUtil.getJsonToListMap(String.valueOf(map.get("data")));
                                    }
                                    String value = String.valueOf(dataMap.get(vModel));
                                    List<String> idList = new ArrayList<>();
                                    if (value.contains("[")){
                                        idList = JsonUtil.getJsonToList(value,String.class);
                                    } else {
                                        idList.add(value);
                                    }
                                    List<String> swapValue = new ArrayList<>();
                                    for (String id : idList){
                                        mapList.stream().filter(map ->
                                                map.get(swapDataVo.getPropsValue()).equals(id)
                                        ).forEach(
                                                modelMap -> swapValue.add(String.valueOf(modelMap.get(swapDataVo.getRelationField())))
                                        );
                                    }
                                    dataMap.put(vModel,swapValue.stream().collect(Collectors.joining(",")));
                                }
                                break;
                            case JnpfKeyConsts.MODIFYTIME:
                            case JnpfKeyConsts.CREATETIME:
                            case JnpfKeyConsts.DATE:
                                //判断是否为时间戳格式
                                String format;
                                String dateData = String.valueOf(dataMap.get(swapVModel));
                                String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
                                if (!dateData.contains("-") && !dateData.contains(":") && dateData.length() > 10) {
                                    DateTimeFormatter ftf = DateTimeFormatter.ofPattern(dateSwapInfo);
                                    format = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) dataMap.get(swapVModel)), ZoneId.of("+8")));
                                } else {
                                    format = dateData;
                                }
                                if (format.contains(".")) {
                                    format = format.substring(0, format.lastIndexOf("."));
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
                                Date date = sdf.parse(format);
                                String outTime = sdf.format(sdf.parse(DateUtil.dateFormat(date)));
                                dataMap.put(vModel, outTime);
                                break;

                            case JnpfKeyConsts.CHILD_TABLE:
                                List<FieLdsModel> children = swapDataVo.getConfig().getChildren();
                                List<Map<String,Object>> mapList = (List<Map<String,Object>>)dataMap.get(swapDataVo.getVModel());
                                List<OnlineDevListDataVO> childList =new ArrayList<>();
                                for (Map<String,Object> map : mapList){
                                    OnlineDevListDataVO vo = new OnlineDevListDataVO();
                                    vo.setData(map);
                                    childList.add(vo);
                                }
                                List<OnlineDevListDataVO> swapList = getSwapList(childList, children,visualDevId, inlineEdit);
                                mapList= swapList.stream().map(m -> m.getData()).collect(Collectors.toList());
                                dataMap.put(swapDataVo.getVModel(),mapList);
                                break;
                            default:
                                dataMap.put(vModel, dataMap.get(swapVModel));
                                break;
                        }
                        //转换数据接口的数据
                        if (dataType != null) {
                            if (!jnpfKey.equals(JnpfKeyConsts.CASCADER) && !jnpfKey.equals(JnpfKeyConsts.CHECKBOX)) {
                                //静态数据
                                if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                                    String redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                                    Map<String, Object> staticMap = (Map<String, Object>) localCache.get(redisKey);
                                    String s = OnlinePublicUtils.getDataInMethod(staticMap, dataMap.get(swapVModel),isMultiple);
                                    dataMap.put(vModel, s);
                                    //远端数据
                                } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                                    //非级联属性把上一步缓存的接口数据Key的child字段删除
                                    String redisKey = String.format("%s-%s-%s-%s-%s-", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), swapDataVo.getConfig().getProps().getLabel(), swapDataVo.getConfig().getProps().getValue());
                                    if (jnpfKey.equals(JnpfKeyConsts.TREESELECT)) {
                                        //树形缓存Key
                                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl());
                                    }
                                    Map<String, Object> dynamicMap = (Map<String, Object>) localCache.get(redisKey);
                                    String s = OnlinePublicUtils.getDataInMethod(dynamicMap, dataMap.get(swapVModel),isMultiple);
                                    dataMap.put(vModel, s);
                                    //数据字典
                                } else if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                                    String redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                                    Object dicObj = localCache.get(redisKey);
                                    List<Map<String, Object>> dictionaryList;
                                    if (dicObj instanceof String) {
                                        dictionaryList = JsonUtil.getJsonToListMap(String.valueOf(localCache.get(redisKey)));
                                    } else {
                                        dictionaryList = (List<Map<String, Object>>) dicObj;
                                    }
                                    Map<String, Object> dataInterfaceMap = OnlinePublicUtils.getDataMap(dictionaryList, swapDataVo);
                                    String s = OnlinePublicUtils.getDataInMethod(dataInterfaceMap, dataMap.get(swapVModel),isMultiple);
                                    dataMap.put(vModel, s);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    log.error("在线开发转换数据错误:" + e.getMessage());
                }
            }
        }
        if (inlineEdit){
            for (OnlineDevListDataVO listVo : list) {
                OnlineDevInfoUtils.getInitLineData(swapDataVoList, listVo.getData());
            }
        }
        return list;
    }

    /**
     * 取出列表所用到的 用户 组织 岗位的id
     *
     * @param list           数据
     * @param swapDataVoList 控件
     */
    public static void pageIdList(List<OnlineDevListDataVO> list, List<FieLdsModel> swapDataVoList, Map<String, Object> localCache) {
        Set<String> userList = (Set<String>) localCache.get("__user_list");
        Set<String> orgList = (Set<String>) localCache.get("__org_list");
        Set<String> posList = (Set<String>) localCache.get("__pos_list");
        Set<String> AllOrgList = (Set<String>) localCache.get("__allOrg_list");
        Set<String> roleList = (Set<String>)localCache.get("__role_list");
        for (FieLdsModel swapDataVo : swapDataVoList) {
            String jnpfKey = swapDataVo.getConfig().getJnpfKey();
            String vModel = swapDataVo.getVModel();
            for (OnlineDevListDataVO listVo : list) {
                Map<String, Object> dataMap = listVo.getData();
                if (StringUtil.isEmpty(String.valueOf(dataMap.get(vModel))) || dataMap.get(vModel) == null) {
                    continue;
                }
                if (String.valueOf(dataMap.get(vModel)).equals("[]") || String.valueOf(dataMap.get(vModel)).equals("null")) {
                    continue;
                } else {
                    switch (jnpfKey) {
                        //公司组件
                        case JnpfKeyConsts.COMSELECT:
                            //部门组件
                        case JnpfKeyConsts.DEPSELECT:
                            //所属部门
                        case JnpfKeyConsts.CURRDEPT:
                            //所属公司
                        case JnpfKeyConsts.CURRORGANIZE:
                            if ("all".equals(swapDataVo.getShowLevel())) {
                                getIdInMethod(AllOrgList, dataMap.get(vModel));
                            } else {
                                getIdInMethod(orgList, dataMap.get(vModel));
                            }
                            break;
                        //角色
                        case JnpfKeyConsts.ROLESELECT:
                            getIdInMethod(roleList,dataMap.get(vModel));
                            break;
                        //岗位组件
                        case JnpfKeyConsts.POSSELECT:
                            //所属岗位
                        case JnpfKeyConsts.CURRPOSITION:
                            getIdInMethod(posList, dataMap.get(vModel));
                            break;

                        //用户组件
                        case JnpfKeyConsts.USERSELECT:
                            //创建用户
                        case JnpfKeyConsts.CREATEUSER:
                            //修改用户
                        case JnpfKeyConsts.MODIFYUSER:
                            getIdInMethod(userList, dataMap.get(vModel));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 存取对应id集合
     *
     * @param idList
     * @param modelData
     * @return
     */
    public static Collection<String> getIdInMethod(Collection<String> idList, Object modelData) {
        if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
            String[][] data = JsonUtil.getJsonToBean(String.valueOf(modelData), String[][].class);
            for (String[] AddressData : data) {
                for (String s : AddressData) {
                    idList.add(s);
                }
            }
        } else if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
            idList.addAll(JsonUtil.getJsonToList(String.valueOf(modelData), String.class));
        } else {
            String[] modelDatas = String.valueOf(modelData).split(",");
            for (int i = 0; i < modelDatas.length; i++) {
                idList.add(modelDatas[i]);
            }
        }
        return idList;
    }

    /**
     * 保存需要转换的数据到redis(系统控件)
     *
     * @param swapDataVoList
     */
    public static void sysNeedSwapData(List<FieLdsModel> swapDataVoList, String visualDevId, Map<String, Object> localCache) {
        init();

        //公共数据
        String dsName = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");

        String redisKey;
        try {
            for (FieLdsModel swapDataVo : swapDataVoList) {
                String jnpfKey = swapDataVo.getConfig().getJnpfKey();
                String dataType = swapDataVo.getConfig().getDataType();
                switch (jnpfKey) {
                    //省市区联动
                    case JnpfKeyConsts.ADDRESS:
                        redisKey = "";
//                        if (!redisUtil.exists("")) {
//                            List<ProvinceEntity> provinceEntityList =new ArrayList<>();
//                            Map<String, String> provinceMap = new HashMap<>(16);
//                            provinceEntityList.stream().forEach(p -> provinceMap.put(p.getId(), p.getFullName()));
//                            redisUtil.insert(redisKey, provinceMap, RedisUtil.CAHCEWEEK);
//                        }
                        if (!localCache.containsKey(redisKey)) {
                            localCache.put(redisKey, redisUtil.getMap(redisKey));
                        }
                        break;
                    default:
                        break;
                }
                if (dataType != null) {
                    //数据接口的数据存放
                    String label;
                    String value;
                    String children = "";
                    List<Map<String, Object>> options = new ArrayList<>();
                    if (swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.CASCADER) || swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.TREESELECT)) {
                        label = swapDataVo.getProps().getProps().getLabel();
                        value = swapDataVo.getProps().getProps().getValue();
                        children = swapDataVo.getProps().getProps().getChildren();
                    } else {
                        label = swapDataVo.getConfig().getProps().getLabel();
                        value = swapDataVo.getConfig().getProps().getValue();
                    }

                    Map<String, String> dataInterfaceMap = new HashMap<>(16);

                    //静态数据
                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                        if (!redisUtil.exists(redisKey)) {
                            if (swapDataVo.getOptions() != null) {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getOptions());
                                String Children = swapDataVo.getProps().getProps().getChildren();
                                JSONArray data = JsonUtil.getListToJsonArray(options);
                                getOptions(label, value, Children, data, options);
                            } else {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getSlot().getOptions());
                            }
                            options.stream().forEach(o -> {
                                dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
                            });
                            String staticData = JsonUtil.getObjectToString(dataInterfaceMap);
                            redisUtil.insert(redisKey, staticData, 60 * 5);
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, dataInterfaceMap);
                            }
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                String staticDataString = redisUtil.getString(redisKey).toString();
                                localCache.put(redisKey, JsonUtil.stringToMap(staticDataString));
                            }
                        }
                    }
                    //远端数据
                    if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl());
                        String redisKey2 = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), label, value, children);
                        if (!redisUtil.exists(redisKey2)) {
                            ActionResult data = null;
                            if (!redisUtil.exists(redisKey)) {
                                data = dataInterfaceService.infoToId(swapDataVo.getConfig().getPropsUrl(),null,null);
                                //缓存接口全部数据
                                redisUtil.insert(redisKey, JSONObject.toJSONString(data), 60 * 5);
                            } else {
                                data = JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), ActionResult.class);
                            }
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, data);
                            }
                            if (data != null && data.getData() != null) {
                                List<Map<String, Object>> dataList = new ArrayList<>();
                                if (data.getData() instanceof DataInterfaceActionVo) {
                                    DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data.getData();
                                    if (actionVo.getData() instanceof List) {
                                        dataList = (List<Map<String, Object>>) actionVo.getData();
                                    }
                                } else if (data.getData() instanceof List) {
                                    dataList = (List<Map<String, Object>>) data.getData();
                                }
                                JSONArray dataAll = JsonUtil.getListToJsonArray(dataList);
                                treeToList(label, value, children, dataAll, options);
                                options.stream().forEach(o -> {
                                    dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
                                });

                                //缓存接口根据特定字段转换后的全部数据
                                String dynamicData = JsonUtil.getObjectToString(dataInterfaceMap);
                                redisUtil.insert(redisKey2, dynamicData, 60 * 5);
                                localCache.put(redisKey2, dataInterfaceMap);
                            }
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, JSONObject.parseObject(String.valueOf(redisUtil.getString(redisKey)), ActionResult.class));
                            }
                            if (!localCache.containsKey(redisKey2)) {
                                //转成map格式
                                String dynamicString = redisUtil.getString(redisKey2).toString();
                                localCache.put(redisKey2, JsonUtil.stringToMap(dynamicString));
                            }
                        }
                    }
                    //数据字典
                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                        if (!redisUtil.exists(redisKey)) {
                            List<DictionaryDataEntity> list = dictionaryDataService.getDicList(swapDataVo.getConfig().getDictionaryType());
                            options = list.stream().map(dic -> {
                                Map<String, Object> dictionaryMap = new HashMap<>(16);
                                dictionaryMap.put("id", dic.getId());
                                dictionaryMap.put("enCode", dic.getEnCode());
                                dictionaryMap.put("fullName", dic.getFullName());
                                return dictionaryMap;
                            }).collect(Collectors.toList());

                            String dictionaryData = JsonUtil.getObjectToString(options);
                            redisUtil.insert(redisKey, dictionaryData, 60 * 5);
                            localCache.put(redisKey, options);
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                String dictionaryStringData = redisUtil.getString(redisKey).toString();
                                localCache.put(redisKey, JsonUtil.getJsonToListMap(dictionaryStringData));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("在线开发转换数据异常:" + e.getMessage());
            e.printStackTrace();
        }


    }

    /**
     * 递归查询
     *
     * @param label
     * @param value
     * @param Children
     * @param data
     * @param options
     */
    public static void getOptions(String label, String value, String Children, JSONArray data, List<Map<String, Object>> options) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, Object> tree = new HashMap<>(16);
            tree.put(value, String.valueOf(ob.get(value)));
            tree.put(label, String.valueOf(ob.get(label)));
            options.add(tree);
            if (ob.get(Children) != null) {
                JSONArray childrenArray = ob.getJSONArray(Children);
                getOptions(label, value, Children, childrenArray, options);
            }
        }
    }

    /**
     * 分组页面
     *
     * @param realList
     * @param columnDataModel
     * @return
     */
    public static List<Map<String, Object>> groupData(List<Map<String, Object>> realList, ColumnDataModel columnDataModel) {
        List<Map<String, Object>> columnList = JsonUtil.getJsonToListMap(columnDataModel.getColumnList());
        String firstField;
        String groupField = columnDataModel.getGroupField();
        Map<String, Object> map = columnList.stream().filter(t -> !String.valueOf(t.get("prop")).equals(columnDataModel.getGroupField())).findFirst().orElse(null);
        if (map == null) {
            map = columnList.stream().filter(t -> String.valueOf(t.get("prop")).equals(columnDataModel.getGroupField())).findFirst().orElse(null);
        }
        firstField = String.valueOf(map.get("prop"));

        Map<String, List<Map<String, Object>>> twoMap = new LinkedHashMap<>();

        for (Map<String, Object> realMap : realList) {
            String value = String.valueOf(realMap.get(groupField));
            boolean isKey = twoMap.get(value) != null;
            if (isKey) {
                List<Map<String, Object>> maps = twoMap.get(value);
                maps.add(realMap);
                twoMap.put(value, maps);
            } else {
                List<Map<String, Object>> childrenList = new ArrayList<>();
                childrenList.add(realMap);
                twoMap.put(value, childrenList);
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String key : twoMap.keySet()) {
            Map<String, Object> thirdMap = new HashMap<>(16);
            thirdMap.put(firstField, !key.equals("null") ? key : "");
            thirdMap.put("top", true);
            thirdMap.put("id", RandomUtil.uuId());
            thirdMap.put("children", twoMap.get(key));
            resultList.add(thirdMap);
        }
        return resultList;
    }

    /**
     * 树形列表页面
     *
     * @param realList
     * @param columnDataModel
     * @return
     */
    public static List<Map<String, Object>> treeListData(List<Map<String, Object>> realList, ColumnDataModel columnDataModel) {
        String parentField = columnDataModel.getParentField() + "_id";
        String childField = columnDataModel.getSubField();
        for (int i = 0; i < realList.size(); i++) {
            Map<String, Object> item = realList.get(i);
            if ((item.get(parentField) != null && !StringUtil.isNotEmpty(item.get(parentField).toString())) || (item.get(parentField) != null && !"[]".equals(item.get(parentField).toString()))) {
                if (addChild(item, realList, parentField, childField) && realList.size() > 0) {
                    realList.remove(item);
                    i--;
                }
            }
        }
        return realList;
    }

    //递归
    private static boolean addChild(Map<String, Object> node, List<Map<String, Object>> list, String parentField, String childField) {

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> ele = list.get(i);
            if (ele.get(childField).equals(node.get(parentField))) {
                if (ele.get("children") == null) {
                    ele.put("children", new ArrayList<>());
                }
                List<Map<String, Object>> children = (List<Map<String, Object>>) ele.get("children");
                children.add(node);
                ele.put("children", children);
                return true;
            }
            if (ele.get("children") != null) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) ele.get("children");
                if (addChild(node, children, parentField, childField)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 级联递归
     *
     * @param value
     * @param label
     * @param children
     * @param data
     * @param result
     */
    private static void treeToList(String value, String label, String children, JSONArray data, List<Map<String, Object>> result) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, Object> tree = new HashMap<>(16);
            tree.put(value, String.valueOf(ob.get(value)));
            tree.put(label, String.valueOf(ob.get(label)));
            result.add(tree);
            if (ob.get(children) != null) {
                JSONArray childArray = ob.getJSONArray(children);
                treeToList(value, label, children, childArray, result);
            }
        }
    }

}

