package org.openea.eap.extj.util;

import org.openea.eap.extj.config.LocalPathConfig;
import org.openea.eap.extj.util.context.SpringContext;

public class FilePathUtil {
    private static LocalPathConfig configValueUtil = (LocalPathConfig) SpringContext.getBean(LocalPathConfig.class);

    public FilePathUtil() {
    }

    public static String getFilePath(String fileType) {
        String filePath = fileType;
        switch (fileType.toLowerCase()) {
            case "useravatar":
                filePath = configValueUtil.getUserAvatarFilePath() + "/";
                break;
            case "mail":
                filePath = configValueUtil.getEmailFilePath() + "/";
                break;
            case "annex":
                filePath = configValueUtil.getWebAnnexFilePath() + "/";
                break;
            case "annexpic":
                filePath = configValueUtil.getWebAnnexFilePath() + "/";
                break;
            case "im":
                filePath = configValueUtil.getImContentFilePath() + "/";
                break;
            case "workflow":
                filePath = configValueUtil.getSystemFilePath() + "/";
                break;
            case "document":
                filePath = configValueUtil.getDocumentFilePath() + "/";
                break;
            case "dataBackup":
                filePath = configValueUtil.getDataBackupFilePath() + "/";
                break;
            case "temporary":
                filePath = configValueUtil.getTemporaryFilePath() + "/";
                break;
            case "allowuploadfiletype":
                filePath = configValueUtil.getAllowUploadFileType() + "/";
                break;
            case "preview":
                filePath = configValueUtil.getDocumentPreviewPath() + "/";
                break;
            case "templatefile":
                filePath = configValueUtil.getTemplateFilePath() + "/";
            case "servicedirectory":
            default:
                break;
            case "documentpreview":
                filePath = configValueUtil.getDocumentPreviewPath() + "/";
                break;
            case "export":
                filePath = configValueUtil.getTemporaryFilePath() + "/";
                break;
            case "bivisualpath":
                filePath = configValueUtil.getBiVisualPath() + "/";
                break;
            case "codetemp":
                filePath = "CodeTemp/";
                break;
            case "templatecodepath":
                filePath = configValueUtil.getTemplateCodePath() + "/";
                break;
            case "filezipdownloadtemppath":
                filePath = "FileTemp/";
        }

        return filePath;
    }
}
