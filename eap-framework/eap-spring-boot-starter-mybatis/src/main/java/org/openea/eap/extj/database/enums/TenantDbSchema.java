package org.openea.eap.extj.database.enums;

public enum TenantDbSchema {
    DEFAULT(0),
    REMOTE(1);

    private final int type;

    private TenantDbSchema(int type) {
        this.type = type;
    }
}