package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.tx.TransactionContext;
import org.openea.eap.extj.database.util.ConnUtil;
import lombok.Cleanup;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.Properties;

/**
 * 
 */@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
public class MyDynamicDataSourceAutoRollbackInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean hasTrans = TransactionSynchronizationManager.isActualTransactionActive();
        if (!hasTrans) {
            hasTrans = StringUtils.hasText(TransactionContext.getXID());
        }
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            if(hasTrans){
                //Postgre Oracle Kingbase 连接报错后不可使用必须主动调用回滚
                Executor executor = (Executor) invocation.getTarget();
                @Cleanup Connection conn = executor.getTransaction().getConnection();
                if(conn != null && !conn.getAutoCommit()){
                    Connection tmpConnection = ConnUtil.getRealConnection(conn);
                    tmpConnection.rollback();
                }
            }
            throw e;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
