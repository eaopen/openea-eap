package org.openea.eap.extj.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.openea.eap.extj.base.entity.ModuleEntity;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.service.ModuleService;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class NullModuleService implements ModuleService {
    @Override
    public List<ModuleEntity> getList() {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleEntity> getList(List<String> list) {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleEntity> getList(String systemId, String category, String keyword, String type, String enabledMark, String parentId) {
        return Collections.emptyList();
    }

    @Override
    public List<ModuleEntity> getList(String id) {
        return Collections.emptyList();
    }

    @Override
    public ModuleEntity getInfo(String id) {
        return null;
    }

    @Override
    public ModuleEntity getInfo(String id, String systemId) {
        return null;
    }

    @Override
    public List<ModuleEntity> getInfoByFullName(String fullName, String systemId) {
        return Collections.emptyList();
    }

    @Override
    public ModuleEntity getInfo(String id, String systemId, String parentId) {
        return null;
    }

    @Override
    public boolean isExistByFullName(ModuleEntity entity, String category, String systemId) {
        return false;
    }

    @Override
    public boolean isExistByEnCode(ModuleEntity entity, String category, String systemId) {
        return false;
    }

    @Override
    public void delete(ModuleEntity entity) {

    }

    @Override
    public void deleteBySystemId(String systemId) {

    }

    @Override
    public void deleteModule(ModuleEntity entity) {

    }

    @Override
    public void create(ModuleEntity entity) {

    }

    @Override
    public boolean update(String id, ModuleEntity entity) {
        return false;
    }

    @Override
    public DownloadVO exportData(String id) {
        return null;
    }

    @Override
    public List<ModuleEntity> getModuleList(String visualId) {
        return Collections.emptyList();
    }

    @Override
    public boolean saveBatch(Collection<ModuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<ModuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<ModuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(ModuleEntity entity) {
        return false;
    }

    @Override
    public ModuleEntity getOne(Wrapper<ModuleEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<ModuleEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<ModuleEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public SuperMapper<ModuleEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<ModuleEntity> getEntityClass() {
        return null;
    }

    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<ModuleEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<ModuleEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean saveOrUpdateIgnoreLogic(ModuleEntity var1) {
        return false;
    }

    @Override
    public ModuleEntity getOneIgnoreLogic(Wrapper<ModuleEntity> var1, boolean var2) {
        return null;
    }

    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<ModuleEntity> var1) {
        return null;
    }

    @Override
    public <V> V getObjIgnoreLogic(Wrapper<ModuleEntity> var1, Function<? super Object, V> var2) {
        return null;
    }
}
