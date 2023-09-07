package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 可视化列表模型
 *

 */
@Data
@Schema(description = "可视化列表模型")
public class VisualFunctionModel {
	@Schema(description = "名称" )
	private String fullName;
	@Schema(description = "编码" )
	private String enCode;
	@Schema(description = "状态" )
	private Integer state;
	@Schema(description = "类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)" )
	private Integer type;
	@Schema(description = "关联的表" )
	private String tables;
	@Schema(description = "创建时间" )
	private Long creatorTime;
	@Schema(description = "创建人" )
	private String creatorUser;
	@Schema(description = "创建人id" )
	private String creatorUserId;
	@Schema(description = "修改时间" )
	private Long lastModifyTime;
	@Schema(description = "修改人" )
	private String lastModifyUser;
	@Schema(description = "修改人id" )
	private String lastModifyUserId;
	@Schema(description = "排序" )
	private Long sortCode;
	@Schema(description = "分类(数据字典维护)" )
	private String category;
	@Schema(description = "主键" )
	private String id;
	@Schema(description = "页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）" )
	private Integer webType;
	@Schema(description = "pc是否发布" )
	private Integer pcIsRelease;
	@Schema(description = "app是否发布" )
	private Integer appIsRelease;
	/**
	 * 是否发布
	 */
	@Schema(description = "是否发布" )
	private Integer isRelease;
	/**
	 * 是否启用流程
	 */
	@Schema(description = "是否启用流程" )
	private Integer enableFlow;

	/**
	 * 状态
	 */
	@Schema(description = "状态是否启用" )
	private Integer enabledMark;
	/**
	 * 移动锁定0-锁定1-可移动
	 */
	@Schema(description = "移动锁定0-禁用1-启用" )
	private Integer enabledLock;

	/**
	 * 是否有包名
	 */
	@Schema(description = "是否有包名")
	private boolean hasPackage = false;
}
