package org.openea.eap.module.visualdev.extend.util;

import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.XSSEscape;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UpUtil {
    public UpUtil() {
    }

    public static List<MultipartFile> getFileAll() {
        MultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest mRequest = resolver.resolveMultipart(ServletUtil.getRequest());
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        List<MultipartFile> list = new ArrayList();
        Iterator var4 = fileMap.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, MultipartFile> map = (Map.Entry)var4.next();
            list.add(map.getValue());
        }

        return list;
    }

    public static long getFileSize(MultipartFile multipartFile) {
        return multipartFile.getSize();
    }

    public static String getFileType(MultipartFile multipartFile) {
        if (multipartFile.getContentType() != null) {
            String[] split = multipartFile.getOriginalFilename().split("\\.");
            if (split.length > 1) {
                return split[split.length - 1];
            }
        }

        return "";
    }

    public static String upLoad(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        } else {
            String fileName = file.getOriginalFilename();
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String time = formatter.format(date);
            String uuidFileName = time + "_" + RandomUtil.uuId() + "@" + fileName;
            File dest = new File(XSSEscape.escapePath(uuidFileName));
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdir();
            }

            try {
                file.transferTo(dest);
                return uuidFileName;
            } catch (IllegalStateException var8) {
                var8.printStackTrace();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            return null;
        }
    }
}