package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
public class PaginationDataInterface extends Pagination {
    @Schema(description = "分类id")
    private String categoryId;
    /**
     * 请求方式
     */
    @Schema(description = "是否分页 0-不分页 1-分页")
    private Integer hasPage;
}
