package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.model.DictionaryExportModel;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.exception.DataException;

import java.util.List;

/**
 * 字典数据
 *
 * todo eap待处理
 *
 */
public interface DictionaryDataService extends SuperService<DictionaryDataEntity> {

    /**
     * 列表
     *
     * @param dictionaryTypeId 字段分类id
     * @param enable 是否只看有效
     * @return ignore
     */
    List<DictionaryDataEntity> getList(String dictionaryTypeId, Boolean enable);

    /**
     * 列表
     *
     * @param dictionaryTypeId 类别主键
     * @return ignore
     */
    List<DictionaryDataEntity> getList(String dictionaryTypeId);
    /**
     * 列表
     *
     * @param dictionaryTypeId 类别主键(在线开发数据转换)
     * @return ignore
     */
    List<DictionaryDataEntity> getDicList(String dictionaryTypeId);
    /**
     * 列表
     *
     * @param dictionaryTypeId 类别主键(在线开发数据转换)
     * @return ignore
     */
    List<DictionaryDataEntity> geDicList(String dictionaryTypeId);
    /**
     * 列表
     *
     * @param parentId 父级id
     * @return ignore
     */
    Boolean isExistSubset(String parentId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    DictionaryDataEntity getInfo(String id);

    /**
     * 代码生成器数据字典转换
     * @param value encode 或者 id
     * @param dictionaryTypeId 类别
     * @return
     */
    DictionaryDataEntity getSwapInfo(String value,String dictionaryTypeId);
    /**
     * 验证名称
     *
     * @param dictionaryTypeId 类别主键
     * @param fullName         名称
     * @param id               主键值
     * @return ignore
     */
    boolean isExistByFullName(String dictionaryTypeId, String fullName, String id);

    /**
     * 验证编码
     *
     * @param dictionaryTypeId 类别主键
     * @param enCode           编码
     * @param id               主键值
     * @return ignore
     */
    boolean isExistByEnCode(String dictionaryTypeId, String enCode, String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(DictionaryDataEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(DictionaryDataEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, DictionaryDataEntity entity);

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

    /**
     * 获取名称
     *
     * @param id 主键id集合
     * @return ignore
     */
    List<DictionaryDataEntity> getDictionName(List<String> id);

    /**
     * 导出数据
     *
     * @param id 主键
     * @return DownloadVO
     */
    DownloadVO exportData(String id);

    /**
     * 导入数据
     *
     * @param exportModel ignore
     * @return ignore
     * @throws DataException ignore
     */
    boolean importData(DictionaryExportModel exportModel) throws DataException;

}
