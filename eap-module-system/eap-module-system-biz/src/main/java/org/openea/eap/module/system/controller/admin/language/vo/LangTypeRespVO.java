package org.openea.eap.module.system.controller.admin.language.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 语言 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LangTypeRespVO extends LangTypeBaseVO {

    @Schema(description = "PK", requiredMode = Schema.RequiredMode.REQUIRED, example = "19948")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}
