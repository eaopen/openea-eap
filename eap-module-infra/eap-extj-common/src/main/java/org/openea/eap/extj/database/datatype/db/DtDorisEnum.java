package org.openea.eap.extj.database.datatype.db;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtDorisEnum implements DtInterface {
    TINY_INT("tinyint", (new IntegerLimit()).fixedPrecision(3)),
    BOOLEAN("boolean", (new IntegerLimit()).fixedPrecision((Integer)null)),
    SMALL_INT("smallint", (new IntegerLimit()).fixedPrecision(5)),
    INT("int", (new IntegerLimit()).fixedPrecision(10)),
    BIGINT("bigint", (new IntegerLimit()).fixedPrecision(19)),
    LARGE_INT("largeint", (new IntegerLimit()).fixedPrecision((Integer)null)),
    CHAR("char", (new StringLimit(true)).charLength(255L, 50L)),
    VARCHAR("varchar", (new StringLimit(true)).charLength(16170L, 50L)),
    STRING("string", new StringLimit()),
    FLOAT("float", (new FloatLimit(true)).precision(35, 18).scale(30, 3)),
    DOUBLE("double", (new FloatLimit(true)).fixedPrecision(35).fixedScale(30)),
    DECIMAL("decimal", (new DecimalLimit(true)).precision(27, 18).scale(30, 3)),
    DATE("date", new DateTimeLimit()),
    DATE_TIME("datetime", new DateTimeLimit()),
    BITMAP("BITMAP", new StringLimit()),
    HLL("HLL", new StringLimit());

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtDorisEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}

