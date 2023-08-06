package org.openea.eap.module.visualdev.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.module.visualdev.base.Pagination;
import lombok.Data;

/**
 *
 */
@Data
@Schema(description = "查询条件模型")
public class PaginationModel extends Pagination {
    @Schema(description = "查询条件json")
    private String queryJson;
    @Schema(description = "菜单id")
    private String menuId;
    @Schema(description = "关联字段")
    private String relationField;
    @Schema(description = "字段对象")
    private String columnOptions;
    @Schema(description = "数据类型")
    private String dataType;
    @Schema(description = "高级查询条件json")
    private String superQueryJson;
}
