package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowTaskCirculateEntity;

import java.util.List;

/**
 * 流程传阅
 *
 *
 */
public interface FlowTaskCirculateService extends SuperService<FlowTaskCirculateEntity> {

    /**
     * 删除（根据实例Id）
     *
     * @param taskId 任务主键
     * @return
     */
    void deleteByTaskId(String taskId);

    /**
     * 删除
     *
     * @param nodeId 节点主键
     * @return
     */
    void deleteByNodeId(String nodeId);

    /**
     * 创建
     *
     * @param entitys 实体对象
     * @return
     */
    void create(List<FlowTaskCirculateEntity> entitys);

    /**
     * 获取列表
     *
     * @param taskId
     * @return
     */
    List<FlowTaskCirculateEntity> getList(String taskId);
}
