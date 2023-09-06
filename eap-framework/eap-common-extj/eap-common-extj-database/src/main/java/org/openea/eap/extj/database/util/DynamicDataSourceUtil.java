package org.openea.eap.extj.database.util;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.support.DdConstants;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LRUMap;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.impl.DbOracle;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.LockObjectUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 动态切换数据源， 配合DynamicDatasource数据源
 *
 * 
 */
@Setter
@Slf4j
@Component
//@ConditionalOnMissingBean(DynamicDataSourceUtil.class)
public class DynamicDataSourceUtil {

    private static int MAX_DATASOURCE_COUNT = 300;

    //最多保存三百个数据源， 按使用率淘汰
    private static LRUMap<String, DbLinkEntity> linksProperties = new LRUMap(MAX_DATASOURCE_COUNT);


    public static DataSourceUtil dataSourceUtil;
    public static DynamicRoutingDataSource dynamicRoutingDataSource;
    public static DynamicDataSourceProperties dynamicDataSourceProperties;
    private static DefaultDataSourceCreator defaultDataSourceCreator;
    private static ConfigValueUtil configValueUtil;

    // @Qualifier("dataSourceSystem") DataSource  dynamicRoutingDataSource
    public DynamicDataSourceUtil(DataSource  dynamicRoutingDataSource
            , DynamicDataSourceProperties dynamicDataSourceProperties
            , DefaultDataSourceCreator defaultDataSourceCreator
            , ConfigValueUtil configValueUtil
            , DataSourceUtil dataSourceUtil) {
        DynamicDataSourceUtil.dynamicRoutingDataSource = (DynamicRoutingDataSource) dynamicRoutingDataSource;
        DynamicDataSourceUtil.dynamicDataSourceProperties = dynamicDataSourceProperties;
        DynamicDataSourceUtil.defaultDataSourceCreator = defaultDataSourceCreator;
        DynamicDataSourceUtil.configValueUtil = configValueUtil;
        DynamicDataSourceUtil.dataSourceUtil = dataSourceUtil;
    }


    /**
     * 创建并切换至远程数据源
     *
     * @param userName
     * @param password
     * @param url
     * @throws DataException
     * @throws SQLException
     */
    public static void switchToDataSource(String userName, String password, String url, String dbType) throws DataException, SQLException {
        String tenantId = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
        if(ObjectUtil.isEmpty(tenantId) || "0".equals(tenantId)){
            return;
        }

        String dbKey = tenantId + userName + password + url;
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        dbLinkEntity.setId(dbKey);
        dbLinkEntity.setUserName(userName);
        dbLinkEntity.setPassword(password);
        dbLinkEntity.setUrl(url);
        if (StringUtil.isEmpty(dbType)) {
            dbLinkEntity.setDbType(DbTypeUtil.getDb(url).getJnpfDbEncode());
        } else {
            dbLinkEntity.setDbType(dbType);
        }
        switchToDataSource(dbLinkEntity);
    }

    /**
     * 创建并切换至远程数据源
     * dbLinkEntity 为空不或ID为空切换为默认数据源 多数据源层级 +1
     *
     * @param dbLinkEntity
     * @throws DataException
     */
    public static void switchToDataSource(DbLinkEntity dbLinkEntity) throws DataException, SQLException {
        //切换目标为系统主库 数据源层级+1
        if (dbLinkEntity == null || StringUtil.isEmpty(dbLinkEntity.getId()) || "0".equals(dbLinkEntity.getId())) {
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                //租户指定数据源
                String dbKey = DataSourceContextHolder.getDatasourceId() + StrPool.DASHED + DdConstants.MASTER;
                DynamicDataSourceContextHolder.push(dbKey);
            }else {
                DynamicDataSourceContextHolder.push(null);
            }
            //DataSourceContextHolder.addDynamicDatasourceLevel();
            return;
        }
        String tenantId = Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
        if(ObjectUtil.isEmpty(tenantId) || "0".equals(tenantId)){
            return;
        }

