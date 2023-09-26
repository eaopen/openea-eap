package org.openea.eap.framework.file.core.enums;

import cn.hutool.core.util.ArrayUtil;
import org.openea.eap.framework.file.core.client.FileClient;
import org.openea.eap.framework.file.core.client.FileClientConfig;
import org.openea.eap.framework.file.core.client.db.DBFileClient;
import org.openea.eap.framework.file.core.client.db.DBFileClientConfig;
import org.openea.eap.framework.file.core.client.ftp.FtpFileClient;
import org.openea.eap.framework.file.core.client.ftp.FtpFileClientConfig;
import org.openea.eap.framework.file.core.client.local.LocalFileClient;
import org.openea.eap.framework.file.core.client.local.LocalFileClientConfig;
import org.openea.eap.framework.file.core.client.s3.S3FileClient;
import org.openea.eap.framework.file.core.client.s3.S3FileClientConfig;
import org.openea.eap.framework.file.core.client.sftp.SftpFileClient;
import org.openea.eap.framework.file.core.client.sftp.SftpFileClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openea.eap.framework.file.core.client.webdav.WebDavFileClient;
import org.openea.eap.framework.file.core.client.webdav.WebDavFileClientConfig;

/**
 * 文件存储器枚举
 *
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, DBFileClientConfig.class, DBFileClient.class),

    LOCAL(10, LocalFileClientConfig.class, LocalFileClient.class),
    FTP(11, FtpFileClientConfig.class, FtpFileClient.class),
    SFTP(12, SftpFileClientConfig.class, SftpFileClient.class),
    WebDav(16, WebDavFileClientConfig.class, WebDavFileClient.class),

    S3(20, S3FileClientConfig.class, S3FileClient.class),
    ;

    /**
     * 存储器
     */
    private final Integer storage;

    /**
     * 配置类
     */
    private final Class<? extends FileClientConfig> configClass;
    /**
     * 客户端类
     */
    private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }

}
