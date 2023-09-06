package org.openea.eap.extj.util.enums;

/**
 * 数据字典分类id
 *
 * 
 */
public enum DictionaryDataEnum {
    /**
     * 功能设计
     */
    VISUALDEV(1, "webDesign"),
    /**
     * 移动设计
     */
    VISUALDEV_APP(2, "appDesigner"),
    /**
     * 流程表单
     */
    VISUALDEV_GENERATER_FLOWWORK(3, "flowForm"),
    /**
     * 功能表单
     */
    VISUALDEV_GENERATER(4, "webForm"),
    /**
     * 移动表单
     */
    VISUALDEV_GENERATER_APP(5, "appForm"),
    /**
     * 门户设计
     */
    VISUALDEV_PORTAL(6, "portalDesigner"),
    /**
     * 数据连接
     */
    SYSTEM_DBLINK(7, "dbType"),
    /**
     * 打印模板
     */
    SYSTEM_PRINTDEV(8, "printDev"),
    /**
     * 数据接口
     */
    SYSTEM_DATAINTERFACE(12, "DataInterfaceType"),
    /**
     * 角色类型
     */
    PERMISSION_ROLE(9, "RoleType"),
    /**
     * 分组id
     */
    PERMISSION_GROUP(12, "groupType"),
    /**
     * 报表设计
     */
    VISUALDEV_REPORT(10, "ReportSort"),
    /**
     * 流程设计
     */
    FLOWWOEK_ENGINE(11, "WorkFlowCategory");

    private Integer type;
    private String dictionaryTypeId;

    DictionaryDataEnum(Integer type, String dictionaryTypeId) {
        this.type = type;
        this.dictionaryTypeId = dictionaryTypeId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDictionaryTypeId() {
        return dictionaryTypeId;
    }

    public void setDictionaryTypeId(String dictionaryTypeId) {
        this.dictionaryTypeId = dictionaryTypeId;
    }

    /**
     * 获取通过type获取数据字典分类id
     *
     * @param type 类型
     * @return
     */
    public static String getTypeId(Integer type) {
        for (DictionaryDataEnum value : DictionaryDataEnum.values()) {
            if (type.equals(value.getType())) {
                return value.getDictionaryTypeId();
            }
        }
        return "";
    }

}
