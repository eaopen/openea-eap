package org.openea.eap.module.visualdev.base.util;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.model.ColumnDataModel;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.model.VisualColumnSearchVO;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.module.visualdev.base.entity.VisualdevReleaseEntity;
import org.openea.eap.module.visualdev.base.model.PaginationModel;
import org.openea.eap.module.visualdev.base.model.dataInterface.DataInterfaceModel;
import org.openea.eap.module.visualdev.base.model.dataInterface.DataInterfacePage;
import org.openea.eap.module.visualdev.base.service.DataInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class OnlineSwapDataUtils {

    @Autowired
    private DataInterfaceService dataInterfaceService;
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
                if (StringUtil.isNotEmpty(paginationModel.getQueryJson()) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataRes)) {
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
                    if (item.get("id" ) == null) {
                        item.put("id" , RandomUtil.uuId());
                    }
                });

                //排序
                String sort = StringUtil.isNotEmpty(paginationModel.getSort()) ? paginationModel.getSort() :
                        StringUtil.isNotEmpty(columnDataModel.getSort())?columnDataModel.getSort():"";
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
            log.error("数据视图，接口请求失败!message={}" , e.getMessage());
        }
        return realList;
    }
}
