package org.openea.eap.extj.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.entity.VisualConfigEntity;
import org.openea.eap.extj.entity.VisualEntity;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.model.visual.VisualPaginationModel;

import java.util.*;

/**
 * 大屏基本信息
 *
 * 
 */
public interface VisualService extends SuperService<VisualEntity> {

    /**
     * 列表
     *
     * @param pagination 条件
     * @return ignore
     */
    List<VisualEntity> getList(VisualPaginationModel pagination);

    /**
     * 列表
     *
     * @return ignore
     */
    List<VisualEntity> getList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    VisualEntity getInfo(String id);

    /**
     * 创建
     *
     * @param entity       实体对象
     * @param configEntity 配置属性
     */
    void create(VisualEntity entity, VisualConfigEntity configEntity);

    /**
     * 更新
     *
     * @param id           主键值
     * @param entity       实体对象
     * @param configEntity 配置属性
     * @return ignore
     */
    boolean update(String id, VisualEntity entity, VisualConfigEntity configEntity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(VisualEntity entity);

    /**
     * 创建
     *
     * @param entity       实体对象
     * @param configEntity 配置属性
     * @throws DataException ignore
     */
    void createImport(VisualEntity entity, VisualConfigEntity configEntity) throws DataException;

}
