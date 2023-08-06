package org.openea.eap.extj.util.enums;

public enum DictionaryDataEnum {
    VISUALDEV(1, "webDesign"),
    VISUALDEV_APP(2, "appDesigner"),
    VISUALDEV_GENERATER_FLOWWORK(3, "flowForm"),
    VISUALDEV_GENERATER(4, "webForm"),
    VISUALDEV_GENERATER_APP(5, "appForm"),
    VISUALDEV_PORTAL(6, "portalDesigner"),
    SYSTEM_DBLINK(7, "dbType"),
    SYSTEM_PRINTDEV(8, "printDev"),
    SYSTEM_DATAINTERFACE(12, "DataInterfaceType"),
    PERMISSION_ROLE(9, "RoleType"),
    PERMISSION_GROUP(12, "groupType"),
    VISUALDEV_REPORT(10, "ReportSort"),
    FLOWWOEK_ENGINE(11, "WorkFlowCategory");

    private Integer type;
    private String dictionaryTypeId;

    private DictionaryDataEnum(Integer type, String dictionaryTypeId) {
        this.type = type;
        this.dictionaryTypeId = dictionaryTypeId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDictionaryTypeId() {
        return this.dictionaryTypeId;
    }

    public void setDictionaryTypeId(String dictionaryTypeId) {
        this.dictionaryTypeId = dictionaryTypeId;
    }

    public static String getTypeId(Integer type) {
        DictionaryDataEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DictionaryDataEnum value = var1[var3];
            if (type.equals(value.getType())) {
                return value.getDictionaryTypeId();
            }
        }

        return "";
    }
}

