package org.openea.eap.extj.util;

import org.openea.eap.extj.base.vo.DownloadVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * todo eap待处理
 */
@Component
public class DataFileExport implements FileExport {

    private static final Logger log = LoggerFactory.getLogger(DataFileExport.class);

    public DataFileExport() {
    }

    public DownloadVO exportFile(Object clazz, String filePath, String fileName, String tableName) {
        fileName = this.containsSensitive(fileName);
        String json = JsonUtil.getObjectToString(clazz);
        if (json == null) {
            json = "";
        }
        fileName = fileName + System.currentTimeMillis() + "." + tableName;
//        FileInfo fileInfo = FileUploadUtils.uploadFile(json.getBytes(StandardCharsets.UTF_8), filePath, fileName);
//        DownloadVO vo = DownloadVO.builder().name(fileInfo.getFilename()).url(UploaderUtil.uploaderFile(fileInfo.getFilename() + "#export") + "&name=" + fileName).build();
//        return vo;
        return null;
    }

    private String containsSensitive(String fileName) {
        if (StringUtil.isNotEmpty(fileName)) {
            String[] split = "<,>,/,\\\\,:,|".split(",");
            String[] var3 = split;
            int var4 = split.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String str = var3[var5];
                fileName = fileName.replaceAll(str, "");
            }
        }

        return fileName;
    }
}
