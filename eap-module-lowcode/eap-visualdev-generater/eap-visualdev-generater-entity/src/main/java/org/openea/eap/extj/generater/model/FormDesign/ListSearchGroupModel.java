package org.openea.eap.extj.generater.model.FormDesign;

import org.openea.eap.extj.generater.model.SearchTypeModel;
import lombok.Data;

import java.util.List;

@Data
public class ListSearchGroupModel {
	/**
	 * 模型名
	 */
	private String modelName;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 外键
	 */
	private String ForeignKey;
	/**
	 * 关联主键
	 */
	private String mainKey;

	/**
	 * 该表下的查询字段
	 */
	private List<SearchTypeModel> searchTypeModelList;
}
