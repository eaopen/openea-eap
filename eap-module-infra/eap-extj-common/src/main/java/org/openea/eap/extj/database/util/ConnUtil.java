package org.openea.eap.extj.database.util;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.exception.DataException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class ConnUtil {

    public ConnUtil() {
    }

    public static Connection getConnOrDefault(DbSourceOrDbLink dbSourceOrDbLink) throws DataException {
        if (dbSourceOrDbLink == null) {
            dbSourceOrDbLink = DynamicDataSourceUtil.getDataSourceUtil();
        }

        return PrepSqlDTO.getConn(((DbSourceOrDbLink)dbSourceOrDbLink).init());
    }

    public static Connection getConn(DbSourceOrDbLink dbSourceOrDbLink) throws DataException {
        return PrepSqlDTO.getConn(dbSourceOrDbLink.init());
    }

    public static Connection getConn(DbSourceOrDbLink dbSourceOrDbLink, String dbName) throws DataException {
        return PrepSqlDTO.getConn(dbSourceOrDbLink.init(dbName));
    }

    public static Connection getConn(String userName, String password, String url) throws DataException {
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        dbLinkEntity.setUserName(userName);
        dbLinkEntity.setPassword(password);
        dbLinkEntity.setUrl(url);
        dbLinkEntity.setDbType(DbTypeUtil.getDb(url).getJnpfDbEncode());
        return PrepSqlDTO.getConn(dbLinkEntity);
    }

    /** @deprecated */
    @Deprecated
    private static Connection getConnection(DbSourceOrDbLink dbSourceOrDbLink) throws DataException {
        return getConnection(dbSourceOrDbLink, (String)null);
    }

    /** @deprecated */
    @Deprecated
    private static Connection getConnection(DbSourceOrDbLink dataSourceUtil, String dbName) throws DataException {
        DbLinkEntity dbLinkEntity = dataSourceUtil.init();
        return DbTypeUtil.checkOracle(dbLinkEntity) ? getOracleConn(dbLinkEntity) : getConnection(dbLinkEntity.getAutoUsername(), dbLinkEntity.getAutoPassword(), getUrl(dbLinkEntity));
    }

    /** @deprecated */
    @Deprecated
    private static Connection getConnection(String userName, String password, String url) throws DataException {
        DbBase db = DbTypeUtil.getDb(url);
        return ConnUtil.ConnCommon.createConn(db.getDriver(), userName, password, url);
    }

    private static Connection getOracleConn(DbLinkEntity dsd) throws DataException {
        DbOracle dbOracle = new DbOracle();
        return dbOracle.getOracleConn(dsd, getUrl(dsd));
    }

    public static String getUrl(DbSourceOrDbLink dbSourceOrDbLink) {
        return getUrl(dbSourceOrDbLink, (String)null);
    }

    public static String getUrl(DbSourceOrDbLink dbSourceOrDbLink, String dbName) {
        return BaseCommon.getDbBaseConnUrl(dbSourceOrDbLink, dbName);
    }

    public static void switchConnectionSchema(Connection conn) throws SQLException {
        if (TenantDataSourceUtil.isMultiTenancy() && DynamicDataSourceUtil.isPrimaryDataSoure()) {
            String schema = TenantDataSourceUtil.getTenantSchema();
            if (StringUtil.isNotEmpty(schema)) {
                Connection tmpConnection = getRealConnection(conn);

                try {
                    switch (DynamicDataSourceUtil.getPrimaryDbType()) {
                        case "SQLServer":
                        case "MySQL":
                            if (!Objects.equals(tmpConnection.getCatalog(), schema)) {
                                tmpConnection.setCatalog(schema);
                            }
                            break;
                        case "PostgreSQL":
                            schema = schema.toLowerCase();
                            if (!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                            break;
                        case "Oracle":
                            schema = schema.toUpperCase();
                            if (!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                            break;
                        case "KingbaseES":
                        case "DM":
                        default:
                            if (!Objects.equals(tmpConnection.getSchema(), schema)) {
                                tmpConnection.setSchema(schema);
                            }
                    }
                } catch (Exception var7) {
                    conn.close();
                    String url = "";
                    if (tmpConnection instanceof ConnectionProxy) {
                        try {
                            url = ((ConnectionProxy)tmpConnection).getDirectDataSource().getUrl();
                        } catch (Exception var6) {
                        }
                    }

                    log.error("切库失败, 租户：{}, URL: {}, Msg: {}", new Object[]{DataSourceContextHolder.getDatasourceId(), url, var7.getMessage()});
                    throw var7;
                }
            }
        }

    }

    public static Connection getRealConnection(Connection connection) {
        Connection tmpConnection = connection;

        for(int i = 0; i < 6; ++i) {
            try {
                tmpConnection = (Connection) ReflectUtil.invoke(tmpConnection, "getConnection", new Object[0]);
            } catch (Exception var4) {
                break;
            }
        }

        return tmpConnection;
    }

    public static DruidDataSource getDruidDataSource(DataSourceUtil dataSourceUtil) throws DataException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSourceUtil.getUserName());
        druidDataSource.setPassword(dataSourceUtil.getPassword());
        druidDataSource.setUrl(getUrl(dataSourceUtil));
        druidDataSource.setDriverClassName(DbTypeUtil.getDb(dataSourceUtil).getDriver());
        return druidDataSource;
    }

    public static class ConnCommon {
        public ConnCommon() {
        }

        public static Connection createConn(String driver, String userName, String password, String url) throws DataException {
            try {
                Class.forName(driver);
                return DriverManager.getConnection(url, userName, password);
            } catch (Exception var5) {
                var5.printStackTrace();
                throw DataException.errorLink(var5.getMessage());
            }
        }

        public static Connection createConnByProp(String driver, String userName, String password, String url, Properties conProps) throws DataException {
            try {
                conProps.put("user", userName);
                conProps.put("password", password);
                Class.forName(driver);
                return DriverManager.getConnection(url, conProps);
            } catch (Exception var6) {
                throw new DataException(var6.getMessage());
            }
        }

        public static Connection getConnRemarks(DataSourceUtil dbSourceOrDbLink) throws DataException {
            Properties props = new Properties();
            props.setProperty("remarks", "true");
            props.setProperty("remarksReporting", "true");
            props.setProperty("useInformationSchema", "true");
            return createConnByProp(DbTypeUtil.getDb(ConnUtil.getUrl(dbSourceOrDbLink)).getDriver(), dbSourceOrDbLink.getUserName(), dbSourceOrDbLink.getPassword(), ConnUtil.getUrl(dbSourceOrDbLink), props);
        }
    }
}
