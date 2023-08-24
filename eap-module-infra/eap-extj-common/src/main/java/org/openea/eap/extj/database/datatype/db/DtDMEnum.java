package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtDMEnum implements DtInterface {
    TINY_INT("TINYINT", new IntegerLimit()),
    SMALL_INT("SMALLINT", new IntegerLimit()),
    INT("INT", new IntegerLimit()),
    BIGINT("BIGINT", new IntegerLimit()),
    CHAR("CHAR", (new StringLimit(true)).charLength(32767L, 150L)),
    VARCHAR("VARCHAR", (new StringLimit(true)).bitLength(32767L, 150L)),
    VARCHAR2("VARCHAR2", (new StringLimit(true)).bitLength(32767L, 150L)),
    TEXT("TEXT", (new StringLimit()).fixedCharLength(2147483647L)),
    CLOB("CLOB", (new StringLimit()).fixedCharLength(2147483647L)),
    DATE("DATE", new DateTimeLimit()),
    DATE_TIME("DATETIME", new DateTimeLimit()),
    TIME_STAMP("TIMESTAMP", new DateTimeLimit()),
    TIME("TIME", new DateTimeLimit()),
    DECIMAL("DECIMAL", (new DecimalLimit(true)).precision(38, 18).scale(38, 6, 3)),
    DEC("DEC", (new DecimalLimit(true)).precision(38, 18).scale(38, 6, 3)),
    FLOAT("FLOAT", (new FloatLimit(true)).precision(24, 18).scale(30, 3)),
    DOUBLE("DOUBLE", (new FloatLimit(true)).precision(53, 18).scale(30, 3));

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtDMEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}