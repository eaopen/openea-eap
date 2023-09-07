package org.openea.eap.extj.engine.model.flowtask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 */
@Data
public class TaskNodeModel {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "节点名称")
    private String nodeName;
    @Schema(description = "节点编码")
    private String nodeCode;
}
