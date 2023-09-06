package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

/**
 * Oracle模型
 *
 * 
 */
public class DbOracle extends DbBase {

    /*================Oracle4种数据库连接url=================*/
    /**
     * SID
     * 或：jdbc:oracle:thin:@{host}:{port}/{SID}
     */
    private static final String SID_URL = "jdbc:oracle:thin:@{host}:{port}:{schema}";
    private static final String SID_SIGN = "SID";
    /**
     * SERVICE：服务名
     */
    private static final String SERVICE_URL = "jdbc:oracle:thin:@//{host}:{port}/{schema}";
    private static final String SERVICE_SIGN = "SERVICE";
    /**
     * SCHEMA：模式
     */
    private static final String SCHEMA_URL =
            "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST={host})(PORT={port})))(CONNECT_DATA=(SERVICE_NAME={schema})))";
    private static final String SCHEMA_SIGN = "SCHEMA";
    /**
     * TNS
     */
    private static final String TNS_URL = "jdbc:oracle:thin:@{schema}";
    private static final String TNS_SIGN = "TNS";

    public static final String ORACLE_SERVICE = "oracleService";
    /**
     * 连接扩展参数
     */
    public static final String ORACLE_LINK_TYPE = "oracleLinkType";
    public static final String ORACLE_ROLE = "oracleRole";

    @Override
    protected void init() {
        setInstance(
                ORACLE,
                DbType.ORACLE,
                com.alibaba.druid.DbType.oracle,
                "1521",
                "sys",
                "oracle",
                "oracle.jdbc.OracleDriver",
                "");
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        String schema = struct.getOracleDbSchema();
        if(StringUtil.isNotEmpty(struct.getOracleParam()) && StringUtil.isEmpty(prepareUrl)) {
            Map<String, Object> oracleParamMap = JsonUtil.stringToMap(struct.getOracleParam());
            if (oracleParamMap.size() > 0) {
                schema = oracleParamMap.get(DbOracle.ORACLE_SERVICE).toString();
                String urlType = oracleParamMap.get(DbOracle.ORACLE_LINK_TYPE).toString();
                switch (urlType) {
                    case SID_SIGN:
                        prepareUrl = SID_URL;
                        break;
                    case SERVICE_SIGN:
                        prepareUrl = SERVICE_URL;
                        break;
                    case SCHEMA_SIGN:
                        prepareUrl = SCHEMA_URL;
                        break;
                    case TNS_SIGN:
                        prepareUrl = TNS_URL;
                        break;
                    default:
                        // throw new DataException("Oracle连接类型指定失败");
                        break;
                }
            }
        }
        if(StringUtil.isEmpty(prepareUrl)){
            prepareUrl = SID_URL;
        }
        return super.getConnUrl(prepareUrl, host, port, null).replace(DbConst.DB_SCHEMA, schema);
    }

    @Override
    protected String getDynamicTableName(String tableName) {
        return DataSourceContextHolder.getDatasourceName().toUpperCase()+"."+tableName;
    }

    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception{
        if(model.getDataType().toUpperCase().contains("TIMESTAMP")){
            model.setDataType("TIMESTAMP");
        }
        super.setPartFieldModel(model, result);
    }

    /**
     * Oracle特殊添加数据连接方式
     */
    public Connection getOracleConn(DbLinkEntity dsd, String url) throws DataException {
        String logonUser = "";
        if(StringUtil.isNotEmpty(dsd.getOracleParam())){
            Map<String, Object> oracleParamMap = JsonUtil.stringToMap(dsd.getOracleParam());
            if(oracleParamMap.size() > 0){
                logonUser = oracleParamMap.get(DbOracle.ORACLE_ROLE).toString();
            }
        }
        return createOracleConn(driver, logonUser, dsd.getUserName(), dsd.getPassword(), url);
    }

    private static Connection createOracleConn(String driver, String logonUser, String userName, String password, String url) throws DataException {
        //Oracle登录角色设置（Default，SYSDBA，SYSOPER）
        Properties conProps = DbOracle.setConnProp(logonUser, userName, password);
        return ConnUtil.ConnCommon.createConnByProp(driver, conProps.getProperty("user"), password, url, conProps);
    }

    public static Properties setConnProp(String logonUser,String userName, String password){
        Properties conProps = new Properties();
        // 使用角色登录：userName + :@{角色}
        if(!logonUser.isEmpty()){
            //defaultRowPrefetch：从服务器预取的默认行数(默认值为“10”) String (containing integer value)
            conProps.put("defaultRowPrefetch", "15");
            /* 这里有一个风险：由于客户userName中含有一个或多个:@导致连接失败 */
            //internal_logon：允许您作为sys登录的权限，如sysdba或sysoper
            conProps.put("internal_logon", logonUser);
            conProps.put("user", userName);
        }
        conProps.put("user", userName);
        conProps.setProperty("password", password);
        return conProps;
    }

}
