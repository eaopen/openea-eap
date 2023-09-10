package org.openea.eap.extj.form.service.impl;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.form.service.FormDataService;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.model.organize.OrganizeConditionModel;
import org.openea.eap.extj.permission.model.user.mod.UserConditionModel;
import org.openea.eap.extj.form.util.FlowFormCustomUtils;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.openea.eap.extj.form.util.FlowFormHttpReqUtils;
import org.openea.eap.extj.form.util.TableFeildsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.*;


@Service
public class FlowDataServiceImpl implements FormDataService {
	@Autowired
	private FlowFormCustomUtils flowFormCustomUtils;
	@Autowired
	private FlowFormHttpReqUtils flowFormHttpReqUtils;

	@Autowired
	private FlowFormService flowFormService;
	@Autowired
	private DbLinkService dblinkService;
	@Autowired
	private FlowFormDataUtil flowDataUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private OrganizeService organizeService;
	@Autowired
	private UserProvider userProvider;

	@Override
	public void create(String formId, String id, Map<String, Object> map) throws WorkFlowException {
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		//判断是否为系统表单
		boolean b = flowFormEntity.getFormType() == 1;
		if (b){
			flowFormHttpReqUtils.create(flowFormEntity,id,UserProvider.getToken(),map);
		} else {
			flowFormCustomUtils.create(flowFormEntity,id,map,null);
		}
	}

	@Override
	public void update(String formId, String id, Map<String, Object> map) throws WorkFlowException, SQLException, DataException {
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		//判断是否为系统表单
		boolean b = flowFormEntity.getFormType() == 1;
		if (b){
			flowFormHttpReqUtils.update(flowFormEntity,id,UserProvider.getToken(),map);
		} else {
			flowFormCustomUtils.update(flowFormEntity,id,map);
		}
	}

