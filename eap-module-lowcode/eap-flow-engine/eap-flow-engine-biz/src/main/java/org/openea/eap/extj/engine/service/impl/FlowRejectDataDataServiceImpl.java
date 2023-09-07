package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowRejectDataEntity;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.engine.mapper.FlowRejectDataMapper;
import org.openea.eap.extj.engine.service.FlowRejectDataService;
import org.springframework.stereotype.Service;

/**
 * 冻结审批
 *
 *
 */
@Service
public class FlowRejectDataDataServiceImpl extends SuperServiceImpl<FlowRejectDataMapper, FlowRejectDataEntity> implements FlowRejectDataService {

    @Override
    public void createOrUpdate(FlowRejectDataEntity rejectEntity) {
        this.saveOrUpdate(rejectEntity);
    }

    @Override
    public void create(FlowRejectDataEntity rejectEntity) {
        if(rejectEntity.getId()==null) {
            rejectEntity.setId(RandomUtil.uuId());
        }
        this.save(rejectEntity);
    }

    @Override
    public void update(String id, FlowRejectDataEntity rejectEntity) {
        rejectEntity.setId(id);
        this.updateById(rejectEntity);
    }

    @Override
    public FlowRejectDataEntity getInfo(String id) {
        QueryWrapper<FlowRejectDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowRejectDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

}
