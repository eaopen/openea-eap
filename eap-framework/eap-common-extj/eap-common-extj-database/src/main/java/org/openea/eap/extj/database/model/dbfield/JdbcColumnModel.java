package org.openea.eap.extj.database.model.dbfield;

import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.constant.RsColumnKeyConst;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import org.openea.eap.extj.database.model.dbfield.base.DbFieldModelBase;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.DbTypeUtil;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * JDBC元数据表模型类型
 *
 * 
 */
@Data
@NoArgsConstructor
public class JdbcColumnModel extends DbFieldModelBase {

    /**
     * 字段别名
     */
    private String label;

    /**
     * 字段类型jdbc编码
     */
    private Integer dtJdbcEncode;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 默认值
     */
    protected String defaultValue;

    /**
     * 所属表名
     */
    private String table;

    /**
     * 字段长度
     */
    private String size;

    /**
     * 字符长度
     * (charLength)
     */
    private Long columnSize;
    /**
     * 标度
     * (precision)
     */
    private Integer numPrecRadix;
    /**
     * 精度
     * (scale)
     */
    private Integer decimalDigits;
    /**
     * 字段位置
     */
    private String ordinalPosition;

    /**
     * 自增
     */
    private String autoIncrement;

    /**
     * java对应数据类型
     */
    private String javaDataType;

    /* ================== 内部方法 ================= */

    /**
     * 字段结构
     */
    public DbFieldModel convertDbFieldModel(String dbEncode) throws Exception {
        DbFieldModel dbFieldModel = new DbFieldModel();
        dbFieldModel.setField(getField());
        dbFieldModel.setComment(getComment());
        dbFieldModel.setDataType(getDataType());
        dbFieldModel.setIsPrimaryKey(getIsPrimaryKey());
        dbFieldModel.setNullSign(getNullSign());
        DtInterface dtEnum = DtInterface.newInstanceByDt(getDataType(), dbEncode);
        assert dtEnum != null;
        dbFieldModel.setDtModelDTO(new DtModelDTO(dtEnum, this.columnSize, Integer.parseInt(this.columnSize.toString()), this.decimalDigits));
        return dbFieldModel;
    }

    /* ================== 静态方法 ================= */

    /**
     * 获取字段元数据对象集合(所有字段)
     * 注意：只能获取结构，无法获取值
     * @param conn 数据连接
     * @param table 表名
     * @return ignore
     * @throws SQLException ignore
     */
    public static List<JdbcColumnModel> getList(Connection conn, String table, String primaryField) throws Exception {
        @Cleanup ResultSet rs = getColumnMetaDateRs(conn, table);
        List<JdbcColumnModel> list = new ArrayList<>();
        while (rs.next()) {
            JdbcColumnModel column = new JdbcColumnModel();
            // 表名
            column.setTable(rs.getString(RsColumnKeyConst.TABLE_NAME));
            // 列名称：
            column.setField(rs.getString(RsColumnKeyConst.COLUMN_NAME));
            // java.sql.Types
            column.setJavaDataType(rs.getString(RsColumnKeyConst.DATA_TYPE));
            // 字段类型：
            column.setDataType(rs.getString(RsColumnKeyConst.TYPE_NAME));
            // 列的大小(标度)：当字符类型表示CharLength，数字类型表示Precision
            column.setColumnSize(rs.getLong(RsColumnKeyConst.COLUMN_SIZE));
            // 小数部分的位数(精度)：
            column.setDecimalDigits(rs.getInt(RsColumnKeyConst.DECIMAL_DIGITS));
            // 描述列的注释：
            column.setComment(rs.getString(RsColumnKeyConst.REMARKS));
            // 该列的默认值：
            column.setDefaultValue(rs.getString(RsColumnKeyConst.COLUMN_DEF));
            // 列中的最大字节数：
            column.setLength(rs.getString(RsColumnKeyConst.CHAR_OCTET_LENGTH));
            // 列的索引：
            column.setOrdinalPosition(rs.getString(RsColumnKeyConst.ORDINAL_POSITION));
            // 是否允许使用 NULL：
            String isNull = rs.getString(RsColumnKeyConst.IS_NULLABLE);
            column.setNullSign(isNull.equalsIgnoreCase("YES") ? DbAliasConst.NULL : DbAliasConst.NOT_NULL);
//            // 指示此列是否自动增加：（部分数据库不支持）
//            column.setAutoIncrement(rs.getString(RsColumnKeyConst.IS_AUTOINCREMENT));
            // 是否是主键主键
            column.setIsPrimaryKey(column.getField().equals(primaryField));
            list.add(column);
        }
        return list;
    }

    /**
     * 获取字段值相对元数据结果集
     * @param rs 结果集
     * @param isLowercase 别名大小写
     * @param isValue 是否取值
     * @return 字段集合
     * @throws SQLException ignore
     */
    public static List<JdbcColumnModel> getList(ResultSet rs, Boolean isLowercase, Boolean isValue) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        List<JdbcColumnModel> dbColumnModelList = new ArrayList<>();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            JdbcColumnModel model = new JdbcColumnModel();
            model.setTable(md.getTableName(i));
            model.setField(md.getColumnName(i));
            // 非空判断
            model.setNullSign(md.isNullable(i) == 1 ? DbAliasConst.NULL : DbAliasConst.NOT_NULL);
            model.setLabel(isLowercase ? md.getColumnLabel(i).toLowerCase() : md.getColumnLabel(i));
            model.setDataType(md.getColumnTypeName(i));
            model.setValue(isValue ? rs.getObject(i) : null);
            dbColumnModelList.add(model);
        }
        return dbColumnModelList;
    }

    public void getOracle(){

    }


    /* ================================== 结果集 ================================== */

    /**
     * 从conn中获取数据库的表元数据
     * @param conn 数据连接
     * @return 返回表元数据
     * @throws SQLException ignore
     */
    public static ResultSet getColumnMetaDateRs(Connection conn, String table) throws Exception {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        /* % 代表 * ，
        ResultSet rs = dbMetaData.getColumns(conn.getCatalog(), "%", table, "%"); */
        switch (DbTypeUtil.getDb(conn).getJnpfDbEncode()){
            case DbBase.SQL_SERVER:
                return dbMetaData.getColumns(conn.getCatalog(), "dbo",table , null);
            case DbBase.POSTGRE_SQL:
                return dbMetaData.getColumns(conn.getCatalog(), "public",table , null);
            case DbBase.KINGBASE_ES:
                return dbMetaData.getColumns(conn.getCatalog(), conn.getSchema(),table , null);
            case DbBase.MYSQL:
            default:
                return dbMetaData.getColumns(conn.getCatalog(), dbMetaData.getUserName(), table, null);
        }


    }

}
