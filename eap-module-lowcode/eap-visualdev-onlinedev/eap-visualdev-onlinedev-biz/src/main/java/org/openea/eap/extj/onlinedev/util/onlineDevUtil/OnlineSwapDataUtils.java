package org.openea.eap.extj.onlinedev.util.onlineDevUtil;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceModel;
import org.openea.eap.extj.base.model.dataInterface.DataInterfacePage;
import org.openea.eap.extj.base.service.DataInterfaceService;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowJsonModel;
import org.openea.eap.extj.engine.service.FlowTemplateService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.extend.entity.ProvinceEntity;
import org.openea.eap.extj.extend.service.BillRuleService;
import org.openea.eap.extj.extend.service.ProvinceService;
import org.openea.eap.extj.form.mapper.FlowFormDataMapper;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.OnlineDevData;
import org.openea.eap.extj.model.visualJson.UploaderTemplateModel;
import org.openea.eap.extj.model.visualJson.analysis.FormModel;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.config.RegListModel;
import org.openea.eap.extj.model.visualJson.TemplateJsonModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.CacheKeyEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.extj.onlinedev.model.OnlineImport.ExcelImportModel;
import org.openea.eap.extj.onlinedev.model.OnlineImport.ImportFormCheckUniqueModel;
import org.openea.eap.extj.onlinedev.model.OnlineImport.OnlineCusCheckModel;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;
import org.openea.eap.extj.onlinedev.service.VisualDevInfoService;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.permission.constant.PermissionConst;
import org.openea.eap.extj.permission.entity.OrganizeRelationEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.permission.service.*;
import org.openea.eap.extj.permission.util.PermissionUtil;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.form.util.FlowFormConstant;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.openea.eap.extj.form.util.FormPublicUtils;
import org.openea.eap.extj.form.util.TableFeildsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OnlineSwapDataUtils {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private VisualDevInfoService visualDevInfoService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private FlowFormDataMapper flowFormDataMapper;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private OrganizeRelationService organizeRelationService;
    @Autowired
    private FlowTemplateService flowTemplateService;

    public List<Map<String, Object>> getSwapList(List<Map<String, Object>> list, List<FieLdsModel> swapDataVoList, String visualDevId, Boolean inlineEdit, List<FormModel> codeList) {
        if (list.isEmpty()) {
            return list;
        }
        return getSwapList(list, swapDataVoList, visualDevId, inlineEdit, codeList, null,true);
    }

    public List<Map<String, Object>> getSwapInfo(List<Map<String, Object>> list, List<FieLdsModel> swapDataVoList, String visualDevId, Boolean inlineEdit, List<FormModel> codeList) {
        if (list.isEmpty()) {
            return list;
        }
        return getSwapList(list, swapDataVoList, visualDevId, inlineEdit, codeList, null,false);
    }

    public List<Map<String, Object>> getSwapList(List<Map<String, Object>> list, List<FieLdsModel> swapDataVoList, String visualDevId, Boolean inlineEdit,
                                                 List<FormModel> codeList, Map<String, Object> localCacheParent, boolean isList) {
        try{
            DynamicDataSourceUtil.switchToDataSource(null);
            if (list.isEmpty()) {
                return list;
            }
            //主表的缓存数据继续使用, 不重新初始化
            Map<String, Object> localCache = Optional.ofNullable(localCacheParent).orElse(new HashMap<>());
            //初始化系统缓存
            sysNeedSwapData(swapDataVoList, visualDevId, localCache);
            //redis key
            String dsName = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
            writeRedisAndList(localCache, swapDataVoList, dsName, visualDevId, inlineEdit, list, codeList, isList);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        return list;
    }

    private List<Map<String, Object>> writeRedisAndList(Map<String, Object> localCache, List<FieLdsModel> swapDataVoList, String dsName, String visualDevId, Boolean inlineEdit,
                                                        List<Map<String, Object>> list, List<FormModel> codeList, boolean isList) {

        Map<String, Object> userMap = (Map<String, Object>) localCache.get("__user_map");
        Map<String, Object> orgMap = (Map<String, Object>) localCache.get("__org_map");
        Map<String, Object> posMap = (Map<String, Object>) localCache.get("__pos_map");
        //Map<String, Object> allOrgMap = (Map<String, Object>) localCache.get("__allOrg_map");
        Map<String, Object> roleMap = (Map<String, Object>) localCache.get("__role_map");
        Map<String, Object> groupMap = (Map<String, Object>) localCache.get("__group_map");

        List<Map<String, String>> proMapList = (List<Map<String, String>>) localCache.get("__pro_maplist");
        for (int x = 0; x < list.size(); x++) {
            Map<String, Object> dataMap = list.get(x);
            if (dataMap == null) {
                dataMap = new HashMap<>();
                list.set(x, dataMap);
            }
            Map<String, Object> dataCopyMap = new HashMap<>(dataMap);
            for (FieLdsModel swapDataVo : swapDataVoList) {
                String jnpfKey = swapDataVo.getConfig().getJnpfKey();
                String swapVModel = swapDataVo.getVModel();
                String vModel = inlineEdit ? swapDataVo.getVModel() + "_name" : swapDataVo.getVModel();
                String dataType = swapDataVo.getConfig().getDataType();
                Boolean isMultiple = Objects.nonNull(swapDataVo.getMultiple()) ? swapDataVo.getMultiple() : false;

                try {
                    Map<String, Map<String, Object>> dataDetailMap = new HashMap<>();
                    if (StringUtil.isEmpty(String.valueOf(dataMap.get(swapVModel))) || String.valueOf(dataMap.get(swapVModel)).equals("[]")
                            || String.valueOf(dataMap.get(swapVModel)).equals("null")) {
                        if (jnpfKey.equals(JnpfKeyConsts.CHILD_TABLE)) {
                            dataMap.put(vModel, new ArrayList<>());
                        } else {
//							dataCopyMap.putAll(dataMap);
                            if (inlineEdit) {
                                dataMap.put(swapVModel, null);
                            }
                            dataMap.put(vModel, null);
                        }
                        continue;
                    } else {
                        //是否联动
                        boolean DynamicNeedCache;
                        String redisKey;
                        switch (jnpfKey) {
                            case JnpfKeyConsts.CALCULATE:
                            case JnpfKeyConsts.NUM_INPUT:
                                Object decimalValue = dataCopyMap.get(swapDataVo.getVModel());
                                if (decimalValue instanceof BigDecimal) {
                                    BigDecimal bd = (BigDecimal) decimalValue;
                                    dataMap.put(vModel, bd.toPlainString());
                                }
                                break;
                            //公司组件
                            case JnpfKeyConsts.COMSELECT:
                                //部门组件
                            case JnpfKeyConsts.DEPSELECT:
                                //所属部门
                            case JnpfKeyConsts.CURRDEPT:
                                //所属组织
                            case JnpfKeyConsts.CURRORGANIZE:
                                if (orgMap.size() == 0) {
                                    orgMap = organizeService.getOrgMap();
                                    redisUtil.insert(dsName + CacheKeyEnum.ORG.getName(), orgMap, 60 * 5);
                                }
                                if ("all".equals(swapDataVo.getShowLevel())) {
                                    //多级组织
                                    String infoByOrgId = PermissionUtil.getLinkInfoByOrgId(String.valueOf(dataMap.get(swapVModel)),
                                            organizeService, false);
                                    dataMap.put(vModel, infoByOrgId);
                                } else {
                                    dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(orgMap, dataMap.get(swapVModel), isMultiple));
                                }
                                break;

                            //岗位组件
                            case JnpfKeyConsts.POSSELECT:
                                //所属岗位
                            case JnpfKeyConsts.CURRPOSITION:
                                if (posMap.size() == 0) {
                                    posMap = positionService.getPosMap();
                                    redisUtil.insert(dsName + CacheKeyEnum.POS.getName(), posMap, 60 * 5);
                                }
                                String posData = OnlinePublicUtils.getDataInMethod(posMap, dataMap.get(swapVModel), isMultiple);
                                if (ObjectUtil.isEmpty(dataMap.get(swapVModel))) {
                                    dataMap.put(vModel, " ");
                                } else {
                                    dataMap.put(vModel, posData);
                                }
                                break;

                            //用户组件
                            case JnpfKeyConsts.USERSELECT:
                                //创建用户
                            case JnpfKeyConsts.CREATEUSER:
                                //修改用户
                            case JnpfKeyConsts.MODIFYUSER:
                                if (userMap.size() == 0) {
                                    userMap = userService.getUserMap();
                                    redisUtil.insert(dsName + CacheKeyEnum.USER.getName(), userMap, 60 * 5);
                                }
                                String userData = OnlinePublicUtils.getDataInMethod(userMap, dataMap.get(swapVModel), isMultiple);
                                dataMap.put(vModel, userData);
                                break;
                            case JnpfKeyConsts.CUSTOMUSERSELECT:
                                List<String> dataNoSwapInMethod = OnlinePublicUtils.getDataNoSwapInMethod(dataMap.get(swapVModel));
                                StringJoiner valueJoin = new StringJoiner(",");
                                for (String data : dataNoSwapInMethod) {
                                    String id = data.contains("--") ? data.substring(0, data.lastIndexOf("--")) : data;
                                    String type = data.contains("--") ? data.substring(data.lastIndexOf("--") + 2) : "";
                                    Map<String, Object> cacheMap;
                                    switch (type) {
                                        case "role":
                                            if (roleMap.size() == 0) {
                                                roleMap = roleService.getRoleMap();
                                                redisUtil.insert(dsName + CacheKeyEnum.ROLE.getName(), roleMap, 60 * 5);
                                            }
                                            cacheMap = roleMap;
                                            break;
                                        case "position":
                                            if (posMap.size() == 0) {
                                                posMap = positionService.getPosMap();
                                                redisUtil.insert(dsName + CacheKeyEnum.POS.getName(), posMap, 60 * 5);
                                            }
                                            cacheMap = posMap;
                                            break;
                                        case "company":
                                        case "department":
                                            if (orgMap.size() == 0) {
                                                orgMap = organizeService.getOrgMap();
                                                redisUtil.insert(dsName + CacheKeyEnum.ORG.getName(), orgMap, 60 * 5);
                                            }
                                            cacheMap = orgMap;
                                            break;
                                        case "group":
                                            if (groupMap.size() == 0) {
                                                groupMap = groupService.getGroupMap();
                                                redisUtil.insert(dsName + CacheKeyEnum.GROUP.getName(), groupMap, 60 * 5);
                                            }
                                            cacheMap = groupMap;
                                            break;
                                        case "user":
                                        default:
                                            if (userMap.size() == 0) {
                                                userMap = userService.getUserMap();
                                                redisUtil.insert(dsName + CacheKeyEnum.USER.getName(), userMap, 60 * 5);
                                            }
                                            cacheMap = userMap;
                                            break;
                                    }
                                    valueJoin.add(Optional.ofNullable(cacheMap.get(id)).orElse("").toString());
                                }
                                dataMap.put(vModel, valueJoin.toString());
                                break;
                            //角色选择
                            case JnpfKeyConsts.ROLESELECT:
                                if (roleMap.size() == 0) {
                                    roleMap = roleService.getRoleMap();
                                    redisUtil.insert(dsName + CacheKeyEnum.ROLE.getName(), roleMap, 60 * 5);
                                }
                                String roleData = OnlinePublicUtils.getDataInMethod(roleMap, dataMap.get(swapVModel), isMultiple);
                                dataMap.put(vModel, roleData);
                                break;

                            case JnpfKeyConsts.GROUPSELECT:
                                if (groupMap.size() == 0) {
                                    groupMap = groupService.getGroupMap();
                                    redisUtil.insert(dsName + CacheKeyEnum.GROUP.getName(), groupMap, 60 * 5);
                                }
                                String groupData = OnlinePublicUtils.getDataInMethod(groupMap, dataMap.get(swapVModel), isMultiple);
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
                                        for (int i = 0; i < AddressData.length; i++) {
                                            String addressDatum = AddressData[i];
                                            String value = String.valueOf(proMapList.get(i).get(addressDatum));
                                            adList.add(value);
                                        }
                                        addList.add(String.join("/", adList));
                                    }
                                    dataMap.put(vModel, String.join(";", addList));
                                } else if (OnlinePublicUtils.getMultiple(addressValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
                                    List<String> proDataS = JsonUtil.getJsonToList(String.valueOf(dataMap.get(swapVModel)), String.class);
                                    List<String> adList = new ArrayList<>();
                                    for (int i = 0; i < proDataS.size(); i++) {
                                        String addressDatum = proDataS.get(i);
                                        String value = String.valueOf(proMapList.get(i).get(addressDatum));
                                        adList.add(value);
                                    }
                                    dataMap.put(vModel, String.join("/", adList));
                                }
                                break;
                            //开关
                            case JnpfKeyConsts.SWITCH:
                                String switchValue = String.valueOf(dataMap.get(swapVModel)).equals("1") ? swapDataVo.getActiveTxt() : swapDataVo.getInactiveTxt();
                                dataMap.put(vModel, switchValue);
                                break;

                            case JnpfKeyConsts.CASCADER:
                            case JnpfKeyConsts.RADIO:
                            case JnpfKeyConsts.CHECKBOX:
                            case JnpfKeyConsts.SELECT:
                            case JnpfKeyConsts.TREESELECT:
                                DynamicNeedCache = swapDataVo.getConfig().getTemplateJson().size() == 0;

                                String interfacelabel = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getLabel() : swapDataVo.getConfig().getProps().getLabel();
                                String interfaceValue = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getValue() : swapDataVo.getConfig().getProps().getValue();
                                String interfaceChildren = swapDataVo.getProps() != null ? Optional.ofNullable(swapDataVo.getProps().getProps().getChildren()).orElse("") : swapDataVo.getConfig().getProps().getChildren();
                                if (DynamicNeedCache) {
                                    if (OnlineDataTypeEnum.STATIC.getType().equals(dataType)) {
                                        redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                                    } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                                        redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), interfaceValue, interfacelabel, interfaceChildren);
                                    } else {
                                        redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                                    }
                                    Map<String, Object> cascaderMap;
                                    if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                                        List<Map<String, Object>> checkBoxList = (List<Map<String, Object>>) localCache.get(redisKey);
                                        cascaderMap = OnlinePublicUtils.getDataMap(checkBoxList, swapDataVo);
                                    } else {
                                        cascaderMap = (Map<String, Object>) localCache.get(redisKey);
                                    }
                                    dataMap.put(vModel, FormPublicUtils.getDataConversion(cascaderMap, dataMap.get(swapVModel)));
                                } else {
                                    List<TemplateJsonModel> templateJsonModels = JsonUtil.getJsonToList(swapDataVo.getConfig().getTemplateJson(), TemplateJsonModel.class);
                                    Map<String, String> paramMap = new HashMap<>();
                                    for (TemplateJsonModel templateJsonModel : templateJsonModels) {
                                        String relationField = templateJsonModel.getRelationField();
                                        if (StringUtil.isEmpty(relationField)) {
                                            continue;
                                        }
                                        String Field = templateJsonModel.getField();
                                        paramMap.put(Field, Optional.ofNullable(dataCopyMap.get(relationField)).orElse("").toString());
                                    }
                                    List<Map<String, Object>> options = new ArrayList<>();
                                    Map<String, Object> dataInterfaceMap = new HashMap();
                                    ActionResult data = dataInterfaceService.infoToId(swapDataVo.getConfig().getPropsUrl(), null, paramMap);
                                    if (data != null && data.getData() != null) {
                                        List<Map<String, Object>> dataList = new ArrayList<>();
                                        if (data.getData() instanceof List) {
                                            dataList = (List<Map<String, Object>>) data.getData();
                                        }
                                        JSONArray dataAll = JsonUtil.getListToJsonArray(dataList);
                                        treeToList(interfacelabel, interfaceValue, interfaceChildren, dataAll, options);
                                        options.stream().forEach(o -> {
                                            dataInterfaceMap.put(String.valueOf(o.get(interfaceValue)), String.valueOf(o.get(interfacelabel)));
                                        });
                                    }
                                    dataMap.put(vModel, FormPublicUtils.getDataConversion(dataInterfaceMap, dataMap.get(swapVModel)));
                                }
                                break;
                            case JnpfKeyConsts.RELATIONFORM:
                                //取关联表单数据 按绑定功能加字段区分数据
                                redisKey = String.format("%s-%s-%s-%s-%s", dsName, JnpfKeyConsts.RELATIONFORM, swapDataVo.getModelId(), swapDataVo.getRelationField(), dataMap.get(vModel));
                                VisualdevModelDataInfoVO infoVO = null;
                                VisualdevEntity entity = visualdevService.getInfo(swapDataVo.getModelId());
                                String keyId = String.valueOf(dataMap.get(swapVModel));
                                if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
                                    infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity);
                                } else {
                                    infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
                                }
                                if (infoVO != null) {
                                    Map<String, Object> formDataMap = JsonUtil.stringToMap(infoVO.getData());
                                    String relationField = swapDataVo.getRelationField();
                                    if (formDataMap != null && formDataMap.size() > 0) {
                                        dataMap.put(vModel + "_id", dataMap.get(swapVModel));
                                        dataMap.put(vModel, formDataMap.get(relationField));
                                        dataDetailMap.put(vModel, formDataMap);
                                    }
                                }
                                break;
                            case JnpfKeyConsts.POPUPSELECT:
                            case JnpfKeyConsts.POPUPTABLESELECT:
                                //是否联动
                                List<TemplateJsonModel> templateJsonModels = JsonUtil.getJsonToList(swapDataVo.getTemplateJson(), TemplateJsonModel.class);
                                DynamicNeedCache = templateJsonModels.size() == 0;
                                redisKey = String.format("%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getInterfaceId(), swapDataVo.getPropsValue(), swapDataVo.getRelationField());
                                List<Map<String, Object>> mapList = new ArrayList<>();
                                Map<String, Object> popMaps = new HashMap<>();
                                String value = String.valueOf(dataMap.get(swapVModel));
                                if (DynamicNeedCache){
                                    if (redisUtil.exists(redisKey)) {
                                        popMaps = redisUtil.getMap(redisKey);
                                    } else {
                                        dataInterfaceService.infoToId(swapDataVo.getInterfaceId(), null, null);
                                    }
                                }else{
                                    List<DataInterfaceModel> listParam=new ArrayList<>();
                                    for (TemplateJsonModel templateJsonModel : templateJsonModels) {
                                        String relationField = templateJsonModel.getRelationField();
                                        DataInterfaceModel dataInterfaceModel=JsonUtil.getJsonToBean(templateJsonModel,DataInterfaceModel.class);
                                        if (StringUtil.isEmpty(relationField)){
                                            continue;
                                        }
                                        dataInterfaceModel.setDefaultValue(String.valueOf(dataCopyMap.get(relationField)));
                                    }
                                    DataInterfacePage dataInterfacePage=new DataInterfacePage();
                                    dataInterfacePage.setParamList(listParam);
                                    dataInterfacePage.setInterfaceId(swapDataVo.getInterfaceId());
                                    List<String> ids = new ArrayList<>();
                                    if (value.startsWith("[")) {
                                        ids = JsonUtil.getJsonToList(value, String.class);
                                    } else {
                                        ids.add(value);
                                    }
                                    dataInterfacePage.setIds(ids);
                                    dataInterfacePage.setPropsValue(swapDataVo.getPropsValue());
                                    dataInterfacePage.setRelationField(swapDataVo.getRelationField());
                                    mapList= dataInterfaceService.infoToInfo(swapDataVo.getInterfaceId(), dataInterfacePage);
                                }
                                StringJoiner stringJoiner = new StringJoiner(",");
                                List<String> popList = new ArrayList<>();
                                if (value.startsWith("[")) {
                                    popList = JsonUtil.getJsonToList(value, String.class);
                                } else {
                                    popList.add(value);
                                }
                                for (String va : popList) {
                                    if (popMaps.size() > 0) {
                                        stringJoiner.add(String.valueOf(popMaps.get(va)));
                                    } else {
                                        Map<String, Object> PopMap = mapList.stream().filter(map -> map.get(swapDataVo.getPropsValue()).equals(va)).findFirst().orElse(new HashMap<>());
                                        if (PopMap.size() > 0) {
                                            dataMap.put(vModel + "_id", dataMap.get(swapVModel));
                                            stringJoiner.add(String.valueOf(PopMap.get(swapDataVo.getRelationField())));
                                            dataDetailMap.put(vModel, PopMap);
                                        }
                                    }
                                }
                                dataMap.put(vModel, String.valueOf(stringJoiner));
                                break;
                            case JnpfKeyConsts.MODIFYTIME:
                            case JnpfKeyConsts.CREATETIME:
                            case JnpfKeyConsts.TIME:
                            case JnpfKeyConsts.DATE:
                                //判断是否为时间戳格式
                                Object dateObj = dataMap.get(swapVModel);
                                LocalDateTime dateTime = null;
                                if (dateObj instanceof LocalDateTime) {
                                    dateTime = (LocalDateTime) dateObj;
                                } else if (dateObj instanceof Timestamp) {
                                    dateTime = ((Timestamp) dateObj).toLocalDateTime();
                                } else {
                                    dateTime = LocalDateTimeUtil.of(DateUtil.parse(dateObj.toString()));
                                }
                                String format = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
                                if (isList && (JnpfKeyConsts.MODIFYTIME.equals(jnpfKey) || JnpfKeyConsts.CREATETIME.equals(jnpfKey))) {
                                    format = "yyyy-MM-dd HH:mm";
                                }
                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                                String date = dateTimeFormatter.format(dateTime);
                                dataMap.put(vModel, date);
                                if (JnpfKeyConsts.MODIFYTIME.equals(jnpfKey) || JnpfKeyConsts.CREATETIME.equals(jnpfKey)) {
                                    dataMap.put(swapDataVo.getVModel(), date);
                                }
                                break;
                            case JnpfKeyConsts.RATE:
                            case JnpfKeyConsts.SLIDER:
                                dataMap.put(vModel, dataMap.get(vModel) != null ? Integer.parseInt(String.valueOf(dataMap.get(vModel))) : null);
                                break;

                            case JnpfKeyConsts.UPLOADFZ:
                            case JnpfKeyConsts.UPLOADIMG:
                                List<Map<String, Object>> fileList = JsonUtil.getJsonToListMap(String.valueOf(dataMap.get(vModel)));
                                dataMap.put(vModel, fileList);
                                break;
                            case JnpfKeyConsts.CHILD_TABLE:
                                List<FieLdsModel> childrens = swapDataVo.getConfig().getChildren();
                                List<Map<String, Object>> childList = (List<Map<String, Object>>) dataMap.get(swapDataVo.getVModel());
                                List<Map<String, Object>> swapList = getSwapList(childList, childrens, visualDevId, inlineEdit, codeList, localCache,true);
                                dataMap.put(swapDataVo.getVModel(), swapList);
                                break;
                            default:
                                dataMap.put(vModel, dataMap.get(swapVModel));
                                break;
                        }
                    }
                    //二维码 条形码最后处理
                    swapCodeDataInfo(codeList, dataMap, dataCopyMap);
                    //关联选择属性
                    if (dataDetailMap.size() > 0) {
                        getDataAttr(swapDataVoList, dataMap, dataDetailMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("在线开发转换数据错误:" + e.getMessage());
                }
            }
        }
        if (inlineEdit) {
            for (Map<String, Object> map : list) {
                //行内编辑过滤子表
                swapDataVoList = swapDataVoList.stream().filter(s -> !s.getVModel().contains("tableField")).collect(Collectors.toList());
                OnlineDevInfoUtils.getInitLineData(swapDataVoList, map);
            }
        }
        return list;
    }

    /**
     * 保存需要转换的数据到redis(系统控件)
     *
     * @param swapDataVoList
     * @param visualDevId
     * @param localCache
     */
    public void sysNeedSwapData(List<FieLdsModel> swapDataVoList, String visualDevId, Map<String, Object> localCache) {
        //公共数据
        String dsName = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");

        String redisKey;
        try {
            boolean needUser = false, needOrg = false, needPos = false, needRole = false, needGroup = false, needProvince = false;
            for (FieLdsModel swapDataVo : swapDataVoList) {
                String jnpfKey = swapDataVo.getConfig().getJnpfKey();
                String dataType = swapDataVo.getConfig().getDataType();
                switch (jnpfKey) {
                    case JnpfKeyConsts.CHILD_TABLE:
                        List<FieLdsModel> children = swapDataVo.getConfig().getChildren();
                        sysNeedSwapData(children, visualDevId, localCache);
                        break;
                    //用户组件
                    case JnpfKeyConsts.USERSELECT:
                        //创建用户
                    case JnpfKeyConsts.CREATEUSER:
                        //修改用户
                    case JnpfKeyConsts.MODIFYUSER:
                        needUser = true;
                        break;
                    //公司组件
                    case JnpfKeyConsts.COMSELECT:
                        //部门组件
                    case JnpfKeyConsts.DEPSELECT:
                        //所属部门
                    case JnpfKeyConsts.CURRDEPT:
                        //所属组织
                    case JnpfKeyConsts.CURRORGANIZE:
                        needOrg = true;
                        break;
                    //岗位组件
                    case JnpfKeyConsts.POSSELECT:
                        //所属岗位
                    case JnpfKeyConsts.CURRPOSITION:
                        needPos = true;
                        break;
                    //角色选择
                    case JnpfKeyConsts.ROLESELECT:
                        needRole = true;
                        break;
                    //分组选择
                    case JnpfKeyConsts.GROUPSELECT:
                        needGroup = true;
                        break;
                    //用户选择组件
                    case JnpfKeyConsts.CUSTOMUSERSELECT:
                        needUser = needOrg = needPos = needGroup = true;
                        break;
                    //省市区选择组件
                    case JnpfKeyConsts.ADDRESS:
                        needProvince = true;
                        break;
                    default:
                        break;
                }
                if (dataType != null) {
                    //数据接口的数据存放
                    String label = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getLabel() : swapDataVo.getConfig().getProps().getLabel();
                    String value = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getValue() : swapDataVo.getConfig().getProps().getValue();
                    String children = swapDataVo.getProps() != null ? Optional.ofNullable(swapDataVo.getProps().getProps().getChildren()).orElse("") : swapDataVo.getConfig().getProps().getChildren();
                    List<Map<String, Object>> options = new ArrayList<>();
                    if (swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.POPUPSELECT) || swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.POPUPTABLESELECT)) {
                        label = swapDataVo.getRelationField();
                        value = swapDataVo.getPropsValue();
                    }
                    Map<String, String> dataInterfaceMap = new HashMap<>(16);
                    String finalValue = value;
                    String finalLabel = label;
                    //静态数据
                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                        if (!redisUtil.exists(redisKey)) {
                            if (swapDataVo.getOptions() != null) {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getOptions());
                                JSONArray data = JsonUtil.getListToJsonArray(options);
                                getOptions(label, value, children, data, options);
                            } else {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getSlot().getOptions());
                            }

                            options.stream().forEach(o -> {
                                dataInterfaceMap.put(String.valueOf(o.get(finalValue)), String.valueOf(o.get(finalLabel)));
                            });
                            redisUtil.insert(redisKey, dataInterfaceMap, 60 * 5);
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, dataInterfaceMap);
                            }
                        } else {
                            if (!localCache.containsKey(redisKey)) {
                                localCache.put(redisKey, redisUtil.getMap(redisKey));
                            }
                        }
                    }
                    //远端数据
                    if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        //联动状态下不做缓存
                        boolean dynamicIsNeedCache = swapDataVo.getConfig().getTemplateJson().size() == 0;
                        if (dynamicIsNeedCache) {
                            redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), value, label, children);
                            if (!redisUtil.exists(redisKey)) {
                                ActionResult data = dataInterfaceService.infoToId(swapDataVo.getConfig().getPropsUrl(), null, null);
                                if (data != null && data.getData() != null) {
                                    List<Map<String, Object>> dataList = new ArrayList<>();
                                    if (data.getData() instanceof List) {
                                        dataList = (List<Map<String, Object>>) data.getData();
                                    }
                                    JSONArray dataAll = JsonUtil.getListToJsonArray(dataList);
                                    treeToList(label, value, children, dataAll, options);
                                    options.stream().forEach(o -> {
                                        dataInterfaceMap.put(String.valueOf(o.get(finalValue)), String.valueOf(o.get(finalLabel)));
                                    });
                                    redisUtil.insert(redisKey, dataInterfaceMap, 60 * 5);
                                    if (!localCache.containsKey(redisKey)) {
                                        localCache.put(redisKey, dataInterfaceMap);
                                    }
                                }
                            } else {
                                if (!localCache.containsKey(redisKey)) {
                                    localCache.put(redisKey, redisUtil.getMap(redisKey));
                                }
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
            //有使用相关组件的情况才初始化数据
            if (needUser && !localCache.containsKey("__user_map")) {
                //人员
                Map<Object, Object> userMap = redisUtil.getMap(dsName + CacheKeyEnum.USER.getName());
                userMap = Optional.ofNullable(userMap).orElse(new HashMap<>(20));
                localCache.put("__user_map", userMap);
            }
            if (needOrg && !localCache.containsKey("__org_map")) {
                //组织
                Map<Object, Object> orgMap = redisUtil.getMap(dsName + CacheKeyEnum.ORG.getName());
                orgMap = Optional.ofNullable(orgMap).orElse(new HashMap<>(20));
                localCache.put("__org_map", orgMap);
            }
			/*if(needOrg && !localCache.containsKey("__allOrg_map")){
				//组织需要递归显示
				Map<Object, Object> allOrgMap = redisUtil.getMap(dsName + CacheKeyEnum.AllORG.getName());
				allOrgMap = Optional.ofNullable(allOrgMap).orElse(new HashMap<>(20));
				localCache.put("__allOrg_map", allOrgMap);
			}*/
            if (needPos && !localCache.containsKey("__pos_map")) {
                //岗位
                Map<Object, Object> posMap = redisUtil.getMap(dsName + CacheKeyEnum.POS.getName());
                posMap = Optional.ofNullable(posMap).orElse(new HashMap<>(20));
                localCache.put("__pos_map", posMap);
            }
            if (needRole && !localCache.containsKey("__role_map")) {
                //角色
                Map<Object, Object> roleMap = redisUtil.getMap(dsName + CacheKeyEnum.ROLE.getName());
                roleMap = Optional.ofNullable(roleMap).orElse(new HashMap<>(20));
                localCache.put("__role_map", roleMap);
            }
            if (needGroup && !localCache.containsKey("__group_map")) {
                //分组
                Map<Object, Object> groupMap = redisUtil.getMap(dsName + CacheKeyEnum.GROUP.getName());
                groupMap = Optional.ofNullable(groupMap).orElse(new HashMap<>(20));
                localCache.put("__group_map", groupMap);
            }
            if (needProvince && !localCache.containsKey("__pro_maplist")) {
                //省市区
                Map<Object, Object> proMap = redisUtil.getMap(String.format("%s-%s-%d", dsName, "province", 1));
                List<Map<String, String>> proMapList = new ArrayList<>();
                if (proMap.size() == 0) {
                    proMapList = fillProMap(dsName);
                } else {
                    for (int i = 1; i <= 4; i++) {
                        proMapList.add(redisUtil.getMap(String.format("%s-%s-%d", dsName, "province", i)));
                    }
                }
                localCache.put("__pro_maplist", proMapList);
            }
        } catch (Exception e) {
            log.error("在线开发转换数据异常:" + e.getMessage());
            e.printStackTrace();
        }
    }


    private List<Map<String, String>> fillProMap(String dsName) {
        List<Map<String, String>> proMapList = new ArrayList<>();
        //分级存储
        for (int i = 1; i <= 4; i++) {
            String redisKey = String.format("%s-%s-%d", dsName, "province", i);
            if (!redisUtil.exists(redisKey)) {
                List<ProvinceEntity> provinceEntityList = provinceService.getProListBytype(String.valueOf(i));
                Map<String, String> provinceMap = new HashMap<>(16);
                if (provinceEntityList != null) {
                    provinceEntityList.stream().forEach(p -> provinceMap.put(p.getId(), p.getFullName()));
                }
                proMapList.add(provinceMap);
                redisUtil.insert(redisKey, provinceMap, RedisUtil.CAHCEWEEK);
            }
        }
        return proMapList;
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
     * 生成关联属性（弹窗选择属性,关联表单属性）
     *
     * @param fieLdsModelList
     * @param dataMap
     * @param dataDetailMap
     */
    private static void getDataAttr(List<FieLdsModel> fieLdsModelList, Map<String, Object> dataMap, Map<String, Map<String, Object>> dataDetailMap) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            if (ObjectUtil.isEmpty(fieLdsModel)) {
                continue;
            }
            ConfigModel config = fieLdsModel.getConfig();
            String jnpfKey = config.getJnpfKey();
            if (jnpfKey.equals(JnpfKeyConsts.RELATIONFORM_ATTR) || jnpfKey.equals(JnpfKeyConsts.POPUPSELECT_ATTR)) {
                //展示数据 ? 存储数据
                boolean isShow = config.getIsStorage() == 1;
                if (isShow) {
                    String relationField = fieLdsModel.getRelationField();
                    String showField = fieLdsModel.getShowField();
                    Map<String, Object> formDataMap = dataDetailMap.get(relationField);
                    if (formDataMap != null) {
                        dataMap.put(relationField + "_" + showField, formDataMap.get(showField));
                    }
                }
            }
        }
    }


    /**
     * 二维码 条形码详情数据
     *
     * @param codeList    控件集合
     * @param swapDataMap 转换后的数据
     * @param dataMap     转换前
     * @return
     */
    public static void swapCodeDataInfo(List<FormModel> codeList, Map<String, Object> swapDataMap, Map<String, Object> dataMap) {
        for (FormModel formModel : codeList) {
            String jnpfKey = formModel.getConfig().getJnpfKey();
            if (jnpfKey.equals(JnpfKeyConsts.QR_CODE) || jnpfKey.equals(JnpfKeyConsts.BARCODE)) {
                String codeDataType = formModel.getDataType();
                if (OnlineDataTypeEnum.RELATION.getType().equals(codeDataType)) {
                    String relationFiled = formModel.getRelationField();
                    if (StringUtil.isNotEmpty(relationFiled)) {
                        Object relationValue = dataMap.get(relationFiled);
                        if (ObjectUtil.isNotEmpty(relationValue)) {
                            swapDataMap.put(relationFiled + "_id", relationValue);
                        }
                    }
                }
            }
        }
    }

    public ExcelImportModel createExcelData(List<Map<String, Object>> dataList, VisualDevJsonModel visualJsonModel) throws WorkFlowException {
        UserInfo userInfo = UserProvider.getUser();
        UserEntity userEntity = userService.getInfo(userInfo.getUserId());
        ExcelImportModel excelImportModel = new ExcelImportModel();
        DbLinkEntity linkEntity = dblinkService.getInfo(visualJsonModel.getDbLinkId());
        //是否开启并发锁
        Boolean concurrency = false;
        Integer primaryKeyPolicy = visualJsonModel.getFormData().getPrimaryKeyPolicy();
        if (visualJsonModel.getFormData().getConcurrencyLock()) {
            concurrency = true;
        }
        //导入功能流程列表数据
        boolean flowEnable = false;
        String flowTemjsonId = "";
        if (visualJsonModel.isFlowEnable()) {
            flowEnable = true;
            FlowTemplateInfoVO vo = flowTemplateService.info(visualJsonModel.getId());
            if (vo == null || StringUtil.isEmpty(vo.getFlowTemplateJson()) || "[]".equals(vo.getFlowTemplateJson())) {
                throw new WorkFlowException("流程未设计！");
            }
            List<FlowJsonModel> collect = JsonUtil.getJsonToList(vo.getFlowTemplateJson(), FlowJsonModel.class);
            flowTemjsonId = collect.get(0).getId();
        }

        String uploaderTemplateJson = visualJsonModel.getColumnData().getUploaderTemplateJson();
        UploaderTemplateModel uploaderTemplateModel = JsonUtil.getJsonToBean(uploaderTemplateJson, UploaderTemplateModel.class);
        String dataType = uploaderTemplateModel.getDataType();
        ImportFormCheckUniqueModel uniqueModel = new ImportFormCheckUniqueModel();
        uniqueModel.setMain(true);
        uniqueModel.setUpdate(dataType.equals("2"));
        uniqueModel.setPrimaryKeyPolicy(primaryKeyPolicy);
        uniqueModel.setLogicalDelete(visualJsonModel.getFormData().getLogicalDelete());

        DataModel dataModel = DataModel.builder().dataNewMap(null).fieLdsModelList(visualJsonModel.getFormListModels())
                .tableModelList(visualJsonModel.getVisualTables()).mainId(null).link(linkEntity).userEntity(userEntity)
                .concurrencyLock(concurrency).primaryKeyPolicy(primaryKeyPolicy).flowEnable(visualJsonModel.isFlowEnable()).build();


        Map<String, Object> localCache = new HashMap<>();

        //读取系统控件 所需编码 id
        Map<String, Object> depMap = organizeService.getOrgEncodeAndName("department");
        localCache.put("_dep_map", depMap);
        Map<String, Object> comMap = organizeService.getOrgNameAndId("");
        localCache.put("_com_map", comMap);
        Map<String, Object> posMap = positionService.getPosEncodeAndName();
        localCache.put("_pos_map", posMap);
        Map<String, Object> userMap = userService.getUserNameAndIdMap();
        localCache.put("_user_map", userMap);
        Map<String, Object> roleMap = roleService.getRoleNameAndIdMap();
        localCache.put("_role_map", roleMap);
        Map<String, Object> groupMap = groupService.getGroupEncodeMap();
        localCache.put("_group_map", groupMap);


        List<Map<String, Object>> failResult = new ArrayList<>();

        try {
            DynamicDataSourceUtil.switchToDataSource(linkEntity);
            @Cleanup Connection connection = DynamicDataSourceUtil.getCurrentConnection();
            uniqueModel.setConnection(connection);
            for (Map<String, Object> data : dataList) {
                Map<String, Object> resultMap = new HashMap<>(data);
                StringJoiner errInfo = new StringJoiner(",");
                Map<String, Object> errorMap = new HashMap<>(data);
                boolean hasError = this.checkExcelData(visualJsonModel.getFormListModels(),
                        data, localCache, resultMap, errInfo, errorMap, uniqueModel);
                if (hasError) {
                    failResult.add(errorMap);
                } else {
                    //导入时默认第一个流程
                    if (flowEnable) {
                        resultMap.put(FlowFormConstant.FLOWID, flowTemjsonId);
                    }
                    dataModel.setDataNewMap(resultMap);
                    if (StringUtil.isNotEmpty(uniqueModel.getId())) {
                        dataModel.setMainId(uniqueModel.getId());
                        flowFormDataUtil.update(dataModel);
                    } else {
                        String mainId = RandomUtil.uuId();
                        dataModel.setMainId(mainId);
                        flowFormDataUtil.create(dataModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WorkFlowException("导入异常！");
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
        excelImportModel.setFnum(failResult.size());
        excelImportModel.setSnum(dataList.size() - failResult.size());
        excelImportModel.setResultType(failResult.size() > 0 ? 1 : 0);
        excelImportModel.setFailResult(failResult);

        return excelImportModel;
    }


    private boolean checkExcelData(List<FieLdsModel> modelList, Map<String, Object> data, Map<String, Object> localCache, Map<String, Object> insMap,
                                   StringJoiner errInfo, Map<String, Object> errorMap, ImportFormCheckUniqueModel uniqueModel) throws Exception {

        UserInfo userInfo = UserProvider.getUser();
        UserEntity userEntity = userService.getInfo(userInfo.getUserId());

        //读取系统控件 所需编码 id
        Map<String, Object> depMap = (Map<String, Object>) localCache.get("_dep_map");
        Map<String, Object> comMap = (Map<String, Object>) localCache.get("_com_map");
        Map<String, Object> posMap = (Map<String, Object>) localCache.get("_pos_map");
        Map<String, Object> userMap = (Map<String, Object>) localCache.get("_user_map");
        Map<String, Object> roleMap = (Map<String, Object>) localCache.get("_role_map");
        Map<String, Object> groupMap = (Map<String, Object>) localCache.get("_group_map");

        //异常数据
        for (FieLdsModel swapDataVo : modelList) {
            try {
                String jnpfKey = swapDataVo.getConfig().getJnpfKey();
                Object valueO = data.get(swapDataVo.getVModel());
                String label = swapDataVo.getConfig().getLabel();
                //是否必填
                boolean required = swapDataVo.getConfig().isRequired();
                if (valueO == null || "null".equals(valueO)) {
                    if (required) {
                        errInfo.add(label + "值不能为空");
                    }
                    continue;
                }
                String value = String.valueOf(valueO);
                if (StringUtil.isEmpty(value)) {
                    continue;
                }
                Boolean multiple = swapDataVo.getMultiple();
                if (JnpfKeyConsts.CHECKBOX.equals(jnpfKey)) {
                    multiple = true;
                }

                boolean valueMul = value.contains(",");
                value = value.trim();
                List<String> valueList = valueMul ? Arrays.asList(value.split(",")) : new ArrayList<>();
                if (!valueMul) {
                    valueList.add(value);
                }
                List<String> ableDepIds = swapDataVo.getAbleDepIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAbleDepIds(), String.class) : new ArrayList<>();
                List<String> ableGroupIds = swapDataVo.getAbleGroupIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAbleGroupIds(), String.class) : new ArrayList<>();
                List<String> ablePosIds = swapDataVo.getAblePosIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAblePosIds(), String.class) : new ArrayList<>();
                List<String> ableRoleIds = swapDataVo.getAbleRoleIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAbleRoleIds(), String.class) : new ArrayList<>();
                List<String> ableUserIds = swapDataVo.getAbleUserIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAbleUserIds(), String.class) : new ArrayList<>();
                List<String> ableIds = swapDataVo.getAbleIds() != null ? JsonUtil.getJsonToList(swapDataVo.getAbleIds(), String.class) : new ArrayList<>();

                String dataType = swapDataVo.getConfig().getDataType();
                OnlineCusCheckModel cusCheckModel = new OnlineCusCheckModel();
                cusCheckModel.setAbleDepIds(ableDepIds);
                cusCheckModel.setAbleGroupIds(ableGroupIds);
                cusCheckModel.setAblePosIds(ablePosIds);
                cusCheckModel.setAbleRoleIds(ableRoleIds);
                cusCheckModel.setAbleUserIds(ableUserIds);
                cusCheckModel.setControlType(jnpfKey);
                List<String> dataList;
                switch (jnpfKey) {
                    case JnpfKeyConsts.NUM_INPUT:
                        String regNum = "^[0-9]*+(.[0-9]*)?$";
                        if (StringUtil.isNotEmpty(value) && !value.matches(regNum)) {
                            errInfo.add(label + "值不正确");
                        }
                        break;
                    /**
                     * 高级控件
                     */
                    case JnpfKeyConsts.COMSELECT:
                        boolean comErrorHapen = false;
                        if (!multiple) {
                            if (valueList.size() > 1) {
                                comErrorHapen = true;
                                errInfo.add(label + "非多选");
                            }
                        }
                        if (!comErrorHapen) {
                            boolean comvalueErroHappen = false;
                            List<List<String>> comTwoList = new ArrayList<>();
                            List<String> comOneList = new ArrayList<>();
                            for (String comValue : valueList) {
                                String[] split = comValue.split("/");
                                List<String> comIdList = new ArrayList<>();
                                for (String comId : split) {
                                    Object o = comMap.get(comId);
                                    if (o != null) {
                                        comIdList.add(o.toString());
                                    } else {
                                        comvalueErroHappen = true;
                                        break;
                                    }
                                }
                                comOneList.addAll(comIdList);
                                comTwoList.add(comIdList);
                            }
                            if (comvalueErroHappen) {
                                errInfo.add(label + "值不正确");
                            } else {
                                insMap.put(swapDataVo.getVModel(), !multiple ? JsonUtil.getObjectToString(comOneList) : JsonUtil.getObjectToString(comTwoList));
                            }
                        }
                        break;
                    case JnpfKeyConsts.DEPSELECT:
                        dataList = checkOptionsControl(multiple, insMap, swapDataVo.getVModel(), label, depMap, valueList, errInfo);
                        if (dataList.size() == valueList.size() && swapDataVo.getSelectType().equals("custom")) {
                            cusCheckModel.setDataList(dataList);
                            checkCustomControl(cusCheckModel, errInfo, label);
                        }
                        break;
                    case JnpfKeyConsts.POSSELECT:
                        dataList = checkOptionsControl(multiple, insMap, swapDataVo.getVModel(), label, posMap, valueList, errInfo);
                        if (dataList.size() == valueList.size() && swapDataVo.getSelectType().equals("custom")) {
                            cusCheckModel.setDataList(dataList);
                            checkCustomControl(cusCheckModel, errInfo, label);
                        }
                        break;
                    case JnpfKeyConsts.USERSELECT:
                        dataList = checkOptionsControl(multiple, insMap, swapDataVo.getVModel(), label, userMap, valueList, errInfo);
                        if (dataList.size() == valueList.size() && swapDataVo.getSelectType().equals("custom")) {
                            cusCheckModel.setDataList(dataList);
                            checkCustomControl(cusCheckModel, errInfo, label);
                        }
                        break;
                    case JnpfKeyConsts.CUSTOMUSERSELECT:
                        boolean cusUserErrorHapen = false;
                        if (!multiple) {
                            //非多选填入多选值
                            if (valueList.size() > 1) {
                                cusUserErrorHapen = true;
                                errInfo.add(label + "非多选");
                            }
                        }
                        if (!cusUserErrorHapen) {
                            boolean cusUserErrorHapen1 = false;
                            List<String> cusUserList = new ArrayList<>();
                            for (String va : valueList) {
                                String type = null;
                                String id = null;
                                if (groupMap.get(va) != null) {
                                    type = "group";
                                    id = groupMap.get(va).toString();
                                } else if (roleMap.get(va) != null) {
                                    type = "role";
                                    id = roleMap.get(va).toString();
                                } else if (depMap.get(va) != null) {
                                    type = "department";
                                    id = depMap.get(va).toString();
                                } else if (comMap.get(va) != null) {
                                    type = "company";
                                    id = comMap.get(va).toString();
                                } else if (posMap.get(va) != null) {
                                    type = "position";
                                    id = posMap.get(va).toString();
                                } else if (userMap.get(va) != null) {
                                    type = "user";
                                    id = userMap.get(va).toString();
                                }
                                if (type == null && id == null) {
                                    cusUserErrorHapen1 = true;
                                } else {
                                    String lastCusId = id + "--" + type;
                                    if (ableIds.size() > 0) {
                                        if (ableIds.contains(lastCusId)) {
                                            cusUserList.add(lastCusId);
                                        } else {
                                            errInfo.add(label + "值超出自定义范围");
                                        }
                                    } else {
                                        cusUserList.add(lastCusId);
                                    }
                                }
                            }
                            if (cusUserErrorHapen1) {
                                errInfo.add(label + "值不正确");
                            } else {
                                insMap.put(swapDataVo.getVModel(), !multiple ? cusUserList.get(0) : JsonUtil.getObjectToString(cusUserList));
                            }
                        }
                        break;
                    case JnpfKeyConsts.ROLESELECT:
                        checkOptionsControl(multiple, insMap, swapDataVo.getVModel(), label, roleMap, valueList, errInfo);
                        break;
                    case JnpfKeyConsts.GROUPSELECT:
                        checkOptionsControl(multiple, insMap, swapDataVo.getVModel(), label, groupMap, valueList, errInfo);
                        break;
                    case JnpfKeyConsts.ADDRESS:
                        boolean addressErrorHapen = false;
                        if (!multiple) {
                            //非多选填入多选值
                            if (valueList.size() > 1) {
                                addressErrorHapen = true;
                                errInfo.add(label + "非多选");
                            }
                        }
                        if (!addressErrorHapen) {
                            boolean addressErrorHapen1 = false;
                            valueList = Arrays.asList(value.split(","));
                            List<String[]> addresss = new ArrayList<>();
                            List<String> addressList1 = new ArrayList<>();
                            for (String va : valueList) {
                                String[] addressSplit = va.split("/");
                                if (addressSplit.length != swapDataVo.getLevel() + 1) {
                                    addressErrorHapen1 = true;
                                }
                                List<String> addressJoined = new ArrayList<>();
                                List<String> addressParentID = new ArrayList<>();
                                for (String add : addressSplit) {
                                    ProvinceEntity PRO = provinceService.getInfo(add, addressParentID);
                                    if (PRO == null) {
                                        addressErrorHapen1 = true;
                                    } else {
                                        addressJoined.add(PRO.getId());
                                        addressParentID.add(PRO.getId());
                                    }
                                }
                                addressList1.addAll(addressJoined);
                                addresss.add(addressJoined.toArray(new String[addressJoined.size()]));
                            }
                            if (addressErrorHapen1) {
                                errInfo.add(label + "值不正确");
                            } else {
                                insMap.put(swapDataVo.getVModel(), multiple ? JsonUtil.getObjectToString(addresss) : JsonUtil.getObjectToString(addressList1));
                            }
                        }
                        break;
                    /**
                     * 系统控件
                     */
                    case JnpfKeyConsts.CURRORGANIZE:
                        List<UserRelationEntity> OrgRelations = userRelationService.getListByUserId(userInfo.getUserId(), PermissionConst.ORGANIZE);
//						String currentOrgValue = userEntity.getOrganizeId();
//						if (!"all".equals(swapDataVo.getShowLevel())) {
//							OrganizeEntity organizeInfo = organizeService.getInfo(userEntity.getOrganizeId());
//							if ("company".equals(organizeInfo.getCategory())) {
//								currentOrgValue = null;
//							}
//						}
                        insMap.put(swapDataVo.getVModel(), OrgRelations.size() > 0 ? OrgRelations.get(0).getObjectId() : null);
                        break;
                    case JnpfKeyConsts.CURRDEPT:
                        List<UserRelationEntity> depUserRelations = userRelationService.getListByUserId(userInfo.getUserId(), PermissionConst.DEPARTMENT);
                        insMap.put(swapDataVo.getVModel(), depUserRelations.size() > 0 ? depUserRelations.get(0).getObjectId() : null);
                        break;
                    case JnpfKeyConsts.CREATEUSER:
                        insMap.put(swapDataVo.getVModel(), userEntity.getId());
                        break;
                    case JnpfKeyConsts.CREATETIME:
                        insMap.put(swapDataVo.getVModel(), DateUtil.now());
                        break;
                    case JnpfKeyConsts.MODIFYTIME:
                        break;
                    case JnpfKeyConsts.MODIFYUSER:
                        break;
                    case JnpfKeyConsts.CURRPOSITION:
                        insMap.put(swapDataVo.getVModel(), userEntity.getPositionId());
                        break;
                    case JnpfKeyConsts.BILLRULE:
                        String billNo = "";
                        try {
                            String rule = swapDataVo.getConfig().getRule();
                            billNo = billRuleService.getBillNumber(rule, false);
                        } catch (Exception e) {
                            log.error("导入excel:获取单据失败");
                        }
                        insMap.put(swapDataVo.getVModel(), billNo);
                        break;
                    /**
                     * 基础控件
                     */
                    case JnpfKeyConsts.SWITCH:
                        String activeTxt = swapDataVo.getActiveTxt();
                        String inactiveTxt = swapDataVo.getInactiveTxt();
                        if (value.equals(activeTxt)) {
                            insMap.put(swapDataVo.getVModel(), 1);
                        } else if (value.equals(inactiveTxt)) {
                            insMap.put(swapDataVo.getVModel(), 0);
                        } else {
                            errInfo.add(label + "值不正确");
                        }
                        break;
                    case JnpfKeyConsts.SLIDER:
                    case JnpfKeyConsts.RATE:
                        Integer Ivalue = Integer.valueOf(value);
                        boolean errorHapen = false;
                        if (swapDataVo.getMin() != null) {
                            errorHapen = Ivalue < swapDataVo.getMin();
                        }
                        if (!errorHapen) {
                            if (swapDataVo.getMax() != null) {
                                errorHapen = Ivalue > swapDataVo.getMax();
                            }
                        }
                        if (errorHapen) {
                            errInfo.add(label + "值不正确");
                        }
                        break;
                    case JnpfKeyConsts.COM_INPUT:
                        Boolean unique = swapDataVo.getConfig().getUnique();
                        boolean comInputError = false;
                        if (unique) {
                            String tableName = Optional.ofNullable(swapDataVo.getConfig().getRelationTable()).orElse(swapDataVo.getConfig().getTableName());
                            //验证唯一
                            SqlTable sqlTable = SqlTable.of(tableName);
                            String key = flowFormDataUtil.getKey(uniqueModel.getConnection(), tableName, uniqueModel.getPrimaryKeyPolicy());
                            SelectStatementProvider render;
                            if (uniqueModel.getLogicalDelete()) {
                                //开启逻辑删除
                                render = SqlBuilder.select(sqlTable.column(swapDataVo.getVModel()), sqlTable.column(key)).from(sqlTable)
                                        .where(sqlTable.column(swapDataVo.getVModel()), SqlBuilder.isEqualTo(value))
                                        .and(sqlTable.column(TableFeildsEnum.DELETEMARK.getField()), SqlBuilder.isNull())
                                        .build().render(RenderingStrategies.MYBATIS3);
                            } else {
                                render = SqlBuilder.select(sqlTable.column(swapDataVo.getVModel()), sqlTable.column(key)).from(sqlTable)
                                        .where(sqlTable.column(swapDataVo.getVModel()), SqlBuilder.isEqualTo(value))
                                        .build().render(RenderingStrategies.MYBATIS3);
                            }
                            List<Map<String, Object>> mapList = flowFormDataMapper.selectManyMappedRows(render);
                            int count = mapList.size();
                            if (count > 0) {
                                //是否开启支持导入数据更新
                                if (uniqueModel.isUpdate()) {
                                    //是否主表
                                    if (uniqueModel.isMain()) {
                                        Map<String, Object> map = mapList.get(0);
                                        uniqueModel.setId(map.get(key).toString());
                                    }
                                } else {
                                    comInputError = true;
                                    errInfo.add(label + "字段数据重复无法进行导入");
                                }
                            }
                        }
                        //验证正则
                        if (StringUtil.isNotEmpty(swapDataVo.getConfig().getRegList())) {
                            List<RegListModel> regList = JsonUtil.getJsonToList(swapDataVo.getConfig().getRegList(), RegListModel.class);
                            for (RegListModel regListModel : regList) {
                                //处理正则格式
                                String reg = regListModel.getPattern();
                                if (reg.startsWith("/") && reg.endsWith("/")) {
                                    reg = reg.substring(1, reg.length() - 1);
                                }
                                boolean matches = value.matches(reg);
                                if (!matches) {
                                    comInputError = true;
                                    errInfo.add(label + regListModel.getMessage());
                                }
                            }
                        }
                        if (!comInputError) {
                            insMap.put(swapDataVo.getVModel(), value);
                        }
                        break;
                    case JnpfKeyConsts.TIME:
                        try {
                            int formatSize = 3;
                            if ("HH:mm".equals(swapDataVo.getFormat())) {
                                formatSize = 2;
                            }
                            String[] timeSplit = value.split(":");
                            boolean timeHasError = false;
                            if (Integer.parseInt(timeSplit[0]) > 23 || timeSplit.length != formatSize) {
                                timeHasError = true;
                            }
                            if (Integer.parseInt(timeSplit[1]) > 59 || timeSplit.length != formatSize) {
                                timeHasError = true;
                            }
                            if (formatSize == 3) {
                                if (Integer.parseInt(timeSplit[2]) > 59 || timeSplit.length != formatSize) {
                                    timeHasError = true;
                                }
                            }
                            boolean timeHasRangeError = false;
                            //判断时间是否在设置范围内
                            String dataFomrat = "yyyy-MM-dd " + swapDataVo.getFormat();
                            if (swapDataVo.getConfig().getStartTimeRule() && StringUtil.isNotEmpty(swapDataVo.getConfig().getStartTimeValue())) {
                                String startTimeValue = "2000-01-01 " + swapDataVo.getConfig().getStartTimeValue();
                                String valueTime = "2000-01-01 " + value;
                                long startTimeLong = DateUtil.parse(startTimeValue, dataFomrat).getTime();
                                long valueTimeLong = DateUtil.parse(valueTime, dataFomrat).getTime();
                                if (valueTimeLong < startTimeLong) {
                                    timeHasRangeError = true;
                                }

                            }
                            if (swapDataVo.getConfig().getEndTimeRule() && StringUtil.isNotEmpty(swapDataVo.getConfig().getEndTimeValue())) {
                                String endTimeValue = "2000-01-01 " + swapDataVo.getConfig().getEndTimeValue();
                                String valueTime = "2000-01-01 " + value;
                                long endTimeLong = DateUtil.parse(endTimeValue, dataFomrat).getTime();
                                long valueTimeLong = DateUtil.parse(valueTime, dataFomrat).getTime();
                                if (valueTimeLong > endTimeLong) {
                                    timeHasRangeError = true;
                                }
                            }
                            if (timeHasError) {
                                throw new RuntimeException();
                            }
                            if (timeHasRangeError) {
                                errInfo.add(label + "值不在范围内");
                                break;
                            }
                            insMap.put(swapDataVo.getVModel(), value);
                        } catch (Exception e) {
                            errInfo.add(label + "值不正确");
                        }
                        break;
                    case JnpfKeyConsts.DATE:
                        String format = swapDataVo.getFormat();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                        try {
                            if (value.length() != format.length()) {
                                throw new RuntimeException();
                            }
                            simpleDateFormat.parse(String.valueOf(value));
                            //判断时间是否在设置范围内
                            boolean timeHasRangeError = FormPublicUtils.dateTimeCondition(swapDataVo, format, value, insMap, jnpfKey);
                            if (timeHasRangeError) {
                                errInfo.add(label + "值不在范围内");
                                break;
                            }
                            insMap.put(swapDataVo.getVModel(), String.valueOf(value));
                        } catch (Exception e) {
                            errInfo.add(label + "值不正确");
                        }
                        break;
                    /**
                     * 子表
                     */
                    case JnpfKeyConsts.CHILD_TABLE:
                        StringJoiner childJoiner = new StringJoiner(",");
                        List<Map<String, Object>> childTable = new ArrayList<>();
                        for (Map<String, Object> childMap : (List<Map<String, Object>>) data.get(swapDataVo.getVModel())) {
                            Map<String, Object> childTableMap = new HashMap<>(childMap);
                            childTableMap.put("mainAndMast", data);
                            Map<String, Object> childerrorMap = new HashMap<>(childMap);
                            uniqueModel.setMain(false);
                            StringJoiner childJoiner1 = new StringJoiner(",");
                            boolean childHasError = this.checkExcelData(swapDataVo.getConfig().getChildren(), childMap, localCache, childTableMap, childJoiner1, childerrorMap, uniqueModel);
                            if (childHasError) {
                                childJoiner.add(childJoiner1.toString());
                            } else {
                                childTable.add(childTableMap);
                            }
                        }
                        if (childJoiner.length() == 0) {
                            insMap.put(swapDataVo.getVModel(), childTable);
                        } else {
                            errInfo.add(childJoiner.toString());
                        }
                        break;
                    default:
                        break;

                }
                /**
                 * 数据接口
                 */
                if (dataType != null) {
                    List<Map<String, Object>> options = new ArrayList<>();
                    String dataLabel = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getLabel() : swapDataVo.getConfig().getProps().getLabel();
                    String dataValue = swapDataVo.getProps() != null ? swapDataVo.getProps().getProps().getValue() : swapDataVo.getConfig().getProps().getValue();
                    String children = swapDataVo.getProps() != null ? Optional.ofNullable(swapDataVo.getProps().getProps().getChildren()).orElse("") : "";

                    boolean isCascader = JnpfKeyConsts.CASCADER.equals(jnpfKey);

                    String localCacheKey;
                    Map<String, Object> dataInterfaceMap = new HashMap<>();
                    //静态数据
                    if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
                        localCacheKey = String.format("%s-%s", swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
                        if (!localCache.containsKey(localCacheKey)) {
                            if (swapDataVo.getOptions() != null) {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getOptions());
                                String Children = swapDataVo.getProps().getProps().getChildren();
                                JSONArray staticData = JsonUtil.getListToJsonArray(options);
                                getOptions(dataLabel, dataValue, Children, staticData, options);
                            } else {
                                options = JsonUtil.getJsonToListMap(swapDataVo.getSlot().getOptions());
                            }
                            Map<String, Object> finalDataInterfaceMap = new HashMap<>(16);
                            String finalDataLabel = dataLabel;
                            String finalDataValue = dataValue;
                            options.stream().forEach(o -> {
                                finalDataInterfaceMap.put(String.valueOf(o.get(finalDataLabel)), o.get(finalDataValue));
                            });
                            localCache.put(localCacheKey, finalDataInterfaceMap);
                            dataInterfaceMap = finalDataInterfaceMap;
                        } else {
                            dataInterfaceMap = (Map<String, Object>) localCache.get(localCacheKey);
                        }

                        checkFormDataInteface(multiple, insMap, swapDataVo.getVModel(), label, dataInterfaceMap, valueList, errInfo, isCascader);
                        //远端数据
                    } else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
                        localCacheKey = String.format("%s-%s-%s-%s", OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), dataValue, dataLabel);
                        if (!localCache.containsKey(localCacheKey)) {
                            ActionResult actionResult = dataInterfaceService.infoToId(swapDataVo.getConfig().getPropsUrl(), null, null);
                            if (actionResult != null && actionResult.getData() != null) {
                                List<Map<String, Object>> dycDataList = new ArrayList<>();
                                if (actionResult.getData() instanceof List) {
                                    dycDataList = (List<Map<String, Object>>) actionResult.getData();
                                }
                                JSONArray dataAll = JsonUtil.getListToJsonArray(dycDataList);
                                treeToList(dataLabel, dataValue, children, dataAll, options);
                                Map<String, Object> finalDataInterfaceMap1 = new HashMap<>(16);
                                String finalDataLabel2 = dataLabel;
                                String finalDataValue1 = dataValue;
                                options.stream().forEach(o -> {
                                    finalDataInterfaceMap1.put(String.valueOf(o.get(finalDataLabel2)), String.valueOf(o.get(finalDataValue1)));
                                });
                                dataInterfaceMap = finalDataInterfaceMap1;
                                localCache.put(localCacheKey, dataInterfaceMap);
                            }
                        } else {
                            dataInterfaceMap = (Map<String, Object>) localCache.get(localCacheKey);
                        }
                        checkFormDataInteface(multiple, insMap, swapDataVo.getVModel(), label, dataInterfaceMap, valueList, errInfo, isCascader);
                        //数据字典
                    } else if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
                        localCacheKey = String.format("%s-%s", OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
                        dataLabel = swapDataVo.getProps().getProps().getLabel();
                        dataValue = swapDataVo.getProps().getProps().getValue();
                        if (!localCache.containsKey(localCacheKey)) {
                            List<DictionaryDataEntity> list = dictionaryDataService.getDicList(swapDataVo.getConfig().getDictionaryType());
                            options = list.stream().map(dic -> {
                                Map<String, Object> dictionaryMap = new HashMap<>(16);
                                dictionaryMap.put("id", dic.getId());
                                dictionaryMap.put("enCode", dic.getEnCode());
                                dictionaryMap.put("fullName", dic.getFullName());
                                return dictionaryMap;
                            }).collect(Collectors.toList());
                            localCache.put(localCacheKey, options);
                        } else {
                            options = (List<Map<String, Object>>) localCache.get(localCacheKey);
                        }
                        Map<String, Object> finalDataInterfaceMap1 = new HashMap<>(16);
                        String finalDataLabel3 = dataLabel;
                        String finalDataValue3 = dataValue;
                        options.stream().forEach(o -> finalDataInterfaceMap1.put(String.valueOf(o.get(finalDataLabel3)), o.get(finalDataValue3)));

                        checkFormDataInteface(multiple, insMap, swapDataVo.getVModel(), label, finalDataInterfaceMap1, valueList, errInfo, isCascader);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errInfo.add(e.getMessage());
            }
        }
        if (errInfo.length() > 0) {
            errorMap.put("errorsInfo", errInfo.toString());
            insMap = errorMap;
            return true;
        } else {
            return false;
        }
    }

    private List<String> checkOptionsControl(boolean multiple, Map<String, Object> insMap, String vModel, String label, Map<String, Object> cacheMap, List<String> valueList, StringJoiner errInfo) {
        boolean error = false;
        if (!multiple) {
            //非多选填入多选值
            if (valueList.size() > 1) {
                error = true;
                errInfo.add(label + "非多选");
            }
        }
        List<String> dataList = new ArrayList<>();
        if (!error) {
            boolean errorHapen = false;
            for (String va : valueList) {
                Object vo = cacheMap.get(va);
                if (vo == null) {
                    errorHapen = true;
                } else {
                    dataList.add(vo.toString());
                }

            }
            if (errorHapen) {
                errInfo.add(label + "值不正确");
            } else {
                insMap.put(vModel, !multiple ? dataList.get(0) : JsonUtil.getObjectToString(dataList));
            }
        }
        return dataList;
    }

    public void checkCustomControl(OnlineCusCheckModel cusCheckModel, StringJoiner errInfo, String label) {
        List<String> ableDepIds = cusCheckModel.getAbleDepIds();
        List<String> ableGroupIds = cusCheckModel.getAbleGroupIds();
        List<String> ablePosIds = cusCheckModel.getAblePosIds();
        List<String> ableRoleIds = cusCheckModel.getAbleRoleIds();
        List<String> ableUserIds = cusCheckModel.getAbleUserIds();
        List<String> dataList = cusCheckModel.getDataList();
        String controlType = cusCheckModel.getControlType();
        boolean contains;
        switch (controlType) {
            case JnpfKeyConsts.DEPSELECT:
                if (ableDepIds.size() > 0) {
                    for (String id : dataList) {
                        contains = ableDepIds.contains(id);
                        if (!contains) {
                            errInfo.add(label + "值超出自定义范围");
                            break;
                        }
                    }
                }
                break;
            case JnpfKeyConsts.USERSELECT:
                List<String> objIds = new ArrayList<>();
                if (ableDepIds.size() > 0) {
                    objIds.addAll(ableDepIds);
                }
                if (ableGroupIds.size() > 0) {
                    objIds.addAll(ableGroupIds);
                }
                if (ablePosIds.size() > 0) {
                    objIds.addAll(ablePosIds);
                }
                if (ableRoleIds.size() > 0) {
                    objIds.addAll(ableRoleIds);
                }
                List<String> UserIds = userRelationService.getListByObjectIdAll(objIds).stream().map(UserRelationEntity::getUserId).collect(Collectors.toList());
                UserIds.addAll(ableUserIds);
                for (String id : dataList) {
                    contains = UserIds.contains(id);
                    if (!contains) {
                        errInfo.add(label + "值超出自定义范围");
                        break;
                    }
                }
                break;
            case JnpfKeyConsts.POSSELECT:
                List<String> posIds = new ArrayList<>();
                if (ableDepIds.size() > 0) {
                    List<String> depIds = organizeRelationService.getRelationListByOrganizeId(ableDepIds, PermissionConst.POSITION).stream().map(OrganizeRelationEntity::getObjectId).collect(Collectors.toList());
                    posIds.addAll(depIds);
                }
                if (ablePosIds.size() > 0) {
                    posIds.addAll(ablePosIds);
                }
                for (String id : dataList) {
                    contains = posIds.contains(id);
                    if (!contains) {
                        errInfo.add(label + "值超出自定义范围");
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void checkFormDataInteface(boolean multiple, Map<String, Object> insMap, String vModel, String label,
                                      Map<String, Object> cacheMap, List<String> valueList, StringJoiner errInfo, boolean isCascader) {
        List<String[]> staticStrData = new ArrayList<>();
        List<String> staticStrDataList1 = new ArrayList<>();
        List<String> staticStrDataList2 = new ArrayList<>();
        boolean hasError = false;
        boolean takeOne = false;
        for (String dicValue : valueList) {
            if (isCascader) {
                if (dicValue.contains("/")) {
                    String[] split = dicValue.split("/");
                    for (String s : split) {
                        Object s1 = cacheMap.get(s);
                        if (s1 != null) {
                            staticStrDataList2.add(s1.toString());
                            staticStrDataList1.add(s1.toString());
                        } else {
                            hasError = true;
                        }
                    }
                    staticStrData.add(staticStrDataList2.toArray(new String[staticStrDataList2.size()]));
                } else {
                    if (cacheMap.get(dicValue) == null) {
                        hasError = true;
                    } else {
                        staticStrDataList1.add(cacheMap.get(dicValue).toString());
                    }
                }
            } else {
                takeOne = true;
                Object s1 = cacheMap.get(dicValue);
                if (s1 != null) {
                    staticStrDataList1.add(s1.toString());
                } else {
                    hasError = true;
                }
            }
        }

        if (hasError) {
            errInfo.add(label + "值不正确");
        } else {
            String v = multiple ? takeOne ? JsonUtil.getObjectToString(staticStrDataList1) : JsonUtil.getObjectToString(staticStrData)
                    : takeOne ? staticStrDataList1.get(0) : JsonUtil.getObjectToString(staticStrDataList1);
            insMap.put(vModel, v);
        }
    }

    /**
     * 获取接口api数据结果
     *
     */
    public List<Map<String, Object>> getInterfaceData(VisualdevReleaseEntity visualdevEntity, PaginationModel paginationModel, ColumnDataModel columnDataModel) {
        List<Map<String, Object>> realList = new ArrayList<>();
        try {
            DataInterfacePage model = JsonUtil.getJsonToBean(paginationModel, DataInterfacePage.class);
            model.setParamList(JsonUtil.getJsonToList(visualdevEntity.getInterfaceParam(), DataInterfaceModel.class));
            //分组和不分页时设置10w条数据  paginationModel.getDataType()导出全部数据
            if(!columnDataModel.getHasPage() || Objects.equals(columnDataModel.getType(),3) || "1".equals(paginationModel.getDataType())){
                model.setPageSize(100000);
            }

            ActionResult dataInterfaceInfo = dataInterfaceService.infoToIdPageList(visualdevEntity.getInterfaceId(), model);
            if (dataInterfaceInfo.getCode() == 200) {
                List<Map<String, Object>> dataInterfaceList = new ArrayList<>();
                PageListVO pageListVO = JsonUtil.getJsonToBean(dataInterfaceInfo.getData(), PageListVO.class);
                List<Map<String,Object>> dataRes=pageListVO.getList();
                PaginationVO pageres=pageListVO.getPagination();
                paginationModel.setTotal(pageres.getTotal());
                //假查询条件
                if (StringUtil.isNotEmpty(paginationModel.getQueryJson()) && CollectionUtils.isNotEmpty(dataRes)) {
                    List<VisualColumnSearchVO> searchVOList = JsonUtil.getJsonToList(columnDataModel.getSearchList(), VisualColumnSearchVO.class);
                    Map<String, Boolean> conditionConfig = new HashMap<>();
                    for (VisualColumnSearchVO item : searchVOList) {
                        if ("1".equals(item.getSearchType())) {
                            conditionConfig.put(item.getVModel(), true);
                        } else {
                            conditionConfig.put(item.getVModel(), false);
                        }
                    }
                    Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
                    for (Map<String, Object> map : dataRes) {
                        if (OnlinePublicUtils.mapCompar(keyJsonMap, map, conditionConfig)) {
                            dataInterfaceList.add(map);
                        }
                    }
                } else {
                    dataInterfaceList = dataRes;
                }
                //判断是否有id没有则随机
                dataInterfaceList.forEach(item -> {
                    if (item.get("id") == null) {
                        item.put("id", RandomUtil.uuId());
                    }
                });

                //排序
                String sort = StringUtil.isNotEmpty(paginationModel.getSort()) ? paginationModel.getSort() :
                        StringUtil.isNotEmpty(columnDataModel.getSort()) ? columnDataModel.getSort() : "";
                String feild = StringUtil.isNotEmpty(paginationModel.getSidx()) ? paginationModel.getSidx() :
                        StringUtil.isNotEmpty(columnDataModel.getDefaultSidx())?columnDataModel.getDefaultSidx():"";
                if("desc".equalsIgnoreCase(sort)){
                    Collections.sort(dataInterfaceList, (m1, m2)-> String.valueOf(m2.get(feild)).compareTo(String.valueOf(m1.get(feild))));
                }else {
                    Collections.sort(dataInterfaceList, (m1, m2)-> String.valueOf(m1.get(feild)).compareTo(String.valueOf(m2.get(feild))));
                }
                realList = dataInterfaceList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据视图，接口请求失败!message={}", e.getMessage());
        }
        return realList;
    }


    public static List convertToList(Object obj) {
        if (obj instanceof List) {
            List arrayList = (List) obj;
            return arrayList;
        } else {
            List arrayList = new ArrayList();
            arrayList.add(obj);
            return arrayList;
        }
    }

    public static String convertValueToString(String obj, boolean mult, boolean isOrg) {
        if (StringUtil.isNotEmpty(obj)) {
            String prefix = "[";
            if (isOrg) {
                prefix = "[[";
            }
            if (mult) {
                if (!obj.startsWith(prefix)) {
                    JSONArray arr = new JSONArray();
                    if (isOrg) {
                        //组织多选为二维数组
                        arr.add(JSONArray.parse(obj));
                    } else {
                        arr.add(obj);
                    }
                    return arr.toJSONString();
                }
            } else {
                if (obj.startsWith(prefix)) {
                    JSONArray objects = JSONArray.parseArray(obj);
                    return objects.size() > 0 ? objects.get(0).toString() : "";
                }
            }
        }
        return obj;
    }

    /**
     * 获取组织数据中的最后一级组织ID
     * 单选数据获取数组中最后一个组织本身的ID
     * 多选数据获取最后一组组织数据中的最后一个组织本身的ID
     *
     * @param data
     * @return
     */
    public static String getLastOrganizeId(Object data) {
        if (data instanceof List) {
            List listData = (List) data;
            data = listData.get(listData.size() - 1);
            return getLastOrganizeId(data);
        } else if (data instanceof String) {
            String strData = (String) data;
            if (strData.startsWith(StrPool.BRACKET_START)) {
                JSONArray jsonArray = JSONArray.parseArray(strData);
                return getLastOrganizeId(jsonArray);
            } else {
                return strData;
            }
        }
        return data.toString();
    }

    /**
     * 输入时表单时间字段根据格式转换去尾巴
     *
     * @param list 字段属性
     * @param map  数据
     */
    public static void swapDatetime(List<FieLdsModel> list, Map<String, Object> map) {
        List<FieLdsModel> fields = new ArrayList<>();
        FormPublicUtils.recursionFieldsExceptChild(fields, list);
        //主副表
        for (FieLdsModel field : fields) {
            try {
                String vModel = field.getVModel();
                String format = field.getFormat();
                ConfigModel config = field.getConfig();
                if (JnpfKeyConsts.DATE.equals(config.getJnpfKey()) && map.get(vModel) != null) {
                    Date date = new Date(Long.parseLong(String.valueOf(map.get(vModel))));
                    String completionStr = "";
                    switch (format) {
                        case "yyyy":
                            completionStr = "-01-01 00:00:00";
                            break;
                        case "yyyy-MM":
                            completionStr = "-01 00:00:00";
                            break;
                        case "yyyy-MM-dd":
                            completionStr = " 00:00:00";
                            break;
                        case "yyyy-MM-dd HH":
                            completionStr = ":00:00";
                            break;
                        case "yyyy-MM-dd HH:mm":
                            completionStr = ":00";
                            break;
                        default:
                            break;
                    }
                    String datestr = org.openea.eap.extj.util.DateUtil.dateToString(date, format);
                    long time = org.openea.eap.extj.util.DateUtil.stringToDate(datestr + completionStr).getTime();
                    map.replace(vModel, time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //子表
        for (FieLdsModel field : fields) {
            if (field.getVModel().toLowerCase().startsWith(JnpfKeyConsts.CHILD_TABLE_PREFIX)) {
                List<FieLdsModel> children = field.getConfig().getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    List<Object> listObj = (List) map.get(field.getVModel());
                    if (CollectionUtils.isEmpty(listObj)) continue;
                    List<Object> listObjNew = new ArrayList<>();
                    for (Object o : listObj) {
                        Map<String, Object> stringObjectMap = JsonUtil.entityToMap(o);
                        swapDatetime(children, stringObjectMap);
                        listObjNew.add(stringObjectMap);
                    }
                    if (CollectionUtils.isNotEmpty(listObjNew)) {
                        map.replace(field.getVModel(), listObjNew);
                    }
                }
            }

        }
    }

}