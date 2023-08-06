package org.openea.eap.extj.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.ibatis.binding.MapperMethod;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.transaction.annotation.Transactional;

public abstract class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {
    public SuperServiceImpl() {
    }

    public boolean removeById(T entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        if (tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill()) {
            try {
                if (tableInfo.getPropertyValue(entity, "deleteUserId") == null) {
                    String userId = UserProvider.getLoginUserId();
                    if (userId != null) {
                        Date deleteTime = DateUtil.getNowDate();
                        tableInfo.setPropertyValue(entity, "deleteUserId", new Object[]{userId});
                        tableInfo.setPropertyValue(entity, "deleteTime", new Object[]{deleteTime});
                    }
                }
            } catch (Exception var5) {
            }
        }

        return super.removeById(entity);
    }

    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = this.getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (this.entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), new Object[]{e});

                    try {
                        if (tableInfo.getPropertyValue(instance, "deleteUserId") == null) {
                            String userId = UserProvider.getLoginUserId();
                            if (userId != null) {
                                Date deleteTime = DateUtil.getNowDate();
                                tableInfo.setPropertyValue(instance, "deleteUserId", new Object[]{userId});
                                tableInfo.setPropertyValue(instance, "deleteTime", new Object[]{deleteTime});
                            }
                        }
                    } catch (Exception var9) {
                    }

                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }

        });
    }

    protected String getSqlStatementIgnoreLogic(SqlMethod sqlMethod) {
        return this.mapperClass.getName() + "." + sqlMethod.getMethod() + "IgnoreLogic";
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean saveOrUpdateIgnoreLogic(T entity) {
        if (null == entity) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.getByIdIgnoreLogic((Serializable)idVal)) ? this.updateByIdIgnoreLogic(entity) : this.save(entity);
        }
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(this.getSqlStatementIgnoreLogic(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(this.getSqlStatementIgnoreLogic(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean updateBatchByIdIgnoreLogic(Collection<T> entityList, int batchSize) {
        String sqlStatement = this.getSqlStatementIgnoreLogic(SqlMethod.UPDATE_BY_ID);
        return this.executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    public T getOneIgnoreLogic(Wrapper<T> queryWrapper, boolean throwEx) {
        return throwEx ? (T) ((SuperMapper)this.baseMapper).selectOne(queryWrapper) : (T) SqlHelper.getObject(this.log, ((SuperMapper)this.baseMapper).selectListIgnoreLogic(queryWrapper));
    }

    public Map<String, Object> getMapIgnoreLogic(Wrapper<T> queryWrapper) {
        return (Map)SqlHelper.getObject(this.log, ((SuperMapper)this.baseMapper).selectMapsIgnoreLogic(queryWrapper));
    }

    public <V> V getObjIgnoreLogic(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(this.log, this.listObjsIgnoreLogic(queryWrapper, mapper));
    }

    public boolean removeByIdIgnoreLogic(Serializable id) {
        return SqlHelper.retBool(((SuperMapper)this.getBaseMapper()).deleteByIdIgnoreLogic(id));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean removeByIdsIgnoreLogic(Collection<?> list) {
        return CollectionUtils.isEmpty(list) ? false : SqlHelper.retBool(((SuperMapper)this.getBaseMapper()).deleteBatchIdsIgnoreLogic(list));
    }

    public boolean removeByIdIgnoreLogic(Serializable id, boolean useFill) {
        return SqlHelper.retBool(((SuperMapper)this.getBaseMapper()).deleteByIdIgnoreLogic(id));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        return this.removeBatchByIdsIgnoreLogic(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean removeBatchByIdsIgnoreLogic(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = this.getSqlStatementIgnoreLogic(SqlMethod.DELETE_BY_ID);
        return this.executeBatch(list, batchSize, (sqlSession, e) -> {
            sqlSession.update(sqlStatement, e);
        });
    }
}