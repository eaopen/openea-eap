package org.openea.eap.extj.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.engine.model.flowtask.TaskNodeModel;
import lombok.Data;

import java.util.List;

/**
 * 
 */
@Data
public class FlowRejectVO {
    @Schema(description = "节点")
    private List<TaskNodeModel> list;
    @Schema(description = "是否选择")
    private Boolean isLastAppro = true;
}
