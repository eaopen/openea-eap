package org.openea.eap.extj.form.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.form.entity.FlowFormRelationEntity;

import java.util.List;

/**
 * 流程表单关联
 *
 * 022/6/30 18:01
 */
public interface FlowFormRelationService extends SuperService<FlowFormRelationEntity> {
    /**
     * 根据流程id保存关联表单
     * @param
     * @return
     */
    void saveFlowIdByFormIds(String flowId,   List<String> formIds);
    /**
     * 根据表单id查询是否存在引用
     * @param
     * @return
     */
    List<FlowFormRelationEntity> getListByFormId(String formId);
}
