package org.openea.eap.extj.permission.model.user.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

/**
 *
 *
 */
@Data
public class PaginationUser extends Pagination {

    @Schema(description = "组织id")
    private String organizeId;

    @Schema(description = "角色id")
    private String roleId;

}
