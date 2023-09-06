package org.openea.eap.extj.database.util;

import java.sql.ResultSet;

/**
 * 在SqlSessionFactory.openSession().select方法里ResultHandler中获取
 * mybatis执行完后resultSet已关闭不可用
 *
 * 此缓存会在ResultSetInterceptor自动清空， 无需手动清空
 *
 * 
 */
public class ResetSetHolder {


    private static final ThreadLocal<ResultSet> RESULTSET_HOLDER = new ThreadLocal<>();

    public static ResultSet getResultSet(){
        return RESULTSET_HOLDER.get();
    }

    public static void setResultSet(ResultSet resultSet){
        RESULTSET_HOLDER.set(resultSet);
    }

    public static void clear(){
        RESULTSET_HOLDER.remove();
    }

}
