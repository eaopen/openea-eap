package org.openea.eap.extj.generater.model;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;


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
