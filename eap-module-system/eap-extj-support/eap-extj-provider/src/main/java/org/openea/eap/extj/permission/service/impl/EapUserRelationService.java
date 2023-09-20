package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.permission.model.permission.PermissionModel;
import org.openea.eap.extj.permission.model.userrelation.UserRelationForm;
import org.openea.eap.extj.permission.service.UserRelationService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapUserRelationService implements UserRelationService {
    @Override
    public void syncDingUserRelation(String sysObjId, List<Long> deptIdList) {

    }

    /**
     * 根据用户主键获取列表
     *
     * @param userId 用户主键
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByUserId(String userId) {
        return Collections.emptyList();
    }

    @Override
    public List<UserRelationEntity> getListByUserIdAndObjType(String userId, String objectType) {
        return null;
    }

    /**
     * 根据用户主键获取列表
     *
     * @param userId 用户主键
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByUserIdAll(List<String> userId) {
        return null;
    }

    /**
     * 根据对象主键获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByObjectId(String objectId) {
        return null;
    }

    /**
     * 根据对象主键获取列表
     *
     * @param objectType
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByObjectType(String objectType) {
        return null;
    }

    @Override
    public List<UserRelationEntity> getListByObjectId(String objectId, String objectType) {
        return null;
    }

    @Override
    public List<UserRelationEntity> getListByObjectIdAll(List<String> objectId) {
        return null;
    }

    /**
     * 根据对象主键删除数据
     *
     * @param objId 对象主键
     * @return
     */
    @Override
    public void deleteAllByObjId(String objId) {

    }

    /**
     * 删除用户所有的关联关系
     *
     * @param userId 用户ID
     */
    @Override
    public void deleteAllByUserId(String userId) {

    }

    /**
     * 保存用户关系
     *
     * @param userRelationEntityList 用户关系集合
     */
    @Override
    public void createByList(List<UserRelationEntity> userRelationEntityList) {

    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @Override
    public UserRelationEntity getInfo(String id) {
        return null;
    }

    /**
     * 创建
     *
     * @param objectId 对象主键
     * @param entitys  实体对象
     */
    @Override
    public void save(String objectId, List<UserRelationEntity> entitys) {

    }

    /**
     * 创建
     *
     * @param list 实体对象
     */
    @Override
    public void save(List<UserRelationEntity> list) {

    }

    /**
     * 删除
     *
     * @param ids 主键值
     */
    @Override
    public void delete(String[] ids) {

    }

    /**
     * 添加岗位或角色成员
     *
     * @param objectId
     * @param userRelationForm
     */
    @Override
    public void saveObjectId(String objectId, UserRelationForm userRelationForm) {

    }

    @Override
    public void roleSaveByUserIds(String roleId, List<String> userIds) {

    }

    /**
     * 获取用户组织/岗位/角色集合
     *
     * @param userId
     * @param objectType
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByObjectType(String userId, String objectType) {
        return null;
    }

    /**
     * 获取用户所有组织关系
     *
     * @param userId 用户id
     * @return 组织关系集合
     */
    @Override
    public List<UserRelationEntity> getAllOrgRelationByUserId(String userId) {
        return null;
    }

    /**
     * 获取个人信息页面用户组织/岗位/角色集合
     *
     * @param objectType 归属类型
     */
    @Override
    public List<PermissionModel> getObjectVoList(String objectType) {
        return null;
    }

    /**
     * 判断岗位/角色与用户是否存在关联关系
     *
     * @param objectType 类型
     * @param objectId   岗位/角色ID
     * @return 存在判断
     */
    @Override
    public Boolean existByObj(String objectType, String objectId) {
        return null;
    }

    /**
     * 获取用户组织关联关系，通过组织ID
     *
     * @param roleId
     */
    @Override
    public List<UserRelationEntity> getListByRoleId(String roleId) {
        return null;
    }

    @Override
    public List<UserRelationEntity> getListByUserId(String userId, String department) {
        return null;
    }

    /**
     * 判断组织下有哪些人
     *
     * @param orgIdList 组织id
     * @return
     */
    @Override
    public List<UserRelationEntity> getListByOrgId(List<String> orgIdList) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<UserRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<UserRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<UserRelationEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(UserRelationEntity entity) {
        return false;
    }

    @Override
    public UserRelationEntity getOne(Wrapper<UserRelationEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<UserRelationEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<UserRelationEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<UserRelationEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<UserRelationEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<UserRelationEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<UserRelationEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(UserRelationEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public UserRelationEntity getOneIgnoreLogic(Wrapper<UserRelationEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<UserRelationEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<UserRelationEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
