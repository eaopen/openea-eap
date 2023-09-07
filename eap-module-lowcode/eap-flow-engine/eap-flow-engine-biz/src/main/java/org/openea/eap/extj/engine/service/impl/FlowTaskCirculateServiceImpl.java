package org.openea.eap.extj.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowTaskCirculateEntity;
import org.openea.eap.extj.engine.mapper.FlowTaskCirculateMapper;
import org.openea.eap.extj.engine.service.FlowTaskCirculateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程传阅
 *
 * 
 */
@Service
public class FlowTaskCirculateServiceImpl extends SuperServiceImpl<FlowTaskCirculateMapper, FlowTaskCirculateEntity> implements FlowTaskCirculateService {

    @Override
    public void deleteByTaskId(String taskId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskId, taskId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteByNodeId(String nodeId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskNodeId, nodeId);
        this.remove(queryWrapper);
    }

    @Override
    public void create(List<FlowTaskCirculateEntity> entitys) {
        for (FlowTaskCirculateEntity entity : entitys) {
            this.save(entity);
        }
    }

    @Override
    public List<FlowTaskCirculateEntity> getList(String taskId) {
        QueryWrapper<FlowTaskCirculateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTaskCirculateEntity::getTaskId, taskId);
        return this.list(queryWrapper);
    }
}
