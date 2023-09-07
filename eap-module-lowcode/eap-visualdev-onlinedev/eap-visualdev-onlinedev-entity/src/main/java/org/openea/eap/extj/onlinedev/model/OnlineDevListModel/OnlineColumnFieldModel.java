package org.openea.eap.extj.onlinedev.model.OnlineDevListModel;

import lombok.Data;

/**
 * 列表字段
 *
 */
@Data
public class OnlineColumnFieldModel {
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 字段
	 */
	private String field;

	/**
	 * 原本字段
	 */
	private String OriginallyField;

	/**
	 * 别名
	 */
	private String otherName;

	private String jnpfKey;
}
