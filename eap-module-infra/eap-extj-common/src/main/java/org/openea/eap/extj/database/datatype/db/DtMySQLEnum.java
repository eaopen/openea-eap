package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtMySQLEnum implements DtInterface {
    TINY_INT("tinyint", (new IntegerLimit()).fixedPrecision(3)),
    SMALL_INT("smallint", (new IntegerLimit()).fixedPrecision(5)),
    MEDIUM_INT("mediumint", (new IntegerLimit()).fixedPrecision(7)),
    INT("int", (new IntegerLimit()).fixedPrecision(10)),
    BIGINT("bigint", (new IntegerLimit()).fixedPrecision(19)),
    BIT("bit", (new StringLimit(true)).charLength(15L, 64L)),
    BLOB("blob", new StringLimit()),
    CHAR("char", (new StringLimit(true)).charLength(50L, 255L)),
    VARCHAR("varchar", (new StringLimit(true)).charLength(16170L, 50L)),
    TINY_TEXT("tinytext", (new StringLimit()).fixedCharLength(225L)),
    TEXT("text", (new StringLimit()).fixedCharLength(65535L)),
    MEDIUM_TEXT("mediumtext", (new StringLimit()).fixedCharLength(16777215L)),
    LONG_TEXT("longtext", (new StringLimit()).fixedBitLength(4294967295L)),
    LONG_BLOB("longblob", (new StringLimit()).fixedCharLength(0L)),
    FLOAT("float", (new FloatLimit(true)).precision(255, 18).scale(30, 3)),
    DOUBLE("double", (new FloatLimit(true)).precision(255, 18).scale(30, 3)),
    DECIMAL("decimal", (new DecimalLimit(true)).precision(65, 18).scale(30, 3)),
    TIME("time", (new DateTimeLimit(true)).scale(6, 6)),
    DATE_TIME("datetime", (new DateTimeLimit(true)).scale(6, 6)),
    TIMESTAMP("timestamp", (new DateTimeLimit(true)).scale(6, 6)),
    YEAR("year", (new DateTimeLimit(true)).precision(4, 4)),
    DATE("date", new DateTimeLimit());

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtMySQLEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}
