package org.openea.eap.extj.database.source.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import org.openea.eap.extj.database.constant.DbConst;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.model.DbStruct;

/**
 * 达梦模型
 *
 * 
 */
public class DbDM extends DbBase {

    @Override
    protected void init() {
        setInstance(
                DM,
                DbType.DM,
                com.alibaba.druid.DbType.dm,
                "5236",
                "SYSDBA",
                "dm",
                "dm.jdbc.driver.DmDriver",
                "jdbc:dm://{host}:{port}/{schema}");
    }

    @Override
    protected String getConnUrl(String prepareUrl, String host, Integer port, DbStruct struct){
        prepareUrl = super.getConnUrl(prepareUrl, host, port, null);
        return prepareUrl.replace(DbConst.DB_SCHEMA, struct.getDmDbSchema());
    }

//    public static void setDmTableModel(DbConnDTO connDTO, List<DbTableModel> tableModelList) {
//        //达梦特殊方法
//        try {
//            @Cleanup Connection dmConn = connDTO.getConn();
//            tableModelList.forEach(tm -> {
//                try {
//                    Integer sum = DbDM.getSum(dmConn, tm.getTable());
//                    tm.setSum(sum);
//                } catch (DataException e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Integer getSum(Connection connection, String table) throws DataException {
//        String sql = "SELECT COUNT(*) as F_SUM FROM " + table;
//        return JdbcUtil.queryOneInt(connection, sql, "F_SUM");
//    }

}
