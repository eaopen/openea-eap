package org.openea.eap.module.system.controller.admin.language.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

/**
 * 语言 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LangTypeBaseVO {

    @Schema(description = "key/别名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key/别名不能为空")
    private String alias;

    @Schema(description = "名称", example = "王五")
    private String name;

}
