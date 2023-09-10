package org.openea.eap.extj.form.service;

import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserEntity;

import java.sql.SQLException;
import java.util.Map;

/**
 * 表单数据操作
 *
 *
 */
public interface FormDataService{

	/**
	 * 新增
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @param map 数据
	 * @return ignore
	 */
	void create(String formId, String id, Map<String, Object> map) throws Exception;

	/**
	 * 修改
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @param map 数据
	 * @return ignore
	 */
	void update(String formId, String id, Map<String, Object> map) throws WorkFlowException, SQLException, DataException;


	void saveOrUpdate(String formId, String id, Map<String, Object> map, UserEntity delegateUser) throws WorkFlowException;

	/**
	 * 删除
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @return ignore
	 */
	boolean delete(String formId, String id) throws Exception;

	/**
	 * 信息
	 *
	 * @param formId 表单id
	 * @param id     主键id
	 * @return ignore
	 */
	Map<String, Object> info(String formId, String id);

	/**
	 * 处理配置（默认当前登录值）
	 * @param configJson
	 * @param havaDefaultCurrentValue
	 * @return
	 */
	String setDefaultCurrentValue(String configJson, Map<String, Integer> havaDefaultCurrentValue);
}
