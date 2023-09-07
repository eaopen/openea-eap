package org.openea.eap.extj.emnus;

public enum ModuleTypeEnum {
    SYSTEM_DATAINTEFASE("bd"),
    SYSTEM_BILLRULE("bb"),
    SYSTEM_MODULE("bm"),
    SYSTEM_DBTABLE("bdb"),
    SYSTEM_DICTIONARYDATA("bdd"),
    SYSTEM_PRINT("bp"),
    VISUAL_DATA("vd"),
    VISUAL_DEV("vdd"),
    VISUAL_APP("va"),
    VISUAL_PORTAL("vp"),
    FLOW_FLOWENGINE("ffe"),
    ACCOUNT_CONFIG("mac"),
    MESSAGE_TEMPLATE("mes"),
    MESSAGE_SEND_CONFIG("msc"),
    FLOW_FLOWDFORM("fff");

    private String tableName;

    private ModuleTypeEnum(String moduleName) {
        this.tableName = moduleName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
