package org.openea.eap.extj.model.visualfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class ImageVO {
    @Schema(description = "路径")
    private String domain;
    @Schema(description = "链接")
    private String link;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "名称")
    private String originalName;
}
