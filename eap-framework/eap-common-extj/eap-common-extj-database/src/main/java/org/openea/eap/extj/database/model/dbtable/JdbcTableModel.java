package org.openea.eap.extj.database.model.dbtable;

import org.openea.eap.extj.database.constant.RsColumnKeyConst;
import org.openea.eap.extj.database.constant.RsTableKeyConst;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.exception.DataException;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC元数据字段模型类型
 *
 *
 */
@Data
@NoArgsConstructor
public class JdbcTableModel {

    /**
     * 数据库类型
     */
    private String dbEncode;

    /**
     * 表名
     */
    private String table;

    /**
     * 表类型
     */
    private String tableType;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 主键字段
     */
    private String primaryField;

    /**
     * jdbc字段集合
     */
    private List<JdbcColumnModel> jdbcColumnModelList;

    /* ================== 构造方法 ================= */

    public JdbcTableModel(DbLinkEntity dbLinkEntity, String table) throws Exception {
        @Cleanup Connection conn = ConnUtil.ConnCommon.getConnRemarks(dbLinkEntity);
        @Cleanup ResultSet rs = getTableMetaDateRs(conn, table);
        String primaryField = getPrimary(conn, table);
        if (rs.next()) {
            this.dbEncode = dbLinkEntity.getDbType();
            this.table = rs.getString(RsTableKeyConst.TABLE_NAME);
            this.tableType = rs.getString(RsTableKeyConst.TABLE_TYPE);
            this.comment = rs.getString(RsTableKeyConst.REMARKS);
            this.primaryField = primaryField;
            this.jdbcColumnModelList = JdbcColumnModel.getList(conn, table, primaryField);
        }else {
            throw new DataException(MsgCode.DB009.get() + "：" + table);
        }
    }

    /* ================== 内部方法 ================= */

    public DbTableFieldModel convertDbTableFieldModel() throws Exception {
        // 转换表
        DbTableFieldModel dbTableFieldModel = new DbTableFieldModel();
        dbTableFieldModel.setTable(this.table);
        dbTableFieldModel.setComment(this.comment);
        // 转换字段集合
        List<DbFieldModel> dbFieldModelList = new ArrayList<>();
        for (JdbcColumnModel jdbcColumnModel : this.jdbcColumnModelList) {
            dbFieldModelList.add(jdbcColumnModel.convertDbFieldModel(this.dbEncode));
        }
        dbTableFieldModel.setDbFieldModelList(dbFieldModelList);
        return dbTableFieldModel;
    }

    /* ================== 静态方法 ================= */

    /**
     * 获取表元数据对象(所有表)
     * @param conn 数据连接
     * @return ignore
     * @throws SQLException ignore
     */
    public static List<JdbcTableModel> getList(Connection conn) throws Exception {
        @Cleanup ResultSet rs = getTableMetaDateRs(conn);
        List<JdbcTableModel> list = new ArrayList<>();
        while (rs.next()) {
            JdbcTableModel jdbcTableModel = new JdbcTableModel();
            jdbcTableModel.setTable(rs.getString(RsTableKeyConst.TABLE_NAME));
            jdbcTableModel.setTableType(rs.getString(RsTableKeyConst.TABLE_TYPE));
            jdbcTableModel.setComment(rs.getString(RsTableKeyConst.REMARKS));
            jdbcTableModel.setJdbcColumnModelList(JdbcColumnModel.getList(conn, jdbcTableModel.getTable(), jdbcTableModel.getPrimaryField()));
            list.add(jdbcTableModel);
        }
        return list;
    }


    public static String getPrimary(DbSourceOrDbLink dbSourceOrDbLink, String table) throws SQLException {
        @Cleanup Connection conn = ConnUtil.getConnOrDefault(dbSourceOrDbLink);
        return getPrimary(conn, table);
    }

    public static String getPrimary(Connection conn, String table) throws SQLException {
        //获取表主键
        @Cleanup ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), null, table);
        if(rs.next()){
            return rs.getString(RsColumnKeyConst.COLUMN_NAME);
        }
        return "";
    }

    public static List<Map<String, String>> getPrimaryMapList(Connection conn, String schema) throws SQLException {
        //获取表主键
        @Cleanup ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), schema, null);
        List<Map<String, String>> list = new ArrayList<>();
        while (rs.next()){
            Map<String, String> map = new HashMap<>();
            map.put(rs.getString(RsColumnKeyConst.TABLE_NAME), rs.getString(RsColumnKeyConst.COLUMN_NAME));
            list.add(map);
        }
        return list;
    }

    /* ================================== 结果集 ================================== */

    /**
     * 从conn中获取数据库的表元数据
     * @param conn 数据连接
     * @return 返回表元数据
     * @throws SQLException ignore
     */
    public static ResultSet getTableMetaDateRs(Connection conn, String table) throws SQLException {
        return conn.getMetaData().getTables(conn.getCatalog(), null, table, new String[]{"TABLE"});
    }

    private static ResultSet getTableMetaDateRs(Connection conn) throws SQLException {
        return getTableMetaDateRs(conn, null);
    }

}
