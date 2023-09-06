package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;

import java.sql.ResultSet;
import java.util.regex.Pattern;

/**
 * PostgreSQL模型
 *
 * 
 */
public class DbPostgre extends DbBase {

    public static String DEF_SCHEMA = "public";

    @Override
    protected void init() {
        setInstance(
                POSTGRE_SQL,
                DbType.POSTGRE_SQL,
                com.alibaba.druid.DbType.postgresql,
                "5432",
                "postgres",
                "postgresql",
                "org.postgresql.Driver",
                "jdbc:postgresql://{host}:{port}/{dbname}");
    }

    @Override
    public String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct) {
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_NAME, struct.getPostGreDbName()).replace(DbConst.DB_SCHEMA, struct.getPostGreDbSchema());
    }

    @Override
    protected String getDynamicTableName(String tableName) {
        return DataSourceContextHolder.getDatasourceName().toLowerCase()+"."+tableName;
    }

    @Override
    public void setPartFieldModel(DbFieldModel model, ResultSet result) throws Exception {
        String nullSignStr = result.getString(DbAliasEnum.ALLOW_NULL.getAlias(this.getJnpfDbEncode()));
        model.setNullSign(DbAliasConst.ALLOW_NULL.getSign(nullSignStr.equals("YES") ? 1 : 0));
        super.setPartFieldModel(model, result);
    }

    private String getCheckSchema(String schema){
        if(StringUtil.isEmpty(schema)){
            // 默认public模式
            schema = DEF_SCHEMA;
        }
        return schema;
    }

    /**
     * 表存在大写与小写，导致大小写敏感，需要双引号
     * @param originTable 原始表名
     * @return 表名
     */
    public static String getTable(String originTable){
        if(Pattern.compile("[A-Z]").matcher(originTable).find()){
            return "\"" + originTable + "\"";
        }else {
            return originTable;
        }
    }

}
