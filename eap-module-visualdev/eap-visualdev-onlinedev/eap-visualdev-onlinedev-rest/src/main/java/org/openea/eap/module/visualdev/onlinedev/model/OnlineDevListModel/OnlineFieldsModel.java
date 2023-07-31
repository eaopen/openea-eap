package org.openea.eap.module.visualdev.onlinedev.model.OnlineDevListModel;

import org.openea.eap.module.visualdev.permission.model.authorize.OnlineDynamicSqlModel;
import lombok.Data;

import java.util.List;


/**
 *在线开发formData
 *
 */
@Data
public class OnlineFieldsModel  {
		private List<OnlineColumnFieldModel> mastTableList;
		private List<OnlineDynamicSqlModel> dynamicSqlModels;
}
