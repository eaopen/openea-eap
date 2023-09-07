package org.openea.eap.extj.engine.model.flowtask;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 
 */
@Data
public class FlowAssistModel {
    @Schema(description = "主键")
    private String ids;
    @Schema(description = "用户")
    private List<String> list;
    @Schema(description = "流程基本主键")
    private String templateId ;
}
