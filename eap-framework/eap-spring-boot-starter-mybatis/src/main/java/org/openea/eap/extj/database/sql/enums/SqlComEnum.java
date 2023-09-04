package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;

import java.util.*;

public enum SqlComEnum implements SqlFrameBase {
    TABLES(new SqlFrameBase[]{SqlMySQLEnum.TABLES, SqlOracleEnum.TABLES, SqlSQLServerEnum.TABLES, SqlDMEnum.TABLES, SqlKingbaseESEnum.TABLES, SqlPostgreSQLEnum.TABLES}),
    TABLESANDVIEW(new SqlFrameBase[]{SqlMySQLEnum.TABLESANDVIEW, SqlOracleEnum.TABLESANDVIEW, SqlSQLServerEnum.TABLESANDVIEW, SqlDMEnum.TABLESANDVIEW, SqlKingbaseESEnum.TABLESANDVIEW, SqlPostgreSQLEnum.TABLESANDVIEW}),
    TABLE(new SqlFrameBase[]{SqlMySQLEnum.TABLE, SqlOracleEnum.TABLE, SqlSQLServerEnum.TABLE, SqlDMEnum.TABLE, SqlKingbaseESEnum.TABLE, SqlPostgreSQLEnum.TABLE}),
    FIELDS(new SqlFrameBase[]{SqlMySQLEnum.FIELDS, SqlOracleEnum.FIELDS, SqlSQLServerEnum.FIELDS, SqlDMEnum.FIELDS, SqlKingbaseESEnum.FIELDS, SqlPostgreSQLEnum.FIELDS}),
    EXISTS_TABLE(new SqlFrameBase[]{SqlMySQLEnum.EXISTS_TABLE, SqlOracleEnum.EXISTS_TABLE, SqlSQLServerEnum.EXISTS_TABLE, SqlDMEnum.EXISTS_TABLE, SqlKingbaseESEnum.EXISTS_TABLE, SqlPostgreSQLEnum.EXISTS_TABLE}),
    CREATE_TABLE(SqlOracleEnum.CREATE_TABLE, Arrays.asList("{table}", "{column}", "{dataType}", "[<DEFAULT> {defaultValue}]", "[[NOT] [NULL]]", "[<PRIMARY KEY>]", "[AUTO_INCREMENT]", "[<COMMENT> {comment}]"), new SqlFrameBase[]{SqlMySQLEnum.CREATE_TABLE, SqlSQLServerEnum.CREATE_TABLE, SqlDMEnum.CREATE_TABLE, SqlKingbaseESEnum.CREATE_TABLE, SqlPostgreSQLEnum.CREATE_TABLE}),
    ADD_COLUMN(SqlMySQLEnum.ALTER_ADD_MODIFY, Arrays.asList("<ADD|MODIFY>", "{table}", "{column}", "{dataType}", "[[NOT] [NULL]]", "[<DEFAULT> {defaultValue}]", "{comment}"), new SqlFrameBase[]{SqlOracleEnum.ADD_COLUMN, SqlPostgreSQLEnum.ADD_COLUMN, SqlDMEnum.ALTER_ADD, SqlKingbaseESEnum.ALTER_ADD, SqlSQLServerEnum.ALTER_COLUMN}),
    DROP_TABLE(SqlOracleEnum.DROP_TABLE, Arrays.asList("{table}"), new SqlFrameBase[]{SqlMySQLEnum.DROP_TABLE}),
    RE_TABLE_NAME(SqlMySQLEnum.RE_TABLE_NAME, Arrays.asList("{oldTable}", "{newTable}"), new SqlFrameBase[]{SqlKingbaseESEnum.RE_TABLE_NAME, SqlPostgreSQLEnum.RE_TABLE_NAME, SqlSQLServerEnum.RE_TABLE_NAME}),
    COMMENT_TABLE(SqlOracleEnum.COMMENT_TABLE, Arrays.asList("{table}", "'{comment}'"), new SqlFrameBase[]{SqlMySQLEnum.COMMENT_TABLE, SqlSQLServerEnum.COMMENT_TABLE}),
    COMMENT_COLUMN(SqlOracleEnum.COMMENT_COLUMN, Arrays.asList("{table}", "{column}", "'{comment}'", "{dataType}", "[DEFAULT {defaultValue}]"), new SqlFrameBase[]{SqlMySQLEnum.COMMENT_COLUMN, SqlSQLServerEnum.COMMENT_COLUMN}),
    SELECT_TABLE(SqlMySQLEnum.SELECT_TABLE, Arrays.asList("{table}")),
    COUNT_SIZE(SqlMySQLEnum.COUNT_SIZE, Arrays.asList("{totalAlias}", "{selectSql}")),
    COUNT_SIZE_TABLE(SqlMySQLEnum.COUNT_TABLE_SIZE, Arrays.asList("{totalAlias}", "{table}")),
    INSERT(SqlMySQLEnum.INSERT, Arrays.asList("{table}", "[【{column},】]", "【{value},】")),
    DELETE_ALL(SqlMySQLEnum.DELETE_ALL, Collections.singletonList("{table}")),
    ORDER_PAGE(SqlMySQLEnum.ORDER_PAGE, Arrays.asList("{selectSql}", "{orderColumn}", "{beginIndex}", "{endIndex}", "{pageSize}", "[DESC]"), new SqlFrameBase[]{SqlOracleEnum.ORDER_PAGE, SqlSQLServerEnum.ORDER_PAGE, SqlPostgreSQLEnum.ORDER_PAGE}),
    ORDER(SqlMySQLEnum.ORDER, Arrays.asList("{column}", "[DESC]")),
    LIKE(SqlMySQLEnum.LIKE, Arrays.asList("{selectSql}", "{column}", "{condition}"));

