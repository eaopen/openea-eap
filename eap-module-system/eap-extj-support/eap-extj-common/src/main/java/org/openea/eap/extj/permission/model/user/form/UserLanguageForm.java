package org.openea.eap.extj.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 *
 */
@Data
public class UserLanguageForm {
    @NotBlank(message = "必填")
    @Schema(description = "语言代码")
    private String language;
}
