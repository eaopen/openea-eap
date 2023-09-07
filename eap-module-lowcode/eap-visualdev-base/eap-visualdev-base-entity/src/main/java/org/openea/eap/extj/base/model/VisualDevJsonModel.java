package org.openea.eap.extj.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;

import java.util.List;

/**
 * json格式化对象（在线开发对象）
 *
 */
@Data
@Schema(description = "功能设计json模型" )
public class VisualDevJsonModel {
	@Schema(description = "主键" )
	private String id;
	@Schema(description = "名称" )
	private String fullName;
	@Schema(description = "类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)" )
	private Integer type;
	@Schema(description = "关联的表对象" )
	private List<TableModel> visualTables;
	@Schema(description = "表单配置对象" )
	private FormDataModel formData;
	@Schema(description = "字段配置对象" )
	private ColumnDataModel columnData;
	@Schema(description = "app字段配置对象" )
	private ColumnDataModel appColumnData;
	@Schema(description = "关联数据连接id" )
	private String dbLinkId;
	@Schema(description = "页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）" )
	private Integer webType;
	@Schema(description = "表单字段列表" )
	private List<FieLdsModel> formListModels;
	@Schema(description = "启用流程" )
	private boolean flowEnable;
	@Schema(description = "树形子列表查询" )
	private boolean isChildSearch = false;//树形子列表查询
	@Schema(description = "树形子列表查询值" )
	private String childValue;//树形子列表查询值

	@Schema(description = "主表主键" )
	private String pkeyId;//切库回传
}
