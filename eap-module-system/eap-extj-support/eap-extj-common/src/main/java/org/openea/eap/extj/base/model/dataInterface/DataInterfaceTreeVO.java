package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class DataInterfaceTreeVO {
    @Schema(description = "主键Id")
    private String categoryId;
    @Schema(description = "接口名称")
    private String fullName;
    @Schema(description = "主键")
    private String id;
    @Schema(description = "是否有子集")
    private Boolean hasChildren;
    private String requestParameters;
    @Schema(description = "子集集合")
    private List<DataInterfaceTreeModel> children;
}
