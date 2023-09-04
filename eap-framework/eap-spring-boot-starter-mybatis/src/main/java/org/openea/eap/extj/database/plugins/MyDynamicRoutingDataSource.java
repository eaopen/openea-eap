package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.dynamic.datasource.tx.ConnectionFactory;
import com.baomidou.dynamic.datasource.tx.ConnectionProxy;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MyDynamicRoutingDataSource extends DynamicRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(MyDynamicRoutingDataSource.class);

    public MyDynamicRoutingDataSource() {
    }

    public Connection getConnection() throws SQLException {
        String xid = TransactionContext.getXID();
        if (!StringUtils.hasText(xid)) {
            return this.getMyDataSource();
        } else {
            String ds = DynamicDataSourceContextHolder.peek();
            ds = !StringUtils.hasText(ds) ? "default" : ds;
            ConnectionProxy connection = ConnectionFactory.getConnection(ds);
            return (Connection)(connection == null ? this.getConnectionProxy(ds, this.getMyDataSource()) : connection);
        }
    }

    private Connection getMyDataSource() throws SQLException {
        String dsKey = DynamicDataSourceContextHolder.peek();
        boolean switchAssign = false;
        if (DynamicDataSourceUtil.isPrimaryDataSoure() && TenantDataSourceUtil.isTenantAssignDataSource()) {
            TenantDataSourceUtil.initTenantAssignDataSource();
            dsKey = TenantDataSourceUtil.getTenantAssignDataSourceMasterKeyName();
            DynamicDataSourceContextHolder.push(dsKey);
            switchAssign = true;
        }

        Connection var5;
        try {
            DataSource dataSource = this.getDataSource(dsKey);
            Connection connection = dataSource.getConnection();
            ConnUtil.switchConnectionSchema(connection);
            var5 = connection;
        } catch (SQLException var11) {
            if (DynamicDataSourceUtil.containsLink(dsKey)) {
                try {
                    this.removeDataSource(dsKey);
                } catch (Exception var10) {
                    log.error("关闭动态数据源【" + dsKey + "】失败", var10);
                }
            } else if (TenantDataSourceUtil.isTenantAssignDataSource()) {
                TenantDataSourceUtil.removeAllAssignDataSource();
            }

            throw var11;
        } finally {
            if (switchAssign) {
                DynamicDataSourceContextHolder.poll();
            }

        }

        return var5;
    }

    private Connection getConnectionProxy(String ds, Connection connection) {
        ConnectionProxy connectionProxy = new ConnectionProxy(connection, ds);
        ConnectionFactory.putConnection(ds, connectionProxy);
        return connectionProxy;
    }
}
