package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowCommentEntity;
import org.openea.eap.extj.engine.model.flowcomment.FlowCommentPagination;

import java.util.List;

/**
 * 流程评论
 *
 * 
 */
public interface FlowCommentService extends SuperService<FlowCommentEntity> {

    /**
     * 列表
     *
     * @param pagination 请求参数
     * @return
     */
    List<FlowCommentEntity> getlist(FlowCommentPagination pagination);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowCommentEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowCommentEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowCommentEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return
     */
    void delete(FlowCommentEntity entity);
}
