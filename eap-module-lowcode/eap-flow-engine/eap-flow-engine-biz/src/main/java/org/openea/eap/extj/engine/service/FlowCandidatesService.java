package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowCandidatesEntity;

import java.util.List;

/**
 * 流程候选人
 *
 * 
 */
public interface FlowCandidatesService extends SuperService<FlowCandidatesEntity> {

    /**
     * 列表
     *
     * @param taskNodeId 节点主键
     * @return
     */
    List<FlowCandidatesEntity> getList(String taskNodeId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowCandidatesEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowCandidatesEntity entity);

    /**
     * 创建
     *
     * @param list 实体对象
     */
    void create(List<FlowCandidatesEntity> list);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowCandidatesEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowCandidatesEntity entity);

    /**
     * 删除
     *
     * @param taskId
     */
    void deleteByTaskId(String taskId);

    /**
     * 拒绝删除候选人节点
     */
    void deleteTaskNodeId(List<String> taskNodeId);

    /**
     * 撤回删除候选人
     *
     * @param taskOperatorId 经办主键
     */
    void delete(List<String> taskOperatorId);

    /**
     * 撤回删除候选人节点
     */
    void deleteTaskNodeId(List<String> taskNodeId, String type);
}
