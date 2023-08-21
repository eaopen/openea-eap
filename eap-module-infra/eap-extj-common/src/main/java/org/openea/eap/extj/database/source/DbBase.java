package org.openea.eap.extj.database.source;

public abstract class DbBase {

    public static final String MYSQL = "MySQL";
    public static final String DM = "DM";
    public static final String KINGBASE_ES = "KingbaseES";
    public static final String ORACLE = "Oracle";
    public static final String POSTGRE_SQL = "PostgreSQL";
    public static final String SQL_SERVER = "SQLServer";
    public static final String DORIS = "Doris";
    //public static final DbBase[] DB_BASES = new DbBase[]{new DbMySQL(), new DbSQLServer(), new DbDM(), new DbOracle(), new DbKingbase(), new DbPostgre(), new DbDoris()};
    public static final String[] DB_ENCODES = new String[]{"MySQL", "Oracle", "SQLServer", "DM", "KingbaseES", "PostgreSQL", "Doris"};


}
