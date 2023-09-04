package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtKingbaseESEnum implements DtInterface {
    TINY_INT("tinyint", new IntegerLimit()),
    SMALL_INT("smallint", new IntegerLimit()),
    INTEGER("integer", new IntegerLimit()),
    BIGINT("bigint", new IntegerLimit()),
    CHAR("character", (new StringLimit(true)).charLength(10485760L, 50L)),
    VARCHAR("varchar", (new StringLimit(true)).charLength(10485760L, 50L)),
    TEXT("text", new StringLimit()),
    FLOAT4("float4", new FloatLimit()),
    FLOAT8("float8", new FloatLimit()),
    NUMERIC("numeric", (new DecimalLimit(true)).precision(1000, 38).scale(1000, 38)),
    TIME("time", new DateTimeLimit()),
    DATE("date", new DateTimeLimit()),
    DATE_TIME("datetime", new DateTimeLimit()),
    TIMESTAMP("timestamp", new DateTimeLimit());

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtKingbaseESEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}

