package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.exception.WorkFlowException;

/**
 * 在线开发工作流
 *
 *
 */
public interface FlowDynamicService {

    /**
     * 流程数据
     *
     * @param flowModel
     */
    void flowTask(FlowModel flowModel, FlowStatusEnum flowStatus, String formId) throws WorkFlowException;

    /**
     * 保存流程
     *
     * @param flowModel
     * @throws WorkFlowException
     */
    void createOrUpdate(FlowModel flowModel) throws WorkFlowException;

    /**
     * 批量保存流程
     *
     * @param flowModel
     * @throws WorkFlowException
     */
    void batchCreateOrUpdate(FlowModel flowModel) throws WorkFlowException;
}
