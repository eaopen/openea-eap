package org.openea.eap.extj.database.util;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.exception.DataException;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTypeUtil {
    public DbTypeUtil() {
    }

    public static DbBase getDb(DbSourceOrDbLink dbSourceOrDbLink) throws DataException {
        String dbSourceOrDbLinkEncode = getEncode(dbSourceOrDbLink.init());
        return getDbCommon(dbSourceOrDbLinkEncode);
    }

    public static DbBase getDb(Connection conn) throws DataException {
        try {
            return getDb(conn.getMetaData().getURL());
        } catch (DataException | SQLException var2) {
            var2.printStackTrace();
            throw new DataException(MsgCode.DB005.get());
        }
    }

    public static DbBase getDb(String url) throws DataException {
        String dbType = url.split(":")[1];
        DbBase[] var2 = DbBase.DB_BASES;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            DbBase dbBase = var2[var4];
            if (dbType.equals(dbBase.getConnUrlEncode())) {
                return dbBase;
            }
        }

        throw new DataException(MsgCode.DB003.get());
    }

    public static DbBase getEncodeDb(String dbEncode) throws DataException {
        DbBase[] var1 = DbBase.DB_BASES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DbBase dbBase = var1[var3];
            if (dbEncode.equals(dbBase.getJnpfDbEncode())) {
                return dbBase;
            }
        }

        throw new DataException(MsgCode.DB003.get());
    }

    public static DbBase getDriver(String dbType) throws DataException {
        DbBase[] var1 = DbBase.DB_BASES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DbBase dbBase = var1[var3];
            if (dbBase.getJnpfDbEncode().contains(dbType)) {
                return dbBase;
            }
        }

        throw new DataException(MsgCode.DB003.get());
    }

    private static Boolean checkDb(DbSourceOrDbLink dataSourceMod, String encode) {
        DbLinkEntity dataSourceDTO = dataSourceMod.init();
        String dataSourDbEncode = null;

        try {
            dataSourDbEncode = getEncode(dataSourceDTO);
        } catch (DataException var5) {
            var5.printStackTrace();
        }

        return encode.equals(dataSourDbEncode);
    }

    public static Boolean checkOracle(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "Oracle");
    }

    public static Boolean checkMySQL(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "MySQL");
    }

    public static Boolean checkSQLServer(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "SQLServer");
    }

    public static Boolean checkDM(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "DM");
    }

    public static Boolean checkKingbase(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "KingbaseES");
    }

    public static Boolean checkPostgre(DbSourceOrDbLink dataSourceMod) {
        return checkDb(dataSourceMod, "PostgreSQL");
    }

    public static <T extends DataSourceUtil> DbType getMybatisEnum(T dataSourceUtil) throws DataException {
        return getDb((DbSourceOrDbLink)dataSourceUtil).getMpDbType();
    }

    public static Boolean compare(String dbType1, String dbType2) throws DataException {
        dbType1 = checkDbTypeExist(dbType1, false);
        dbType2 = checkDbTypeExist(dbType2, false);
        return dbType1 != null && dbType2 != null ? dbType1.equals(dbType2) : false;
    }

    private static String getEncode(DbLinkEntity dataSourceDTO) throws DataException {
        return checkDbTypeExist(dataSourceDTO.getDbType(), true);
    }

    private static DbBase getDbCommon(String encode) {
        DbBase[] var1 = DbBase.DB_BASES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DbBase db = var1[var3];
            if (db.getJnpfDbEncode().equalsIgnoreCase(encode)) {
                return db;
            }
        }

        return null;
    }

    private static String checkDbTypeExist(String dbType, Boolean exceptionOnOff) throws DataException {
        String[] var2 = DbBase.DB_ENCODES;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String enEncode = var2[var4];
            if (enEncode.equals(dbType)) {
                return enEncode;
            }
        }

        if (exceptionOnOff) {
            throw new DataException(MsgCode.DB001.get());
        } else {
            return null;
        }
    }

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