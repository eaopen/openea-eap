package org.openea.eap.extj.onlinedev.service;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;

import java.util.List;
import java.util.Map;

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
