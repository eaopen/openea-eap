package org.openea.eap.module.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 岗位精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSimpleRespVO {

    @Schema(description = "岗位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "eap")
    private String code;

    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    private String name;

}
