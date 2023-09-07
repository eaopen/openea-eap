package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.model.position.PaginationPosition;
import org.openea.eap.extj.permission.service.PositionService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class EapPositionService implements PositionService {
    @Override
    public PositionEntity getInfo(String pos) {
        return null;
    }

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    @Override
    public PositionEntity getByFullName(String fullName) {
        return null;
    }

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @param encode
     * @return
     */
    @Override
    public PositionEntity getByFullName(String fullName, String encode) {
        return null;
    }

    /**
     * 验证名称
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    @Override
    public boolean isExistByFullName(PositionEntity entity, boolean isFilter) {
        return false;
    }

    /**
     * 验证编码
     *
     * @param entity
     * @param isFilter 是否过滤
     * @return
     */
    @Override
    public boolean isExistByEnCode(PositionEntity entity, boolean isFilter) {
        return false;
    }

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    @Override
    public void create(PositionEntity entity) {

    }

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    @Override
    public boolean update(String id, PositionEntity entity) {
        return false;
    }

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    @Override
    public void delete(PositionEntity entity) {

    }

    /**
     * 上移
     *
     * @param id 主键值
     */
    @Override
    public boolean first(String id) {
        return false;
    }

    /**
     * 下移
     *
     * @param id 主键值
     */
    @Override
    public boolean next(String id) {
        return false;
    }

    /**
     * 获取名称
     *
     * @param id
     * @return
     */
    @Override
    public List<PositionEntity> getPositionName(List<String> id) {
        return null;
    }

    /**
     * 获取名称
     *
     * @param id
     * @param keyword
     * @return
     */
    @Override
    public List<PositionEntity> getPositionName(List<String> id, String keyword) {
        return null;
    }

    /**
     * 获取岗位列表
     *
     * @param organizeId 组织id
     * @return
     */
    @Override
    public List<PositionEntity> getListByOrganizeId(String organizeId) {
        return null;
    }

    /**
     * 获取用户组织底下所有的岗位
     *
     * @param organizeId
     * @param userId
     * @return
     */
    @Override
    public List<PositionEntity> getListByOrgIdAndUserId(String organizeId, String userId) {
        return null;
    }

    /**
     * 通过名称获取岗位列表
     *
     * @param fullName 岗位名称
     * @param enCode   编码
     * @return
     */
    @Override
    public List<PositionEntity> getListByFullName(String fullName, String enCode) {
        return null;
    }

    @Override
    public List<PositionEntity> getCurPositionsByOrgId(String orgId) {
        return null;
    }

    /**
     * 列表
     *
     * @return
     */
    @Override
    public List<PositionEntity> getList() {
        return null;
    }

    /**
     * 岗位名列表（在线开发）
     *
     * @param idList
     * @return
     */
    @Override
    public List<PositionEntity> getPosList(List<String> idList) {
        return null;
    }

    @Override
    public List<PositionEntity> getPosList(Set<String> posList) {
        return null;
    }

    @Override
    public Map<String, Object> getPosMap() {
        return null;
    }

    @Override
    public Map<String, Object> getPosEncodeAndName() {
        return null;
    }

    /**
     * 获取redis存储的岗位信息
     *
     * @return
     */
    @Override
    public List<PositionEntity> getPosRedisList() {
        return null;
    }

    /**
     * 列表
     *
     * @param paginationPosition 条件
     * @return
     */
    @Override
    public List<PositionEntity> getList(PaginationPosition paginationPosition) {
        return null;
    }

    /**
     * 列表
     *
     * @param userId 用户主键
     * @return
     */
    @Override
    public List<PositionEntity> getListByUserId(String userId) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<PositionEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<PositionEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<PositionEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(PositionEntity entity) {
        return false;
    }

    @Override
    public PositionEntity getOne(Wrapper<PositionEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<PositionEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<PositionEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<PositionEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<PositionEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<PositionEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<PositionEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(PositionEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public PositionEntity getOneIgnoreLogic(Wrapper<PositionEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<PositionEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<PositionEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
