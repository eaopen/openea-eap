package org.openea.eap.extj.database.util;

import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.dbtable.JdbcTableModel;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能
 *
 *
 */
public class JdbcOriginUtil {

    /**
     * 获取字段元数据map集合(所有表)
     * @param conn 数据连接
     * @return ignore
     * @throws SQLException ignore
     */
    public static List<Map<String,String>> getColumnMataMapList(Connection conn, String table) throws Exception {
        @Cleanup ResultSet rs = JdbcColumnModel.getColumnMetaDateRs(conn, table);
        List<Map<String,String>> mapList = new ArrayList<>();
        while (rs.next()) {
            /*===================遍历表字段所有元数据=====================*/;
            Map<String,String> map = new HashMap<>(16);
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                map.put(rs.getMetaData().getColumnName(i), rs.getString(i));
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 获取表元数据map集合
     * @param conn 数据连接
     * @return ignore
     * @throws SQLException ignore
     */
    public static List<Map<String,String>> getTableMapList(Connection conn) throws SQLException {
        @Cleanup ResultSet rs = JdbcTableModel.getTableMetaDateRs(conn, null);
        List<Map<String,String>> mapList = new ArrayList<>();
        while (rs.next()) {
            Map<String,String> map = new HashMap<>();
            // 模式下所有表元数据
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            // 遍历表所有元数据信息,一个map包含一张表
            for (int i = 1; i <=  resultSetMetaData.getColumnCount(); i++) {
                map.put(resultSetMetaData.getColumnName(i), rs.getString(i));
            }
            mapList.add(map);
        }
        return mapList;
    }

}
