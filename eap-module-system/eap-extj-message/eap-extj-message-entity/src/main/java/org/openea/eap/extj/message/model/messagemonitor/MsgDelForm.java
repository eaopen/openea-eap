package org.openea.eap.extj.message.model.messagemonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MsgDelForm {
    @Schema(description = "id集合")
    private String[] ids;
}

