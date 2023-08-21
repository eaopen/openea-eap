package org.openea.eap.extj.database.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.apache.commons.collections4.map.LRUMap;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class DynamicDataSourceUtil {

    private static int MAX_DATASOURCE_COUNT = 300;
    private static LRUMap<String, DbLinkEntity> linksProperties;
    public static DataSourceUtil dataSourceUtil;
    public static DynamicRoutingDataSource dynamicRoutingDataSource;
    public static DynamicDataSourceProperties dynamicDataSourceProperties;
    private static DefaultDataSourceCreator defaultDataSourceCreator;
    private static ConfigValueUtil configValueUtil;

    public DynamicDataSourceUtil(@Qualifier("dataSourceSystem") DataSource dynamicRoutingDataSource, DynamicDataSourceProperties dynamicDataSourceProperties, DefaultDataSourceCreator defaultDataSourceCreator, ConfigValueUtil configValueUtil, DataSourceUtil dataSourceUtil) {
        DynamicDataSourceUtil.dynamicRoutingDataSource = (DynamicRoutingDataSource)dynamicRoutingDataSource;
        DynamicDataSourceUtil.dynamicDataSourceProperties = dynamicDataSourceProperties;
        DynamicDataSourceUtil.defaultDataSourceCreator = defaultDataSourceCreator;
        DynamicDataSourceUtil.configValueUtil = configValueUtil;
        DynamicDataSourceUtil.dataSourceUtil = dataSourceUtil;
    }

    public static void switchToDataSource(String userName, String password, String url, String dbType) throws DataException, SQLException {
        String tenantId = (String) Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
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

    public static void switchToDataSource(DbLinkEntity dbLinkEntity) throws DataException, SQLException {
        String dbKey;
        if (dbLinkEntity != null && !StringUtil.isEmpty(dbLinkEntity.getId()) && !"0".equals(dbLinkEntity.getId())) {
            dbKey = (String)Optional.ofNullable(DataSourceContextHolder.getDatasourceId()).orElse("");
            String dbKey = dbKey + dbLinkEntity.getId();
            String removeKey = null;
            boolean insert = true;
            synchronized(LockObjectUtil.addLockKey(dbKey)) {
                if (dynamicRoutingDataSource.getDataSources().containsKey(dbKey)) {
                    synchronized(linksProperties) {
                        if (((DbLinkEntity)linksProperties.get(dbKey)).equals(dbLinkEntity)) {
                            insert = false;
                        }
                    }
                }

                if (insert) {
                    DataSource ds = createDataSource(dbLinkEntity);
                    if (ds instanceof ItemDataSource && ((ItemDataSource)ds).getRealDataSource() instanceof DruidDataSource) {
                        ((DruidDataSource)((ItemDataSource)ds).getRealDataSource()).setMinIdle(0);
                    }

                    dynamicRoutingDataSource.addDataSource(dbKey, ds);
                    synchronized(linksProperties) {
                        if (linksProperties.size() == MAX_DATASOURCE_COUNT) {
                            removeKey = (String)linksProperties.firstKey();
                        }

                        linksProperties.put(dbKey, dbLinkEntity);
                    }
                }
            }

            DynamicDataSourceContextHolder.push(dbKey);
            if (removeKey != null) {
                try {
                    dynamicRoutingDataSource.removeDataSource(removeKey);
                } catch (Exception var10) {
                    log.error("移除数据源失败：{}", var10.getMessage());
                }
            }

        } else {
            if (TenantDataSourceUtil.isTenantAssignDataSource()) {
                dbKey = DataSourceContextHolder.getDatasourceId() + "-" + "master";
                DynamicDataSourceContextHolder.push(dbKey);
            } else {
                DynamicDataSourceContextHolder.push((String)null);
            }

        }
    }

    public static void clearSwitchDataSource() {
        DynamicDataSourceContextHolder.poll();
    }

    public static Connection getCurrentConnection() throws SQLException {
        return dynamicRoutingDataSource.getConnection();
    }

    public static <T extends DataSourceUtil> DataSource createDataSource(T dbLinkEntity) throws DataException, SQLException {
        return createDataSource(dbLinkEntity, dbLinkEntity.getUrl());
    }

    public static <T extends DataSourceUtil> DataSource createDataSource(T dbLinkEntity, String url) throws DataException {
        DataSourceProperty dataSourceProperty = createDataSourceProperty(dbLinkEntity, url);
        DataSource ds = defaultDataSourceCreator.createDataSource(dataSourceProperty);
        return ds;
    }

    public static <T extends DataSourceUtil> DataSourceProperty createDataSourceProperty(T dbLinkEntity, String url) {
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
        if (dataSourceProperty.getDruid().getValidationQuery() == null && (DbTypeUtil.checkKingbase(dbLinkEntity) || DbTypeUtil.checkDM(dbLinkEntity))) {
            dataSourceProperty.getDruid().setValidationQuery("select 1;");
        }

        if (DbTypeUtil.checkOracle(dbLinkEntity)) {
            dataSourceProperty.getDruid().setConnectionProperties(DbOracle.setConnProp("Default", dbLinkEntity.getUserName(), dbLinkEntity.getPassword()));
        }

        return dataSourceProperty;
    }

    public static boolean isPrimaryDataSoure() {
        String dsKey = DynamicDataSourceContextHolder.peek();
        return StringUtil.isEmpty(dsKey) || dynamicDataSourceProperties.getPrimary().equals(dsKey);
    }

    public static String getPrimaryDbType() {
        return dataSourceUtil.getDbType();
    }

    public static DataSourceUtil getDataSourceUtil() {
        return dataSourceUtil;
    }

    public static boolean containsLink(String key) {
        return linksProperties.containsKey(key);
    }

    static {
        linksProperties = new LRUMap(MAX_DATASOURCE_COUNT);
    }
}
