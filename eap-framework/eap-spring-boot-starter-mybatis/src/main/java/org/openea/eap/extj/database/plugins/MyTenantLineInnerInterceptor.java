package org.openea.eap.extj.database.plugins;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class MyTenantLineInnerInterceptor extends TenantLineInnerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyTenantLineInnerInterceptor.class);

    public MyTenantLineInnerInterceptor() {
    }

    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!DataSourceContextHolder.isAssignDataSource()) {
            if (NotTenantPluginHolder.isNotSwitch()) {
                NotTenantPluginHolder.clearNotSwitchFlag();
            } else if (DynamicDataSourceUtil.isPrimaryDataSoure()) {
                if (StringUtil.isEmpty(DataSourceContextHolder.getDatasourceId())) {
                    log.info("未获取到线程租户ID, 跳过切库");
                } else {
                    try {
                        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
                    } catch (Exception var8) {
                        if (log.isDebugEnabled()) {
                            log.debug("语句解析失败", var8);
                        }
                    }

                }
            }
        }
    }

    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        if (!DataSourceContextHolder.isAssignDataSource()) {
            if (NotTenantPluginHolder.isNotSwitch()) {
                NotTenantPluginHolder.clearNotSwitchFlag();
            } else if (DynamicDataSourceUtil.isPrimaryDataSoure()) {
                if (StringUtil.isEmpty(DataSourceContextHolder.getDatasourceId())) {
                    log.info("未获取到线程租户ID, 跳过切库");
                } else {
                    try {
                        super.beforePrepare(sh, connection, transactionTimeout);
                    } catch (Exception var5) {
                        if (log.isDebugEnabled()) {
                            log.debug("语句解析失败", var5);
                        }
                    }

                }
            }
        }
    }

    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(".");
        } else {
            column.append(table.getName()).append(".");
        }

        column.append(super.getTenantLineHandler().getTenantIdColumn());
        return new Column(column.toString());
    }
}
