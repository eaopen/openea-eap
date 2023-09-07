package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowEngineVisibleEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowAssistModel;

import java.util.List;

/**
 * 流程可见
 *
 * 
 */
public interface FlowEngineVisibleService extends SuperService<FlowEngineVisibleEntity> {

    /**
     * 列表
     *
     * @param flowIdList 流程主键
     * @return
     */
    List<FlowEngineVisibleEntity> getList(List<String> flowIdList);

    /**
     * 列表
     *
     * @return
     */
    List<FlowEngineVisibleEntity> getList();

    /**
     * 可见流程列表
     *
     * @param userId 用户主键
     * @return
     */
    List<FlowEngineVisibleEntity> getVisibleFlowList(String userId);

    /**
     * 可见流程列表
     *
     * @param userId 用户主键
     * @return
     */
    List<FlowEngineVisibleEntity> getVisibleFlowList(String userId, String type);

    /**
     * 删除流程可见
     */
    void deleteVisible(String flowId);

    /**
     * 保存协管数据
     */
    void assistList(FlowAssistModel assistModel);
}
