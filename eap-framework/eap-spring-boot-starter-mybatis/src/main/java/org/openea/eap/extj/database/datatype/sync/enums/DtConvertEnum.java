package org.openea.eap.extj.database.datatype.sync.enums;

import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.model.DtConvertModel;
import org.openea.eap.extj.exception.DataException;

import  org.openea.eap.extj.database.datatype.db.*;

public enum DtConvertEnum {
    MYSQL("MySQL") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtMySQLEnum)dtEnum) {
                case TINY_INT:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NUMBER, DtSQLServerEnum.TINY_INT, DtDMEnum.TINY_INT, DtKingbaseESEnum.TINY_INT, DtPostgreSQLEnum.INT2, DtDorisEnum.TINY_INT});
                    break;
                case SMALL_INT:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NUMBER, DtSQLServerEnum.SMALL_INT, DtDMEnum.SMALL_INT, DtKingbaseESEnum.SMALL_INT, DtPostgreSQLEnum.INT2, DtDorisEnum.SMALL_INT});
                    break;
                case MEDIUM_INT:
                case INT:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NUMBER, DtSQLServerEnum.INT, DtDMEnum.INT, DtKingbaseESEnum.INTEGER, DtPostgreSQLEnum.INT4, DtDorisEnum.INT});
                    break;
                case BIGINT:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NUMBER, DtSQLServerEnum.BIGINT, DtDMEnum.BIGINT, DtKingbaseESEnum.BIGINT, DtPostgreSQLEnum.INT8, DtDorisEnum.BIGINT});
                    break;
                case CHAR:
                    this.convert(model, new DtInterface[]{DtOracleEnum.CHAR, DtSQLServerEnum.CHAR, DtDMEnum.CHAR, DtKingbaseESEnum.CHAR, DtPostgreSQLEnum.CHAR, DtDorisEnum.CHAR});
                    break;
                case BIT:
                case BLOB:
                case VARCHAR:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NVARCHAR2, DtSQLServerEnum.NVARCHAR, DtDMEnum.VARCHAR, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR, DtDorisEnum.VARCHAR});
                    break;
                case TINY_TEXT:
                case TEXT:
                case MEDIUM_TEXT:
                case LONG_TEXT:
                case LONG_BLOB:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NCLOB, DtSQLServerEnum.N_TEXT, DtDMEnum.CLOB, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT, DtDorisEnum.STRING});
                    break;
                case FLOAT:
                    this.convert(model, new DtInterface[]{DtDMEnum.FLOAT, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtKingbaseESEnum.FLOAT4, DtPostgreSQLEnum.FLOAT4, DtDorisEnum.FLOAT});
                    break;
                case DOUBLE:
                    this.convert(model, new DtInterface[]{DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtDMEnum.DOUBLE, DtKingbaseESEnum.FLOAT8, DtPostgreSQLEnum.FLOAT8, DtDorisEnum.DOUBLE});
                    break;
                case DECIMAL:
                    this.convert(model, new DtInterface[]{DtOracleEnum.NUMBER, DtSQLServerEnum.DECIMAL, DtDMEnum.DECIMAL, DtKingbaseESEnum.NUMERIC, DtPostgreSQLEnum.NUMERIC, DtDorisEnum.DECIMAL});
                    break;
                case YEAR:
                case DATE:
                    this.convert(model, new DtInterface[]{DtOracleEnum.TIMESTAMP, DtSQLServerEnum.DATE_TIME, DtDMEnum.DATE_TIME, DtKingbaseESEnum.DATE_TIME, DtPostgreSQLEnum.TIMESTAMP, DtDorisEnum.DATE});
                    break;
                case TIME:
                    this.convert(model, new DtInterface[]{DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIME, DtDMEnum.TIME, DtKingbaseESEnum.TIME, DtPostgreSQLEnum.TIME, DtDorisEnum.DATE_TIME});
                    break;
                case DATE_TIME:
                    this.convert(model, new DtInterface[]{DtOracleEnum.TIMESTAMP, DtSQLServerEnum.DATE_TIME, DtDMEnum.DATE_TIME, DtKingbaseESEnum.DATE_TIME, DtPostgreSQLEnum.TIMESTAMP, DtDorisEnum.DATE_TIME});
                    break;
                case TIMESTAMP:
                    this.convert(model, new DtInterface[]{DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIMESTAMP, DtDMEnum.TIME_STAMP, DtKingbaseESEnum.TIMESTAMP, DtPostgreSQLEnum.TIMESTAMP, DtDorisEnum.DATE_TIME});
            }

            model.setDtMySQLEnum((DtMySQLEnum)dtEnum);
            return model;
        }
    },
    ORACLE("Oracle") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtOracleEnum)dtEnum) {
                case NUMBER:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.BIGINT, DtSQLServerEnum.BIGINT, DtDMEnum.BIGINT, DtKingbaseESEnum.BIGINT, DtPostgreSQLEnum.INT8});
                    break;
                case CHAR:
                case NCHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.CHAR, DtSQLServerEnum.CHAR, DtDMEnum.CHAR, DtKingbaseESEnum.CHAR, DtPostgreSQLEnum.CHAR});
                    break;
                case VARCHAR2:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtSQLServerEnum.NVARCHAR, DtDMEnum.VARCHAR, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case NVARCHAR2:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtSQLServerEnum.NVARCHAR, DtDMEnum.VARCHAR2, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case CLOB:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TEXT, DtSQLServerEnum.TEXT, DtDMEnum.TEXT, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case NCLOB:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.LONG_TEXT, DtSQLServerEnum.N_TEXT, DtDMEnum.CLOB, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case FLOAT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.FLOAT, DtDMEnum.FLOAT, DtSQLServerEnum.FLOAT, DtKingbaseESEnum.FLOAT4, DtPostgreSQLEnum.FLOAT4});
                    break;
                case DATE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE, DtSQLServerEnum.DATE, DtDMEnum.DATE, DtKingbaseESEnum.DATE, DtPostgreSQLEnum.DATE});
                    break;
                case TIMESTAMP:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIMESTAMP, DtSQLServerEnum.TIMESTAMP, DtDMEnum.TIME_STAMP, DtKingbaseESEnum.TIMESTAMP, DtPostgreSQLEnum.TIMESTAMP});
            }

            model.setDtOracleEnum((DtOracleEnum)dtEnum);
            return model;
        }
    },
    SQL_SERVER("SQLServer") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtSQLServerEnum)dtEnum) {
                case TINY_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TINY_INT, DtOracleEnum.NUMBER, DtDMEnum.TINY_INT, DtKingbaseESEnum.TINY_INT, DtPostgreSQLEnum.INT2});
                    break;
                case SMALL_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.SMALL_INT, DtOracleEnum.NUMBER, DtDMEnum.SMALL_INT, DtKingbaseESEnum.SMALL_INT, DtPostgreSQLEnum.INT2});
                    break;
                case INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.INT, DtOracleEnum.NUMBER, DtDMEnum.INT, DtKingbaseESEnum.INTEGER, DtPostgreSQLEnum.INT4});
                    break;
                case BIGINT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.BIGINT, DtOracleEnum.NUMBER, DtDMEnum.BIGINT, DtKingbaseESEnum.BIGINT, DtPostgreSQLEnum.INT8});
                    break;
                case CHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.CHAR, DtOracleEnum.CHAR, DtDMEnum.CHAR, DtKingbaseESEnum.CHAR, DtPostgreSQLEnum.CHAR});
                    break;
                case NVARCHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtOracleEnum.NVARCHAR2, DtDMEnum.VARCHAR2, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case VARCHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtOracleEnum.NVARCHAR2, DtDMEnum.VARCHAR, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case VARCHAR_MAX:
                case IMAGE:
                case TEXT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TEXT, DtOracleEnum.CLOB, DtDMEnum.TEXT, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case N_TEXT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.LONG_TEXT, DtOracleEnum.NCLOB, DtDMEnum.CLOB, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case DATE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE, DtOracleEnum.DATE, DtDMEnum.DATE, DtKingbaseESEnum.DATE, DtPostgreSQLEnum.DATE});
                    break;
                case TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIME, DtOracleEnum.TIMESTAMP, DtDMEnum.TIME, DtKingbaseESEnum.TIME, DtPostgreSQLEnum.TIME});
                    break;
                case DATE_TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE_TIME, DtOracleEnum.TIMESTAMP, DtDMEnum.DATE_TIME, DtKingbaseESEnum.DATE_TIME, DtPostgreSQLEnum.TIMESTAMP});
                    break;
                case TIMESTAMP:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIMESTAMP, DtOracleEnum.TIMESTAMP, DtDMEnum.TIME_STAMP, DtKingbaseESEnum.TIMESTAMP, DtPostgreSQLEnum.TIMESTAMP});
                    break;
                case FLOAT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.FLOAT, DtDMEnum.FLOAT, DtOracleEnum.FLOAT, DtKingbaseESEnum.FLOAT4, DtPostgreSQLEnum.FLOAT4});
                    break;
                case DECIMAL:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DECIMAL, DtOracleEnum.NUMBER, DtDMEnum.DECIMAL, DtKingbaseESEnum.NUMERIC, DtPostgreSQLEnum.NUMERIC});
            }

            model.setDtSQLServerEnum((DtSQLServerEnum)dtEnum);
            return model;
        }
    },
    DM("DM") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtDMEnum)dtEnum) {
                case TINY_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TINY_INT, DtOracleEnum.NUMBER, DtSQLServerEnum.TINY_INT, DtKingbaseESEnum.TINY_INT, DtPostgreSQLEnum.INT2});
                    break;
                case SMALL_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.SMALL_INT, DtOracleEnum.NUMBER, DtSQLServerEnum.SMALL_INT, DtKingbaseESEnum.SMALL_INT, DtPostgreSQLEnum.INT2});
                    break;
                case INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.INT, DtOracleEnum.NUMBER, DtSQLServerEnum.INT, DtKingbaseESEnum.INTEGER, DtPostgreSQLEnum.INT4});
                    break;
                case BIGINT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.BIGINT, DtOracleEnum.NUMBER, DtSQLServerEnum.BIGINT, DtKingbaseESEnum.BIGINT, DtPostgreSQLEnum.INT8});
                    break;
                case CHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.CHAR, DtOracleEnum.CHAR, DtSQLServerEnum.CHAR, DtKingbaseESEnum.CHAR, DtPostgreSQLEnum.CHAR});
                    break;
                case VARCHAR:
                case VARCHAR2:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtOracleEnum.NVARCHAR2, DtSQLServerEnum.NVARCHAR, DtKingbaseESEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case TEXT:
                case CLOB:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TEXT, DtOracleEnum.CLOB, DtSQLServerEnum.TEXT, DtKingbaseESEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case DATE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE, DtSQLServerEnum.DATE, DtOracleEnum.DATE, DtKingbaseESEnum.DATE, DtPostgreSQLEnum.DATE});
                    break;
                case DATE_TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE_TIME, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.DATE_TIME, DtKingbaseESEnum.DATE_TIME, DtPostgreSQLEnum.TIMESTAMP});
                    break;
                case TIME_STAMP:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIMESTAMP, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIMESTAMP, DtKingbaseESEnum.TIMESTAMP, DtPostgreSQLEnum.TIMESTAMP});
                    break;
                case TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIME, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIME, DtKingbaseESEnum.TIME, DtPostgreSQLEnum.TIME});
                    break;
                case FLOAT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.FLOAT, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtKingbaseESEnum.FLOAT4, DtPostgreSQLEnum.FLOAT4});
                    break;
                case DOUBLE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DOUBLE, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtKingbaseESEnum.FLOAT8, DtPostgreSQLEnum.FLOAT8});
                    break;
                case DECIMAL:
                case DEC:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DECIMAL, DtOracleEnum.NUMBER, DtSQLServerEnum.DECIMAL, DtKingbaseESEnum.NUMERIC, DtPostgreSQLEnum.NUMERIC});
            }

            model.setDtDMEnum((DtDMEnum)dtEnum);
            return model;
        }
    },
    KINGBASE("KingbaseES") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtKingbaseESEnum)dtEnum) {
                case TINY_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TINY_INT, DtOracleEnum.NUMBER, DtSQLServerEnum.TINY_INT, DtDMEnum.TINY_INT, DtPostgreSQLEnum.INT2});
                    break;
                case SMALL_INT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.SMALL_INT, DtOracleEnum.NUMBER, DtSQLServerEnum.SMALL_INT, DtDMEnum.SMALL_INT, DtPostgreSQLEnum.INT2});
                    break;
                case INTEGER:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.INT, DtOracleEnum.NUMBER, DtSQLServerEnum.INT, DtDMEnum.INT, DtPostgreSQLEnum.INT4});
                    break;
                case BIGINT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.BIGINT, DtOracleEnum.NUMBER, DtSQLServerEnum.BIGINT, DtDMEnum.BIGINT, DtPostgreSQLEnum.INT8});
                    break;
                case CHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.CHAR, DtOracleEnum.CHAR, DtSQLServerEnum.CHAR, DtDMEnum.CHAR, DtPostgreSQLEnum.CHAR});
                    break;
                case VARCHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtOracleEnum.NVARCHAR2, DtSQLServerEnum.NVARCHAR, DtDMEnum.VARCHAR, DtPostgreSQLEnum.VARCHAR});
                    break;
                case TEXT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TEXT, DtOracleEnum.CLOB, DtSQLServerEnum.TEXT, DtDMEnum.TEXT, DtPostgreSQLEnum.TEXT});
                    break;
                case FLOAT4:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.FLOAT, DtDMEnum.FLOAT, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtPostgreSQLEnum.FLOAT4});
                    break;
                case FLOAT8:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DOUBLE, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtDMEnum.DOUBLE, DtPostgreSQLEnum.FLOAT8});
                    break;
                case NUMERIC:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DECIMAL, DtOracleEnum.NUMBER, DtSQLServerEnum.DECIMAL, DtDMEnum.DECIMAL, DtPostgreSQLEnum.NUMERIC});
                    break;
                case TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIME, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIME, DtDMEnum.TIME, DtPostgreSQLEnum.TIME});
                    break;
                case DATE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE, DtSQLServerEnum.DATE, DtOracleEnum.DATE, DtDMEnum.DATE, DtPostgreSQLEnum.DATE});
                    break;
                case DATE_TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE_TIME, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.DATE_TIME, DtDMEnum.DATE_TIME, DtPostgreSQLEnum.TIMESTAMP});
                    break;
                case TIMESTAMP:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIMESTAMP, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIMESTAMP, DtDMEnum.TIME_STAMP, DtPostgreSQLEnum.TIMESTAMP});
            }

            model.setDtKingbaseESEnum((DtKingbaseESEnum)dtEnum);
            return model;
        }
    },
    POSTGRE("PostgreSQL") {
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
            DtConvertModel<T> model = new DtConvertModel();
            switch ((DtPostgreSQLEnum)dtEnum) {
                case INT2:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.SMALL_INT, DtOracleEnum.NUMBER, DtSQLServerEnum.SMALL_INT, DtDMEnum.SMALL_INT, DtKingbaseESEnum.SMALL_INT});
                    break;
                case INT4:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.INT, DtOracleEnum.NUMBER, DtSQLServerEnum.INT, DtDMEnum.INT, DtKingbaseESEnum.INTEGER});
                    break;
                case INT8:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.BIGINT, DtOracleEnum.NUMBER, DtSQLServerEnum.BIGINT, DtDMEnum.BIGINT, DtKingbaseESEnum.BIGINT});
                    break;
                case CHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.CHAR, DtOracleEnum.CHAR, DtSQLServerEnum.CHAR, DtDMEnum.CHAR, DtKingbaseESEnum.CHAR});
                    break;
                case VARCHAR:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.VARCHAR, DtOracleEnum.NVARCHAR2, DtSQLServerEnum.NVARCHAR, DtDMEnum.VARCHAR, DtKingbaseESEnum.VARCHAR});
                    break;
                case TEXT:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TEXT, DtOracleEnum.CLOB, DtSQLServerEnum.TEXT, DtDMEnum.TEXT, DtKingbaseESEnum.TEXT});
                    break;
                case FLOAT4:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.FLOAT, DtDMEnum.FLOAT, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtKingbaseESEnum.FLOAT4});
                    break;
                case FLOAT8:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DOUBLE, DtOracleEnum.FLOAT, DtSQLServerEnum.FLOAT, DtDMEnum.DOUBLE, DtKingbaseESEnum.FLOAT8});
                    break;
                case NUMERIC:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DECIMAL, DtOracleEnum.NUMBER, DtSQLServerEnum.DECIMAL, DtDMEnum.DECIMAL, DtKingbaseESEnum.NUMERIC});
                    break;
                case TIME:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIME, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIME, DtDMEnum.TIME, DtKingbaseESEnum.TIME});
                    break;
                case DATE:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.DATE, DtSQLServerEnum.DATE, DtOracleEnum.DATE, DtDMEnum.DATE, DtKingbaseESEnum.DATE});
                    break;
                case TIMESTAMP:
                    this.convert(model, new DtInterface[]{DtMySQLEnum.TIMESTAMP, DtOracleEnum.TIMESTAMP, DtSQLServerEnum.TIMESTAMP, DtDMEnum.TIME_STAMP, DtKingbaseESEnum.TIMESTAMP});
            }

            model.setDtPostgreSQLEnum((DtPostgreSQLEnum)dtEnum);
            return model;
        }
    };

    private final String dbType;

    public static <T extends DtInterface> DtConvertModel<T> getConvertModel(T fromDtEnum) throws DataException {
        return choose(fromDtEnum.getDbType()).getModel(fromDtEnum);
    }

    protected <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum) {
        return null;
    }

    private static DtConvertEnum choose(String dbType) throws DataException {
        DtConvertEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DtConvertEnum dbMethod = var1[var3];
            if (dbMethod.getDbType().equals(dbType)) {
                return dbMethod;
            }
        }

        throw new DataException(MsgCode.DB005.get());
    }

    protected <T extends DtInterface> void convert(DtConvertModel<T> model, DtInterface... convertDtEnums) {
        try {
            DtInterface[] var3 = convertDtEnums;
            int var4 = convertDtEnums.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                DtInterface convertDtEnum = var3[var5];
                model.setDtEnum(convertDtEnum);
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public String getDbType() {
        return this.dbType;
    }

    private DtConvertEnum(String dbType) {
        this.dbType = dbType;
    }
}
