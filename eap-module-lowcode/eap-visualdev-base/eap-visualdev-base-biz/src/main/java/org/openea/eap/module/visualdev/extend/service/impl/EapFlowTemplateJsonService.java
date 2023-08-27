package org.openea.eap.module.visualdev.extend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.module.visualdev.extend.entity.FlowTemplateJsonEntity;
import org.openea.eap.module.visualdev.extend.model.flow.FlowTemplateJsonPage;
import org.openea.eap.module.visualdev.extend.model.flowengine.FlowPagination;
import org.openea.eap.module.visualdev.extend.service.FlowTemplateJsonService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapFlowTemplateJsonService implements FlowTemplateJsonService {
    @Override
    public boolean saveBatch(Collection<FlowTemplateJsonEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<FlowTemplateJsonEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<FlowTemplateJsonEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(FlowTemplateJsonEntity entity) {
        return false;
    }

    @Override
    public FlowTemplateJsonEntity getOne(Wrapper<FlowTemplateJsonEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<FlowTemplateJsonEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<FlowTemplateJsonEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public SuperMapper<FlowTemplateJsonEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<FlowTemplateJsonEntity> getEntityClass() {
        return null;
    }

    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<FlowTemplateJsonEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<FlowTemplateJsonEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean saveOrUpdateIgnoreLogic(FlowTemplateJsonEntity var1) {
        return false;
    }

    @Override
    public FlowTemplateJsonEntity getOneIgnoreLogic(Wrapper<FlowTemplateJsonEntity> var1, boolean var2) {
        return null;
    }

    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<FlowTemplateJsonEntity> var1) {
        return null;
    }

    @Override
    public <V> V getObjIgnoreLogic(Wrapper<FlowTemplateJsonEntity> var1, Function<? super Object, V> var2) {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getMonitorList() {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getTemplateList(List<String> id) {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getTemplateJsonList(List<String> id) {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getListPage(FlowTemplateJsonPage page, boolean isPage) {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getChildListPage(FlowPagination page) {
        return null;
    }

    @Override
    public List<FlowTemplateJsonEntity> getMainList(List<String> id) {
        return null;
    }

    @Override
    public FlowTemplateJsonEntity getInfo(String id) throws WorkFlowException {
        return null;
    }

    @Override
    public FlowTemplateJsonEntity getJsonInfo(String id) {
        return null;
    }

    @Override
    public void create(FlowTemplateJsonEntity entity) {

    }

    @Override
    public void update(String id, FlowTemplateJsonEntity entity) {

    }

    @Override
    public void delete(FlowTemplateJsonEntity entity) {

    }

    @Override
    public void deleteFormFlowId(FlowTemplateJsonEntity entity) {

    }

    @Override
    public List<FlowTemplateJsonEntity> getListAll(List<String> id) {
        return null;
    }

    @Override
    public void templateJsonMajor(String ids) throws WorkFlowException {

    }

    @Override
    public List<String> sendMsgConfigList(FlowTemplateJsonEntity engine) {
        return null;
    }

    @Override
    public void updateFullName(String groupId, String fullName) {

    }
}
