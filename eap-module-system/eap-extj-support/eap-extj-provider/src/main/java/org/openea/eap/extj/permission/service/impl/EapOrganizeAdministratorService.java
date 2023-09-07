package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.OrganizeAdministratorEntity;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.model.organizeadministrator.OrganizeAdministratorListVo;
import org.openea.eap.extj.permission.service.OrganizeAdministratorService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapOrganizeAdministratorService implements OrganizeAdministratorService {
    /**
     * 获取 机构分级管理员信息
     *
     * @param userId
     * @param organizeId
     * @return
     */
    @Override
    public OrganizeAdministratorEntity getOne(String userId, String organizeId) {
        return null;
    }

    /**
     * 根据userId获取列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<OrganizeAdministratorEntity> getOrganizeAdministratorEntity(String userId) {
        return null;
    }

    /**
     * 新建
     *
     * @param entity 实体对象
     */
    @Override
    public void create(OrganizeAdministratorEntity entity) {

    }

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    @Override
    public boolean update(String id, OrganizeAdministratorEntity entity) {
        return false;
    }

    /**
     * 删除
     *
     * @param userId 用户id
     */
    @Override
    public boolean deleteByUserId(String userId) {
        return false;
    }

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    @Override
    public void delete(OrganizeAdministratorEntity entity) {

    }

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     *
     * @param id 主键值
     * @return
     */
    @Override
    public OrganizeAdministratorEntity getInfo(String id) {
        return null;
    }

    /**
     * 获取 OrganizeAdminIsTratorEntity 信息
     *
     * @param organizeId 机构主键值
     * @return
     */
    @Override
    public OrganizeAdministratorEntity getInfoByOrganizeId(String organizeId) {
        return null;
    }

    /**
     * 获取 OrganizeAdminIsTratorEntity 列表
     *
     * @param organizeIdList 机构主键值
     * @return
     */
    @Override
    public List<OrganizeAdministratorEntity> getListByOrganizeId(List<String> organizeIdList) {
        return null;
    }

    /**
     * 获取二级管理员列表
     *
     * @param pagination 分页参数
     * @return
     */
    @Override
    public List<OrganizeAdministratorListVo> getList(Pagination pagination) {
        return null;
    }

    @Override
    public List<OrganizeEntity> getListByAuthorize() {
        return null;
    }

    @Override
    public List<OrganizeAdministratorEntity> getListByUserID(String userId) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<OrganizeAdministratorEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<OrganizeAdministratorEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<OrganizeAdministratorEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(OrganizeAdministratorEntity entity) {
        return false;
    }

    @Override
    public OrganizeAdministratorEntity getOne(Wrapper<OrganizeAdministratorEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<OrganizeAdministratorEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<OrganizeAdministratorEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<OrganizeAdministratorEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<OrganizeAdministratorEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<OrganizeAdministratorEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<OrganizeAdministratorEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(OrganizeAdministratorEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public OrganizeAdministratorEntity getOneIgnoreLogic(Wrapper<OrganizeAdministratorEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<OrganizeAdministratorEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<OrganizeAdministratorEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
