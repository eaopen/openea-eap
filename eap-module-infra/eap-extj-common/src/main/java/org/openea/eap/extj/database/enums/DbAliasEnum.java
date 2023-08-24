package org.openea.eap.extj.database.enums;

public enum DbAliasEnum {
    TABLE_SIZE("F_TABLE_SIZE"),
    TABLE_NAME("F_TABLE_NAME"),
    TABLE_COMMENT("F_TABLE_COMMENT"),
    TABLE_TYPE("F_TABLE_TYPE"),
    TABLE_SUM("F_TABLE_SUM"),
    FIELD("F_FIELD"),
    FIELD_COMMENT("F_FIELD_COMMENT"),
    DEFAULT_VALUE("F_DEFAULT_VALUE"),
    AUTO_INCREMENT("F_AUTO_INCREMENT"),
    COLUMN_DEFAULT("F_COLUMN_DEFAULT"),
    IS_IDENTITY("F_IS_IDENTITY"),
    ALLOW_NULL("F_ALLOW_NULL"),
    PRIMARY_KEY("F_PRIMARY_KEY"),
    DATA_TYPE("F_DATA_TYPE"),
    CHAR_LENGTH("F_CHAR_LENGTH"),
    NUM_PRECISION("F_NUM_PRECISION"),
    NUM_SCALE("F_NUM_SCALE"),
    TOTAL_RECORD("totalRecord"),
    TOTAL("total"),
    AUTO_TRIGGER("F_AUTO_TRIGGER");

    private final String alias;

    public String getAlias(String dbEncode) {
        return getAlias(dbEncode, this.alias);
    }

    public static String getAlias(String dbEncode, String keyWord) {
        if ("PostgreSQL".equals(dbEncode)) {
            return keyWord.toLowerCase();
        } else {
            return "Oracle".equals(dbEncode) ? keyWord.toUpperCase() : keyWord;
        }
    }

    private DbAliasEnum(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }
}
