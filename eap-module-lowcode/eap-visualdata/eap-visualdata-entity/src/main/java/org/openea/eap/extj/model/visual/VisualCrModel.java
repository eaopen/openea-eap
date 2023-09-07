package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualCrModel {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "分类")
    private String category;
}
