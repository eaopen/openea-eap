package org.openea.eap.extj.engine.model.flowbefore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class FlowBatchModel {
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "数量")
    private Long num;
}
