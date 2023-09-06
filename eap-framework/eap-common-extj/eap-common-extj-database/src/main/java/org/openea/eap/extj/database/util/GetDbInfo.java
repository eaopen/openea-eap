package org.openea.eap.extj.database.util;

import org.openea.eap.extj.database.model.TenantLinkModel;
import org.openea.eap.extj.util.StringUtil;

/**
 * 获取数据库相关信息
 *
 * 
 */
public class GetDbInfo {

    /**
     * 得到url
     *
     * @param model
     * @return
     */
    public static String getUrl(TenantLinkModel model) {
        if (model != null) {
            // 如果使用自定义URL
            if (StringUtil.isNotEmpty(model.getConnectionStr())) {
                return model.getConnectionStr();
            }
            // 不是使用自定义URL
            String url = "";
            if ("mysql".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:mysql://{host}:{port}/{dbname}";
            } else if ("SQLServer".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:sqlserver://{host}:{port};databaseName={dbname}";
            } else if ("Oracle".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:oracle:thin:@//{host}:{port}/{schema}";
            } else if ("DM8".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:dm://{host}:{port}/{schema}";
            } else if ("KingbaseES".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:kingbase8://{host}:{port}/{dbname}?currentSchema={schema}";
            } else if ("PostgreSQL".equalsIgnoreCase(model.getDbType())) {
                url = "jdbc:postgresql://{host}:{port}/{dbname}";
            }
            url = url.replace("{host}", model.getHost());
            url = url.replace("{port}", model.getPort());
            url = url.replace("{dbname}", model.getServiceName());
            url = url.replace("{schema}", model.getDbSchema());
            return url;
        }
        return null;
    }

    /**
     * 判断数据库类型
     *
     * @param driverName 驱动名称
     * @return
     */
    public static String getDbType(String driverName) {
        if (StringUtil.isNotEmpty(driverName)) {
            // 不是使用自定义URL
            String dbType = "";
            if (driverName.contains("mysql")) {
                dbType = "MySQL";
            } else if (driverName.contains("sqlserver")) {
                dbType = "SQLServer";
            } else if (driverName.contains("oracle")) {
                dbType = "Oracle";
            } else if (driverName.contains("dm")) {
                dbType = "DM8";
            } else if (driverName.contains("kingbase8")) {
                dbType = "KingbaseES";
            } else if (driverName.contains("postgresql")) {
                dbType = "PostgreSQL";
            }
            return dbType;
        }
        return null;
    }

}
