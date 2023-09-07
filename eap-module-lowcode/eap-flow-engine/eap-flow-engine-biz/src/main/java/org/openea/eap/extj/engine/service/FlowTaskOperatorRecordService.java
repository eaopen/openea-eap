package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorRecordEntity;

import java.util.List;
import java.util.Set;

/**
 * 流程经办记录
 *
 * 
 */
public interface FlowTaskOperatorRecordService extends SuperService<FlowTaskOperatorRecordEntity> {

    /**
     * 列表
     *
     * @param taskId 流程实例Id
     * @return
     */
    List<FlowTaskOperatorRecordEntity> getList(String taskId);

    /**
     * 消息汇总列表
     *
     * @param taskId       流程实例Id
     * @param handleStatus 状态
     * @return
     */
    List<FlowTaskOperatorRecordEntity> getRecordList(String taskId, List<Integer> handleStatus);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowTaskOperatorRecordEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowTaskOperatorRecordEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     * @return
     */
    void create(FlowTaskOperatorRecordEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    void update(String id, FlowTaskOperatorRecordEntity entity);

    /**
     * 驳回流转记录状态
     *
     * @param taskNodeId 流程id
     * @param taskId     流程实例Id
     */
    void updateStatus(Set<String> taskNodeId, String taskId);

    /**
     * 通过3个id查询记录
     *
     * @param taskId
     * @param taskNodeId
     * @param taskOperatorId
     * @return
     */
    FlowTaskOperatorRecordEntity getInfo(String taskId, String taskNodeId, String taskOperatorId);

    /**
     * 更新撤回经办记录
     *
     * @param idAll 经办id
     */
    void updateStatus(List<String> idAll);

    /**
     * 更新驳回流程节点
     *
     * @param taskId 流程id
     */
    void update(String taskId);

    FlowTaskOperatorRecordEntity getIsCheck(String taskOperatorId, Integer status);
}
