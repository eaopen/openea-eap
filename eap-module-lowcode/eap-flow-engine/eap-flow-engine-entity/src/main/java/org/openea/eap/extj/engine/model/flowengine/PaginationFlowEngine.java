package org.openea.eap.extj.engine.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class PaginationFlowEngine extends Pagination {
    private Integer enabledMark;
    @Schema(hidden = true)
    @JsonIgnore
    private Integer type;
}
