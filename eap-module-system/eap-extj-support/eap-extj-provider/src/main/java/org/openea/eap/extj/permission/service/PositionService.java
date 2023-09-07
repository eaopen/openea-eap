package org.openea.eap.extj.permission.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.model.position.PaginationPosition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 岗位信息
 *
 *
 */
public interface PositionService extends SuperService<PositionEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<PositionEntity> getList();

    /**
     * 岗位名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<PositionEntity> getPosList(List<String> idList);


    /**
     * 岗位名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<PositionEntity> getPosList(Set<String> idList);

    Map<String,Object> getPosMap();

    Map<String,Object> getPosEncodeAndName();

    /**
     * 获取redis存储的岗位信息
     *
     * @return
     */
    List<PositionEntity> getPosRedisList();

    /**
     * 列表
     *
     * @param paginationPosition 条件
     * @return
     */
    List<PositionEntity> getList(PaginationPosition paginationPosition);

    /**
     * 列表
     *
     * @param userId 用户主键
     * @return
     */
    List<PositionEntity> getListByUserId(String userId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    PositionEntity getInfo(String id);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    PositionEntity getByFullName(String fullName);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    PositionEntity getByFullName(String fullName,String encode);

    /**
     * 验证名称
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    boolean isExistByFullName(PositionEntity entity, boolean isFilter);

    /**
     * 验证编码
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    boolean isExistByEnCode(PositionEntity entity, boolean isFilter);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(PositionEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, PositionEntity entity);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(PositionEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     */
    boolean next(String id);

    /**
     * 获取名称
     *
     * @return
     */
    List<PositionEntity> getPositionName(List<String> id);

    /**
     * 获取名称
     *
     * @return
     */
    List<PositionEntity> getPositionName(List<String> id, String keyword);

    /**
     * 获取岗位列表
     *
     * @param organizeId 组织id
     * @return
     */
    List<PositionEntity> getListByOrganizeId(String organizeId);

    /**
     * 获取用户组织底下所有的岗位
     * @param organizeId
     * @param userId
     * @return
     */
    List<PositionEntity> getListByOrgIdAndUserId(String organizeId, String userId);

    /**
     * 通过名称获取岗位列表
     *
     * @param fullName  岗位名称
     * @param enCode    编码
     * @return
     */
    List<PositionEntity> getListByFullName(String fullName, String enCode);

    List<PositionEntity> getCurPositionsByOrgId(String orgId);

}
