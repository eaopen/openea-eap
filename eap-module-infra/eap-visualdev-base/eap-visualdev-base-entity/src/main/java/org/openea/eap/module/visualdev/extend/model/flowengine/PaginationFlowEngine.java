package org.openea.eap.module.visualdev.extend.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class PaginationFlowEngine extends Pagination {
    private Integer enabledMark;
    @Schema(hidden = true)
    @JsonIgnore
    private Integer type;
}
