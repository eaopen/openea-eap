package org.openea.eap.extj.engine.model.flowcandidate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class FlowCandidateVO {

    @Schema(description = "节点")
    private List<FlowCandidateListModel> list;
    /**
     * 1.有分支 //2.没有分支有候选人 //3.没有分支也没有候选人
     */
    @Schema(description = "类型")
    private Integer type;
}
