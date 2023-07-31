package org.openea.eap.module.visualdev.generater.util.common;

import org.openea.eap.module.visualdev.base.UserInfo;
import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.config.ConfigValueUtil;
import org.openea.eap.module.visualdev.database.model.entity.DbLinkEntity;
import org.openea.eap.module.visualdev.database.util.DataSourceUtil;
import org.openea.eap.module.visualdev.base.model.DownloadCodeForm;
import org.openea.eap.module.visualdev.model.visualJson.FormDataModel;

import java.sql.SQLException;

/**
 * 代码生成实现接口
 *
 * @author JNPF开发平台组
 * @version V3.2
 * @copyright 引迈信息技术有限公司（https://www.jnpfsoft.com）
 * @date  2021/10/8
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
