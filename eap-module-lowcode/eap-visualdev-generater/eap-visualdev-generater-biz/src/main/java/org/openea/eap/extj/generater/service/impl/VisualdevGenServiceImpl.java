package org.openea.eap.extj.generater.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.CaseFormat;
import org.apache.velocity.app.Velocity;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.mapper.VisualdevMapper;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.base.util.VisualUtils;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.generater.factory.CodeGenerateFactory;
import org.openea.eap.extj.generater.model.GenBaseInfo;
import org.openea.eap.extj.generater.service.VisualdevGenService;
import org.openea.eap.extj.generater.util.app.AppGenModel;
import org.openea.eap.extj.generater.util.app.AppGenUtil;
import org.openea.eap.extj.generater.util.common.CodeGenerateUtil;
import org.openea.eap.extj.generater.util.custom.VelocityEnum;
import org.openea.eap.extj.generater.util.workFlow.WorkGenModel;
import org.openea.eap.extj.generater.util.workFlow.WorkGenUtil;
import org.openea.eap.extj.model.FileListVO;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.util.FileUploadUtils;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class VisualdevGenServiceImpl extends SuperServiceImpl<VisualdevMapper, VisualdevEntity> implements VisualdevGenService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private CodeGenerateFactory CodeGenerateFactory;

    @Override
    public String codeGengerate(VisualdevEntity entity, DownloadCodeForm downloadCodeForm) throws Exception {
        UserInfo userInfo = userProvider.get();
        DbLinkEntity linkEntity = null;
        if (entity != null) {
            // 是否存在关联数据库
            if(StringUtil.isNotEmpty(entity.getDbLinkId())){
                linkEntity = dblinkService.getInfo(entity.getDbLinkId());
            }
            // 是否存在关联表
            if (StringUtil.isNotEmpty(entity.getVisualTables())) {
                FormDataModel model = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
                model.setModule(downloadCodeForm.getModule());
                model.setClassName(downloadCodeForm.getClassName());
                model.setAreasName(downloadCodeForm.getModule());
                model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
                List<FieLdsModel> filterFeildList = JsonUtil.getJsonToList(model.getFields(), FieLdsModel.class);
                model.setFields(JSON.toJSONString(filterFeildList));
                String fileName = entity.getFullName() +System.currentTimeMillis();
                //初始化模板
                Velocity.reset();
                VelocityEnum.init.initVelocity(FileUploadUtils.getLocalBasePath() + configValueUtil.getTemplateCodePath());

                List<TableModel> list = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
                //获取主表
                String mainTable = list.stream().filter(t->"1".equals(t.getTypeId())).findFirst().orElse(null).getTable();
                //获取主键
                String pKeyName = VisualUtils.getpKey(linkEntity, mainTable).toLowerCase().trim().replaceAll("f_", "");
                //自定义包名
                String modulePackageName = StringUtil.isNotEmpty(downloadCodeForm.getModulePackageName()) ? downloadCodeForm.getModulePackageName() : GenBaseInfo.PACKAGE_NAME;
                downloadCodeForm.setModulePackageName(modulePackageName);
                //获取其他子表的主键
                Map<String, Object> childpKeyMap = new HashMap<>(16);
                for (TableModel tableModel : list) {
                    String childKey = VisualUtils.getpKey(linkEntity,tableModel.getTable());
                    if (childKey.length()>2){
                        if ("f_".equals(childKey.substring(0, 2).toLowerCase())) {
                            childKey = childKey.substring(2);
                        }
                    }
                    childpKeyMap.put(tableModel.getTable(), childKey);
                }
                //判断子表名称
                List<String> childTb = new ArrayList();
                if (!StringUtil.isEmpty(downloadCodeForm.getSubClassName())) {
                    childTb = Arrays.asList(downloadCodeForm.getSubClassName().split(","));
                }

                Set<String> set = new HashSet<>(childTb);
                boolean result = childTb.size() == set.size() ? true : false;
                if (!result) {
                    return MsgCode.EXIST001.get();
                }
                String templatesPath = null ;
                if (entity.getType() == 3) {
                    //工作流生成器
                    downloadCodeForm.setModule("form");
                    String templatePath = "TemplateCode1";
                    WorkGenModel workGenModel = new WorkGenModel();
                    workGenModel.setEntity(entity);
                    String keyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pKeyName);
                    workGenModel.setPKeyName(keyName);
                    workGenModel.setServiceDirectory(FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath());
                    workGenModel.setTemplateCodePath(FileUploadUtils.getLocalBasePath() + configValueUtil.getTemplateCodePath());
                    List<FileListVO> fileList = FileUploadUtils.getDefaultFileList(configValueUtil.getTemplateCodePath() + templatePath + "/");
                    for (FileListVO fileListVO : fileList) {
                        String[] split = fileListVO.getFileName().split("/");
                        String typeName = split[split.length - 2];
                        String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemplateCodePath() + templatePath + "/" + typeName + "/";
                        String folderName = configValueUtil.getTemplateCodePath();
                        String objectName = split[split.length - 1];
                        FileUploadUtils.downLocal(folderName + templatePath + "/" + typeName, filePath, objectName);
                    }
                    workGenModel.setDownloadCodeForm(downloadCodeForm);
                    workGenModel.setUserInfo(userInfo);
                    workGenModel.setTemplatePath(templatePath);
                    workGenModel.setFileName(fileName);
                    workGenModel.setLinkEntity(linkEntity);
                    workGenModel.setDataSourceUtil(dataSourceUtil);
                    workGenModel.setModel(model);
                    WorkGenUtil workGenUtil = new WorkGenUtil();
                    workGenUtil.generate(workGenModel);
                    workGenUtil.htmlTemplates(workGenModel);
                }
                if (entity.getType() == 4 ) {
                    switch (entity.getWebType()){
                        case 1:
                            templatesPath = entity.getEnableFlow() == 1 ? "TemplateCode5" : "TemplateCode4";
                            break;
                        case 2:
                            templatesPath = entity.getEnableFlow() == 1 ? "TemplateCode3" : "TemplateCode2";
                            break;
                        case 3:
                            templatesPath = "TemplateCode3";
                        default:
                            break;
                    }
                    List<FileListVO> fileList = FileUploadUtils.getDefaultFileList(configValueUtil.getTemplateCodePath() + templatesPath + "/");
                    for (FileListVO fileListVO : fileList) {
                        String[] split = fileListVO.getFileName().split("/");
                        String typeName = split[split.length - 2];
                        String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getTemplateCodePath() + templatesPath + "/" + typeName + "/";
                        String folderName = configValueUtil.getTemplateCodePath();
                        String objectName = split[split.length - 1];
                        FileUploadUtils.downLocal(folderName + templatesPath + "/" + typeName, filePath, objectName);
                    }
                    String column = StringUtil.isNotEmpty(entity.getColumnData())?entity.getColumnData():"{}";
                    ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(column, ColumnDataModel.class);
                    boolean groupTable = "3".equals(String.valueOf(columnDataModel.getType()));
                    CodeGenerateUtil generator = CodeGenerateFactory.getGenerator(templatesPath);
                    generator.htmlTemplates(fileName, entity, downloadCodeForm, model, templatesPath, userInfo, configValueUtil, pKeyName);
                    generator.generate(entity, dataSourceUtil, fileName, templatesPath, downloadCodeForm, userInfo, configValueUtil,linkEntity);
                    entity.setColumnData(entity.getAppColumnData());
                    AppGenModel appGenModel = new AppGenModel();
                    appGenModel.setEntity(entity);
                    String keyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, pKeyName);
                    appGenModel.setPKeyName(keyName);
                    appGenModel.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
                    appGenModel.setTemplateCodePath(configValueUtil.getTemplateCodePath());
                    appGenModel.setDownloadCodeForm(downloadCodeForm);
                    appGenModel.setUserInfo(userInfo);
                    appGenModel.setTemplatePath(templatesPath);
                    appGenModel.setFileName(fileName);
                    appGenModel.setLinkEntity(linkEntity);
                    appGenModel.setDataSourceUtil(dataSourceUtil);
                    appGenModel.setGroupTable(groupTable);
                    appGenModel.setType(String.valueOf(columnDataModel.getType()));
                    appGenModel.setModel(model);
                    AppGenUtil appGenUtil = new AppGenUtil();
                    appGenUtil.htmlTemplates(appGenModel);
                }
                return fileName;
            }
        }
        return null;
    }

}
