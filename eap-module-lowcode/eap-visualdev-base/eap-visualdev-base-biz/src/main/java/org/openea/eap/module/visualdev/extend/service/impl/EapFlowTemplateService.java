package org.openea.eap.module.visualdev.extend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.module.visualdev.extend.entity.FlowEngineVisibleEntity;
import org.openea.eap.module.visualdev.extend.entity.FlowTemplateEntity;
import org.openea.eap.module.visualdev.extend.entity.FlowTemplateJsonEntity;
import org.openea.eap.module.visualdev.extend.model.flowengine.FlowPagination;
import org.openea.eap.module.visualdev.extend.model.flowengine.PaginationFlowEngine;
import org.openea.eap.module.visualdev.extend.model.flowtemplate.FlowExportModel;
import org.openea.eap.module.visualdev.extend.model.flowtemplate.FlowTemplateInfoVO;
import org.openea.eap.module.visualdev.extend.model.flowtemplate.FlowTemplateListVO;
import org.openea.eap.module.visualdev.extend.model.flowtemplate.FlowTemplateVO;
import org.openea.eap.module.visualdev.extend.service.FlowTemplateService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapFlowTemplateService implements FlowTemplateService {
    @Override
    public boolean saveBatch(Collection<FlowTemplateEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<FlowTemplateEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<FlowTemplateEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(FlowTemplateEntity entity) {
        return false;
    }

    @Override
    public FlowTemplateEntity getOne(Wrapper<FlowTemplateEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<FlowTemplateEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<FlowTemplateEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public SuperMapper<FlowTemplateEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<FlowTemplateEntity> getEntityClass() {
        return null;
    }

    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<FlowTemplateEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<FlowTemplateEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean saveOrUpdateIgnoreLogic(FlowTemplateEntity var1) {
        return false;
    }

    @Override
    public FlowTemplateEntity getOneIgnoreLogic(Wrapper<FlowTemplateEntity> var1, boolean var2) {
        return null;
    }

    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<FlowTemplateEntity> var1) {
        return null;
    }

    @Override
    public <V> V getObjIgnoreLogic(Wrapper<FlowTemplateEntity> var1, Function<? super Object, V> var2) {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getPageList(FlowPagination pagination) {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getList(PaginationFlowEngine pagination) {
        return null;
    }

    @Override
    public FlowTemplateEntity getInfo(String id) throws WorkFlowException {
        return null;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        return false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        return false;
    }

    @Override
    public void create(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException {

    }

    @Override
    public void create(FlowTemplateEntity entity) {

    }

    @Override
    public FlowTemplateInfoVO info(String id) throws WorkFlowException {
        return null;
    }

    @Override
    public FlowTemplateVO updateVisible(String id, FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonList) throws WorkFlowException {
        return null;
    }

    @Override
    public boolean update(String id, FlowTemplateEntity entity) throws WorkFlowException {
        return false;
    }

    @Override
    public void copy(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJsonEntity) throws WorkFlowException {

    }

    @Override
    public void delete(FlowTemplateEntity entity) throws WorkFlowException {

    }

    @Override
    public FlowExportModel exportData(String id) throws WorkFlowException {
        return null;
    }

    @Override
    public void ImportData(FlowTemplateEntity entity, List<FlowTemplateJsonEntity> templateJson, List<FlowEngineVisibleEntity> visibleList) throws WorkFlowException {

    }

    @Override
    public List<FlowTemplateListVO> getTreeList(PaginationFlowEngine pagination, boolean isList) {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getFlowFormList() {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getTemplateList(List<String> id) {
        return null;
    }

    @Override
    public FlowTemplateEntity getFlowIdByCode(String code) throws WorkFlowException {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getListAll(FlowPagination pagination, boolean isPage) {
        return null;
    }

    @Override
    public List<FlowTemplateEntity> getListByFlowIds(FlowPagination pagination, List<String> listAll, Boolean isAll, Boolean isPage, String userId) {
        return null;
    }
}
