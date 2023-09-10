package org.openea.eap.extj.onlinedev.util.onlineDevUtil;

import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.TIMESTAMP;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceActionVo;
import org.openea.eap.extj.base.service.DataInterfaceService;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.OnlineDevData;
import org.openea.eap.extj.model.visualJson.analysis.FormModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.CacheKeyEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.OnlineDataTypeEnum;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;
import org.openea.eap.extj.onlinedev.service.VisualDevInfoService;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.permission.entity.*;
import org.openea.eap.extj.permission.service.*;
import org.openea.eap.extj.permission.util.PermissionUtil;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.context.SpringContext;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.form.util.FormPublicUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 在线详情编辑工具类
 *
 */
@Slf4j
public class OnlineDevInfoUtils {
	private static RedisUtil redisUtil;
	private static DictionaryDataService dictionaryDataService;
	private static UserService userService;
	private static PositionService positionService;
	private static OrganizeService organizeService;
	private static VisualdevService visualdevService;
	private static VisualdevModelDataService visualdevModelDataService;
	private static DataInterfaceService dataInterfaceService;
	private static VisualDevInfoService visualDevInfoService;
	//private static ProvinceService provinceService;
	private static RoleService roleService;
	private static GroupService groupService;

	public static void init() {
		visualdevModelDataService = SpringContext.getBean(VisualdevModelDataService.class);
		dictionaryDataService = SpringContext.getBean(DictionaryDataService.class);
		userService = SpringContext.getBean(UserService.class);
		positionService = SpringContext.getBean(PositionService.class);
		redisUtil = SpringContext.getBean(RedisUtil.class);
		organizeService = SpringContext.getBean(OrganizeService.class);
		visualdevService = SpringContext.getBean(VisualdevService.class);
		dataInterfaceService = SpringContext.getBean(DataInterfaceService.class);
		visualDevInfoService = SpringContext.getBean(VisualDevInfoService.class);
		//provinceService = SpringContext.getBean(ProvinceService.class);
		roleService = SpringContext.getBean(RoleService.class);
		groupService = SpringContext.getBean(GroupService.class);
	}

	/**
	 * @param modelList 模型
	 * @param dataMap   数据
	 * @return
	 */
	public static Map<String, Object> swapTableDataInfo(List<FieLdsModel> modelList, Map<String, Object> dataMap, String visualDevId, List<FormModel> codeList) {
		init();

		String dsName = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");

		// 组织
		Object orgData = redisUtil.getString(visualDevId + CacheKeyEnum.ORG.getName());
		Map<String, Object> orgMap = Objects.nonNull(orgData) ? JsonUtil.stringToMap(orgData.toString()) : new HashMap<>(20);

		// 组织多级
		Object allOrgData = redisUtil.getString(visualDevId + CacheKeyEnum.AllORG.getName());
		Map<String, Object> allOrgMap = Objects.nonNull(allOrgData) ? JsonUtil.stringToMap(allOrgData.toString()) : new HashMap<>(20);

		//岗位
		Object posData = redisUtil.getString(visualDevId + CacheKeyEnum.POS.getName());
		Map<String, Object> posMap = Objects.nonNull(posData) ? JsonUtil.stringToMap(posData.toString()) : new HashMap<>(20);

		//人员
		Object userData = redisUtil.getString(visualDevId + CacheKeyEnum.USER.getName());
		Map<String, Object> userMap = Objects.nonNull(userData) ? JsonUtil.stringToMap(userData.toString()) : new HashMap<>(20);

		//角色
		Object roleData = redisUtil.getString(visualDevId + CacheKeyEnum.ROLE.getName());
		Map<String, Object> roleMap = Objects.nonNull(roleData) ? JsonUtil.stringToMap(roleData.toString()) : new HashMap<>(20);

		//分组
		Object groupData = redisUtil.getString(visualDevId + CacheKeyEnum.GROUP.getName());
		Map<String, Object> groupMap = Objects.nonNull(groupData) ? JsonUtil.stringToMap(groupData.toString()) : new HashMap<>(20);

		//省市区
		Map<String, Object> proMap = new HashMap<>();

		Map<String, Object> dataCopyMap = new HashMap<>();
		dataCopyMap.putAll(dataMap);

		try {
			for (FieLdsModel swapDataVo : modelList) {
				String jnpfKey = swapDataVo.getConfig().getJnpfKey();
				String vModel = swapDataVo.getVModel();
				String dataType = String.valueOf(swapDataVo.getConfig().getDataType());
				Boolean isMultiple = swapDataVo.getMultiple();
				Object val = dataMap.get(vModel);
				if (StringUtil.isEmpty(String.valueOf(dataMap.get(vModel))) || val == null) {
					continue;
				}
				if (String.valueOf(dataMap.get(vModel)).equals("[]") || String.valueOf(dataMap.get(vModel)).equals("null")) {
					dataMap.put(vModel, null);
				} else {
					switch (jnpfKey) {
						//组织组件
						case JnpfKeyConsts.COMSELECT:
							//部门组件
						case JnpfKeyConsts.DEPSELECT:
							//所属部门
						case JnpfKeyConsts.CURRDEPT:
							//所属公司
						case JnpfKeyConsts.CURRORGANIZE:
							//显示层级
							if("all".equals(swapDataVo.getShowLevel())){
								if (allOrgMap.size()>0){
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(allOrgMap, val, isMultiple));
								}else {
									dataMap.put(vModel,PermissionUtil.getLinkInfoByOrgId(val.toString(), organizeService, false));
								}
							}else {
								if (orgMap.size()>0){
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(orgMap, val, isMultiple));
								}else {
									dataMap.put(vModel,getOrgValue(String.valueOf(val)));
								}
							}
							break;

						//岗位组件
						case JnpfKeyConsts.POSSELECT:
						//所属岗位
						case JnpfKeyConsts.CURRPOSITION:
							if (posMap.size()>0){
								dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(posMap, val, isMultiple));
							}else {
								dataMap.put(vModel, getPosValue(String.valueOf(val)));
							}
							break;

						//角色选择
						case JnpfKeyConsts.ROLESELECT:
							if (roleMap.size()>0){
								dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(roleMap, val, isMultiple));
							}else {
								dataMap.put(vModel, getRoleValue(String.valueOf(val)));
							}
							break;

						//分组选择
						case JnpfKeyConsts.GROUPSELECT:
							if (groupMap.size()>0){
								dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(groupMap, val, isMultiple));
							}else {
								dataMap.put(vModel, getGroupValue(String.valueOf(val)));
							}
							break;

