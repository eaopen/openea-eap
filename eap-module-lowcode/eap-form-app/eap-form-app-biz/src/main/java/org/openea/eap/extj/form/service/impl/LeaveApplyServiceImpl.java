package org.openea.eap.extj.form.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.form.mapper.LeaveApplyMapper;
import org.openea.eap.extj.form.service.LeaveApplyService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.form.entity.LeaveApplyEntity;
import org.openea.eap.extj.form.model.leaveapply.LeaveApplyForm;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.ServiceAllUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 流程表单【请假申请】
 *
 *
 */
@Service
public class LeaveApplyServiceImpl extends SuperServiceImpl<LeaveApplyMapper, LeaveApplyEntity> implements LeaveApplyService {

    @Autowired
    private ServiceAllUtil serviceAllUtil;

    @Override
    public LeaveApplyEntity getInfo(String id) {
        QueryWrapper<LeaveApplyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LeaveApplyEntity::getId, id);
        return getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, LeaveApplyEntity entity, LeaveApplyForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            save(entity);
            serviceAllUtil.useBillNumber("WF_LeaveApplyNo");
        } else {
            entity.setId(id);
            updateById(entity);
        }
    }

    @Override
    @DSTransactional
    public void submit(String id, LeaveApplyEntity entity, LeaveApplyForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            save(entity);
            serviceAllUtil.useBillNumber("WF_LeaveApplyNo");
        } else {
            entity.setId(id);
            updateById(entity);
        }
    }

    @Override
    public void data(String id, String data) {
        LeaveApplyForm leaveApplyForm = JsonUtil.getJsonToBean(data, LeaveApplyForm.class);
        LeaveApplyEntity entity = JsonUtil.getJsonToBean(leaveApplyForm, LeaveApplyEntity.class);
        entity.setId(id);
        saveOrUpdate(entity);
    }

}
