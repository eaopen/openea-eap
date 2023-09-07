package org.openea.eap.extj.engine.model.flowdelegate;

import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 委托发起流程列表
 *
 *
 */
@Data
public class FlowDelegateStartPagination  extends Pagination {
    @Schema(description = "分类")
    private String category;
    @Schema(description = "类型")
    private String flowType;
}
