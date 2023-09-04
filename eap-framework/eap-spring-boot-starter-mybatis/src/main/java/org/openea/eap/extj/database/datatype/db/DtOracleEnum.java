package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.DateTimeLimit;
import org.openea.eap.extj.database.datatype.limit.FloatLimit;
import org.openea.eap.extj.database.datatype.limit.NumberLimit;
import org.openea.eap.extj.database.datatype.limit.StringLimit;

public enum DtOracleEnum implements DtInterface {
    NUMBER("NUMBER", (new NumberLimit(true)).precision(38, 11).scale(127, -84)),
    CHAR("CHAR", (new StringLimit(true)).charLength(2000L, 50L)),
    NCHAR("VARCHAR2", (new StringLimit(true)).charLength(2000L, 50L)),
    VARCHAR2("VARCHAR2", (new StringLimit(true)).bitLength(4000L, 50L)),
    NVARCHAR2("NVARCHAR2", (new StringLimit(true)).charLength(2000L, 50L)),
    CLOB("CLOB", new StringLimit()),
    BLOB("BLOB", new StringLimit()),
    NCLOB("NCLOB", new StringLimit()),
    FLOAT("FLOAT", (new FloatLimit(true)).precision(126, 18).scale(126, 3)),
    DATE("DATE", new DateTimeLimit()),
    TIMESTAMP("TIMESTAMP", new DateTimeLimit());

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtOracleEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}

