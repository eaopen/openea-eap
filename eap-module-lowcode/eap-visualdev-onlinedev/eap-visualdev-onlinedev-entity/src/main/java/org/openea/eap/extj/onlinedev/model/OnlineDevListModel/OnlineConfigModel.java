package org.openea.eap.extj.onlinedev.model.OnlineDevListModel;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.config.ConfigPropsModel;
import org.openea.eap.extj.model.visualJson.config.RegListModel;

import java.util.List;

/**
 *
 *
 */
@Data
public class OnlineConfigModel {
	private String label;
	private String labelWidth;
	private Boolean showLabel;
	private Boolean changeTag;
	private Boolean border;
	private String tag;
	private String tagIcon;
	private Boolean required;
	private String layout;
	private String dataType;
	private Integer span;
	private String jnpfKey;
	private String dictionaryType;
	private Integer formId;
	private Long renderKey;
	private Integer columnWidth;
	private List<RegListModel> regList;
	private Object defaultValue;
	/**
	 * app静态数据
	 */
	private String options;
	/**
	 * 判断defaultValue类型
	 */
	private String valueType;
	private String propsUrl;
	private String optionType;
	private ConfigPropsModel props;
	/**
	 * 子表添加字段
	 */
	private String showTitle;
	private String tableName;
	private List<OnlineFieldsModel> children;
	/**
	 * 单据规则使用
	 */
	private String rule;

	/**
	 * 验证规则触发方式
	 */
	private String trigger="blur";
	/**
	 * 隐藏
	 */
	private Boolean noShow=false;
}
