package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.GroupEntity;
import org.openea.eap.extj.permission.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class EapGroupService implements GroupService {
    @Override
    public GroupEntity getInfo(String va) {
        return null;
    }

    /**
     * 获取分组详情
     *
     * @param fullName
     * @param encode
     * @return
     */
    @Override
    public GroupEntity getInfo(String fullName, String encode) {
        return null;
    }

    /**
     * 添加
     *
     * @param entity
     */
    @Override
    public void crete(GroupEntity entity) {

    }

    /**
     * 修改
     *
     * @param id
     * @param entity
     */
    @Override
    public Boolean update(String id, GroupEntity entity) {
        return null;
    }

    /**
     * 删除
     *
     * @param entity
     */
    @Override
    public void delete(GroupEntity entity) {

    }

    /**
     * 判断名称是否重复
     *
     * @param fullName
     * @param id
     * @return
     */
    @Override
    public Boolean isExistByFullName(String fullName, String id) {
        return null;
    }

    /**
     * 判断编码是否重复
     *
     * @param enCode
     * @param id
     * @return
     */
    @Override
    public Boolean isExistByEnCode(String enCode, String id) {
        return null;
    }

    /**
     * 通过分组id获取分组集合
     *
     * @param list
     * @param filter
     * @return
     */
    @Override
    public List<GroupEntity> getListByIds(List<String> list, Boolean filter) {
        return null;
    }

    @Override
    public Map<String, Object> getGroupMap() {
        return null;
    }

    @Override
    public Map<String, Object> getGroupEncodeMap() {
        return null;
    }

    /**
     * 获取用户列表
     *
     * @param pagination 关键字
     * @return
     */
    @Override
    public List<GroupEntity> getList(Pagination pagination) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<GroupEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<GroupEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<GroupEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(GroupEntity entity) {
        return false;
    }

    @Override
    public GroupEntity getOne(Wrapper<GroupEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<GroupEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<GroupEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public List<GroupEntity> list() {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<GroupEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<GroupEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<GroupEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<GroupEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(GroupEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public GroupEntity getOneIgnoreLogic(Wrapper<GroupEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<GroupEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<GroupEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
