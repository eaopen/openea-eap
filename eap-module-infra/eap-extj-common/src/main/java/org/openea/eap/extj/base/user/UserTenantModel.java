package org.openea.eap.extj.base.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserTenantModel implements Serializable {

    private String tenantId;
    private String dbName;
    private boolean isAssignDataSource;
}
