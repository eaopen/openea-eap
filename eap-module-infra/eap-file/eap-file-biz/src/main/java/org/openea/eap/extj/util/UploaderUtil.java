package org.openea.eap.extj.util;

public class UploaderUtil {
    public UploaderUtil() {
    }

    /**
     * 头像名称处理
     * @param fileName
     * @return
     */
    public static String uploaderImg(String fileName) {
        return uploaderImg((String)null, fileName);
    }

    /**
     * 头像名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderImg(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Image/userAvatar/";
        }

        return url + fileName;
    }

    /**
     * 附件名称处理
     * @param url
     * @param fileName
     * @return
     */
    public static String uploaderFile(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Download?encryption=";
        }

        String ticket = TicketUtil.createTicket("", 60L);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }

    /**
     * 附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderFile(String fileName) {
        return uploaderFile((String)null, fileName);
    }

    /**
     * 代码生成器附件名称处理
     * @param fileName
     * @return
     */
    public static String uploaderVisualFile(String fileName) {
        String url = "/api/visualdev/Generater/DownloadVisCode?encryption=";
        String ticket = TicketUtil.createTicket("", 60L);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }
}