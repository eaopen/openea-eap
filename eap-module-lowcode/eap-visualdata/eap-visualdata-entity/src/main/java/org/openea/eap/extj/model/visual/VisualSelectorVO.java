package org.openea.eap.extj.model.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 */
@Data
public class VisualSelectorVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "是否有下级")
    private Boolean hasChildren;
    @Schema(description = "下级")
    private List<VisualSelectorVO> children;
}
