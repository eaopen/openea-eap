package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
), @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class MyMasterSlaveAutoRoutingPlugin implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(MyMasterSlaveAutoRoutingPlugin.class);
    protected DynamicRoutingDataSource dynamicDataSource;

    public MyMasterSlaveAutoRoutingPlugin(DataSource dataSource) {
        this.dynamicDataSource = (DynamicRoutingDataSource)dataSource;
    }

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (DataSourceContextHolder.isAssignDataSource() && DynamicDataSourceUtil.isPrimaryDataSoure()) {
            MappedStatement ms = (MappedStatement)args[0];
            String pushedDataSource = null;

            Object var9;
            try {
                TenantDataSourceUtil.initTenantAssignDataSource();
                String tenantId = StringUtil.isNotEmpty(DataSourceContextHolder.getDatasourceId()) ? DataSourceContextHolder.getDatasourceId() : "";
                String masterKey = tenantId + "-" + "master";
                String slaveKey = tenantId + "-" + "slave";
                String dataSource = SqlCommandType.SELECT == ms.getSqlCommandType() ? slaveKey : masterKey;
                if (!this.dynamicDataSource.getGroupDataSources().containsKey(dataSource)) {
                    dataSource = masterKey;
                }

                pushedDataSource = DynamicDataSourceContextHolder.push(dataSource);
                var9 = invocation.proceed();
            } finally {
                if (pushedDataSource != null) {
                    DynamicDataSourceContextHolder.poll();
                }

            }

            return var9;
        } else {
            return invocation.proceed();
        }
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}

