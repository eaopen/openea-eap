package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;

/**
 * MySQL模型
 *
 * 
 */
public class DbMySQL extends DbBase {

    @Override
    protected void init(){
        setInstance(
                MYSQL,
                DbType.MYSQL,
                com.alibaba.druid.DbType.mysql,
                "3306",
                "root",
                "mysql",
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://{host}:{port}/{dbname}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false"
                //connUrl = "jdbc:mysql://{host}:{port}/{dbname}?useUnicode=true&autoReconnect=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
                );
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getMysqlDbName());
    }

}
