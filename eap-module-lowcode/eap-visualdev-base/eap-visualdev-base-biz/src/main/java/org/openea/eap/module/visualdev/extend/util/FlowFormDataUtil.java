package org.openea.eap.module.visualdev.extend.util;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.module.visualdev.extend.model.flow.DataModel;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class FlowFormDataUtil {
    public String getKey(Connection conn, String mainTable, Integer primaryKeyPolicy) throws SQLException {
        String pKeyName = "";
//        if (primaryKeyPolicy == 2) {
//            pKeyName = TableFeildsEnum.FLOWTASKID.getField();
//        } else {
            //catalog 数据库名
            String catalog = conn.getCatalog();
            @Cleanup ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(catalog, null, mainTable);
            while (primaryKeyResultSet.next()) {
                pKeyName = primaryKeyResultSet.getString("COLUMN_NAME");
            }
            primaryKeyResultSet.close();
//        }
        String databaseProductName = conn.getMetaData().getDatabaseProductName().trim();
        if (databaseProductName.contains("Oracle") || databaseProductName.contains("DM DBMS")) {
            pKeyName = pKeyName.toUpperCase();
        }
        return pKeyName;
    }

    public void create(DataModel dataModel) {
    }

    public void update(DataModel dataModel) {
    }
}
