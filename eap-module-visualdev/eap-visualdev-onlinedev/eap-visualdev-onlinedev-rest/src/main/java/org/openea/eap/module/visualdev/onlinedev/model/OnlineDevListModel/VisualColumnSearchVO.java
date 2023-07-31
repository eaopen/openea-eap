package org.openea.eap.module.visualdev.onlinedev.model.OnlineDevListModel;

import org.openea.eap.module.visualdev.model.visualJson.SlotModel;
import org.openea.eap.module.visualdev.model.visualJson.config.ConfigModel;
import lombok.Data;

/**
 *
 */
@Data
public class VisualColumnSearchVO {
	/**
	 * 查询条件类型 1.等于 2.模糊 3.范围
	 */
	private String searchType;
	private String vModel;
	/**
	 * 查询值
	 */
	private Object value;
	/**
	 * 是否多选
	 */
	private Boolean multiple;

	private Boolean searchMultiple;

	private ConfigModel config;
	/**
	 * 省市区
	 */
	private Integer level;
	/**
	 * 时间类型格式
	 */
	private String format;
	private String type;

	/**
	 * 数据库字段
	 */
	private String field;
	private String table;

	private PropsModel props;
	private SlotModel slot;

	private String selectType;
	private String ableDepIds;
	private String ableIds;
	private String ablePosIds;
	private String ableUserIds;
	private String ableRoleIds;
	private String ableGroupIds;
}
