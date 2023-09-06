package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;

import java.sql.ResultSet;

/**
 * 类功能
 *
 */
public class DbDoris extends DbBase {

    @Override
    protected void init() {
        setInstance(
                DORIS,
                DbType.MYSQL,
                com.alibaba.druid.DbType.mysql,
                "9030",
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

    @Override
    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception {
        long charLength = result.getLong(DbAliasEnum.CHAR_LENGTH.getAlias(this.getJnpfDbEncode()));
        // Doris 长文本格式为String，但Jdbc查出的数据类型为：varchar & charLength = 2147483643
        if(model.getDataType().equalsIgnoreCase("varchar") && charLength == 2147483643){
            model.setDataType("text");
        }
        super.setPartFieldModel(model, result);
    }

}
