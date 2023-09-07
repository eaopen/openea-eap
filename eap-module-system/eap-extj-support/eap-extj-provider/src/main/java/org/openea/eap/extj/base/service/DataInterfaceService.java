package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.entity.DataInterfaceEntity;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceActionModel;
import org.openea.eap.extj.base.model.dataInterface.DataInterfacePage;
import org.openea.eap.extj.base.model.dataInterface.PaginationDataInterface;
import org.openea.eap.extj.exception.DataException;

import java.util.List;
import java.util.Map;

public interface DataInterfaceService extends SuperService<DataInterfaceEntity> {
    /**
     * 获取接口列表(分页)
     *
     * @param pagination 分页参数
     * @return ignore
     */
    List<DataInterfaceEntity> getList(PaginationDataInterface pagination, String dataType, boolean enabledMark);

    /**
     * 获取接口列表下拉框
     *
     * @return ignore
     * @param filterPage
     */
    List<DataInterfaceEntity> getList(boolean filterPage);

    /**
     * 获取接口数据
     *
     * @param id 主键
     * @return ignore
     */
    DataInterfaceEntity getInfo(String id);

    /**
     * 添加数据接口
     *
     * @param entity 实体
     */
    void create(DataInterfaceEntity entity);

    /**
     * 修改接口
     *
     * @param entity 实体
     * @param id     主键
     * @return 实体
     * @throws DataException ignore
     */
    boolean update(DataInterfaceEntity entity, String id) throws DataException;

    /**
     * 删除接口
     *
     * @param entity 实体
     */
    void delete(DataInterfaceEntity entity);

    /**
     * 判断接口名称是否重复
     *
     * @param fullName 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 判断编码是否重复
     *
     * @param fullName 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByEnCode(String fullName, String id);

    /**
     * 访问接口路径
     *
     * @param id 主键
     * @return ignore
     */
    ActionResult infoToId(String id);

    /**
     * 获取接口分页数据
     *
     * @param id   主键
     * @param page 分页参数
     * @return ignore
     */
    ActionResult infoToIdPageList(String id, DataInterfacePage page);

    /**
     * 获取接口详情数据
     *
     * @param id   主键
     * @param page 分页参数
     * @return ignore
     */
    List<Map<String, Object>> infoToInfo(String id, DataInterfacePage page);

    /**
     * 访问接口路径
     *
     * @param id       主键
     * @param tenantId 租户encode
     * @param map      需要替换的参数
     * @return ignore
     */
    ActionResult infoToId(String id, String tenantId, Map<String, String> map);

    /**
     * 任务调度使用
     * @param id       主键
     * @param tenantId 租户encode
     * @param map      需要替换的参数
     * @param token    token
     * @return
     */
    ActionResult infoToId(String id, String tenantId, Map<String, String> map, String token, String appId, String invokType, Pagination pagination, Map<String,Object> showMap);

    /**
     * 访问接口路径的应用认证
     *
     * @param id       主键
     * @param tenantId 租户encode
     * @param model      需要替换的参数
     * @return ignore
     */
    ActionResult infoToIdNew(String id, String tenantId, DataInterfaceActionModel model);

    /**
     * 检查参数
     * @param
     * @return
     */
    DataInterfaceActionModel checkParams( Map<String,String> map);

    /**
     * 预览语句
     *
     * @param id
     * @return
     */
    Object preview(String id);
}
