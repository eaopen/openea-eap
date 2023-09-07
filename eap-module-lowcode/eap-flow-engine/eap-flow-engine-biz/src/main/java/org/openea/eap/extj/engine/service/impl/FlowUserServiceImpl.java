package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowUserEntity;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.engine.mapper.FlowUserMapper;
import org.openea.eap.extj.engine.service.FlowUserService;
import org.springframework.stereotype.Service;

/**
 * 流程发起用户信息
 *
 * 
 */
@Service
public class FlowUserServiceImpl extends SuperServiceImpl<FlowUserMapper, FlowUserEntity> implements FlowUserService {

    @Override
    public FlowUserEntity getInfo(String id) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public FlowUserEntity getTaskInfo(String id) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getTaskId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(FlowUserEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowUserEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(FlowUserEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowUserEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }
}
