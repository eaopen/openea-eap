package org.openea.eap.extj.permission.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.ColumnsPurviewEntity;

/**
 * 模块列表权限业务类
 *
 */
public interface ColumnsPurviewService extends SuperService<ColumnsPurviewEntity> {

    /**
     * 通过moduleId获取列表权限
     *
     * @param moduleId
     * @return
     */
    ColumnsPurviewEntity getInfo(String moduleId);

    /**
     * 判断是保存还是编辑
     *
     * @param moduleId
     * @param entity
     * @return
     */
    boolean update(String moduleId, ColumnsPurviewEntity entity);
}
