package org.openea.eap.extj.database.datatype.db;


import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.*;

public enum DtSQLServerEnum implements DtInterface {
    TINY_INT("tinyint", new IntegerLimit()),
    SMALL_INT("smallint", new IntegerLimit()),
    INT("int", new IntegerLimit()),
    BIGINT("bigint", new IntegerLimit()),
    CHAR("char", (new StringLimit(true)).charLength(8000L, 50L)),
    NVARCHAR("nvarchar", (new StringLimit(true)).charLength(8000L, 50L)),
    VARCHAR("varchar", (new StringLimit(true)).charLength(4000L, 50L)),
    VARCHAR_MAX("nvarchar(max)", (new StringLimit()).fixedCharLength(4005L)),
    TEXT("text", new StringLimit()),
    N_TEXT("ntext", new StringLimit()),
    IMAGE("image", new StringLimit()),
    FLOAT("float", (new FloatLimit()).precision(53, 18).scale(18, 3)),
    DATE("date", new DateTimeLimit()),
    TIME("time", new DateTimeLimit()),
    DATE_TIME("datetime", new DateTimeLimit()),
    DATE_TIME2("datetime2", new DateTimeLimit()),
    TIMESTAMP("timestamp", new DateTimeLimit()),
    DECIMAL("decimal", (new DecimalLimit(true)).precision(38, 18).scale(18, 3));

    private final String dataType;
    private final DtLimitBase dtLimit;

    public String getDataType() {
        return this.dataType;
    }

    public DtLimitBase getDtLimit() {
        return this.dtLimit;
    }

    private DtSQLServerEnum(String dataType, DtLimitBase dtLimit) {
        this.dataType = dataType;
        this.dtLimit = dtLimit;
    }
}
