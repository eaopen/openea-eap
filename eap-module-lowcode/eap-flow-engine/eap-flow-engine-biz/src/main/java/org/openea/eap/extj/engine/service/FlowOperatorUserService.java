package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowOperatorUserEntity;

import java.util.List;
import java.util.Set;

/**
 * 流程依次审批
 *
 * 
 */
public interface FlowOperatorUserService extends SuperService<FlowOperatorUserEntity> {

    /**
     * 列表
     *
     * @param taskId 流程实例Id
     * @return
     */
    List<FlowOperatorUserEntity> getList(String taskId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowOperatorUserEntity getInfo(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowOperatorUserEntity getTaskInfo(String id);

    /**
     * 获取
     * @return
     */
    List<FlowOperatorUserEntity> getTaskList(String taskId, String taskNodeId);

    /**
     * 创建
     *
     * @param list 实体对象
     */
    void create(List<FlowOperatorUserEntity> list);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowOperatorUserEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowOperatorUserEntity entity);

    /**
     * 删除
     *
     * @param taskId
     */
    void deleteByTaskId(String taskId);

    /**
     * 驳回的节点之后审批人删除
     *
     * @param taskId
     * @param taskNodeId
     */
    void updateReject(String taskId, Set<String> taskNodeId);
}
