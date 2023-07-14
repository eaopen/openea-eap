package org.openea.eap.module.system.controller.admin.language.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 语言更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LangTypeUpdateReqVO extends LangTypeBaseVO {

    @Schema(description = "PK", requiredMode = Schema.RequiredMode.REQUIRED, example = "19948")
    @NotNull(message = "PK不能为空")
    private Long id;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}
