package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.entity.DictionaryTypeEntity;

import java.util.List;

public interface DictionaryTypeService extends SuperService<DictionaryTypeEntity>{

    /**
     * 列表
     *
     * @return ignore
     */
    List<DictionaryTypeEntity> getList();

    /**
     * 信息
     *
     * @param enCode 代码
     * @return ignore
     */
    DictionaryTypeEntity getInfoByEnCode(String enCode);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    DictionaryTypeEntity getInfo(String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(DictionaryTypeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, DictionaryTypeEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @return ignore
     */
    boolean delete(DictionaryTypeEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean next(String id);
}
