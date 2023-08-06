package org.openea.eap.extj.emnus;

public enum SearchMethodEnum {
    Contains("Contains"),
    Equal("Equal"),
    NotEqual("NotEqual"),
    LessThan("LessThan"),
    LessThanOrEqual("LessThanOrEqual"),
    GreaterThan("GreaterThan"),
    GreaterThanOrEqual("GreaterThanOrEqual"),
    Included("Included"),
    NotIncluded("NotIncluded"),
    IsNull("IsNull"),
    IsNotNull("IsNotNull"),
    Like("like"),
    NotLike("NotLike");

    private String message;

    private SearchMethodEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}