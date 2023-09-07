package org.openea.eap.extj.base.model.dblink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DbLinkCreUpForm extends DbLinkBaseForm{

    @Schema(description = "有效标识")
    @NotNull(message = "必填")
    private boolean enabledMark;

}

