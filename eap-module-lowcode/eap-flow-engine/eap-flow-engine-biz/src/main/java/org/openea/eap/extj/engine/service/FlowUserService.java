package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowUserEntity;

/**
 * 流程发起用户信息
 *
 * 
 */
public interface FlowUserService extends SuperService<FlowUserEntity> {

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowUserEntity getInfo(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowUserEntity getTaskInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowUserEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowUserEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowUserEntity entity);

    /**
     * 删除
     *
     * @param taskId
     */
    void deleteByTaskId(String taskId);
}
