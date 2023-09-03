package org.openea.eap.module.system.service.dict;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import org.openea.eap.module.system.dal.dataobject.dict.DictDataDO;
import org.openea.eap.module.system.dal.dataobject.dict.DictTypeDO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DictDataServiceImplExt extends DictDataServiceImpl implements DictDataService{

    @Override
    public PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO reqVO) {
        String dataType = reqVO.getDataType();
        if(ObjectUtil.isEmpty(dataType) || "data".equals(dataType)){
            return super.getDictDataPage(reqVO);
        }
        // json/sql/api
        if("json".equals(dataType)){
            List<DictDataDO> dictDataList = getDictDataListByJson(reqVO.getDictType());
            return new PageResult<>(dictDataList, 0L + dictDataList.size());
        }
        return new PageResult<>();
    }

    @Override
    public List<DictDataDO> getDictDataList(DictDataExportReqVO reqVO) {
        String dataType = reqVO.getDataType();
        if(ObjectUtil.isEmpty(dataType) || "data".equals(dataType)){
            return super.getDictDataList(reqVO);
        }
        // json/sql/api
        if("json".equals(dataType)){
            return getDictDataListByJson(reqVO.getDictType());
        }
        return Collections.emptyList();
    }


    public List<DictDataDO> getDictDataListByJson(String dictType){
        List<DictDataDO> dictDataList = new ArrayList<>();
        try{
            DictTypeDO dictTypeDO = this.dictTypeService.getDictType(dictType);
            JSONArray jsonArray = JSON.parseArray(dictTypeDO.getDataJson());
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                DictDataDO dictData = new DictDataDO();
                BeanUtil.copyProperties(jsonItem, dictData);
                // todo remove prop that dictData have
                dictData.setExtendProps(jsonItem);
                dictDataList.add(dictData);
            }
        }catch (Throwable t){
            log.warn(t.getMessage());
        }
        return dictDataList;
    }

    public List<DictDataDO> getDictDataListBySql(String dictType){
        List<DictDataDO> dictDataList = new ArrayList<>();
        try{
            DictTypeDO dictTypeDO = this.dictTypeService.getDictType(dictType);
            String ds = dictTypeDO.getDataDs();
            String sql = dictTypeDO.getDataSql();

        }catch (Throwable t){
            log.warn(t.getMessage());
        }
        return dictDataList;
    }

}
