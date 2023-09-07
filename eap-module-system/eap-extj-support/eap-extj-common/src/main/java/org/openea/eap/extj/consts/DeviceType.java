package org.openea.eap.extj.consts;

public enum DeviceType {
    PC("PC"),
    APP("APP"),
    TEMPUSER("TEMPUSER"),
    TEMPUSERLIMITED("TEMPUSERLIMITED");

    private final String device;

    public String getDevice() {
        return this.device;
    }

    private DeviceType(String device) {
        this.device = device;
    }
}
