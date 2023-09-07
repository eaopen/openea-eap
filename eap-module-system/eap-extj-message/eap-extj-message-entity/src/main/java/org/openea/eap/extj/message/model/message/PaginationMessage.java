package org.openea.eap.extj.message.model.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
public class PaginationMessage extends Pagination {
    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Integer isRead;

}
