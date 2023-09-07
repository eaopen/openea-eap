package org.openea.eap.extj.generater.util.app;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import lombok.Data;


@Data
public class AppGenModel {
    /**
     * 文件夹名字
     */
    private String fileName;
    /**
     * 实体对象
     */
    private VisualdevEntity entity;
    /**
     * 下载对象
     */
    private DownloadCodeForm downloadCodeForm;
    /**
     * 表单对象
     */
    private FormDataModel model;
    /**
     * 模板文件
     */
    private String templatePath;
    /**
     * 主键
     */
    private String pKeyName;
    /**
     * 本地数据源
     */
    private DataSourceUtil dataSourceUtil;
    /**
     * 数据连接
     */
    private DbLinkEntity linkEntity;
    /**
     * 个人信息
     */
    private UserInfo userInfo;
    /**
     * 生成文件名字
     */
    private String className;
    /**
     * 数据库表
     */
    private String table;
    /**
     * 生成路径
     */
    private String serviceDirectory;
    /**
     * 模板路径
     */
    private String templateCodePath;

    private Boolean groupTable;

    private String type;

}
