package org.openea.eap.extj.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.database.plugins.MyDefaultSqlInjector;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.UserProvider;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class SuperServiceImpl<M extends SuperMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {

    /**
     * 填充删除用户与删除时间
     * @param entity 实体
     * @return
     */
    @Override
    public boolean removeById(T entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        if (tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill()) {
            try{
                if(tableInfo.getPropertyValue(entity, "deleteUserId") == null){
                    String userId = UserProvider.getLoginUserId();
                    if (userId != null) {
                        Date deleteTime = DateUtil.getNowDate();
                        tableInfo.setPropertyValue(entity, "deleteUserId" , userId);
                        tableInfo.setPropertyValue(entity, "deleteTime" , deleteTime);
                    }
                }
            }catch (Exception e){ }
        }
        return super.removeById(entity);
    }

    /**
     * 填充删除用户与删除时间
     * @param list      主键ID或实体列表
     * @param batchSize 批次大小
     * @param useFill   是否启用填充(为true的情况,会将入参转换实体进行delete删除)
     * @return
     */
    @Override
    public boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = getSqlStatement(SqlMethod.DELETE_BY_ID);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return executeBatch(list, batchSize, (sqlSession, e) -> {
            if (useFill && tableInfo.isWithLogicDelete()) {
                if (entityClass.isAssignableFrom(e.getClass())) {
                    sqlSession.update(sqlStatement, e);
                } else {
                    T instance = tableInfo.newInstance();
                    tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), e);
                    try{
                        if(tableInfo.getPropertyValue(instance, "deleteUserId") == null){
                            String userId = UserProvider.getLoginUserId();
                            if (userId != null) {
                                Date deleteTime = DateUtil.getNowDate();
                                tableInfo.setPropertyValue(instance, "deleteUserId" , userId);
                                tableInfo.setPropertyValue(instance, "deleteTime" , deleteTime);
                            }
                        }
                    }catch (Exception ex){ }
                    sqlSession.update(sqlStatement, instance);
                }
            } else {
                sqlSession.update(sqlStatement, e);
            }
        });
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    protected String getSqlStatementIgnoreLogic(SqlMethod sqlMethod) {
        return mapperClass.getName() + StringPool.DOT + sqlMethod.getMethod() + MyDefaultSqlInjector.ignoreLogicPrefix;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateIgnoreLogic(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getByIdIgnoreLogic((Serializable) idVal)) ? save(entity) : updateByIdIgnoreLogic(entity);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(this.entityClass, this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatementIgnoreLogic(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatementIgnoreLogic(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatementIgnoreLogic(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public T getOneIgnoreLogic(Wrapper<T> queryWrapper, boolean throwEx) {
        if (throwEx) {
            return baseMapper.selectOne(queryWrapper);
        }
        return SqlHelper.getObject(log, baseMapper.selectListIgnoreLogic(queryWrapper));
    }

    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, baseMapper.selectMapsIgnoreLogic(queryWrapper));
    }

    @Override
    public <V> V getObjIgnoreLogic(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjsIgnoreLogic(queryWrapper, mapper));
    }

    @Override
    public boolean removeByIdIgnoreLogic(Serializable id) {
        return SqlHelper.retBool(getBaseMapper().deleteByIdIgnoreLogic(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIdsIgnoreLogic(Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return SqlHelper.retBool(getBaseMapper().deleteBatchIdsIgnoreLogic(list));
    }

    @Override
    public boolean removeByIdIgnoreLogic(Serializable id, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteByIdIgnoreLogic(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(Collection<?> list, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return removeBatchByIdsIgnoreLogic(list, batchSize, tableInfo.isWithLogicDelete() && tableInfo.isWithUpdateFill());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIdsIgnoreLogic(Collection<?> list, int batchSize, boolean useFill) {
        String sqlStatement = getSqlStatementIgnoreLogic(SqlMethod.DELETE_BY_ID);
        return executeBatch(list, batchSize, (sqlSession, e) -> {
            sqlSession.update(sqlStatement, e);
        });
    }

}
