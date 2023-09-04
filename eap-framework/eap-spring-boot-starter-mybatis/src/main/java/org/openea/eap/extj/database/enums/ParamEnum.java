package org.openea.eap.extj.database.enums;

public enum ParamEnum {
    DB_URL("{dbUrl}"),
    DB_NAME("{dbName}"),
    USER_NAME("{userName}"),
    DB_SCHEMA("{dbSchema}"),
    TABLE_SPACE("{tableSpace}"),
    TABLE("{table}"),
    SPLIT("split"),
    FILED("{filed}"),
    DATA_LENGTH("{dataLength}"),
    COMMENT("{comment}");

    private final String target;

    public String getTarget() {
        return this.target;
    }

    public String getParamSign() {
        return "?";
    }

    private ParamEnum(String target) {
        this.target = target;
    }
}

