package org.openea.eap.extj.permission.model.datainterface;

public enum DataInterfaceVarEnum {

    /**
     * 当前用户
     */
    USER("@user"),
    /**
     * 当前组织及子组织
     */
    ORGANDSUB("@currentOrganizationAndSuborganization"),
    /**
     * 当前用户及下属
     */
    USERANDSUB("@currentUsersAndSubordinates"),
    /**
     * 当前分管组织
     */
    CHARORG("@chargeorganization"),
    /**
     * 当前组织
     */
    ORG("@organization"),
    /**
     * 当前分管组织及子组织
     */
    CHARORGANDSUB("@currentChargeorganizationAndSuborganization"),
    /**
     * 页行数
     */
    PAGESIZE("@pageSize"),
    /**
     * 关键字
     */
    KEYWORD("@keyword"),
    /**
     * 当前页
     */
    CURRENTPAGE("@currentPage"),
    /**
     * 条数
     */
    OFFSETSIZE("@offsetSize"),
    /**
     * 当前分管组织及子组织
     */
    SHOWKEY("@showKey"),
    /**
     * 当前分管组织及子组织
     */
    SHOWVALUE("@showValue")
    ;
    private String condition;

    DataInterfaceVarEnum(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
}
