package org.openea.eap.extj.onlinedev.model.OnlineDevListModel;

import org.openea.eap.extj.permission.model.authorize.OnlineDynamicSqlModel;
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
