package org.openea.eap.extj.database.util;

public class DbTypeUtil {

    public static String getDbEncodeByProductName(String databaseProductName) {
        switch (databaseProductName.toUpperCase()) {
            case "ORACLE":
                return "Oracle";
            case "POSTGRESQL":
                return "PostgreSQL";
            case "MICROSOFT SQL SERVER":
                return "SQLServer";
            default:
                return "";
        }
    }
}