package org.openea.eap.module.visualdev.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.model.FormDataField;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.model.DataModel;
import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.base.entity.VisualdevModelDataEntity;
import org.openea.eap.module.visualdev.base.entity.VisualdevModelDataInfoVO;
import org.openea.eap.module.visualdev.base.model.PaginationModel;
import org.openea.eap.module.visualdev.base.service.VisualdevModelDataService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class VisualdevModelDataServiceImpl implements VisualdevModelDataService {
    @Override
    public boolean saveBatch(Collection<VisualdevModelDataEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<VisualdevModelDataEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<VisualdevModelDataEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(VisualdevModelDataEntity entity) {
        return false;
    }

    @Override
    public VisualdevModelDataEntity getOne(Wrapper<VisualdevModelDataEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<VisualdevModelDataEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<VisualdevModelDataEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public SuperMapper<VisualdevModelDataEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<VisualdevModelDataEntity> getEntityClass() {
        return null;
    }

    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<VisualdevModelDataEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<VisualdevModelDataEntity> var1, int var2) {
        return false;
    }

    @Override
    public boolean saveOrUpdateIgnoreLogic(VisualdevModelDataEntity var1) {
        return false;
    }

    @Override
    public VisualdevModelDataEntity getOneIgnoreLogic(Wrapper<VisualdevModelDataEntity> var1, boolean var2) {
        return null;
    }

    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<VisualdevModelDataEntity> var1) {
        return null;
    }

    @Override
    public <V> V getObjIgnoreLogic(Wrapper<VisualdevModelDataEntity> var1, Function<? super Object, V> var2) {
        return null;
    }

    @Override
    public List<FormDataField> fieldList(String id, Integer filterType) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getPageList(VisualdevEntity visualdevEntity, PaginationModel paginationModel) {
        return null;
    }

    @Override
    public VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataException, SQLException {
        return null;
    }

    @Override
    public ActionResult visualCreate(VisualdevEntity visualdevEntity, DataModel dataModel) throws WorkFlowException {
        return null;
    }
}
