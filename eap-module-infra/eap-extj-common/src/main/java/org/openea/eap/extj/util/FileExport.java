package org.openea.eap.extj.util;

import org.openea.eap.extj.base.vo.DownloadVO;

public interface FileExport {
    DownloadVO exportFile(Object clazz, String filePath, String fileName, String tableName);
}