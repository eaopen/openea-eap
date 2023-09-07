package org.openea.eap.extj.emnus;

public enum ExportModelTypeEnum {
    Design(1, "design"),
    App(2, "app"),
    Portal(5, "portal");

    private final int code;
    private final String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ExportModelTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}