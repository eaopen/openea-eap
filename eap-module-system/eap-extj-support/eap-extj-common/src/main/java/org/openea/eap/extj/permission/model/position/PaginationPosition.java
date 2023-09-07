package org.openea.eap.extj.permission.model.position;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

/**
 *
 *
 */
@Data
public class PaginationPosition extends Pagination {
   @Schema(description = "组织id")
   private String organizeId;
}