	@Override
	public void saveOrUpdate(String formId, String id, Map<String, Object> map, UserEntity delegateUser) throws WorkFlowException {
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		Integer formType = flowFormEntity.getFormType();
		if(map.get(TableFeildsEnum.VERSION.getField().toUpperCase())!=null){//针对Oracle数据库大小写敏感，出现大写字段补充修复
			map.put(TableFeildsEnum.VERSION.getField(),map.get(TableFeildsEnum.VERSION.getField().toUpperCase()));
		}
		//系统表单
		if (formType == 1){
			flowFormHttpReqUtils.saveOrUpdate(flowFormEntity,id,UserProvider.getToken(),map);
		} else {
			try {
				flowFormCustomUtils.saveOrUpdate(flowFormEntity,id,map,delegateUser);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			} catch (DataException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean delete(String formId, String id) throws Exception {
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		List<TableModel> tableModels = JsonUtil.getJsonToList(flowFormEntity.getTableJson(), TableModel.class);
		FormDataModel formData = JsonUtil.getJsonToBean(flowFormEntity.getPropertyJson(), FormDataModel.class);
		Integer primaryKeyPolicy = formData.getPrimaryKeyPolicy();
		DbLinkEntity linkEntity = StringUtil.isNotEmpty(flowFormEntity.getDbLinkId()) ? dblinkService.getInfo(flowFormEntity.getDbLinkId()) : null;
		flowDataUtil.deleteTable(id, primaryKeyPolicy,tableModels,linkEntity);
		return true;
	}

	@Override
	public Map<String, Object> info(String formId, String id) {
		FlowFormEntity flowFormEntity = flowFormService.getById(formId);
		Assert.notNull(flowFormEntity, "表单信息不存在");
		Map<String, Object> allDataMap = new HashMap();
		//判断是否为系统表单
		boolean b = flowFormEntity.getFormType() == 1;
		if (b){
			allDataMap = flowFormHttpReqUtils.info(flowFormEntity,id,UserProvider.getToken());
		} else {
			allDataMap = flowFormCustomUtils.info(flowFormEntity,id);
		}
		return allDataMap;
	}

	/**
	 * 处理配置（默认当前登录值）
	 * @param configJson
	 * @param havaDefaultCurrentValue
	 * @return
	 */
	@Override
	public String setDefaultCurrentValue(String configJson, Map<String, Integer> havaDefaultCurrentValue) {
		if(StringUtil.isEmpty(configJson)) {
			return configJson;
		}
		Map<String, Object> configJsonMap = JsonUtil.stringToMap(configJson.trim());
		if(configJsonMap == null && configJsonMap.isEmpty()) {
			return configJson;
		}
		int isChange = 0;

		UserInfo userInfo = null;
		//处理字段
		Object fieldsObj = configJsonMap.get("fields");
		List<Map<String, Object>> fieldsList = null;
		if(fieldsObj != null) {
			fieldsList = (List<Map<String, Object>>)fieldsObj;
			if(fieldsList != null && !fieldsList.isEmpty()) {
				if(userInfo == null) {
					userInfo = userProvider.get();
				}
				setDefaultCurrentValue(havaDefaultCurrentValue, fieldsList, userInfo, "add");
				configJsonMap.put("fields", fieldsList);
				isChange = 1;
			}
		}
		//处理查询条件
		Object searchObj = configJsonMap.get("searchList");
		List<Map<String, Object>> searchList = null;
		if(searchObj != null) {
			searchList = (List<Map<String, Object>>)searchObj;
			if(searchList != null && !searchList.isEmpty()) {
				if(userInfo == null) {
					userInfo = userProvider.get();
				}
				setDefaultCurrentValue(havaDefaultCurrentValue, searchList, userInfo, "search");
				configJsonMap.put("searchList", searchList);
				isChange = 1;
			}
		}

		//处理查询条件
		Object columnListObj = configJsonMap.get("columnList");
		List<Map<String, Object>> columnList = null;
		if(columnListObj != null) {
			columnList = (List<Map<String, Object>>)columnListObj;
			if(columnList != null && !columnList.isEmpty()) {
				if(userInfo == null) {
					userInfo = userProvider.get();
				}
				setDefaultCurrentValue(havaDefaultCurrentValue, columnList, userInfo, "add");
				configJsonMap.put("columnList", columnList);
				isChange = 1;
			}
		}

		if(isChange == 1) {
			return JsonUtil.getObjectToString(configJsonMap);
		} else {
			return configJson;
		}
	}

	//递归处理默认当前配置
	private void setDefaultCurrentValue(Map<String, Integer> havaDefaultCurrentValue, List<Map<String, Object>> itemList, UserInfo userInfo, String parseFlag) {
		for(int i = 0, len = itemList.size(); i < len; i++) {
			Map<String, Object> itemMap = itemList.get(i);
			if(itemMap == null || itemMap.isEmpty()) {
				continue;
			}
			Map<String, Object> configMap = (Map<String, Object>)itemMap.get("__config__");
			if(configMap == null || configMap.isEmpty()) {
				continue;
			}
			List<Map<String,Object>> childrenList = (List<Map<String,Object>>)configMap.get("children");
			if(childrenList != null && !childrenList.isEmpty()) {
				setDefaultCurrentValue(havaDefaultCurrentValue, childrenList, userInfo, parseFlag);
				configMap = (Map<String, Object>)itemMap.get("__config__");
			}
			String jnpfKey = (String)configMap.get("jnpfKey");
			String defaultCurrent = String.valueOf(configMap.get("defaultCurrent"));
			if("userSelect".equals(jnpfKey) && "true".equals(defaultCurrent)) {
				String selectType = String.valueOf(itemMap.get("selectType"));
				String formId = String.valueOf(configMap.get("formId"));
				String vModel = String.valueOf(itemMap.get("__vModel__"));
				Integer inited = havaDefaultCurrentValue.get(formId + "____" + vModel);
				int havaFlag = 0;
				if(inited != null) {
					havaFlag = inited;
				}else {
					if ("all".equals(selectType)) {
						havaFlag = 1;
					} else if ("custom".equals(selectType)) {
						List<String> ableUserIds = (List<String>) itemMap.get("ableUserIds");
						if (ableUserIds != null && !ableUserIds.isEmpty() && ableUserIds.contains(userInfo.getUserId())) {
							havaFlag = 1;
						} else {
							List<String> ableGroupIds = (List<String>) itemMap.get("ableGroupIds");
							List<String> ableDepIds = (List<String>) itemMap.get("ableDepIds");
							List<String> ableRoleIds = (List<String>) itemMap.get("ableRoleIds");
							List<String> ablePosIds = (List<String>) itemMap.get("ablePosIds");

							UserConditionModel userConditionModel = new UserConditionModel();
							userConditionModel.setUserIds(ableUserIds);
							userConditionModel.setDepartIds(ableDepIds);
							userConditionModel.setGroupIds(ableGroupIds);
							userConditionModel.setRoleIds(ableRoleIds);
							userConditionModel.setPositionIds(ablePosIds);
							String userId = userService.getDefaultCurrentValueUserId(userConditionModel);
							if(StringUtil.isNotEmpty(userId) && userId.equals(userInfo.getUserId())) {
								havaFlag = 1;
							}else {
								havaFlag = 0;
							}
						}
					} else {
						havaFlag = 0;
					}
				}
				havaDefaultCurrentValue.put(formId + "____" + vModel, havaFlag);

				if("search".equals(parseFlag)) {
					String searchMultiple = String.valueOf(itemMap.get("searchMultiple"));
					if ("true".equals(searchMultiple)) {
						if(havaFlag == 1) {
							configMap.put("defaultValue", new String[]{userInfo.getUserId()});
						} else {
							configMap.put("defaultValue", new String[]{});
						}
					} else {
						if(havaFlag == 1) {
							configMap.put("defaultValue", userInfo.getUserId());
						} else {
							configMap.put("defaultValue", null);
						}
					}
				} else {
					String multiple = String.valueOf(itemMap.get("multiple"));
					if ("true".equals(multiple)) {
						if(havaFlag == 1) {
							configMap.put("defaultValue", new String[]{userInfo.getUserId()});
						} else {
							configMap.put("defaultValue", new String[]{});
						}
					} else {
						if(havaFlag == 1) {
							configMap.put("defaultValue", userInfo.getUserId());
						} else {
							configMap.put("defaultValue", null);
						}
					}
				}
				itemMap.put("__config__", configMap);
				itemList.set(i, itemMap);
			} else if("depSelect".equals(jnpfKey) && "true".equals(defaultCurrent)) {
				String selectType = String.valueOf(itemMap.get("selectType"));
				String formId = String.valueOf(configMap.get("formId"));
				String vModel = String.valueOf(itemMap.get("__vModel__"));
				Integer inited = havaDefaultCurrentValue.get(formId + "____" + vModel);
				int havaFlag = 0;
				if(StringUtil.isEmpty(userInfo.getOrganizeId())) {
					havaFlag = 0;
				}else if(inited != null) {
					havaFlag = inited;
				}else {
					if ("all".equals(selectType)) {
						havaFlag = 1;
					} else if ("custom".equals(selectType)) {
						List<String> ableDepIds = (List<String>) itemMap.get("ableDepIds");
						if (ableDepIds != null && !ableDepIds.isEmpty() && ableDepIds.contains(userInfo.getDepartmentId())) {
							havaFlag = 1;
						} else if (ableDepIds != null && !ableDepIds.isEmpty()) {
							OrganizeConditionModel organizeConditionModel = new OrganizeConditionModel();
							organizeConditionModel.setDepartIds(ableDepIds);
							String departmentId = organizeService.getDefaultCurrentValueDepartmentId(organizeConditionModel);
							if(StringUtil.isNotEmpty(departmentId) && departmentId.equals(userInfo.getOrganizeId())) {
								havaFlag = 1;
							}else {
								havaFlag = 0;
							}
						} else {
							havaFlag = 0;
						}
					} else {
						havaFlag = 0;
					}
				}
				havaDefaultCurrentValue.put(formId + "____" + vModel, havaFlag);

				if("search".equals(parseFlag)) {
					String searchMultiple = String.valueOf(itemMap.get("searchMultiple"));
					if ("true".equals(searchMultiple)) {
						if(havaFlag == 1) {
							configMap.put("defaultValue", new String[]{userInfo.getOrganizeId()});
						} else {
							configMap.put("defaultValue", new String[]{});
						}
					} else {
						if(havaFlag == 1) {
							configMap.put("defaultValue", userInfo.getOrganizeId());
						} else {
							configMap.put("defaultValue", null);
						}
					}
				} else {
					String multiple = String.valueOf(itemMap.get("multiple"));
					if ("true".equals(multiple)) {
						if(havaFlag == 1) {
							configMap.put("defaultValue", new String[]{userInfo.getOrganizeId()});
						} else {
							configMap.put("defaultValue", new String[]{});
						}
					} else {
						if(havaFlag == 1) {
							configMap.put("defaultValue", userInfo.getOrganizeId());
						} else {
							configMap.put("defaultValue", null);
						}
					}
				}

				itemMap.put("__config__", configMap);
				itemList.set(i, itemMap);
			}else { //其他类型，暂不做处理

			}
		}
	}

}
