package org.openea.eap.module.system.controller.admin.language.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 翻译 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class I18nJsonDataRespVO extends I18nJsonDataBaseVO {

    @Schema(description = "PK", requiredMode = Schema.RequiredMode.REQUIRED, example = "2508")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
