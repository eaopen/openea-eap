package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.tx.TransactionContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.openea.eap.extj.database.util.ConnUtil;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.util.Collections;
import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class MyDynamicDataSourceAutoRollbackInterceptor  implements Interceptor {
    public MyDynamicDataSourceAutoRollbackInterceptor() {
    }

    public Object intercept(Invocation invocation) throws Throwable {
        boolean hasTrans = TransactionSynchronizationManager.isActualTransactionActive();
        if (!hasTrans) {
            hasTrans = StringUtils.hasText(TransactionContext.getXID());
        }

        try {
            return invocation.proceed();
        } catch (Throwable var11) {
            if (hasTrans) {
                Executor executor = (Executor)invocation.getTarget();
                Connection conn = executor.getTransaction().getConnection();

                try {
                    if (conn != null && !conn.getAutoCommit()) {
                        Connection tmpConnection = ConnUtil.getRealConnection(conn);
                        tmpConnection.rollback();
                    }
                } finally {
                    if (Collections.singletonList(conn).get(0) != null) {
                        conn.close();
                    }

                }
            }

            throw var11;
        }
    }

    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
