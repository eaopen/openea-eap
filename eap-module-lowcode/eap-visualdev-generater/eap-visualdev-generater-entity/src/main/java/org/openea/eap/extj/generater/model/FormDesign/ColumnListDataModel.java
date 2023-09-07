package org.openea.eap.extj.generater.model.FormDesign;

import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.analysis.FormMastTableModel;
import lombok.Data;

import java.util.List;

/**
 * 列表字段
 *
 * 
 */
@Data
public class ColumnListDataModel {
	/**
	 * model别名
	 */
	private String modelName;

	/**
	 * 外键
	 */
	private String relationField;

	/**
	 * 外键首字母大写
	 */
	private String relationUpField;

	/**
	 * 关联主键
	 */
	private String mainKey;

	/**
	 * 关联主键首字母大写
	 */
	private String mainUpKey;

	/**
	 * 所拥有字段
	 */
	private List<String> fieldList;

	/**
	 * 控件属性
	 */
	private List<FormMastTableModel> fieLdsModelList;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 首字母小写
	 */
	private String modelLowName;

	/**
	 * 首字母大写
	 */
	private String modelUpName;

	/**
	 * 当前表主键
	 */
	private String mainField;

	/**
	 * 对应控件(去除jnpf)
	 */
	private List<FieLdsModel> fieLdsModels;
}
