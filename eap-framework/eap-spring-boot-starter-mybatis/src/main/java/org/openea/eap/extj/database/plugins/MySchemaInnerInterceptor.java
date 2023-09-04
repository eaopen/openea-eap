package org.openea.eap.extj.database.plugins;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MySchemaInnerInterceptor extends DynamicTableNameInnerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MySchemaInnerInterceptor.class);
    private Runnable hook;
    private TableNameHandler tableNameHandler;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    public void setHook(Runnable hook) {
        this.hook = hook;
    }

    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!DataSourceContextHolder.isAssignDataSource()) {
            if (NotTenantPluginHolder.isNotSwitch()) {
                NotTenantPluginHolder.clearNotSwitchFlag();
            } else if (DynamicDataSourceUtil.isPrimaryDataSoure()) {
                if (StringUtil.isEmpty(DataSourceContextHolder.getDatasourceId())) {
                    log.info("未获取到线程租户ID, 跳过切库");
                } else {
                    PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
                    if (!InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                        mpBs.sql(this.changeTable(mpBs.sql()));
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
                    PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
                    MappedStatement ms = mpSh.mappedStatement();
                    SqlCommandType sct = ms.getSqlCommandType();
                    if ((sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) && !InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                        PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                        mpBs.sql(this.changeTable(mpBs.sql()));
                    }

                }
            }
        }
    }

    public String changeTable(String sql) {
        ExceptionUtils.throwMpe(null == this.tableNameHandler, "Please implement TableNameHandler processing logic", new Object[0]);
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;

        TableNameParser.SqlToken name;
        for(Iterator var6 = names.iterator(); var6.hasNext(); last = name.getEnd()) {
            name = (TableNameParser.SqlToken)var6.next();
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                builder.append(this.tableNameHandler.dynamicTableName(sql, name.getValue()));
            }
        }

        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }

        if (this.hook != null) {
            this.hook.run();
        }

        return builder.toString();
    }

    public Runnable getHook() {
        return this.hook;
    }

    public TableNameHandler getTableNameHandler() {
        return this.tableNameHandler;
    }

    public DataSourceUtil getDataSourceUtil() {
        return this.dataSourceUtil;
    }

    public void setTableNameHandler(TableNameHandler tableNameHandler) {
        this.tableNameHandler = tableNameHandler;
    }

    public void setDataSourceUtil(DataSourceUtil dataSourceUtil) {
        this.dataSourceUtil = dataSourceUtil;
    }

    public MySchemaInnerInterceptor() {
    }

    public MySchemaInnerInterceptor(Runnable hook, TableNameHandler tableNameHandler, DataSourceUtil dataSourceUtil) {
        this.hook = hook;
        this.tableNameHandler = tableNameHandler;
        this.dataSourceUtil = dataSourceUtil;
    }
}
