package org.openea.eap.extj.enums;

/**
 * 图片类型
 *
 *
 */
public enum VisualImgEnum {
    /**
     * 背景图片
     */
    BG("0", "bg"),
    /**
     * 图片框
     */
    BORDER("1", "border"),
    /**
     * 图片
     */
    SOURCE("2", "source"),
    /**
     * banner
     */
    BANNER("3", "banner"),
    /**
     * banner
     */
    BACKGROUND("5", "background"),
    /**
     * 大屏截图
     */
    SCREENSHOT("4", "screenShot");

    /**
     * 状态码
     */
    private String code;
    /**
     * 消息
     */
    private String message;

    VisualImgEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 判断名称是否存在
     *
     * @return boolean
     */
    public static VisualImgEnum getByMessage(String type) {
        for (VisualImgEnum status : VisualImgEnum.values()) {
            if (status.getMessage().equals(type)) {
                return status;
            }
        }
        return null;
    }

}
