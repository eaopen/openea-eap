package org.openea.eap.extj.emnus;

public enum DataFieldType {
    Double("Double"),
    Varchar("String"),
    Number("Int32");

    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private DataFieldType(String message) {
        this.message = message;
    }
}
