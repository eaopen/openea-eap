package org.openea.eap.extj.model.visualcategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class VisualCategoryInfoVO {
    @Schema(description = "分类键值")
    private String categoryKey;
    @Schema(description = "分类名称")
    private String categoryValue;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "是否已删除")
    private Integer isDeleted;
}
