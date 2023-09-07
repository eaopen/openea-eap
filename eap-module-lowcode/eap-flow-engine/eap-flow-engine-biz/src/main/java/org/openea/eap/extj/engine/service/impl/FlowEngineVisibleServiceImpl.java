package org.openea.eap.extj.engine.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.engine.entity.FlowEngineVisibleEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowAssistModel;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.engine.mapper.FlowEngineVisibleMapper;
import org.openea.eap.extj.engine.service.FlowEngineVisibleService;
import org.openea.eap.extj.util.ServiceAllUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程可见
 *
 *
 */
@Service
public class FlowEngineVisibleServiceImpl extends SuperServiceImpl<FlowEngineVisibleMapper, FlowEngineVisibleEntity> implements FlowEngineVisibleService {

    @Autowired
    private ServiceAllUtil serviceUtil;

    @Override
    public List<FlowEngineVisibleEntity> getList(List<String> flowIdList) {
        List<FlowEngineVisibleEntity> list = new ArrayList<>();
        if (flowIdList.size() > 0) {
            QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowEngineVisibleEntity::getFlowId, flowIdList);
            queryWrapper.lambda().orderByAsc(FlowEngineVisibleEntity::getSortCode).orderByDesc(FlowEngineVisibleEntity::getCreatorTime);
            list.addAll(this.list(queryWrapper));
        }
        return list;
    }

    @Override
    public List<FlowEngineVisibleEntity> getList() {
        QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowEngineVisibleEntity> getVisibleFlowList(String userId) {
        return getVisibleFlowList(userId, "1");
    }

    @Override
    public List<FlowEngineVisibleEntity> getVisibleFlowList(String userId, String type) {
        List<String> userList = new ArrayList<>();
        userList.add(userId);
        List<UserRelationEntity> list = serviceUtil.getListByUserIdAll(userList);
        List<String> userRelationList = list.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
        userRelationList.add(userId);
        QueryWrapper<FlowEngineVisibleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(FlowEngineVisibleEntity::getOperatorId, userRelationList);
        wrapper.lambda().eq(FlowEngineVisibleEntity::getType, type);
        List<FlowEngineVisibleEntity> flowList = this.list(wrapper);
        return flowList;
    }

    @Override
    public void deleteVisible(String flowId) {
        QueryWrapper<FlowEngineVisibleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowEngineVisibleEntity::getFlowId, flowId);
        this.remove(queryWrapper);
    }

    @Override
    @DSTransactional
    public void assistList(FlowAssistModel assistModel) {
        List<String> tempId = new ArrayList() {{
            add(assistModel.getTemplateId());
        }};
        List<FlowEngineVisibleEntity> assistListAll = this.getList(tempId);
        for (FlowEngineVisibleEntity entity : assistListAll) {
            this.removeById(entity.getId());
        }
        List<String> list = assistModel.getList();
        List<FlowEngineVisibleEntity> visibleList = new ArrayList<>();
        for (String idAll : list) {
            String[] id = idAll.split("--");
            String operatorId = id[0];
            String type = id.length > 1 ? id[1] : "user";
            FlowEngineVisibleEntity visible = new FlowEngineVisibleEntity();
            visible.setOperatorId(operatorId);
            visible.setOperatorType(type);
            visible.setType("2");
            visibleList.add(visible);
        }
        for (int i = 0; i < visibleList.size(); i++) {
            FlowEngineVisibleEntity visibleEntity = visibleList.get(i);
            visibleEntity.setId(RandomUtil.uuId());
            visibleEntity.setFlowId(assistModel.getTemplateId());
            visibleEntity.setSortCode(Long.parseLong(i + ""));
            this.save(visibleEntity);
        }
    }
}
