package org.openea.eap.extj.database.sql.model;

import lombok.Data;

@Data
public class DbStruct {
    private String userName;
    private String dbTableSpace;
    private String mysqlDbName;
    private String oracleDbSchema;
    private String oracleParam;
    private String sqlServerDbName;
    private String sqlServerDbSchema = "dbo";
    private String dmDbSchema;
    private String kingBaseDbName;
    private String kingBaseDbSchema = "public";
    private String postGreDbName;
    private String postGreDbSchema = "public";

}
