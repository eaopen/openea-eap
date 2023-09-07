package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowOperatorUserEntity;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.engine.mapper.FlowOperatorUserMapper;
import org.openea.eap.extj.engine.service.FlowOperatorUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 流程依次审批
 *
 * 
 */
@Service
public class FlowOperatorUserServiceImpl extends SuperServiceImpl<FlowOperatorUserMapper, FlowOperatorUserEntity> implements FlowOperatorUserService {

    @Override
    public List<FlowOperatorUserEntity> getList(String taskId) {
        return getTaskList(taskId, null);
    }

    @Override
    public FlowOperatorUserEntity getInfo(String id) {
        QueryWrapper<FlowOperatorUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowOperatorUserEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public FlowOperatorUserEntity getTaskInfo(String id) {
        QueryWrapper<FlowOperatorUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowOperatorUserEntity::getTaskId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<FlowOperatorUserEntity> getTaskList(String taskId, String taskNodeId) {
        QueryWrapper<FlowOperatorUserEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(taskId)) {
            queryWrapper.lambda().eq(FlowOperatorUserEntity::getTaskId, taskId);
        }
        if (StringUtil.isNotEmpty(taskNodeId)) {
            queryWrapper.lambda().eq(FlowOperatorUserEntity::getTaskNodeId, taskNodeId);
        }
        queryWrapper.lambda().orderByAsc(FlowOperatorUserEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public void create(List<FlowOperatorUserEntity> list) {
        for (FlowOperatorUserEntity entity : list) {
            entity.setId(RandomUtil.uuId());
            this.save(entity);
        }
    }

    @Override
    public void update(String id, FlowOperatorUserEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void delete(FlowOperatorUserEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowOperatorUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowOperatorUserEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void updateReject(String taskId, Set<String> taskNodeId) {
        if (taskNodeId.size() > 0) {
            QueryWrapper<FlowOperatorUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FlowOperatorUserEntity::getTaskId, taskId);
            queryWrapper.lambda().in(FlowOperatorUserEntity::getTaskNodeId, taskNodeId);
            this.remove(queryWrapper);
        }
    }
}
