package org.openea.eap.extj.database.datatype.sync.enums;

import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DtConvertMultiEnum {
    INTEGER(new DtMySQLEnum[]{DtMySQLEnum.TINY_INT, DtMySQLEnum.SMALL_INT, DtMySQLEnum.INT, DtMySQLEnum.BIGINT, DtMySQLEnum.MEDIUM_INT}, new DtOracleEnum[]{DtOracleEnum.NUMBER}, new DtSQLServerEnum[]{DtSQLServerEnum.TINY_INT, DtSQLServerEnum.SMALL_INT, DtSQLServerEnum.INT, DtSQLServerEnum.BIGINT}, new DtDMEnum[]{DtDMEnum.TINY_INT, DtDMEnum.SMALL_INT, DtDMEnum.INT, DtDMEnum.BIGINT}, new DtKingbaseESEnum[]{DtKingbaseESEnum.TINY_INT, DtKingbaseESEnum.SMALL_INT, DtKingbaseESEnum.INTEGER, DtKingbaseESEnum.BIGINT}, new DtPostgreSQLEnum[]{DtPostgreSQLEnum.INT2, DtPostgreSQLEnum.INT4, DtPostgreSQLEnum.INT8}, new DtDorisEnum[]{DtDorisEnum.TINY_INT, DtDorisEnum.SMALL_INT, DtDorisEnum.INT, DtDorisEnum.BIGINT}),
    STRING(new DtMySQLEnum[]{DtMySQLEnum.CHAR, DtMySQLEnum.VARCHAR, DtMySQLEnum.TINY_TEXT, DtMySQLEnum.TEXT, DtMySQLEnum.MEDIUM_TEXT, DtMySQLEnum.LONG_TEXT}, new DtOracleEnum[]{DtOracleEnum.CHAR, DtOracleEnum.NCHAR, DtOracleEnum.VARCHAR2, DtOracleEnum.NVARCHAR2, DtOracleEnum.CLOB, DtOracleEnum.NCLOB}, new DtSQLServerEnum[]{DtSQLServerEnum.CHAR, DtSQLServerEnum.VARCHAR, DtSQLServerEnum.NVARCHAR, DtSQLServerEnum.VARCHAR_MAX, DtSQLServerEnum.TEXT, DtSQLServerEnum.N_TEXT, DtSQLServerEnum.IMAGE}, new DtDMEnum[]{DtDMEnum.CHAR, DtDMEnum.VARCHAR, DtDMEnum.VARCHAR2, DtDMEnum.TEXT, DtDMEnum.CLOB}, new DtKingbaseESEnum[]{DtKingbaseESEnum.CHAR, DtKingbaseESEnum.VARCHAR, DtKingbaseESEnum.TEXT}, new DtPostgreSQLEnum[]{DtPostgreSQLEnum.CHAR, DtPostgreSQLEnum.VARCHAR, DtPostgreSQLEnum.TEXT}, new DtDorisEnum[]{DtDorisEnum.CHAR, DtDorisEnum.VARCHAR}),
    FLOAT(new DtMySQLEnum[]{DtMySQLEnum.FLOAT, DtMySQLEnum.DOUBLE}, new DtOracleEnum[]{DtOracleEnum.FLOAT}, new DtSQLServerEnum[]{DtSQLServerEnum.FLOAT}, new DtDMEnum[]{DtDMEnum.FLOAT, DtDMEnum.DOUBLE}, new DtKingbaseESEnum[]{DtKingbaseESEnum.FLOAT4, DtKingbaseESEnum.FLOAT8}, new DtPostgreSQLEnum[]{DtPostgreSQLEnum.FLOAT4, DtPostgreSQLEnum.FLOAT8}, new DtDorisEnum[]{DtDorisEnum.FLOAT, DtDorisEnum.DOUBLE}),
    DECIMAL(new DtMySQLEnum[]{DtMySQLEnum.DECIMAL}, new DtOracleEnum[]{DtOracleEnum.NUMBER}, new DtSQLServerEnum[]{DtSQLServerEnum.DECIMAL}, new DtDMEnum[]{DtDMEnum.DECIMAL, DtDMEnum.DEC}, new DtKingbaseESEnum[]{DtKingbaseESEnum.NUMERIC}, new DtPostgreSQLEnum[]{DtPostgreSQLEnum.NUMERIC}, new DtDorisEnum[]{DtDorisEnum.DECIMAL}),
    DATE_TIME(new DtMySQLEnum[]{DtMySQLEnum.YEAR, DtMySQLEnum.TIME, DtMySQLEnum.DATE, DtMySQLEnum.DATE_TIME, DtMySQLEnum.TIMESTAMP}, new DtOracleEnum[]{DtOracleEnum.DATE, DtOracleEnum.TIMESTAMP}, new DtSQLServerEnum[]{DtSQLServerEnum.DATE, DtSQLServerEnum.TIME, DtSQLServerEnum.DATE_TIME, DtSQLServerEnum.TIMESTAMP}, new DtDMEnum[]{DtDMEnum.DATE, DtDMEnum.DATE_TIME, DtDMEnum.TIME_STAMP, DtDMEnum.TIME}, new DtKingbaseESEnum[]{DtKingbaseESEnum.TIME, DtKingbaseESEnum.DATE, DtKingbaseESEnum.DATE_TIME, DtKingbaseESEnum.TIMESTAMP}, new DtPostgreSQLEnum[]{DtPostgreSQLEnum.TIME, DtPostgreSQLEnum.DATE, DtPostgreSQLEnum.TIMESTAMP}, new DtDorisEnum[]{DtDorisEnum.DATE, DtDorisEnum.DATE_TIME});

    private final DtMySQLEnum[] dtMySQLEnums;
    private final DtOracleEnum[] dtOracleEnums;
    private final DtSQLServerEnum[] dtSQLServerEnums;
    private final DtDMEnum[] dtDMEnums;
    private final DtKingbaseESEnum[] dtKingbaseESEnums;
    private final DtPostgreSQLEnum[] dtPostgreSQLEnums;
    private final DtDorisEnum[] dtDorisEnums;

    public static DtInterface[] getConverts(String dbType, DtConvertMultiEnum cEnum) throws Exception {
        Method method = DtConvertMultiEnum.class.getMethod("getDt" + dbType + "Enums");
        return (DtInterface[])((DtInterface[])method.invoke(cEnum));
    }

    public List<DtInterface> getAllConverts() {
        List<DtInterface> list = new ArrayList();
        list.addAll(Arrays.asList(this.getDtMySQLEnums()));
        list.addAll(Arrays.asList(this.getDtOracleEnums()));
        list.addAll(Arrays.asList(this.getDtSQLServerEnums()));
        list.addAll(Arrays.asList(this.getDtDMEnums()));
        list.addAll(Arrays.asList(this.getDtKingbaseESEnums()));
        list.addAll(Arrays.asList(this.getDtPostgreSQLEnums()));
        return list;
    }

    public DtMySQLEnum[] getDtMySQLEnums() {
        return this.dtMySQLEnums;
    }

    public DtOracleEnum[] getDtOracleEnums() {
        return this.dtOracleEnums;
    }

    public DtSQLServerEnum[] getDtSQLServerEnums() {
        return this.dtSQLServerEnums;
    }

    public DtDMEnum[] getDtDMEnums() {
        return this.dtDMEnums;
    }

    public DtKingbaseESEnum[] getDtKingbaseESEnums() {
        return this.dtKingbaseESEnums;
    }

    public DtPostgreSQLEnum[] getDtPostgreSQLEnums() {
        return this.dtPostgreSQLEnums;
    }

    public DtDorisEnum[] getDtDorisEnums() {
        return this.dtDorisEnums;
    }

    private DtConvertMultiEnum(DtMySQLEnum[] dtMySQLEnums, DtOracleEnum[] dtOracleEnums, DtSQLServerEnum[] dtSQLServerEnums, DtDMEnum[] dtDMEnums, DtKingbaseESEnum[] dtKingbaseESEnums, DtPostgreSQLEnum[] dtPostgreSQLEnums, DtDorisEnum[] dtDorisEnums) {
        this.dtMySQLEnums = dtMySQLEnums;
        this.dtOracleEnums = dtOracleEnums;
        this.dtSQLServerEnums = dtSQLServerEnums;
        this.dtDMEnums = dtDMEnums;
        this.dtKingbaseESEnums = dtKingbaseESEnums;
        this.dtPostgreSQLEnums = dtPostgreSQLEnums;
        this.dtDorisEnums = dtDorisEnums;
    }
}