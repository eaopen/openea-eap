package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowRejectDataEntity;

/**
 * 冻结审批
 *
 *
 */
public interface FlowRejectDataService extends SuperService<FlowRejectDataEntity> {

    /**
     * 新增
     * @param rejectEntity
     */
    void createOrUpdate(FlowRejectDataEntity rejectEntity);

    /**
     * 新增
     * @param rejectEntity
     */
    void create(FlowRejectDataEntity rejectEntity);

    /**
     * 更新
     * @param rejectEntity
     */
    void update(String id, FlowRejectDataEntity rejectEntity);

    /**
     * 获取信息
     * @param id
     * @return
     */
    FlowRejectDataEntity getInfo(String id);

}
