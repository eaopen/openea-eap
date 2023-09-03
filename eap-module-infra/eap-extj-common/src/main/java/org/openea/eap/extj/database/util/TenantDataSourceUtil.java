package org.openea.eap.extj.database.util;

import org.openea.eap.extj.database.model.TenantVO;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.exception.LoginException;

/**
 * disable tenant
 */
public class TenantDataSourceUtil {

    public static boolean isMultiTenancy() {
        return false;
    }

    public static boolean isTenantAssignDataSource() {
        return false;
    }

    public static void initDataSourceTenantDbName(DbSourceOrDbLink dataSourceUtil) {}


    public static TenantVO switchTenant(String tenantId) throws LoginException {
//        TenantVO tenantVO = getTenantInfo(tenantId);
//        switchTenant(tenantId, tenantVO);
//        return tenantVO;
        return null;
    }


    public static String getTenantSchema() {
        return null;
    }

    public static void initTenantAssignDataSource() {
    }
}
