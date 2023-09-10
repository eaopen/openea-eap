package org.openea.eap.extj.util.file;

import lombok.Data;

/**
 * 文件储存类型常量类
 *
 *
 */
@Data
public class StorageType {
    /**
     * 本地存储
     */
    public static final String STORAGE = "storage";

    /**
     * Minio存储
     */
    public static final String MINIO = "minio";



    public static final String ALI_OSS = "aliyun-oss";
    public static final String QINIU = "qiniu-kodo";
    public static final String TENCENT = "tencent-cos";

}
