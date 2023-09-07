package org.openea.eap.extj.model.visualconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class VisualConfigCrForm {
    @Schema(description = "大屏详情")
    private String detail;
    @Schema(description = "内容")
    private String component;
}
