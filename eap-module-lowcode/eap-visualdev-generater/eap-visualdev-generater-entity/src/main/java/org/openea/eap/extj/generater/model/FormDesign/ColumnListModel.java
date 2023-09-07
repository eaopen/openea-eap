package org.openea.eap.extj.generater.model.FormDesign;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.options.ColumnOptionModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.model.visualJson.props.PropsModel;
import org.openea.eap.extj.model.visualJson.fields.slot.SlotModel;

import java.util.List;

/**
 * 列表
 *
 */
@Data
public class ColumnListModel  {

	private boolean first = false;

	/**
	 * 字段
	 */
	private String prop;
	/**
	 * 列名
	 */
	private String label;
	/**
	 * 对齐
	 */
	private String align;

	private String jnpfKey;

	private String dataType;

	private String vModel;

	private Boolean sortable;

	/**
	 * 列表子表表名
	 */
	private String columnTableName;

	/**
	 * 处理后的prop
	 */
	private String newProp;

	private PropsBeanModel propsModel;

	private Boolean multiple;

	private ConfigModel config;
	private SlotModel slot;
	private String placeholder;
	private Object style;
	private Boolean clearable;
	private String prefixicon;
	private Integer precision;
	private String suffixicon;
	private String maxlength;
	private Boolean showWordLimit;
	private Boolean readonly;
	private Boolean disabled;
	/**
	 * 关联表单id
	 */
	private String modelId="";
	/**
	 * 关联表单 二维码 条形码 字段
	 */
	private String relationField;
	private Boolean hasPage;
	private String pageSize;
	private String type;
	private Object autosize;
	private Integer step;
	private Boolean stepstrictly;
	private String controlsPosition;
	private Object textStyle;
	private Integer lineHeight;
	private Integer fontSize;
	private Boolean showChinese;
	private Boolean showPassword;

	/**
	 * 大小
	 */
	private String size;
	private Boolean filterable;
	/**
	 * 冻结
	 */
	private String fixed = "none";
	/**
	 * 关联表单属性
	 */
	private String showField;
	/**
	 * 待定
	 */
	private PropsModel props;
	/**
	 * 待定
	 */
	private Boolean showAllLevels;
	private String separator;
	private Boolean isrange;
	private String rangeseparator;
	private String startplaceholder;
	private String endplaceholder;
	private String format;
	private String valueformat;
	private Object pickeroptions;
	private Integer max;
	private Boolean allowhalf;
	private Boolean showText;
	private Boolean showScore;
	private Boolean showAlpha;
	private String colorformat;
	private String activetext;
	private String inactivetext;
	private String activecolor;
	private String inactivecolor;
	private String activevalue;
	private String inactivevalue;
	private Integer min;
	private Boolean showStops;
	private Boolean range;
	private String content;
	private String header;
	private Boolean accordion;
	private String tabPosition;
	/**
	 * 未找到
	 */
	private String accept;
	private Boolean showTip;
	private Integer fileSize;
	private String sizeUnit;
	private Integer limit;
	private String contentposition;
	private String buttonText;
	private Integer level;
	private String options;
	private String actionText;
	private String shadow;
	private String name;
	private String title;

	/**
	 * 查询方式 1.eq 2.like 3.between
	 */
	private Integer searchType;
	private String interfaceId;
	private List<ColumnOptionModel> columnOptions;
	private String propsValue;

	/**
	 * 开关 值
	 */
	private String activeTxt ="开";
	private String inactiveTxt = "关";

	/**
	 * 条形码 条码颜色
	 */
	private String lineColor;
	/**
	 * 条形码 背景色
	 */
	private String background;
	/**
	 * 条形码 宽高
	 */
	private Integer width;
	private Integer height;
	/**
	 * 条形码 二维码 固定值
	 */
	private String staticText;


	/**
	 * 二维码 条码颜色
	 */
	private String colorDark;

	/**
	 * 二维码 背景色
	 */
	private String colorLight;

	/**
	 * 子表是否合计
	 */
	private Boolean showSummary;

	/**
	 * 子表合计字段
	 */
	private String summaryField;

	/**
	 * 所属部门展示内容
	 */
	private String showLevel;

	/**
	 * 弹窗 样式属性
	 */
	private String popupType;
	private String popupTitle;
	private String popupWidth;

	private String selectType;

	//自定义
	private String ableIds;
	private String ableDepIds;
	private String ablePosIds;
	private String ableUserIds;
	private String ableRoleIds;
	private String ableGroupIds;

	private ColumnChildListModel columnChildListModel ;

	//数字输入-金额大小写等属性
	private String addonAfter;
	private String addonBefore;
	private Boolean isAmountChinese;
	private Boolean thousands;

	//时间控件新增字段
	private String startTime;
	private String endTime;
	private String startRelationField;
	private String endRelationField;
	/**
	 * 下拉补全展示条数
	 */
	private Integer total;

}
