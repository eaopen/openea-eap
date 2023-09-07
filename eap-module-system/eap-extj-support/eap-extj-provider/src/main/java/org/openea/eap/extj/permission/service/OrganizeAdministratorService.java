package org.openea.eap.extj.permission.service;


import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.OrganizeAdministratorEntity;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.model.organizeadministrator.OrganizeAdministratorListVo;

import java.util.List;

/**
 *
 * 机构分级管理员
 *
 */
public interface OrganizeAdministratorService extends SuperService<OrganizeAdministratorEntity> {



    /**
     * 获取 机构分级管理员信息
     * @param userId
     * @param organizeId
     * @return
     */
    OrganizeAdministratorEntity getOne(String userId, String organizeId);

    /**
     * 根据userId获取列表
     * @param userId
     * @return
     */
    List<OrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId);

    /**
     * 新建
     * @param entity  实体对象
     */
    void create(OrganizeAdministratorEntity entity);

    /**
     * 更新
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, OrganizeAdministratorEntity entity);

    /**
     * 删除
     * @param userId 用户id
     */
    boolean deleteByUserId(String userId);

    /**
     * 删除
     * @param entity 实体对象
     */
    void delete(OrganizeAdministratorEntity entity);

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     * @param id 主键值
     * @return
     */
    OrganizeAdministratorEntity getInfo(String id);

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     * @param organizeId 机构主键值
     * @return
     */
    OrganizeAdministratorEntity getInfoByOrganizeId(String organizeId);

    /**
     * 获取 OrganizeAdminIsTratorEntity 列表
     * @param organizeIdList 机构主键值
     * @return
     */
    List<OrganizeAdministratorEntity> getListByOrganizeId(List<String> organizeIdList);

    /**
     * 获取二级管理员列表
     *
     * @param pagination 分页参数
     * @return
     */
    List<OrganizeAdministratorListVo> getList(Pagination pagination);

    List<OrganizeEntity> getListByAuthorize();

    /**
     * 获取
     * @param userID
     * @return
     */
    List<OrganizeAdministratorEntity> getListByUserID(String userID);
}
