package org.openea.eap.extj.database.plugins;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema模式租户插件
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MySchemaInnerInterceptor extends DynamicTableNameInnerInterceptor {

    private Runnable hook;

    public void setHook(Runnable hook) {
        this.hook = hook;
    }

    /**
     * 表名处理器，是否处理表名的情况都在该处理器中自行判断
     */
    private TableNameHandler tableNameHandler;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        //租户指定数据源不处理
        if (DataSourceContextHolder.isAssignDataSource()) {
            return;
        }
        if (NotTenantPluginHolder.isNotSwitch()) {
            NotTenantPluginHolder.clearNotSwitchFlag();
            return;
        }
        //非主库不切库
        if(!DynamicDataSourceUtil.isPrimaryDataSoure()){
            return;
        }
        //不绑定数据源的接口不切库
        /*if(NotTenantPluginHolder.isNotSwitchAlways()){
            return;
        }*/
        if(StringUtil.isEmpty(DataSourceContextHolder.getDatasourceId())){
            log.info("未获取到线程租户ID, 跳过切库");
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        if (!InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            // 非忽略执行
            mpBs.sql(this.changeTable(mpBs.sql()));
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        //租户指定数据源不处理
        if (DataSourceContextHolder.isAssignDataSource()) {
            return;
        }
        if (NotTenantPluginHolder.isNotSwitch()) {
            NotTenantPluginHolder.clearNotSwitchFlag();
            return;
        }
        //非主库不切库
        if(!DynamicDataSourceUtil.isPrimaryDataSoure()){
            return;
        }
        //不绑定数据源的接口不切库
        /*if(NotTenantPluginHolder.isNotSwitchAlways()){
            return;
        }*/
        if(StringUtil.isEmpty(DataSourceContextHolder.getDatasourceId())){
            log.info("未获取到线程租户ID, 跳过切库");
            return;
        }
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (!InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                // 非忽略执行
                PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                mpBs.sql(this.changeTable(mpBs.sql()));

                /*
                //只有修改、新增、删除数据才切库
                //先保留代码看后续需求
                SQLStatement sqlStatement = SQLParserUtils.createSQLStatementParser(mpBs.sql(), DbTypeUtil.getDb(dataSourceUtil).getDruidDbType()).parseStatement();
                if (sqlStatement instanceof SQLSelectStatement
                        || sqlStatement instanceof SQLUpdateStatement
                        || sqlStatement instanceof SQLInsertStatement
                        || sqlStatement instanceof SQLDeleteStatement) {
                    mpBs.sql(this.changeTable(mpBs.sql()));
                }*/
            }
        }
    }

    public String changeTable(String sql) {
        ExceptionUtils.throwMpe(null == tableNameHandler, "Please implement TableNameHandler processing logic");
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                builder.append(tableNameHandler.dynamicTableName(sql, name.getValue()));
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        if (hook != null) {
            hook.run();
        }
        return builder.toString();
    }

    public void setTableNameHandler(final TableNameHandler tableNameHandler) {
        this.tableNameHandler = tableNameHandler;
    }
}
