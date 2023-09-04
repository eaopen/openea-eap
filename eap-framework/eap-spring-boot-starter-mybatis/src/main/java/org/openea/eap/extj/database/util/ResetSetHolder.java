package org.openea.eap.extj.database.util;

import java.sql.ResultSet;

public class ResetSetHolder {
    private static final ThreadLocal<ResultSet> RESULTSET_HOLDER = new ThreadLocal();

    public ResetSetHolder() {
    }

    public static ResultSet getResultSet() {
        return (ResultSet)RESULTSET_HOLDER.get();
    }

    public static void setResultSet(ResultSet resultSet) {
        RESULTSET_HOLDER.set(resultSet);
    }

    public static void clear() {
        RESULTSET_HOLDER.remove();
    }
}