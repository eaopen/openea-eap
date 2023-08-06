package org.openea.eap.extj.constant.model;

public class MCode {
    private final String type;
    private final String code;
    private final String desc;

    public MCode(String type, String desc) {
        this.type = type;
        this.code = this.getClass().getName();
        this.desc = desc;
    }

    public String get() {
        return this.desc;
    }

    public String get(String... values) {
        String info = this.desc;
        String[] var3 = values;
        int var4 = values.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String value = var3[var5];
            info = info.replaceFirst("\\?", value);
        }

        return info;
    }

    public String getMsg() {
        return this.type + ":" + this.code + " " + this.desc;
    }
}