package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.model.visualJson.VisualdevTreeChildModel;

import java.util.List;

/**
 *

 */
@Data
@Schema(description = "功能树形VO" )
public class VisualdevTreeVO {
    @Schema(description = "主键" )
    private String id;
    @Schema(description = "名称" )
    private String fullName;
    @Schema(description = "是否有子集" )
    private Boolean hasChildren;
    @Schema(description = "排序" )
    private Long sortCode;
    @Schema(description = "子集对象" )
    private List<VisualdevTreeChildModel> children;
}
