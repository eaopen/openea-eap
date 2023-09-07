package org.openea.eap.extj.portal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
@Schema(description="查询条件")
public class PortalPagination extends Pagination {

	@Schema(description = "分类（字典）")
	private String category;

	@Schema(description = "类型(0-门户设计,1-配置路径)")
	private Integer type;

	@Schema(description = "锁定(0-禁用,1-启用)")
	private Integer enabledLock;

	@Schema(description = "平台")
	private String platform = "web";

}
