package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorRecordEntity;
import org.openea.eap.extj.engine.enums.FlowRecordEnum;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.engine.enums.FlowNodeEnum;
import org.openea.eap.extj.engine.mapper.FlowTaskOperatorRecordMapper;
import org.openea.eap.extj.engine.service.FlowTaskOperatorRecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 流程经办
 *
 * 
 */
@Service
public class FlowTaskOperatorRecordServiceImpl extends SuperServiceImpl<FlowTaskOperatorRecordMapper, FlowTaskOperatorRecordEntity> implements FlowTaskOperatorRecordService {

    @Override
    public List<FlowTaskOperatorRecordEntity> getList(String taskId) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId).orderByAsc(FlowTaskOperatorRecordEntity::getHandleTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskOperatorRecordEntity> getRecordList(String taskId, List<Integer> handleStatus) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId);
        if (handleStatus.size() > 0) {
            queryWrapper.lambda().in(FlowTaskOperatorRecordEntity::getHandleStatus, handleStatus);
        }
        queryWrapper.lambda().orderByDesc(FlowTaskOperatorRecordEntity::getHandleTime);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskOperatorRecordEntity getInfo(String id) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(FlowTaskOperatorRecordEntity entity) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getId, entity.getId());
        this.remove(queryWrapper);
    }

    @Override
    public void create(FlowTaskOperatorRecordEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowTaskOperatorRecordEntity entity) {
        entity.setId(id);
        this.updateById(entity);
    }

    @Override
    public void updateStatus(Set<String> taskNodeId, String taskId) {
        if (taskNodeId.size() > 0) {
            UpdateWrapper<FlowTaskOperatorRecordEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(FlowTaskOperatorRecordEntity::getTaskNodeId, taskNodeId);
            updateWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId);
            updateWrapper.lambda().set(FlowTaskOperatorRecordEntity::getStatus, FlowRecordEnum.revoke.getCode());
            this.update(updateWrapper);
        }
    }

    @Override
    public FlowTaskOperatorRecordEntity getInfo(String taskId, String taskNodeId, String taskOperatorId) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId);
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskNodeId, taskNodeId);
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskOperatorId, taskOperatorId);
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getStatus, FlowNodeEnum.FreeApprover.getCode());
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getHandleStatus, FlowRecordEnum.audit.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public void updateStatus(List<String> idAll) {
        if (idAll.size() > 0) {
            UpdateWrapper<FlowTaskOperatorRecordEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(FlowTaskOperatorRecordEntity::getId, idAll);
            updateWrapper.lambda().set(FlowTaskOperatorRecordEntity::getStatus, FlowRecordEnum.revoke.getCode());
            this.update(updateWrapper);
        }
    }

    @Override
    public void update(String taskId) {
        UpdateWrapper<FlowTaskOperatorRecordEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskId, taskId);
        updateWrapper.lambda().set(FlowTaskOperatorRecordEntity::getStatus, FlowRecordEnum.revoke.getCode());
        this.update(updateWrapper);
    }

    @Override
    public FlowTaskOperatorRecordEntity getIsCheck(String taskOperatorId, Integer status) {
        QueryWrapper<FlowTaskOperatorRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getTaskOperatorId, taskOperatorId);
        queryWrapper.lambda().eq(FlowTaskOperatorRecordEntity::getStatus, status);
        return this.getOne(queryWrapper);
    }
}
