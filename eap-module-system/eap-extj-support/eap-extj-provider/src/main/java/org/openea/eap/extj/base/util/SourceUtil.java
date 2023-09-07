package org.openea.eap.extj.base.util;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.database.util.TenantDataSourceUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;


public class SourceUtil {

    public static DataSourceConfig dbConfig(String dbName, DataSourceUtil linkEntity) {
        if (linkEntity == null) {
            if(TenantDataSourceUtil.isTenantAssignDataSource()){
                linkEntity = TenantDataSourceUtil.getTenantAssignDataSource(DataSourceContextHolder.getDatasourceId()).toDbLinkEntity();
            }else{
                linkEntity = DynamicDataSourceUtil.dataSourceUtil.init();
            }
            if (!"PostgreSQL".equals(linkEntity.getDbType()) && StringUtil.isNotEmpty(dbName)) {
                linkEntity.setDbName(dbName);
            }
        }
        // todo
//        DataSourceConfig dsc = new DataSourceConfig();
//        try {
//            DbBase dbBase = DbTypeUtil.getDb(linkEntity);
//            dsc.setDbType(dbBase.getMpDbType());
//            dsc.setDriverName(dbBase.getDriver());
//            dsc.setUsername(linkEntity.getUserName());
//            dsc.setPassword(linkEntity.getPassword());
//            dsc.setSchemaName(linkEntity.getDbSchema());
//
//            // oracle 默认 schema = username
//            if (dbBase.getMpDbType().toString().equalsIgnoreCase(DbType.ORACLE.getDb())) {
//                dsc.setSchemaName(linkEntity.getUserName());
//            }
//
//            dsc.setUrl(ConnUtil.getUrl(linkEntity));
//        } catch (Exception e) {
//            e.getStackTrace();
//        }
//        return dsc;
        return null;
    }

}
