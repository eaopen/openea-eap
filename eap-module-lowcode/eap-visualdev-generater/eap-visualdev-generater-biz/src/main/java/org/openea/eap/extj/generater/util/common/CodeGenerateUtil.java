package org.openea.eap.extj.generater.util.common;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.model.visualJson.FormDataModel;

import java.sql.SQLException;

/**
 * 代码生成实现接口
 *
 *
 */
public interface CodeGenerateUtil {
	/**
	 * 生成前端页面
	 *
	 * @param fileName
	 * @param entity
	 * @param downloadCodeForm
	 * @param model
	 * @param templatePath
	 * @param userInfo
	 * @param configValueUtil
	 * @param pKeyName
	 */
	 void htmlTemplates(String fileName, VisualdevEntity entity, DownloadCodeForm downloadCodeForm, FormDataModel model, String templatePath, UserInfo userInfo, ConfigValueUtil configValueUtil, String pKeyName) throws Exception;


	/**
	 * 生成后端代码
	 *
	 * @param entity
	 * @param dataSourceUtil
	 * @param fileName
	 * @param templatePath
	 * @param downloadCodeForm
	 * @param userInfo
	 * @param configValueUtil
	 * @param linkEntity
	 * @throws SQLException
	 */
	 void generate(VisualdevEntity entity, DataSourceUtil dataSourceUtil, String fileName, String templatePath, DownloadCodeForm downloadCodeForm, UserInfo userInfo, ConfigValueUtil configValueUtil, DbLinkEntity linkEntity) throws SQLException ;


}
