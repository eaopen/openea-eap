package org.openea.eap.extj.engine.model.flowbefore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class FlowTemplateModel {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "流程基本主键")
    private String templateId;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "类型")
    private Integer visibleType;
    @Schema(description = "json字段")
    private String flowTemplateJson;
    @Schema(description = "版本")
    private String version;
    @Schema(description = "类型")
    private Integer type;
}
