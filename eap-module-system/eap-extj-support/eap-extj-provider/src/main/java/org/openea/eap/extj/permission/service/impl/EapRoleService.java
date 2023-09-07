package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.RoleEntity;
import org.openea.eap.extj.permission.model.role.RolePagination;
import org.openea.eap.extj.permission.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * todo eap
 */
@Service
public class EapRoleService implements RoleService {
    @Override
    public List<RoleEntity> getList() {
        return null;
    }

    /**
     * 获取全局角色集合
     *
     * @return 角色对象集合
     */
    @Override
    public List<RoleEntity> getGlobalList() {
        return null;
    }

    /**
     * 列表
     *
     * @param userId 用户ID
     * @return 角色对象集合
     */
    @Override
    public List<RoleEntity> getListByUserId(String userId) {
        return null;
    }

    @Override
    public RoleEntity getInfo(String roleId) {
        return null;
    }

    /**
     * 根据id集合返回角色对象集合
     *
     * @param roleIds 角色ID集合
     * @return 角色对象集合
     */
    @Override
    public List<RoleEntity> getListByIds(List<String> roleIds) {
        return null;
    }

    @Override
    public List<RoleEntity> getList(RolePagination page, Integer globalMark) {
        return null;
    }

    /**
     * 验证名称
     *
     * @param fullName   名称
     * @param id         主键值
     * @param globalMark
     */
    @Override
    public Boolean isExistByFullName(String fullName, String id, Integer globalMark) {
        return null;
    }

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     */
    @Override
    public Boolean isExistByEnCode(String enCode, String id) {
        return null;
    }

    /**
     * 判断当前组织下是否存在角色
     *
     * @param orgId 组织ID
     */
    @Override
    public Boolean existCurRoleByOrgId(String orgId) {
        return null;
    }

    @Override
    public void create(RoleEntity entity) {

    }

    @Override
    public Boolean update(String id, RoleEntity entity) {
        return null;
    }

    @Override
    public void delete(RoleEntity entity) {

    }

    /**
     * 组织底下所有角色
     *
     * @param userId
     * @param orgId
     * @return
     */
    @Override
    public List<RoleEntity> getListByUserIdAndOrgId(String userId, String orgId) {
        return null;
    }

    /**
     * 当前用户拥有的所有角色集合
     */
    @Override
    public List<String> getRoleIdsByCurrentUser() {
        return null;
    }

    /**
     * 当前用户拥有的所有角色集合
     *
     * @param orgId
     */
    @Override
    public List<String> getRoleIdsByCurrentUser(String orgId) {
        return null;
    }

    /**
     * 获取用户组织底下，及全局的
     *
     * @param userId
     * @param orgId
     * @return
     */
    @Override
    public List<String> getAllRoleIdsByUserIdAndOrgId(String userId, String orgId) {
        return null;
    }

    @Override
    public List<RoleEntity> getSwaptListByIds(Set<String> roleList) {
        return null;
    }

    @Override
    public Map<String, Object> getRoleMap() {
        return null;
    }

    @Override
    public Map<String, Object> getRoleNameAndIdMap() {
        return null;
    }

    /**
     * 获取角色实体
     *
     * @param fullName 角色名称
     * @return 角色对象
     */
    @Override
    public RoleEntity getInfoByFullName(String fullName) {
        return null;
    }

    /**
     * 获取角色实体
     *
     * @param fullName 角色名称
     * @param enCode
     * @return 角色对象
     */
    @Override
    public RoleEntity getInfoByFullName(String fullName, String enCode) {
        return null;
    }

    /**
     * 获取当前用户的默认组织下的所有角色集合
     *
     * @param orgId 组织ID
     * @return 角色对象集合
     */
    @Override
    public List<RoleEntity> getCurRolesByOrgId(String orgId) {
        return null;
    }

    /**
     * 获取组织下的所有角色
     *
     * @param orgId 组织ID
     * @return 角色对象集合
     */
    @Override
    public List<RoleEntity> getRolesByOrgId(String orgId) {
        return null;
    }

    @Override
    public String getBindInfo(String roleId, List<String> reduceOrgIds) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<RoleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<RoleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<RoleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(RoleEntity entity) {
        return false;
    }

    @Override
    public RoleEntity getOne(Wrapper<RoleEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<RoleEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<RoleEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<RoleEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<RoleEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<RoleEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<RoleEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(RoleEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public RoleEntity getOneIgnoreLogic(Wrapper<RoleEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<RoleEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<RoleEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
