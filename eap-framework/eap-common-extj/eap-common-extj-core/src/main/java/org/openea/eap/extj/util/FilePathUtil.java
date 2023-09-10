package org.openea.eap.extj.util;

import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.FileTypeConstant;
import org.openea.eap.extj.util.context.SpringContext;

public class FilePathUtil {

    private static ConfigValueUtil configValueUtil = SpringContext.getBean(ConfigValueUtil.class);

    /**
     * 通过fileType获取文件夹名称
     *
     * @param fileType 文件类型
     * @return
     */
    public static String getFilePath(String fileType) {
        String filePath = fileType;
        //判断是那种类型得到相应的文件夹
        switch (fileType.toLowerCase()) {
            //用户头像存储路径
            case FileTypeConstant.USERAVATAR:
                filePath = configValueUtil.getUserAvatarFilePath();
                break;
            //邮件文件存储路径
            case FileTypeConstant.MAIL:
                filePath = configValueUtil.getEmailFilePath();
                break;
            //前端附件文件目录
            case FileTypeConstant.ANNEX:
                filePath = configValueUtil.getWebAnnexFilePath();
                break;
            case FileTypeConstant.ANNEXPIC:
                filePath = configValueUtil.getWebAnnexFilePath();
                break;
            //IM聊天图片+语音存储路径
            case FileTypeConstant.IM:
                filePath = configValueUtil.getImContentFilePath();
                break;
            //临时文件存储路径
            case FileTypeConstant.WORKFLOW:
                filePath = configValueUtil.getSystemFilePath();
                break;
            //文档管理存储路径
            case FileTypeConstant.DOCUMENT:
                filePath = configValueUtil.getDocumentFilePath();
                break;
            //数据库备份文件路径
            case FileTypeConstant.DATABACKUP:
                filePath = configValueUtil.getDataBackupFilePath();
                break;
            //临时文件存储路径
            case FileTypeConstant.TEMPORARY:
                filePath = configValueUtil.getTemporaryFilePath();
                break;
            //允许上传文件类型
            case FileTypeConstant.ALLOWUPLOADFILETYPE:
                filePath = configValueUtil.getAllowUploadFileType();
                break;
            //文件在线预览存储pdf
            case FileTypeConstant.DOCUMENTPREVIEWPATH:
                filePath = configValueUtil.getDocumentPreviewPath();
                break;
            //文件模板存储路径
            case FileTypeConstant.TEMPLATEFILE:
                filePath = configValueUtil.getTemplateFilePath();
                break;
            //前端文件目录
            case FileTypeConstant.SERVICEDIRECTORY:
                break;
//            //后端文件目录
//            case FileTypeConstant.WEBDIRECTORY:
//                filePath = configValueUtil.getWebDirectoryPath();
//                break;
            // 文档预览
            case FileTypeConstant.DOCUMENTPREVIEW:
                filePath = configValueUtil.getDocumentPreviewPath();
                break;
            //导出
            case FileTypeConstant.EXPORT:
                filePath = configValueUtil.getTemporaryFilePath();
                break;
            // 大屏相关图片
            case FileTypeConstant.BIVISUALPATH:
                filePath = configValueUtil.getBiVisualPath();
                break;
            case FileTypeConstant.CODETEMP:
                filePath = configValueUtil.getTemplateCodePath();
                break;
            case FileTypeConstant.TEMPLATECODEPATH:
                filePath = configValueUtil.getTemplateCodePath();
                break;
            case FileTypeConstant.FILEZIPDOWNTEMPPATH:
                filePath = configValueUtil.getFileZipDownTempPath();
                break;
            default:
                break;
        }
        return filePath;
    }
}
