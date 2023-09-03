package org.openea.eap.extj.database.util;

import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.model.TenantVO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.model.MultiTenantType;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;

/**
 * disable tenant
 */
public class TenantDataSourceUtil {

    private static ConfigValueUtil configValueUtil;

    public TenantDataSourceUtil() {
    }


    public static boolean isMultiTenancy() {
        return false;
    }

    public static boolean isTenantColumn() {
        return isMultiTenancy() && MultiTenantType.COLUMN.eq(configValueUtil.getMultiTenantType());
    }

    public static boolean isTenantAssignDataSource() {
        return false;
    }

    public static void initDataSourceTenantDbName(DbSourceOrDbLink dataSourceUtil) {
        if (isMultiTenancy()) {
            if (isTenantAssignDataSource()) {
                return;
            }

            if (!(dataSourceUtil instanceof DataSourceUtil) || dataSourceUtil instanceof DbLinkEntity && !"0".equals(((DbLinkEntity)dataSourceUtil).getId()) && ((DbLinkEntity)dataSourceUtil).getId() != null) {
                return;
            }

            boolean isColumn = isTenantColumn();
            DataSourceUtil ds = (DataSourceUtil)dataSourceUtil;

            // ignore tenant thing

        }
    }

    public static TenantVO switchTenant(String tenantId) throws LoginException {
//        TenantVO tenantVO = getTenantInfo(tenantId);
//        switchTenant(tenantId, tenantVO);
//        return tenantVO;
        return null;
    }


    public static String getTenantName() {
        String result = "";
        if (isMultiTenancy() && !DataSourceContextHolder.isAssignDataSource()) {
            result = DataSourceContextHolder.getDatasourceName();
            result = convertSchemaName(result);
        }

        return result;
    }

    public static String convertSchemaName(String dbName) {
        if (StringUtil.isNotEmpty(dbName)) {
            switch (DynamicDataSourceUtil.dataSourceUtil.getDbType()) {
                case "PostgreSQL":
                    dbName = dbName.toLowerCase();
                    break;
                case "Oracle":
                    dbName = dbName.toUpperCase();
            }
        }

        return dbName;
    }

    public static String getTenantSchema() {
        String result = "";
        if (isMultiTenancy() && configValueUtil.getMultiTenantType().eq(MultiTenantType.SCHEMA)) {
            result = getTenantName();
        }
        return result;
    }

    public static void initTenantAssignDataSource() {
    }


}
