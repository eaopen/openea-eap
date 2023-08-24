package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.entity.ModuleEntity;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.exception.DataException;

import java.util.List;

public interface ModuleService extends SuperService<ModuleEntity> {

    /**
     * 列表
     *
     * @return ignore
     */
    List<ModuleEntity> getList();

    /**
     * 列表
     *
     * @return ignore
     */
    List<ModuleEntity> getList(List<String> list);

    /**
     * 列表
     *
     * @param systemId 系统id
     * @param category
     * @param keyword
     * @param parentId
     * @return ignore
     */
    List<ModuleEntity> getList(String systemId, String category, String keyword, String type, String enabledMark, String parentId);

    /**
     * 通过id获取子菜单
     *
     * @param id 主键
     * @return ignore
     */
    List<ModuleEntity> getList(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ModuleEntity getInfo(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ModuleEntity getInfo(String id, String systemId);

    /**
     * 信息
     *
     * @param fullName 主键值
     * @return ignore
     */
    List<ModuleEntity> getInfoByFullName(String fullName, String systemId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ModuleEntity getInfo(String id, String systemId, String parentId);

    /**
     * 验证名称
     *
     * @param entity   ignore
     * @param category 分类
     * @param systemId 分类
     * @return ignore
     */
    boolean isExistByFullName(ModuleEntity entity, String category, String systemId);

    /**
     * 验证编码
     *
     * @param entity   实体
     * @param category 分类
     * @param systemId 分类
     * @return ignore
     */
    boolean isExistByEnCode(ModuleEntity entity, String category, String systemId);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ModuleEntity entity);

    /**
     * 删除
     *
     * @param systemId 实体对象
     */
    void deleteBySystemId(String systemId);


    /**
     * 删除权限（同步菜单 不处理数据权限）
     *
     * @param entity 实体对象
     */
    void deleteModule(ModuleEntity entity);
    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ModuleEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, ModuleEntity entity);

    /**
     * 导出数据
     *
     * @param id 主键
     * @return DownloadVO ignore
     */
    DownloadVO exportData(String id);

    /**
     * 导入数据
     *
     * @param exportModel 导出模型
     * @return ignore
     * @throws DataException ignore
     */
//    boolean importData(ModuleExportModel exportModel) throws DataException;

    /**
     * 功能设计发布功能自动创建app pc菜单
     * @return
     */
    List<ModuleEntity> getModuleList(String visualId);
}
