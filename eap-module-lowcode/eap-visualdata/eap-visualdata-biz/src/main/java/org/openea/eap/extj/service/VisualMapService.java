package org.openea.eap.extj.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.entity.VisualMapEntity;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.openea.eap.extj.model.VisualPagination;

import java.util.List;

/**
 * 大屏地图配置
 *
 *
 */
public interface VisualMapService extends SuperService<VisualMapEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return ignore
     */
    List<VisualMapEntity> getList(VisualPagination pagination);

    /**
     * 列表
     * @param pagination 条件
     * @param columns 筛选字段
     * @return
     */
    List<VisualMapEntity> getListWithColnums(VisualPagination pagination, SFunction<VisualMapEntity, ?>... columns);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    VisualMapEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualMapEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, VisualMapEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualMapEntity entity);
}
