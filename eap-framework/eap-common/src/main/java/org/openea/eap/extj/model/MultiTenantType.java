package org.openea.eap.extj.model;

public enum MultiTenantType {
    COLUMN("字段模式"),
    SCHEMA("SCHEMA模式");

    private String directions;

    private MultiTenantType(String directions) {
        this.directions = directions;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        return val == null ? false : this.eq(val.name());
    }

    public String getDirections() {
        return this.directions;
    }
}