        //String dbKey = tenantId + dbLinkEntity.getId();
        String dbKey = (String)Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("") + dbLinkEntity.getId();
        String removeKey = null;
        boolean insert = true;
        synchronized (LockObjectUtil.addLockKey(dbKey)) {
            if (dynamicRoutingDataSource.getDataSources().containsKey(dbKey)) {
                synchronized (linksProperties) {
                    if (linksProperties.get(dbKey).equals(dbLinkEntity)) {
                        insert = false;
                    }
                }
            }
            if (insert) {
                DataSource ds = createDataSource(dbLinkEntity);
                //单独设置动态切换的数据源参数
                if (ds instanceof ItemDataSource) {
                    if (((ItemDataSource) ds).getRealDataSource() instanceof DruidDataSource) {
                        //运行中创建的连接， 30分钟空闲后不保留
                        ((DruidDataSource) ((ItemDataSource) ds).getRealDataSource()).setMinIdle(0);
//                    ((DruidDataSource)((ItemDataSource)ds).getRealDataSource()).setTimeBetweenConnectErrorMillis(DruidAbstractDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
                    }
                }
                dynamicRoutingDataSource.addDataSource(dbKey, ds);
                synchronized (linksProperties) {
                    if (linksProperties.size() == MAX_DATASOURCE_COUNT) {
                        removeKey = linksProperties.firstKey();
                    }
                    linksProperties.put(dbKey, dbLinkEntity);
                }
            }
        }
        /*if(dbKey.contains("_")){
            //分组数据源
            dbKey = dbKey.split("_")[0];
        }*/
        DynamicDataSourceContextHolder.push(dbKey);
//        DataSourceContextHolder.addDynamicDatasourceLevel();
        if (removeKey != null) {
            try {
                dynamicRoutingDataSource.removeDataSource(removeKey);
            } catch (Exception e) {
                log.error("移除数据源失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 移除当前设置的远程数据源， 清除上次清除之后切换的所有数据源
     * 需要先调用 switchToDataSource切换数据源
     */
    public static void clearSwitchDataSource() {
        DynamicDataSourceContextHolder.poll();
//        for (int i = 0; i < DataSourceContextHolder.getDynamicDatasourceLevel(); i++) {
//            DynamicDataSourceContextHolder.poll();
//        }
//        DataSourceContextHolder.clearDynamicDatasourceLevel();
    }

    /**
     * 获取当前数据源的数据链接(切库后的)
     * 用完之后一定要关闭
     *
     * @return
     * @throws SQLException
     */
    public static Connection getCurrentConnection() throws SQLException {
        return dynamicRoutingDataSource.getConnection();
    }

    /**
     * 创建数据源
     *
     * @param dbLinkEntity
     * @param <T>
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public static <T extends DataSourceUtil> DataSource createDataSource(T dbLinkEntity) throws DataException, SQLException {
        return createDataSource(dbLinkEntity, dbLinkEntity.getUrl());
    }

    /**
     * 创建数据源
     *
     * @param dbLinkEntity
     * @param url          覆盖自动生成的连接地址
     * @param <T>
     * @return
     * @throws DataException
     * @throws SQLException
     */
    public static <T extends DataSourceUtil> DataSource createDataSource(T dbLinkEntity, String url) throws DataException {
        DataSourceProperty dataSourceProperty = createDataSourceProperty(dbLinkEntity, url);
        DataSource ds = defaultDataSourceCreator.createDataSource(dataSourceProperty);
        return ds;
    }

    public static <T extends DataSourceUtil> DataSourceProperty createDataSourceProperty(T dbLinkEntity, String url){
        if (StringUtil.isEmpty(url)) {
            url = ConnUtil.getUrl(dbLinkEntity, dbLinkEntity.getDbName());
        }
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUsername(dbLinkEntity.getAutoUsername());
        dataSourceProperty.setPassword(dbLinkEntity.getAutoPassword());
        dataSourceProperty.setUrl(url);
        dataSourceProperty.setDriverClassName(DbTypeUtil.getDb(dbLinkEntity).getDriver());
        dataSourceProperty.setDruid(dynamicDataSourceProperties.getDruid());
        dataSourceProperty.setLazy(true);
        dataSourceProperty.getDruid().setBreakAfterAcquireFailure(true);
        //不同库设置检查SQL语句,SqlServer, Mysql, Oracle, Postgre已有默认SQL检查器
        if (dataSourceProperty.getDruid().getValidationQuery() == null && (DbTypeUtil.checkKingbase(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity))) {
            dataSourceProperty.getDruid().setValidationQuery("select 1;");
        }
        // oracle参数处理
        if (DbTypeUtil.checkOracle(dbLinkEntity)) {
            dataSourceProperty.getDruid().setConnectionProperties(DbOracle.setConnProp("Default", dbLinkEntity.getUserName(), dbLinkEntity.getPassword()));
        }
        return dataSourceProperty;
    }



    /**
     * 当前是否是主库环境
     * @return
     */
    public static boolean isPrimaryDataSoure() {
        String dsKey = DynamicDataSourceContextHolder.peek();
        return StringUtil.isEmpty(dsKey) || dynamicDataSourceProperties.getPrimary().equals(dsKey);
    }

    /**
     * 获取主库数据源类型
     * @return
     */
    public static String getPrimaryDbType() {
        return dataSourceUtil.getDbType();
    }

    public static DataSourceUtil getDataSourceUtil(){
        return dataSourceUtil;
    }

    public static boolean containsLink(String key) {
        return linksProperties.containsKey(key);
    }

}
