package org.openea.eap.extj.engine.model.flowtemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowSelectVO {
    @Schema(description = "流程主键")
    private String id;
    @Schema(description = "流程名称")
    private String fullName;
    @Schema(description = "流程json")
    private String flowTemplateJson;
}
