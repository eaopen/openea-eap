package org.openea.eap.extj.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openea.eap.extj.base.mapper.SuperMapper;
import org.springframework.transaction.annotation.Transactional;

public interface SuperService<T> extends IService<T> {
    SuperMapper<T> getBaseMapper();

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean saveOrUpdateBatchIgnoreLogic(Collection<T> entityList) {
        return this.saveOrUpdateBatchIgnoreLogic(entityList, 1000);
    }

    boolean saveOrUpdateBatchIgnoreLogic(Collection<T> var1, int var2);

    default boolean removeByIdIgnoreLogic(Serializable id) {
        return SqlHelper.retBool(this.getBaseMapper().deleteByIdIgnoreLogic(id));
    }

    default boolean removeByIdIgnoreLogic(Serializable id, boolean useFill) {
        throw new UnsupportedOperationException("不支持的方法!");
    }

    default boolean removeByIdIgnoreLogic(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().deleteByIdIgnoreLogic(entity));
    }

    default boolean removeByMapIgnoreLogic(Map<String, Object> columnMap) {
        Assert.notEmpty(columnMap, "error: columnMap must not be empty", new Object[0]);
        return SqlHelper.retBool(this.getBaseMapper().deleteByMapIgnoreLogic(columnMap));
    }

    default boolean removeIgnoreLogic(Wrapper<T> queryWrapper) {
        return SqlHelper.retBool(this.getBaseMapper().deleteIgnoreLogic(queryWrapper));
    }

    default boolean removeByIdsIgnoreLogic(Collection<?> list) {
        return CollectionUtils.isEmpty(list) ? false : SqlHelper.retBool(this.getBaseMapper().deleteBatchIdsIgnoreLogic(list));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean removeByIdsIgnoreLogic(Collection<?> list, boolean useFill) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        } else {
            return useFill ? this.removeBatchByIdsIgnoreLogic(list, true) : SqlHelper.retBool(this.getBaseMapper().deleteBatchIdsIgnoreLogic(list));
        }
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean removeBatchByIdsIgnoreLogic(Collection<?> list) {
        return this.removeBatchByIdsIgnoreLogic(list, 1000);
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean removeBatchByIdsIgnoreLogic(Collection<?> list, boolean useFill) {
        return this.removeBatchByIdsIgnoreLogic(list, 1000, useFill);
    }

    default boolean removeBatchByIdsIgnoreLogic(Collection<?> list, int batchSize) {
        throw new UnsupportedOperationException("不支持的方法!");
    }

    default boolean removeBatchByIdsIgnoreLogic(Collection<?> list, int batchSize, boolean useFill) {
        throw new UnsupportedOperationException("不支持的方法!");
    }

    default boolean updateByIdIgnoreLogic(T entity) {
        return SqlHelper.retBool(this.getBaseMapper().updateByIdIgnoreLogic(entity));
    }

    default boolean updateIgnoreLogic(Wrapper<T> updateWrapper) {
        return this.updateIgnoreLogic((T) null, updateWrapper);
    }

    default boolean updateIgnoreLogic(T entity, Wrapper<T> updateWrapper) {
        return SqlHelper.retBool(this.getBaseMapper().updateIgnoreLogic(entity, updateWrapper));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    default boolean updateBatchByIdIgnoreLogic(Collection<T> entityList) {
        return this.updateBatchByIdIgnoreLogic(entityList, 1000);
    }

    boolean updateBatchByIdIgnoreLogic(Collection<T> var1, int var2);

    boolean saveOrUpdateIgnoreLogic(T var1);

    default T getByIdIgnoreLogic(Serializable id) {
//        return this.getBaseMapper().selectByIdIgnoreLogic(id);
        return this.getBaseMapper().selectById(id);
    }


    default List<T> listByIdsIgnoreLogic(Collection<? extends Serializable> idList) {
        return this.getBaseMapper().selectBatchIdsIgnoreLogic(idList);
    }

    default List<T> listByMapIgnoreLogic(Map<String, Object> columnMap) {
        return this.getBaseMapper().selectByMapIgnoreLogic(columnMap);
    }

    default T getOneIgnoreLogic(Wrapper<T> queryWrapper) {
        return this.getOneIgnoreLogic(queryWrapper, true);
    }

    T getOneIgnoreLogic(Wrapper<T> var1, boolean var2);

    Map<String, Object> getMapIgnoreLogic(Wrapper<T> var1);

    <V> V getObjIgnoreLogic(Wrapper<T> var1, Function<? super Object, V> var2);

    default long countIgnoreLogic() {
        return this.count(Wrappers.emptyWrapper());
    }

    default long countIgnoreLogic(Wrapper<T> queryWrapper) {
        return SqlHelper.retCount(this.getBaseMapper().selectCountIgnoreLogic(queryWrapper));
    }

    default List<T> listIgnoreLogic(Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectListIgnoreLogic(queryWrapper);
    }

    default List<T> listIgnoreLogic() {
        return this.listIgnoreLogic(Wrappers.emptyWrapper());
    }

    default <E extends IPage<T>> E pageIgnoreLogic(E page, Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectPageIgnoreLogic(page, queryWrapper);
    }

    default <E extends IPage<T>> E pageIgnoreLogic(E page) {
        return this.pageIgnoreLogic(page, Wrappers.emptyWrapper());
    }

    default List<Map<String, Object>> listMapsIgnoreLogic(Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectMapsIgnoreLogic(queryWrapper);
    }

    default List<Map<String, Object>> listIgnoreLogicMaps() {
        return this.listMapsIgnoreLogic(Wrappers.emptyWrapper());
    }

    default List<Object> listObjsIgnoreLogic() {
        return this.listObjsIgnoreLogic(Function.identity());
    }

    default <V> List<V> listObjsIgnoreLogic(Function<? super Object, V> mapper) {
        return this.listObjsIgnoreLogic(Wrappers.emptyWrapper(), mapper);
    }

    default List<Object> listObjsIgnoreLogic(Wrapper<T> queryWrapper) {
        return this.listObjsIgnoreLogic(queryWrapper, Function.identity());
    }

    default <V> List<V> listObjsIgnoreLogic(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return (List)this.getBaseMapper().selectObjsIgnoreLogic(queryWrapper).stream().filter(Objects::nonNull).map(mapper).collect(Collectors.toList());
    }

    default <E extends IPage<Map<String, Object>>> E pageMapsIgnoreLogic(E page, Wrapper<T> queryWrapper) {
        return this.getBaseMapper().selectMapsPageIgnoreLogic(page, queryWrapper);
    }

    default <E extends IPage<Map<String, Object>>> E pageMapsIgnoreLogic(E page) {
        return this.pageMapsIgnoreLogic(page, Wrappers.emptyWrapper());
    }

    default boolean saveOrUpdateIgnoreLogic(T entity, Wrapper<T> updateWrapper) {
        return this.updateIgnoreLogic(entity, updateWrapper) || this.saveOrUpdateIgnoreLogic(entity);
    }
}
