package org.openea.eap.extj.engine.model.flowtask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openea.eap.extj.base.PaginationTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PaginationFlowTask extends PaginationTime {
    /**
     * 流程模板id
     **/
    @Schema(description = "流程模板主键")
    private String templateId;
    @Schema(description = "流程主键")
    private String flowId;
    /**
     * 所属分类
     **/
    @Schema(description = "所属分类")
    private String flowCategory;
    @Schema(description = "创建用户主键")
    private String creatorUserId;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "批量审批",hidden = true)
    @JsonIgnore
    private Integer isBatch;
    @Schema(description = "编码")
    private String nodeCode;
    @Schema(description = "紧急程度")
    private Integer flowUrgent;
    @Schema(description = "是否委托",hidden = true)
    @JsonIgnore
    private Boolean delegateType = false;
}
