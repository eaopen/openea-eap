package org.openea.eap.extj.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class FlowCandidateUserModel {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "头像")
    private String headIcon;
    @Schema(description = "组织")
    private String organize;
}
