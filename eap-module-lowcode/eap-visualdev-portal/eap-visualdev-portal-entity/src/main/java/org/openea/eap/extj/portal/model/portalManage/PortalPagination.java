package org.openea.eap.extj.portal.model.portalManage;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
@Schema(description = "查询条件")
public class PortalPagination extends Pagination {
	/**
	 * 分类
	 */
	@Schema(description = "分类（字典）")
	private String category;

	/**
	 * 类型(0-门户设计,1-配置路径)
	 */
	@Schema(description = "类型(0-门户设计,1-配置路径)")
	private Integer type;
}
