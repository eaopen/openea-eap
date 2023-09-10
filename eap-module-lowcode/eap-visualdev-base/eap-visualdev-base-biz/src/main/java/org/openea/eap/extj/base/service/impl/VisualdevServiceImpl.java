package org.openea.eap.extj.base.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.mapper.VisualdevMapper;
import org.openea.eap.extj.base.model.PaginationVisualdev;
import org.openea.eap.extj.base.service.FilterService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.VisualdevReleaseService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.form.VisualTableModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.FormAllModel;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.form.util.ConcurrencyUtils;
import org.openea.eap.extj.form.util.VisualDevTableCre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**

 */
@Service
public class VisualdevServiceImpl extends SuperServiceImpl<VisualdevMapper, VisualdevEntity> implements VisualdevService {

    @Autowired
    private ConcurrencyUtils concurrencyVisualUtils;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private VisualDevTableCre visualDevTableCreUtil;
    @Autowired
    private ConcurrencyUtils concurrencyUtils;
    @Autowired
    private DbTableServiceImpl dbTableService;
    @Autowired
    private FilterService filterService;
    @Autowired
    private VisualdevReleaseService visualdevReleaseService;

    @Override
    public List<VisualdevEntity> getList(PaginationVisualdev paginationVisualdev) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isEmpty(paginationVisualdev.getKeyword())) {
            flag = true;
            queryWrapper.lambda().like(VisualdevEntity::getFullName, paginationVisualdev.getKeyword());
        }
        queryWrapper.lambda().eq(VisualdevEntity::getType, paginationVisualdev.getType());
        if (StringUtil.isNotEmpty(paginationVisualdev.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(VisualdevEntity::getCategory, paginationVisualdev.getCategory());
        }
        // 排序
        queryWrapper.lambda().orderByAsc(VisualdevEntity::getSortCode).orderByDesc(VisualdevEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(VisualdevEntity::getLastModifyTime);
        }
        Page<VisualdevEntity> page = new Page<>(paginationVisualdev.getCurrentPage(), paginationVisualdev.getPageSize());
        IPage<VisualdevEntity> userPage = this.page(page, queryWrapper);
        return paginationVisualdev.setData(userPage.getRecords(), page.getTotal());
    }


    @Override
    public List<VisualdevEntity> getList() {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        // 排序
        queryWrapper.lambda().orderByAsc(VisualdevEntity::getSortCode).orderByDesc(VisualdevEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualdevEntity getInfo(String id) {
        QueryWrapper<VisualdevEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualdevEntity::getId, id);
        return this.getOne(queryWrapper);
    }


    @Override
    public VisualdevEntity getReleaseInfo(String id) {
        VisualdevReleaseEntity visualdevReleaseEntity = visualdevReleaseService.getById(id);
        VisualdevEntity visualdevEntity = null;
        if(visualdevReleaseEntity != null){
            visualdevEntity = JsonUtil.getJsonToBean(visualdevReleaseEntity, VisualdevEntity.class);
        }
        if(visualdevEntity == null){
            visualdevEntity = getById(id);
        }
        return visualdevEntity;
    }

    @Override
    public Map<String,String> getTableMap(String formData){
        Map<String,String> tableMap  = new HashMap<>();
        if(StringUtil.isEmpty(formData)){
            return tableMap;
        }
        FormDataModel formDataModel = JsonUtil.getJsonToBean(formData, FormDataModel.class);
        String fields = formDataModel.getFields();
        List<FieLdsModel> list = JsonUtil.getJsonToList(fields, FieLdsModel.class);
        list.forEach(item->{
            this.solveTableName(item,tableMap);
        });
        return  tableMap;
    }

    private void solveTableName(FieLdsModel item, Map tableMap){
        ConfigModel config = item.getConfig();
        if(config!=null){
            List<FieLdsModel> children = config.getChildren();
            if("table".equals(config.getJnpfKey())){
                if(children!=null && children.size()>0 ){
                    FieLdsModel fieLdsModel = children.get(0);
                    String parentVModel = fieLdsModel.getConfig().getParentVModel();
                    String relationTable = fieLdsModel.getConfig().getRelationTable();
                    tableMap.put(parentVModel,relationTable);
                }
            }
            if(children!=null){
                children.forEach( item2 ->{
                    this.solveTableName(item2, tableMap);
                });
            }
        }
    }

    @Override
    @SneakyThrows
    public Boolean create(VisualdevEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }

        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);

        if (formDataModel != null) {
            //数据过滤信息
            Map<String, String> tableMap = this.getTableMap(entity.getFormData());
            // 保存app,pc过滤配置
            filterService.saveRuleList(entity.getId(),entity,1,1,tableMap);

            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            Boolean logicalDelete = formDataModel.getLogicalDelete();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();

            //判断是否要创表
            List<TableModel> tableModels = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);

            //有表
            if (tableModels.size() > 0) {
                List<TableModel> visualTables = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
                TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);
                //判断自增是否匹配
                concurrencyUtils.checkAutoIncrement(primaryKeyPolicy,entity.getDbLinkId(),visualTables);
                //在主表创建锁字段
                try {
                    if (logicalDelete && mainTable!=null) {
                        concurrencyUtils.creDeleteMark(mainTable.getTable(), entity.getDbLinkId());
                    }
                    if (concurrencyLock) {
                        concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                    }
                    if (primaryKeyPolicy == 2) {
                        concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                    }
                    concurrencyUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("创建锁字段失败");
                }
            }
        }
        entity.setCreatorUser(userProvider.get().getUserId());
        entity.setSortCode(RandomUtil.parses());
        return this.saveOrUpdateIgnoreLogic(entity);
    }

    @Override
    public boolean update(String id, VisualdevEntity entity) throws Exception {
        entity.setId(id);
        entity.setLastModifyUser(userProvider.get().getUserId());
        boolean b = this.updateById(entity);

        //代码生成修改时就要生成字段-做一些判断
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        if (formDataModel != null) {
            //是否开启安全锁
            Boolean concurrencyLock = formDataModel.getConcurrencyLock();
            Boolean logicalDelete = formDataModel.getLogicalDelete();
            int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
            //判断是否要创表
            List<TableModel> visualTables = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
            //有表
            if (visualTables.size() > 0) {
                if (formDataModel != null) {
                    try {
                        TableModel mainTable = visualTables.stream().filter(f -> f.getTypeId().equals("1" )).findFirst().orElse(null);
                        if (logicalDelete && mainTable != null) {
                            //在主表创建逻辑删除
                            concurrencyVisualUtils.creDeleteMark(mainTable.getTable(), entity.getDbLinkId());
                        }
                        if (concurrencyLock) {
                            //在主表创建锁字段
                            concurrencyVisualUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                        }
                        if (primaryKeyPolicy == 2) {
                            concurrencyVisualUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                        }
                        concurrencyVisualUtils.createFlowEngine(mainTable.getTable(), entity.getDbLinkId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //判断自增是否匹配
                concurrencyUtils.checkAutoIncrement(primaryKeyPolicy,entity.getDbLinkId(),visualTables);
            }
        }
        return b;
    }

    @Override
    public void delete(VisualdevEntity entity) throws WorkFlowException {
        if (entity != null) {
            //删除表单
            flowFormService.removeById(entity.getId());
            List<String> ids = new ArrayList<>();
            ids.add(entity.getId());
            this.removeByIds(ids);
        }
    }

    @Override
    public Long getObjByEncode(String encode, Integer type) {
        QueryWrapper<VisualdevEntity> visualWrapper = new QueryWrapper<>();
        visualWrapper.lambda().eq(VisualdevEntity::getEnCode, encode).eq(VisualdevEntity::getType, type);
        Long count = this.baseMapper.selectCount(visualWrapper);
        return count;
    }

    @Override
    public Long getCountByName(String name, Integer type) {
        QueryWrapper<VisualdevEntity> visualWrapper = new QueryWrapper<>();
        visualWrapper.lambda().eq(VisualdevEntity::getFullName, name).eq(VisualdevEntity::getType, type);
        Long count = this.baseMapper.selectCount(visualWrapper);
        return count;
    }

    @Override
    public void createTable(VisualdevEntity entity) throws WorkFlowException {
        boolean isTenant = false; //TenantDataSourceUtil.isTenantColumn();
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        //是否开启安全锁
        Boolean concurrencyLock = formDataModel.getConcurrencyLock();
        int primaryKeyPolicy = formDataModel.getPrimaryKeyPolicy();
        Boolean logicalDelete = formDataModel.getLogicalDelete();

        Map<String, Object> formMap = JsonUtil.stringToMap(entity.getFormData());
        List<FieLdsModel> list = JsonUtil.getJsonToList(formMap.get("fields"), FieLdsModel.class);
        JSONArray formJsonArray = JsonUtil.getJsonToJsonArray(String.valueOf(formMap.get("fields")));
        List<TableModel> visualTables = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);

        List<FormAllModel> formAllModel = new ArrayList<>();
        RecursionForm recursionForm = new RecursionForm();
        recursionForm.setTableModelList(visualTables);
        recursionForm.setList(list);
        FormCloumnUtil.recursionForm(recursionForm, formAllModel);

        String tableName = "mt" + RandomUtil.uuId();

        String dbLinkId = entity.getDbLinkId();
        VisualTableModel model = new VisualTableModel(formJsonArray, formAllModel, tableName, dbLinkId, entity.getFullName(), concurrencyLock, primaryKeyPolicy,logicalDelete);
        List<TableModel> tableModelList = visualDevTableCreUtil.tableList(model);

        if (formDataModel != null) {
            try {
                TableModel mainTable = tableModelList.stream().filter(f -> f.getTypeId().equals("1")).findFirst().orElse(null);
                if (logicalDelete && mainTable != null) {
                    //在主表创建逻辑删除
                    concurrencyUtils.creDeleteMark(mainTable.getTable(), entity.getDbLinkId());
                }
                if (concurrencyLock && mainTable != null) {
                    //在主表创建锁字段
                    concurrencyUtils.createVersion(mainTable.getTable(), entity.getDbLinkId());
                }
                if (formDataModel.getPrimaryKeyPolicy() == 2) {
                    concurrencyUtils.createFlowTaskId(mainTable.getTable(), entity.getDbLinkId());
                }
                if (isTenant) {
                    for (TableModel tableModel : visualTables) {
                        concurrencyUtils.createTenantId(tableModel.getTable(), entity.getDbLinkId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        formMap.put("fields", formJsonArray);
        //更新
        entity.setFormData(JsonUtil.getObjectToString(formMap));
        entity.setVisualTables(JsonUtil.getObjectToString(tableModelList));
    }

    @Override
    public Map<String, String> getTableNameToKey(String modelId) {
        Map<String, String> childKeyMap = new HashMap<>();
        VisualdevEntity info = this.getInfo(modelId);
        FormDataModel formDataModel = JsonUtil.getJsonToBean(info.getFormData(), FormDataModel.class);
        List<FieLdsModel> fieLdsModels = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> childFields = fieLdsModels.stream().filter(f -> JnpfKeyConsts.CHILD_TABLE.equals(f.getConfig().getJnpfKey())).collect(Collectors.toList());
        childFields.stream().forEach(c ->
                childKeyMap.put(c.getConfig().getTableName().toLowerCase(), c.getVModel())
        );
        return childKeyMap;
    }
    @Override
    public Boolean getPrimaryDbField(String linkId, String  table) throws Exception{
        DbTableFieldModel dbTableModel = dbTableService.getDbTableModel(linkId, table);
        List<DbFieldModel> data = dbTableModel.getDbFieldModelList();
        DbFieldModel dbFieldModel = data.stream().filter(DbFieldModel::getIsPrimaryKey).findFirst().orElse(null);
        if (dbFieldModel != null){
            return dbFieldModel.getIsAutoIncrement() != null && dbFieldModel.getIsAutoIncrement();
        }else {
            return null;
        }
    }
}
