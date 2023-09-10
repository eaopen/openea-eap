package org.openea.eap.extj.form.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.form.entity.FlowFormRelationEntity;
import org.openea.eap.extj.form.service.FlowFormRelationService;
import org.openea.eap.extj.form.mapper.FlowFormRelationMapper;
import org.openea.eap.extj.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程表单关联
 *
 * 
 */
@Service
public class FlowFormRelationServiceImpl extends SuperServiceImpl<FlowFormRelationMapper, FlowFormRelationEntity> implements FlowFormRelationService {

    @Override
    public void saveFlowIdByFormIds(String flowId, List<String> formIds) {
        QueryWrapper<FlowFormRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowFormRelationEntity::getFlowId, flowId);
        List<FlowFormRelationEntity> list = this.list(queryWrapper);
        this.removeBatchByIds(list);
        if(CollectionUtils.isNotEmpty(formIds)){
            for(String formId:formIds){
                FlowFormRelationEntity entity=new FlowFormRelationEntity();
                entity.setFlowId(flowId);
                entity.setId(RandomUtil.uuId());
                entity.setFormId(formId);
                this.save(entity);
            }
        }
    }

    @Override
    public List<FlowFormRelationEntity> getListByFormId(String formId) {
        QueryWrapper<FlowFormRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowFormRelationEntity::getFormId, formId);
        List<FlowFormRelationEntity> list = this.list(queryWrapper);
        return list;
    }
}
