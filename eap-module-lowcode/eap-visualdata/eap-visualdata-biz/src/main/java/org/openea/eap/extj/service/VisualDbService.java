package org.openea.eap.extj.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.entity.VisualDbEntity;
import org.openea.eap.extj.model.VisualPagination;

import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 * 
 */
public interface VisualDbService extends SuperService<VisualDbEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return ignore
     */
    List<VisualDbEntity> getList(VisualPagination pagination);

    /**
     * 列表
     *
     * @return ignore
     */
    List<VisualDbEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    VisualDbEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualDbEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, VisualDbEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualDbEntity entity);

    /**
     * 测试连接
     *
     * @param entity 实体对象
     * @return ignore
     */
    boolean dbTest(VisualDbEntity entity);

    /**
     * 执行sql
     *
     * @param entity 实体对象
     * @param sql    sql
     * @return ignore
     */
    List<Map<String, Object>> query(VisualDbEntity entity, String sql);
}
