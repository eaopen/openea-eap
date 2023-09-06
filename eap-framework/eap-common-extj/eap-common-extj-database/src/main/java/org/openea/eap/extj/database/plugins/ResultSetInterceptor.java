package org.openea.eap.extj.database.plugins;

import org.openea.eap.extj.database.util.ResetSetHolder;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Mybatis ResultSet拦截器
 * @see ResetSetHolder
 * 
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Statement statement = (Statement) args[0];
        ResultSet rs = statement.getResultSet();
        if (rs != null) {
            ResetSetHolder.setResultSet(rs);
        }
        Object result;
        try{
            result = invocation.proceed();
        }finally{
            ResetSetHolder.clear();
        }
        return result;
    }
}
