package org.openea.eap.module.visualdev.onlinedev.service;

import org.openea.eap.module.visualdev.base.UserInfo;
import org.openea.eap.module.visualdev.base.model.VisualDevJsonModel;
import org.openea.eap.module.visualdev.exception.WorkFlowException;
import org.openea.eap.module.visualdev.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.module.visualdev.onlinedev.model.PaginationModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 列表临时接口
 *
 * @author JNPF开发平台组
 * @version V3.2.0
 * @copyright 引迈信息技术有限公司（https://www.jnpfsoft.com）
 * @date 2021/7/28
 */
@Service
public interface VisualDevListService {


	/**
	 * 无表数据
	 *
	 * @param modelId
	 * @return
	 */
	List<Map<String,Object>> getWithoutTableData(String modelId);

	/**
	 * 有表查询
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>>  getListWithTable(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel, UserInfo userInfo, String moduleId, List<VisualColumnSearchVO> searchVOList, List<String> columnPropList);

	/**
	 * 列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getDataList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException;

	/**
	 * 外链列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getDataListLink(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException;

	/**
	 * 无表数据处理
	 *
	 * @param list
	 * @param searchVOList
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getList(List<Map<String, Object>> list, List<VisualColumnSearchVO> searchVOList, PaginationModel paginationModel);

	/**
	 * 关联表单列表数据
	 *
	 * @param visualDevJsonModel
	 * @param paginationModel
	 * @return
	 */
	List<Map<String, Object>> getRelationFormList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel);
}