						//用户组件
						case JnpfKeyConsts.USERSELECT:
							//创建用户
						case JnpfKeyConsts.CREATEUSER:
							//修改用户
						case JnpfKeyConsts.MODIFYUSER:
							if (userMap.size()>0){
								dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(userMap, val, isMultiple));
							}else {
								dataMap.put(vModel,getUserValue(String.valueOf(val)));
							}
							break;

						//省市区联动
						case JnpfKeyConsts.ADDRESS:
							String adressValue = String.valueOf(dataMap.get(vModel));
							if (OnlinePublicUtils.getMultiple(adressValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
								String[][] data = JsonUtil.getJsonToBean(adressValue, String[][].class);
								List<String> addList = new ArrayList<>();
								for (String[] AddressData : data) {
									List<String> adList = new ArrayList<>();
									for (String s : AddressData) {
										adList.add(String.valueOf(proMap.get(s)));
									}
									addList.add(String.join("/", adList));
								}
								dataMap.put(vModel, String.join(";", addList));
							} else {
								List<String> proDataS = JsonUtil.getJsonToList(adressValue, String.class);
								proDataS = proDataS.stream().map(pro -> String.valueOf(proMap.get(pro))).collect(Collectors.toList());
								dataMap.put(vModel, String.join("/", proDataS));
							}
							break;
						//开关 滑块
						case JnpfKeyConsts.SWITCH:
							String switchValue = String.valueOf(dataMap.get(vModel)).equals("1") ? swapDataVo.getActiveTxt() : swapDataVo.getInactiveTxt();
							dataMap.put(vModel, switchValue);
							break;
						case JnpfKeyConsts.RATE:
						case JnpfKeyConsts.SLIDER:
							dataMap.put(vModel, dataMap.get(vModel) != null ? Integer.parseInt(String.valueOf(dataMap.get(vModel))) : null);
							break;
						//级联
						case JnpfKeyConsts.CASCADER:
							String redisKey;
							Map<String, Object> cascaderMap;
							if (OnlineDataTypeEnum.STATIC.getType().equals(dataType)) {
								redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
							} else if (OnlineDataTypeEnum.DYNAMIC.getType().equals(dataType)) {
								redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), swapDataVo.getProps().getPropsModel().getLabel(), swapDataVo.getProps().getPropsModel().getValue(), swapDataVo.getProps().getPropsModel().getChildren());
							} else {
								redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
							}
							Object cascaderDatas = redisUtil.getString(redisKey);
							if (Objects.nonNull(cascaderDatas)) {
								//数据字典 存储的是List<Map>
								if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
									List<Map<String, Object>> options = JsonUtil.getJsonToListMap(cascaderDatas.toString());
									cascaderMap = OnlinePublicUtils.getDataMap(options, swapDataVo);
								} else {
									cascaderMap = JsonUtil.stringToMap(String.valueOf(cascaderDatas));
								}
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(cascaderMap, dataMap.get(vModel),isMultiple));
							}
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
							Object checkData = redisUtil.getString(checkBox);
							if (Objects.nonNull(checkData)) {
								//数据字典 存储的是List<Map>
								if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
									List<Map<String, Object>> options = JsonUtil.getJsonToListMap(checkData.toString());
									checkboxMap = OnlinePublicUtils.getDataMap(options, swapDataVo);
								} else {
									checkboxMap = JsonUtil.entityToMap(checkData);
								}
								dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(checkboxMap, dataMap.get(vModel),isMultiple));
							}
							break;
						case JnpfKeyConsts.RELATIONFORM:
							VisualdevEntity entity = visualdevService.getInfo(swapDataVo.getModelId());
							VisualdevModelDataInfoVO infoVO;
							String keyId = String.valueOf(dataMap.get(vModel));
							Map<String, Object> formDataMap = new HashMap<>(16);
							if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
								infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity);
							} else {
								infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
							}
							formDataMap = JsonUtil.stringToMap(infoVO.getData());
							String relationField = swapDataVo.getRelationField();
							if (formDataMap.size() > 0) {
								dataMap.put(vModel + "_id", dataMap.get(vModel));
								dataMap.put(vModel, formDataMap.get(relationField));
							}
							break;
						case JnpfKeyConsts.POPUPSELECT:
							Object data = dataInterfaceService.infoToId(swapDataVo.getInterfaceId()).getData();
							DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data;
							List<Map<String, Object>> mapList = new ArrayList<>();
							if (actionVo.getData() instanceof List) {
								mapList = (List<Map<String, Object>>) actionVo.getData();
							}
							Map<String, Object> PopMap = mapList.stream().filter(map -> map.get(swapDataVo.getPropsValue()).equals(dataMap.get(vModel))).findFirst().orElse(new HashMap<>());
							if (PopMap.size()>0){
								dataMap.put(vModel+"_id",dataMap.get(vModel));
								dataMap.put(vModel, PopMap.get(swapDataVo.getRelationField()));
							}
							break;
						case JnpfKeyConsts.POPUPTABLESELECT:
							Object popData = dataInterfaceService.infoToId(swapDataVo.getInterfaceId()).getData();
							DataInterfaceActionVo actionPo = (DataInterfaceActionVo) popData;
							List<Map<String, Object>> popMapList = new ArrayList<>();
							if (actionPo.getData() instanceof List) {
								popMapList = (List<Map<String, Object>>) actionPo.getData();
							}
							String popValue = String.valueOf(dataMap.get(vModel));
							List<String> idList = new ArrayList<>();
							if (popValue.contains("[")){
								idList = JsonUtil.getJsonToList(popValue,String.class);
							} else {
								idList.add(popValue);
							}
							List<String> swapValue = new ArrayList<>();
							for (String id : idList){
								popMapList.stream().filter(map ->
										map.get(swapDataVo.getPropsValue()).equals(id)
								).forEach(
										modelMap -> swapValue.add(String.valueOf(modelMap.get(swapDataVo.getRelationField())))
								);
							}
							dataMap.put(vModel,swapValue.stream().collect(Collectors.joining(",")));
							break;
						case JnpfKeyConsts.MODIFYTIME:
						case JnpfKeyConsts.CREATETIME:
						case JnpfKeyConsts.DATE:
							//判断是否为时间戳格式
							String format;
							String dateData = String.valueOf(dataMap.get(vModel));
							String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
							if (!dateData.contains("-") && !dateData.contains(":") && dateData.length() > 10) {
								DateTimeFormatter ftf = DateTimeFormatter.ofPattern(dateSwapInfo);
								format = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) dataMap.get(vModel)), ZoneId.of("+8")));
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
						case JnpfKeyConsts.UPLOADFZ:
						case JnpfKeyConsts.UPLOADIMG:
							List<Map<String, Object>> fileList = JsonUtil.getJsonToListMap(String.valueOf(dataMap.get(vModel)));
							dataMap.put(vModel, fileList);
							break;
						default:
							break;
					}

					//转换数据接口的数据
					if (dataType != null) {
						if (!jnpfKey.equals(JnpfKeyConsts.CASCADER) && !jnpfKey.equals(JnpfKeyConsts.CHECKBOX)) {
							//静态数据
							if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
								String redisKey = String.format("%s-%s-%s", visualDevId, swapDataVo.getVModel(), OnlineDataTypeEnum.STATIC.getType());
								Object staticData = redisUtil.getString(redisKey);
								if(Objects.nonNull(staticData)){
									Map<String, Object> staticMap = JsonUtil.stringToMap(staticData.toString());
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(staticMap, dataMap.get(vModel),isMultiple));
								}
								//远端数据
							} else if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
								//非级联属性把上一步缓存的接口数据Key的child字段删除
								String redisKey = String.format("%s-%s-%s-%s-%s-", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), swapDataVo.getConfig().getProps().getLabel(), swapDataVo.getConfig().getProps().getValue());
								if (jnpfKey.equals(JnpfKeyConsts.TREESELECT)) {
									//树形缓存Key
									redisKey = String.format("%s-%s-%s-%s-%s-%s", dsName, OnlineDataTypeEnum.DYNAMIC.getType(), swapDataVo.getConfig().getPropsUrl(), swapDataVo.getProps().getPropsModel().getLabel(), swapDataVo.getProps().getPropsModel().getValue(), swapDataVo.getProps().getPropsModel().getChildren());
								}
								Object dynamicData = redisUtil.getString(redisKey);
								if (Objects.nonNull(dynamicData)){
									Map<String, Object> dynamicMap = JsonUtil.stringToMap(String.valueOf(dynamicData));
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(dynamicMap, dataMap.get(vModel),isMultiple));
								}

								//数据字典
							} else if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
								String redisKey = String.format("%s-%s-%s", dsName, OnlineDataTypeEnum.DICTIONARY.getType(), swapDataVo.getConfig().getDictionaryType());
								Object dicObj = redisUtil.getString(redisKey);
								if (Objects.nonNull(dicObj)) {
									List<Map<String, Object>> dicObjList = JsonUtil.getJsonToListMap(dicObj.toString());
									Map<String, Object> dictionaryMap = OnlinePublicUtils.getDataMap(dicObjList, swapDataVo);
									dataMap.put(vModel, OnlinePublicUtils.getDataInMethod(dictionaryMap, dataMap.get(vModel),isMultiple));
								}
							}
						}
					}
				}
			}
			//二维码 条形码最后处理
			swapCodeDataInfo(codeList, dataMap, dataCopyMap);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("在线开发转换数据异常:" + e.getMessage());
		}
		return dataMap;
	}

	/**
	 * 子表数据转换(不取缓存)
	 *
	 * @param modelList
	 * @param dataMap
	 * @return
	 */
	public static Map<String, Object> swapChildTableDataInfo(List<FieLdsModel> modelList, Map<String, Object> dataMap, List<FormModel> codeList) {
		init();
		Map<String, Object> dataCopyMap = new HashMap<>();
		dataCopyMap.putAll(dataMap);

		Map<String, Map<String, Object>> dataDetailMap = new HashMap<>();
		try {
			for (FieLdsModel swapDataVo : modelList) {
				String jnpfKey = swapDataVo.getConfig().getJnpfKey();
				String dataType = swapDataVo.getConfig().getDataType();
				String vModel = swapDataVo.getVModel();
				Object val = dataMap.get(vModel);
				Boolean isMultiple = swapDataVo.getMultiple();
				String modelValue = String.valueOf(val);
				if (StringUtil.isEmpty(modelValue) || "null".equals(modelValue)) {
					continue;
				}
				if (dataType != null) {
					//数据接口的数据存放
					String label;
					String value;
					String Children = "";
					List<Map<String, Object>> options = new ArrayList<>();
					if (swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.CASCADER) || swapDataVo.getConfig().getJnpfKey().equals(JnpfKeyConsts.TREESELECT)) {
						PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(swapDataVo.getProps().getProps(), PropsBeanModel.class);
						label = propsBeanModel.getLabel();
						value = propsBeanModel.getValue();
						Children = propsBeanModel.getChildren();
					} else {
						label = swapDataVo.getConfig().getProps().getLabel();
						value = swapDataVo.getConfig().getProps().getValue();
					}
					if (dataType.equals(OnlineDataTypeEnum.STATIC.getType())) {
						if (StringUtil.isNotEmpty(swapDataVo.getOptions())) {
							options = JsonUtil.getJsonToListMap(swapDataVo.getOptions());
							if (ObjectUtil.isNotEmpty(swapDataVo.getProps().getPropsModel())) {
								Children = swapDataVo.getProps().getPropsModel().getChildren();
							} else {
								PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(swapDataVo.getProps().getProps(), PropsBeanModel.class);
								Children = propsBeanModel.getChildren();
							}
							JSONArray data = JsonUtil.getListToJsonArray(options);
							OnlineDevListUtils.getOptions(label, value, Children, data, options);
						} else {
							options = JsonUtil.getJsonToListMap(swapDataVo.getSlot().getOptions());
						}
					}
					if (dataType.equals(OnlineDataTypeEnum.DYNAMIC.getType())) {
						ActionResult data = dataInterfaceService.infoToId(swapDataVo.getConfig().getPropsUrl());
						DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data.getData();
						List<Map<String, Object>> dataList = new ArrayList<>();
						if (actionVo.getData() instanceof List) {
							dataList = (List<Map<String, Object>>) actionVo.getData();
							JSONArray dataAll = JsonUtil.getListToJsonArray(dataList);
							treeToList(label, value, Children, dataAll, options);
						}
					}
					if (dataType.equals(OnlineDataTypeEnum.DICTIONARY.getType())) {
						List<DictionaryDataEntity> list = dictionaryDataService.getDicList(swapDataVo.getConfig().getDictionaryType());
						options = list.stream().map(dic -> {
							Map<String, Object> dictionaryMap = new HashMap<>(16);
							dictionaryMap.put("id", dic.getId());
							dictionaryMap.put("enCode", dic.getEnCode());
							dictionaryMap.put("fullName", dic.getFullName());
							return dictionaryMap;
						}).collect(Collectors.toList());
					}

					Map<String, String> dataInterfaceMap = new HashMap<>(16);
					options.stream().forEach(o -> {
						dataInterfaceMap.put(String.valueOf(o.get(value)), String.valueOf(o.get(label)));
					});

					List<String> valueList = new ArrayList<>();
					if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
						String[][] data = JsonUtil.getJsonToBean(modelValue, String[][].class);
						for (String[] casData : data) {
							for (String s : casData) {
								valueList.add(s);
							}
						}
					} else if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
						valueList = JsonUtil.getJsonToList(modelValue, String.class);
					} else {
						valueList.add(modelValue);
					}
					String allValue = valueList.stream().map(va -> dataInterfaceMap.get(va)).collect(Collectors.joining(","));
					dataMap.put(vModel, allValue);
				} else {
					switch (jnpfKey) {
						//公司组件
						case JnpfKeyConsts.COMSELECT:
							//部门组件
						case JnpfKeyConsts.DEPSELECT:
							//所属部门
						case JnpfKeyConsts.CURRDEPT:
							dataMap.put(vModel, getOrgValue(modelValue));
							break;

						//所属组织
						case JnpfKeyConsts.CURRORGANIZE:
							boolean isAll = "all".equals(swapDataVo.getShowLevel());
								if (isAll){
										dataMap.put(vModel, PermissionUtil.getLinkInfoByOrgId(modelValue, organizeService, false));
								}else {
									OrganizeEntity organizeEntity = organizeService.getInfo(modelValue);
									dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : modelValue);
								}
							break;

						//岗位组件
						case JnpfKeyConsts.POSSELECT:
							//所属岗位
						case JnpfKeyConsts.CURRPOSITION:
							dataMap.put(vModel,getPosValue(modelValue));
							break;

						case JnpfKeyConsts.ROLESELECT:
							dataMap.put(vModel,getRoleValue(modelValue));
							break;
						//分组选择
						case JnpfKeyConsts.GROUPSELECT:
								dataMap.put(vModel, getGroupValue(modelValue));
							break;

						//用户组件
						case JnpfKeyConsts.USERSELECT:
							//创建用户
						case JnpfKeyConsts.CREATEUSER:
							//修改用户
						case JnpfKeyConsts.MODIFYUSER:
							dataMap.put(vModel,getUserValue(modelValue));
							break;

						//省市区联动
						case JnpfKeyConsts.ADDRESS:
							String value = String.valueOf(dataMap.get(vModel));
//							if (OnlinePublicUtils.getMultiple(value, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
//								String[][] data = JsonUtil.getJsonToBean(value, String[][].class);
//								List<String> addList = new ArrayList<>();
//								for (String[] AddressData : data) {
//									List<String> adList = new ArrayList<>();
//									for (String s : AddressData) {
//										adList.add(s);
//									}
//									addList.add(String.join("/", provinceService.getProList(adList).stream().map(pro->pro.getFullName()).collect(Collectors.toList())));
//								}
//								dataMap.put(vModel, String.join(";", addList));
//							} else {
//								List<String> proDataS = JsonUtil.getJsonToList(value, String.class);
//								dataMap.put(vModel, String.join(",",	provinceService.getProList(proDataS).stream().map(pro->pro.getFullName()).collect(Collectors.toList())));
//							}
							break;

						case JnpfKeyConsts.RELATIONFORM:
							VisualdevEntity entity = visualdevService.getInfo(swapDataVo.getModelId());
							VisualdevModelDataInfoVO infoVO;
							String keyId = String.valueOf(dataMap.get(vModel));
							Map<String, Object> formDataMap = new HashMap<>(16);
							if (!StringUtil.isEmpty(entity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(entity.getVisualTables())) {
								infoVO = visualDevInfoService.getDetailsDataInfo(keyId, entity);
							} else {
								infoVO = visualdevModelDataService.infoDataChange(keyId, entity);
							}
							formDataMap = JsonUtil.stringToMap(infoVO.getData());
							String relationField = swapDataVo.getRelationField();
							if (formDataMap.size() > 0) {
								dataMap.put(vModel + "_id", dataMap.get(vModel));
								dataMap.put(vModel, formDataMap.get(relationField));
								dataDetailMap.put(vModel, formDataMap);
							}
							break;

						case JnpfKeyConsts.POPUPSELECT:
							Object data = dataInterfaceService.infoToId(swapDataVo.getInterfaceId()).getData();
							DataInterfaceActionVo actionVo = (DataInterfaceActionVo) data;
							List<Map<String, Object>> mapList = new ArrayList<>();
							if (actionVo.getData() instanceof List) {
								mapList = (List<Map<String, Object>>) actionVo.getData();
							}
							Map<String, Object> PopMap = mapList.stream().filter(map -> map.get(swapDataVo.getPropsValue()).equals(dataMap.get(vModel))).findFirst().orElse(null);
							if (PopMap.size()>0){
								dataMap.put(vModel + "_id", dataMap.get(vModel));
								dataMap.put(vModel, PopMap.get(swapDataVo.getColumnOptions().get(0).getValue()));
								dataDetailMap.put(vModel,PopMap);
							}
							break;
						case JnpfKeyConsts.POPUPTABLESELECT:
							Object popData = dataInterfaceService.infoToId(swapDataVo.getInterfaceId()).getData();
							DataInterfaceActionVo actionPo = (DataInterfaceActionVo) popData;
							List<Map<String, Object>> popMapList = new ArrayList<>();
							if (actionPo.getData() instanceof List) {
								popMapList = (List<Map<String, Object>>) actionPo.getData();
							}
							String popValue = String.valueOf(dataMap.get(vModel));
							List<String> idList = new ArrayList<>();
							if (popValue.contains("[")){
								idList = JsonUtil.getJsonToList(popValue,String.class);
							} else {
								idList.add(popValue);
							}
							List<String> swapValue = new ArrayList<>();
							for (String id : idList){
								popMapList.stream().filter(map ->
										map.get(swapDataVo.getPropsValue()).equals(id)
								).forEach(
										modelMap -> swapValue.add(String.valueOf(modelMap.get(swapDataVo.getRelationField())))
								);
							}
							dataMap.put(vModel,swapValue.stream().collect(Collectors.joining(",")));
							break;
						case JnpfKeyConsts.MODIFYTIME:
						case JnpfKeyConsts.CREATETIME:
						case JnpfKeyConsts.DATE:
							//判断是否为时间戳格式
							String format;
							String dateData = String.valueOf(dataMap.get(vModel));
							String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
							if (!dateData.contains("-") && !dateData.contains(":") && dateData.length() > 10) {
								DateTimeFormatter ftf = DateTimeFormatter.ofPattern(dateSwapInfo);
								format = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) dataMap.get(vModel)), ZoneId.of("+8")));
							} else {
								format = dateData;
							}
							if (format.contains(".")) {
								format = format.substring(0, format.lastIndexOf("."));
							}
							SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
							try {
								Date date = sdf.parse(format);
								String outTime = sdf.format(sdf.parse(DateUtil.dateFormat(date)));
								dataMap.put(vModel, outTime);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							break;

						//开关 滑块
						case JnpfKeyConsts.SWITCH:
							String switchValue = String.valueOf(dataMap.get(vModel)).equals("1") ? swapDataVo.getActiveTxt() : swapDataVo.getInactiveTxt();
							dataMap.put(vModel, switchValue);
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

						default:
							break;
					}
				}
			}
			//转换二维码
			swapCodeDataInfo(codeList, dataMap, dataCopyMap);
			//关联选择属性
			if (dataDetailMap.size() > 0) {
				getDataAttr(modelList, dataMap, dataDetailMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	/**
	 * 转换数据格式(编辑页)
	 *
	 * @param modelList 控件
	 * @param dataMap 数据
	 * @return
	 */
	public static Map<String, Object> swapDataInfoType(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
		init();
		dataMap = Optional.ofNullable(dataMap).orElse(new HashMap<>());
		try {
			DynamicDataSourceUtil.switchToDataSource(null);
			List<String> systemConditions = new ArrayList(){{
				add(JnpfKeyConsts.CURRORGANIZE);
				add(JnpfKeyConsts.CURRDEPT);
				add(JnpfKeyConsts.CURRPOSITION);
			}};
			for (FieLdsModel swapDataVo : modelList) {
				String jnpfKey = swapDataVo.getConfig().getJnpfKey();
				String vModel = swapDataVo.getVModel();
				Object value = dataMap.get(vModel);
				if (value == null || ObjectUtil.isEmpty(value)) {
					if (systemConditions.contains(jnpfKey)){
						dataMap.put(vModel, " ");
					}else{
						dataMap.put(vModel, "");
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
						//处理为时间戳
						String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
						String s1 = String.valueOf(value);
						Long s = null;
						try {
							if(value instanceof TemporalAccessor){
								s1 = TemporalAccessorUtil.format((TemporalAccessor) value, dateSwapInfo);
							}
							s = sdf.parse(s1).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						dataMap.put(vModel, s);
						break;
					case JnpfKeyConsts.CREATETIME:
					case JnpfKeyConsts.MODIFYTIME:
						dataMap.put(vModel, value);
						String pattern = "yyyy-MM-dd HH:mm:ss";
						String dateValue = "";
						if(ObjectUtil.isNotEmpty(value)){
							if (value instanceof TIMESTAMP){
								String s2 = value.toString();
								String substring = s2.substring(0, s2.lastIndexOf("."));
								dateValue = substring;
							} else if (value instanceof Date){
								dateValue = DateUtil.dateToString((Date)value,pattern);
							} else if (value instanceof Timestamp){
								SimpleDateFormat df = new SimpleDateFormat(pattern);
								dateValue = df.format(value);
							}else {
								dateValue = DateUtil.dateToString(DateUtil.localDateTimeToDate((LocalDateTime)value),pattern);
							}
						}
						dataMap.put(vModel,dateValue);
						break;
					case JnpfKeyConsts.SWITCH:
					case JnpfKeyConsts.RATE:
					case JnpfKeyConsts.SLIDER:
						dataMap.put(vModel, value != null ? Integer.parseInt(String.valueOf(value)) : null);
						break;
					//系统自动生成控件
					case JnpfKeyConsts.CURRORGANIZE:
					case JnpfKeyConsts.CURRDEPT:
						if ("all".equals(swapDataVo.getShowLevel())){
							String organizeName = PermissionUtil.getLinkInfoByOrgId(String.valueOf(value), organizeService, false);
							dataMap.put(vModel,organizeName);
						}else {
							OrganizeEntity organizeEntity = organizeService.getInfo(String.valueOf(value));
							dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : value);
						}
						break;

					case JnpfKeyConsts.CURRPOSITION:
						PositionEntity positionEntity = positionService.getInfo(String.valueOf(value));
						dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
						break;

					case JnpfKeyConsts.CREATEUSER:
					case JnpfKeyConsts.MODIFYUSER:
						UserEntity userEntity = userService.getInfo(String.valueOf(value));
						String userValue = Objects.nonNull(userEntity) ? userEntity.getAccount().equalsIgnoreCase("admin")
								? "管理员" :  userEntity.getRealName() : String.valueOf(value);
						dataMap.put(vModel, userValue);
						break;
					default:
						if (OnlinePublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
							String[][] data = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
							dataMap.put(vModel, data);
						} else if (OnlinePublicUtils.getMultiple(String.valueOf(value), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
							List<String> list = JsonUtil.getJsonToList(String.valueOf(value), String.class);
							dataMap.put(vModel, list);
						} else {
							dataMap.put(vModel, value);
						}
						break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			DynamicDataSourceUtil.clearSwitchDataSource();
		}
		return dataMap;
	}

	/**
	 * 转换数据格式(编辑页)V
	 *
	 * @param modelList 控件
	 * @param dataMap 数据
	 * @return
	 */
	public static Map<String, Object> getInitLineData(List<FieLdsModel> modelList, Map<String, Object> dataMap) {
		init();
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
					Long s = null;
					if(value instanceof  Date){
						s = ((Date) value).getTime();
					}else if(value instanceof LocalDateTime){
						s = DateUtil.localDateTimeToDate((LocalDateTime) value).getTime();
					}else if(value instanceof TIMESTAMP){
						//处理为时间戳
						String dateSwapInfo = swapDataVo.getFormat() != null ? swapDataVo.getFormat() : swapDataVo.getType() != null && swapDataVo.getType().equals(JnpfKeyConsts.DATE) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
						SimpleDateFormat sdf = new SimpleDateFormat(dateSwapInfo);
						String s1 = String.valueOf(value);
						try {
							s = sdf.parse(s1).getTime();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					dataMap.put(vModel, s);
					break;

				case JnpfKeyConsts.SWITCH:
				case JnpfKeyConsts.RATE:
				case JnpfKeyConsts.SLIDER:
					dataMap.put(vModel, value != null ? Integer.parseInt(String.valueOf(value)) : null);
					break;
				//系统自动生成控件
				case JnpfKeyConsts.CURRORGANIZE:
				case JnpfKeyConsts.CURRDEPT:
					if ("all".equals(swapDataVo.getShowLevel())){
						String organizeName = PermissionUtil.getLinkInfoByOrgId(String.valueOf(value), organizeService, false);
						dataMap.put(vModel,organizeName);
					}else {
						OrganizeEntity organizeEntity = organizeService.getInfo(String.valueOf(value));
						dataMap.put(vModel, Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : value);
					}
					break;

				case JnpfKeyConsts.CURRPOSITION:
					PositionEntity positionEntity = positionService.getInfo(String.valueOf(value));
					dataMap.put(vModel, Objects.nonNull(positionEntity) ? positionEntity.getFullName() : value);
					break;

				case JnpfKeyConsts.CREATEUSER:
				case JnpfKeyConsts.MODIFYUSER:
					UserEntity userEntity = userService.getInfo(String.valueOf(value));
					String userValue = Objects.nonNull(userEntity) ? userEntity.getAccount().equalsIgnoreCase("admin")
							? "管理员" :  userEntity.getRealName() : String.valueOf(value);
					dataMap.put(vModel, userValue);
					break;
				default:
					dataMap.put(vModel, FormPublicUtils.getDataConversion(value));
					break;
			}
		}
		return dataMap;
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
	 * 生成关联属性（弹窗选择属性,关联表单属性）
	 * @param fieLdsModelList
	 * @param dataMap
	 * @param dataDetailMap
	 */
	private static void getDataAttr(List<FieLdsModel> fieLdsModelList, Map<String, Object> dataMap, Map<String, Map<String, Object>> dataDetailMap) {
		for (FieLdsModel fieLdsModel : fieLdsModelList) {
			String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
			if (jnpfKey.equals(JnpfKeyConsts.RELATIONFORM_ATTR) || jnpfKey.equals(JnpfKeyConsts.POPUPSELECT_ATTR)) {
				String relationField = fieLdsModel.getRelationField();
				String showField = fieLdsModel.getShowField();
				Map<String, Object> formDataMap = dataDetailMap.get(relationField);
				dataMap.put(relationField + "_" + showField, formDataMap.get(showField));
			}
		}
	}

	/**
	 * 转换组织
	 * @param modelValue
	 * @return
	 */
	private static String getOrgValue(String modelValue) {
		String orgValue;
		List<String> valueList;
		if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] prgArray = JsonUtil.getJsonToBean(modelValue, String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] prgData : prgArray) {
				List<String> adList = new ArrayList<>();
				for (String s : prgData) {
					OrganizeEntity info = organizeService.getInfo(s);
					adList.add(Objects.nonNull(info) ? info.getFullName() : "");
				}
				String porData = adList.stream().collect(Collectors.joining("/"));
				addList.add(porData);
			}
			orgValue = String.join(";", addList);
		} else {
			if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
				valueList = JsonUtil.getJsonToList(modelValue, String.class);
			} else {
				valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
			}
			String allValue = valueList.stream().map(va -> {
				OrganizeEntity organizeEntity = organizeService.getInfo(va);
				return Objects.nonNull(organizeEntity) ? organizeEntity.getFullName() : va;
			}).collect(Collectors.joining(","));
			orgValue = allValue;
		}
		return orgValue;
	}

	/**
	 * 转换岗位
	 * @param modelValue
	 * @return
	 */
	private static String getPosValue(String modelValue){
		String posValue;
		List<String> valueList;
		if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] prgArray = JsonUtil.getJsonToBean(modelValue, String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] prgData : prgArray) {
				List<String> adList = new ArrayList<>();
				for (String s : prgData) {
					PositionEntity info = positionService.getInfo(s);
					adList.add(Objects.nonNull(info) ? info.getFullName() : "");
				}
				String porData = adList.stream().collect(Collectors.joining("/"));
				addList.add(porData);
			}
			posValue = String.join(";", addList);
		} else {
			if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
				valueList = JsonUtil.getJsonToList(modelValue, String.class);
			} else {
				valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
			}
			String allValue = valueList.stream().map(va -> {
				PositionEntity positionEntity = positionService.getInfo(va);
				return Objects.nonNull(positionEntity) ? positionEntity.getFullName() : va;
			}).collect(Collectors.joining(","));
			posValue = allValue;
		}
		return posValue;
	}

	/**
	 * 转换用户
	 * @param modelValue
	 * @return
	 */
	private static String getUserValue(String modelValue){
		String userValue;
		List<String> valueList;
		if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] prgArray = JsonUtil.getJsonToBean(modelValue, String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] prgData : prgArray) {
				List<String> adList = new ArrayList<>();
				for (String s : prgData) {
					UserEntity info = userService.getInfo(s);
					adList.add(Objects.nonNull(info) ? info.getRealName() : "");
				}
				String porData = adList.stream().collect(Collectors.joining("/"));
				addList.add(porData);
			}
			userValue = String.join(";", addList);
		} else {
			if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
				valueList = JsonUtil.getJsonToList(modelValue, String.class);
			} else {
				valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
			}
			String allValue = valueList.stream().map(va -> {
				UserEntity userEntity = userService.getInfo(va);
				return Objects.nonNull(userEntity) ? userEntity.getRealName() : va;
			}).collect(Collectors.joining(","));
			userValue = allValue;
		}
		return userValue;
	}


	/**
	 * 转换角色
	 * @param modelValue
	 * @return
	 */
	private static String getRoleValue(String modelValue){
		String value;
		List<String> valueList;
		if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] prgArray = JsonUtil.getJsonToBean(modelValue, String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] prgData : prgArray) {
				List<String> adList = new ArrayList<>();
				for (String s : prgData) {
					RoleEntity info = roleService.getInfo(s);
					adList.add(Objects.nonNull(info) ? info.getFullName() : "");
				}
				String porData = adList.stream().collect(Collectors.joining("/"));
				addList.add(porData);
			}
			value = String.join(";", addList);
		} else {
			if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
				valueList = JsonUtil.getJsonToList(modelValue, String.class);
			} else {
				valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
			}
			String allValue = valueList.stream().map(va -> {
				RoleEntity userEntity = roleService.getInfo(va);
				return Objects.nonNull(userEntity) ? userEntity.getFullName() : va;
			}).collect(Collectors.joining(","));
			value = allValue;
		}
		return value;
	}

	/**
	 * 转换分组
	 * @param modelValue
	 * @return
	 */
	private static String getGroupValue(String modelValue){
		String value;
		List<String> valueList;
		if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] prgArray = JsonUtil.getJsonToBean(modelValue, String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] prgData : prgArray) {
				List<String> adList = new ArrayList<>();
				for (String s : prgData) {
					GroupEntity info = groupService.getInfo(s);
					adList.add(Objects.nonNull(info) ? info.getFullName() : "");
				}
				String porData = adList.stream().collect(Collectors.joining("/"));
				addList.add(porData);
			}
			value = String.join(";", addList);
		} else {
			if (OnlinePublicUtils.getMultiple(modelValue, MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
				valueList = JsonUtil.getJsonToList(modelValue, String.class);
			} else {
				valueList = Stream.of(modelValue.split(",")).collect(Collectors.toList());
			}
			String allValue = valueList.stream().map(va -> {
				GroupEntity info = groupService.getInfo(va);
				return Objects.nonNull(info) ? info.getFullName() : va;
			}).collect(Collectors.joining(","));
			value = allValue;
		}
		return value;
	}


}
