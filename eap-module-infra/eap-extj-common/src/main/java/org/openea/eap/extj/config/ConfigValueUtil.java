package org.openea.eap.extj.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.openea.eap.extj.model.MultiTenantType;
import org.springframework.stereotype.Component;


/**
 * todo eap
 */
@ConfigurationProperties(
        prefix = "config"
)
@Component
@Data
public class ConfigValueUtil {

    private String dataBackupFilePath;
    private String temporaryFilePath;
    private String systemFilePath;
    private String templateFilePath;
    private String templateCodePath;
    private String emailFilePath;
    private String biVisualPath;
    private String documentFilePath;
    private String documentPreviewPath;
    private String userAvatarFilePath;
    private String imContentFilePath;
    @Value("${config.AllowUploadFileType:}")
    private String allowUploadFileType;
    @Value("${config.AllowUploadImageType:}")
    private String allowUploadImageType;
    @Value("${config.AllowPreviewFileType:}")
    private String allowPreviewFileType;
    @Value("${config.PreviewType:}")
    private String previewType;
    @Value("${config.kkFileUrl:}")
    private String kkFileUrl;
    private String serviceDirectoryPath;
    @Value("${config.CodeAreasName:}")
    private String codeAreasName;
    private String webAnnexFilePath;
    private boolean enablePreAuth;
    private boolean shardingSphereEnabled;
    @Value("${config.ErrorReport:}")
    private String errorReport;
    @Value("${config.ErrorReportTo:}")
    private String errorReportTo;
    @Value("${config.RecordLog:}")
    private String recordLog;
    private boolean multiTenancy;
    @Value("${config.MultiTenancyUrl:}")
    private String multiTenancyUrl;
    private MultiTenantType multiTenantType;
    private String multiTenantColumn = "F_TenantId";
    private boolean enableLogicDelete = false;
    private String logicDeleteColumn = "F_DELETEMARK";
    @Value("${config.SoftVersion:}")
    private String softVersion;
    @Value("${config.IgexinEnabled:}")
    private String igexinEnabled;
    @Value("${config.IgexinAppid:}")
    private String igexinAppid;
    @Value("${config.IgexinAppkey:}")
    private String igexinAppkey;
    @Value("${config.IgexinMastersecret:}")
    private String igexinMastersecret;
    @Value("${config.AppUpdateContent:}")
    private String appUpdateContent;
    @Value("${config.AppVersion:}")
    private String appVersion;
    @Value("${config.TestVersion:}")
    private String testVersion;
    private boolean enableInnerAuth;
    private String appPushUrl;
}
