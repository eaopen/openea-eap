package org.openea.eap.module.system.service.dict;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.datasource.EapDynamicDataSourceUtil;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import org.openea.eap.module.system.dal.dataobject.dict.DictDataDO;
import org.openea.eap.module.system.dal.dataobject.dict.DictTypeDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        if("sql".equals(dataType)){
            // todo 后续扩展分页支持
            List<DictDataDO> dictDataList = getDictDataListBySql(reqVO.getDictType());
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
        if("sql".equals(dataType)){
            return getDictDataListBySql(reqVO.getDictType());
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

    private static String[] codeFields = {"code","key","key_","value","value_","F_EnCode"};
    private static String[] labelFields = {"label","label_","name","name_","F_FullName"};

    private String getDictDataCode(Map dictRow){
        String code = null;
        for(String field: codeFields){
            code = MapUtil.getStr(dictRow, field);
            if(ObjectUtil.isNotEmpty(code)){
                break;
            }
        }
        return code;
    }

    private String getDictDataLabel(Map dictRow){
        String label = null;
        for(String field: labelFields){
            label = MapUtil.getStr(dictRow, field);
            if(ObjectUtil.isNotEmpty(label)){
                break;
            }
        }
        return label;
    }
    public List<DictDataDO> getDictDataListBySql(String dictType){
        List<DictDataDO> dictDataList = new ArrayList<>();
        try{
            DictTypeDO dictTypeDO = this.dictTypeService.getDictType(dictType);
            String ds = dictTypeDO.getDataDs();
            String sql = dictTypeDO.getDataSql();
            // todo sql 参数解析替换
            JdbcTemplate jdbcTemplate = EapDynamicDataSourceUtil.getJdbcTemplate(ds);
            // 是否限制最大数量？
            List<Map<String, Object>> rowData = jdbcTemplate.queryForList(sql);
            if(CollectionUtil.isNotEmpty(rowData)){
                for(Map row: rowData){
                    DictDataDO dictDataDO = new DictDataDO();
                    dictDataDO.setDictType(dictType);
                    // code
                    String code = getDictDataCode(row);
                    dictDataDO.setValue(code);
                    // label
                    String label = getDictDataLabel(row);
                    if(ObjectUtil.isEmpty(label)){
                        label = code;
                    }
                    dictDataDO.setLabel(label);
                    // enable/active
                    dictDataDO.setExtendProps(new JSONObject(row));
                    dictDataList.add(dictDataDO);
                }
            }
        }catch (Throwable t){
            log.warn(t.getMessage());
        }finally {

        }
        return dictDataList;
    }

}
