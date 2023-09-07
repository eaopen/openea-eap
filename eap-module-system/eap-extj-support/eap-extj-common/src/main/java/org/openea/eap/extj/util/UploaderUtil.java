package org.openea.eap.extj.util;

public class UploaderUtil {
    public UploaderUtil() {
    }

    public static String uploaderImg(String fileName) {
        return uploaderImg((String)null, fileName);
    }

    public static String uploaderImg(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Image/userAvatar/";
        }

        return url + fileName;
    }

    public static String uploaderFile(String url, String fileName) {
        if (url == null) {
            url = "/api/file/Download?encryption=";
        }

        String ticket = TicketUtil.createTicket("", 60L);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }

    public static String uploaderFile(String fileName) {
        return uploaderFile((String)null, fileName);
    }

    public static String uploaderVisualFile(String fileName) {
        String url = "/api/visualdev/Generater/DownloadVisCode?encryption=";
        String ticket = TicketUtil.createTicket("", 60L);
        String name = DesUtil.aesEncode(ticket + "#" + fileName);
        return url + name;
    }
}