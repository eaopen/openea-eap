package org.openea.eap.extj.util;

import lombok.Cleanup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class FileDownloadUtil {

    /**
     * 下载文件
     *
     * @param bytes
     * @param fileName 订单信息.pdf
     */
    public static void downloadFile(byte[] bytes, String fileName, String downName) {
        if (StringUtil.isNotEmpty(downName)) {
            fileName = downName;
        }
        HttpServletResponse response = ServletUtil.getResponse();
        HttpServletRequest request = ServletUtil.getRequest();
        try {
            @Cleanup InputStream is = new ByteArrayInputStream(bytes);
            @Cleanup BufferedInputStream bis = new BufferedInputStream(is);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
            if (fileName.endsWith(".pdf")) {
                response.setContentType("application/pdf");
            }
            if (fileName.contains(".svg")) {
                response.setContentType("image/svg+xml");
            }
            //编码的文件名字,关于中文乱码的改造
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            if (-1 != agent.indexOf("msie") || -1 != agent.indexOf("trident")) {
                //IE
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (-1 != agent.indexOf("mozilla")) {
                //火狐，谷歌
                codeFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            } else {
                codeFileName = URLEncoder.encode(fileName, "UTF-8");
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(codeFileName.getBytes(), "utf-8"));
            @Cleanup OutputStream os = response.getOutputStream();
            int i;
            byte[] buff = new byte[1024 * 8];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 显示文件
     */
    public static void flushImage(byte[] bytes) {
        try {
            @Cleanup OutputStream outputStream = null;
            @Cleanup InputStream in = null;
            //读取指定路径下面的文件
            in = new ByteArrayInputStream(bytes);
            outputStream = new BufferedOutputStream(ServletUtil.getResponse().getOutputStream());
            //创建存放文件内容的数组
            byte[] buff = new byte[1024];
            //所读取的内容使用n来接收
            int n;
            //当没有读取完时,继续读取,循环
            while ((n = in.read(buff)) != -1) {
                //将字节数组的数据全部写入到输出流中
                outputStream.write(buff, 0, n);
            }
            //强制将缓存区的数据进行输出
            outputStream.flush();
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
