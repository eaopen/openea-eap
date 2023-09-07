package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.model.VisualPagination;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualPaginationModel extends VisualPagination {
    @Schema(description = "分类")
    private Integer category;


}
