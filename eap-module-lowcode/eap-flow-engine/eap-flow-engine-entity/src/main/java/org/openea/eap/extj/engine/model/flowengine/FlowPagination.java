package org.openea.eap.extj.engine.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "分页模型")
public class FlowPagination extends Pagination {

    @Schema(description = "分类")
    private String category;
    @Schema(description = "类型")
    private Integer flowType;
    @Schema(description = "标志")
    private Integer enabledMark = 1;
    @Schema(description = "流程模板主键",hidden = true)
    @JsonIgnore
    private List<String> templateIdList = new ArrayList<>();
}
