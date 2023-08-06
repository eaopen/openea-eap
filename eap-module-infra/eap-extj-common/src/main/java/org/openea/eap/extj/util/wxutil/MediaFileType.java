package org.openea.eap.extj.util.wxutil;

public enum MediaFileType {
    News("news"),
    Image("image"),
    Voice("voice"),
    Video("video"),
    Thumb("thumb"),
    File("file");

    private String message;

    private MediaFileType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
