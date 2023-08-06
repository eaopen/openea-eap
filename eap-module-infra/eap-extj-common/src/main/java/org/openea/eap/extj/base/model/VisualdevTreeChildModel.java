package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "功能树形模型" )
public class VisualdevTreeChildModel {
    @Schema(description = "主键" )
    private String id;
    @Schema(description = "名称" )
    private String fullName;
}
