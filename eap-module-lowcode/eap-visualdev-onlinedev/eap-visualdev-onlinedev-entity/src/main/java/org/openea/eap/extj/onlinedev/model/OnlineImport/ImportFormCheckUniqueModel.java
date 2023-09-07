package org.openea.eap.extj.onlinedev.model.OnlineImport;

import lombok.Data;

import java.sql.Connection;

/**
 * 导入验证 表单验证
 *
 */
@Data
public class ImportFormCheckUniqueModel {
	private boolean isUpdate;
	private boolean isMain;
	private String id;
	/**
	 * 主键
	 */
	private Connection connection;
	private Integer primaryKeyPolicy;
	private Boolean logicalDelete = false;
}
