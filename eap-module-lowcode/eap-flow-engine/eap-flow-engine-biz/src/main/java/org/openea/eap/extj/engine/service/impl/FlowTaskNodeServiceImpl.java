package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.engine.enums.FlowNodeEnum;
import org.openea.eap.extj.engine.mapper.FlowTaskNodeMapper;
import org.openea.eap.extj.engine.service.FlowTaskNodeService;
import org.openea.eap.extj.engine.util.FlowNature;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点
 *
 *
 */
@Service
public class FlowTaskNodeServiceImpl extends SuperServiceImpl<FlowTaskNodeMapper, FlowTaskNodeEntity> implements FlowTaskNodeService {

    @Override
    public List<FlowTaskNodeEntity> getListAll() {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(FlowTaskNodeEntity::getSortCode).orderByDesc(FlowTaskNodeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTaskNodeEntity> getList(String taskId) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId).orderByAsc(FlowTaskNodeEntity::getSortCode).orderByDesc(FlowTaskNodeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public FlowTaskNodeEntity getInfo(String id) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(FlowTaskNodeEntity entity) {
        this.save(entity);
    }

    @Override
    public void update(FlowTaskNodeEntity entity) {
        this.updateById(entity);
    }

    @Override
    public void update(String taskId) {
        UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(FlowTaskNodeEntity::getTaskId, taskId);
        wrapper.lambda().set(FlowTaskNodeEntity::getCompletion, FlowNodeEnum.Futility.getCode());
        wrapper.lambda().set(FlowTaskNodeEntity::getState, FlowNodeEnum.Futility.getCode());
        this.update(wrapper);
    }

    @Override
    public void updateCompletion(List<String> id, int start) {
        if (id.size() > 0) {
            UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
            wrapper.lambda().in(FlowTaskNodeEntity::getId, id);
            wrapper.lambda().set(FlowTaskNodeEntity::getCompletion, start);
            this.update(wrapper);
        }
    }

    @Override
    public void updateTaskNode(List<FlowTaskNodeEntity> taskNodeLis) {
        for (FlowTaskNodeEntity taskNodeLi : taskNodeLis) {
            String nodeNext = StringUtil.isNotEmpty(taskNodeLi.getNodeNext()) ? taskNodeLi.getNodeNext() : FlowNature.NodeEnd;
            UpdateWrapper<FlowTaskNodeEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(FlowTaskNodeEntity::getId, taskNodeLi.getId());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getNodeNext, nodeNext);
            updateWrapper.lambda().set(FlowTaskNodeEntity::getSortCode, taskNodeLi.getSortCode());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getState, taskNodeLi.getState());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getCandidates, taskNodeLi.getCandidates());
            updateWrapper.lambda().set(FlowTaskNodeEntity::getNodePropertyJson, taskNodeLi.getNodePropertyJson());
            this.update(updateWrapper);
        }
    }

    @Override
    public void updateTaskNodeCandidates(List<String> id) {
        if (id.size() > 0) {
            UpdateWrapper<FlowTaskNodeEntity> wrapper = new UpdateWrapper<>();
            wrapper.lambda().in(FlowTaskNodeEntity::getId, id);
            wrapper.lambda().set(FlowTaskNodeEntity::getCandidates, JsonUtil.getObjectToString(new ArrayList<>()));
            this.update(wrapper);
        }
    }

}
