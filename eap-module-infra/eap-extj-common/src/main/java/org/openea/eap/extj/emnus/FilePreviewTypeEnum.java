package org.openea.eap.extj.emnus;

public enum FilePreviewTypeEnum {
    YOZO_ONLINE_PREVIEW("yozoOnlinePreview"),
    LOCAL_PREVIEW("localPreview");

    private String type;

    private FilePreviewTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
