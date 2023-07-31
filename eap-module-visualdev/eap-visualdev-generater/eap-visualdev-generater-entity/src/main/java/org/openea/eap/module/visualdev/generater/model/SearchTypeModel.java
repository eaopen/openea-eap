package org.openea.eap.module.visualdev.generater.model;


import org.openea.eap.module.visualdev.model.visualJson.config.ConfigModel;
import lombok.Data;

/**
 *
 *
 */
@Data
public class SearchTypeModel {
	private String vModel;
	private String dataType;
	private Integer searchType;
	private String label;
	private String jnpfKey;
	private String format;
	private String multiple;
	/**
	 * 搜索框显示
	 */
	private String placeholder;
	private ConfigModel config;

	private String TableName;
	private String afterVModel;

	private String showLevel;
}
