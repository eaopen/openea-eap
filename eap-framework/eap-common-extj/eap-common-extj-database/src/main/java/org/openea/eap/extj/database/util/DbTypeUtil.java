package org.openea.eap.extj.database.util;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 处理判断数据库类型有关工具类
 *
 *
 */
public class DbTypeUtil {

    /*===========================数据库对象（重载）=====================================*/

    /**
     * 根据数据库名获取数据库对象
     * Case insensitive 大小写不敏感
     * @param dbSourceOrDbLink 数据源
     * @return  DbTableEnum2 数据表枚举类
     */
    public static DbBase getDb(DbSourceOrDbLink dbSourceOrDbLink) throws DataException {
        String dbSourceOrDbLinkEncode = getEncode(dbSourceOrDbLink.init());
        return getDbCommon(dbSourceOrDbLinkEncode);
    }

    public static DbBase getDb(Connection conn) throws DataException{
        try {
            return getDb(conn.getMetaData().getURL());
        } catch (SQLException | DataException e) {
            e.printStackTrace();
        }
        throw new DataException(MsgCode.DB005.get());
    }

    public static DbBase getDb(String url) throws DataException {
        String dbType = url.split(":")[1];
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbType.equals(dbBase.getConnUrlEncode())){
                return dbBase;
            }
        }
        throw new DataException(MsgCode.DB003.get());
    }

    public static DbBase getEncodeDb(String dbEncode) throws DataException {
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbEncode.equals(dbBase.getJnpfDbEncode())){
                return dbBase;
            }
        }
        throw new DataException(MsgCode.DB003.get());
    }

    public static DbBase getDriver(String dbType) throws DataException {
        for(DbBase dbBase : DbBase.DB_BASES){
            if(dbBase.getJnpfDbEncode().contains(dbType)){
                return dbBase;
            }
        }
        throw new DataException(MsgCode.DB003.get());
    }

    /*===========================校验数据库类型=============================*/
    /**
     * IOC思想
     * @return 是否匹配
     */
    private static Boolean checkDb(DbSourceOrDbLink dataSourceMod, String encode){
        DbLinkEntity dataSourceDTO = dataSourceMod.init();
        String dataSourDbEncode = null;
        try {
            dataSourDbEncode = getEncode(dataSourceDTO);
        } catch (DataException e) {
            e.printStackTrace();
        }
        return encode.equals(dataSourDbEncode);
    }

    public static Boolean checkOracle(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.ORACLE);
    }

    public static Boolean checkMySQL(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.MYSQL);
    }

    public static Boolean checkSQLServer(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.SQL_SERVER);
    }

    public static Boolean checkDM(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.DM);
    }

    public static Boolean checkKingbase(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod, DbBase.KINGBASE_ES);
    }

    public static Boolean checkPostgre(DbSourceOrDbLink dataSourceMod){
        return checkDb(dataSourceMod,DbBase.POSTGRE_SQL);
    }

    /*============================专用代码区域=========================*/

    /**
     * MybatisPlusConfig
     */
    public static <T extends DataSourceUtil>DbType getMybatisEnum(T dataSourceUtil) throws DataException{
        return getDb(dataSourceUtil).getMpDbType();
    }

    /**
     * 默认数据库与数据连接判断
     */
    public static Boolean compare(String dbType1,String dbType2) throws DataException{
        dbType1 = checkDbTypeExist(dbType1,false);
        dbType2 = checkDbTypeExist(dbType2,false);
        if(dbType1 != null && dbType2 != null){
            return dbType1.equals(dbType2);
        }else {
            return false;
        }
    }

    /*=========================内部复用代码================================*/

    /*====标准类型（重载）==*/

    /**
     * 获取标准类型编码
     * 根据DbType
     * @param dataSourceDTO 数据源
     * @return String
     */
    private static String getEncode(DbLinkEntity dataSourceDTO)throws DataException{
        return checkDbTypeExist(dataSourceDTO.getDbType(), true);
    }
    /**============**

     /**
     * 获取数据库对象
     * @param encode 数据标准编码
     * @return 数据库基类
     */
    private static DbBase getDbCommon(String encode){
        for (DbBase db : DbBase.DB_BASES) {
            if (db.getJnpfDbEncode().equalsIgnoreCase(encode)) {
                return db;
            }
        }
        return null;
    }

    /**
     * 0、校验数据类型是否符合编码标准（包含即可）
     * @param dbType 数据类型
     * @param exceptionOnOff 无匹配是否抛异常
     * @return 数据标准编码
     * @throws DataException 数据库类型不符合编码
     */
    private static String checkDbTypeExist(String dbType, Boolean exceptionOnOff) throws DataException {
        for(String enEncode : DbBase.DB_ENCODES){
            if(enEncode.equalsIgnoreCase(dbType)){
                return enEncode;
            }
        }
        if(exceptionOnOff){
            throw new DataException(MsgCode.DB001.get());
        }
        return null;
    }

    /**
     * 根据数据库连接获取的产品名称获取数据库类型编码
     * @param databaseProductName
     * @return
     */
    public static String getDbEncodeByProductName(String databaseProductName){
        switch (databaseProductName.toUpperCase()){
            case "ORACLE":
                return DbBase.ORACLE;
            case "POSTGRESQL":
                return DbBase.POSTGRE_SQL;
            case "MICROSOFT SQL SERVER":
                return DbBase.SQL_SERVER;
            default:
                return "";
        }
    }

}
