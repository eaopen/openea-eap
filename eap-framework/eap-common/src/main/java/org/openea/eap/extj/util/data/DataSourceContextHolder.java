package org.openea.eap.extj.util.data;

/**
 * todo eap待处理
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT_DB_NAME_HOLDER = new ThreadLocal();
    private static final ThreadLocal<String> CONTEXT_DB_ID_HOLDER = new ThreadLocal();
    private static final ThreadLocal<Boolean> CONTEXT_ASSIGN_HOLDER = new ThreadLocal();

    public DataSourceContextHolder() {
    }

    public static void setDatasource(String dbId, String dbName, boolean assign) {
        CONTEXT_DB_NAME_HOLDER.set(dbName);
        CONTEXT_DB_ID_HOLDER.set(dbId);
        CONTEXT_ASSIGN_HOLDER.set(assign);
    }

    public static String getDatasourceId() {
        String str = (String)CONTEXT_DB_ID_HOLDER.get();
        return str;
    }

    public static String getDatasourceName() {
        String str = (String)CONTEXT_DB_NAME_HOLDER.get();
        return str;
    }

    public static Boolean isAssignDataSource() {
        return Boolean.TRUE.equals(CONTEXT_ASSIGN_HOLDER.get());
    }

    public static void clearDatasourceType() {
        CONTEXT_DB_NAME_HOLDER.remove();
        CONTEXT_DB_ID_HOLDER.remove();
        CONTEXT_ASSIGN_HOLDER.remove();
    }
}
