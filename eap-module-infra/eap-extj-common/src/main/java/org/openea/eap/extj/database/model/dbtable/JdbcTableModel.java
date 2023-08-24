package org.openea.eap.extj.database.model.dbtable;

import lombok.Data;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.exception.DataException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * eap todo
 */
@Data
public class JdbcTableModel {

    private String dbEncode;
    private String table;
    private String tableType;
    private String comment;
    private String primaryField;
    private List<JdbcColumnModel> jdbcColumnModelList;

    public JdbcTableModel(){

    }
    public JdbcTableModel(DbLinkEntity dbLinkEntity, String table) throws Exception {
        Connection conn = ConnUtil.ConnCommon.getConnRemarks(dbLinkEntity);

        try {
            ResultSet rs = getTableMetaDateRs(conn, table);

            try {
                String primaryField = getPrimary(conn, table);
                if (!rs.next()) {
                    throw new DataException(MsgCode.DB009.get() + "：" + table);
                }

                this.dbEncode = dbLinkEntity.getDbType();
                this.table = rs.getString("TABLE_NAME");
                this.tableType = rs.getString("TABLE_TYPE");
                this.comment = rs.getString("REMARKS");
                this.primaryField = primaryField;
                this.jdbcColumnModelList = JdbcColumnModel.getList(conn, table, primaryField);
            } finally {
                if (Collections.singletonList(rs).get(0) != null) {
                    rs.close();
                }

            }
        } finally {
            if (Collections.singletonList(conn).get(0) != null) {
                conn.close();
            }

        }

    }

    public DbTableFieldModel convertDbTableFieldModel() throws Exception {
        DbTableFieldModel dbTableFieldModel = new DbTableFieldModel();
        dbTableFieldModel.setTable(this.table);
        dbTableFieldModel.setComment(this.comment);
        List<DbFieldModel> dbFieldModelList = new ArrayList();
        Iterator var3 = this.jdbcColumnModelList.iterator();

        while(var3.hasNext()) {
            JdbcColumnModel jdbcColumnModel = (JdbcColumnModel)var3.next();
            dbFieldModelList.add(jdbcColumnModel.convertDbFieldModel(this.dbEncode));
        }

        dbTableFieldModel.setDbFieldModelList(dbFieldModelList);
        return dbTableFieldModel;
    }

    public static List<JdbcTableModel> getList(Connection conn) throws Exception {
        ResultSet rs = getTableMetaDateRs(conn);

        try {
            List<JdbcTableModel> list = new ArrayList();

            while(rs.next()) {
                JdbcTableModel jdbcTableModel = new JdbcTableModel();
                jdbcTableModel.setTable(rs.getString("TABLE_NAME"));
                jdbcTableModel.setTableType(rs.getString("TABLE_TYPE"));
                jdbcTableModel.setComment(rs.getString("REMARKS"));
                jdbcTableModel.setJdbcColumnModelList(JdbcColumnModel.getList(conn, jdbcTableModel.getTable(), jdbcTableModel.getPrimaryField()));
                list.add(jdbcTableModel);
            }

            ArrayList var7 = (ArrayList) list;
            return var7;
        } finally {
            if (Collections.singletonList(rs).get(0) != null) {
                rs.close();
            }

        }
    }

    public static String getPrimary(DbSourceOrDbLink dbSourceOrDbLink, String table) throws SQLException {
        Connection conn = ConnUtil.getConnOrDefault(dbSourceOrDbLink);

        String var3;
        try {
            var3 = getPrimary(conn, table);
        } finally {
            if (Collections.singletonList(conn).get(0) != null) {
                conn.close();
            }

        }

        return var3;
    }

    public static String getPrimary(Connection conn, String table) throws SQLException {
        ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), (String)null, table);

        String var3;
        try {
            if (rs.next()) {
                var3 = rs.getString("COLUMN_NAME");
                return var3;
            }

            var3 = "";
        } finally {
            if (Collections.singletonList(rs).get(0) != null) {
                rs.close();
            }

        }

        return var3;
    }

    public static List<Map<String, String>> getPrimaryMapList(Connection conn, String schema) throws SQLException {
        ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), schema, (String)null);

        try {
            List<Map<String, String>> list = new ArrayList();

            while(rs.next()) {
                Map<String, String> map = new HashMap();
                map.put(rs.getString("TABLE_NAME"), rs.getString("COLUMN_NAME"));
                list.add(map);
            }
            return list;
        } finally {
            if (Collections.singletonList(rs).get(0) != null) {
                rs.close();
            }

        }
    }

    public static ResultSet getTableMetaDateRs(Connection conn, String table) throws SQLException {
        return conn.getMetaData().getTables(conn.getCatalog(), (String)null, table, new String[]{"TABLE"});
    }

    private static ResultSet getTableMetaDateRs(Connection conn) throws SQLException {
        return getTableMetaDateRs(conn, (String)null);
    }
}
