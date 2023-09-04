package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtPostgreSQLEnum implements DtInterface {
    INT2("int2", new IntegerLimit()),
    INT4("int4", new IntegerLimit()),
    INT8("int8", new IntegerLimit()),
    CHAR("char", (new StringLimit(true)).charLength(10485760L, 50L)),
    VARCHAR("varchar", (new StringLimit(true)).charLength(10485760L, 50L)),
    TEXT("text", new StringLimit()),
    FLOAT4("float4", (new FloatLimit(true)).precision(24, 18).scale(18, 3)),
    FLOAT8("float8", (new FloatLimit(true)).precision(53, 18).scale(18, 3)),
    NUMERIC("numeric", (new DecimalLimit(true)).precision(1000, 18).scale(100, 3)),
    TIME("time", new DateTimeLimit()),
    DATE("date", new DateTimeLimit()),
    TIMESTAMP("timestamp", new DateTimeLimit());

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtPostgreSQLEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}