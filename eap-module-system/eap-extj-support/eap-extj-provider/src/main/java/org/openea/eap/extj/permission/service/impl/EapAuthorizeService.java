package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.model.base.SystemBaeModel;
import org.openea.eap.extj.permission.entity.AuthorizeEntity;
import org.openea.eap.extj.permission.model.authorize.*;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapAuthorizeService implements AuthorizeService {
    @Override
    public <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel) {
        // TODO
        return null;
    }

    @Override
    public boolean getConditionSql(UserInfo userInfo, String moduleId, String mainTable, QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder from, List<OnlineDynamicSqlModel> sqlModelList) {
        return false;
    }

    @Override
    public List<SystemBaeModel> findSystem(List<String> roleIds) {
        return null;
    }

    @Override
    public void savePortalManage(String portalManageId, SaveAuthForm saveAuthForm) {

    }

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param userInfo 对象
     * @return
     */
    @Override
    public AuthorizeVO getAuthorize(UserInfo userInfo) throws Exception {
        return null;
    }

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param isCache 是否存在redis
     * @return
     */
    @Override
    public AuthorizeVO getAuthorize(boolean isCache) {
        return null;
    }

    /**
     * 创建
     *
     * @param objectId      对象主键
     * @param authorizeList 实体对象
     */
    @Override
    public void save(String objectId, AuthorizeDataUpForm authorizeList) {

    }

    /**
     * 创建
     *
     * @param saveBatchForm 对象主键
     * @param isBatch
     */
    @Override
    public void saveBatch(SaveBatchForm saveBatchForm, boolean isBatch) {

    }

    /**
     * 根据用户id获取列表
     *
     * @param isAdmin 是否管理员
     * @param userId  用户主键
     * @return
     */
    @Override
    public List<AuthorizeEntity> getListByUserId(boolean isAdmin, String userId) {
        return null;
    }

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    @Override
    public List<AuthorizeEntity> getListByObjectId(String objectId) {
        return null;
    }

    @Override
    public Boolean existByObjId(String objectId) {
        return null;
    }

    /**
     * 通过角色id集合获取系统
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<SystemBaeModel> systemListByRoleIds(List<String> roleIds) {
        return null;
    }

    @Override
    public List<AuthorizeEntity> getListByObjectId(String roleId, String authorizePortalManage) {
        return null;
    }

    /**
     * 根据对象Id获取列表
     *
     * @param itemId
     * @param objectType 对象主键
     * @return
     */
    @Override
    public List<AuthorizeEntity> getListByObjectAndItem(String itemId, String objectType) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<AuthorizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<AuthorizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<AuthorizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(AuthorizeEntity entity) {
        return false;
    }

    @Override
    public AuthorizeEntity getOne(Wrapper<AuthorizeEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<AuthorizeEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<AuthorizeEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<AuthorizeEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<AuthorizeEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<AuthorizeEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<AuthorizeEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(AuthorizeEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public AuthorizeEntity getOneIgnoreLogic(Wrapper<AuthorizeEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<AuthorizeEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<AuthorizeEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
