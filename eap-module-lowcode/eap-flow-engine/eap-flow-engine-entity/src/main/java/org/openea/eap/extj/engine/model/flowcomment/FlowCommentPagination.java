package org.openea.eap.extj.engine.model.flowcomment;

import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowCommentPagination extends Pagination {

    @Schema(description = "任务主键")
    private String taskId;

}
