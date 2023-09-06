package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.dynamic.datasource.tx.ConnectionFactory;
import com.baomidou.dynamic.datasource.tx.ConnectionProxy;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.database.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 自定义动态数据源类
 * 
 */
@Slf4j
public class MyDynamicRoutingDataSource extends DynamicRoutingDataSource {

    @Override
    public Connection getConnection() throws SQLException {
        String xid = TransactionContext.getXID();
        if (!StringUtils.hasText(xid)) {
            return getMyDataSource();
        } else {
            String ds = DynamicDataSourceContextHolder.peek();
            ds = !StringUtils.hasText(ds) ? "default" : ds;
            ConnectionProxy connection = ConnectionFactory.getConnection(ds);
            return connection == null ? getConnectionProxy(ds, getMyDataSource()) : connection;
        }
    }

    private Connection getMyDataSource() throws SQLException {
        String dsKey = DynamicDataSourceContextHolder.peek();
        boolean switchAssign = false;
        if(DynamicDataSourceUtil.isPrimaryDataSoure() && TenantDataSourceUtil.isTenantAssignDataSource()){
            //租户系统指定数据源, 如果当前源为主库 直接切换至指定源, 处理事务开启时Spring先行获取连接未切库问题
            dsKey = TenantDataSourceUtil.getTenantAssignDataSourceMasterKeyName();
            DynamicDataSourceContextHolder.push(dsKey);
            switchAssign = true;
        }
        try{
            DataSource dataSource = getDataSource(dsKey);
            Connection connection = dataSource.getConnection();
            ConnUtil.switchConnectionSchema(connection);
            return connection;
        }catch (SQLException e){
            //移除运行中动态创建的数据源
            //避免第三方数据库关闭后一直尝试重新创建连接
            if (DynamicDataSourceUtil.containsLink(dsKey)) {
                try {
                    //Druid数据源如果正在获取数据源 有概率连接创建线程无法停止
                    //if(((ItemDataSource) dataSource).getRealDataSource() instanceof DruidDataSource){
                    //    ((DruidDataSource) ((ItemDataSource) dataSource).getRealDataSource()).setBreakAfterAcquireFailure(true);
                    //}
                    removeDataSource(dsKey);
                } catch (Exception ee) {
                    log.error("关闭动态数据源【" + dsKey + "】失败", ee);
                }
            }else if(TenantDataSourceUtil.isTenantAssignDataSource()){
                //租户指定数据源 连接失败全部移除
                TenantDataSourceUtil.removeAllAssignDataSource();
            }
            throw e;
        }finally{
            if(switchAssign){
                DynamicDataSourceContextHolder.poll();
            }
        }
    }


    private Connection getConnectionProxy(String ds, Connection connection) {
        ConnectionProxy connectionProxy = new ConnectionProxy(connection, ds);
        ConnectionFactory.putConnection(ds, connectionProxy);
        return connectionProxy;
    }
}
