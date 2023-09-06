package org.openea.eap.extj.base;

/**
 * 查询功能
 *
 *
 */
public enum SearchMethodEnum {
    /**
     * like
     */
    Contains("Contains"),
    /**
     * 等于
     */
    Equal("Equal"),
    /**
     * 不等于
     */
    NotEqual("NotEqual"),
    /**
     * 小于
     */
    LessThan("LessThan"),
    /**
     * 小于等于
     */
    LessThanOrEqual("LessThanOrEqual"),
    /**
     * 大于
     */
    GreaterThan("GreaterThan"),
    /**
     * 大于等于
     */
    GreaterThanOrEqual("GreaterThanOrEqual"),
    /**
     * 包含
     */
    Included("Included"),
    /**
     * 不包含
     */
    NotIncluded("NotIncluded"),
    /**
     * 是null
     */
    IsNull("IsNull"),
    /**
     * 不是null
     */
    IsNotNull("IsNotNull"),
    /**
     * 包含(like)
     */
    Like("like"),
    /**
     * 不包含(notLike)
     */
    NotLike("NotLike")
    ;

    private String message;

    SearchMethodEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
