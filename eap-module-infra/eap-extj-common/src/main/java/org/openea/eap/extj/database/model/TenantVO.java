package org.openea.eap.extj.database.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantVO implements Serializable {
    private String dbName;
}