package org.openea.eap.extj.database.sql.enums;

import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.enums.ParamEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.database.sql.model.DbStruct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * SqlServer SQL语句模板
 *
 *
 */
@Getter
@AllArgsConstructor
public enum SqlSQLServerEnum implements SqlFrameBase {

    /* =============================== 系统语句 ==================================== */
    FIELDS("SELECT cast(a.name as varchar(50)) "+ DbAliasEnum.FIELD.getAlias()+" ," +
            " cast(case when exists(SELECT 1 FROM sysobjects where xtype='PK' and name in ( " +
            " SELECT name FROM sysindexes WHERE indid in( " +
            " SELECT indid FROM sysindexkeys WHERE id = a.id AND colid=a.colid ))) " +
            " then '1' else '0' end as varchar(50)) "+DbAliasEnum.PRIMARY_KEY.getAlias() +", " +
            " cast(b.name as varchar(50)) "+ DbAliasEnum.DATA_TYPE.getAlias() +", " +
//            " cast(COLUMNPROPERTY(a.id,a.name,'PRECISION') as varchar(50)) " + DbAliasConst.DATA_LENGTH + ", " +
            " a.length " + DbAliasEnum.CHAR_LENGTH.getAlias() + ", " +
            " a.xprec " + DbAliasEnum.NUM_PRECISION.getAlias() + ", " +
            " a.xscale " + DbAliasEnum.NUM_SCALE.getAlias() + ", " +
            " h.is_identity " + DbAliasEnum.IS_IDENTITY.getAlias() + ", " +
            " cast(case when a.isnullable=0 then '0'else '1' end as varchar(50)) "+ DbAliasEnum.ALLOW_NULL.getAlias() +", " +
            " cast(isnull(e.text,'') as varchar(50)) " + DbAliasEnum.DEFAULT_VALUE.getAlias() +", " +
            " cast(isnull(g.[value],'') as varchar(50)) " + DbAliasEnum.FIELD_COMMENT.getAlias() +
            "\nFROM syscolumns a " +
            "left join systypes b on a.xusertype=b.xusertype " +
            "inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' " +
            "left join syscomments e on a.cdefault=e.id " +
            "left join sys.extended_properties g on a.id=g.major_id and a.colid=g.minor_id " +
            "left join sys.extended_properties f on d.id=f.major_id and f.minor_id=0 " +
            "left join sys.columns h ON d.id= h.object_id and a.name = h.name " +
            "where d.name = " + ParamEnum.DB_NAME.getParamSign() +
            "\norder by a.id,a.colorder"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
        }
    },
    TABLES(
            "SET NOCOUNT ON DECLARE\n" +
                    "@TABLEINFO TABLE (\n" +
                    "\tNAME VARCHAR ( 50 ),\n" +
                    "\tSUMROWS VARCHAR ( 11 ),\n" +
                    "\tRESERVED VARCHAR ( 50 ),\n" +
                    "\tDATA VARCHAR ( 50 ),\n" +
                    "\tINDEX_SIZE VARCHAR ( 50 ),\n" +
                    "\tUNUSED VARCHAR ( 50 ),\n" +
                    "\tPK VARCHAR ( 50 ) \n" +
                    ") DECLARE\n" +
                    "@TABLENAME TABLE ( NAME VARCHAR ( 50 ) ) DECLARE\n" +
                    "@NAME VARCHAR ( 50 ) DECLARE\n" +
                    "@PK VARCHAR ( 50 ) INSERT INTO @TABLENAME ( NAME ) SELECT\n" +
                    "O.NAME \n" +
                    "FROM\n" +
                    "\tSYSOBJECTS O,\n" +
                    "\tSYSINDEXES I \n" +
                    "WHERE\n" +
                    "\tO.ID = I.ID \n" +
                    "\tAND O.XTYPE = 'U' \n" +
                    "\tAND I.INDID < 2 \n" +
                    "\tAND O.UID  = (SELECT schema_id FROM sys.schemas where name = " + ParamEnum.DB_SCHEMA.getParamSign() + ")\n" +
                    "ORDER BY\n" +
                    "\tI.ROWS DESC,\n" +
                    "\tO.NAME\n" +
                    "WHILE\n" +
                    "\t\tEXISTS ( SELECT 1 FROM @TABLENAME ) BEGIN\n" +
                    "\t\tSELECT TOP\n" +
                    "\t\t\t1 @NAME = NAME \n" +
                    "\t\tFROM\n" +
                    "\t\t\t@TABLENAME DELETE @TABLENAME \n" +
                    "\t\tWHERE\n" +
                    "\t\t\tNAME = @NAME DECLARE\n" +
                    "\t\t\t@OBJECTID INT \n" +
                    "\t\t\tSET @OBJECTID = OBJECT_ID( @NAME ) SELECT\n" +
                    "\t\t\t@PK = COL_NAME( @OBJECTID, COLID ) \n" +
                    "\t\tFROM\n" +
                    "\t\t\tSYSOBJECTS AS O\n" +
                    "\t\t\tINNER JOIN SYSINDEXES AS I ON I.NAME = O.NAME\n" +
                    "\t\t\tINNER JOIN SYSINDEXKEYS AS K ON K.INDID = I.INDID \n" +
                    "\t\tWHERE\n" +
                    "\t\t\tO.XTYPE = 'PK' \n" +
                    "\t\t\tAND PARENT_OBJ = @OBJECTID \n" +
                    "\t\t\tAND K.ID = @OBJECTID INSERT INTO @TABLEINFO ( NAME, SUMROWS, RESERVED, DATA, INDEX_SIZE, UNUSED ) EXEC SYS.SP_SPACEUSED @NAME UPDATE @TABLEINFO \n" +
                    "\t\t\tSET PK = @PK \n" +
                    "\t\tWHERE\n" +
                    "\t\t\tNAME = @NAME \n" +
                    "\tEND SELECT CAST\n" +
                    "\t\t( F.NAME AS VARCHAR ( 50 ) ) " + DbAliasEnum.TABLE_NAME.getAlias() + ",\n" +
                    "\t\tCAST ( ISNULL( P.TDESCRIPTION, F.NAME ) AS VARCHAR ( 50 ) ) " + DbAliasEnum.TABLE_COMMENT.getAlias() + ",\n" +
                    "\t\tCAST ( F.RESERVED AS VARCHAR ( 50 ) ) " + DbAliasEnum.TABLE_SIZE.getAlias() + ",\n" +
                    "\t\tCAST ( RTRIM( F.SUMROWS ) AS VARCHAR ( 50 ) ) " + DbAliasEnum.TABLE_SUM.getAlias() + ",\n" +
                    "\t\tCAST ( F.PK AS VARCHAR ( 50 ) ) " + DbAliasEnum.PRIMARY_KEY.getAlias() + " \n" +
                    "\tFROM\n" +
                    "\t\t@TABLEINFO F\n" +
                    "\t\tLEFT JOIN (\n" +
                    "\t\tSELECT\n" +
                    "\t\t\tNAME =\n" +
                    "\t\tCASE\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t\tWHEN A.COLORDER = 1 THEN\n" +
                    "\t\t\t\tD.NAME ELSE '' \n" +
                    "\t\t\tEND,\n" +
                    "\t\t\tTDESCRIPTION =\n" +
                    "\t\tCASE\n" +
                    "\t\t\t\t\n" +
                    "\t\t\t\tWHEN A.COLORDER = 1 THEN\n" +
                    "\t\t\t\tISNULL( F.VALUE, '' ) ELSE '' \n" +
                    "\t\t\tEND \n" +
                    "\t\t\tFROM\n" +
                    "\t\t\t\tSYSCOLUMNS A\n" +
                    "\t\t\t\tLEFT JOIN SYSTYPES B ON A.XUSERTYPE = B.XUSERTYPE\n" +
                    "\t\t\t\tINNER JOIN SYSOBJECTS D ON A.ID = D.ID \n" +
                    "\t\t\t\tAND D.XTYPE = 'U' \n" +
                    "\t\t\t\tAND D.NAME <> 'DTPROPERTIES'\n" +
                    "\t\t\t\tLEFT JOIN SYS.EXTENDED_PROPERTIES F ON D.ID = F.MAJOR_ID \n" +
                    "\t\t\tWHERE\n" +
                    "\t\t\t\tA.COLORDER = 1 \n" +
                    "\t\t\t\tAND F.MINOR_ID = 0 \n" +
                    "\t\t\t) P ON F.NAME = P.NAME \n" +
                    "\t\tWHERE\n" +
                    "\t\t\t1 = 1 \n" +
                    "\tORDER BY\n" +
                    "\t" + DbAliasEnum.TABLE_NAME.getAlias()
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getSqlServerDbSchema());
        }
    },
    TABLESANDVIEW(
            "SELECT s.Name as " + DbAliasEnum.TABLE_NAME.getAlias()
                    + ",Convert(varchar(max),tbp.value) as " + DbAliasEnum.TABLE_COMMENT.getAlias()
                    + ",s.type as " + DbAliasEnum.TABLE_TYPE.getAlias()
                    + "\nFROM sysobjects s\n" +
                    "LEFT JOIN sys.extended_properties as tbp ON s.id=tbp.major_id and tbp.minor_id=0  AND (tbp.Name='MS_Description' OR tbp.Name is null) WHERE s.xtype IN('V','U')"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getSqlServerDbSchema());
        }
    },
    TABLE(
            TABLES.sqlFrame.replace("1 = 1", "F.NAME = " + ParamEnum.TABLE.getParamSign())
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(dbStruct.getSqlServerDbSchema());
            list.add(table);
        }
    },
    EXISTS_TABLE(
            "SELECT COUNT (*) AS TOTAL FROM (" +
            "SELECT table_name FROM INFORMATION_SCHEMA.TABLES where table_type = 'BASE TABLE' and TABLE_NAME = "+ParamEnum.TABLE.getParamSign()
            + ") AS COUNT_TAB"
    ){
        @Override
        public void setStructParams(String table, DbStruct dbStruct, List<String> list) {
            list.add(table);
        }
    },

    /* =============================== 定义语句 ==================================== */
    CREATE_TABLE(SqlDMEnum.CREATE_TABLE.getSqlFrame()){
        @Override
        public String createIncrement(String sqlFrame,  Map<String, String> paramsMap) {
            return SqlDMEnum.CREATE_TABLE.createIncrement(sqlFrame, paramsMap);
        }
    },
    // （Data Definition Language）简称 DDL：用来建立数据库、数据库对象和定义列的命令。包括：create、alter、drop
    RE_TABLE_NAME   ("EXEC sp_rename {oldTable}, {newTable}"),


    COMMENT_TABLE   ("EXEC sp_addextendedproperty 'MS_Description',N'{comment}','SCHEMA',N'dbo','TABLE',N'{table}'"),
    COMMENT_COLUMN  ("EXEC sp_addextendedproperty 'MS_Description',N'{comment}','SCHEMA',N'dbo','TABLE',N'{table}','COLUMN',N'{column}'"),
    ALTER_COLUMN    ("<ALTER> <TABLE> {table} <ADD> {column} {dataType} [[NOT] [NULL]] [<DEFAULT> {defaultValue}]"),

    /* =============================== DML操作语句 ==================================== */
    // （Data Manipulation Language）简称 DML：用来操纵数据库中数据的命令。包括：select、insert、update、delete。

    /*第二种方式：offset fetch next方式（SQL2012以上的版本才支持：推荐使用 ）-*/
    SELECT_PAGE_NEW ("{selectSql} [orderSql] OFFSET {beginIndex} rows fetch next {pageSize} rows only"),
    /*
            row_number() over(order by {column})
            row_number() over(order by RAND())      随机字段
            row_number() over(order by NEWID())     临时ID
     */
    ORDER_PAGE     ("SELECT * " +
                            "FROM " +
                                "(SELECT TOP {endIndex} ROW_NUMBER() OVER(ORDER BY {orderColumn}) JNPF_ROW,* " +
                            "FROM " +
                                "({selectSql}) AS JNPF_TAB ORDER BY JNPF_ROW) AS JNPF_TEMP_TABLE " +
                            "WHERE " +
                                "JNPF_ROW BETWEEN {beginIndex} AND {endIndex}"),
    DB_TIME_SQL     ("Select CONVERT(varchar(100), GETDATE(), 120) as TIME"),



    /* =============================== 其他 ==================================== */



    ;

    public String sqlFrame;
    private final String dbEncode = DbBase.SQL_SERVER;

}
