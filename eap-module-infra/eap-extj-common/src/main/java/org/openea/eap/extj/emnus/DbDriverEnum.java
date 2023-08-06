package org.openea.eap.extj.emnus;

public enum DbDriverEnum {
    MYSQL("com.mysql.cj.jdbc.Driver"),
    ORACLE("oracle.jdbc.OracleDriver"),
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver");

    private String dbDriver;

    private DbDriverEnum(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbDriver() {
        return this.dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
}
