package org.openea.eap.extj.database.enums;

/**
 * 
 */
public enum TenantDbSchema {
    /**
     * 默认数据源
     */
    DEFAULT(0),
    /**
     * 指定数据源
     */
    REMOTE(1);


    private final int type;

    TenantDbSchema(int type) {
        this.type = type;
    }
}
