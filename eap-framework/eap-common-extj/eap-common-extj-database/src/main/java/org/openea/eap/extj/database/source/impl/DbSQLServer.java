package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.util.data.DataSourceContextHolder;

/**
 * SQLServer模型
 *
 * 
 */
public class DbSQLServer extends DbBase {

    @Override
    protected void init() {
        setInstance(
                SQL_SERVER,
                DbType.SQL_SERVER,
                com.alibaba.druid.DbType.sqlserver,
                "1433",
                "sa",
                "sqlserver",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "jdbc:sqlserver://{host}:{port};databaseName={dbname}");
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getSqlServerDbName()).replace(DbConst.DB_SCHEMA, struct.getSqlServerDbSchema());
    }

    @Override
    protected String getDynamicTableName(String tableName) {
        return DataSourceContextHolder.getDatasourceName()+".dbo." + tableName;
    }

}
