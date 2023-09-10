package org.openea.eap.extj.util;


import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.model.FileListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

/**
 * todo eap待处理
 */
public class FileUploadUtils {

    public static FileInfo uploadFile(MultipartFile multipartFile, String path, String fileName) {
        return null;
    }

    public static FileInfo uploadFile(byte[] bytes, String path, String fileName) {
        FileInfo fileInfo=new FileInfo();
        fileInfo.setId(RandomUtil.uuId());
        fileInfo.setPath(path);
        fileInfo.setFilename(fileName);
        return fileInfo;
    }
    public static byte[] downloadFileByte(String temporaryFilePath, String fileName, boolean b) {
        return null;
    }

    public static String getLocalBasePath() {
        return null;
    }

    public static void downLocal(String s, String filePath, String objectName) {
    }


    public static List<FileListVO> getDefaultFileList(String s) {
        return null;
    }

    public static List<FileListVO> getFileList(String path) {
        return Collections.emptyList();
    }
}