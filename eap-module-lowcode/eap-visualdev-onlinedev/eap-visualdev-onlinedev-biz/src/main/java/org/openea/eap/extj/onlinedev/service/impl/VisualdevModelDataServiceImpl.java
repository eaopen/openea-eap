package org.openea.eap.extj.onlinedev.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.VisualdevReleaseService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.*;
import org.openea.eap.extj.model.visualJson.analysis.FormModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.onlinedev.entity.VisualdevModelDataEntity;
import org.openea.eap.extj.onlinedev.mapper.VisualdevModelDataMapper;
import org.openea.eap.extj.onlinedev.model.OnlineDevListModel.VisualColumnSearchVO;
import org.openea.eap.extj.onlinedev.model.PaginationModelExport;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;
import org.openea.eap.extj.onlinedev.service.VisualDevListService;
import org.openea.eap.extj.onlinedev.service.VisualdevModelDataService;
import org.openea.eap.extj.onlinedev.util.OnlineDevDbUtil;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineDevInfoUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlinePublicUtils;
import org.openea.eap.extj.onlinedev.util.onlineDevUtil.OnlineSwapDataUtils;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.form.util.FlowFormDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisualdevModelDataServiceImpl extends SuperServiceImpl<VisualdevModelDataMapper, VisualdevModelDataEntity> implements VisualdevModelDataService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;
    @Autowired
    private VisualDevListService visualDevListService;
    @Autowired
    private OnlineSwapDataUtils onlineSwapDataUtils;
    @Autowired
    private OnlineDevDbUtil onlineDevDbUtil;
    @Autowired
    private FlowFormDataUtil flowFormDataUtil;


    @Override
    public List<VisualdevModelDataEntity> getList(String modelId) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getVisualDevId, modelId);
        return this.list(queryWrapper);
    }

    /**
     * 表单字段
     * @param id
     * @param filterType  过滤类型，0或者不传为默认过滤子表和关联表单，1-弹窗配置需要过滤掉的类型
     * @return
     */
    @Override
    public List<FormDataField> fieldList(String id, Integer filterType) {
        VisualdevReleaseEntity entity = visualdevReleaseService.getById(id);
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);

        List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<FieLdsModel> mainFieldModelList = new ArrayList<>();

        OnlinePublicUtils.recursionFields(mainFieldModelList,fieLdsModelList);
        //过滤掉无法传递的组件
        List<String> notInList=new ArrayList<>();
        notInList.add(JnpfKeyConsts.RELATIONFORM);
        notInList.add(JnpfKeyConsts.CHILD_TABLE);
        if (Objects.equals(filterType,1)) {
            notInList.add("link");
            notInList.add("button");
            notInList.add("JNPFText");
            notInList.add("alert");
            notInList.add(JnpfKeyConsts.QR_CODE);
            notInList.add(JnpfKeyConsts.BARCODE);
            notInList.add(JnpfKeyConsts.BILLRULE);
            notInList.add(JnpfKeyConsts.CREATEUSER);
            notInList.add(JnpfKeyConsts.CREATETIME);
            notInList.add(JnpfKeyConsts.UPLOADIMG);
            notInList.add(JnpfKeyConsts.UPLOADFZ);
            notInList.add(JnpfKeyConsts.MODIFYUSER);
            notInList.add(JnpfKeyConsts.MODIFYTIME);
        }

        List<FormDataField> formDataFieldList = mainFieldModelList.stream().filter(fieLdsModel ->
                !"".equals(fieLdsModel.getVModel())
                && StringUtil.isNotEmpty(fieLdsModel.getVModel())
                && !notInList.contains(fieLdsModel.getConfig().getJnpfKey())
        ).map(fieLdsModel -> {
            FormDataField formDataField = new FormDataField();
            formDataField.setLabel(fieLdsModel.getConfig().getLabel());
            formDataField.setVModel(fieLdsModel.getVModel());
            return formDataField;
        }).collect(Collectors.toList());

        return formDataFieldList;
    }

    @Override
    public List<Map<String, Object>> getPageList(VisualdevEntity entity, PaginationModel paginationModel) {
        String json = null;
        if (StringUtil.isNotEmpty(paginationModel.getKeyword())) {
            Map<String, Object> map = new HashMap<>();
            map.put(paginationModel.getRelationField(), paginationModel.getKeyword());
            json = JsonUtil.getObjectToString(map);
        }
        paginationModel.setQueryJson(json);
        VisualDevJsonModel visualJsonModel = OnlinePublicUtils.getVisualJsonModel(entity);

        //判断请求客户端来源
        String header = ServletUtil.getHeader("jnpf-origin");

        if (!"pc".equals(header)){
            visualJsonModel.setColumnData(visualJsonModel.getAppColumnData());
        }
        List<Map<String, Object>> dataList = visualDevListService.getRelationFormList(visualJsonModel, paginationModel);
        return dataList;
    }

    @Override
    public List<Map<String, Object>> exportData(String[] keys, PaginationModelExport paginationModelExport, VisualDevJsonModel visualDevJsonModel) {
        PaginationModel paginationModel =new PaginationModel();
        BeanUtil.copyProperties(paginationModelExport,paginationModel);
        List<String> keyList = Arrays.asList(keys);
        List<Map<String,Object>> noSwapDataList;
        ColumnDataModel columnDataModel = visualDevJsonModel.getColumnData();
        List<VisualColumnSearchVO> searchVOList = new ArrayList<>();
        List<TableModel> visualTables = visualDevJsonModel.getVisualTables();
        TableModel mainTable = visualTables.stream().filter(vi -> vi.getTypeId().equals("1")).findFirst().orElse(null);
        //封装查询条件
        if (StringUtil.isNotEmpty(paginationModel.getQueryJson())){
            Map<String, Object> keyJsonMap = JsonUtil.stringToMap(paginationModel.getQueryJson());
            searchVOList= JsonUtil.getJsonToList(columnDataModel.getSearchList(),VisualColumnSearchVO.class);
            searchVOList =	searchVOList.stream().map(searchVO->{
                searchVO.setValue(keyJsonMap.get(searchVO.getVModel()));
                return searchVO;
            }).filter(vo->vo.getValue()!=null && StringUtil.isNotEmpty(String.valueOf(vo.getValue()))).collect(Collectors.toList());
            //左侧树查询
            boolean b =false;
            if (columnDataModel.getTreeRelation()!=null){
                b = keyJsonMap.keySet().stream().anyMatch(t -> t.equalsIgnoreCase(String.valueOf(columnDataModel.getTreeRelation())));
            }
            if (b && keyJsonMap.size()>searchVOList.size()){
                String relation =String.valueOf(columnDataModel.getTreeRelation());
                VisualColumnSearchVO vo =new VisualColumnSearchVO();
                vo.setSearchType("1");
                vo.setVModel(relation);
                vo.setValue(keyJsonMap.get(relation));
                searchVOList.add(vo);
            }
        }
        //判断有无表
        List<VisualColumnSearchVO> searchVOS = new ArrayList<>();
        if (visualDevJsonModel.getVisualTables().size()>0){
            //当前用户信息
            UserInfo userInfo = userProvider.get();
            //菜单id
            String moduleId = paginationModel.getMenuId();
            for (VisualColumnSearchVO vo : searchVOList){
                String vModel = vo.getVModel();
                if (vModel.startsWith("tableField")){
                    vo.setField(vModel.substring(vModel.lastIndexOf("-")+1));
                    vo.setTable(vo.getConfig().getRelationTable());
                } else if (vModel.contains("jnpf_")){
                    String fieldName = vModel.substring(vModel.lastIndexOf("jnpf_" )).replace("jnpf_", "" );
                    String tableName = vModel.substring(vModel.indexOf("_" ) + 1, vModel.lastIndexOf("_jnpf" ));
                    vo.setTable(tableName);
                    vo.setField(fieldName);
                } else {
                    vo.setField(vModel);
                    vo.setTable(mainTable.getTable());
                }
                searchVOS.add(vo);
            }
            noSwapDataList =visualDevListService.getListWithTable(visualDevJsonModel,paginationModel,userInfo,moduleId,searchVOS,keyList);
        }else{
            noSwapDataList =visualDevListService.getWithoutTableData(visualDevJsonModel.getId());
            noSwapDataList = visualDevListService.getList(noSwapDataList, searchVOList, paginationModel);
        }

        //数据转换
        FormDataModel formDataModel = visualDevJsonModel.getFormData();
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(),FieLdsModel.class);
        List<FieLdsModel> fields = new ArrayList<>();
        OnlinePublicUtils.recursionFields(fields, fieLdsModels);
        noSwapDataList = onlineSwapDataUtils.getSwapList(noSwapDataList, fields,visualDevJsonModel.getId(),false,new ArrayList<>());

        return noSwapDataList;
    }


    @Override
    public VisualdevModelDataEntity getInfo(String id) {
        QueryWrapper<VisualdevModelDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevModelDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataException, SQLException {
        FormDataModel formDataModel = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> modelList = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);

        VisualdevModelDataEntity visualdevModelDataEntity = this.getInfo(id);

        List<FieLdsModel> childFieldModelList = new ArrayList<>();
        List<FieLdsModel> mainFieldModelList = new ArrayList<>();
        //二维码 条形码
        List<FormModel> models = new ArrayList<>();
        OnlinePublicUtils.recurseFiled(modelList, mainFieldModelList,childFieldModelList,models);
        mainFieldModelList=	mainFieldModelList.stream().map(fieLdsModel -> {
            if(ObjectUtil.isNotEmpty(fieLdsModel.getProps())){
                if (ObjectUtil.isNotEmpty(fieLdsModel.getProps().getProps())){
                    PropsBeanModel propsBeanModel = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
                    fieLdsModel.getProps().setPropsModel(propsBeanModel);
                }
            }
            return fieLdsModel;
        }).collect(Collectors.toList());

        if (visualdevModelDataEntity != null) {
            Map<String, Object>  DataMap = JsonUtil.stringToMap(visualdevModelDataEntity.getData());
            Map<String, Object> childTableMap = DataMap.entrySet().stream().filter(m -> m.getKey().contains("tableField"))
                .collect(Collectors.toMap((e) -> (String) e.getKey(),
                (e) -> ObjectUtil.isNotEmpty(e.getValue()) ? e.getValue() : ""));
            Map<String, Object> mainTableMap = DataMap.entrySet().stream().filter(m -> !m.getKey().contains("tableField"))
                .collect(Collectors.toMap((e) -> (String) e.getKey(),
                    (e) -> ObjectUtil.isNotEmpty(e.getValue()) ? e.getValue() : ""));
            mainTableMap = OnlineDevInfoUtils.swapTableDataInfo(mainFieldModelList, mainTableMap, visualdevEntity.getId(),models);

            for (Map.Entry<String,Object> entry : childTableMap.entrySet()){
                List<Map<String, Object>> listMap = JsonUtil.getJsonToListMap(String.valueOf(entry.getValue()));
                FieLdsModel fieLdsModel = childFieldModelList.stream().filter(child -> child.getVModel().equalsIgnoreCase(entry.getKey())).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(fieLdsModel)){
                    List<Map<String,Object>> tableValueList = new ArrayList<>();
                    if (Objects.nonNull(listMap)){
                        for (Map<String, Object> map : listMap){
                            Map<String,Object> childFieldMap  = OnlineDevInfoUtils.swapChildTableDataInfo(fieLdsModel.getConfig().getChildren(),map,models);
                            tableValueList.add(childFieldMap);
                        }
                    }
                    Map<String,Object> childFieldsMap = new HashMap<>();
                    childFieldsMap.put(entry.getKey(),tableValueList);
                    mainTableMap.putAll(childFieldsMap);
                }
            }
            String objectToString = JsonUtilEx.getObjectToString(mainTableMap);
            VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
            vo.setData(objectToString);
            vo.setId(id);
            return vo;
        }
        return null;
    }

    @Override
    public ActionResult visualCreate(VisualdevEntity visualdevEntity, DataModel dataModel) throws WorkFlowException {
        if (!StringUtil.isEmpty(visualdevEntity.getVisualTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            flowFormDataUtil.create(dataModel);
        } else {
            VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
            entity.setData(JsonUtilEx.getObjectToString(dataModel.getDataNewMap()));
            entity.setVisualDevId(visualdevEntity.getId());
            entity.setId(dataModel.getMainId());
            entity.setSortcode(RandomUtil.parses());
            entity.setCreatortime(new Date());
            entity.setCreatoruserid(userProvider.get().getUserId());
            entity.setEnabledmark(1);
            this.save(entity);
        }
        return ActionResult.success(MsgCode.SU001.get());
    }

    @Override
    public ActionResult visualUpdate(VisualdevEntity visualdevEntity, DataModel dataModel) throws WorkFlowException {
        if (StringUtil.isEmpty(visualdevEntity.getVisualTables()) || OnlineDevData.TABLE_CONST.equals(visualdevEntity.getVisualTables())) {
            VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
            entity.setData(JsonUtilEx.getObjectToString(dataModel.getDataNewMap()));
            entity.setVisualDevId(visualdevEntity.getId());
            entity.setId(dataModel.getMainId());
            entity.setLastModifyTime(new Date());
            entity.setLastmodifyuserid(userProvider.get().getUserId());
            this.updateById(entity);
        } else {
            flowFormDataUtil.update(dataModel);
        }
            return ActionResult.success(MsgCode.SU004.get());
    }


    @Override
    @DSTransactional
    public void delete(VisualdevModelDataEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean tableDelete(String id,VisualDevJsonModel visualDevJsonModel) throws Exception {
        DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());
        return onlineDevDbUtil.deleteTable(id, visualDevJsonModel, linkEntity);
    }

    @Override
    public ActionResult tableDeleteMore(List<String> ids, VisualDevJsonModel visualDevJsonModel) throws Exception {
        DbLinkEntity linkEntity = dblinkService.getInfo(visualDevJsonModel.getDbLinkId());
        return onlineDevDbUtil.deleteTables(ids, visualDevJsonModel, linkEntity);
    }

}
