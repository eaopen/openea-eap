package org.openea.eap.extj.database.plugins;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Column模式租户插件
 * 
 */
@Slf4j
public class MyTenantLineInnerInterceptor extends TenantLineInnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        //租户指定数据源不处理
        if(DataSourceContextHolder.isAssignDataSource()){
            return;
        }
        if(NotTenantPluginHolder.isNotSwitch()){
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
        try {
            super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        } catch (Exception e){
            //特殊语句解析失败
            if(log.isDebugEnabled()){
                log.debug("语句解析失败", e);
            }
        }
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        //租户指定数据源不处理
        if(DataSourceContextHolder.isAssignDataSource()){
            return;
        }
        if(NotTenantPluginHolder.isNotSwitch()){
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
        try {
            super.beforePrepare(sh, connection, transactionTimeout);
        } catch (Exception e){
            //特殊语句解析失败
            if(log.isDebugEnabled()){
                log.debug("语句解析失败", e);
            }
        }
    }

    @Override
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName()).append(".");
        } else{
            column.append(table.getName()).append(".");
        }

        column.append(super.getTenantLineHandler().getTenantIdColumn());
        return new Column(column.toString());
    }
}
