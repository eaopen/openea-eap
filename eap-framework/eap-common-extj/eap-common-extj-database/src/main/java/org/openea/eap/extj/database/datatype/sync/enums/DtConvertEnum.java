package org.openea.eap.extj.database.datatype.sync.enums;

import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.sync.model.DtConvertModel;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.exception.DataException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库相对应一些方法
 * 固定转换关系
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DtConvertEnum {

    /**
     * 根据数据选择的方法枚举
     */
    MYSQL(DbBase.MYSQL){
        /**
         * DtMutualConvertEnum(默认原始搭配)，在这个地方重复的枚举会重写覆盖原来的搭配
         */
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtMySQLEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case TINY_INT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.TINY_INT,
                        DtDMEnum.TINY_INT,
                        DtKingbaseESEnum.TINY_INT,
                        DtPostgreSQLEnum.INT2,
                        DtDorisEnum.TINY_INT
                ); break;
                case SMALL_INT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.SMALL_INT,
                        DtDMEnum.SMALL_INT,
                        DtKingbaseESEnum.SMALL_INT,
                        DtPostgreSQLEnum.INT2,
                        DtDorisEnum.SMALL_INT
                ); break;
                case MEDIUM_INT:
                case INT:
                case INT_UNSIGNED: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.INT,
                        DtDMEnum.INT,
                        DtKingbaseESEnum.INTEGER,
                        DtPostgreSQLEnum.INT4,
                        DtDorisEnum.INT
                ); break;
                case BIGINT:
                case BIGINT_UNSIGNED: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.BIGINT,
                        DtDMEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT,
                        DtPostgreSQLEnum.INT8,
                        DtDorisEnum.BIGINT
                ); break;
                // ====================== 文本字符串类型:  ==========================;==== */
                // mysql(varchar) 转成 oracle(NVARCHAR2)、sqlserver(nvarchar;
                case CHAR: convert(model,
                        DtOracleEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtKingbaseESEnum.CHAR,
                        DtPostgreSQLEnum.CHAR,
                        DtDorisEnum.CHAR
                ); break;
                // mysql(test) 转成 oracle(NCLOB)、sqlserver(nvarchar(max); break;
                case BIT:
                case BLOB:
                case VARCHAR: convert(model,
                        DtOracleEnum.NVARCHAR2,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR,
                        DtDorisEnum.VARCHAR
                ); break;
                case TINY_TEXT:
                case TEXT:
                case MEDIUM_TEXT:
                case LONG_TEXT:
                case LONG_BLOB: convert(model,
                        DtOracleEnum.NCLOB,
                        DtSQLServerEnum.N_TEXT,
                        DtDMEnum.CLOB,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT,
                        DtDorisEnum.STRING
                ); break;
                // ====================== 浮点类型:  =============================;= */
                case FLOAT: convert(model,
                        DtDMEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4,
                        DtPostgreSQLEnum.FLOAT4,
                        DtDorisEnum.FLOAT
                ); break;
                case DOUBLE: convert(model,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtDMEnum.DOUBLE,
                        DtKingbaseESEnum.FLOAT8,
                        DtPostgreSQLEnum.FLOAT8,
                        DtDorisEnum.DOUBLE
                ); break;
                // ====================== 定点数类型:  ============================;== */
                case DECIMAL: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.DECIMAL,
                        DtDMEnum.DECIMAL,
                        DtKingbaseESEnum.NUMERIC,
                        DtPostgreSQLEnum.NUMERIC,
                        DtDorisEnum.DECIMAL
                ); break;
                // ====================== 日期时间类型:  ===========================;=== */
                case YEAR:
                case DATE: convert(model,
                        DtOracleEnum.DATE,
                        DtSQLServerEnum.DATE_TIME,
                        DtDMEnum.DATE_TIME,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP,
                        DtDorisEnum.DATE
                ); break;
                case TIME: convert(model,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIME,
                        DtDMEnum.TIME,
                        DtKingbaseESEnum.TIME,
                        DtPostgreSQLEnum.TIME,
                        DtDorisEnum.DATE_TIME
                ); break;
                case DATE_TIME:convert(model,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.DATE_TIME,
                        DtDMEnum.DATE_TIME,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP,
                        DtDorisEnum.DATE_TIME
                ); break;
                case TIMESTAMP: convert(model,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIMESTAMP,
                        DtDMEnum.TIME_STAMP,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP,
                        DtDorisEnum.DATE_TIME
                ); break;
                default:
            }
            model.setDtMySQLEnum((DtMySQLEnum) dtEnum);
            return model;
        }
    },
    ORACLE(DbBase.ORACLE){
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtOracleEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case NUMBER: convert(model,
                        DtMySQLEnum.BIGINT,
                        DtSQLServerEnum.BIGINT,
                        DtDMEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT,
                        DtPostgreSQLEnum.INT8
                ); break;
                // ===================== 文本字符串类型 ===========================;= */
                case CHAR:
                case NCHAR: convert(model,
                        DtMySQLEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtKingbaseESEnum.CHAR,
                        DtPostgreSQLEnum.CHAR
                ); break;
                case VARCHAR2: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                case NVARCHAR2: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR2,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                case CLOB: convert(model,
                        DtMySQLEnum.TEXT,
                        DtSQLServerEnum.TEXT,
                        DtDMEnum.TEXT,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT
                ); break;
                case NCLOB: convert(model,
                        DtMySQLEnum.LONG_TEXT,
                        DtSQLServerEnum.N_TEXT,
                        DtDMEnum.CLOB,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT
                ); break;
                // ===================== 浮点类型  =============================;*/
                case FLOAT: convert(model,
                        DtMySQLEnum.FLOAT,
                        DtDMEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4,
                        DtPostgreSQLEnum.FLOAT4
                ); break;
                // ===================== 日期时间类型:  ==========================;== */
                case DATE: convert(model,
                        DtMySQLEnum.DATE,
                        DtSQLServerEnum.DATE,
                        DtDMEnum.DATE,
                        DtKingbaseESEnum.DATE,
                        DtPostgreSQLEnum.DATE
                ); break;
                case TIMESTAMP: convert(model,
                        DtMySQLEnum.TIMESTAMP,
                        DtSQLServerEnum.TIMESTAMP,
                        DtDMEnum.TIME_STAMP,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                default:
            }
            model.setDtOracleEnum((DtOracleEnum) dtEnum);
            return model;
        }
    },
    SQL_SERVER(DbBase.SQL_SERVER){
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtSQLServerEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case TINY_INT: convert(model,
                        DtMySQLEnum.TINY_INT,
                        DtOracleEnum.NUMBER,
                        DtDMEnum.TINY_INT,
                        DtKingbaseESEnum.TINY_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case SMALL_INT: convert(model,
                        DtMySQLEnum.SMALL_INT,
                        DtOracleEnum.NUMBER,
                        DtDMEnum.SMALL_INT,
                        DtKingbaseESEnum.SMALL_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case INT: convert(model,
                        DtMySQLEnum.INT,
                        DtOracleEnum.NUMBER,
                        DtDMEnum.INT,
                        DtKingbaseESEnum.INTEGER,
                        DtPostgreSQLEnum.INT4
                ); break;
                case BIGINT: convert(model,
                        DtMySQLEnum.BIGINT,
                        DtOracleEnum.NUMBER,
                        DtDMEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT,
                        DtPostgreSQLEnum.INT8
                ); break;
                // ================== 文本字符串类型 =============================== *;
                case CHAR: convert(model,
                        DtMySQLEnum.CHAR,
                        DtOracleEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtKingbaseESEnum.CHAR,
                        DtPostgreSQLEnum.CHAR
                ); break;
                case NVARCHAR: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtOracleEnum.NVARCHAR2,
                        DtDMEnum.VARCHAR2,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                case VARCHAR: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtOracleEnum.NVARCHAR2,
                        DtDMEnum.VARCHAR,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                case VARCHAR_MAX:
                case IMAGE:
                case TEXT: convert(model,
                        DtMySQLEnum.TEXT,
                        DtOracleEnum.CLOB,
                        DtDMEnum.TEXT,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT
                ); break;
                case N_TEXT: convert(model,
                        DtMySQLEnum.LONG_TEXT,
                        DtOracleEnum.NCLOB,
                        DtDMEnum.CLOB,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT
                ); break;
                // ================== 日期时间类型:  =============================== *;
                case DATE: convert(model,
                        DtMySQLEnum.DATE,
                        DtOracleEnum.DATE,
                        DtDMEnum.DATE,
                        DtKingbaseESEnum.DATE,
                        DtPostgreSQLEnum.DATE
                ); break;
                case TIME: convert(model,
                        DtMySQLEnum.TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtDMEnum.TIME,
                        DtKingbaseESEnum.TIME,
                        DtPostgreSQLEnum.TIME
                ); break;
                case DATE_TIME: convert(model,
                        DtMySQLEnum.DATE_TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtDMEnum.DATE_TIME,
                        DtKingbaseESEnum.DATE_TIME,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                case TIMESTAMP: convert(model,
                        DtMySQLEnum.TIMESTAMP,
                        DtOracleEnum.TIMESTAMP,
                        DtDMEnum.TIME_STAMP,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                // ===================== 浮点类型  =============================;*/
                case FLOAT: convert(model,
                        DtMySQLEnum.FLOAT,
                        DtDMEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4,
                        DtPostgreSQLEnum.FLOAT4
                ); break;
                // ================== 定点数类型:  =============================== *;
                case DECIMAL: convert(model,
                        DtMySQLEnum.DECIMAL,
                        DtOracleEnum.NUMBER,
                        DtDMEnum.DECIMAL,
                        DtKingbaseESEnum.NUMERIC,
                        DtPostgreSQLEnum.NUMERIC
                ); break;
                default:
            }
            model.setDtSQLServerEnum((DtSQLServerEnum) dtEnum);
            return model;
        }
    },
    DM(DbBase.DM){
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtDMEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case TINY_INT: convert(model,
                        DtMySQLEnum.TINY_INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.TINY_INT,
                        DtKingbaseESEnum.TINY_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case SMALL_INT: convert(model,
                        DtMySQLEnum.SMALL_INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.SMALL_INT,
                        DtKingbaseESEnum.SMALL_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case INT: convert(model,
                        DtMySQLEnum.INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.INT,
                        DtKingbaseESEnum.INTEGER,
                        DtPostgreSQLEnum.INT4
                ); break;
                case BIGINT: convert(model,
                        DtMySQLEnum.BIGINT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT,
                        DtPostgreSQLEnum.INT8
                ); break;
                // ===================== 文本字符串类型 ===========================*;
                case CHAR: convert(model,
                        DtMySQLEnum.CHAR,
                        DtOracleEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtKingbaseESEnum.CHAR,
                        DtPostgreSQLEnum.CHAR
                ); break;
                case VARCHAR:
                case VARCHAR2: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtOracleEnum.NVARCHAR2,
                        DtSQLServerEnum.NVARCHAR,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                case TEXT:
                case CLOB: convert(model,
                        DtMySQLEnum.TEXT,
                        DtOracleEnum.CLOB,
                        DtSQLServerEnum.TEXT,
                        DtKingbaseESEnum.TEXT,
                        DtPostgreSQLEnum.TEXT      
                ); break;
                // ===================== 时间格式 ===========================*;
                case DATE: convert(model,
                        DtMySQLEnum.DATE,
                        DtSQLServerEnum.DATE,
                        DtOracleEnum.DATE,
                        DtKingbaseESEnum.DATE,
                        DtPostgreSQLEnum.DATE
                ); break;
                case DATE_TIME: convert(model,
                        DtMySQLEnum.DATE_TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.DATE_TIME,
                        DtKingbaseESEnum.DATE_TIME,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                case TIME_STAMP: convert(model,
                        DtMySQLEnum.TIMESTAMP,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIMESTAMP,
                        DtKingbaseESEnum.TIMESTAMP,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                case TIME: convert(model,
                        DtMySQLEnum.TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIME,
                        DtKingbaseESEnum.TIME,
                        DtPostgreSQLEnum.TIME
                ); break;
                // =====================浮点===========================*;
                case FLOAT: convert(model,
                        DtMySQLEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4,
                        DtPostgreSQLEnum.FLOAT4       
                ); break;
                case DOUBLE: convert(model,
                        DtMySQLEnum.DOUBLE,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT8,
                        DtPostgreSQLEnum.FLOAT8    
                ); break;
                // ===================== 定点型 ===========================*
                case DECIMAL:
                case DEC: convert(model,
                        DtMySQLEnum.DECIMAL,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.DECIMAL,
                        DtKingbaseESEnum.NUMERIC,
                        DtPostgreSQLEnum.NUMERIC   
                ); break;
                default:
            }
            model.setDtDMEnum((DtDMEnum) dtEnum);
            return model;
        }
    },
    KINGBASE(DbBase.KINGBASE_ES){
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtKingbaseESEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case TINY_INT: convert(model,
                        DtMySQLEnum.TINY_INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.TINY_INT,
                        DtDMEnum.TINY_INT,
                        DtPostgreSQLEnum.INT2   
                ); break;
                case SMALL_INT: convert(model,
                        DtMySQLEnum.SMALL_INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.SMALL_INT,
                        DtDMEnum.SMALL_INT,
                        DtPostgreSQLEnum.INT2 
                ); break;
                case INTEGER: convert(model,
                        DtMySQLEnum.INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.INT,
                        DtDMEnum.INT,
                        DtPostgreSQLEnum.INT4       
                ); break;
                case BIGINT: convert(model,
                        DtMySQLEnum.BIGINT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.BIGINT,
                        DtDMEnum.BIGINT,
                        DtPostgreSQLEnum.INT8  
                ); break;
                // ============= 文本字符串类型 ===========================*;
                case CHAR: convert(model,
                        DtMySQLEnum.CHAR,
                        DtOracleEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtPostgreSQLEnum.CHAR       
                ); break;
                case VARCHAR: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtOracleEnum.NVARCHAR2,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR   
                ); break;
                case TEXT: convert(model,
                        DtMySQLEnum.TEXT,
                        DtOracleEnum.CLOB,
                        DtSQLServerEnum.TEXT,
                        DtDMEnum.TEXT,
                        DtPostgreSQLEnum.TEXT      
                ); break;
                // =============浮点===========================*;
                case FLOAT4: convert(model,
                        DtMySQLEnum.FLOAT,
                        DtDMEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtPostgreSQLEnum.FLOAT4    
                ); break;
                case FLOAT8: convert(model,
                        DtMySQLEnum.DOUBLE,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtDMEnum.DOUBLE,
                        DtPostgreSQLEnum.FLOAT8     
                ); break;
                // =================== 定点型 ========================================*;
                case NUMERIC: convert(model,
                        DtMySQLEnum.DECIMAL,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.DECIMAL,
                        DtDMEnum.DECIMAL,
                        DtPostgreSQLEnum.NUMERIC  
                ); break;
                // ============= 时间格式 ===========================*;
                case TIME: convert(model,
                        DtMySQLEnum.TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIME,
                        DtDMEnum.TIME,
                        DtPostgreSQLEnum.TIME       
                ); break;
                case DATE: convert(model,
                        DtMySQLEnum.DATE,
                        DtSQLServerEnum.DATE,
                        DtOracleEnum.DATE,
                        DtDMEnum.DATE,
                        DtPostgreSQLEnum.DATE        
                ); break;
                case DATE_TIME: convert(model,
                        DtMySQLEnum.DATE_TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.DATE_TIME,
                        DtDMEnum.DATE_TIME,
                        DtPostgreSQLEnum.TIMESTAMP  
                ); break;
                case TIMESTAMP: convert(model,
                        DtMySQLEnum.TIMESTAMP,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIMESTAMP,
                        DtDMEnum.TIME_STAMP,
                        DtPostgreSQLEnum.TIMESTAMP  
                ); break;
                default:
            }
            model.setDtKingbaseESEnum((DtKingbaseESEnum) dtEnum);
            return model;
        }
    },
    POSTGRE(DbBase.POSTGRE_SQL){
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtPostgreSQLEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case INT2: convert(model,
                        DtMySQLEnum.SMALL_INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.SMALL_INT,
                        DtDMEnum.SMALL_INT,
                        DtKingbaseESEnum.SMALL_INT
                ); break;
                case INT4: convert(model,
                        DtMySQLEnum.INT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.INT,
                        DtDMEnum.INT,
                        DtKingbaseESEnum.INTEGER
                ); break;
                case INT8: convert(model,
                        DtMySQLEnum.BIGINT,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.BIGINT,
                        DtDMEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT
                ); break;
                // =================== 文本字符串类型 =======================================*/
                case CHAR: convert(model,
                        DtMySQLEnum.CHAR,
                        DtOracleEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtKingbaseESEnum.CHAR
                ); break;
                case VARCHAR: convert(model,
                        DtMySQLEnum.VARCHAR,
                        DtOracleEnum.NVARCHAR2,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR,
                        DtKingbaseESEnum.VARCHAR
                ); break;
                case TEXT: convert(model,
                        DtMySQLEnum.TEXT,
                        DtOracleEnum.CLOB,
                        DtSQLServerEnum.TEXT,
                        DtDMEnum.TEXT,
                        DtKingbaseESEnum.TEXT
                ); break;
                // ====================浮点===========================*/
                case FLOAT4: convert(model,
                        DtMySQLEnum.FLOAT,
                        DtDMEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4
                ); break;
                case FLOAT8: convert(model,
                        DtMySQLEnum.DOUBLE,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtDMEnum.DOUBLE,
                        DtKingbaseESEnum.FLOAT8
                ); break;
                // =================== 定点型 =======================================*/
                case NUMERIC: convert(model,
                        DtMySQLEnum.DECIMAL,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.DECIMAL,
                        DtDMEnum.DECIMAL,
                        DtKingbaseESEnum.NUMERIC
                ); break;
                // =================== 时间日期 =======================================*/
                case TIME: convert(model,
                        DtMySQLEnum.TIME,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIME,
                        DtDMEnum.TIME,
                        DtKingbaseESEnum.TIME
                ); break;
                case DATE: convert(model,
                        DtMySQLEnum.DATE,
                        DtSQLServerEnum.DATE,
                        DtOracleEnum.DATE,
                        DtDMEnum.DATE,
                        DtKingbaseESEnum.DATE
                ); break;
                case TIMESTAMP: convert(model,
                        DtMySQLEnum.TIMESTAMP,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.TIMESTAMP,
                        DtDMEnum.TIME_STAMP,
                        DtKingbaseESEnum.TIMESTAMP
                ); break;
                default:
            }
            model.setDtPostgreSQLEnum((DtPostgreSQLEnum)dtEnum);
            return model;
        }
    },

    Doris(DbBase.DORIS){
        /**
         * DtMutualConvertEnum(默认原始搭配)，在这个地方重复的枚举会重写覆盖原来的搭配
         */
        @Override
        public <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){
            DtConvertModel<T> model = new DtConvertModel<>();
            switch ((DtDorisEnum)dtEnum){
                /* =============================== 整数类型 =============================== */
                case TINY_INT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.TINY_INT,
                        DtDMEnum.TINY_INT,
                        DtKingbaseESEnum.TINY_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case SMALL_INT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.SMALL_INT,
                        DtDMEnum.SMALL_INT,
                        DtKingbaseESEnum.SMALL_INT,
                        DtPostgreSQLEnum.INT2
                ); break;
                case INT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.INT,
                        DtDMEnum.INT,
                        DtKingbaseESEnum.INTEGER,
                        DtPostgreSQLEnum.INT4
                ); break;
                case BIGINT: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.BIGINT,
                        DtDMEnum.BIGINT,
                        DtKingbaseESEnum.BIGINT,
                        DtPostgreSQLEnum.INT8
                ); break;
                // ====================== 文本字符串类型:  ==========================;==== */
                // mysql(varchar) 转成 oracle(NVARCHAR2)、sqlserver(nvarchar;
                case CHAR: convert(model,
                        DtOracleEnum.CHAR,
                        DtSQLServerEnum.CHAR,
                        DtDMEnum.CHAR,
                        DtKingbaseESEnum.CHAR,
                        DtPostgreSQLEnum.CHAR
                ); break;
                // mysql(test) 转成 oracle(NCLOB)、sqlserver(nvarchar(max); break;
                case VARCHAR: convert(model,
                        DtOracleEnum.NVARCHAR2,
                        DtSQLServerEnum.NVARCHAR,
                        DtDMEnum.VARCHAR,
                        DtKingbaseESEnum.VARCHAR,
                        DtPostgreSQLEnum.VARCHAR
                ); break;
                // ====================== 浮点类型:  =============================;= */
                case FLOAT: convert(model,
                        DtDMEnum.FLOAT,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtKingbaseESEnum.FLOAT4,
                        DtPostgreSQLEnum.FLOAT4
                ); break;
                case DOUBLE: convert(model,
                        DtOracleEnum.FLOAT,
                        DtSQLServerEnum.FLOAT,
                        DtDMEnum.DOUBLE,
                        DtKingbaseESEnum.FLOAT8,
                        DtPostgreSQLEnum.FLOAT8
                ); break;
                // ====================== 定点数类型:  ============================;== */
                case DECIMAL: convert(model,
                        DtOracleEnum.NUMBER,
                        DtSQLServerEnum.DECIMAL,
                        DtDMEnum.DECIMAL,
                        DtKingbaseESEnum.NUMERIC,
                        DtPostgreSQLEnum.NUMERIC
                ); break;
                // ====================== 日期时间类型:  ===========================;=== */
                case DATE: convert(model,
                        DtOracleEnum.TIMESTAMP,
                        DtSQLServerEnum.DATE_TIME,
                        DtDMEnum.DATE_TIME,
                        DtKingbaseESEnum.DATE_TIME,
                        DtPostgreSQLEnum.TIMESTAMP
                ); break;
                case DATE_TIME:
                default:
            }
            model.setDtDorisEnum((DtDorisEnum) dtEnum);
            return model;
        }
    }
    ;

    private final String dbType;

    /**
     * 获取转换模型
     * @param fromDtEnum 数据类型枚举
     * @return 转换模型
     * @throws DataException ignore
     */
    public static <T extends DtInterface> DtConvertModel<T> getConvertModel(T fromDtEnum) throws DataException {
        return choose(fromDtEnum.getDbType()).getModel(fromDtEnum);
    }

    /* ================================ 内部方法 ================================== */

    /**
     * 根据数据类型枚举获取对应转换模型
     * @param dtEnum 数据类型枚举
     * @return ignore
     */
    protected  <T extends DtInterface> DtConvertModel<T> getModel(T dtEnum){return null;}

    /**
     * 根据数据获取其转换规则枚举
     * @param dbType 数据库类型
     * @return 转换规则枚举
     * @throws DataException ignore
     */
    private static DtConvertEnum choose(String dbType) throws DataException {
        for (DtConvertEnum dbMethod : DtConvertEnum.values()) {
            if(dbMethod.getDbType().equals(dbType)){
                return dbMethod;
            }
        }
        throw  new DataException(MsgCode.DB005.get());
    }

    /**
     * 内部特殊转换
     * @param model 转换模型
     * @param convertDtEnums 特殊转换集合
     */
    protected <T extends DtInterface> void convert(DtConvertModel<T> model, DtInterface... convertDtEnums){
        try{
            // 特殊的转换关系
            for (DtInterface convertDtEnum : convertDtEnums) {
                model.setDtEnum(convertDtEnum);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
