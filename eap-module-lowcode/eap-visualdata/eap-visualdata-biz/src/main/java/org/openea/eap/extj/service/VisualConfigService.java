package org.openea.eap.extj.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.entity.VisualConfigEntity;

import java.util.List;

/**
 * 大屏基本配置
 *
 *
 */
public interface VisualConfigService extends SuperService<VisualConfigEntity> {

    /**
     * 信息
     *
     * @return ignore
     */
    List<VisualConfigEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    VisualConfigEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualConfigEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, VisualConfigEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualConfigEntity entity);
}
