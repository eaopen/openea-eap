package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.OrganizeRelationEntity;
import org.openea.eap.extj.permission.service.OrganizeRelationService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapOrganizeRelationService implements OrganizeRelationService {
    @Override
    public List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByOrganizeId(List<String> organizeIds, String objectType) {
        return null;
    }

    @Override
    public List<String> getPositionListByOrganizeId(List<String> organizeIds) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByRoleId(String roleId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByRoleIdList(List<String> roleId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByObjectIdAndType(String objectType, String objectId) {
        return null;
    }

    @Override
    public Boolean existByRoleIdAndOrgId(String roleId, String organizeId) {
        return null;
    }

    @Override
    public Boolean existByObjTypeAndOrgId(String objectType, String organizeId) {
        return null;
    }

    @Override
    public Boolean existByObjAndOrgId(String objectType, String objId, String organizeId) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getRelationListByType(String objectType) {
        return null;
    }

    @Override
    public List<OrganizeRelationEntity> getListByTypeAndOrgId(String objectType, String orgId) {
        return null;
    }

    @Override
    public Boolean deleteAllByRoleId(String roleId) {
        return null;
    }

    @Override
    public String autoGetMajorPositionId(String userId, String changeToMajorOrgId, String currentMajorPosId) {
        return null;
    }

    @Override
    public String autoGetMajorOrganizeId(String userId, List<String> orgIds, String currentMajorOrgId) {
        return null;
    }

    @Override
    public void autoSetOrganize(List<String> userIds) {

    }

    @Override
    public void autoSetPosition(List<String> userIds) {

    }

    @Override
    public Boolean checkBasePermission(String userId, String orgId) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<OrganizeRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<OrganizeRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<OrganizeRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(OrganizeRelationEntity entity) {
        return false;
    }

    @Override
    public OrganizeRelationEntity getOne(Wrapper<OrganizeRelationEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<OrganizeRelationEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<OrganizeRelationEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<OrganizeRelationEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<OrganizeRelationEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<OrganizeRelationEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<OrganizeRelationEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(OrganizeRelationEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public OrganizeRelationEntity getOneIgnoreLogic(Wrapper<OrganizeRelationEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<OrganizeRelationEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<OrganizeRelationEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
