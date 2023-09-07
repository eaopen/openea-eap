package org.openea.eap.extj.base.model.online;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class BatchOnlineModel implements Serializable {
    @Schema(description = "id集合")
    private String[] ids;
}
