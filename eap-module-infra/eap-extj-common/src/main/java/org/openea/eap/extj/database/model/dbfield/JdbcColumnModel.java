package org.openea.eap.extj.database.model.dbfield;

import lombok.Data;
import org.openea.eap.extj.database.model.dbfield.base.DbFieldModelBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class JdbcColumnModel extends DbFieldModelBase {
    private String label;
    private Integer dtJdbcEncode;
    private Object value;
    protected String defaultValue;
    private String table;
    private String size;
    private Long columnSize;
    private Integer numPrecRadix;
    private Integer decimalDigits;
    private String ordinalPosition;
    private String autoIncrement;
    private String javaDataType;

    public DbFieldModel convertDbFieldModel(String dbEncode) throws Exception {
        DbFieldModel dbFieldModel = new DbFieldModel();
        dbFieldModel.setField(this.getField());
        dbFieldModel.setComment(this.getComment());
        dbFieldModel.setDataType(this.getDataType());
        dbFieldModel.setIsPrimaryKey(this.getIsPrimaryKey());
        dbFieldModel.setIsAutoIncrement(convertIsAutoIncrement(dbEncode, this.getAutoIncrement()));
        dbFieldModel.setNullSign(this.getNullSign());
        dbFieldModel.setDefaultValue(this.getDefaultValue());
        DtInterface dtEnum = DtInterface.newInstanceByDt(this.getDataType(), dbEncode);

        assert dtEnum != null;

        dbFieldModel.setDtModelDTO(new DtModelDTO(dtEnum, this.columnSize, Integer.parseInt(this.columnSize.toString()), this.decimalDigits));
        return dbFieldModel;
    }

    private static Boolean convertIsAutoIncrement(String dbEncode, String sign) {
        switch (dbEncode) {
            case "MySQL":
                return sign.equalsIgnoreCase("YES");
            case "Oracle":
            case "SQLServer":
            case "KingbaseES":
            case "DM":
            case "PostgreSQL":
            default:
                return false;
        }
    }

    public static List<JdbcColumnModel> getList(Connection conn, String table, String primaryField) throws Exception {
        ResultSet rs = getColumnMetaDateRs(conn, table);

        try {
            List<JdbcColumnModel> list = new ArrayList();

            while(rs.next()) {
                JdbcColumnModel column = new JdbcColumnModel();
                column.setTable(rs.getString("TABLE_NAME"));
                column.setField(rs.getString("COLUMN_NAME"));
                column.setJavaDataType(rs.getString("DATA_TYPE"));
                column.setDataType(rs.getString("TYPE_NAME"));
                column.setColumnSize(rs.getLong("COLUMN_SIZE"));
                column.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                column.setComment(rs.getString("REMARKS"));
                column.setDefaultValue(rs.getString("COLUMN_DEF"));
                column.setLength(rs.getString("CHAR_OCTET_LENGTH"));
                column.setOrdinalPosition(rs.getString("ORDINAL_POSITION"));
                String isNull = rs.getString("IS_NULLABLE");
                column.setNullSign(isNull.equalsIgnoreCase("YES") ? "NULL" : "NOT NULL");

                try {
                    column.setAutoIncrement(rs.getString("IS_AUTOINCREMENT"));
                } catch (Exception var11) {
                }

                column.setIsPrimaryKey(column.getField().equals(primaryField));
                list.add(column);
            }

            ArrayList var13 = list;
            return var13;
        } finally {
            if (Collections.singletonList(rs).get(0) != null) {
                rs.close();
            }

        }
    }

    public static List<JdbcColumnModel> getList(ResultSet rs, Boolean isLowercase, Boolean isValue) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        List<JdbcColumnModel> dbColumnModelList = new ArrayList();

        for(int i = 1; i <= md.getColumnCount(); ++i) {
            JdbcColumnModel model = new JdbcColumnModel();
            model.setTable(md.getTableName(i));
            model.setField(md.getColumnName(i));
            model.setNullSign(md.isNullable(i) == 1 ? "NULL" : "NOT NULL");
            model.setLabel(isLowercase ? md.getColumnLabel(i).toLowerCase() : md.getColumnLabel(i));
            model.setDataType(md.getColumnTypeName(i));
            model.setValue(isValue ? rs.getObject(i) : null);
            dbColumnModelList.add(model);
        }

        return dbColumnModelList;
    }

    public void getOracle() {
    }

    public static ResultSet getColumnMetaDateRs(Connection conn, String table) throws Exception {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        switch (DbTypeUtil.getDb(conn).getJnpfDbEncode()) {
            case "SQLServer":
                return dbMetaData.getColumns(conn.getCatalog(), "dbo", table, (String)null);
            case "PostgreSQL":
                return dbMetaData.getColumns(conn.getCatalog(), "public", table, (String)null);
            case "KingbaseES":
                return dbMetaData.getColumns(conn.getCatalog(), conn.getSchema(), table, (String)null);
            case "MySQL":
            default:
                return dbMetaData.getColumns(conn.getCatalog(), dbMetaData.getUserName(), table, (String)null);
        }
    }
}
