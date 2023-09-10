package org.openea.eap.extj.form.model.flow;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "流程引擎信息模型")
public class FlowTempInfoModel {
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "流程引擎id")
    private String id;
    @Schema(description = "是否启用")
    private Integer enabledMark;
}
