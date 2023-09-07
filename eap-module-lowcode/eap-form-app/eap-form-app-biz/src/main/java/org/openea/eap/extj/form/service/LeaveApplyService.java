package org.openea.eap.extj.form.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.entity.LeaveApplyEntity;
import org.openea.eap.extj.form.model.leaveapply.LeaveApplyForm;

/**
 * 流程表单【请假申请】
 *
 *
 */
public interface LeaveApplyService extends SuperService<LeaveApplyEntity> {

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    LeaveApplyEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id     主键值
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void save(String id, LeaveApplyEntity entity, LeaveApplyForm form);

    /**
     * 提交
     *
     * @param id     主键值
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void submit(String id, LeaveApplyEntity entity, LeaveApplyForm form);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