    private String sqlFrame;
    private SqlFrameBase baseSqlEnum;
    private List<SqlFrameBase> frameEnums;
    private List<String> frameParamList;
    private final String dbEncode = "common";

    private SqlComEnum(SqlFrameBase baseSqlEnum, List frameParamList) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        this.frameEnums = new ArrayList();
        this.frameEnums.add(baseSqlEnum);
    }

    private SqlComEnum(SqlFrameBase baseSqlEnum, List frameParamList, SqlFrameBase... frameEnums) {
        this.baseSqlEnum = baseSqlEnum;
        this.sqlFrame = baseSqlEnum.getSqlFrame();
        this.frameParamList = frameParamList;
        List<SqlFrameBase> frameEnumsList = new ArrayList(Arrays.asList(frameEnums));
        frameEnumsList.add(baseSqlEnum);
        this.frameEnums = frameEnumsList;
    }

    private SqlComEnum(SqlFrameBase... frameEnums) {
        this.frameEnums = Arrays.asList(frameEnums);
    }

    public SqlFrameBase getSqlFrameEnum(String dbEncode) {
        if (this.getFrameEnums() != null) {
            Iterator it = this.getFrameEnums().iterator();
            while(it.hasNext()) {
                SqlFrameBase sqlEnum = (SqlFrameBase)it.next();
                if (sqlEnum.getDbEncode().equalsIgnoreCase(dbEncode)) {
                    return sqlEnum;
                }
            }
        }
        return null;
    }

    public SqlFrameBase getSqlFrameEnum(SqlFrameBase sqlFrameBase) {
        if (this.getFrameEnums() != null) {
            Iterator it = this.getFrameEnums().iterator();

            while(it.hasNext()) {
                SqlFrameBase sqlEnum = (SqlFrameBase)it.next();
                if (sqlEnum.equals(sqlFrameBase)) {
                    return sqlEnum;
                }
            }
        }

        return null;
    }

    public PrepSqlDTO getPrepSqlDto(DbSourceOrDbLink dataSourceMod, String table) {
        SqlFrameBase sysTemSqlEnum = this.getSqlFrameEnum(dataSourceMod.init().getDbType());
        return sysTemSqlEnum.getPrepSqlDto(dataSourceMod, table);
    }

    public String getSqlFrame() {
        return this.sqlFrame;
    }

    public SqlFrameBase getBaseSqlEnum() {
        return this.baseSqlEnum;
    }

    public List<SqlFrameBase> getFrameEnums() {
        return this.frameEnums;
    }

    public List<String> getFrameParamList() {
        return this.frameParamList;
    }

    public String getDbEncode() {
        this.getClass();
        return "common";
    }

    private SqlComEnum(String sqlFrame, SqlFrameBase baseSqlEnum, List frameEnums, List frameParamList) {
        this.sqlFrame = sqlFrame;
        this.baseSqlEnum = baseSqlEnum;
        this.frameEnums = frameEnums;
        this.frameParamList = frameParamList;
    }
}
