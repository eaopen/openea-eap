package org.openea.eap.module.visualdev.base.service.impl;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.model.VisualColumnSearchVO;
import org.openea.eap.module.visualdev.base.model.PaginationModel;
import org.openea.eap.module.visualdev.base.model.VisualDevJsonModel;
import org.openea.eap.module.visualdev.base.service.VisualDevListService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VisualDevListServiceImpl  implements VisualDevListService {


    @Override
    public List<Map<String, Object>> getWithoutTableData(String modelId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getListWithTable(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel, UserInfo userInfo, String moduleId, List<VisualColumnSearchVO> searchVOList, List<String> columnPropList) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getDataList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getDataListLink(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) throws WorkFlowException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getList(List<Map<String, Object>> list, List<VisualColumnSearchVO> searchVOList, PaginationModel paginationModel) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getRelationFormList(VisualDevJsonModel visualDevJsonModel, PaginationModel paginationModel) {
        return null;
    }
}

