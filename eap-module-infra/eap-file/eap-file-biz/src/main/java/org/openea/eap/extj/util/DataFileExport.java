package org.openea.eap.extj.util;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.util.file.DbSensitiveConstant;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 *  数据接口文件导入导出
 */
@Component
@Slf4j
public class DataFileExport implements FileExport {

    @Override
    public DownloadVO exportFile(Object obj, String filePath, String fileName, String tableName) {
        fileName = containsSensitive(fileName);
        /** 1.model拼凑成Json字符串 */
        String json = JsonUtil.getObjectToString(obj);
        /** 2.写入到文件中 */
        fileName += System.currentTimeMillis() + "." + tableName;
        if (json == null) {
            json = "";
        }
        FileInfo fileInfo = FileUploadUtils.uploadFile(json.getBytes(StandardCharsets.UTF_8), filePath, fileName);
        /** 生成下载下载文件路径 */
        DownloadVO vo = DownloadVO.builder().name(fileInfo.getFilename()).url(UploaderUtil.uploaderFile(fileInfo.getFilename() + "#" + "export") + "&name=" + fileName).build();
        return vo;
    }

    /**
     * 替换敏感字
     *
     * @param fileName
     * @return
     */
    private String containsSensitive(String fileName) {
        if (StringUtil.isNotEmpty(fileName)) {
            String[] split = DbSensitiveConstant.FILE_SENSITIVE.split(",");
            for (String str : split) {
                fileName = fileName.replaceAll(str, "");
            }
        }
        return fileName;
    }
}
