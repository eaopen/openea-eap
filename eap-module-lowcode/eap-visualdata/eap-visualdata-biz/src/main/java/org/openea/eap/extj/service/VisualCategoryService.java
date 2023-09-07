package org.openea.eap.extj.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.entity.VisualCategoryEntity;
import org.openea.eap.extj.model.VisualPagination;

import java.util.List;

/**
 * 大屏
 *
 *
 */
public interface VisualCategoryService extends SuperService<VisualCategoryEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return 大屏分类列表（分页）
     */
    List<VisualCategoryEntity> getList(VisualPagination pagination);

    /**
     * 列表
     *
     * @return 大屏分类列表
     */
    List<VisualCategoryEntity> getList();

    /**
     * 验证值
     *
     * @param value 名称
     * @param id    主键值
     * @return ignore
     */
    boolean isExistByValue(String value, String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    VisualCategoryEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(VisualCategoryEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, VisualCategoryEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualCategoryEntity entity);
}
