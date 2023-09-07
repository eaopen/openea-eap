package org.openea.eap.extj.onlinedev.util.onlineDevUtil;

import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.model.VisualWebTypeEnum;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.FormEnum;
import org.openea.eap.extj.model.visualJson.analysis.FormModel;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevEnum.MultipleControlEnum;
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
 */
public class OnlinePublicUtils {
	/**
	 * 判断有表无表
	 * @return
	 */
	public static Boolean isUseTables(String tableJson){
			if (!StringUtil.isEmpty(tableJson) && !OnlineDevData.TABLE_CONST.equals(tableJson)) {
				return true;
			}
				return false;
		}

	/**
	 * map key转小写
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
		return  obj;
	}

	/**
	 * 去除列表里无用的控件
	 * @param fieldsModelList
	 * @return
	 */
	public static void removeUseless(List<FieLdsModel> fieldsModelList) {
		for (int i = 0 ; i<fieldsModelList.size();i++){
			if (fieldsModelList.get(i).getConfig().getJnpfKey()==null){
				continue;
			}
			if (fieldsModelList.get(i).getConfig().getJnpfKey().equals(JnpfKeyConsts.CHILD_TABLE)){
				continue;
			}
		}
	}


	/**
	 * 递归控件
	 * @return
	 */
	public static void recursionFields(List<FieLdsModel> allFields,List<FieLdsModel> fieLdsModelList){
		for (FieLdsModel fieLdsModel : fieLdsModelList){
			ConfigModel config = fieLdsModel.getConfig();
			String jnpfKey = config.getJnpfKey();
			if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)){
				allFields.add(fieLdsModel);
				continue;
			} else {
				 if (config.getChildren()!= null){
					 recursionFields(allFields, config.getChildren());
				 } else {
					 if (jnpfKey==null){
						 continue;
					 }
					 allFields.add(fieLdsModel);
				 }
			}
		}
	}

	/**
	 * 判断字符串是否以某个字符开头
	 * @param var1 完整字符串
	 * @param var2 统计字符
	 * @return
	 */
	public static Boolean getMultiple(String var1,String var2){
		if (var1.startsWith(var2)){
			return true;
		}
			return false;
	}

	/**
	 *  数据字典处理（从缓存中取出）
	 * @param dataList
	 * @param swapModel
	 * @return
	 */
	public static Map<String,Object> getDataMap(List<Map<String,Object>> dataList, FieLdsModel swapModel) {
		String label;
		String value;
		if (swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.CASCADER) || swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.TREESELECT)
				|| swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.SELECT) || swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.RADIO)
				|| swapModel.getConfig().getJnpfKey().equals(JnpfKeyConsts.CHECKBOX)) {
			PropsBeanModel propsModel = swapModel.getProps().getPropsModel();
			if (propsModel!=null){
				label = swapModel.getProps().getPropsModel().getLabel();
				value = swapModel.getProps().getPropsModel().getValue();
			}else {
				label = swapModel.getProps().getProps().getLabel();
				value = swapModel.getProps().getProps().getValue();
			}
		} else {
			label = swapModel.getConfig().getProps().getLabel();
			value = swapModel.getConfig().getProps().getValue();
		}
		Map<String,Object> dataInterfaceMap = new HashMap<>();
		dataList.stream().forEach(data->{
			dataInterfaceMap.put(String.valueOf(data.get(value)),String.valueOf(data.get(label)));
		});
		return dataInterfaceMap;
	}

	/**
	 * 获取时间(+8)
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateByFormat(Long date,String format){
		DateTimeFormatter ftf = DateTimeFormatter.ofPattern(format);
		String dateString = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("+8")));
		return dateString;
	}

	/**
	 * 递归表单控件
	 * @param modelList 所有控件
	 * @param mainFields 主表
	 * @param childFields 子表
	 * @param models 二维码 条形码
	 */
	public static void recurseFiled(List<FieLdsModel> modelList, List<FieLdsModel> mainFields, List<FieLdsModel> childFields, List<FormModel> models){
		for (FieLdsModel fieLdsModel : modelList){
			ConfigModel config = fieLdsModel.getConfig();
			String jnpfkey = config.getJnpfKey();
			List<FieLdsModel> childrenList = config.getChildren();
			boolean isJnpfKey = StringUtil.isEmpty(jnpfkey);
			List<String> keyList = new ArrayList() {
				{
					this.add(FormEnum.collapseItem.getMessage());
					this.add(FormEnum.collapseItem.getMessage());
					this.add(FormEnum.row.getMessage());
					this.add(FormEnum.card.getMessage());
					this.add(FormEnum.tab.getMessage());
					this.add(FormEnum.collapse.getMessage());
					this.add(FormEnum.tableGrid.getMessage());
					this.add(FormEnum.tableGridTr.getMessage());
					this.add(FormEnum.tableGridTd.getMessage());
				}
			};
			if (keyList.contains(jnpfkey) || isJnpfKey){
				if (childrenList.size()>0){
					recurseFiled(childrenList,mainFields,childFields,models);
				}else {
					mainFields.add(fieLdsModel);
				}
			}else if (FormEnum.table.getMessage().equals(jnpfkey)) {
					childFields.add(fieLdsModel);
			} else if (FormEnum.groupTitle.getMessage().equals(jnpfkey) || FormEnum.divider.getMessage().equals(jnpfkey) || FormEnum.JNPFText.getMessage().equals(jnpfkey)) {

			}else if (FormEnum.QR_CODE.getMessage().equals(jnpfkey) || FormEnum.BARCODE.getMessage().equals(jnpfkey)){
				FormModel formModel = JsonUtil.getJsonToBean(fieLdsModel, FormModel.class);
				models.add(formModel);
			}else {
				mainFields.add(fieLdsModel);
			}
		}
	}

	/**
	 * 递归控件
	 * @return
	 */
	public static void recursionFormFields(List<FieLdsModel> allFields,List<FieLdsModel> fieLdsModelList){
		for (FieLdsModel fieLdsModel : fieLdsModelList){
			ConfigModel config = fieLdsModel.getConfig();
			String jnpfKey = config.getJnpfKey();
			if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)){
				allFields.add(fieLdsModel);
				continue;
			} else {
				if (config.getChildren()!= null){
					recursionFormFields(allFields, config.getChildren());
				} else {
					allFields.add(fieLdsModel);
				}
			}
		}
	}

	/**
	 * 递归控件(取出所有子集)
	 * @return
	 */
	public static void recursionFormChildFields(List<FieLdsModel> allFields,List<FieLdsModel> fieLdsModelList){
		for (FieLdsModel fieLdsModel : fieLdsModelList){
			ConfigModel config = fieLdsModel.getConfig();
			String jnpfKey = config.getJnpfKey();
			if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)){
				String childVmodel = fieLdsModel.getVModel();
				for (FieLdsModel child : Optional.ofNullable(fieLdsModel.getConfig().getChildren()).orElse(new ArrayList<>())){
					if (child.getVModel()!= null){
						child.setVModel(childVmodel + "-" + child.getVModel());
						allFields.add(child);
					}
				}
			} else {
				if (config.getChildren()!= null){
					recursionFormChildFields(allFields, config.getChildren());
				} else {
					if (StringUtil.isNotEmpty(fieLdsModel.getVModel())){
						allFields.add(fieLdsModel);
					}
				}
			}
		}
	}

	public static void recurseOnlineFiled(List<FieLdsModel> modelList,List<FieLdsModel> mainFields,List<FieLdsModel> childFields){
		for (FieLdsModel fieLdsModel : modelList){
			ConfigModel config = fieLdsModel.getConfig();
			String jnpfkey = config.getJnpfKey();
			List<FieLdsModel> childrenList = config.getChildren();
			boolean isJnpfKey = StringUtil.isEmpty(jnpfkey);
			if (FormEnum.row.getMessage().equals(jnpfkey) || FormEnum.card.getMessage().equals(jnpfkey)
					|| FormEnum.tab.getMessage().equals(jnpfkey) || FormEnum.collapse.getMessage().equals(jnpfkey)
					|| isJnpfKey){
				if (childrenList.size()>0){
					recurseOnlineFiled(childrenList,mainFields,childFields);
				}else {
					mainFields.add(fieLdsModel);
				}
			}else if (FormEnum.table.getMessage().equals(jnpfkey)) {
				childFields.add(fieLdsModel);
			} else if (FormEnum.groupTitle.getMessage().equals(jnpfkey) || FormEnum.divider.getMessage().equals(jnpfkey) || FormEnum.JNPFText.getMessage().equals(jnpfkey)) {

			} else {
				mainFields.add(fieLdsModel);
			}
		}
	}

	/**
	 *
	 * @param redisMap 缓存集合
	 * @param modelData  数据
	 * @param isMultiple 是否多选
	 * @return
	 */
	public static String getDataInMethod(Map<String, Object> redisMap, Object modelData,Boolean isMultiple) {
		if (redisMap == null || redisMap.isEmpty()) {
			return modelData.toString();
		}
		String Separator = isMultiple ? ";" : "/";
		String s2;
		if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_TWO.getMultipleChar())) {
			String[][] data = JsonUtil.getJsonToBean(String.valueOf(modelData), String[][].class);
			List<String> addList = new ArrayList<>();
			for (String[] AddressData : data) {
				List<String> adList = new ArrayList<>();
				for (String s : AddressData) {
					adList.add(String.valueOf(redisMap.get(s)));
				}
				addList.add(String.join("/", adList));
			}
			s2 = String.join(";", addList);
		} else if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
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

	public static List<String> getDataNoSwapInMethod(Object modelData){
		List<String> dataValueList = new ArrayList<>();
	  if (OnlinePublicUtils.getMultiple(String.valueOf(modelData), MultipleControlEnum.MULTIPLE_JSON_ONE.getMultipleChar())) {
			List<String> modelDataList = JsonUtil.getJsonToList(String.valueOf(modelData), String.class);
			dataValueList = modelDataList;
		} else {
			String[] modelDatas = String.valueOf(modelData).split(",");
			for (int i = 0; i < modelDatas.length; i++) {
				dataValueList.add(modelDatas[i]);
			}
		}
	  return dataValueList;
	}

	public static VisualDevJsonModel getVisualJsonModel(VisualdevEntity entity){
		VisualDevJsonModel jsonModel = new VisualDevJsonModel();
		jsonModel.setColumnData(JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class));
		jsonModel.setAppColumnData(JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class));
		FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
		jsonModel.setFormData(formDataModel);
		if(!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())){
			jsonModel.setFormListModels(JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class));
		}
		jsonModel.setVisualTables(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
		jsonModel.setId(entity.getId());
		jsonModel.setDbLinkId(entity.getDbLinkId());
		jsonModel.setFullName(entity.getFullName());
		jsonModel.setType(entity.getType());
		jsonModel.setWebType(entity.getWebType());
		jsonModel.setFlowEnable(entity.getEnableFlow() == 1);
		return jsonModel;
	}

	public static VisualDevJsonModel getVisualJsonModel(VisualdevReleaseEntity entity){
		VisualDevJsonModel jsonModel = new VisualDevJsonModel();
		if(entity.getColumnData()!=null){
			jsonModel.setColumnData(JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class));
		}
		if(entity.getAppColumnData()!=null){
			jsonModel.setAppColumnData(JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class));
		}
		FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
		jsonModel.setFormData(formDataModel);
		if(!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())){
			jsonModel.setFormListModels(JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class));
		}
		jsonModel.setVisualTables(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
		jsonModel.setId(entity.getId());
		jsonModel.setDbLinkId(entity.getDbLinkId());
		jsonModel.setFullName(entity.getFullName());
		jsonModel.setType(entity.getType());
		jsonModel.setWebType(entity.getWebType());
		jsonModel.setFlowEnable(entity.getEnableFlow() == 1);
		return jsonModel;
	}
	/**
	 * @param mapList
	 * @return List<Map < String, Object>>
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
	 * 判断两个map有相同key-value
	 *
	 * @param conditionConfig 条件配置，true==，false模糊查询
	 * @return
	 */
	public static boolean mapCompar(Map<String, Object> conditionMap, Map<String, Object> hashMap2, Map<String, Boolean> conditionConfig) {
		boolean isChange = false;
		for (String key : conditionMap.keySet()) {
			String m1value = conditionMap.get(key) == null ? "" : conditionMap.get(key).toString();
			String m2value = hashMap2.get(key) == null ? "" : (String) hashMap2.get(key);
			boolean flag = conditionConfig != null && conditionConfig.get(key) != null ? conditionConfig.get(key) : false;
			if (flag && m1value.equals(m2value)) {
				isChange = true;
			} else if (!flag && m2value.indexOf(m1value) >= 0) {
				isChange = true;
			} else {
				return false;//必须条件全满足才会true
			}
		}
		return isChange;
	}
}

