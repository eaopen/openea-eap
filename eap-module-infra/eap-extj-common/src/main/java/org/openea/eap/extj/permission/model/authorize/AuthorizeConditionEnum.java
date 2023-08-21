package org.openea.eap.extj.permission.model.authorize;

public enum AuthorizeConditionEnum {
    /**
     * 任意文本
     */
    TEXT("text"),
    /**
     * 当前组织
     */
    ORGANIZE("@organizeId"),
    /**
     * 当前组织及子组织
     */
    ORGANIZEANDUNDER("@organizationAndSuborganization"),
    /**
     * 当前用户
     */
    USER("@userId"),
    /**
     * 当前用户及下属
     */
    USERANDUNDER("@userAraSubordinates"),
    /**
     * 当前分管组织
     */
    BRANCHMANAGEORG("@branchManageOrganize"),

    /**
     * 当前分管组织及子组织
     */
    BRANCHMANAGEORGANIZEUNDER("@branchManageOrganizeAndSub")
    ;
    private String condition;

    AuthorizeConditionEnum(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}