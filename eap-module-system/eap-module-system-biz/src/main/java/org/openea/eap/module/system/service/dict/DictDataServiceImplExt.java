package org.openea.eap.module.system.service.dict;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.datasource.EapDynamicDataSourceUtil;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import org.openea.eap.module.system.dal.dataobject.dict.DictDataDO;
import org.openea.eap.module.system.dal.dataobject.dict.DictTypeDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

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
                jsonItem.put(dictDataIndexField, i++);
                DictDataDO dictData = convertDictDataDo(jsonItem);
                if(dictData!=null){
                    dictDataList.add(dictData);
                }
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
            // todo sql 参数解析替换
            JdbcTemplate jdbcTemplate = EapDynamicDataSourceUtil.getJdbcTemplate(ds);
            // 是否限制最大数量？
            List<Map<String, Object>> rowData = jdbcTemplate.queryForList(sql);
            if(CollectionUtil.isNotEmpty(rowData)){
                int i = 0;
                for(Map row: rowData){
                    JSONObject jsonItem = new JSONObject(row);
                    jsonItem.put(dictDataIndexField, i++);
                    DictDataDO dictData = convertDictDataDo(jsonItem);
                    if(dictData!=null){
                        dictDataList.add(dictData);
                    }
                }
            }
        }catch (Throwable t){
            log.warn(t.getMessage());
        }finally {

        }
        return dictDataList;
    }

    public static String dictDataIndexField = "__index__";
    public static Map<String, String[]> mappingDictFields = new HashMap<>();
    static {
        // code/value
        mappingDictFields.put("value", new String[]{"code","value","value_","key","key_","F_EnCode"});
        // label
        mappingDictFields.put("label", new String[]{"label","label_","name","name_","F_FullName"});
        // enable/active
        mappingDictFields.put("enable", new String[]{"enable","active","active_"});
        // id
        mappingDictFields.put("id", new String[]{"id","id_","F_Id",dictDataIndexField});
        // sort
        mappingDictFields.put("sort", new String[]{"sort","sn","sn_",dictDataIndexField});
        // colorType
        mappingDictFields.put("colorType", new String[]{"colorType","color","buttonType","btn_type"});
        // cssClass
        mappingDictFields.put("cssClass", new String[]{"cssClass","class","style"});
    }

    protected DictDataDO convertDictDataDo(JSONObject jsonItem){
        DictDataDO dictData = new DictDataDO();
        // default
        dictData.setStatus(CommonStatusEnum.ENABLE.getStatus());
        BeanUtil.copyProperties(jsonItem, dictData);
        // check code/value
        String code = dictData.getValue();
        if(ObjectUtil.isEmpty(code)){
            code = checkDictMappingField("value", jsonItem);
            if(ObjectUtil.isNotEmpty(code)){
                dictData.setValue(code);
            }
        }
        jsonItem.remove("value");
        // check label
        String label = dictData.getLabel();
        if(ObjectUtil.isEmpty(label)){
            label = checkDictMappingField("label", jsonItem);
            if(ObjectUtil.isNotEmpty(label)){
                dictData.setLabel(label);
            }
        }
        jsonItem.remove("label");
        // check status  0-开启 1-关闭
        // enable/active  1-有效  0-无效
        String strEnable = checkDictMappingField("enable", jsonItem);
        if("false".equalsIgnoreCase(strEnable) || "0".equals(strEnable)){
            dictData.setStatus(CommonStatusEnum.DISABLE.getStatus());
        }
        jsonItem.remove("status");
        // id
        Long id = dictData.getId();
        if(id==null){
            String strId = checkDictMappingField("id", jsonItem);
            if(ObjectUtil.isNotEmpty(strId)){
                try{
                    id = Long.valueOf(strId);
                    dictData.setId(id);
                }catch (Throwable ignored){

                }
            }
        }
        // sort
        Integer sort = dictData.getSort();
        if(sort==null){
            String strSort = checkDictMappingField("sort", jsonItem);
            if(ObjectUtil.isNotEmpty(strSort)){
                try{
                    sort = Integer.valueOf(strSort);
                    dictData.setSort(sort);
                }catch (Throwable ignored){

                }
            }
        }
        jsonItem.remove("sort");
        jsonItem.remove(dictDataIndexField);
        // colorType
        String colorType = dictData.getColorType();
        if(ObjectUtil.isEmpty(colorType)){
            colorType = checkDictMappingField("colorType", jsonItem);
            if(ObjectUtil.isNotEmpty(colorType)){
                dictData.setColorType(colorType);
            }
        }
        jsonItem.remove("colorType");
        // cssClass
        String cssClass = dictData.getCssClass();
        if(ObjectUtil.isEmpty(cssClass)){
            cssClass = checkDictMappingField("cssClass", jsonItem);
            if(ObjectUtil.isNotEmpty(cssClass)){
                dictData.setCssClass(cssClass);
            }
        }
        jsonItem.remove("cssClass");
        // ext
        dictData.setExtendProps(jsonItem);
        return dictData;
    }
    private String checkDictMappingField(String field, JSONObject jsonItem){
        String fieldValue = null;
        if(mappingDictFields.containsKey(field)){
            String[] mappingFields = mappingDictFields.get(field);
            for(String field2: mappingFields){
                if(jsonItem.containsKey(field2)){
                    fieldValue = jsonItem.getString(field2);
                    if(!dictDataIndexField.equals(field2)){
                        jsonItem.remove(field2);
                    }
                    if(ObjectUtil.isNotEmpty(fieldValue)){
                        break;
                    }
                }
            }
        }else if(jsonItem.containsKey(field)){
            fieldValue = jsonItem.getString(field);
        }
        jsonItem.remove(field);
        return fieldValue;
    }

}
