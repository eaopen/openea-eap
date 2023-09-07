package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowCandidatesEntity;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.engine.mapper.FlowCandidatesMapper;
import org.openea.eap.extj.engine.service.FlowCandidatesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程候选人
 *
 * 
 */
@Service
public class FlowCandidatesServiceImpl extends SuperServiceImpl<FlowCandidatesMapper, FlowCandidatesEntity> implements FlowCandidatesService {

    @Override
    public List<FlowCandidatesEntity> getList(String taskNodeId) {
        QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowCandidatesEntity::getTaskNodeId, taskNodeId);
        return this.list(queryWrapper);
    }

    @Override
    public FlowCandidatesEntity getInfo(String id) {
        QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowCandidatesEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(FlowCandidatesEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void create(List<FlowCandidatesEntity> list) {
        for (FlowCandidatesEntity entity : list) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
        }
    }

    @Override
    public void update(String id, FlowCandidatesEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(FlowCandidatesEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowCandidatesEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteTaskNodeId(List<String> taskNodeId) {
        if (taskNodeId.size() > 0) {
            QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowCandidatesEntity::getTaskNodeId, taskNodeId);
            this.remove(queryWrapper);
        }
    }

    @Override
    public void delete(List<String> taskOperatorId) {
        if (taskOperatorId.size() > 0) {
            QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowCandidatesEntity::getOperatorId, taskOperatorId);
            this.remove(queryWrapper);
        }
    }

    @Override
    public void deleteTaskNodeId(List<String> taskNodeId, String type) {
        if (taskNodeId.size() > 0) {
            QueryWrapper<FlowCandidatesEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowCandidatesEntity::getTaskNodeId, taskNodeId);
            queryWrapper.lambda().eq(FlowCandidatesEntity::getType, type);
            this.remove(queryWrapper);
        }
    }
}
