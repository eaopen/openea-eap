package org.openea.eap.extj.base.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Cleanup;
import org.openea.eap.extj.base.mapper.DbLinkMapper;
import org.openea.eap.extj.base.model.dblink.PaginationDbLink;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.DbTableService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.*;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

@Service
public class DbLinkServiceImpl extends SuperServiceImpl<DbLinkMapper, DbLinkEntity> implements DbLinkService, InitializingBean {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DbTableService dbTableService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public List<DbLinkEntity> getList() {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(DbLinkEntity::getSortCode)
                .orderByDesc(DbLinkEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DbLinkEntity> getList(PaginationDbLink pagination) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(DbLinkEntity::getFullName, pagination.getKeyword())
            );
        }
        if (StringUtil.isNotEmpty(pagination.getDbType())) {
            flag = true;
            queryWrapper.lambda().eq(DbLinkEntity::getDbType, pagination.getDbType());
        }
        queryWrapper.lambda().orderByAsc(DbLinkEntity::getSortCode)
                .orderByDesc(DbLinkEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(DbLinkEntity::getLastModifyTime);
        }
        Page<DbLinkEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DbLinkEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public DbLinkEntity getInfo(String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DbLinkEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DbLinkEntity::getId, id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public void create(DbLinkEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setEnabledMark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, DbLinkEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(DbLinkEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DbLinkEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(DbLinkEntity::getSortCode, upSortCode)
                .orderByDesc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DbLinkEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DbLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(DbLinkEntity::getSortCode, upSortCode)
                .orderByAsc(DbLinkEntity::getSortCode);
        List<DbLinkEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            downEntity.setLastModifyTime(new Date());
            upEntity.get(0).setSortCode(temp);
            this.updateById(upEntity.get(0));
            this.updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public boolean testDbConnection(DbLinkEntity entity) throws Exception{
        //判断字典数据类型编码是否错误，大小写不敏感
        DbBase db = DbTypeUtil.getDb(entity);
        if(db == null){
            throw new DataException(MsgCode.DB001.get());
        }
        @Cleanup Connection conn = ConnUtil.getConn(entity.getUserName(), entity.getPassword(), ConnUtil.getUrl(entity));
        return conn != null;
    }

    /**
     * 设置数据源
     * @param dbLinkId 数据连接id
     * @throws DataException ignore
     */
    @Override
    public DbLinkEntity getResource(String dbLinkId) throws Exception {
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        //多租户是否开启
        if("0".equals(dbLinkId)){
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                // 默认数据库, 租户管理指定租户数据源
//                dbLinkEntity = TenantDataSourceUtil.getTenantAssignDataSource(DataSourceContextHolder.getDatasourceId()).toDbLinkEntity();
                dbLinkEntity.setId("0");
            }else {
                // 默认数据库查询，从配置获取数据源信息
                BeanUtils.copyProperties(dataSourceUtils, dbLinkEntity);
                dbLinkEntity.setId("0");
                // 是系统默认的多租户
                TenantDataSourceUtil.initDataSourceTenantDbName(dbLinkEntity);
            }
        }else {
            try {
                DynamicDataSourceUtil.switchToDataSource(null);
                dbLinkEntity = dblinkService.getInfo(dbLinkId);
            }finally {
                DynamicDataSourceUtil.clearSwitchDataSource();
            }
        }
        // 添加并且切换数据源
        return dbLinkEntity;
    }

    @Override
    public void afterPropertiesSet(){
        // fix dataSourceUtils is empty
        if(dataSource!=null
                && dataSourceUtils!=null && ObjectUtil.isEmpty(dataSourceUtils.getDbName())){
            if(dataSource instanceof DynamicRoutingDataSource){
                DruidDataSource druidDs =  (DruidDataSource)((ItemDataSource)((DynamicRoutingDataSource)dataSource).getDataSource("master")).getRealDataSource();
                BeanUtils.copyProperties(druidDs, dataSourceUtils);
                dataSourceUtils.setDriver(druidDs.getDriverClassName());
                String dbType = druidDs.getDbType();
                for(int i=0; i<DbBase.DB_ENCODES.length; i++){
                    if(DbBase.DB_ENCODES[i].equalsIgnoreCase(dbType)){
                        dbType = DbBase.DB_ENCODES[i];
                        break;
                    }
                }
                dataSourceUtils.setDbType(dbType);
                String dbName = DataSourceUtil.getDbNameFromJdbcUrl(druidDs.getUrl());
                dataSourceUtils.setDbName(dbName);
                dataSourceUtils.setUserName(druidDs.getUsername());
            }
        }

        PrepSqlDTO.DB_LINK_FUN = (dbLinkId)-> {
            try {
                return (DbLinkEntity) getResource(dbLinkId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }



}
