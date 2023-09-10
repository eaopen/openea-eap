package org.openea.eap.extj.engine.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.entity.FlowEngineVisibleEntity;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateJsonEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowPagination;
import org.openea.eap.extj.engine.model.flowengine.PaginationFlowEngine;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowtemplate.FlowExportModel;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateListVO;
import org.openea.eap.extj.engine.model.flowtemplate.FlowTemplateVO;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowJsonModel;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowTemplateJsonPage;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.engine.mapper.FlowTemplateMapper;
import org.openea.eap.extj.engine.service.FlowEngineVisibleService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.engine.service.FlowTemplateJsonService;
import org.openea.eap.extj.engine.service.FlowTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 流程引擎
 *
 *
 */
@Service
public class FlowTemplateServiceImpl extends SuperServiceImpl<FlowTemplateMapper, FlowTemplateEntity> implements FlowTemplateService {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowEngineVisibleService flowEngineVisibleService;
    @Autowired
    private FlowTemplateJsonService flowTemplateJsonService;

    @Override
    public List<FlowTemplateEntity> getPageList(FlowPagination pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        UserInfo userInfo = userProvider.get();
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        if (!userInfo.getIsAdministrator()) {
            List<FlowEngineVisibleEntity> visibleFlowList = flowEngineVisibleService.getVisibleFlowList(userInfo.getUserId(), "2");
            List<String> tempId = visibleFlowList.stream().map(FlowEngineVisibleEntity::getFlowId).collect(Collectors.toList());
            tempId.addAll(flowTemplateJsonService.getMonitorList().stream().map(FlowTemplateJsonEntity::getTemplateId).collect(Collectors.toList()));
            if (tempId.size() == 0) {
                return new ArrayList<>();
            }
            queryWrapper.lambda().in(FlowTemplateEntity::getId, tempId);
        }
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().like(FlowTemplateEntity::getFullName, pagination.getKeyword());
        }
        if (StringUtil.isNotEmpty(pagination.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getCategory, pagination.getCategory());
        }
        //排序
        queryWrapper.lambda().orderByAsc(FlowTemplateEntity::getSortCode).orderByDesc(FlowTemplateEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(FlowTemplateEntity::getLastModifyTime);
        }
        Page<FlowTemplateEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<FlowTemplateEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTemplateEntity> getList(PaginationFlowEngine pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(pagination.getType())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getType, pagination.getType());
        }
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().like(FlowTemplateEntity::getFullName, pagination.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(pagination.getEnabledMark())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getEnabledMark, pagination.getEnabledMark());
        }
        //排序
        queryWrapper.lambda().orderByAsc(FlowTemplateEntity::getSortCode).orderByDesc(FlowTemplateEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(FlowTemplateEntity::getLastModifyTime);
        }
        queryWrapper.lambda().select(
                FlowTemplateEntity::getId, FlowTemplateEntity::getEnCode,
                FlowTemplateEntity::getFullName,
                FlowTemplateEntity::getType, FlowTemplateEntity::getIcon,
                FlowTemplateEntity::getCategory, FlowTemplateEntity::getIconBackground,
                FlowTemplateEntity::getSortCode, FlowTemplateEntity::getEnabledMark,
                FlowTemplateEntity::getCreatorTime, FlowTemplateEntity::getCreatorUserId
        );
        return this.list(queryWrapper);
    }

    @Override
    public FlowTemplateEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateEntity::getId, id);
        FlowTemplateEntity FlowTemplateEntity = this.getOne(queryWrapper);
        if (FlowTemplateEntity == null) {
            throw new WorkFlowException(MsgCode.WF113.get());
        }
        return FlowTemplateEntity;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateEntity::getFullName, fullName);
        queryWrapper.lambda().eq(FlowTemplateEntity::getType, 0);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(FlowTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateEntity::getEnCode, enCode);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(FlowTemplateEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    @DSTransactional
    public void create(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException {
        if (isExistByFullName(entity.getFullName(), entity.getId())) {
            throw new WorkFlowException("流程名称不能重复");
        }
        if (isExistByEnCode(entity.getEnCode(), entity.getId())) {
            throw new WorkFlowException("流程编码不能重复");
        }
        boolean formType = entity.getType() == 1;
        UserInfo userInfo = userProvider.get();
        entity.setId(StringUtil.isNotEmpty(entity.getId()) ? entity.getId() : RandomUtil.uuId());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setEnabledMark(0);
        this.saveOrUpdate(entity);
        List<String> tempIdList = new ArrayList() {{
            add(entity.getId());
        }};
        List<FlowTemplateJsonEntity> templateList = flowTemplateJsonService.getTemplateList(tempIdList);
        Set<String> formIdList = new HashSet<>();
        //保存引擎数据
        for (int k = 0; k < templateJsonList.size(); k++) {
            FlowTemplateJsonEntity templatejson = templateJsonList.get(k);
            templatejson.setId(RandomUtil.uuId());
            templatejson.setTemplateId(entity.getId());
            templatejson.setGroupId(StringUtil.isNotEmpty(templatejson.getGroupId()) ? templatejson.getGroupId() : RandomUtil.uuId());
            List<FlowEngineVisibleEntity> visibleList = visibleList(templatejson, formType, formIdList);
            flowEngineVisibleService.deleteVisible(templatejson.getId());
            int version = templateList.stream().filter(t -> t.getGroupId().equals(templatejson.getGroupId())).map(FlowTemplateJsonEntity::getVersion).mapToInt(Integer::parseInt).max().orElse(0) + 1;
            templatejson.setVersion(version + "");
            templatejson.setSortCode(Long.parseLong(k + ""));
            templatejson.setVisibleType(visibleList.size() == 0 ? 0 : 1);
            templatejson.setEnabledMark(1);
            flowTemplateJsonService.saveOrUpdate(templatejson);
            for (int i = 0; i < visibleList.size(); i++) {
                FlowEngineVisibleEntity visibleEntity = visibleList.get(i);
                visibleEntity.setId(RandomUtil.uuId());
                visibleEntity.setFlowId(templatejson.getId());
                visibleEntity.setSortCode(Long.parseLong(i + ""));
                flowEngineVisibleService.save(visibleEntity);
            }
        }
        if(!formType){
            serviceUtil.formIdList(new ArrayList<>(formIdList),entity.getId());
        }
        if (formType && formIdList.size() > 1) {
            throw new WorkFlowException("流程表单不一致，请重新选择。");
        }
    }

    @Override
    public void create(FlowTemplateEntity entity) {
        UserInfo userInfo = userProvider.get();
        entity.setId(StringUtil.isNotEmpty(entity.getId()) ? entity.getId() : RandomUtil.uuId());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(new Date());
        entity.setEnabledMark(0);
        this.saveOrUpdate(entity);
    }

    @Override
    public FlowTemplateInfoVO info(String id) throws WorkFlowException {
        FlowTemplateEntity flowEntity = getInfo(id);
        FlowTemplateInfoVO vo = JsonUtil.getJsonToBean(flowEntity, FlowTemplateInfoVO.class);
        List<String> idList = new ArrayList() {{
            add(id);
        }};
        FlowTemplateJsonPage page = new FlowTemplateJsonPage();
        page.setTemplateId(id);
        List<FlowTemplateJsonEntity> listAll = flowTemplateJsonService.getListPage(page, false);
        List<FlowTemplateJsonEntity> list = flowTemplateJsonService.getMainList(idList);
        List<FlowTaskEntity> flowTaskList = flowTaskService.getTemplateIdList(id);
        List<FlowJsonModel> templateList = new ArrayList<>();
        for (FlowTemplateJsonEntity templateJson : list) {
            FlowJsonModel model = new FlowJsonModel();
            model.setFlowId(templateJson.getId());
            model.setId(templateJson.getId());
            model.setFullName(templateJson.getFullName());
            model.setFlowTemplateJson(JsonUtil.stringToMap(templateJson.getFlowTemplateJson()));
            List<String> flowIdList = listAll.stream().filter(t -> templateJson.getGroupId().equals(t.getGroupId())).map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
            boolean isDelete = flowTaskList.stream().filter(t -> flowIdList.contains(t.getFlowId())).count() > 0;
            model.setIsDelete(isDelete);
            templateList.add(model);
        }
        //判断是否是在线开发同步过来的流程
        List<FlowFormEntity> formList = serviceUtil.getFlowIdList(id);
        String onlineFormId = "";
        if (formList.size() > 0) {
            FlowFormEntity form = formList.get(0);
            if (form != null && form.getFlowType() == 1 && form.getFormType() == 2) {
                onlineFormId = form.getId();
            }
        }
        vo.setOnlineDev(StringUtil.isNotEmpty(onlineFormId));
        vo.setOnlineFormId(onlineFormId);
        vo.setFlowTemplateJson(JsonUtil.getObjectToString(templateList));
        return vo;
    }

    @Override
    @DSTransactional
    public FlowTemplateVO updateVisible(String id, FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException {
        if (isExistByFullName(entity.getFullName(), id)) {
            throw new WorkFlowException("流程名称不能重复");
        }
        if (isExistByEnCode(entity.getEnCode(), id)) {
            throw new WorkFlowException("流程编码不能重复");
        }
        boolean formType = entity.getType() == 1;
        UserInfo userInfo = userProvider.get();
        FlowTemplateVO vo = new FlowTemplateVO();
        List<String> listVO = new ArrayList<>();
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userInfo.getUserId());
        this.saveOrUpdate(entity);
        //删除没有用到的流程
        List<String> tempIdList = new ArrayList() {{
            add(entity.getId());
        }};
        List<FlowTemplateJsonEntity> templateList = flowTemplateJsonService.getMainList(tempIdList);
        for (int i = 0; i < templateList.size(); i++) {
            FlowTemplateJsonEntity templateJson = templateList.get(i);
            List<FlowTaskEntity> flowList = flowTaskService.getFlowList(templateJson.getId());
            if (flowList.size() > 0) {
                templateList.remove(i);
            }
        }
        List<String> tempJsonIdList = templateList.stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
        List<String> templateJsonId = templateJsonList.stream().filter(t -> StringUtil.isNotEmpty(t.getId())).map(FlowTemplateJsonEntity::getId).collect(Collectors.toList());
        tempJsonIdList.removeAll(templateJsonId);
        for (String tempJsonId : tempJsonIdList) {
            FlowTemplateJsonEntity delTemplateJson = flowTemplateJsonService.getInfo(tempJsonId);
            flowEngineVisibleService.deleteVisible(delTemplateJson.getId());
            flowTemplateJsonService.delete(delTemplateJson);
        }
        Set<String> formIdList = new HashSet<>();
        //保存引擎数据
        for (int k = 0; k < templateJsonList.size(); k++) {
            FlowTemplateJsonEntity templateJson = templateJsonList.get(k);
            boolean isTempId = StringUtil.isNotEmpty(templateJson.getId());
            String jsonId = isTempId ? templateJson.getId() : RandomUtil.uuId();
            //json变化才新增版本，true=变化
            FlowTemplateJsonEntity info = isTempId ? flowTemplateJsonService.getInfo(jsonId) : null;
            boolean ischange = info != null && StringUtil.isNotEmpty(info.getFlowTemplateJson()) && !info.getFlowTemplateJson().equals(templateJson.getFlowTemplateJson());
            //判断流程任务是否被使用
            List<FlowTaskEntity> flowList = flowTaskService.getFlowList(jsonId);
            boolean isRand = flowList.size() > 0 && ischange;
            templateJson.setId(isRand ? RandomUtil.uuId() : jsonId);
            templateJson.setTemplateId(id);
            templateJson.setGroupId(isTempId ? info.getGroupId() : RandomUtil.uuId());
            int version = 1;
            //判断是否在使用,新增版本
            if (isRand) {
                List<String> templateId = new ArrayList() {{
                    add(id);
                }};
                version = flowTemplateJsonService.getTemplateList(templateId).stream().filter(t -> t.getGroupId().equals(templateJson.getGroupId())).map(FlowTemplateJsonEntity::getVersion).mapToInt(Integer::parseInt).max().orElse(0) + 1;
                listVO.add(templateJson.getId());
            }
            List<FlowEngineVisibleEntity> visibleList = visibleList(templateJson, formType, formIdList);
            flowEngineVisibleService.deleteVisible(templateJson.getId());
            templateJson.setVisibleType(visibleList.size() == 0 ? 0 : 1);
            templateJson.setSortCode(Long.parseLong(k + ""));
            templateJson.setEnabledMark(isRand ? 0 : isTempId ? info.getEnabledMark() : 1);
            templateJson.setVersion(isRand ? version + "" : isTempId ? info.getVersion() : version + "");
            flowTemplateJsonService.saveOrUpdate(templateJson);
            for (int i = 0; i < visibleList.size(); i++) {
                FlowEngineVisibleEntity visibleEntity = visibleList.get(i);
                visibleEntity.setId(RandomUtil.uuId());
                visibleEntity.setFlowId(templateJson.getId());
                visibleEntity.setSortCode(Long.parseLong(i + ""));
                flowEngineVisibleService.save(visibleEntity);
            }
            flowTemplateJsonService.updateFullName(templateJson.getGroupId(), templateJson.getFullName());
        }
        vo.setIsMainVersion(listVO.size() > 0);
        vo.setId(String.join(",", listVO));
        if(!formType){
            serviceUtil.formIdList(new ArrayList<>(formIdList),entity.getId());
        }
        if (formType && formIdList.size() > 1) {
            throw new WorkFlowException("流程表单不一致，请重新选择。");
        }
        return vo;
    }

    @Override
    public boolean update(String id, FlowTemplateEntity entity) {
        entity.setId(id);
        boolean flag = this.updateById(entity);
        return flag;
    }

    @Override
    @DSTransactional
    public void copy(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonEntity) throws WorkFlowException {
        try {
            this.create(entity, templateJsonEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WorkFlowException(MsgCode.WF103.get());
        }
    }

    @Override
    @DSTransactional
    public void delete(FlowTemplateEntity entity) throws WorkFlowException {
        FlowFormEntity form = serviceUtil.getForm(entity.getId());
        if (form != null && form.getFlowType() == 1 && form.getFormType() == 2) {
            throw new WorkFlowException("该流程由在线开发生成的，无法直接删除，请在功能设计中删除相关功能！");
        }
        List<FlowTaskEntity> flowTaskList = flowTaskService.getTemplateIdList(entity.getId());
        if (flowTaskList.size() > 0) {
            throw new WorkFlowException("该流程内工单任务流转未结束，无法删除！");
        }
        List<String> templateIdList = new ArrayList() {{
            add(entity.getId());
        }};
        List<FlowTemplateJsonEntity> list = flowTemplateJsonService.getTemplateList(templateIdList);
        this.removeById(entity.getId());
        serviceUtil.formIdList(new ArrayList<>(),entity.getId());
        for (FlowTemplateJsonEntity templateJson : list) {
            List<String> sendConfigIdList = flowTemplateJsonService.sendMsgConfigList(templateJson);
            if (sendConfigIdList != null && sendConfigIdList.size() > 0) {
                serviceUtil.updateSendConfigUsed(entity.getId(), sendConfigIdList);
            }
            serviceUtil.deleteFormId(templateJson.getId());
            flowEngineVisibleService.deleteVisible(templateJson.getId());
            flowTemplateJsonService.deleteFormFlowId(templateJson);
        }
    }

    @Override
    public FlowExportModel exportData(String id) throws WorkFlowException {
        FlowTemplateEntity entity = getInfo(id);
        List<FlowTemplateJsonEntity> templateJsonList = flowTemplateJsonService.getMainList(new ArrayList() {{
            add(id);
        }});
        List<String> flowIdList = new ArrayList() {{
            addAll(templateJsonList.stream().map(FlowTemplateJsonEntity::getId).collect(Collectors.toList()));
            add(entity.getId());
        }};
        List<FlowEngineVisibleEntity> visibleList = flowEngineVisibleService.getList(flowIdList);
        FlowExportModel model = new FlowExportModel();
        model.setVisibleList(visibleList);
        model.setTemplateEntity(entity);
        model.setTemplateJsonEntity(templateJsonList);
        return model;
    }

    @Override
    @DSTransactional
    public void ImportData(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList, List<FlowEngineVisibleEntity> visibleList) throws WorkFlowException {
        if (entity != null) {
            if (isExistByFullName(entity.getFullName(), null)) {
                throw new WorkFlowException("流程名称不能重复");
            }
            if (isExistByEnCode(entity.getEnCode(), null)) {
                throw new WorkFlowException("流程编码不能重复");
            }
            try {
                this.saveOrUpdateIgnoreLogic(entity);
                if (templateJsonList != null) {
                    for (int i = 0; i < templateJsonList.size(); i++) {
                        flowTemplateJsonService.saveOrUpdateIgnoreLogic(templateJsonList.get(i));
                    }
                }
                if (visibleList != null) {
                    for (int i = 0; i < visibleList.size(); i++) {
                        flowEngineVisibleService.saveOrUpdateIgnoreLogic(visibleList.get(i));
                    }
                }
            } catch (Exception e) {
                throw new WorkFlowException(MsgCode.IMP003.get());
            }
        }
    }

    @Override
    public List<FlowTemplateListVO> getTreeList(PaginationFlowEngine pagination, boolean isList) {
        List<FlowTemplateEntity> data = isList ? getList(pagination) : getFlowFormList();
        List<DictionaryDataEntity> dictionList = serviceUtil.getDiList();
        Map<String, List<FlowTemplateEntity>> dataList = data.stream().collect(Collectors.groupingBy(FlowTemplateEntity::getCategory));
        List<FlowTemplateListVO> listVOS = new LinkedList<>();
        for (DictionaryDataEntity entity : dictionList) {
            FlowTemplateListVO model = new FlowTemplateListVO();
            model.setFullName(entity.getFullName());
            model.setId(entity.getId());
            List<FlowTemplateEntity> childList = dataList.get(entity.getId()) != null ? dataList.get(entity.getId()) : new ArrayList<>();
            model.setNum(childList.size());
            if (childList.size() > 0) {
                model.setChildren(JsonUtil.getJsonToList(childList, FlowTemplateListVO.class));
            }
            listVOS.add(model);
        }
        return listVOS;
    }

    @Override
    public List<FlowTemplateEntity> getFlowFormList() {
        FlowPagination flowPagination = new FlowPagination();
        flowPagination.setFlowType(0);
        List<FlowTemplateEntity> data = getListAll(flowPagination, false);
        return data;
    }

    @Override
    public List<FlowTemplateEntity> getTemplateList(List<String> id) {
        List<FlowTemplateEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTemplateEntity::getId, id);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public FlowTemplateEntity getFlowIdByCode(String code) throws WorkFlowException {
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateEntity::getEnCode, code);
        FlowTemplateEntity FlowTemplateEntity = this.getOne(queryWrapper);
        if (FlowTemplateEntity == null) {
            throw new WorkFlowException(MsgCode.WF113.get());
        }
        return FlowTemplateEntity;
    }

    @Override
    public List<FlowTemplateEntity> getListAll(FlowPagination pagination, boolean isPage) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        UserInfo userInfo = userProvider.get();
        boolean visibleType = userInfo.getIsAdministrator();
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
       /* if (!isPage || !visibleType) {
            List<String> id = flowEngineVisibleService.getVisibleFlowList(userInfo.getUserId()).stream().map(FlowEngineVisibleEntity::getFlowId).collect(Collectors.toList());
            List<String> listAll = flowTemplateJsonService.getListAll(id).stream().map(FlowTemplateJsonEntity::getTemplateId).collect(Collectors.toList());
            if (listAll.size() == 0) {
                return new ArrayList<>();
            }
            queryWrapper.lambda().in(FlowTemplateEntity::getId, listAll);
        }*/
        if (ObjectUtil.isNotEmpty(pagination.getEnabledMark())) {
            queryWrapper.lambda().eq(FlowTemplateEntity::getEnabledMark, pagination.getEnabledMark());
        }
        if (ObjectUtil.isNotEmpty(pagination.getFlowType())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getType, pagination.getFlowType());
        }
        if (ObjectUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().like(FlowTemplateEntity::getFullName, pagination.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(pagination.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getCategory, pagination.getCategory());
        }
        if (ObjectUtil.isNotEmpty(pagination.getTemplateIdList())) {
            queryWrapper.lambda().in(FlowTemplateEntity::getId, pagination.getTemplateIdList());
        }
        queryWrapper.lambda().orderByAsc(FlowTemplateEntity::getSortCode).orderByDesc(FlowTemplateEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(FlowTemplateEntity::getLastModifyTime);
        }
        queryWrapper.lambda().select(
                FlowTemplateEntity::getId, FlowTemplateEntity::getEnCode,
                FlowTemplateEntity::getFullName,
                FlowTemplateEntity::getType, FlowTemplateEntity::getIcon,
                FlowTemplateEntity::getCategory, FlowTemplateEntity::getIconBackground,
                FlowTemplateEntity::getCreatorUserId, FlowTemplateEntity::getSortCode,
                FlowTemplateEntity::getEnabledMark, FlowTemplateEntity::getCreatorTime
        );
        if (isPage) {
            Page<FlowTemplateEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
            IPage<FlowTemplateEntity> userPage = this.page(page, queryWrapper);
            return pagination.setData(userPage.getRecords(), page.getTotal());
        } else {
            return this.list(queryWrapper);
        }
    }

    private List<FlowEngineVisibleEntity> visibleList(FlowTemplateJsonEntity entity, boolean formType, Set<String> formIdList) throws WorkFlowException {
        List<FlowEngineVisibleEntity> visibleList = new ArrayList<>();
        String templeId = entity.getTemplateId();
        String formData = StringUtil.isNotEmpty(entity.getFlowTemplateJson()) ? entity.getFlowTemplateJson() : "{}";
        ChildNode childNode = JsonUtil.getJsonToBean(formData, ChildNode.class);
        Properties properties = childNode.getProperties();
        if (formType) {
            FlowFormEntity form = serviceUtil.getForm(properties.getFormId());
            if (form != null && StringUtil.isNotEmpty(form.getFlowId()) && !form.getFlowId().equals(templeId)) {
                throw new WorkFlowException("表单已被引用，请重新选择！");
            }
            //清空 原先绑定的功能表单流程id
            List<FlowFormEntity> flowIdList = serviceUtil.getFlowIdList(templeId);
            for (FlowFormEntity formEntity : flowIdList) {
                formEntity.setFlowId(null);
                serviceUtil.updateForm(formEntity);
            }
            if (form != null) {
                form.setFlowId(templeId);
                serviceUtil.updateForm(form);
            }
        }
        if (StringUtil.isNotEmpty(properties.getFormId())) {
            formIdList.add(properties.getFormId());
        }
        //可见的用户
        for (String user : properties.getInitiator()) {
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(user);
            visible.setOperatorType("user");
            visible.setType("1");
            visibleList.add(visible);
        }
        //可见的岗位
        for (String position : properties.getInitiatePos()) {
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(position);
            visible.setOperatorType("position");
            visible.setType("1");
            visibleList.add(visible);
        }
        //可见的角色
        for (String role : properties.getInitiateRole()) {
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(role);
            visible.setOperatorType("role");
            visible.setType("1");
            visibleList.add(visible);
        }
        //20220601添加功能
        //可见的部门
        for (String department : properties.getInitiateOrg()) {
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(department);
            visible.setOperatorType("organize");
            visible.setType("1");
            visibleList.add(visible);
        }
        //可见的分组
        for (String group : properties.getInitiateGroup()) {
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(group);
            visible.setOperatorType("group");
            visible.setType("1");
            visibleList.add(visible);
        }
        return visibleList;
    }

    @Override
    public List<FlowTemplateEntity> getListByFlowIds(FlowPagination pagination, List<String> listAll, Boolean isAll, Boolean isPage, String userId) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<FlowTemplateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateEntity::getEnabledMark, 1);
        List<String> listIn = new ArrayList<>();
        List<String> listVisible = null;
        if (StringUtil.isNotEmpty(userId)) {//当用户不为空的时候【判断该用户的权限
            UserEntity userInfo = serviceUtil.getUserInfo(userId);
            boolean visibleType = "1".equals(userInfo.getIsAdministrator());
            if (!visibleType) {
                List<String> id = flowEngineVisibleService.getVisibleFlowList(userInfo.getId()).stream().map(FlowEngineVisibleEntity::getFlowId).collect(Collectors.toList());
                //可见列表
                listVisible = flowTemplateJsonService.getListAll(id).stream().map(FlowTemplateJsonEntity::getTemplateId).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(listVisible) && CollectionUtil.isNotEmpty(listAll)) {
                for (String str : listAll) {
                    if (listVisible.contains(str)) {
                        listIn.add(str);
                    }
                }
            } else if (isAll) {
                listIn = listVisible;
            }
        } else {//不判断用户权限
            listIn = listAll;
        }
        if (CollectionUtil.isEmpty(listIn) && !isAll) {
            return new ArrayList<>();
        }
        if (CollectionUtil.isNotEmpty(listIn)) {
            queryWrapper.lambda().in(FlowTemplateEntity::getId, listIn);
        }
        if (ObjectUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().like(FlowTemplateEntity::getFullName, pagination.getKeyword());
        }
        if (ObjectUtil.isNotEmpty(pagination.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getCategory, pagination.getCategory());
        }
        if (ObjectUtil.isNotEmpty(pagination.getFlowType())) {
            flag = true;
            queryWrapper.lambda().eq(FlowTemplateEntity::getType, pagination.getFlowType());
        }
        queryWrapper.lambda().orderByAsc(FlowTemplateEntity::getSortCode).orderByDesc(FlowTemplateEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(FlowTemplateEntity::getLastModifyTime);
        }
        queryWrapper.lambda().select(
                FlowTemplateEntity::getId, FlowTemplateEntity::getEnCode,
                FlowTemplateEntity::getFullName,
                FlowTemplateEntity::getType, FlowTemplateEntity::getIcon,
                FlowTemplateEntity::getCategory, FlowTemplateEntity::getIconBackground,
                FlowTemplateEntity::getCreatorUserId, FlowTemplateEntity::getSortCode,
                FlowTemplateEntity::getEnabledMark, FlowTemplateEntity::getCreatorTime
        );
        if (isPage) {
            Page<FlowTemplateEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
            IPage<FlowTemplateEntity> userPage = this.page(page, queryWrapper);
            return pagination.setData(userPage.getRecords(), page.getTotal());
        } else {
            return list(queryWrapper);
        }

    }


}
