package org.openea.eap.extj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * todo eap
 */
@Component
@Data
public class LocalPathConfig {
    @Value("${config.DataBackupFilePath}")
    private String dataBackupFilePath;
    @Value("${config.TemporaryFilePath}")
    private String temporaryFilePath;
    @Value("${config.SystemFilePath}")
    private String systemFilePath;
    @Value("${config.TemplateFilePath}")
    private String templateFilePath;
    @Value("${config.TemplateCodePath}")
    private String templateCodePath;
    @Value("${config.EmailFilePath}")
    private String emailFilePath;
    @Value("${config.BiVisualPath}")
    private String biVisualPath;
    @Value("${config.DocumentFilePath}")
    private String documentFilePath;
    @Value("${config.DocumentPreviewPath}")
    private String documentPreviewPath;
    @Value("${config.UserAvatarFilePath}")
    private String userAvatarFilePath;
    @Value("${config.IMContentFilePath}")
    private String imContentFilePath;
    @Value("${config.MPMaterialFilePath}")
    private String mpMaterialFilePath;
    @Value("${config.MPUploadFileType}")
    private String mpUploadFileType;
    @Value("${config.WeChatUploadFileType}")
    private String weChatUploadFileType;
    @Value("${config.AllowUploadFileType}")
    private String allowUploadFileType;
    @Value("${config.AllowUploadImageType}")
    private String allowUploadImageType;
    @Value("${config.WebDirectoryPath:}")
    private String webDirectoryPath;
    @Value("${config.WebAnnexFilePath}")
    private String webAnnexFilePath;
    @Value("${config.AllowPreviewFileType}")
    private String allowPreviewFileType;
    @Value("${config.PreviewType}")
    private String previewType;
    @Value("${config.kkFileUrl}")
    private String kkFileUrl;

}
